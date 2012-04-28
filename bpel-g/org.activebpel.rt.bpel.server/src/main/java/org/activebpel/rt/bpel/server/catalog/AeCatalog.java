// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/catalog/AeCatalog.java,v 1.12 2008/02/17 21:38:54 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.catalog;

import bpelg.services.deploy.types.pdd.ReferenceType;
import net.sf.ehcache.Statistics;
import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.impl.list.AeCatalogItemDetail;
import org.activebpel.rt.bpel.impl.list.AeCatalogItemPlanReference;
import org.activebpel.rt.bpel.impl.list.AeCatalogListResult;
import org.activebpel.rt.bpel.impl.list.AeCatalogListingFilter;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.IAeDeploymentProvider;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.bpel.server.catalog.report.AeInMemoryCatalogListing;
import org.activebpel.rt.bpel.server.catalog.report.IAeCatalogAdmin;
import org.activebpel.rt.bpel.server.catalog.resource.AeResourceCache;
import org.activebpel.rt.bpel.server.catalog.resource.AeResourceException;
import org.activebpel.rt.bpel.server.catalog.resource.IAeResourceCache;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentId;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.xml.AeQName;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * Default impl of the IAeCatalog.
 */
public class AeCatalog implements IAeCatalog, IAeCatalogAdmin {
	/** Used to cache wsdl resources */
	private IAeResourceCache mCache;

	/** Maps the locating hint to a mapping object for the resource. */
	private Map<String, IAeCatalogMapping> mLocationToMapping;

	/**
	 * Maps the namespaces to a mapping object or a list of mapping object for
	 * the resource.
	 */
	private Map<String, Object> mNamespaceMapping;

	/** Maps the a list of catalog mapping for each deployment id. */
	private Map<IAeDeploymentId, IAeCatalogMapping[]> mDeploymentMapping;

	/** Catalog listeners */
	private final Collection<IAeCatalogListener> mListeners = new ArrayList<IAeCatalogListener>();

	/**
	 * Constructor.
	 */
	public AeCatalog() {
		init();
	}

	/**
	 * Creates the various pieces of the catalog.
	 */
	protected void init() {
		setLocationToMapping(new HashMap<String, IAeCatalogMapping>());
		setNamespaceMapping(new HashMap<String, Object>());
		setDeploymentMapping(new HashMap<IAeDeploymentId, IAeCatalogMapping[]>());
		AeResourceCache cache = new AeResourceCache("ae-resource-cache");
		mCache = cache;
	}

	/**
	 * @see org.activebpel.rt.bpel.server.catalog.IAeCatalog#addCatalogEntries(org.activebpel.rt.bpel.server.deploy.IAeDeploymentId,
	 *      org.activebpel.rt.bpel.server.catalog.IAeCatalogMapping[], boolean)
	 */
	public synchronized void addCatalogEntries(IAeDeploymentId aDeploymentId,
			IAeCatalogMapping[] aMappings, boolean aReplaceFlag) {
		if (aMappings.length == 0) {
			return;
		}
		getResourceCache().clear();

		// create a list of booleans where true indicates a new entry based
		// event for events below
		List<Boolean> catalogEvents = new ArrayList<Boolean>();

		// check each mapping for replacement or addition to current locations
		for (int i = 0; i < aMappings.length; i++) {
			if (getLocationToMapping().get(aMappings[i].getLocationHint()) != null) {
				if (aReplaceFlag)
					addMappingForLocation(aMappings[i]);
				catalogEvents.add(Boolean.FALSE);
			} else {
				addMappingForLocation(aMappings[i]);
				catalogEvents.add(Boolean.TRUE);
			}
		}

		// fire the events for all the passed catalog deployments now that we
		// are done processing
		// note that this avoids interdependency issues like those in defect
		// 2312
		int i = 0;
		for (Iterator iter = catalogEvents.iterator(); iter.hasNext(); ++i) {
			// for new entries call through deploy sender otherwise warning
			// sender
			if (((Boolean) iter.next()).booleanValue())
				fireEvent(IAeCatalogEventDispatcher.DEPLOY_SENDER,
						aMappings[i].getLocationHint(), true);
			else
				fireEvent(IAeCatalogEventDispatcher.WARNING_SENDER,
						aMappings[i].getLocationHint(), aReplaceFlag);
		}

		// if passed a deployment id record this array in the mapping
		if (aDeploymentId != null)
			getDeploymentMapping().put(aDeploymentId, aMappings);
	}

	/**
	 * Add a mapping to our in-memory map of catalog items. Also update our
	 * namespace maps if the mapping is associated with a wsdl namespace.
	 * 
	 * @param aMapping
	 */
	protected void addMappingForLocation(IAeCatalogMapping aMapping) {
		getLocationToMapping().put(aMapping.getLocationHint(), aMapping);
		if (aMapping.isWsdlEntry())
			addNamespaceMapping(aMapping);
	}

	/**
	 * Adds a namespace mapping for the passed wsdl mapping.
	 * 
	 * @param aMapping
	 */
	@SuppressWarnings("unchecked")
	protected void addNamespaceMapping(IAeCatalogMapping aMapping) {
		// check if mapping already exists which requires adding to
		Object obj = getNamespaceMapping().get(aMapping.getTargetNamespace());
		if (obj == null) {
			getNamespaceMapping().put(aMapping.getTargetNamespace(), aMapping);
		} else {
			HashMap<String, IAeCatalogMapping> map;
			if (obj instanceof HashMap) {
				// rather than create a synchronized list for possible iterator
				// usage we'll copy here
				map = new HashMap<String, IAeCatalogMapping>((HashMap<String, IAeCatalogMapping>) obj);
				map.put(aMapping.getLocationHint(), aMapping);
			} else {
				map = new HashMap<String, IAeCatalogMapping>();
				map.put(((IAeCatalogMapping) obj).getLocationHint(), (IAeCatalogMapping)obj);
				map.put(aMapping.getLocationHint(), aMapping);
			}
			getNamespaceMapping().put(aMapping.getTargetNamespace(), map);
		}
	}

	/**
	 * Get a mapping to our in-memory map of catalog items. or null if it
	 * doesn't exist
	 * 
	 * @param aLocation
	 * @return IAeCatalogMapping if it exists
	 */
	public synchronized IAeCatalogMapping getMappingForLocation(String aLocation) {
		return getLocationToMapping().get(aLocation);
	}

	/**
	 * Returns true if the catalog has a mapping for the passed location.
	 * 
	 * @param aLocation
	 * @return true if the corresponding IAeCatalogMapping exists
	 */
	public boolean hasMappingForLocation(String aLocation) {
		return getMappingForLocation(aLocation) != null;
	}

	/**
	 * @see org.activebpel.rt.bpel.server.catalog.IAeCatalog#getResourceCache()
	 */
	public IAeResourceCache getResourceCache() {
		return mCache;
	}

	/**
	 * @param aCache
	 */
	protected void setCache(IAeResourceCache aCache) {
		mCache = aCache;
	}

	/**
	 * Faciliates maintainance of the list of displaced catalog mappings.
	 * 
	 * @param aMap
	 *            The map containing the deploymentId to displaced resource
	 *            entry
	 * @param aDeployId
	 *            The deployment Id we are keying off of
	 * @param aLocationHint
	 *            The location hint for entry which was displaced
	 */
	protected void addDisplacedCatalogEntry(Map<IAeDeploymentId, Set<String>> aMap, IAeDeploymentId aDeployId, String aLocationHint) {
		Set<String> catalogLocations = aMap.get(aDeployId);
		if (catalogLocations == null) {
			catalogLocations = new HashSet<String>();
			aMap.put(aDeployId, catalogLocations);
		}

		catalogLocations.add(aLocationHint);
	}

	/**
	 * Given a map of deployments with stale resources, this routine will update
	 * the resource catalog to contain valid resources from the BPR deployment.
	 * 
	 * @param aDisplacedMappings
	 *            map of displace resource entries
	 */
	protected void updateDisplacedCatalogItems(Map<IAeDeploymentId, Set<String>> aDisplacedMappings) {
		// Add catalog entries back for all displaced entries
		for (Entry<IAeDeploymentId, Set<String>> entry : aDisplacedMappings.entrySet())
		{
			IAeDeploymentId deployId = entry.getKey();
			Set<String> displacedLocations = entry.getValue();

			IAeCatalogMapping[] newMappings = new IAeCatalogMapping[displacedLocations.size()];
			IAeCatalogMapping[] currentMappings = getDeploymentMapping().get(deployId);
			for (int i = 0, j = 0; i < currentMappings.length; ++i) {
				if (displacedLocations.contains(currentMappings[i].getLocationHint()))
					newMappings[j++] = currentMappings[i];
			}

			// Call addCatalog with null deployment Id, so we do not modify the
			// deployment mapping entries
			addCatalogEntries(null, newMappings, true);
		}
	}

	/**
	 * @see org.activebpel.rt.bpel.server.catalog.IAeCatalog#remove(org.activebpel.rt.bpel.server.deploy.IAeDeploymentId)
	 */
	public synchronized void remove(IAeDeploymentId aId) {
		IAeCatalogMapping[] mappings = getDeploymentMapping().get(aId);
		if (mappings == null)
			return;

		// Check all mappings for this deployment to see if they are referenced
		// by other deployments
		Map<IAeDeploymentId, Set<String>> displacedMappings = new HashMap<IAeDeploymentId, Set<String>>();
		for (int i = 0; i < mappings.length; ++i) {
			String location = mappings[i].getLocationHint();
			for (Entry<IAeDeploymentId, IAeCatalogMapping[]> entry : getDeploymentMapping().entrySet()) {
				// Do not check our own mapping, since it is being removed
				// anyways
				IAeDeploymentId deployId = entry.getKey();
				if (!aId.equals(deployId)) {
					// Add any displaced entries to our list, so that we may fix
					// them up later
					IAeCatalogMapping[] deployMappings = entry.getValue();
					for (int j = 0; j < deployMappings.length; ++j) {
						if (location.equals(deployMappings[j].getLocationHint()))
							addDisplacedCatalogEntry(displacedMappings, deployId, deployMappings[j].getLocationHint());
					}
				}
			}

			// Remove the mapping, now that we've recorded all displaced
			// locations
			remove(mappings[i]);
		}

		// Update all mappings which we determined to have been displaced
		updateDisplacedCatalogItems(displacedMappings);

		// Remove the mapping entry
		getDeploymentMapping().remove(aId);
	}

	/**
	 * @see org.activebpel.rt.bpel.server.catalog.IAeCatalog#clear()
	 */
	public synchronized void clear() {
		init();
	}

	/**
	 * Removes a resource mapping from the catalog and from cache.
	 * 
	 * @param aMapping
	 *            The deployment to flush from catalog.
	 */
	protected void remove(IAeCatalogMapping aMapping) {
		// for wsdl entries remove namespace mapping
		if (aMapping.isWsdlEntry()
				&& AeUtil.notNullOrEmpty(aMapping.getTargetNamespace()))
			removeNamespaceMapping(aMapping);
		// remove other mappings/cache for this location
		getLocationToMapping().remove(aMapping.getLocationHint());
		getResourceCache().removeResource(
				new ReferenceType().withLocation(aMapping.getLocationHint())
						.withTypeURI(aMapping.getTypeURI()));
		fireEvent(IAeCatalogEventDispatcher.REMOVE_SENDER,
				aMapping.getLocationHint(), false);
	}

	/**
	 * Removes the passed catalog mapping from the namespaces map.
	 * 
	 * @param aMapping
	 */
	@SuppressWarnings("unchecked")
	protected void removeNamespaceMapping(IAeCatalogMapping aMapping) {
		Object obj = getNamespaceMapping().get(aMapping.getTargetNamespace());
		if (obj != null) {
			Map<String, IAeCatalogMapping> map;
			if (obj instanceof Map) {
				// rather than create a synchronized list for possible iterator
				// usage we'll copy here
				// then find by location hint this entry and remove it
				map = new HashMap<String, IAeCatalogMapping>((Map<String, IAeCatalogMapping>) obj);
				for (Iterator<IAeCatalogMapping> iter = map.values().iterator(); iter.hasNext();) {
					IAeCatalogMapping mapping = iter.next();
					if (AeUtil.compareObjects(aMapping.getLocationHint(), mapping.getLocationHint())) {
						iter.remove();
						break;
					}
				}
				// if last entry then just remove key altogether otherwise put
				// updated list in
				if (map.size() == 0)
					getNamespaceMapping().remove(aMapping.getTargetNamespace());
				else
					getNamespaceMapping().put(aMapping.getTargetNamespace(), map);
			} else {
				// only 1 entry so remove
				getNamespaceMapping().remove(aMapping.getTargetNamespace());
			}
		}
	}

	/**
	 * @see org.activebpel.rt.wsdl.IAeWSDLProvider#dereferenceIteration(java.lang.Object)
	 */
	public AeBPELExtendedWSDLDef dereferenceIteration(Object aIteration) {
		IAeCatalogMapping mapping = (IAeCatalogMapping) aIteration;
		try {
			// Per Chris, We only ever search for WSDL in the catalog so it's
			// safe to cast to a WSDL file here.
			return (AeBPELExtendedWSDLDef) getResourceCache().getResource(
					new ReferenceType().withLocation(mapping.getLocationHint()).withTypeURI(mapping
							.getTypeURI()));
		} catch (AeResourceException ex) {
			return null;
		}
	}

	/**
	 * @see org.activebpel.rt.wsdl.IAeWSDLProvider#getWSDLIterator(java.lang.String)
	 */
	public synchronized Iterator getWSDLIterator(String aNamespaceUri) {
		Object obj = getNamespaceMapping().get(aNamespaceUri);
		if (obj instanceof Map) {
			// Create new list to avoid ConcurrenModificationException during
			// traversal
			@SuppressWarnings("unchecked")
			List<IAeCatalogMapping> list = new ArrayList<IAeCatalogMapping>(((Map<String,IAeCatalogMapping>) obj).values());
			return list.iterator();
		} else if (obj != null)
			return Collections.singleton(obj).iterator();
		else
			return Collections.EMPTY_SET.iterator();
	}

	/**
	 * @see org.activebpel.rt.bpel.server.catalog.IAeCatalog#addCatalogListener(org.activebpel.rt.bpel.server.catalog.IAeCatalogListener)
	 */
	public void addCatalogListener(IAeCatalogListener aListener) {
		synchronized (mListeners) {
			if (!mListeners.contains(aListener)) {
				mListeners.add(aListener);
			}
		}
	}

	/**
	 * @see org.activebpel.rt.bpel.server.catalog.IAeCatalog#removeCatalogListener(org.activebpel.rt.bpel.server.catalog.IAeCatalogListener)
	 */
	public void removeCatalogListener(IAeCatalogListener aListener) {
		synchronized (mListeners) {
			mListeners.remove(aListener);
		}
	}

	/**
	 * Create and fire the AeCatalogEvent to the appropriate handler method on
	 * the IAeCatalogListener.
	 * 
	 * @param aSender
	 * @param aLocationHint
	 * @param aIsReplacement
	 */
	protected void fireEvent(IAeCatalogEventDispatcher aSender,
			String aLocationHint, boolean aIsReplacement) {
		List recipients = null;
		synchronized (mListeners) {
			if (!isListenerListEmpty()) {
				recipients = new ArrayList<IAeCatalogListener>(mListeners);
			}
		}

		if (recipients != null) {
			AeCatalogEvent event = AeCatalogEvent.create(aLocationHint,
					aIsReplacement);
			for (Iterator iter = recipients.iterator(); iter.hasNext();) {
				IAeCatalogListener listener = (IAeCatalogListener) iter.next();
				aSender.dispatch(listener, event);
			}
		}
	}

	/**
	 * Return true if the listener list is empty.
	 */
	protected boolean isListenerListEmpty() {
		return mListeners.isEmpty();
	}

	/**
	 * @see org.activebpel.rt.bpel.server.catalog.report.IAeCatalogAdmin#getCatalogListing(org.activebpel.rt.bpel.impl.list.AeCatalogListingFilter)
	 */
	public synchronized AeCatalogListResult getCatalogListing(
			AeCatalogListingFilter aFilter) {
        return AeInMemoryCatalogListing.extractListing(aFilter,
                getLocationToMapping());
    }

	/**
	 * @see org.activebpel.rt.bpel.server.catalog.report.IAeCatalogAdmin#getCatalogInputSource(java.lang.String)
	 */
	public InputSource getCatalogInputSource(String aLocationHint) {
		try {
			IAeCatalogMapping mapping = getMappingForLocation(aLocationHint);
			if (mapping != null)
				return mapping.getInputSource();
		} catch (IOException e) {
			AeException.logError(
					e,
					MessageFormat.format(
							AeMessages.getString("AeCatalog.ERROR_1"), //$NON-NLS-1$
							new Object[] { aLocationHint }));
		}

		return null;
	}

	/**
	 * @see org.activebpel.rt.bpel.server.catalog.report.IAeCatalogAdmin#getCatalogItemDetail(java.lang.String)
	 */
	public synchronized AeCatalogItemDetail getCatalogItemDetail(
			String aLocationHint) {
		IAeCatalogMapping mapping = getLocationToMapping()
				.get(aLocationHint);
		// check for mapping == null, in case where the catalog entry has been
		// deleted.
		if (mapping != null) {
			try {
				// TODO (cck) all catalog items are considered XML documents
				// this may not be the case at some point
				String text = AeXMLParserBase.documentToString(
						mapping.getDocument(), true);
				AeCatalogItemPlanReference[] plansThatUseThisWsdl = getPlanReferencesForLocation(aLocationHint);
				AeCatalogItemDetail detail = new AeCatalogItemDetail(
						aLocationHint, mapping.getTypeURI(),
						mapping.getTargetNamespace(), text,
						plansThatUseThisWsdl,
						AeUtil.getShortNameForLocation(aLocationHint));
				return detail;
			} catch (Throwable ex) {
				AeException.logError(ex);
				return new AeCatalogItemDetail(
						aLocationHint,
						mapping.getTypeURI(),
						mapping.getTargetNamespace(),
						AeMessages
								.getString("AeCatalog.ERROR_CREATING_ITEM_DETAIL"), new AeCatalogItemPlanReference[0], AeUtil.getShortNameForLocation(aLocationHint)); //$NON-NLS-1$
			}
		} else {
			return null;
		}
	}

	/**
	 * Finds all plans that are refering to thge passed location.
	 * 
	 * @param aLocationHint
	 * @return Array of plan references for the passed location
	 */
	protected AeCatalogItemPlanReference[] getPlanReferencesForLocation(
			String aLocationHint) throws AeException {
		// Loops through plans to cross reference the resource usage
		List<AeCatalogItemPlanReference> details = new ArrayList<AeCatalogItemPlanReference>();
		for (Iterator iter = AeEngineFactory.getBean(
				IAeDeploymentProvider.class).getDeployedPlans(); iter.hasNext();) {
			IAeProcessDeployment deployment = (IAeProcessDeployment) iter
					.next();
			for (ReferenceType ref : deployment.getAllReferenceTypes()) {
				if (AeUtil.compareObjects(aLocationHint,ref.getLocation()))
					details.add(new AeCatalogItemPlanReference(new AeQName(deployment.getProcessDef().getQName())));
			}
		}

		return details.toArray(new AeCatalogItemPlanReference[details.size()]);
	}

	/**
	 * @return Returns the locationToMapping.
	 */
	protected Map<String, IAeCatalogMapping> getLocationToMapping() {
		return mLocationToMapping;
	}

	/**
	 * @param aLocationToMapping
	 *            The locationToMapping to set.
	 */
	protected void setLocationToMapping(Map<String, IAeCatalogMapping> aLocationToMapping) {
		mLocationToMapping = aLocationToMapping;
	}

	/**
	 * @return Returns the namespaceMapping.
	 */
	protected Map<String, Object> getNamespaceMapping() {
		return mNamespaceMapping;
	}

	/**
	 * @param aNamespaceMapping
	 *            The namespaceMapping to set.
	 */
	protected void setNamespaceMapping(Map<String, Object> aNamespaceMapping) {
		mNamespaceMapping = aNamespaceMapping;
	}

	/**
	 * @return Returns the deploymentMapping.
	 */
	protected Map<IAeDeploymentId, IAeCatalogMapping[]> getDeploymentMapping() {
		return mDeploymentMapping;
	}

	/**
	 * @param aDeploymentMapping
	 *            The deploymentMapping to set.
	 */
	protected void setDeploymentMapping(Map<IAeDeploymentId, IAeCatalogMapping[]> aDeploymentMapping) {
		mDeploymentMapping = aDeploymentMapping;
	}

	public Statistics getCacheStatistics() {
		return mCache.getStatistics();
	}
}
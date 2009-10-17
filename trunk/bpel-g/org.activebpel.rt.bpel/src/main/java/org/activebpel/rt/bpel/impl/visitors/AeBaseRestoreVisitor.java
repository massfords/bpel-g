// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/visitors/AeBaseRestoreVisitor.java,v 1.6.4.1 2008/04/21 16:09:43 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.visitors;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeEndpointReference;
import org.activebpel.rt.bpel.def.util.AeLocationPathUtils;
import org.activebpel.rt.bpel.impl.AeBusinessProcess;
import org.activebpel.rt.bpel.impl.AePartnerLink;
import org.activebpel.rt.bpel.impl.AeVariable;
import org.activebpel.rt.bpel.impl.IAeDynamicScopeParent;
import org.activebpel.rt.bpel.impl.IAeImplStateNames;
import org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl;
import org.activebpel.rt.bpel.impl.activity.AeDynamicScopeCreator;
import org.activebpel.rt.bpel.impl.activity.support.AeCorrelationSet;
import org.activebpel.rt.bpel.impl.storage.AeProcessImplStateAttributeDefaults;
import org.activebpel.rt.bpel.impl.storage.AeRestoreImplState;
import org.activebpel.rt.util.AeIntMap;
import org.activebpel.rt.util.AeUtil;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Convenience base class for visitors that restore state in implementation
 * objects.
 */
public class AeBaseRestoreVisitor extends AeImplTraversingVisitor implements IAeImplStateNames
{
   /** State object from which to restore state. */
   private final AeRestoreImplState mImplState;

   /** Maps object ids to objects. */
   private final AeIntMap mIdObjectMap = new AeIntMap();
   
   /**
    * Constructor.
    *
    * @param aImplState The state object from which to restore state.
    */
   public AeBaseRestoreVisitor(AeRestoreImplState aImplState)
   {
      mImplState = aImplState;
   }
   
   /**
    * Restores the scope instance specified by the path. This path must point to
    * a scope nested within a dynamic scope parent (parallel forEach, onEvent, onAlarm).
    * 
    * @param aScopeLocation
    * @param aGenerateNewObjectIdsFlag True if the scope being restored should create
    *                                  new object ids for its child objects.
    *                                  False if the restored scope will be immediately
    *                                  visited and have its object id's restored
    *                                  from the state doc.
    */
   protected AeActivityScopeImpl restoreScopeInstance(String aScopeLocation, boolean aGenerateNewObjectIdsFlag) 
      throws AeBusinessProcessException
   {
      String parentPath = AeLocationPathUtils.getParentPath(aScopeLocation);
      IAeDynamicScopeParent scopeParent = (IAeDynamicScopeParent) getImplState().getProcess().findBpelObjectOrThrow(parentPath);

      int instanceToRestore = AeLocationPathUtils.getNodePathInstanceNum(aScopeLocation);
      List scopes = AeDynamicScopeCreator.create(aGenerateNewObjectIdsFlag, scopeParent, instanceToRestore, instanceToRestore);
      AeActivityScopeImpl scope = (AeActivityScopeImpl) scopes.get(0);
      scopeParent.getCompensatableChildren().add(scope);
      return scope;
   }
   
   /**
    * Returns the value of the specified attribute or its default value if the
    * attribute is not present on the element.
    *
    * @param aElement The owning <code>Element</code>.
    * @param aAttributeName The name of the state attribute.
    * @return String
    */
   protected static String getAttribute(Element aElement, String aAttributeName)
   {
      String attribute = aElement.getAttribute(aAttributeName);
   
      // If the requested attribute is missing, then use its default value.
      if (AeUtil.isNullOrEmpty(attribute))
      {
         attribute = AeProcessImplStateAttributeDefaults.getDefaults().get(aAttributeName);
      }
   
      return attribute;
   }

   /**
    * Returns the <code>boolean</code> value of the specified attribute or its
    * default value if the attribute is not present on the element.
    *
    * @param aElement The owning <code>Element</code>.
    * @param aAttributeName The name of the state attribute.
    * @return int
    */
   protected static boolean getAttributeBoolean(Element aElement, String aAttributeName)
   {
      return "true".equals(getAttribute(aElement, aAttributeName)); //$NON-NLS-1$
   }

   /**
    * Returns the <code>int</code> value of the specified attribute or its
    * default value if the attribute is not present on the element.
    *
    * @param aElement The owning <code>Element</code>.
    * @param aAttributeName The name of the state attribute.
    * @return int
    */
   protected static int getAttributeInt(Element aElement, String aAttributeName)
   {
      return Integer.parseInt(getAttribute(aElement, aAttributeName));
   }
   
   /**
    * Returns the <code>long</code> value of the specified attribute or its
    * default value if the attribute is not present on the element.
    *
    * @param aElement The owning <code>Element</code>.
    * @param aAttributeName The name of the state attribute.
    * @return long
    */
   protected static long getAttributeLong(Element aElement, String aAttributeName)
   {
      return Long.parseLong(getAttribute(aElement, aAttributeName));
   }   

   /**
    * Returns the state object used by this visitor.
    */
   protected AeRestoreImplState getImplState()
   {
      return mImplState;
   }

   /**
    * Returns object with the specified id.
    *
    * @param aObjectId
    */
   protected Object getObject(int aObjectId)
   {
      return getIdObjectMap().get(aObjectId);
   }

   /**
    * Returns map from object ids to objects.
    */
   protected AeIntMap getIdObjectMap()
   {
      return mIdObjectMap;
   }

   /**
    * Assign the specified id to the specified object.
    *
    * @param aObjectId
    * @param aObject
    */
   protected void putIdObject(int aObjectId, Object aObject)
   {
      getIdObjectMap().put(aObjectId, aObject);
   }

   /**
    * Restores a correlation set.
    *
    * @param aElement The correlation set's state <code>Element</code>
    * @param aCorrelationSet The correlation set to restore.
    */
   protected static void restoreCorrelationSet(Element aElement, AeCorrelationSet aCorrelationSet) throws AeBusinessProcessException
   {
      boolean initialized = getAttributeBoolean(aElement, STATE_INIT);
      aCorrelationSet.setInitialized(initialized);
      
      if (aCorrelationSet.hasCustomLocationPath())
      {
         aCorrelationSet.setLocationId(getAttributeInt(aElement, STATE_LOCATIONID));
      }

      if (initialized)
      {
         // Select the property elements.
         String xpath = "./" + STATE_PROPERTY; //$NON-NLS-1$
         List properties = selectNodes(aElement, xpath, "Error restoring correlation set properties"); //$NON-NLS-1$

         // Convert property elements to property map.
         Map map = new HashMap();

         for (Iterator i = properties.iterator(); i.hasNext(); )
         {
            Element property = (Element) i.next();
            String name = getAttribute(property, STATE_NAME);
            String ns = getAttribute(property, STATE_NAMESPACEURI);
            QName key = new QName(ns, name);
            String value = getAttribute(property, STATE_VALUE);

            map.put(key, value);
         }

         aCorrelationSet.setPropertyValues(map);
      }

      // Restore the version number *after* setting other data, because
      // setting data may update the version number.
      int versionNumber = getAttributeInt(aElement, STATE_VERSION);
      aCorrelationSet.setVersionNumber(versionNumber);
   }

   /**
    * Restores a scope activity's variable.
    *
    * @param aElement The variable's state <code>Element</code>
    * @param aVariable The variable to restore.
    */
   protected static void restoreVariable(Element aElement, AeVariable aVariable) throws AeBusinessProcessException
   {
      // Restore the version number *after* setting other data, because
      // setting data may update the version number.
      int versionNumber = getAttributeInt(aElement, STATE_VERSION);
      aVariable.setVersionNumber(versionNumber);
      
      if (aVariable.hasCustomLocationPath())
      {
         AeBusinessProcess process = ((AeBusinessProcess)aVariable.getProcess());
         process.addVariableMapping(aVariable);
      }
      
      // TODO (KR) Update variable to load its data dynamically.
   }

   /**
    * Restores a scope activity's variable.
    *
    * @param aElement The partner link's state <code>Element</code>
    * @param aPartnerLink The partner link to restore.
    */
   protected static void restorePartnerLink(Element aElement, AePartnerLink aPartnerLink) throws AeBusinessProcessException
   {
      if (!AeUtil.isNullOrEmpty(aPartnerLink.getMyRole()))
      {
         IAeEndpointReference reference = aPartnerLink.getMyReference();
         restoreEndpointReference(reference, aElement, STATE_ROLE);
      }

      if (!AeUtil.isNullOrEmpty(aPartnerLink.getPartnerRole()))
      {
         IAeEndpointReference reference = aPartnerLink.getPartnerReference();
         restoreEndpointReference(reference, aElement, STATE_PROLE);
      }

      int versionNumber = getAttributeInt(aElement, STATE_VERSION);
      aPartnerLink.setVersionNumber(versionNumber);

      if (aPartnerLink.hasCustomLocationPath())
      {
         AeBusinessProcess process = ((AeBusinessProcess) aPartnerLink.getProcess());
         process.addPartnerLinkMapping(aPartnerLink);
      }
   }


   /**
    * Restores an endpoint reference.
    *
    * @param aReference The endpoint reference to restore.
    * @param aElement The document element for the endpoint reference's partner link.
    * @param aTag The tag for the element that contains the endpoint reference's data.
    * @throws AeBusinessProcessException
    */
   protected static void restoreEndpointReference(IAeEndpointReference aReference, Element aElement, String aTag) throws AeBusinessProcessException
   {
      // Select the endpoint reference element from the specified partner role
      // element.
      String xpath = "./" + aTag + "/*"; // relative path from partnerLink element //$NON-NLS-1$ //$NON-NLS-2$
      Element element = (Element) selectSingleNode(aElement, xpath, "Error restoring endpoint reference"); //$NON-NLS-1$

      // Set the endpoint reference's data from the 1 matching element.
      aReference.setReferenceData(element);
   }

   /**
    * Selects nodes by XPath.
    *
    * @param aNode The node to select from.
    * @param aPath The XPath expression.
    * @param aErrorMessage The message to use for exceptions.
    * @return List The list of matching nodes.
    * @throws AeBusinessProcessException
    */
   protected static List selectNodes(Node aNode, String aPath, String aErrorMessage) throws AeBusinessProcessException
   {
      try
      {
         XPath xpath = new DOMXPath(aPath);
         return xpath.selectNodes(aNode);
      }
      catch (JaxenException e)
      {
         throw new AeBusinessProcessException(aErrorMessage + AeMessages.getString("AeRestoreImplStateVisitor.ERROR_23") + aPath, e); //$NON-NLS-1$
      }
   }

   /**
    * Selects a single node by XPath. Returns <code>null</code> if there is no
    * matching node.
    *
    * @param aNode The node to select from.
    * @param aPath The XPath expression.
    * @param aErrorMessage The message to use for exceptions.
    * @return Node The matching node or <code>null</code> if there is no matching node.
    * @throws AeBusinessProcessException
    */
   protected static Node selectOptionalNode(Node aNode, String aPath, String aErrorMessage) throws AeBusinessProcessException
   {
      List nodes;
   
      try
      {
         XPath xpath = new DOMXPath(aPath);
         nodes = xpath.selectNodes(aNode);
      }
      catch (JaxenException e)
      {
         throw new AeBusinessProcessException(aErrorMessage + AeMessages.getString("AeRestoreImplStateVisitor.ERROR_23") + aPath, e); //$NON-NLS-1$
      }
   
      // Check for no more than 1 matching node.
      if (nodes.size() > 1)
      {
         throw new AeBusinessProcessException(aErrorMessage + AeMessages.getString("AeRestoreImplStateVisitor.ERROR_25") + aPath); //$NON-NLS-1$
      }
   
      return (nodes.size() == 0) ? null : (Node) nodes.get(0);
   }

   /**
    * Selects a single node by XPath.
    *
    * @param aNode The node to select from.
    * @param aPath The XPath expression.
    * @param aErrorMessage The message to use for exceptions.
    * @return Node The matching node.
    * @throws AeBusinessProcessException
    */
   protected static Node selectSingleNode(Node aNode, String aPath, String aErrorMessage) throws AeBusinessProcessException
   {
      List nodes;
   
      try
      {
         XPath xpath = new DOMXPath(aPath);
         nodes = xpath.selectNodes(aNode);
      }
      catch (JaxenException e)
      {
         throw new AeBusinessProcessException(aErrorMessage + AeMessages.getString("AeRestoreImplStateVisitor.ERROR_23") + aPath, e); //$NON-NLS-1$
      }
   
      // Check for exactly 1 matching node.
      if (nodes.size() == 0)
      {
         throw new AeBusinessProcessException(aErrorMessage + AeMessages.getString("AeRestoreImplStateVisitor.ERROR_27") + aPath); //$NON-NLS-1$
      }
   
      if (nodes.size() > 1)
      {
         throw new AeBusinessProcessException(aErrorMessage + AeMessages.getString("AeRestoreImplStateVisitor.ERROR_25") + aPath); //$NON-NLS-1$
      }
   
      return (Node) nodes.get(0);
   }

   /**
    * Converts a string representing a date in milliseconds to a
    * <code>Date</code>. An empty string represents a null date.
    *
    * @param aMillisString
    * @return Date
    */
   protected static Date toDate(String aMillisString)
   {
      Date result = null;
   
      if (!AeUtil.isNullOrEmpty(aMillisString))
      {
         long millis = Long.parseLong(aMillisString);
   
         result = new Date(millis);
      }
   
      return result;
   }
}
// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/AeCorrelationSet.java,v 1.34 2007/07/12 18:58:22 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.def.AeCorrelationSetDef;
import org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.AeCorrelationViolationException;
import org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl;
import org.activebpel.rt.bpel.xpath.AeXPathHelper;
import org.activebpel.rt.message.AeMessagePartsMap;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeXPathUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.wsdl.def.IAePropertyAlias;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.activebpel.rt.xml.schema.AeTypeMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Models a <code>correlationSet</code> defined as part of a scope or process.
 */
public class AeCorrelationSet extends AeScopedObject implements Cloneable
{
   /** Records whether or not this correlation set has been initialized. */
   private boolean mInitialized = false;
   
   /** The set of data associated with an initialized correlation set. */
   private HashMap mPropertyValues;
   
   /** maintains list of listeners waiting to queue themselves once we're initialized */
   private Collection mListeners;

   /** The version number of the variable. Increments with each change */
   private int mVersionNumber;

   /**
    * Ctor requires def and parent impl.
    * @param aDef
    * @param aParent
    */
   public AeCorrelationSet(AeCorrelationSetDef aDef, AeActivityScopeImpl aParent)
   {
      super(aParent, aDef);
   }

   /**
    * Converts a Map of Correlation Set property data into XML formatted string.
    * @param aCorrSetData map of Correlation Propert data 
    */
   public static String convertCorrSetDataToString(Map aCorrSetData)
   {
      Document crsDoc = AeXmlUtil.newDocument();
      Element root = crsDoc.createElement("corrSet");  //$NON-NLS-1$
      crsDoc.appendChild(root);
      
      for (Iterator iter = aCorrSetData.keySet().iterator(); iter.hasNext();)
      {
         QName propQName = (QName)iter.next();
         String value = (String) aCorrSetData.get(propQName);
         Element ele = crsDoc.createElement("property");  //$NON-NLS-1$  
         root.appendChild(ele);
         ele.setAttribute("name", propQName.getLocalPart());  //$NON-NLS-1$  
         ele.setAttribute("namespaceURI", propQName.getNamespaceURI());  //$NON-NLS-1$  
         ele.setAttribute("value", String.valueOf(value));  //$NON-NLS-1$ 
      }
      
      return AeXMLParserBase.documentToString(crsDoc, true);
   }

   /**
    * Converts an XML document containing Correlation Set property values to a Map of
    * property name to values. 
    * @param aCorrSetData the XML document to process if null returns an empty map
    * @throws AeException if document is invalid
    */
   public static Map convertCorrSetDataToMap(Document aCorrSetData) throws AeException
   {
      Map correlationData = new HashMap();
      if(aCorrSetData != null)
      {
         List propertyNodeList = AeXPathUtil.selectNodes(aCorrSetData.getDocumentElement(), "//property"); //$NON-NLS-1$
         for (Iterator iter = propertyNodeList.iterator(); iter.hasNext();)
         {
            Element ele = (Element) iter.next();
            QName propQName = new QName( ele.getAttribute("namespaceURI"), ele.getAttribute("name")); //$NON-NLS-1$ //$NON-NLS-2$
            correlationData.put(propQName , ele.getAttribute("value") );  //$NON-NLS-1$
         }
      }      
      return correlationData;
   }
   
   /**
    * Returns the property values for this correlation set. If the set is not
    * initialized then you'll get an exception here. 
    */
   public Map getPropertyValues() throws AeCorrelationViolationException
   {
      if( ! isInitialized())
         throw new AeCorrelationViolationException(getBPELNamespace(), AeCorrelationViolationException.UNINITIALIZED_CORRELATION_SET);

      return getPropertyValuesMap();
   }

   /**
    * Getter for the property values map with lazy load.
    */
   protected Map getPropertyValuesMap()
   {
      if(mPropertyValues == null)
         mPropertyValues = new HashMap();
      return mPropertyValues;
   }
   
   /**
    * Initializes the correlation set by extracting its values from the variable 
    * passed in. 
    * @param aMessageData
    */
   protected void initiate(IAeMessageData aMessageData, AeMessagePartsMap aMessagePartsMap) throws AeBusinessProcessException
   {
      Map map = getPropertyValuesMap();
      buildPropertyMap(aMessageData, aMessagePartsMap, map);
      setInitialized(true);
      notifyListeners();
   }
   
   /**
    * Convenience method that either initiates or validates the correlation set
    * based on the initiate flag passed in.
    * @param aMessageData
    * @param aMessagePartsMap
    * @param aInitiateValue
    * @throws AeBusinessProcessException
    */
   public void initiateOrValidate(IAeMessageData aMessageData, AeMessagePartsMap aMessagePartsMap, String aInitiateValue) throws AeBusinessProcessException
   {
      if (AeCorrelationDef.INITIATE_YES.equals(aInitiateValue) && !isInitialized())
         initiate(aMessageData, aMessagePartsMap);
      else
         validate(aMessageData, aMessagePartsMap);
   }
   
   /**
    * Validates that the correlation values that have already been initialized for 
    * this set have not changed in this variable.
    * @param aMessageData
    * @throws AeBusinessProcessException
    */
   protected void validate(IAeMessageData aMessageData, AeMessagePartsMap aMessagePartsMap) throws AeBusinessProcessException
   {
      if (!isInitialized())
         throw new AeCorrelationViolationException(getBPELNamespace(), AeCorrelationViolationException.UNINITIALIZED_CORRELATION_SET);
      
      Map map = new HashMap();
      buildPropertyMap(aMessageData, aMessagePartsMap, map);
      
      if (!getPropertyValuesMap().equals(map))
      {
         throw new AeCorrelationViolationException(getBPELNamespace(), AeCorrelationViolationException.IMMUTABLE);
      }
   }

   /**
    * Builds a correlation property map.
    * 
    * @param aMessageData Contents of message
    * @param aMap An empty correlation map.
    * @throws AeBusinessProcessException
    * @throws AeBpelException
    */
   private void buildPropertyMap(IAeMessageData aMessageData, AeMessagePartsMap aMessagePartsMap, Map aMap)
      throws AeBusinessProcessException, AeBpelException
   {
      for (Iterator it=getDefinition().getPropertiesList(); it.hasNext();)
      {
         QName propName = (QName) it.next();
         IAePropertyAlias propAlias = getProcess().getProcessDefinition().getPropertyAliasForCorrelation(aMessagePartsMap, propName);
         
         AeTypeMapping typeMapping = getProcess().getEngine().getTypeMapping();
         QName propType = getProcess().getProcessDefinition().getPropertyType(propAlias.getPropertyName());
         Object simpleType = AeXPathHelper.getInstance(getBPELNamespace()).extractCorrelationPropertyValue(
                     propAlias, aMessageData, typeMapping, propType);
         aMap.put(propName, simpleType);
      }
   }

   /**
    * Notifies the listeners that the correlation set has been initialized. 
    */
   private void notifyListeners() throws AeBusinessProcessException
   {
      if (mListeners != null)
      {
         List list = new ArrayList(getListeners());
         for (Iterator iter = list.iterator(); iter.hasNext();)
         {
            IAeCorrelationListener listener = (IAeCorrelationListener) iter.next();
            listener.correlationSetInitialized(this);
         }
      }
   }

   /**
    * Convenience method to cast the def to our specific class
    */
   public AeCorrelationSetDef getDefinition()
   {
      return (AeCorrelationSetDef) getBaseDef();
   }
   
   /**
    * Returns true if this correlation sett has been initialized.
    */
   public boolean isInitialized()
   {
      return mInitialized;
   }

   /**
    * Set this correlation sets intialized state flag. 
    */
   public void setInitialized(boolean aInitialized)
   {
      // Increment the version number unless we're clearing an already cleared correlation set
      // (i.e. don't increment if (!aInitialized && !mInitialized)).
      if (aInitialized || mInitialized)
      {
         setVersionNumber(getVersionNumber() + 1);
      }

      mInitialized = aInitialized;
   }

   /**
    * Adds a listener to receive a callback when this correlation set is initialized  
    */
   public void addCorrelationListener(IAeCorrelationListener aCorrelationListener)
   {
      getListeners().add(aCorrelationListener);
   }
   
   /**
    * Adds a listener to receive a callback when this correlation set is initialized  
    */
   public void removeCorrelationListener(IAeCorrelationListener aCorrelationListener)
   {
      getListeners().remove(aCorrelationListener);
   }

   /**
    * Getter for the listeners collection. 
    */
   private Collection getListeners()
   {
      if (mListeners == null)
      {
         mListeners = new ArrayList();
      }
      return mListeners;
   }
   
   /**
    * Clears the values for the correlation set. Called from the correlation 
    * set's declaring scope each time the scope is going to execute since the 
    * correlation set's state is not preserved across invocations.
    */
   public void clear()
   {
      setInitialized(false);
      if(mPropertyValues != null)
	     getPropertyValuesMap().clear();
   }

   /**
    * Returns a copy of this corr set so it can be persisted in our snapshot for 
    * any future compensation logic.  
    */
   public Object clone()
   {
      try
      {
         return super.clone();
      }
      catch (CloneNotSupportedException e)
      {
         throw new InternalError("y no clone work?"); //$NON-NLS-1$
      }
   }
   
   /**
    * Sets property values map.
    *
    * @param aMap the map of name-value pairs to set
    */
   public void setPropertyValues(Map aMap) throws AeBpelException
   {
      setInitialized(true);
      mPropertyValues = new HashMap(aMap);
   }

   /**
    * Getter for the version number
    */
   public int getVersionNumber()
   {
      return mVersionNumber;
   }

   /**
    * Setter for the version number
    */
   public void setVersionNumber(int aVersionNumber)
   {
      mVersionNumber = aVersionNumber;
   }

   /**
    * Returns the BPEL namespace.
    */
   protected String getBPELNamespace()
   {
      return getProcess().getProcessDefinition().getNamespace();
   }
}

//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/AeStorageUtil.java,v 1.6 2007/07/12 19:00:12 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage;

import java.io.Reader;
import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.coord.IAeCoordinating;
import org.activebpel.rt.bpel.coord.IAeCoordinationManager;
import org.activebpel.rt.bpel.impl.IAeCoordinationManagerInternal;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.impl.fastdom.AeFastElement;
import org.activebpel.rt.bpel.impl.fastdom.AeFastText;
import org.activebpel.rt.bpel.impl.queue.AeInboundReceive;
import org.activebpel.rt.bpel.impl.queue.AeMessageReceiver;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.coord.AeCoordinationContext;
import org.activebpel.rt.bpel.server.coord.AeCoordinationFactory;
import org.activebpel.rt.bpel.server.coord.AePersistentCoordinationId;
import org.activebpel.rt.bpel.server.coord.IAeCoordinationId;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXPathUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Util methods used by the storage layer.
 */
public class AeStorageUtil
{
   /** The name of the attribute used for the type of correlated property. */
   private static final String TYPE_ATTRNAME = "type"; //$NON-NLS-1$
   /** The name of the attribute used for the type of correlated property. */
   private static final String NAMESPACE_ATTRNAME = "namespace"; //$NON-NLS-1$
   /** The name of the attribute used for the type of correlated property. */
   private static final String LOCAL_PART_ATTRNAME = "localPart"; //$NON-NLS-1$
   /** The name of the xml element used for the root of a  property. */
   private static final String PROPERTY_TAGNAME = "property"; //$NON-NLS-1$

   /**
    * Creates an instances of IAeCoordinating given some a bunch of data, typically from the
    * database.
    * 
    * @param aProcessId
    * @param aCoordinationId
    * @param aState
    * @param aCoordinationRole
    * @param aContextDocument
    * @param aManager
    * @throws AeStorageException
    */
   public static IAeCoordinating createCoordinating(long aProcessId, String aCoordinationId, String aState,
         int aCoordinationRole, Document aContextDocument, IAeCoordinationManager aManager)
         throws AeStorageException
   {
      AePersistentCoordinationId coordinationId = new AePersistentCoordinationId(aProcessId, aCoordinationId);
      AeCoordinationContext context = null;
      try
      {
         context = AeStorageUtil.createContext(coordinationId, aContextDocument);
      }
      catch (Exception e)
      {
         throw new AeStorageException(e);
      }
      
      IAeCoordinating coordinating = null;
      try
      {
         AeCoordinationFactory factory = AeCoordinationFactory.getInstance();
         coordinating = factory.createCoordination((IAeCoordinationManagerInternal) aManager, context,
               aState, aCoordinationRole);
      }
      catch (Exception e)
      {
         throw new AeStorageException(e);
      }
      return coordinating;
   }
   
   /**
    * Creates XML document given the context.
    * @param aContext
    */
   public static AeFastDocument createCoordinationContextDocument(AeCoordinationContext aContext) throws AeStorageException
   {
      AeFastDocument fastDocument = new AeFastDocument();
      AeFastElement element = new AeFastElement("coordinationContext");//$NON-NLS-1$
      element.setAttribute("contextFactory", "AeCoordinationContext");   //$NON-NLS-1$  //$NON-NLS-2$      
      fastDocument.appendChild(element);
      Properties properties = aContext.getProperties();
      Iterator it = properties.keySet().iterator();
      while (it.hasNext())
      {
         String key = (String) it.next();
         String value = properties.getProperty(key);
         element = new AeFastElement("property");//$NON-NLS-1$
         element.setAttribute("name", key);   //$NON-NLS-1$         
         fastDocument.getRootElement().appendChild(element);
         element.appendChild(new AeFastText(value));
      }
      return fastDocument;     
   }
   
   /**
    * Creates a context given the xml document.
    * 
    * @param aCoordinationId
    * @param aContextDocument
    * @throws Exception
    */
   public static AeCoordinationContext createContext(IAeCoordinationId aCoordinationId, Document aContextDocument) throws Exception
   {
      Properties properties = new Properties();
      List propertyNodes = AeXPathUtil.selectNodes(aContextDocument, "//property" ); //$NON-NLS-1$
      Iterator it = propertyNodes.iterator();
      while (it.hasNext())
      {
         Element ele = (Element) it.next();
         String name = ele.getAttribute("name");//$NON-NLS-1$
         String value = getText(ele, "text()");//$NON-NLS-1$
         properties.setProperty(name, value);
      }
      AeCoordinationContext context = new AeCoordinationContext(aCoordinationId);
      context.setProperties(properties);
      return context;
   }
         
   /**
    * Returns the node value (text) for the given context and xpath.
    * @param aNode
    * @param aPath
    * @throws AeException
    */
   protected static String getText(Node aNode, String aPath) throws AeException
   {
      String rVal = ""; //$NON-NLS-1$
      try
      {
         XPath xpath = new DOMXPath(aPath);
         Node node = (Node) xpath.selectSingleNode(aNode);
         if (node != null)
         {
            rVal = node.getNodeValue();
         }
      }
      catch (JaxenException e)
      {
         throw new AeException(AeMessages.format("AeCoordinationStorageUtil.XPATH_ERROR",aPath), e); //$NON-NLS-1$
      }
      return rVal;
   }    

   /**
    * Creates a AeFastDocument representing the correlation properties.
    * 
    * @param aMessageReceiver
    */
   public static AeFastDocument getCorrelationProperties(AeMessageReceiver aMessageReceiver)
   {
      AeFastDocument propsDoc = new AeFastDocument();
      AeFastElement propsElem = new AeFastElement("properties"); //$NON-NLS-1$
      propsDoc.appendChild(propsElem);

      for (Iterator iter = aMessageReceiver.getCorrelation().entrySet().iterator(); iter.hasNext(); )
      {
         Map.Entry entry = (Map.Entry) iter.next();
         QName key = (QName) entry.getKey();
         Object value = entry.getValue();

         AeFastElement propElem = new AeFastElement(PROPERTY_TAGNAME);
         propElem.setAttribute(LOCAL_PART_ATTRNAME, key.getLocalPart());
         propElem.setAttribute(NAMESPACE_ATTRNAME, AeUtil.getSafeString(key.getNamespaceURI()));
         propElem.setAttribute(TYPE_ATTRNAME, value.getClass().getName());
         propElem.appendChild(new AeFastText(value.toString()));
         propsElem.appendChild(propElem);
      }

      return propsDoc;
   }

   /**
    * Deserializes a correlation set from an XML string representation back to a correlation map.
    * 
    * @param aReader The XML string representation of the correlation map.
    * @return The correlation <code>Map</code>.
    * @throws AeStorageException
    */
   public static Map deserializeCorrelationProperties(Reader aReader) throws AeStorageException
   {
      try
      {
         AeXMLParserBase parser = new AeXMLParserBase();
         parser.setValidating(false);
         Document doc = parser.loadDocument(aReader, null);

         return deserializeCorrelationProperties(doc.getDocumentElement());
      }
      catch (AeException ex)
      {
         throw new AeStorageException(ex);
      }
   }

   /**
    * "Deserializes" a correlation set from a DOM Element representation back to a correlation map.
    * 
    * @param aRootElement
    * @throws AeStorageException
    */
   public static Map deserializeCorrelationProperties(Element aRootElement) throws AeStorageException
   {
      try
      {
         Map map = new HashMap();

         NodeList nl = aRootElement.getElementsByTagName(PROPERTY_TAGNAME);
         for (int i = 0; i < nl.getLength(); i++)
         {
            Element propElem = (Element) nl.item(i);
            String nameLocal = propElem.getAttribute(LOCAL_PART_ATTRNAME);
            String nameNamespace = propElem.getAttribute(NAMESPACE_ATTRNAME);
            QName name = new QName(nameNamespace, nameLocal);
            String valueType = propElem.getAttribute(TYPE_ATTRNAME);
            String value = AeXmlUtil.getText(propElem);
            Class c = Class.forName(valueType);
            Constructor constructor = c.getConstructor( new Class[] { String.class } );
            map.put(name, constructor.newInstance(new Object[] { value }));
         }

         return map;
      }
      catch (Exception ex)
      {
         throw new AeStorageException(ex);
      }
   }

   /**
    * Returns the "matches" hash value of the receive.
    * 
    * @param aReceiveObject A message receiver.
    * @return The receive's hash value (with no correlation data hashed in).
    */
   public static int getReceiveMatchHash(AeMessageReceiver aReceiveObject)
   {
      // TODO (EPW) change to use the partner link id or location only here (requires DB patch).
      // TODO (EPW) use the process name instead of the port type
      return getReceiveMatchHash(
         aReceiveObject.getOperation(),
         aReceiveObject.getPartnerLinkOperationKey().getPartnerLinkName(),
         aReceiveObject.getPortType());
   }

   /**
    * Returns the "correlates" hash value of the receive.
    * 
    * @param aReceiveObject A message receiver.
    * @return The receive's hash value (correlation data only).
    */
   public static int getReceiveCorrelatesHash(AeMessageReceiver aReceiveObject)
   {
      return getReceiveCorrelatesHash(aReceiveObject.getCorrelation());
   }

   /**
    * Returns the "matches" hash value of the inbound receive.
    * 
    * @param aInboundReceive An inbound receive.
    * @return The receive's hash value (with no correlation data hashed in).
    */
   public static int getReceiveMatchHash(AeInboundReceive aInboundReceive)
   {
      // TODO (EPW) change to use the partner link id or location only here (requires DB patch).
      // TODO (EPW) change to use the process QName instead of portType (requires DB patch).
      return getReceiveMatchHash(
         aInboundReceive.getOperation(),
         aInboundReceive.getPartnerLinkOperationKey().getPartnerLinkName(),
         aInboundReceive.getPortType());
   }

   /**
    * This method returns a Hash value that gets stored in the queued receive 
    * table.  This hash value is used to select potentially matching receives.
    * Note that this hash value does not guarantee equality, but is a good 
    * starting point for quickly selecting receives that might match.  A final
    * greedy comparison is needed to fully determine equality.
    * 
    * @param aOperation The receive's operation.
    * @param aPartnerLinkName The receive's partner link name.
    * @param aPortType The receive's port type.
    * @return A hash value.
    */
   public static int getReceiveMatchHash(String aOperation, String aPartnerLinkName, QName aPortType)
   {
      int hash = 0;
      hash += aOperation.hashCode();
      hash += aPartnerLinkName.hashCode();
      hash += getQNameHashCode(aPortType);
      return hash;
   }

   /**
    * This method returns a Hash value that gets stored in the queued receive 
    * table.  This hash value is used to select potentially matching receives.
    * Note that this hash value does not guarantee equality, but is a good 
    * starting point for quickly selecting receives that might match.  A final
    * greedy comparison is needed to fully determine equality.  In addition, 
    * this hash value is only useful for finding receives with the same 
    * correlation data.  The combination of this hash value and the "matches"
    * hash value can be used to find a list of matching AND correlating receives.
    * 
    * @param aCorrelationMap The correlation map for the receive.
    * @return A hash value.
    */
   public static int getReceiveCorrelatesHash(Map aCorrelationMap)
   {
      int hash = 0;
      int count = 1;
      // Sort the keys - this ensures that the correlation set hash is always calculated the
      // same, even if the data is in the map in a different order.
      SortedSet ss = new TreeSet(new Comparator()
      {
         public int compare(Object o1, Object o2)
         {
            String str1 = o1.toString();
            String str2 = o2.toString();
            return str1.compareTo(str2);
         }

         public boolean equals(Object obj)
         {
            return false;
         }
      });
      ss.addAll(aCorrelationMap.keySet());
      for (Iterator iter = ss.iterator(); iter.hasNext(); count++)
      {
         QName key = (QName) iter.next();
         Object val = aCorrelationMap.get(key);
         hash += getQNameHashCode(key) + val.hashCode() * count++;
      }
      return hash;
   }
   
   /**
    * Gets the hash code of a QName.
    * 
    * @param aQName
    */
   public static int getQNameHashCode(QName aQName)
   {
      return ("{" + aQName.getNamespaceURI() + "}" + aQName.getLocalPart()).hashCode(); //$NON-NLS-1$ //$NON-NLS-2$
   }
   
   /**
    * Rolls back the connection and logs any errors.
    * @param aStorageConnection
    */
   public static void rollback(IAeStorageConnection aStorageConnection)
   {
      try
      {
         aStorageConnection.rollback();
      }
      catch(AeStorageException ase)
      {
         AeException.logError(ase);
      }
   }
   
}

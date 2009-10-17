// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/storage/AeRestoreImplState.java,v 1.12 2006/06/26 16:50:49 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.storage;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeLocatableObject;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.AeBusinessProcess;
import org.activebpel.rt.bpel.impl.IAeImplStateNames;
import org.activebpel.rt.bpel.impl.activity.support.AeCompInfo;
import org.activebpel.rt.bpel.impl.visitors.AeRestoreCompInfoVisitor;
import org.activebpel.rt.bpel.impl.visitors.AeRestoreImplStateVisitor;
import org.activebpel.rt.bpel.impl.visitors.AeTerminationStateUpgradeVisitor;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Restores the state of BPEL implementation objects as saved by
 * <code>AeProcessImplState</code>.
 */
public class AeRestoreImplState implements IAeImplStateNames
{
   /** Array of tags of elements that contain process state. */
   public static final String[] sStateElementTagsArray =
   {
      STATE_ACTY,
      STATE_CORRSET,
      STATE_LINK,
      STATE_PLINK,
      STATE_ROOT
   };

   /** Set of tags of elements that contain process state. */
   private static Set mStateElementTags;

   /** The XML document containing the serialized process state. */
   private Document mDocument;

   /** List of paths in the execution queue. */
   private List mExecutionQueuePaths;

   /** Maps location paths to state elements. */
   private Map mLocationPathsMap;
   
   /** Process to restore. */
   private AeBusinessProcess mProcess;

   /** Visitor that traverses the implementation objects. */
   private AeRestoreImplStateVisitor mStateVisitor;

   /** Visitor that traverses the implementation objects to restore {@link AeCompInfo} objects. */
   private AeRestoreCompInfoVisitor mCompInfoVisitor;

   /** Variable locker data. */
   private Node mVariableLockerData;

   /** XML parser used for loading process state documents. */
   private AeXMLParserBase mXMLParser;
   
   /** Version that was read from the state */
   private String mStateVersion;

   /**
    * Returns the value of the specified attribute for a BPEL implementation
    * object.
    *
    * @param aImpl The BPEL implementation object.
    * @param aAttributeName The name of the state attribute.
    * @return String
    */
   public String getAttribute(AeAbstractBpelObject aImpl, String aAttributeName) throws AeBusinessProcessException
   {
      Element element = getElement(aImpl);
      String attribute = element.getAttribute(aAttributeName);

      // If the requested attribute is missing, then use its default value.
      if (AeUtil.isNullOrEmpty(attribute))
      {
         attribute = AeProcessImplStateAttributeDefaults.getDefaults().get(aAttributeName);
      }

      return attribute;
   }

   /**
    * Returns the XML document containing the serialized process state.
    *
    * @return Document
    */
   protected Document getDocument()
   {
      return mDocument;
   }

   /**
    * Returns the element containing the state for a BPEL implementation
    * object.
    *
    * @param aImpl The BPEL implementation object.
    * @return Element The element containing the object's state.
    */
   public Element getElement(IAeLocatableObject aImpl) throws AeBusinessProcessException
   {
      return getElement(aImpl.getLocationPath());
   }

   /**
    * Returns the element containing the state for a BPEL object specified via
    * its definition object.
    *
    * @param aDef The definition object for the BPEL object.
    * @return Element The element containing the object's state.
    */
   public Element getElement(AeBaseDef aDef) throws AeBusinessProcessException
   {
      return getElement(aDef.getLocationPath());
   }

   /**
    * Returns the element containing the state for a BPEL object specified by
    * its location path.
    *
    * @param aLocationPath The location path for the BPEL object.
    * @return Element The element containing the object's state.
    */
   protected Element getElement(String aLocationPath) throws AeBusinessProcessException
   {
      Element element = (Element) getLocationPathsMap().get(aLocationPath);

      if (element == null)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeRestoreImplState.ERROR_0") + aLocationPath); //$NON-NLS-1$
      }

      return element;
   }
   
   /**
    * Returns the list of location paths in the process's execution queue.
    *
    * @return List
    * @throws AeBusinessProcessException
    */
   public List getExecutionQueuePaths() throws AeBusinessProcessException
   {
      if (mExecutionQueuePaths == null)
      {
         List result = new LinkedList();

         // Get execution queue nodes.
         List nodes;

         try
         {
            String xpath = "/*/" + STATE_QUEUE + "/" + STATE_QUEUEITEM + "/@" + STATE_LOC; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            nodes = selectNodes(getDocument(), xpath);
         }
         catch (AeException e)
         {
            throw new AeBusinessProcessException(AeMessages.getString("AeRestoreImplState.ERROR_4"), e); //$NON-NLS-1$
         }

         for (Iterator i = nodes.iterator(); i.hasNext(); )
         {
            Attr attr = (Attr) i.next();
            String locationPath = attr.getValue();

            result.add(locationPath);
         }

         mExecutionQueuePaths = result;
      }

      return mExecutionQueuePaths;
   }

   /**
    * Returns the map of location paths to state elements.
    *
    * @return Map
    * @throws AeBusinessProcessException
    */
   protected Map getLocationPathsMap() throws AeBusinessProcessException
   {
      if (mLocationPathsMap == null)
      {
         Map pathsToElements = new HashMap();
         Map elementsToPaths = new HashMap();

         // Get all nodes that have a locationId attribute.
         List nodes;

         try
         {
            nodes = selectNodes(getDocument(), "//@" + STATE_LOCATIONID); //$NON-NLS-1$
         }
         catch (AeException e)
         {
            throw new AeBusinessProcessException(AeMessages.getString("AeRestoreImplState.ERROR_6"), e); //$NON-NLS-1$
         }

         // Map location ids to elements in the state document.
         for (Iterator i = nodes.iterator(); i.hasNext(); )
         {
            Attr attr = (Attr) i.next();
            Element element = attr.getOwnerElement();

            // Map only to state elements. Ignore location ids that may appear
            // elsewhere.
            if (isStateElement(element))
            {
               int locationId = Integer.parseInt(attr.getValue());
               String locationPath = getProcess().getLocationPath(locationId);

               // the process might not have a mapping of this location id which
               // means that it's a dynamically created object (nested within 
               // an executing parallel for each)
               // we'll add mappings for these objects below when we visit the
               // nodes again for location paths
               if (locationPath != null)
               {
                  pathsToElements.put(locationPath, element);
                  elementsToPaths.put(element, locationPath);
               }
            }
         }

         // Get all nodes that have a locationPath attribute.
         try
         {
            nodes = selectNodes(getDocument(), "//@" + STATE_LOC); //$NON-NLS-1$
         }
         catch (AeException e)
         {
            throw new AeBusinessProcessException(AeMessages.getString("AeRestoreImplState.ERROR_1"), e); //$NON-NLS-1$
         }

         // Map location paths to elements in the state document.
         for (Iterator i = nodes.iterator(); i.hasNext(); )
         {
            Attr attr = (Attr) i.next();
            Element element = attr.getOwnerElement();

            // Map only to state elements. Ignore location paths that may
            // appear elsewhere.
            if (isStateElement(element))
            {
               String locationPath = attr.getValue();

               // If it's a relative path, then prepend the full path from the
               // parent node.
               if (!locationPath.startsWith("/")) //$NON-NLS-1$
               {
                  Element parent = (Element) element.getParentNode();
                  String parentPath = (String) elementsToPaths.get(parent);
                  
                  locationPath = parentPath + "/" + locationPath; //$NON-NLS-1$
               }
               
               pathsToElements.put(locationPath, element);
               elementsToPaths.put(element, locationPath);
            }
         }

         mLocationPathsMap = pathsToElements;
      }

      return mLocationPathsMap;
   }

   /**
    * Returns the process to restore.
    *
    * @return AeBusinessProcess
    */
   public AeBusinessProcess getProcess()
   {
      return mProcess;
   }

   /**
    * Returns the set of tags of elements that contain process state.
    */
   protected static Set getStateElementTags()
   {
      if (mStateElementTags == null)
      {
         mStateElementTags = Collections.unmodifiableSet(new HashSet(Arrays.asList(sStateElementTagsArray)));
      }

      return mStateElementTags;
   }

   /**
    * Returns the visitor used to restore the current state.
    *
    * @return AeRestoreImplStateVisitor
    */
   protected AeRestoreImplStateVisitor getStateVisitor()
   {
      if (mStateVisitor == null)
      {
         AeRestoreImplStateVisitor visitor = new AeRestoreImplStateVisitor(this);

         setStateVisitor(visitor);
      }

      return mStateVisitor;
   }

   /**
    * Returns the visitor used to restore {@link AeCompInfo} objects.
    *
    * @return AeRestoreCompInfoVisitor
    */
   protected AeRestoreCompInfoVisitor getCompInfoVisitor()
   {
      if (mCompInfoVisitor == null)
      {
         AeRestoreCompInfoVisitor visitor = new AeRestoreCompInfoVisitor(this);

         setCompInfoVisitor(visitor);
      }

      return mCompInfoVisitor;
   }

   /**
    * Returns variable locker serialization.
    *
    * @return Node
    * @throws AeBusinessProcessException
    */
   public Node getVariableLockerData() throws AeBusinessProcessException
   {
      if (mVariableLockerData == null)
      {
         // Select variable locker node.
         List nodes;

         try
         {
            String xpath = "/*/" + STATE_VARIABLELOCKER; //$NON-NLS-1$
            nodes = selectNodes(getDocument(), xpath);
         }
         catch (AeException e)
         {
            throw new AeBusinessProcessException(AeMessages.getString("AeRestoreImplState.ERROR_8"), e); //$NON-NLS-1$
         }

         // There should be exactly 1 matching node.
         if (nodes.size() == 0)
         {
            throw new AeBusinessProcessException(AeMessages.getString("AeRestoreImplState.ERROR_9")); //$NON-NLS-1$
         }

         if (nodes.size() > 1)
         {
            throw new AeBusinessProcessException(AeMessages.getString("AeRestoreImplState.ERROR_10")); //$NON-NLS-1$
         }

         mVariableLockerData = (Node) nodes.get(0);
      }

      return mVariableLockerData;
   }

   /**
    * Returns the XML parser that we use to load documents.
    */
   protected AeXMLParserBase getXMLParser()
   {
      if (mXMLParser == null)
      {
         mXMLParser = new AeXMLParserBase();

         // Turn off validation on the parser.
         mXMLParser.setValidating(false);
      }

      return mXMLParser;
   }

   /**
    * Returns <code>true</code> if and only if the specified element is a state
    * element.
    */
   protected static boolean isStateElement(Element aElement)
   {
      return getStateElementTags().contains(aElement.getTagName());
   }

   /**
    * Restores the state of the process.
    */
   protected void restoreState() throws AeBusinessProcessException
   {
      if ((getDocument()) != null && (getProcess() != null))
      {
         getProcess().accept(getStateVisitor());

         // restore all of the comp info objects
         getProcess().accept(getCompInfoVisitor());
         
         IAeImplVisitor upgradeVisitor = getUpgradeVisitor();
         if (upgradeVisitor != null)
         {
            getProcess().accept(upgradeVisitor);
         }
      }
   }
   
   /**
    * Returns an upgrade visitor if the process was restored from an older version of the 
    * state document and requires an additional pass through the impl visitor to adjust its
    * state.
    */
   protected IAeImplVisitor getUpgradeVisitor()
   {
      // We currently only have two versions of the state doc and therefore only need a little code
      // inline here to determine what kind of upgrade is in order.
      if (IAeImplStateNames.STATE_DOC_1_0.equals(getStateVersion()) && (!getProcess().getState().isFinal() || getProcess().isCompensating()))
      {
         // process was written w/ 1.0
         // process is not final OR process is compensating
         return new AeTerminationStateUpgradeVisitor();
      }
      return null;
   }

   /**
    * Restores the state of the process from a serialized XML document.
    *
    * @param aDocument The XML document containing the serialized state.
    */
   public void restoreState(Document aDocument) throws AeBusinessProcessException
   {
      setDocument(aDocument);
      // Visit the implementation objects to restore state.
      restoreState();
   }

   /**
    * Restores the state of the process.
    *
    * @param aInputStream The Java <code>InputStream</code> from which to read
    * the serialized process state.
    */
   public void restoreState(InputStream aInputStream) throws AeBusinessProcessException
   {
      Document document;

      try
      {
         document = getXMLParser().loadDocument(aInputStream, null);         
      }
      catch (AeException e)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeRestoreImplState.ERROR_11"), e); //$NON-NLS-1$
      }
      
      restoreState(document);
   }

   /**
    * Select nodes by XPath.
    *
    * @param aNode The node to select from.
    * @param aPath The XPath expression.
    * @return List The list of matching nodes.
    * @throws AeException
    */
   protected static List selectNodes(Node aNode, String aPath) throws AeException
   {
      try
      {
         XPath xpath = new DOMXPath(aPath);
         return xpath.selectNodes(aNode);
      }
      catch (JaxenException e)
      {
         throw new AeException(AeMessages.getString("AeRestoreImplState.ERROR_12") + aPath, e); //$NON-NLS-1$
      }
   }

   /**
    * Sets the visitor used to restore the {@link AeCompInfo} objects.
    *
    * @param aVisitor The visitor to set.
    */
   protected void setCompInfoVisitor(AeRestoreCompInfoVisitor aVisitor)
   {
      mCompInfoVisitor = aVisitor;
   }
   
   /**
    * Sets the document containing the serialized process state.
    *
    * @param aDocument The document to set.
    */
   protected void setDocument(Document aDocument)
   {
      mDocument = aDocument;
   }

   /**
    * Sets the process to restore.
    *
    * @param aProcess The process to set.
    */
   public void setProcess(AeBusinessProcess aProcess)
   {
      mProcess = aProcess;
   }

   /**
    * Sets the visitor used to restore the current state.
    *
    * @param aVisitor The visitor to set.
    */
   protected void setStateVisitor(AeRestoreImplStateVisitor aVisitor)
   {
      mStateVisitor = aVisitor;
   }
   
   /**
    * Return any business process property elements stored in the process state doc.
    * 
    * @throws AeException
    */
   public Collection getBusinessProcessPropertiesElements() throws AeException
   {
      return selectNodes( getDocument(), "/" + STATE_PROCESSSTATE + "/" + STATE_PROCESSPROPERTY ); //$NON-NLS-1$ //$NON-NLS-2$
   }
   
   /**
    * Return any faultingActivity elements stored in the process state doc.
    * 
    * @throws AeException
    */
   public Collection getFaultingActivityElements() throws AeException
   {
      return selectNodes( getDocument(), "/" + STATE_PROCESSSTATE + "/" + STATE_FAULTING_ACTIVITY ); //$NON-NLS-1$ //$NON-NLS-2$
   }

   /**
    * @return Returns the stateVersion.
    */
   public String getStateVersion()
   {
      return mStateVersion;
   }

   /**
    * @param aStateVersion The stateVersion to set.
    */
   public void setStateVersion(String aStateVersion)
   {
      mStateVersion = aStateVersion;
   }
}

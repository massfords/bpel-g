// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/io/readers/AeDomTraverser.java,v 1.9 2008/02/17 21:09:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def.io.readers;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.io.AeCommentIO;
import org.activebpel.rt.xml.def.io.IAeDefRegistry;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Drives the DOM traversal and reader lookup process.
 */
public class AeDomTraverser
{
   /** registry for bpel element readers */
   private IAeDefRegistry mRegistry;
   /** AeProcessDef result of deserialization */
   private AeBaseXmlDef mRootDef;
   /** preserves comments for bpel and extension element objects */
   private AeCommentIO mCommentIO;
   /** determines which DOM elements should be traversed */
   private IAeTraversalFilter mTraversalFilter;
   
   /**
    * Constructor.
    * @param aReg the bpel element registry
    */
   public AeDomTraverser(IAeDefRegistry aReg)
   {
      mRegistry = aReg;
      mCommentIO = new AeCommentIO();
   }
   
   /**
    * Starts the traversal process which reads in the entire doc and
    * creates the corresponding AeBaseXmlDef object model.
    * 
    * @param aRootElement target root element (i.e. bpel process, ws-ht, bPEL4People, ...) element
    * @throws AeException
    */
   public void traverseRootElement(Element aRootElement) throws AeException
   {
      QName qName = extractQName(aRootElement);
      IAeDefReader reader = mRegistry.getReader(null, qName);
      if (reader != null)
      {
         AeBaseXmlDef rootDef = reader.read(null, aRootElement);
         preserveProcessComments(aRootElement, rootDef);
         traverseChildren(rootDef, aRootElement);
         mRootDef = rootDef; 
      }
      else
      {
         String QNameString = AeXmlUtil.encodeQName(qName, aRootElement, "ns1"); //$NON-NLS-1$
         throw new AeException("Registry does not contain a reader for BPEL element: " + QNameString); //$NON-NLS-1$
      }
   }
   
   /**
    * Starts the traversal process with a predefined root def which reads in the element and
    * creates the corresponding AeBaseXmlDef object model as children of the root def.
    * 
    * @param aRootDef the def to serve as the root for the deserialized aElement
    * @param aElement target root element (i.e. bpel process, ws-ht, bPEL4People, ...) element
    * @throws AeException
    */
   public void traverseElement(AeBaseXmlDef aRootDef,  Element aElement )
   throws AeException
   {
      mRootDef = aRootDef;
      preserveProcessComments(aElement, getRootDef());
      traverseDefElement(getRootDef(), aElement);
       
   }
   
   /**
    * Preserve the comments above the passed element on the def object.
    * 
    * @param aRootElement
    * @param aRootDef
    */
   protected void preserveProcessComments(Element aRootElement, AeBaseXmlDef aRootDef)
   {
      // process comments for process element.
      if (aRootElement.getParentNode() != null)
      {
         NodeList children = aRootElement.getParentNode().getChildNodes();
         for (int i = 0; i < children.getLength(); ++i)
         {
            Node node = children.item(i);
            if ( node.getNodeType() == Node.COMMENT_NODE )         
            {
               mCommentIO.appendToComments(node.getNodeValue());
            }
         }
      }
      
      // assign any comments found to the process definition
      mCommentIO.preserveComments(aRootDef);
   }

   /**
    * Determine is the current element is a Model element or an
    * extension element and pass it off to the corresponding reader.
    * If it is a Model element, traverse its children as well.
    * @param aParentDef
    * @param aElement
    * @throws AeException
    */
   protected void traverseDefElement(AeBaseXmlDef aParentDef, Element aElement) throws AeException
   {
      QName qName = extractQName( aElement );

      AeBaseXmlDef newDef = null;
      IAeDefReader reader = getRegistry().getReader( aParentDef, qName );
      // Read the def if the returned reader is not null.
      if (reader != null)
         newDef = reader.read( aParentDef, aElement );
      
      // If the def was null, there was an error reading it or no reader was found...so read it in as an
      // extension element.
      if (newDef == null)
      {
         newDef = getRegistry().getExtensionReader().read(aParentDef, aElement);
         mCommentIO.preserveComments(newDef);
      }
      else
      {
         mCommentIO.preserveComments(newDef);
         traverseChildren( newDef, aElement );
      }
   }
   
   /**
    * Interface determines if we should continue traversing the DOM. Some def
    * objects may want to preserve the DOM elements for later interpretation.
    * An example of this would be an element that contained a literal DOM for 
    * use at execution time.
    */
   public static interface IAeTraversalFilter
   {
      /**
       * Returns true if the children should be traversed.
       * @param aDef
       * @param aElement
       */
      public boolean shouldTraverseChildren(AeBaseXmlDef aDef, Element aElement);
   }
   
   /**
    * Traverses all DOM elments by default 
    */
   private static class AeTraverseAll implements IAeTraversalFilter 
   {
      /**
       * @see org.activebpel.rt.xml.def.io.readers.AeDomTraverser.IAeTraversalFilter#shouldTraverseChildren(org.activebpel.rt.xml.def.AeBaseXmlDef, org.w3c.dom.Element)
       */
      public boolean shouldTraverseChildren(AeBaseXmlDef aDef, Element aElement)
      {
         return true;
      }
   }
   
   /**
    * Propagates the recursive traversal.
    * @param aDef will serve as the parent def for any recursive reads
    * @param aElement read in the elements child elements
    * @throws AeException
    */
   protected void traverseChildren( AeBaseXmlDef aDef, Element aElement )
   throws AeException
   {
      // assign var def readers will handle children
      // because of potential literal values
      if (!getTraversalFilter().shouldTraverseChildren(aDef, aElement))
      {
         return;
      }
      
      // look for element or comment nodes
      // process child element nodes and 
      // store text from comment nodes
      NodeList children = aElement.getChildNodes();
      if (children != null)
      {
         for (int i = 0; i < children.getLength(); ++i)
         {
            Node node = children.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
               traverseDefElement(aDef, (Element)node);
            }
            else if ( node.getNodeType() == Node.COMMENT_NODE )         
            {
               mCommentIO.appendToComments(node.getNodeValue());
            }
         }
         mCommentIO.getAndClearLastComments();
      }
   }
   
   /**
    * Convenience method for extracting element QName.
    * @param aElement
    * @return element qname
    */
   protected QName extractQName(Element aElement)
   {
      return new QName(aElement.getNamespaceURI(),aElement.getLocalName());
   }

   /**
    * Accessor for AeProcessDef after deserialization.
    * @return AeProcessDef object model
    */
   public AeBaseXmlDef getRootDef()
   {
      return mRootDef;
   }
   
   /**
    * Accessor for bpel registry.
    * @return BPEL element registry
    */
   protected IAeDefRegistry getRegistry()
   {
      return mRegistry;
   }

   /**
    * @return the traversalFilter
    */
   public IAeTraversalFilter getTraversalFilter()
   {
      if (mTraversalFilter == null)
      {
         setTraversalFilter(new AeTraverseAll());
      }
      return mTraversalFilter;
   }

   /**
    * @param aTraversalFilter the traversalFilter to set
    */
   public void setTraversalFilter(IAeTraversalFilter aTraversalFilter)
   {
      mTraversalFilter = aTraversalFilter;
   }
}

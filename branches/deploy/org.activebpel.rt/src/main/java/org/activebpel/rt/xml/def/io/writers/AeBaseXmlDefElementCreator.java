//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/io/writers/AeBaseXmlDefElementCreator.java,v 1.1 2007/11/08 12:31:22 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def.io.writers; 

import java.util.Stack;

import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.io.IAeDefRegistry;
import org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Helper class for creating DOM elements from def objects using a registry to
 * create the writers and a traverser for traversing the model. 
 */
public class AeBaseXmlDefElementCreator
{
   /** document that creator is creating */
   private Document mDocument;

   /** result dom element */
   private Element mSerializedElement;

   /** stack for maintaining parent elements */
   private Stack mStack = new Stack();

   /** Def registry */
   private IAeDefRegistry mRegistry;
   
   /** visitor to use for traversing */
   private IAeBaseXmlDefVisitor mTraversalVisitor;

   /**
    * ctor
    * 
    * @param aRegistry
    * @param aTraversalVisitor
    */
   public AeBaseXmlDefElementCreator(IAeDefRegistry aRegistry, IAeBaseXmlDefVisitor aTraversalVisitor)
   {
      setRegistry(aRegistry);
      setTraversalVisitor(aTraversalVisitor);
   }

   /**
    * Push the current element onto the stack.
    * @param aElement
    */
   private void push(Element aElement)
   {
      mStack.push(aElement);
   }

   /**
    * Peek at the top level element.
    * @return the current top level element
    */
   private Element peek()
   {
      return mStack.isEmpty() ? null : (Element)mStack.peek();
   }

   /**
    * Pop the current element off the stack.
    */
   private void pop()
   {
      mStack.pop();
   }

   /**
    * Accessor for the serialized document.
    * 
    * @return the xml
    */
   public Document getDocument()
   {
      return mDocument;
   }

   /**
    * Accessor for the serialized element.
    * 
    * @return the dom element
    */
   public Element getElement()
   {
      return mSerializedElement;
   }

   /**
    * Creates an element for the def, adding it to the parent element passed in. The new element is then
    * pushed onto the stack and we traverse the def to write all of its children. We then write any extensions
    * for the def followed by a pop() and then done and done.
    * @param aDef
    */
   public void createElementAndTraverse(AeBaseXmlDef aDef)
   {
      Element parentElementFromStack = peek();
      Element element = createElement(aDef, parentElementFromStack);
      if ( mDocument == null )
      {
         mSerializedElement = element;
         mDocument = element.getOwnerDocument();
      }
      push(element);
      aDef.accept(getTraversalVisitor());
      pop();
   }

   /**
    * Creates the element using the writer and the current element on the stack as the parent element.
    * @param aBaseDef
    */
   protected Element createElement(AeBaseXmlDef aBaseDef)
   {
      return getWriter(aBaseDef).createElement(aBaseDef, peek());
   }

   /**
    * Creates the element using the writer
    * @param aBaseDef
    * @param aParentElement - if null, a new doc is created to contain the new element
    */
   protected Element createElement(AeBaseXmlDef aBaseDef, Element aParentElement)
   {
      return getWriter(aBaseDef).createElement(aBaseDef, aParentElement);
   }

   /**
    * Accessor for the IAeHtDefWriter.
    * @param aBaseDef
    * @return IAeHtDefWriter impl
    */
   protected IAeDefWriter getWriter(AeBaseXmlDef aBaseDef)
   {
      Class parentClass = null;

      if ( aBaseDef.getParentXmlDef() != null )
      {
         parentClass = aBaseDef.getClass();
      }

      return getRegistry().getWriter(parentClass, aBaseDef);
   }

   /**
    * @return Returns the registry.
    */
   protected IAeDefRegistry getRegistry()
   {
      return mRegistry;
   }

   /**
    * @param aRegistry the registry to set
    */
   protected void setRegistry(IAeDefRegistry aRegistry)
   {
      mRegistry = aRegistry;
   }

   /**
    * @return the traversalVisitor
    */
   public IAeBaseXmlDefVisitor getTraversalVisitor()
   {
      return mTraversalVisitor;
   }

   /**
    * @param aTraversalVisitor the traversalVisitor to set
    */
   public void setTraversalVisitor(IAeBaseXmlDefVisitor aTraversalVisitor)
   {
      mTraversalVisitor = aTraversalVisitor;
   }
}
 
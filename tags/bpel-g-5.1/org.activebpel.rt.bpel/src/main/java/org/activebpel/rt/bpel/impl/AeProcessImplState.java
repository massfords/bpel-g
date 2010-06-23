// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeProcessImplState.java,v 1.19 2005/11/23 21:34:51 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.impl.fastdom.AeFastElement;
import org.activebpel.rt.bpel.impl.fastdom.AeFastText;
import org.activebpel.rt.bpel.impl.fastdom.AeForeignNode;
import org.activebpel.rt.bpel.impl.fastdom.IAeFastParent;
import org.activebpel.rt.bpel.impl.storage.AeProcessImplStateAttributeCounts;
import org.activebpel.rt.bpel.impl.storage.AeProcessImplStateAttributeDefaults;
import org.activebpel.rt.bpel.impl.visitors.AeSaveImplStateVisitor;
import org.w3c.dom.Node;

/**
 * State saving object for Business Process implementation.
 */
public class AeProcessImplState implements IAeImplStateNames
{
   /** Root impl scope for traversal. */
   private AeBusinessProcess mProcess ;

   /** The document we'll be creating. */
   private AeFastDocument mFastDocument ;

   /** The current Node being populated. */
   private AeFastElement mCurrentNode ;

   /** Stack to keep track of current node parent. */
   private final LinkedList mParentStack = new LinkedList();

   /** Visitor that traverses the implementation and populates this object. */
   private AeSaveImplStateVisitor mStateVisitor ;

   /** <code>true</code> if and only if serializing for persistence. */
   private boolean mForPersistence;

   /** <code>Map</code> from nodes in the state tree to associated location paths. */
   private final Map mNodesToLocationPathsMap = new HashMap();

   /**
    * Get the visitor used to record the current state.
    *
    * @return AeSaveImplStateVisitor
    */
   protected AeSaveImplStateVisitor getStateVisitor()
   {
      if ( mStateVisitor == null )
         setStateVisitor( new AeSaveImplStateVisitor( this ));

      mStateVisitor.setForPersistence( isForPersistence() );

      return mStateVisitor ;
   }

   /**
    * Set the visitor used to record the current state.
    *
    * @param aVisitor The visitor to set.
    */
   protected void setStateVisitor( AeSaveImplStateVisitor aVisitor )
   {
      mStateVisitor = aVisitor ;
   }

   /**
    * Save the current state of the process.
    *
    * @throws AeBusinessProcessException
    */
   public AeFastDocument getProcessDocument() throws AeBusinessProcessException
   {
      setCurrentParent( getFastDocument() );

      getProcess().accept( getStateVisitor() );

      if (getParentStack().size() != 1)
      {
         throw new RuntimeException(AeMessages.getString("AeProcessImplState.ERROR_0")); //$NON-NLS-1$
      }

      return getFastDocument();
   }

   /**
    * Append the specified attribute to the current node.
    *
    * @param aName
    * @param aValue
    */
   public void appendAttribute( String aName, String aValue )
   {
      // Track attribute value counts (when enabled in AeProcessImplStateAttributeCounts).
      AeProcessImplStateAttributeCounts.getCounts().incrementCount(aName, aValue);

      if (isForPersistence() && isDefaultAttributeValue(aName, aValue))
      {
         // Don't persist this attribute if it's the default value. This
         // minimizes the size of the document for persistence, because the
         // deserializer will assume that missing attributes have their default
         // values.
      }
      else
      {
         getCurrentNode().setAttribute(aName, aValue);
      }
   }

   /**
    * Append the specified attributes to the current node.
    *
    * @param aAttributes A string array containing name:value attribute pairs.
    */
   public void appendAttributes( String[] aAttributes )
   {
      // We're iterating over name:value pairs, so we go by 2 here.
      for ( int i = 0 ; i < aAttributes.length ; i+=2 )
      {
         String name = aAttributes[i];
         String value;

         try
         {
            value = aAttributes[i+1];
         }
         catch ( ArrayIndexOutOfBoundsException ae )
         {
            // TODO - exception here??? i+1 failed...
            value = "--- ATTRIBUTE VALUE IS MISSING!!!! ---"; //$NON-NLS-1$
         }

         appendAttribute(name, value);
      }
   }

   /**
    * Appends an element to the current parent.
    *
    * @param aElement
    */
   public void appendElement(AeFastElement aElement)
   {
      setCurrentNode(aElement);
      getCurrentParent().appendChild(aElement);
   }

   /**
    * Creates an element, makes it the current node and appends it to the
    * current parent node.
    *
    * @param aElementName The name of the new element.
    */
   public void appendElement( String aElementName )
   {
      appendElement(new AeFastElement(aElementName));
   }

   /**
    * Appends an element to the current node with the specified attributes.
    *
    * @param aName The element name.
    * @param aAttributes A string array containing name:value attribute pairs.
    */
   public void appendElement( String aName, String[] aAttributes )
   {
      appendElement( aName );
      appendAttributes( aAttributes );
   }

   /**
    * Appends a node from another <code>Document</code> to the current parent.
    *
    * @param aNode
    */
   public void appendForeignNode(Node aNode)
   {
      getCurrentParent().appendChild(new AeForeignNode(aNode));
   }

   /**
    * Appends an attribute for the given location path to the current node. If
    * serializing for persistence, then trims the leading portion of the
    * location if that portion matches the location path associated with the
    * current parent node.
    *
    * @param aLocationPath
    */
   public void appendLocationPathAttribute(String aLocationPath)
   {
      if (isForPersistence())
      {
         getNodesToLocationPathsMap().put(getCurrentNode(), aLocationPath);

         IAeFastParent parent = getCurrentParent();
         String parentPath = (String) getNodesToLocationPathsMap().get(parent);

         if ((parentPath != null) && aLocationPath.startsWith(parentPath + "/")) //$NON-NLS-1$
         {
            aLocationPath = aLocationPath.substring(parentPath.length() + 1);
         }
      }

      appendAttribute(STATE_LOC, aLocationPath);
   }

   /**
    * Appends a text node to the current parent.
    *
    * @param aText String that comprises the text node's data.
    */
   public void appendTextNode( String aText )
   {
      getCurrentParent().appendChild(new AeFastText(aText));
   }

   /**
    * Returns <code>true</code> if and only if the specified value is the
    * default value for the specified attribute.
    *
    * @param aAttributeName
    * @param aValue
    */
   protected boolean isDefaultAttributeValue(String aAttributeName, String aValue)
   {
      String aDefaultValue = AeProcessImplStateAttributeDefaults.getDefaults().get(aAttributeName);
      return (aValue != null) && aValue.equals(aDefaultValue);
   }

   // accessor/mutators...

   /**
    * Set the root of the implementation to save.
    *
    * @param aProc Root scope (e.g., AeBusinessProcess) of the implementation to be
    * output.
    */
   public void setProcess( AeBusinessProcess aProc )
   {
      mProcess = aProc ;
   }

   /**
    * Get the current document node.
    *
    * @return Element
    */
   protected AeFastElement getCurrentNode()
   {
      return mCurrentNode;
   }

   /**
    * Get the current parent node.
    *
    * @return Parent
    */
   protected IAeFastParent getCurrentParent()
   {
      return (IAeFastParent) mParentStack.getFirst();
   }

   /**
    * Get the current document.
    *
    * @return AeFastDocument
    */
   protected AeFastDocument getFastDocument()
   {
      if (mFastDocument == null)
      {
         setFastDocument(new AeFastDocument());
      }

      return mFastDocument;
   }

   /**
    * Returns <code>Map</code> from nodes in the state tree to associated
    * location paths. This is used only if serializing for persistence.
    */
   protected Map getNodesToLocationPathsMap()
   {
      return mNodesToLocationPathsMap;
   }

   /**
    * Get the root scope (e.g., AeBusinessProcess, etc.), set by the client.
    *
    * @return AeBusinessProcess
    */
   public AeBusinessProcess getProcess() throws AeBusinessProcessException
   {
      if ( mProcess == null )
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeProcessImplState.ERROR_2")); //$NON-NLS-1$
      }

      return mProcess;
   }

   /**
    * Set the current document node.
    *
    * @param aNode The node to set.
    */
   protected void setCurrentNode(AeFastElement aNode)
   {
      mCurrentNode = aNode;
   }

   /**
    * Get the parent stack object.
    *
    * @return LinkedList
    */
   protected LinkedList getParentStack()
   {
      return mParentStack;
   }

   /**
    * Returns <code>true</code> if and only if serializing for persistence.
    */
   public boolean isForPersistence()
   {
      return mForPersistence;
   }

   /**
    * Reinstate the previously stored parent from the top of the stack.
    */
   public void popParent()
   {
      if (getParentStack().isEmpty())
      {
         throw new RuntimeException("Unmatched popParent()!"); //$NON-NLS-1$
      }

      IAeFastParent parent = (IAeFastParent) getParentStack().removeFirst();

      if (!(parent instanceof AeFastElement))
      {
         throw new RuntimeException("Mismatched popParent()!"); //$NON-NLS-1$
      }

      setCurrentNode( (AeFastElement) parent );
   }

   /**
    * Make the current node the parent and push the current parent up on
    * the parent stack.
    */
   public void pushParent()
   {
      setCurrentParent( getCurrentNode() );
   }

   /**
    * Set a new current parent node.
    *
    * @param aNode The node to set.
    */
   protected void setCurrentParent(IAeFastParent aNode)
   {
      if ( aNode != null )
         getParentStack().addFirst( aNode );
   }

   /**
    * Set the document we're creating.
    *
    * @param aDocument The document to set.
    */
   protected void setFastDocument(AeFastDocument aDocument)
   {
      mFastDocument = aDocument;
   }

   /**
    * Sets the flag that specifies whether we are serializing for persistence.
    *
    * @param aForPersistence <code>true</code> to serialize for persistence.
    */
   public void setForPersistence(boolean aForPersistence)
   {
      mForPersistence = aForPersistence;
   }
}

// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/AeBaseXmlDef.java,v 1.20.4.3 2008/04/21 16:05:13 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.util.AeCloneUtil;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Base definition object for xml definitions
 */
public abstract class AeBaseXmlDef implements Cloneable
{
   /** A map of all referenced namespaces referenced at the current node level */
   protected Map mNamespaceMap;
   /** Default namespace or null if not set */
   private String mDefaultNamespace;
   /** Path that uniquely identifies this element in the model hierarchy. */
   private String mLocationPath ;
   /** Unique location identifier for this element in the model hierarchy. */
   private int mLocationId ;
   /** Optional comment from the xml source */
   private String mComment;
   /** List of documentation children of any construct. */
   private List mDocumentationDefs;
   /** Parent def, if any. */
   private AeBaseXmlDef mParent;
   /** A list of extension elements */
   private List mExtensionElementDefs;
   /** The extension attributes for the element. */
   private List mExtensionAttributeDefs;

   /**
    * Default constructor
    */
   public AeBaseXmlDef()
   {
      super();
   }

   /**
    * Set this instance's parent def.
    *
    * @param aParent The parent to set.
    */
   public void setParentXmlDef( AeBaseXmlDef aParent )
   {
      mParent = aParent ;
   }

   /**
    * <p>Get this instance's parent def.</p>
    * 
    * <p>
    * N.B.: the AeDefAssignParentVisitor must be used before assuming that
    * parent-child def relationships are valid.  These relationships are NOT
    * updated automatically (e.g., during modification by the process design user,
    * etc.).
    * </p>
    * @return AeBaseDef if parent exists, null otherwise.
    */
   public AeBaseXmlDef getParentXmlDef()
   {
      return mParent ;
   }

   /**
    * Returns the default namespace for this def, or null if one is not
    * declared.
    */
   public String getDefaultNamespace()
   {
      return mDefaultNamespace;
   }

   /**
    * Sets the default namespace declaration for this def.
    *
    * @param aNamespace
    */
   public void setDefaultNamespace(String aNamespace)
   {
      mDefaultNamespace = aNamespace;
   }

   /**
    * Removes the default namespace declaration, if any.
    */
   public void removeDefaultNamespace()
   {
      mDefaultNamespace = null;
   }

   /**
    * @return Returns the namespace map.
    */
   public Map getNamespaceMap()
   {
      return getNamespaceMap(false);
   }

   /**
    * Returns the namespacemap. If a map has not been created and
    * <code>aCreate=true</code> then a map is created, otherwise returns
    * <code>Collections.EMPTY_MAP</code>.
    * @param aCreate
    */
   protected Map getNamespaceMap(boolean aCreate)
   {
      if (mNamespaceMap != null)
      {
         return mNamespaceMap;
      }
      else if (mNamespaceMap == null && aCreate)
      {
         mNamespaceMap = new HashMap();
         return mNamespaceMap;
      }
      else
      {
         return Collections.EMPTY_MAP;
      }
   }

   /**
    * Provides the ability to add a namespace to the list.
    *
    * @param aPrefix  name of the namespace to be added (e.g. bpws) If null or
    *                 empty then this sets the default namespace for the def.
    * @param aNamespace value of the namespace to be added
    */
   public void addNamespace(String aPrefix, String aNamespace)
   {
      if ("xmlns".equals(aPrefix) || AeUtil.isNullOrEmpty(aPrefix)) //$NON-NLS-1$
      {
         setDefaultNamespace(aNamespace);
      }
      else
      {
         getNamespaceMap(true).put(aPrefix, aNamespace);
      }
   }

   /**
    * Provides the ability to add all namespaces in scope to the namespacemap
    */
   public void addNamespacesInScope()
   {
     addNamespacesInScope(getParentXmlDef());
   }

   /**
    * Provides the ability to add all namespaces in scope to the namespacemap
    * @param def
    */
   public void addNamespacesInScope(AeBaseXmlDef def)
   {
      if (def != null)
      {
        for (Iterator iterator = def.getNamespaceMap().entrySet().iterator(); iterator.hasNext();)
        {
           Map.Entry entry = (Map.Entry)iterator.next();
           if ( !(getNamespaceMap(true).containsKey(entry.getKey())) )
           {
             getNamespaceMap(true).put(entry.getKey(), entry.getValue());
           }
        }
        addNamespacesInScope(def.getParentXmlDef());
      }
   }
   
   /**
    * Adds all of the namespace declarations to the def
    * @param aNamespaceMap
    */
   public void addNamespaces(Map aNamespaceMap)
   {
      getNamespaceMap(true).putAll(aNamespaceMap);
   }
   
   /**
    * Converts a string to a QName
    * @param aEncodedQName
    */
   public QName toQName(String aEncodedQName)
   {
      String prefix = AeXmlUtil.extractPrefix(aEncodedQName);
      String localPart = AeXmlUtil.extractLocalPart(aEncodedQName);
      return new QName(findNamespace(prefix), localPart);
   }

   /**
    * Provides the ability to find a namespace given a prefix.
    *
    * @param aPrefix the prefix to search for.
    * @return the value of the namespace if found or null if not found
    */
   public String getNamespace(String aPrefix)
   {
      return (String) getNamespaceMap().get(aPrefix);
   }

   /**
    * Walks up the parent hierarchy to resolve the prefix to a namespace
    * @param aPrefix
    */
   public String findNamespace(String aPrefix)
   {
      String ns = null;
      AeBaseXmlDef def = this;
      while (def != null && (ns = def.getNamespace(aPrefix)) == null)
      {
         def = def.getParentXmlDef();
      }
      return ns;
   }

   /**
    * Returns a list of the namespace prefixes. This will not include a mapping
    * for the default namespace.
    */
   public Set getNamespacePrefixList()
   {
      if (getNamespaceMap().isEmpty())
         return Collections.EMPTY_SET;

      Set prefixes = new HashSet(getNamespaceMap().keySet());
      // remove the default ns
      prefixes.remove(""); //$NON-NLS-1$
      return prefixes;
   }

   /**
    * Gets a set of in-scope prefixes mapped to the given namespace.
    *
    * @param aNamespace
    */
   public Set findPrefixesForNamespace(String aNamespace)
   {
      if ( !AeUtil.isNullOrEmpty(aNamespace) )
      {
         HashSet set = new HashSet();
         AeBaseXmlDef def = this;

         // We need to process the defs top-down, so put them all into a stack first.
         Stack defStack = new Stack();
         while (def != null)
         {
            defStack.push(def);
            def = def.getParentXmlDef();
         }

         // Now go through the stack and process each def.
         while (!defStack.isEmpty())
         {
            def = (AeBaseXmlDef) defStack.pop();
            getPrefixesForNamespace(def, aNamespace, set);
         }

         return set;
      }
      else
      {
         return Collections.EMPTY_SET;
      }
   }

   /**
    * Finds an in-scope prefix for the given namespace.
    *
    * @param aNamespace
    */
   public String findPrefixForNamespace(String aNamespace)
   {
      Set prefixes = findPrefixesForNamespace(aNamespace);
      if (AeUtil.notNullOrEmpty(prefixes))
         return (String) prefixes.iterator().next();
      return null;
   }

   /**
    * Finds the prefixes for the given namespace URI in the given def.
    *
    * @param aDef
    * @param aNamespace
    * @param aResultSet
    */
   private void getPrefixesForNamespace(AeBaseXmlDef aDef, String aNamespace, Set aResultSet)
   {
      for (Iterator iter = aDef.getNamespaceMap().entrySet().iterator(); iter.hasNext(); )
      {
         Map.Entry entry = (Map.Entry) iter.next();
         String prefix = (String) entry.getKey();
         String ns = (String) entry.getValue();
         // Either add the prefix to the set (if it matches the NS) or
         // remove the prefix (since it doesn't match).  The code works
         // this way because this method is called from "findPrefixesForNamespace"
         // which walks DOWN the def tree gathering up prefixes.  The else
         // clause here exists for the case of shadowed prefix declarations.
         // In other words, if a prefix is redeclared at a lower level, and 
         // the namespace it is bound to is NOT the namespace we are looking 
         // for, then we need to remove it from the collection (because it
         // may have been bound to a matching namespace higher in the def
         // tree, and therefore it would be in the Set).
         if (aNamespace.equals(ns) && AeUtil.notNullOrEmpty(prefix))
         {
            aResultSet.add(prefix);
         }
         else if (AeUtil.notNullOrEmpty(prefix))
         {
            aResultSet.remove(prefix);
         }
      }
   }

   /**
    * Get the unique location path for this element.
    *
    * @return String
    */
   public String getLocationPath()
   {
      return mLocationPath;
   }

   /**
    * Set the unique location path for this element.
    *
    * @param aString
    */
   public final void setLocationPath(String aString)
   {
      mLocationPath = aString;
   }

   /**
    * Get the unique location id for this element.
    *
    * @return int
    */
   public int getLocationId()
   {
      return mLocationId;
   }

   /**
    * Set the unique location id for this element.
    *
    * @param aLocationId
    */
   public void setLocationId(int aLocationId)
   {
      mLocationId = aLocationId;
   }

   /**
    * Gets the BPEL comment.
    */
   public String getComment()
   {
      return mComment;
   }

   /**
    * Sets the comment.
    * @param aString
    */
   public void setComment(String aString)
   {
      mComment = aString;
   }

   /**
    * Mutator for adding an extension element.
    * @param aExtension the IAeExtensionElement impl to be added
    */
   public void addExtensionElementDef( AeExtensionElementDef aExtension )
   {
      if( mExtensionElementDefs == null )
         mExtensionElementDefs = new ArrayList();
      mExtensionElementDefs.add( aExtension );
      assignParent(aExtension);
   }

   /**
    * Mutator for removing an extension element.
    * @param aExtension the IAeExtensionElement impl to be added
    */
   public void removeExtensionElementDef( AeExtensionElementDef aExtension )
   {
      if (mExtensionElementDefs != null)
         mExtensionElementDefs.remove(aExtension);
   }

   /**
    * Creates a new extension element def with the given QName and
    * adds it to the collection of extension element defs.
    *
    * @param aQName
    */
   public AeExtensionElementDef createExtensionElementDef(QName aQName)
   {
      return createExtensionElementDef(aQName, "ns"); //$NON-NLS-1$
   }

   /**
    * Creates a new extension element def with the given QName and
    * adds it to the collection of extension element defs.
    *
    * @param aQName
    * @param aPreferredPrefix
    */
   public AeExtensionElementDef createExtensionElementDef(QName aQName, String aPreferredPrefix)
   {
      Document doc = AeXmlUtil.newDocument();
      Element element;
      if (AeUtil.notNullOrEmpty(aQName.getNamespaceURI()))
      {
         Set prefixes = findPrefixesForNamespace(aQName.getNamespaceURI());
         String prefix = aPreferredPrefix;
         if (AeUtil.notNullOrEmpty(prefixes))
            prefix = (String) prefixes.iterator().next();
         element = doc.createElementNS(aQName.getNamespaceURI(), prefix + ":" + aQName.getLocalPart()); //$NON-NLS-1$
         element.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns:" + prefix, aQName.getNamespaceURI()); //$NON-NLS-1$
      }
      else
      {
         element = doc.createElementNS("", aQName.getLocalPart()); //$NON-NLS-1$
      }
      doc.appendChild(element);
      AeExtensionElementDef extElemDef = new AeExtensionElementDef(element);

      addExtensionElementDef(extElemDef);
      return extElemDef;
   }

   /**
    * Returns an iterator for any extension elements
    * (or any empty iterator if none are present).
    * @return iterator over any extension element
    */
   public List getExtensionElementDefs()
   {
      if( mExtensionElementDefs == null )
         return Collections.EMPTY_LIST;
      return mExtensionElementDefs;
   }

   /**
    * Convenience method to get the first extension element def or null if there
    * are no extensions.
    */
   public AeExtensionElementDef getFirstExtensionElementDef()
   {
      Iterator iter = getExtensionElementDefs().iterator();
      if (iter.hasNext())
      {
         return (AeExtensionElementDef) iter.next();
      }
      return null;
   }

   /**
    * Gets a single extension element by QName.  Returns null if no extension
    * element with the given QName is found.  If multiple extension elements with
    * the given QName exist, only the first one is returned.
    *
    * @param aElementQName
    */
   public AeExtensionElementDef getExtensionElementDef(QName aElementQName)
   {
      for (Iterator iter = getExtensionElementDefs().iterator(); iter.hasNext(); )
      {
         AeExtensionElementDef eeDef = (AeExtensionElementDef) iter.next();
         if (AeUtil.compareObjects(eeDef.getElementQName(), aElementQName))
         {
            return eeDef;
         }
      }
      return null;
   }

   /**
    * Convenience method to get all extension elements
    * @param aElementQName
    */
   public List getAllExtensionElementDef(QName aElementQName)
   {
      List list = new ArrayList();
      for (Iterator iter = getExtensionElementDefs().iterator(); iter.hasNext(); )
      {
         AeExtensionElementDef eeDef = (AeExtensionElementDef) iter.next();
         if (AeUtil.compareObjects(eeDef.getElementQName(), aElementQName))
         {
            list.add(eeDef);
         }
      }
      return list;
   }

   /**
    * Gets a single extension attribute by QName.  Returns null if no extension
    * attribute with the given QName is found.  If multiple extension attributes with
    * the given QName exist, only the first one is returned.
    * @param aAttributeQName
    */
   public AeExtensionAttributeDef getExtensionAttributeDef(QName aAttributeQName)
   {
      for (Iterator iter = getExtensionAttributeDefs().iterator(); iter.hasNext(); )
      {
         AeExtensionAttributeDef attrDef = (AeExtensionAttributeDef) iter.next();
         if (AeUtil.compareObjects(attrDef.getQName(), aAttributeQName))
         {
            return attrDef;
         }
      }
      return null;
   }

   /**
    * @return Returns the documentation defs.
    */
   public List getDocumentationDefs()
   {
      if (mDocumentationDefs == null)
         return Collections.EMPTY_LIST;
      return mDocumentationDefs;
   }

   /**
    * @param aDocumentationDef The documentation def to add.
    */
   public void addDocumentationDef(AeDocumentationDef aDocumentationDef)
   {
      if (mDocumentationDefs == null)
         mDocumentationDefs = new ArrayList();
      mDocumentationDefs.add(aDocumentationDef);
   }

   /**
    * Adds an extension attribute to the def.
    *
    * @param aDef
    */
   public void addExtensionAttributeDef(AeExtensionAttributeDef aDef)
   {
      if (mExtensionAttributeDefs == null)
         mExtensionAttributeDefs = new LinkedList();
      mExtensionAttributeDefs.add(aDef);
   }

   /**
    * @return Returns the extensionAttributes.
    */
   public List getExtensionAttributeDefs()
   {
      if( mExtensionAttributeDefs == null )
         return Collections.EMPTY_LIST;
      return mExtensionAttributeDefs;
   }

   /**
    * Walks all of the attributes looking for an attribute that can produce the
    * given adapter.
    * @param aClass
    */
   public IAeAdapter getAdapterFromAttributes(Class aClass)
   {
      for(Iterator it=getExtensionAttributeDefs().iterator(); it.hasNext();)
      {
         AeExtensionAttributeDef attribDef = (AeExtensionAttributeDef) it.next();
         if (attribDef.getExtensionObject() != null)
         {
            IAeExtensionObject extObject = attribDef.getExtensionObject();
            IAeAdapter adapter = extObject.getAdapter(aClass);
            if (adapter != null)
            {
               return adapter;
            }
         }
      }
      return null;
   }

   /**
    * Abstract method for visitor pattern
    * @param aVisitor
    */
   public abstract void accept(IAeBaseXmlDefVisitor aVisitor);

   /**
    * Note: The parent reference is explicitly not set and top level clone operation is
    *       expected to run a visitor to set the parentage.
    * @see java.lang.Object#clone()
    */
   public Object clone()
   {
      try
      {
         AeBaseXmlDef baseDef = (AeBaseXmlDef)super.clone();
         baseDef.setParentXmlDef(null);

         baseDef.mDocumentationDefs = AeCloneUtil.deepClone(mDocumentationDefs);
         baseDef.mExtensionElementDefs = AeCloneUtil.deepClone(mExtensionElementDefs);
         baseDef.mExtensionAttributeDefs = AeCloneUtil.deepClone(mExtensionAttributeDefs);
         baseDef.mNamespaceMap = AeCloneUtil.deepClone(mNamespaceMap);

         return baseDef;
      }
      catch (CloneNotSupportedException e)
      {
         AeException.logError(e);
         return null;
      }
   }

   /**
    * We want to have a comparison operator, but do not want to override the equals for this base
    * since BPEL base defs also inherit from us.
    * @param aOther
    */
   public boolean compare(Object aOther)
   {
      if (! (aOther instanceof AeBaseXmlDef))
         return false;

      AeBaseXmlDef otherDef = (AeBaseXmlDef)aOther;
      boolean same = AeUtil.compareObjects(otherDef.getDocumentationDefs(), getDocumentationDefs());
      same &= AeUtil.compareObjects(otherDef.getExtensionElementDefs(), getExtensionElementDefs());
      same &= AeUtil.compareObjects(otherDef.getExtensionAttributeDefs(), getExtensionAttributeDefs());
      same &= AeUtil.compareObjects(otherDef.getComment(), getComment());
      same &= (otherDef.getLocationId() == getLocationId());

      return same;
   }

   /**
    * We want to have an implementation of the hash code method, but do not want to override
    * hashCode from base class since BPEL defs also inherit from us.
    */
   public int getHashCode()
   {
      // not using the location id here since there are some cases where the id
      // might not be unique. for example, b4p defs within a bpel process.
      return AeUtil.getSafeString(getLocationPath()).hashCode();
   }

   /**
    * Returns true if the def understands the extension element.
    * @param aAeExtensionElementDef
    */
   public boolean isExtensionUnderstood(AeExtensionElementDef aAeExtensionElementDef)
   {
      return false;
   }

   /**
    * Returns true if the def understands the extension attribute.
    * @param aExtensionAttributeDef
    */
   public boolean isExtensionUnderstood(AeExtensionAttributeDef aExtensionAttributeDef)
   {
      return false;
   }
   
   /**
    * Sets def as the parent of the passed def
    * @param aChildDef
    */
   protected void assignParent(AeBaseXmlDef aChildDef)
   {
      if (aChildDef != null)
         aChildDef.setParentXmlDef(this);
   }
   
   /**
    * Walks the list and assigns this def as the parent to each of the defs
    * in the list.
    * @param aList
    */
   protected void assignParent(List aList)
   {
      if (AeUtil.notNullOrEmpty(aList))
      {
         for (Iterator it = aList.iterator(); it.hasNext();)
         {
            AeBaseXmlDef def = (AeBaseXmlDef) it.next();
            assignParent(def);
         }
      }
   }
}
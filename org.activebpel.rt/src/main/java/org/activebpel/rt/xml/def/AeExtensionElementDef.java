// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/AeExtensionElementDef.java,v 1.11 2008/02/29 23:40:23 vvelusamy Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def;


import javax.xml.namespace.QName;

import org.activebpel.rt.util.AeCompareXML;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Impl of the IAeExtensionElementDef interface.
 * <br />
 * Simply wraps the extension element.
 */
public class AeExtensionElementDef extends AeBaseXmlDef implements IAeExtensionObjectParentDef
{
   /** the actual dom element */
   private Element mExtensionElement;
   /** any accumulated comments */
   private String mComments;
   
   /** Extension element object */
   private IAeExtensionObject mExtensionObject;
   
   /**
    * Default c'tor.
    */
   public AeExtensionElementDef(Element aElement)
   {
      this(aElement, true);
   }
   
   /**
    * Constructors the def with option to specify whether the {@link Element} stored should contain all namespace
    * declarations gathered from the entire {@link Document} or not.
    */
   public AeExtensionElementDef(Element aElement, boolean aGatherAllNamespaceDeclarations)
   {
      super();

      Element clone = null;
      if (aGatherAllNamespaceDeclarations)
      {
         // This clone will gather all namespaces declared in the entire Document.
         clone = AeXmlUtil.cloneElement(aElement);
      }
      else
      {
         // Just make a deep copy of this Element.
         clone = (Element) aElement.cloneNode(true);
      }
      setExtensionElement(clone);
   }

   /**
    * Mutator for setting extension element.
    * @param aElement extension element
    */
   protected void setExtensionElement( Element aElement )
   {
      mExtensionElement = aElement;
   }
   
   /**
    * Accessor for extension element.
    * @return actual dom element
    */
   public Element getExtensionElement()
   {
      return mExtensionElement;
   }
   
   /**
    * @param aComments
    */
   public void setComments( String aComments )
   {
      mComments = aComments;
   }
   
   /**
    * Accessor for comment string.
    * @return any comment or null if none have been set
    */
   public String getComments()
   {
      return mComments;
   }

   /**
    * Gets the element qname
    */
   public QName getElementQName()
   {
      return AeXmlUtil.getElementType(getExtensionElement());
   }

   /**
    * @see org.activebpel.rt.xml.def.AeBaseXmlDef#accept(org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void accept(IAeBaseXmlDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }

   /**
    * @see org.activebpel.rt.xml.def.IAeExtensionObjectParentDef#getExtensionObject()
    */
   public IAeExtensionObject getExtensionObject()
   {
      return mExtensionObject;
   }

   /**
    * @see org.activebpel.rt.xml.def.IAeExtensionObjectParentDef#setExtensionObject(org.activebpel.rt.xml.def.IAeExtensionObject)
    */
   public void setExtensionObject(IAeExtensionObject aExtensionObject)
   {
      mExtensionObject = aExtensionObject;
      AeXmlDefUtil.installDef(mExtensionObject, this);
   }
   
   /**
    * 
    * @return true if extension object is set else returns false
    */
   public boolean isUnderstood()
   {
      return (getExtensionObject() != null) || (getParentXmlDef() != null && getParentXmlDef().isExtensionUnderstood(this));
   }
   
   /**
    * @see java.lang.Object#clone()
    */
   public Object clone()
   {
      AeExtensionElementDef def = (AeExtensionElementDef)super.clone();
      if (getExtensionElement() != null)
         def.setExtensionElement(AeXmlUtil.cloneElement(getExtensionElement()));

      return def;
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object aOther)
   {
      if (!(aOther instanceof AeExtensionElementDef))
         return false;

      AeExtensionElementDef otherDef = (AeExtensionElementDef)aOther;
      boolean same = compare(aOther);  
      
      if (getExtensionElement() != null && otherDef.getExtensionElement() != null)
         same &= AeCompareXML.compare(getExtensionElement(), otherDef.getExtensionElement());
      else if (getExtensionElement() == null || otherDef.getExtensionElement() == null)
      {
         // It's ok for both to be null but not just one
         if (getExtensionElement() != otherDef.getExtensionElement())
            same = false;
      }

      return same;
   }

   /**
     * @see java.lang.Object#hashCode()
     */
    public final int hashCode()
    {
       return getHashCode();
    }
}
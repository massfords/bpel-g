// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/AeExtensionAttributeDef.java,v 1.9 2008/02/12 00:04:46 rnaylor Exp $
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

import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor;


/**
 * A simple class that holds the information about an extension attribute 
 * (namespace, qualified name, value).
 */
public class AeExtensionAttributeDef extends AeBaseXmlDef implements IAeExtensionObjectParentDef
{
   /** The attribute's namespace. */
   private String mNamespace;
   /** The attribute's qualified name. */
   private String mQualifiedName;
   /** The attribute value. */
   private String mValue;

   /** Extension element object */
   private IAeExtensionObject mExtensionObject;

   /**
    * Constructor.
    * 
    * @param aNamespace
    * @param aQualifiedName
    * @param aValue
    */
   public AeExtensionAttributeDef(String aNamespace, String aQualifiedName, String aValue)
   {
      mNamespace = aNamespace;
      mQualifiedName = aQualifiedName;
      mValue = aValue;
   }

   /**
    * @return Returns the namespace.
    */
   public String getNamespace()
   {
      return mNamespace;
   }

   /**
    * @return Returns the qualifiedName.
    */
   public String getQualifiedName()
   {
      return mQualifiedName;
   }

   /**
    * @return Returns the value.
    */
   public String getValue()
   {
      return mValue;
   }

   /**
    * @see org.activebpel.rt.xml.def.AeBaseXmlDef#accept(IAeBaseXmlDefVisitor)
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
    * @return true if extension object is set else returns false
    */
   public boolean isUnderstood()
   {
      return (getExtensionObject() != null);
   }
   
   /**
    * Returns QName of attribute def
    */
   public QName getQName()
   {
      return new QName(getNamespace(), AeXmlUtil.extractLocalPart(getQualifiedName()));
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object aOther)
   {
      if (! (aOther instanceof AeExtensionAttributeDef))
         return false;
      
      AeExtensionAttributeDef otherDef = (AeExtensionAttributeDef)aOther;
      boolean same = compare(aOther);  
      same &= AeUtil.compareObjects(otherDef.getNamespace(), getNamespace()); 
      same &= AeUtil.compareObjects(otherDef.getQName(), getQName()); 
      same &= AeUtil.compareObjects(otherDef.getValue(), getValue()); 
      
      return same; 
}
   /**
     * @see java.lang.Object#hashCode()
     */
    public final int hashCode()
    {
       return getQualifiedName().hashCode();
    }
}
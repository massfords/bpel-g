// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/AeDocumentationDef.java,v 1.4 2007/11/13 16:55:23 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xml.def;

import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor;

/**
 * A def implementation of the generic documentation constructs
 */
public class AeDocumentationDef extends AeBaseXmlDef
{
   /** The source uri attribute if specified. */
   private String mSource;
   /** The language attribute if specified. */
   private String mLanguage;
   /** The value of the documentation element. */
   private String mValue;

   /**
    * Default c'tor.
    */
   public AeDocumentationDef()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.xml.def.AeBaseXmlDef#accept(IAeBaseXmlDefVisitor)
    */
   public void accept(IAeBaseXmlDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }

   /**
    * @return Returns the value.
    */
   public String getValue()
   {
      return mValue;
   }

   /**
    * @param aValue The value to set.
    */
   public void setValue(String aValue)
   {
      mValue = aValue;
   }

   /**
    * @return Returns the language.
    */
   public String getLanguage()
   {
      return mLanguage;
   }

   /**
    * @param aLanguage The language to set.
    */
   public void setLanguage(String aLanguage)
   {
      mLanguage = aLanguage;
   }

   /**
    * @return Returns the source.
    */
   public String getSource()
   {
      return mSource;
   }

   /**
    * @param aSource The source to set.
    */
   public void setSource(String aSource)
   {
      mSource = aSource;
   }
   
   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object aOther)
   {
      if (! (aOther instanceof AeDocumentationDef))
         return false;
      
      AeDocumentationDef otherDef = (AeDocumentationDef)aOther;
      boolean same = compare(aOther);  
      same &= AeUtil.compareObjects(otherDef.getSource(), getSource()); 
      same &= AeUtil.compareObjects(otherDef.getLanguage(), getLanguage()); 
      same &= AeUtil.compareObjects(otherDef.getValue(), getValue()); 
      
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
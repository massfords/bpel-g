//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeIntegerPropertySetterTag.java,v 1.1 2007/04/24 17:23:13 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.tags;

import javax.servlet.jsp.JspException;

import org.activebpel.rt.util.AeUtil;

/**
 * Tag that can use to set integer values in a bean.
 * This tag handles checking parameter string to make sure it is in a number
 * format before setting the parsed integer value.
 *
 */
public class AeIntegerPropertySetterTag extends AeAbstractFormPropertyTag
{
   /**
    * Indicates that a default value has been set.
    */
   private boolean mHasDefault;
   /** The default value if the html form param value is invalid. */   
   private int mDefault;
   /** Minium value. */
   private int mMin = Integer.MIN_VALUE;
   /** Maximum value. */
   private int mMax = Integer.MAX_VALUE;
   
   /**
    * Sets the integer value if present and is a valid integer string.
    * Otherwise, if given, a default value will be used.
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
   public int doStartTag() throws JspException
   {
      Integer intObject = null;
      if ( AeUtil.isNumber(getParamValue()) )
      {
         int n = getBoundedValue ( AeUtil.parseInt(getParamValue(),0) );
         intObject = new Integer(n);         
      }
      else if ( isHasDefault() )
      {
         intObject = new Integer( getBoundedValue(getDefault()) );
      }
      if (intObject != null)
      {
         setPropertyOnBean(intObject, int.class);
      }
      return SKIP_BODY;
   }

   /**
    * Returns the given number bounded by the min and max.
    * @param aNumber
    * @return bounded number
    */
   protected int getBoundedValue(int aNumber)
   {
      if (aNumber < getMin())
      {
         aNumber = getMin();
      } 
      else if (aNumber > getMax())
      {
         aNumber = getMax();
      }
      return aNumber;
   }

   /**
    * @return the default
    */
   public int getDefault()
   {
      return mDefault;
   }

   /**
    * @param aDefault the default to set
    */
   public void setDefault(int aDefault)
   {
      setHasDefault(true);
      mDefault = aDefault;
   }


   /**
    * @return the hasDefault
    */
   protected boolean isHasDefault()
   {
      return mHasDefault;
   }

   /**
    * @param aHasDefault the hasDefault to set
    */
   protected void setHasDefault(boolean aHasDefault)
   {
      mHasDefault = aHasDefault;
   }

   /**
    * @return the max
    */
   public int getMax()
   {
      return mMax;
   }

   /**
    * @param aMax the max to set
    */
   public void setMax(int aMax)
   {
      mMax = aMax;
   }

   /**
    * @return the min
    */
   public int getMin()
   {
      return mMin;
   }

   /**
    * @param aMin the min to set
    */
   public void setMin(int aMin)
   {
      mMin = aMin;
   }   
}

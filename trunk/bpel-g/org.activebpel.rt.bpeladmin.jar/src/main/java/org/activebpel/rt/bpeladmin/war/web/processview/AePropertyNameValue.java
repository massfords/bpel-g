//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/processview/AePropertyNameValue.java,v 1.9 2007/03/06 15:48:58 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//                   PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.processview;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.activebpel.rt.bpeladmin.war.AeMessages;
import org.activebpel.rt.bpeladmin.war.web.AeWebUtil;
import org.activebpel.rt.util.AeUtil;

/**
 * A simple wrapper aound name:value (key:value) sets.
 */
public class AePropertyNameValue
{
   /**
    * key or id for the name value set.
    */
   private String mKey;
   /** name of attrbute or property */
   private String mName;

   /** value of property. */
   private String mValue;

   /** Date version of the property value */
   private Date mDate = null;

   /** Location path to be hyperlinked (if available). */
   private String mLocationPath = null;

   /** Return true if this property can be edited. */
   private boolean mEditable;
   /** Edit property for process id*/
   private String mEditProcessId;
   /** Edit property for the editable content*/
   private String mEditData;
   /** Edit property for edit operation type. */
   private String mEditOperation;
   /**
    * Constructs the name/value wrapper.
    */
   public AePropertyNameValue(String aName, String aValue, boolean aEditFlag)
   {
      this(aName, aValue, false, null, aEditFlag);
   }

   /**
    * Constructs the name/value wrapper.
    */
   public AePropertyNameValue(String aName, String aValue, String aLocationPath)
   {
      this(aName, aValue, false, aLocationPath, false);
   }

   /**
    * Constructs the name/value wrapper.
    */
   public AePropertyNameValue(String aName, String aValue, boolean aIsDate, String aLocationPath, boolean aEditFlag)
   {
      this(aName, aName, aValue, aIsDate, aLocationPath, aEditFlag);
   }

   /**
    * Constructs the name/value wrapper.
    */
   public AePropertyNameValue(String aKey, String aName, String aValue, boolean aIsDate, String aLocationPath, boolean aEditFlag)
   {
      setKey(aKey);
      setName(aName);
      setValue(aValue);
      setLocationPath(aLocationPath);
      mEditable = aEditFlag;

      if (aIsDate)
      {
         String dateFormatPattern = AeMessages.getString("AePropertyNameValue.date_time_pattern") ; //$NON-NLS-1$
         SimpleDateFormat df = new SimpleDateFormat(dateFormatPattern);
         try
         {
            mDate = df.parse(mValue);
         }
         catch(Exception e)
         {
            // Workaround for GNU Classpath defect 27189
            // (see http://gcc.gnu.org/bugzilla/show_bug.cgi?id=27189).
            //
            // TODO (KR) Remove this when the bug is fixed.
            int n = mValue.indexOf("--"); //$NON-NLS-1$
            if (n > 0)
            {
               try
               {
                  mDate = df.parse(mValue.substring(0, n) + "-0" + mValue.substring(n + 2)); //$NON-NLS-1$
               }
               catch (Exception ignoreThisOneToo)
               {
               }
            }
         }
      }
   }

   /**
    * @return Returns the key.
    */
   public String getKey()
   {
      return mKey;
   }

   /**
    * @param aKey The key to set.
    */
   public void setKey(String aKey)
   {
      mKey = aKey;
   }

   /**
    * @return The propery display name based on the resoure bundle.
    */
   public String getDisplayName()
   {
      return AeMessages.getString("AeProcessViewBean.property." + getName(), //$NON-NLS-1$
            AeProcessViewUtil.formatLabel( getName() ));
   }

   /**
    * Sets the property name.
    * @param aName property name.
    */
   public void setName(String aName)
   {
      mName = aName;
   }

   /**
    * @return Returns the name.
    */
   public String getName()
   {
      return mName;
   }

   /**
    * @return Returns the value.
    */
   public String getValue()
   {
      return mValue;
   }

   /**
    * Sets the value for the property.
    * @param aValue property value.
    */
   public void setValue(String aValue)
   {
      mValue = AeUtil.getSafeString(aValue);
   }

   /**
    * @return True if this the value is a Date
    */
   public boolean isDateValue()
   {
      return (mDate != null);
   }

   /**
    * @return The value as Date object or null if the value is not a date.
    */
   public Date getDate()
   {
      return mDate;
   }

   /**
    * @return Returns the estimated number of rows the String value occupies based on
    *         the occurances of carriage return characters.
    */
   public int getRowCount()
   {
      int row = 1; // default is atleast one row.
      String text = AeUtil.getSafeString( getValue() );

      for (int i = 0; i < text.length(); i++)
      {
         if (text.charAt(i) == '\n')
         {
            row++;
         }
      }
      return row;
   }

   /**
    * @return True if this property has a location path to be hyperlinked.
    */
   public boolean isHasLocationPath()
   {
      return !AeUtil.isNullOrEmpty(getLocationPath());
   }

   /**
    * @return Returns the mLocationPath.
    */
   public String getLocationPath()
   {
      return mLocationPath;
   }

   /**
    * @param aLocationPath The mLocationPath to set.
    */
   public void setLocationPath(String aLocationPath)
   {
      mLocationPath = aLocationPath;
   }

   /**
    * @return Returns the single quote escaped value of the location path.
    */
   public String getEscapedLocationPath()
   {
      return AeWebUtil.escapeSingleQuotes(mLocationPath);
   }

   /**
    * @return Returns the editable.
    */
   public boolean isEditable()
   {
      return mEditable;
   }

   /**
    * @param aEditable The editable to set.
    */
   public void setEditable(boolean aEditable)
   {
      mEditable = aEditable;
   }
   /**
    * @return Returns the editData.
    */
   public String getEditData()
   {
      return mEditData;
   }
   /**
    * @param aEditData The editData to set.
    */
   public void setEditData(String aEditData)
   {
      mEditData = aEditData;
   }

   /**
    * @return Returns the editOperation.
    */
   public String getEditOperation()
   {
      return mEditOperation;
   }
   /**
    * @param aEditOperation The editOperation to set.
    */
   public void setEditOperation(String aEditOperation)
   {
      mEditOperation = aEditOperation;
   }

   /**
    * @return Returns the editProcessId.
    */
   public String getEditProcessId()
   {
      return mEditProcessId;
   }
   /**
    * @param aEditProcessId The editProcessId to set.
    */
   public void setEditProcessId(String aEditProcessId)
   {
      mEditProcessId = aEditProcessId;
   }
}

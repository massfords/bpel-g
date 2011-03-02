// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeIfPropertyMatchesTag.java,v 1.2 2007/04/27 21:53:53 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.tags;

import javax.servlet.jsp.JspException;

import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.war.AeMessages;

/**
 * A tag that includes its body content only if the
 * given value property matches the string value of the
 * specified property for a named bean.
 * NOTE: the return type of the bean property can be
 * any type, but the evaluation will be performed
 * against its toString method.
 */
public class AeIfPropertyMatchesTag extends AeAbstractBeanPropertyTag
{
   /** The value to evaluate against. */
   protected String mValue;
   /** The return type of the bean property. */
   protected Class mClassType;
   /**  Copy from bean name */
   private String mFromName;
   /** Copy from bean property name. */
   private String mFromProperty;
   
   /**
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
   public int doStartTag() throws JspException
   {
      if( isMatch() )
      {
         return EVAL_BODY_INCLUDE;
      }
      else
      {
         return SKIP_BODY;
      }
   }

   /**
    * Returns true if the given value matches the property value.
    * @throws JspException
    */
   protected boolean isMatch() throws JspException
   {
      Object actualValue = getPropertyFromBean( getReturnType() );
      return handleCompareValue( actualValue );
   }

   /**
    * Returns true of the string value of the actual argument matches the tag value.
    * @param aActualValue property object to compared to
    * @return true if property matches the value.
    */
   protected boolean handleCompareValue(Object aActualValue) throws JspException
   {
      Object value = getValueForComparison();
      return value != null && value.equals( aActualValue.toString() );
   }
   
   /**
    * This method will allow dynamic comparison of properties between two beans 
    */
   protected Object getValueForComparison() throws JspException
   {
      Object value = null;
      if (AeUtil.notNullOrEmpty( getFromName() ) && AeUtil.notNullOrEmpty( getFromProperty() ) )
      {
         // get value obtained from another bean.
         Object fromBean = pageContext.findAttribute( getFromName() );
         if (fromBean != null)
         {
            value = getPropertyFromBean(fromBean, getFromProperty());
         }
      }
      else
      {
         // get value from 'value' attribute in the tag.
         value = getValue();
      } 
      return value;
   }


   /**
    * Accessor for the comparison value.
    */
   public String getValue()
   {
      return mValue;
   }

   /**
    * Setter for the comparison value.
    * @param aString
    */
   public void setValue(String aString)
   {
      mValue = aString;
   }
   
   /**
    * @return the fromName
    */
   public String getFromName()
   {
      return mFromName;
   }

   /**
    * @param aFromName the fromName to set
    */
   public void setFromName(String aFromName)
   {
      mFromName = aFromName;
   }

   /**
    * @return the fromProperty
    */
   public String getFromProperty()
   {
      return mFromProperty;
   }

   /**
    * @param aFromProperty the fromProperty to set
    */
   public void setFromProperty(String aFromProperty)
   {
      mFromProperty = aFromProperty;
   }   

   /**
    * Setter for the return class type.  This impl has
    * special code to handle the primitive int type - pass in "int".
    * All other cases are handled via <code>Class.forName</code>.
    * @param aClassType The return class type.
    * @throws JspException
    */
   public void setClassType( String aClassType ) throws JspException
   {
      if( "int".equals( aClassType) ) //$NON-NLS-1$
      {
         mClassType = int.class;
      }
      else
      {
         try
         {
            mClassType = Class.forName( aClassType );
         }
         catch (ClassNotFoundException e)
         {
            throw new JspException(AeMessages.getString("AeIfPropertyMatchesTag.7") + aClassType ); //$NON-NLS-1$
         }
      }
   }

   /**
    * Accessor for the return class type.
    */
   protected Class getReturnType()
   {
      return mClassType;
   }

   /**
    * @see javax.servlet.jsp.tagext.Tag#release()
    */
   public void release()
   {
      super.release();
      mClassType = null;
      setName( null );
      setProperty( null );
      setValue( null );
   }
}
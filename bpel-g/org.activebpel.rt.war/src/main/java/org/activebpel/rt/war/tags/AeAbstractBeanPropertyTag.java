//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeAbstractBeanPropertyTag.java,v 1.5 2007/09/05 16:39:03 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.tags; 

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.war.AeMessages;

/**
 * Provides support for reading a property from another bean within the page context.
 */
abstract public class AeAbstractBeanPropertyTag extends AeAbstractResourceTag
{
   /** property we're going to read from the bean */
   private String mProperty;
   /** name of the bean */
   private String mName;
   
   /**
    * Gets the error value from the bean or null.
    */
   protected String getErrorValue() throws JspException
   {
      String errorValue = null;
      if (getBean() instanceof IAeErrorAwareBean)
      {
         IAeErrorAwareBean bean = (IAeErrorAwareBean) getBean();
         errorValue = bean.getErrorValue(getProperty());
      }
      return errorValue;
   }
   
   
   /**
    * Invokes the bean's property and returns the value.
    * @return bean property value.
    * @throws JspException
    */
   protected Object getPropertyFromBean() throws JspException
   {
      return getPropertyFromBean(getBean(), getProperty());
   }

   /**
    * Invokes the bean's property and returns the value. 
    * @param aReturnType
    * @return Object associated with the bean property
    * @throws JspException
    */
   protected Object getPropertyFromBean(Class aReturnType) throws JspException
   {
      return getPropertyFromBean( getBean() , getProperty(), aReturnType);
   }   
   
   /**
    * Invokes the bean's property and returns the value. The bean's property name
    * can have method names chained using the dot as a seperator. E.g. customer.address.city
    * is equivalent to getCustomer().getAddress().getCity().
    * @param aBean
    * @param aProperty
    * @return bean property value.
    * @throws JspException
    */
   protected Object getPropertyFromBean(Object aBean, String aProperty) throws JspException
   {
      return getPropertyFromBean(aBean,  aProperty, null);
   }
   
   /**
    * Invokes the bean's property and returns the value. The bean's property name
    * can have method names chained using the dot as a seperator. E.g. customer.address.city
    * is equivalent to getCustomer().getAddress().getCity().
    *
    * @param aBean
    * @param aProperty method name.
    * @return bean property value.
    * @throws JspException
    */
   protected Object getPropertyFromBean(Object aBean, String aProperty, Class aReturnType) throws JspException
   {
      Object retVal = null;
      if( aBean != null && AeUtil.isNullOrEmpty(aProperty))
      {
         // if the property (method) name is empty, then return the toString() value.
         retVal = (aReturnType == null) ? aBean.toString() : aBean;
      }
      else if( aBean != null )
      {
         try
         {
            Class beanClass = aBean.getClass();
            // get list of method names (from string dot separated format method1.method2.methodN).
            String methodNameList[] = aProperty.split("\\."); //$NON-NLS-1$
            for (int i = 0; i < methodNameList.length; i++)
            {
               Method method = AeBeanUtils.getAccessor(beanClass, methodNameList[i], aReturnType);
               retVal = method.invoke(aBean, new Object[0]);
               if (retVal != null)
               {
                  aBean = retVal;
                  beanClass = aBean.getClass();
               }
               else
               {
                  break;
               }
            }
         }
         catch(Exception e)
         {
            throw new JspException(AeMessages.getString("AeAbstractBeanPropertyTag.ERROR_0") + e.getMessage() ); //$NON-NLS-1$
         }
      }
      else
      {
         throw new JspException(AeMessages.getString("AeAbstractBeanPropertyTag.NULL_BEAN") ); //$NON-NLS-1$
      }
      return retVal;
   }
   
   
   /**
    * Invokes the bean's setter with the specified value.
    * 
    * @param aValue
    * @param aType
    * @throws JspException
    */
   protected void setPropertyOnBean(Object aValue, Class aType) throws JspException
   {
      setPropertyOnBean( getBean(), getProperty(), aValue, aType);
   }
   /**
    * Invokes the bean's setter with the specified value.
    * @param aBean
    * @param aProperty
    * @param aValue
    * @param aType
    * @throws JspException
    */
   protected void setPropertyOnBean(Object aBean, String aProperty, Object aValue, Class aType) throws JspException
   {
      // todo (PJ): if class aType is null, then convert the string aValue to appropriate type 
      // based on table described in http://java.sun.com/products/jsp/tags/11/syntaxref11.fm13.html.
      if( aBean != null )
      {
         try
         {
            Class beanClass = aBean.getClass();
            Object[] args = {aValue};            
            Method method = AeBeanUtils.getMutator(beanClass, aProperty, aType);
            method.invoke(aBean, args);
         }
         catch(Exception e)
         {
            throw new JspException(AeMessages.getString("AeAbstractBeanPropertyTag.ERROR_1") + e.getMessage() ); //$NON-NLS-1$
         }
      }
      else
      {
         throw new JspException(AeMessages.getString("AeAbstractBeanPropertyTag.NULL_BEAN") ); //$NON-NLS-1$
      }
   }
   
   /**
    * Gets the bean from the context.
    */
   protected Object getBean() throws JspException
   {
      return getBean( getName() );
   }
   
   /**
    * Gets the named bean from the context.
    */
   protected Object getBean(String aBeanName) throws JspException
   {
      Object bean = pageContext.getAttribute( aBeanName );
      
      if (bean == null)
      {
         bean = pageContext.getAttribute( getName(), PageContext.REQUEST_SCOPE );
         if (bean == null)
         {
            throw new JspException(AeMessages.format("AeAbstractBeanPropertyTag.BEAN_NOT_FOUND", aBeanName) ); //$NON-NLS-1$
         }
      }     
      return bean;
   }   

   /**
    * Helper method that xlates the IOException during write to JspException
    * @param aValue
    * @throws JspException
    */
   protected void write(String aValue) throws JspException
   {
      try
      {
         pageContext.getOut().write(aValue);
      }
      catch (IOException e)
      {
         throw new JspException(e);
      }
   }
   

   /**
    * @return Returns the name.
    */
   public String getName()
   {
      return mName;
   }
   
   /**
    * @param aName The name to set.
    */
   public void setName(String aName)
   {
      mName = aName;
   }
   
   /**
    * @return Returns the property.
    */
   public String getProperty()
   {
      return mProperty;
   }
   
   /**
    * @param aProperty The property to set.
    */
   public void setProperty(String aProperty)
   {
      mProperty = aProperty;
   }
}
 

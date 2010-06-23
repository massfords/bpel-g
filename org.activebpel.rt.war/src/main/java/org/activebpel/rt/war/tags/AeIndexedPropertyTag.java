// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeIndexedPropertyTag.java,v 1.1 2007/04/24 17:23:13 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.tags;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;

import org.activebpel.rt.war.AeMessages;

/**
 * Tag for "iterating" through the indexed properties of 
 * a bean object.  The indexed types are then added to the
 * page context (via the id attribute) and available as
 * scriptable beans on the the page.
 * 
 * NOTE: By design convention, it is expected that any
 * indexed property will have a corresponding 
 * getPropertySize method.
 */
public class AeIndexedPropertyTag extends AeAbstractBeanPropertyTag
{
   private static final String INDEX = "Index"; //$NON-NLS-1$

   /** The scriptable object name. */
   protected String mId;
   /** The indexed class.  */
   protected Class mIndexedClass;
   
   /** The bean object */
   protected Object mBean;
   /** The indexed property accessor method */
   protected Method mMethod;
   /** The size of the indexed property array. */
   protected int mSize;
   /** The current index. */
   protected int mIndex;

   /**
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
   public int doStartTag() throws JspTagException
   {
      mBean = pageContext.findAttribute( getName() );
      if( mBean != null )
      {
         Class beanClass = mBean.getClass();
         initSize( beanClass );
         
         if( mSize > 0 )
         {
            initMethod(beanClass);
            mIndex = 0;
            assignElement();
            return EVAL_BODY_AGAIN;
         }
         else
         {
            return SKIP_BODY;
         }
      }
      else
      {
         throw new JspTagException( AeMessages.getString("AeIndexedPropertyTag.0") + getName() ); //$NON-NLS-1$
      }
   }
   
   /**
    * Determine the size of the indexed property array.
    * @param aBeanClass
    * @throws JspTagException
    */
   protected void initSize( Class aBeanClass ) throws JspTagException
   {
      try
      {
         String getXXXSize = getProperty()+"Size"; //$NON-NLS-1$
         Method getSizeMethod = AeBeanUtils.getAccessor( aBeanClass, getXXXSize, int.class );
         Integer sizeObject = (Integer)invokeMethod( mBean, getSizeMethod, "size" ); //$NON-NLS-1$
         mSize = sizeObject.intValue();
      }
      catch (IntrospectionException e)
      {
         e.printStackTrace();
         throw new JspTagException(e.getMessage());
      }
   }
   
   /**
    * Initialize the indexed property accessor method.
    * @param aBeanClass
    * @throws JspTagException
    */
   protected void initMethod( Class aBeanClass ) throws JspTagException
   {
      try
      {
         mMethod = AeBeanUtils.getIndexedAccessor( aBeanClass, getProperty(), getIndexedClass() );
      }
      catch (IntrospectionException e)
      {
         throw new JspTagException( e.getMessage() );
      }   
   }
   
   /**
    * Add the current indexed object to the page context.
    * @throws JspTagException
    */
   protected void assignElement() throws JspTagException
   {
      Object element = invokeMethod( mBean, mMethod, mIndex, "element" ); //$NON-NLS-1$
      pageContext.setAttribute( getId(), element );
      pageContext.setAttribute(getId() + INDEX, this); 
   }

   /**
    * Utility for invoking the given method on the object instance.
    * @param aInstance
    * @param aMethod
    * @param aLabel Used as a "readable" identifier on error messages.
    * @throws JspTagException
    */
   protected Object invokeMethod( Object aInstance, Method aMethod, String aLabel )
   throws JspTagException
   {
      return invokeMethod( aInstance, aMethod, new Object[0], aLabel );      
   }

   /**
    * Utility for invoking the given method on the object instance.
    * @param aInstance
    * @param aMethod
    * @param aIndex The index of the object to return.
    * @param aLabel Used as a "readable" identifier on error messages.
    * @throws JspTagException
    */
   protected Object invokeMethod( Object aInstance, Method aMethod, 
                                             int aIndex, String aLabel )
   throws JspTagException
   {
      Integer[] args = { new Integer(aIndex) };
      return invokeMethod( aInstance, aMethod, args, aLabel );
   }
   
   /**
    * Utility for invoking the given method on the object instance.
    * @param aInstance
    * @param aMethod
    * @param aArgs
    * @param aLabel Used as a "readable" identifier on error messages.
    * @throws JspTagException
    */
   protected Object invokeMethod( Object aInstance, Method aMethod, 
                                                Object[] aArgs, String aLabel )
   throws JspTagException
   {
      try
      {
         return aMethod.invoke( aInstance, aArgs );
      }
      catch (IllegalAccessException e)
      {
         throw new JspTagException(MessageFormat.format(AeMessages.getString("AeIndexedPropertyTag.1"), //$NON-NLS-1$
                                                        new Object[] {aLabel, getProperty(), getName()}) );
      }
      catch (InvocationTargetException e)
      {
         e.printStackTrace();
         throw new JspTagException(MessageFormat.format(AeMessages.getString("AeIndexedPropertyTag.2"), //$NON-NLS-1$
                                                        new Object[] {aLabel, getProperty(), getName()}) );
      }
   }

   /**
    * @see javax.servlet.jsp.tagext.IterationTag#doAfterBody()
    */
   public int doAfterBody() throws JspTagException
   {
      BodyContent content = getBodyContent();
      try
      {
         content.writeOut( getPreviousOut() );
      }
      catch( IOException io )
      {
         io.printStackTrace();
         throw new JspTagException(AeMessages.getString("AeIndexedPropertyTag.10") + io.getMessage() ); //$NON-NLS-1$
      }
      content.clearBody();
      
      if( ++mIndex < mSize )
      {
         assignElement();
         return EVAL_BODY_AGAIN;
      }
      else
      {
         return SKIP_BODY;
      }
   }
   
   /**
    * Getter for the index
    */
   public int getIndex()
   {
      return mIndex;
   }
   
   /**
    * @see javax.servlet.jsp.tagext.Tag#release()
    */
   public void release()
   {
      super.release();

      pageContext.removeAttribute(getId(), PageContext.PAGE_SCOPE);
      pageContext.removeAttribute(getId() + INDEX, PageContext.PAGE_SCOPE); 

      setName(null);
      setId(null);
      setProperty(null);
      mIndexedClass = null;
      mIndex = 0;
      mSize = 0;
      mMethod = null;
      mBean = null;
   }

   /**
    * @see javax.servlet.jsp.tagext.TagSupport#getId()
    */
   public String getId()
   {
      return mId;
   }

   /**
    * Getter for indexed property class name.
    */
   public Class getIndexedClass()
   {
      return mIndexedClass;
   }

   /**
    * @see javax.servlet.jsp.tagext.TagSupport#setId(java.lang.String)
    */
   public void setId(String aString)
   {
      mId = aString;
   }
   
   /**
    * Setter for the indexed property return value class name.
    * @param aClassName 
    * @throws JspTagException Wraps a <code>ClassNotFoundException</code>.
    */
   public void setIndexedClassName(String aClassName) throws JspTagException
   {
      try
      {
         mIndexedClass = Class.forName( aClassName );
      }
      catch (ClassNotFoundException e)
      {
         throw new JspTagException(AeMessages.getString("AeIndexedPropertyTag.11") + aClassName ); //$NON-NLS-1$
      }
   }
}

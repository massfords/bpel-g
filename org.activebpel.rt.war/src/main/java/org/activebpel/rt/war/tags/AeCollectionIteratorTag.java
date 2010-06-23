//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeCollectionIteratorTag.java,v 1.1 2007/04/24 17:23:13 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.tags;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

/**
 * Simple tag to iterate over a collection.
 *
 * &lt;ae:CollectionIterator name="nameOfBean" property="beanColletionPropertyName" id="indexName" /&gt;
 *    Item = &lt;jsp:getProperty name="indexName" property="propName" /&gt;  &lt;br/&gt;
 * &lt;/ae:CollectionIterator&gt;
 */
public class AeCollectionIteratorTag extends AeAbstractBeanPropertyTag
{
   /**
    * Collection iterator.
    */
   private Iterator mCollectionIterator = null;

   /**
    * @return the collectionIterator
    */
   protected Iterator getCollectionIterator()
   {
      return mCollectionIterator;
   }

   /**
    * @param aCollectionIterator the collectionIterator to set
    */
   protected void setCollectionIterator(Iterator aCollectionIterator)
   {
      mCollectionIterator = aCollectionIterator;
   }

   /**
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
   public int doStartTag() throws JspException
   {
      if (getCollectionIterator() == null)
      {
         Object obj = getPropertyFromBean();
         if (obj != null && obj instanceof Collection)
         {
            setCollectionIterator( ((Collection) obj).iterator() );
         }
      }

      if( getCollectionIterator() != null && getCollectionIterator().hasNext() )
      {
         setPageContextValue();
         return EVAL_BODY_INCLUDE;
      }
      else
      {
         return SKIP_BODY;
      }
   }

   /**
    * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
    */
   public int doEndTag() throws JspException
   {
      if (getId() != null)
      {
         pageContext.removeAttribute(getId(), PageContext.PAGE_SCOPE);
      }
      setName(null);
      setId(null);
      setProperty(null);
      setCollectionIterator(null);
      return super.doEndTag();
   }

   /**
    * @see javax.servlet.jsp.tagext.IterationTag#doAfterBody()
    */
   public int doAfterBody() throws JspTagException
   {
      if( getCollectionIterator() != null && getCollectionIterator().hasNext() )
      {
         setPageContextValue();
         return EVAL_BODY_AGAIN;
      }
      else
      {
         return SKIP_BODY;
      }
   }

   /**
    * Sets the current iterator value in the page context.
    */
   protected void setPageContextValue()
   {
      Object obj = getCollectionIterator().next();
      pageContext.setAttribute(getId(), obj);
   }

}




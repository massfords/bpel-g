//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeXpathNodeIteratorTag.java,v 1.1 2008/01/11 15:17:44 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.tags;

import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

import org.activebpel.rt.AeException;
import org.activebpel.rt.util.AeXPathUtil;
import org.w3c.dom.Node;

/**
 * <p>Tag which allows one to iterate over a node list</p>
 * <pre>
 * &lt;ae:XpathNodeIterator name="beanName" property="contextNode" query="xpath" id="indexName" &gt;
 *    &lt;ae:XPathSelectNodeText name="indexName"  query="xpath" /&gt;
 * &lt;/ae:XpathNodeIterator &gt;
 * </pre>
 */
public class AeXpathNodeIteratorTag extends AeAbstractXpathSelectionTag
{
   /**
    * Node list iterator.
    */
   private Iterator mNodeListIterator = null;

   public int doStartTag() throws JspException
   {
      if (getNodeListIterator() == null)
      {
         try
         {
            Node contextNode = getContextNode();
            List list = AeXPathUtil.selectNodes(contextNode, getQuery(), getNamespaceMap());
            setNodeListIterator(list.iterator());
         }
         catch(AeException aex)
         {
            throw new JspException(aex);
         }
      }

      if( getNodeListIterator() != null && getNodeListIterator().hasNext() )
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
    * @return the node list iterator
    */
   protected Iterator getNodeListIterator()
   {
      return mNodeListIterator;
   }

   /**
    * @param aNodeListIterator the aNodeListIterator to set
    */
   protected void setNodeListIterator(Iterator aNodeListIterator)
   {
      mNodeListIterator = aNodeListIterator;
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
      setNodeListIterator(null);
      return super.doEndTag();
   }

   /**
    * @see javax.servlet.jsp.tagext.IterationTag#doAfterBody()
    */
   public int doAfterBody() throws JspTagException
   {
      if( getNodeListIterator() != null && getNodeListIterator().hasNext() )
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
      Object obj = getNodeListIterator().next();
      pageContext.setAttribute(getId(), obj);
   }

}

//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeXpathExistTag.java,v 1.2 2008/02/17 21:57:11 MFord Exp $
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

import org.activebpel.rt.AeException;
import org.activebpel.rt.util.AeXPathUtil;
import org.w3c.dom.Node;

/**
 * <p>Tag which evaluates the body if the xpath exists</p>
 * <pre>
 * &lt;ae:XpathExists name="beanName" property="contextNode" query="xpath" &gt;
 *  body
 *  &lt;/ae:XpathExists &gt;
 * </pre>
 */
public class AeXpathExistTag extends AeAbstractXpathSelectionTag
{
   /**
    * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
    */
   public int doStartTag() throws JspException
   {
      if ( pathExists() )
      {
         return EVAL_BODY_INCLUDE;
      }
      else
      {
         return SKIP_BODY;
      }
   }

   /**
    * Returns true if the xpath exists.
    * @throws JspException
    */
   protected boolean pathExists() throws JspException
   {
      try
      {
         Node contextNode = getContextNode();
         return AeXPathUtil.selectSingleNode(contextNode, getQuery(), getNamespaceMap()) != null;
      }
      catch(AeException aex)
      {
         throw new JspException(aex);
      }
   }
}

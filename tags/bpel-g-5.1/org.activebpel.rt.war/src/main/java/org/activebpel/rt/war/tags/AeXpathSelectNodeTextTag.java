//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeXpathSelectNodeTextTag.java,v 1.2 2008/02/17 21:57:11 MFord Exp $
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
import org.activebpel.rt.util.AeXPathUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * <p>Tag which allows one to get the xml node text.</p>
 * <pre>
 * &lt;ae:XPathSelectNodeText name="beanName" property="contextNode" query="xpath" /&gt;
 * </pre>
 */
public class AeXpathSelectNodeTextTag extends AeAbstractXpathSelectionTag
{
   /**
    * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
    */
   public int doStartTag() throws JspException
   {
      write( getNodeText() );
      return SKIP_BODY;
   }

   /**
    * Returns the node text
    * @throws JspException
    */
   protected String getNodeText() throws JspException
   {
      Node contextNode = getContextNode();
      // if xpath query is given, then apply it on context node, else get text from context node.
      if (AeUtil.notNullOrEmpty( getQuery() ))
      {
         return  AeUtil.getSafeString( AeXPathUtil.selectText(contextNode, getQuery(), getNamespaceMap()) );
      }
      else
      {
         return  AeUtil.getSafeString( AeXmlUtil.getText( (Element) contextNode) );
      }
   }
}

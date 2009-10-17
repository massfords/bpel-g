//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeAbstractXpathSelectionTag.java,v 1.2 2008/02/17 21:57:11 MFord Exp $
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
import org.activebpel.rt.war.AeMessages;
import org.w3c.dom.Node;

/**
 * Base class used by xpath selection query tags.
 */
public abstract class AeAbstractXpathSelectionTag extends AeAbstractXpathTag
{
   /** Xpath selection path query. */
   private String mQuery;

   /**
    * @return the query
    */
   public String getQuery()
   {
      return mQuery;
   }

   /**
    * @param aQuery the query to set
    */
   public void setQuery(String aQuery)
   {
      mQuery = aQuery;
   }

   /**
    * Returns the xpath context node or <code>null</code> if not available.
    */
   protected Node getContextNode() throws JspException
   {
      Object contextObject;
      // if the propery name is given, then use the bean property as the context,
      // otherwise, use the bean (i.e. self) as the context.
      if (AeUtil.notNullOrEmpty( getProperty() ))
      {
         contextObject = getPropertyFromBean();
      }
      else
      {
         contextObject = getBean();
      }
      if (contextObject instanceof Node)
      {
         return (Node) contextObject;
      }
      else if (contextObject != null)
      {
         throw new JspException(AeMessages.format("AeAbstractXpathSelectionTag.INVALID_CONTEXT_NODE", contextObject.getClass().getName() ) ); //$NON-NLS-1$
      }
      else
      {
         throw new JspException(AeMessages.getString("AeAbstractXpathSelectionTag.NO_CONTEXT_NODE")); //$NON-NLS-1$
      }
   }
}

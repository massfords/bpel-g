//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeAbstractXpathTag.java,v 1.1 2008/01/11 15:17:44 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.tags;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.PageContext;

/**
 * Base tag class related to tags that needs to access a names space mapping.
 */
public abstract class AeAbstractXpathTag extends AeAbstractBeanPropertyTag
{
   /** Request attribute key used to store the name space map. */
   public static final String NSMAP_KEY = "org.activebpel.rt.war.tags.NSMAP"; //$NON-NLS-1$
   /**
    * Returns the NS Map from the request scope. If the map did not exist, a new hash map
    * is created and set on the request scope.
    * @return Map containing namespace prefix uri mapping.
    */
   protected Map getNamespaceMap()
   {
      Map map = (Map) pageContext.getAttribute(NSMAP_KEY, PageContext.REQUEST_SCOPE );
      if (map == null)
      {
         map = new HashMap();
         pageContext.setAttribute(NSMAP_KEY, map, PageContext.REQUEST_SCOPE );
      }
      return map;
   }

}

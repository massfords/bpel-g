//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeIfParamInListTag.java,v 1.1 2007/04/24 17:23:12 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.tags;

import org.activebpel.rt.util.AeUtil;

/**
 * A tag that includes its body content only if the given http parameter value is
 * in the given list of strings. The list of strings are separated by a comma.
 * This class does a case-insensitve test against the given list of values.
 */
public class AeIfParamInListTag extends AeIfParamMatchesTag
{
   
   /**
    * Returns true if the request parameter is in the given list of comma separated values.
    */
   protected boolean shouldEvaluateBody()
   {
      String paramValue = pageContext.getRequest().getParameter( getProperty() );
      return AeUtil.isStringInCsvList(paramValue, getValue(), false);
   }   

}

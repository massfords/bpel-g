//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeIfParamNotMatchesTag.java,v 1.1 2007/04/24 17:23:13 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.tags;


/**
 * A tag that will include its body content if
 * the value of the named http servlet request parameter
 * does not match the expected value.
 */
public class AeIfParamNotMatchesTag extends AeIfParamMatchesTag
{

   /**
    * Returns true if the request parameter does not match the 
    * expected value.
    */
   protected boolean shouldEvaluateBody()
   {
      return !( super.shouldEvaluateBody() );
   }   

}

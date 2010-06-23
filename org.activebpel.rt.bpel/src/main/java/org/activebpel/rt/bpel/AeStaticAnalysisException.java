// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/AeStaticAnalysisException.java,v 1.3 2006/07/19 20:07:57 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel;

/**
 * This exception indicates some error within the business process that should
 * have been caught during the static analysis of the business process. An 
 * example of this might be an activity trying to access a variable that isn't
 * declared in any enclosing scope or at the process level. 
 */
public class AeStaticAnalysisException extends AeBusinessProcessException
{
   /**
    * Creates a static analysis exception with the given message.
    * 
    * @param aMessage
    */
   public AeStaticAnalysisException(String aMessage)
   {
      super(aMessage);
   }
}

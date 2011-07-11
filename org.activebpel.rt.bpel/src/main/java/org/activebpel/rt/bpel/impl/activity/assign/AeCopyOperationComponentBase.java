//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/AeCopyOperationComponentBase.java,v 1.3.16.1 2008/04/21 16:09:43 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign; 


/**
 * Base class for copy operation components like &lt;from&gt; and &lt;to&gt; variant
 * implementations. 
 */
public class AeCopyOperationComponentBase
{
   /** name of the variable */
   private String mVariableName;
   
   /** used to resolve variable names and execute expressions */
   private IAeCopyOperation mCopyOperation;
   
   /**
    * Ctor accepts the def and context
    */
   public AeCopyOperationComponentBase()
   {
   }

   /**
    * @return Returns the copy operation.
    */
   protected IAeCopyOperation getCopyOperation()
   {
      return mCopyOperation;
   }

   /**
    * @param aCopyOperation The context to set.
    */
   public void setCopyOperation(IAeCopyOperation aCopyOperation)
   {
      mCopyOperation = aCopyOperation;
   }

   /**
    * Getter for the variable name
    */
   public String getVariableName()
   {
      return mVariableName;
   }
   
   /**
    * Setter for the variable name
    * @param aVariableName
    */
   public void setVariableName(String aVariableName)
   {
      mVariableName = aVariableName;
   }
}
 
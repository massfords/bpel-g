//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/IAeBpelLegacyConstants.java,v 1.3 2007/09/26 02:21:03 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io; 

/**
 * Interface for attribute or element names that changed from version 1.1 to 2.0. 
 */
public interface IAeBpelLegacyConstants
{
   /** new attr is successfulBranchesOnly */
   public static final String COUNT_COMPLETED_BRANCHES_ONLY = "countCompletedBranchesOnly"; //$NON-NLS-1$
   public static final String TAG_VARIABLE_ACCESS_SERIALIZABLE = "variableAccessSerializable"; //$NON-NLS-1$

   /** Switch activity is replace with the if activity in 2.0. */
   public static final String TAG_CASE = "case"; //$NON-NLS-1$
   public static final String TAG_OTHERWISE = "otherwise"; //$NON-NLS-1$
   public static final String TAG_SWITCH = "switch"; //$NON-NLS-1$

   /** terminate activity is now 'exit'. */
   public static final String TAG_TERMINATE = "terminate"; //$NON-NLS-1$
   
   public static final String TAG_PARTNERS = "partners"; //$NON-NLS-1$
   public static final String TAG_PARTNER = "partner"; //$NON-NLS-1$
   
}

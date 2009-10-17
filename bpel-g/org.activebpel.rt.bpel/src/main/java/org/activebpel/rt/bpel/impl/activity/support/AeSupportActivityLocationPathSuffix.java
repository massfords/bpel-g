//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/AeSupportActivityLocationPathSuffix.java,v 1.2 2006/06/26 16:50:39 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.support;


/**
 * Lists the location paths suffixes used by the support activity.
 * For example, "_ImplicitCompensateActivity".
 */
public class AeSupportActivityLocationPathSuffix
{
   /** Location path name for a default/implicit compensate activity object. */
   public static final String  IMPLICIT_COMPENSATE_ACTIVITY       = "_ImplicitCompensateActivity"; //$NON-NLS-1$
   
   /** Location path name for a default/implicit compensation handler. */
   public static final String  IMPLICIT_COMPENSATION_HANDLER      = "_ImplicitCompensationHandler"; //$NON-NLS-1$
   
   /** Location path name for a default/implicit termination handler. */
   public static final String  IMPLICIT_TERMINATION_HANDLER      = "_ImplicitTerminationHandler"; //$NON-NLS-1$

   /** Location path name of a default/implicit fault handler. */
   public static final String  IMPLICIT_FAULT_HANDLER             = "_ImplicitFaultHandler"; //$NON-NLS-1$
   
   /** 
    * Location path name for the implicit compensate activity which is run at the end of fault/compensation handler
    * to compensate any remaining coordinated activities. 
    */
   public static final String  IMPLICIT_CC_COMPENSATE_ACTIVITY    = "_ImplicitCompensateActivityCc"; //$NON-NLS-1$
   
   /** Location path name for the coordination container support object in a scope. */
   public static final String  COORDINATION_CONTAINER             = "_CoordinationContainer" ; //$NON-NLS-1$
   
   /**
    * Location path name for the Coordinator's (main process) compensation handler (proxy).
    */
   public static final String  COORDINATION_COMPENSATION_HANDLER  = "_CoordinatedCompensationHandler" ; //$NON-NLS-1$
   
   /**
    * Location path name for the Participant's (subprocess) implicit compensate activity object. 
    */
   public static final String  COORDINATION_COMPENSATE_ACTIVITY   = "_CoordinationCompensationActivity"; //$NON-NLS-1$
         
}

// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/IAeErrorReporter.java,v 1.7 2006/12/14 22:41:40 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation;

import org.activebpel.rt.bpel.def.AeProcessDef;


/**
 * Specification for error reporting classes.
 */
public interface IAeErrorReporter extends IAeBaseErrorReporter
{
   /** Set the root definition node for error reporting. */
   public void setRootDefNode( AeProcessDef aRootDefNode );
   
   /**
    * Remove the issues for a given node.  This is for cases when a node is found to be 'not used', etc.
    * 
    * @param aNode The node whose issues should be removed.
    */
   public abstract void removeIssues( Object aNode );

   /**
    * Report the errors collected by this object.
    */
   public abstract void reportErrors();

   /**
    * Report the warnings collected by this object.
    */
   public abstract void reportWarnings();

   /**
    * Report the information collected by this object.
    */
   public abstract void reportInfo();

   /**
    * Report all errors, warnings and information collected by this object.
    */
   public abstract void reportAll();

   /**
    * Returns true if we are processing validations errors for the BPEL.
    */
   public abstract boolean isProcessErrors();

   /**
    * Returns true if we are processing validations warnings for the BPEL.
    */
   public abstract boolean isProcessWarnings();

   /**
    * Returns true if we are processing validations info for the BPEL.
    */
   public abstract boolean isProcessInfos();
}

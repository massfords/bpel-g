//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/IAeDefMessagePartsMapVisitor.java,v 1.2 2006/09/15 14:49:55 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.def.AeProcessDef;

/**
 * visits each node and inlines the message parts info used for the wsio operation 
 */
public interface IAeDefMessagePartsMapVisitor
{
   /**
    * Traverses the given process definition and assigns message parts maps to
    * web service activities.
    *
    * @param aDef
    * @param aThrowOnErrorsFlag
    * @throws AeBusinessProcessException
    */
   public void assignMessagePartsMaps(AeProcessDef aDef, boolean aThrowOnErrorsFlag) throws AeBusinessProcessException;
}
 
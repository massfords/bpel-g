//$Header: /Development/AEDevelopment/projects/org.activebpel.wsio/src/org/activebpel/wsio/invoke/IAeTwoPhaseInvokeHandler.java,v 1.3 2008/03/23 01:44:28 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.wsio.invoke;

/**
 * <p>
 * Extends the invoke handler such that the invocation process
 * is comprised of two parts - prepare followed by handleInvoke.
 * </p>
 * The prepare method is first called by the engine, and if this
 * method returns true, then the handleInvoke method is called,
 * asynchronous to the current execution thread. 
 *
 */
public interface IAeTwoPhaseInvokeHandler extends IAeInvokeHandler
{
   /**
    * <p>
    * Lets the handler prepare for the invoke. This method is called prior
    * to the handleInvoke method. If this method returns true, then the handleInvoke
    * will be called. 
    * </p>
    * <p>
    * This method is called in the same execution thread as the invoke activity.
    * How ever, the handleInvoke may be called asynchronously.
    * </p> 
    * @param aInvoke web service invoke. 
    * @param aQueryData
    * @return true if successful.
    */
   public boolean prepare(IAeInvoke aInvoke, String aQueryData ) throws AeInvokePrepareException;
}

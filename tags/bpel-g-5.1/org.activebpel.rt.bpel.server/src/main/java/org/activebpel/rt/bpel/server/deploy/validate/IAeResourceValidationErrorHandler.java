//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/IAeResourceValidationErrorHandler.java,v 1.1 2005/11/01 14:39:24 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.validate;

import org.w3c.dom.Node;


/**
 * This interface defines methods that will be called by a validator when it is
 * validating a resource (PDD, PDEF, etc...) file/document.
 */
public interface IAeResourceValidationErrorHandler
{
   /**
    * Called when the validator finds a fatal error and cannot continue.
    * 
    * @param aMessage
    */
   public void fatalError(String aMessage);
   
   /**
    * Called when a warning is found while parsing the resource.
    * 
    * @param aMessage
    * @param aLineNumber
    */
   public void parseWarning(String aMessage, int aLineNumber);

   /**
    * Called when an error is found while parsing the resource.
    * 
    * @param aMessage
    * @param aLineNumber
    */
   public void parseError(String aMessage, int aLineNumber);

   /**
    * Called when a fatal error is found while parsing the resource.
    * 
    * @param aMessage
    * @param aLineNumber
    */
   public void parseFatalError(String aMessage, int aLineNumber);

   /**
    * Called when the validator discovers an error when performing "additional validation".  This
    * additional validation tries to find problems that cannot be caught by the resource's schema.
    * 
    * @param aMessage
    * @param aNode
    */
   public void contentError(String aMessage, Node aNode);
   
   /**
    * Called when the validator discovers a warning when performing "additional validation".  This
    * additional validation tries to find problems that cannot be caught by the resource's schema.
    * 
    * @param aMessage
    * @param aNode
    */
   public void contentWarning(String aMessage, Node aNode);
}

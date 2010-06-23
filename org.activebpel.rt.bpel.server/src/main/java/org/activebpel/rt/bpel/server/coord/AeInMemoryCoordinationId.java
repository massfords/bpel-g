//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/AeInMemoryCoordinationId.java,v 1.2 2005/11/16 16:48:18 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord;


/**
 * Class to construct a coordination id based on the process id and 
 * the invoke acitivity's location path.
 */
public class AeInMemoryCoordinationId implements IAeCoordinationId
{
   /**
    * context id.
    */
   private String mIdentifier = null;
   
   /**
    * Constructs a coordination id given the process id and the location path.
    * @param aProcessId
    * @param aLocationPath
    */
   public AeInMemoryCoordinationId(String aProcessId, String aLocationPath)
   {
      mIdentifier = "activebpel:coord-id:" + aProcessId + ":" + aLocationPath;  //$NON-NLS-1$  //$NON-NLS-2$
   }
   
   /**
    * Returns the coordination context id.
    */
   public String getIdentifier()
   {
      return mIdentifier;
   }
   
}

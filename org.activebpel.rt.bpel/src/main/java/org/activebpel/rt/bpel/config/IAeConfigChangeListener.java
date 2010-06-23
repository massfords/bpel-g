//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/config/IAeConfigChangeListener.java,v 1.1 2004/12/14 16:24:15 PCollins Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.config;




/**
 * Interface for listeners interested in receiving engine configuration 
 * change notices.
 */
public interface IAeConfigChangeListener
{
   
   /**
    * Notification method of engine configuration change.
    * @param aConfig
    */
   public void updateConfig( IAeUpdatableEngineConfig aConfig );

}

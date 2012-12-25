//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/IAeWSBPELFactory.java,v 1.1 2007/10/09 15:01:54 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2004-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io;

import java.util.Set;

/**
 * Interface for WSBPEL 2.0 factory to provide access to understood extension activities
 */
public interface IAeWSBPELFactory
{
   /**
    * Set the understoodExtensionActivities
    * @param aUnderstoodActivities
    */
   public void setUnderstoodExtensionActivities(Set aUnderstoodActivities);
   
}

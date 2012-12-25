//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/IAeExtensionAwareDefVisitorFactory.java,v 1.1 2007/10/12 16:09:48 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2004-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors;

import org.activebpel.rt.xml.def.io.IAeExtensionRegistry;

/**
 * Interface to set extension registry for WSBPEL 2.0 visitor factories 
 */
public interface IAeExtensionAwareDefVisitorFactory extends IAeDefVisitorFactory
{
   /** 
    * Sets the extension registry
    * @param aExtensionRegistry
    */
   public void setExtensionRegistry(IAeExtensionRegistry aExtensionRegistry);
}

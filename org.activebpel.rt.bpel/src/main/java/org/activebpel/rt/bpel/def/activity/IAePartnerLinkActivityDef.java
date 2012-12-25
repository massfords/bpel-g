//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/IAePartnerLinkActivityDef.java,v 1.2 2006/06/26 16:50:31 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.activity;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AePartnerLinkOpKey;

/**
 * Provides interface for defs that model a partner link operation like a <code>receive</code>,
 * <code>onMessage</code>, <code>invoke</code>, and <code>reply</code>.
 */
public interface IAePartnerLinkActivityDef
{
   /**
    * Gets the name of the partner link.
    */
   public String getPartnerLink();

   /**
    * Gets the port type.
    */
   public QName getPortType();

   /**
    * Gets the operation
    */
   public String getOperation();

   /**
    * Returns the partnerlink:operation key for this activity.
    */
   public AePartnerLinkOpKey getPartnerLinkOperationKey();
}

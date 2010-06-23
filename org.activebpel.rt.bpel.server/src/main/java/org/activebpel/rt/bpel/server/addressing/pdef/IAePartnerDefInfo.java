// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/addressing/pdef/IAePartnerDefInfo.java,v 1.2 2004/07/08 13:10:03 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.addressing.pdef;

import org.activebpel.rt.bpel.IAeEndpointReference;

import java.util.Iterator;

import javax.xml.namespace.QName;

/**
 *  Describes a partner definition.
 */
public interface IAePartnerDefInfo
{
   
   /**
    * Accessor for the principal.
    */
   public String getPrincipal();
   
   /**
    * Iterator over the partner link types mapped to 
    * the principal.
    */
   public Iterator getPartnerLinkTypes();
   
   /**
    * Accessor for the role name mapped to the 
    * partner link type - there should only ever
    * be one of these.
    * @param aPlinkType
    */
   public String getRoleName( QName aPlinkType );
   
   /**
    * Accessor for the end point reference mapped to the
    * partner link type.
    * @param aPlinkType
    */
   public IAeEndpointReference getEndpointReference( QName aPlinkType );
}

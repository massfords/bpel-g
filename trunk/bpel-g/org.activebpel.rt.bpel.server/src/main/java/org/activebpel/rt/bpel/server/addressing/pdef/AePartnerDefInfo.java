// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/addressing/pdef/AePartnerDefInfo.java,v 1.3 2004/08/03 21:16:24 ckeller Exp $
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * Default implementation of the IAePartnerDef info.
 */
public class AePartnerDefInfo implements IAePartnerDefInfo
{
   /** principal name */
   private String mPrincipal;
   /** contains the internal mappings */
   private Map mInfo;
   
   /**
    * Constructor.
    * @param aPrincipal
    */
   public AePartnerDefInfo( String aPrincipal )
   {
      mPrincipal = aPrincipal;
      mInfo = new HashMap(); 
   }
   
   /**
    * Add a parnterLinkType,role to endpoint referecen mapping
    * @param aPlink
    * @param aRole
    * @param aRef
    */
   public void addInfo( QName aPlink, String aRole, IAeEndpointReference aRef )
   {
      List info = new ArrayList();
      info.add( aRole );
      info.add( aRef );
      mInfo.put( aPlink, info );
   }

   /**
    * @see org.activebpel.rt.bpel.server.addressing.pdef.IAePartnerDefInfo#getEndpointReference(javax.xml.namespace.QName)
    */
   public IAeEndpointReference getEndpointReference( QName aPlinkType )
   {
      return (IAeEndpointReference) ((List)mInfo.get(aPlinkType)).get(1);
   }

   /**
    * @see org.activebpel.rt.bpel.server.addressing.pdef.IAePartnerDefInfo#getPartnerLinkTypes()
    */
   public Iterator getPartnerLinkTypes()
   {
      return mInfo.keySet().iterator();
   }

   /**
    * @see org.activebpel.rt.bpel.server.addressing.pdef.IAePartnerDefInfo#getPrincipal()
    */
   public String getPrincipal()
   {
      return mPrincipal;
   }

   /**
    * @see org.activebpel.rt.bpel.server.addressing.pdef.IAePartnerDefInfo#getRoleName(javax.xml.namespace.QName)
    */
   public String getRoleName(QName aPlinkType)
   {
      return (String) ((List)mInfo.get(aPlinkType)).get(0);
   }
}


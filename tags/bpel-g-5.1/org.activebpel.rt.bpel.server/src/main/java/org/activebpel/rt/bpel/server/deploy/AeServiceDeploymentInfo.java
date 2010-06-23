// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeServiceDeploymentInfo.java,v 1.8 2007/02/13 15:26:59 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AePartnerLinkDefKey;
import org.activebpel.rt.util.AeUtil;
import org.w3c.dom.Element;

/**
 * Models service data for persistence.
 */
public class AeServiceDeploymentInfo implements IAeServiceDeploymentInfo
{
   /** Web service name. */
   protected String mServiceName;
   /** Partner link key. */
   protected AePartnerLinkDefKey mPartnerLinkDefKey;
   /** Binding type. */
   protected String mBinding;
   /** Any required roles - comma delimited string. */
   protected String mAllowedRoles;
   /** Process qname. */
   protected QName mProcessQName;
   /** Service Policy data. */
   protected List mPolicies;
  
   /**
    * Constructor.
    * @param aServiceName
    * @param aPartnerLinkDefKey
    * @param aBinding
    * @param aRoles
    * @param aPolicyList
    */
   public AeServiceDeploymentInfo( 
      String aServiceName, 
      AePartnerLinkDefKey aPartnerLinkDefKey,
      String aBinding,
      String aRoles,
      List   aPolicyList)
   {
      mServiceName = aServiceName;
      mPartnerLinkDefKey = aPartnerLinkDefKey;
      mBinding = aBinding;
      mAllowedRoles = aRoles;
      mPolicies = aPolicyList == null ? new ArrayList() : aPolicyList;
   }

   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo#getServiceName()
    */
   public String getServiceName()
   {
      return mServiceName;
   }
   
   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo#getPartnerLinkName()
    */
   public String getPartnerLinkName()
   {
      return mPartnerLinkDefKey.getPartnerLinkName();
   }
   
   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo#getPartnerLinkDefKey()
    */
   public AePartnerLinkDefKey getPartnerLinkDefKey()
   {
      return mPartnerLinkDefKey;
   }
   
   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo#getBinding()
    */
   public String getBinding()
   {
      return mBinding;
   }
   
   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo#isMessageService()
    */
   public boolean isMessageService()
   {
      return AeDeployConstants.BIND_MSG.equals( getBinding() );
   }
   
   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo#isRPCEncoded()
    */
   public boolean isRPCEncoded()
   {
      return AeDeployConstants.BIND_RPC.equals( getBinding() );
   }
   
   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo#isRPCLiteral()
    */
   public boolean isRPCLiteral()
   {
      return AeDeployConstants.BIND_RPC_LIT.equals( getBinding() );
   }
   
   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo#isExternalService()
    */
   public boolean isExternalService()
   {
      return AeDeployConstants.BIND_EXTERNAL.equals(getBinding());
   }

   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo#isPolicyService()
    */
   public boolean isPolicyService()
   {
      return AeDeployConstants.BIND_POLICY.equals(getBinding());
   }
   
   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo#addRole(java.lang.String)
    */
   public void addRole( String aRole )
   {
      if( AeUtil.isNullOrEmpty(mAllowedRoles) )
      {
         mAllowedRoles = aRole;
      }
      else
      {
         mAllowedRoles+=(","+aRole); //$NON-NLS-1$
      }
   }
   
   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo#getAllowedRolesAsString()
    */
   public String getAllowedRolesAsString()
   {
      return mAllowedRoles;
   }
   
   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo#getAllowedRoles()
    */
   public Set getAllowedRoles()
   {
      if( AeUtil.isNullOrEmpty(mAllowedRoles) )
      {
         return Collections.EMPTY_SET;
      }
      else
      {
         Set set = new HashSet();
         StringTokenizer st = new StringTokenizer(mAllowedRoles, "," ); //$NON-NLS-1$
         while( st.hasMoreTokens() )
         {
            set.add( st.nextToken().trim() );
         }
         return set;
      }
   }
   
   /**
    * Setter for process qname.
    */
   public void setProcessQName( QName aQName )
   {
      mProcessQName = aQName;
   }
   
   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo#getProcessQName()
    */
   public QName getProcessQName()
   {
      return mProcessQName;
   }
   
   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo#getProcessName()
    */
   public String getProcessName()
   {
      return getProcessQName().getLocalPart();
   }
   
   /**
    * Setter for list of policies.
    */
   public void setPolicies( List policies )
   {
      mPolicies = policies;
   }

   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo#addPolicy(org.w3c.dom.Element)
    */
   public void addPolicy( Element policy )
   {
      if (mPolicies == null)
         mPolicies = new ArrayList();
      mPolicies.add(policy);
       
   }

   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo#getPolicies()
    */
   public List getPolicies()
   {
      if (mPolicies == null)
         mPolicies = new ArrayList();
      return mPolicies;
   }
}

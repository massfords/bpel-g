// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/addressing/pdef/AePartnerAddressBook.java,v 1.5 2006/02/24 16:37:30 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.addressing.pdef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.IAeEndpointReference;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentId;

/**
 * Default container for all partner definitions mapped to 
 * a principal.
 */
public class AePartnerAddressBook implements IAePartnerAddressBook
{
   /** principal name string */
   protected String mPrincipal;
   /** map of partnerlink type/role keys to ref objects */
   protected Map mMappings;
   
   /**
    * Constructor.
    * @param aPrincipal
    */
   public AePartnerAddressBook( String aPrincipal )
   {
      mPrincipal = aPrincipal;
      mMappings = new HashMap(); 
   }
   
   /**
    * Add the partner defi
    * 
    * @param aDeploymentId
    * @param aDeploymentLocation
    * @param aInfo
    */
   public void mergeAddresses( IAeDeploymentId aDeploymentId, 
                        String aDeploymentLocation, IAePartnerDefInfo aInfo )
   {
      if( getPrincipal().equals( aInfo.getPrincipal() ) )
      {
         for (Iterator iter = aInfo.getPartnerLinkTypes(); iter.hasNext();)
         {
            QName partnerLinkType = (QName) iter.next();
            String role = aInfo.getRoleName( partnerLinkType );
            IAeEndpointReference endPt = aInfo.getEndpointReference( partnerLinkType );

            AePartnerAddressRef ref = getPartnerAddressRef(partnerLinkType,role);
            
            if( ref == null )
            {
               ref = new AePartnerAddressRef(partnerLinkType,role);
               getMappings().put( makeKey(partnerLinkType,role), ref );
            }
            
            ref.updateEndpoint( endPt, aDeploymentId, aDeploymentLocation );
         }
      }
   }

   /**
    * Decrements the reference counts for any resources associated
    * with the deployment.  It will then remove any resources associated 
    * with the deployment context if the reference count is zero.
    * @param aDeploymentId
    */
   public void remove( IAeDeploymentId aDeploymentId )
   {
      List deletes = new ArrayList();
      for (Iterator iter = getMappings().entrySet().iterator(); iter.hasNext();)
      {
         Map.Entry entry = (Map.Entry) iter.next();
         AePartnerAddressRef ref = (AePartnerAddressRef)entry.getValue();
         ref.remove( aDeploymentId );

         if( ref.isOkToRemove() )
         {
            deletes.add( entry.getKey() );
         }
      }
      
      for (Iterator iter = deletes.iterator(); iter.hasNext();)
      {
         getMappings().remove( iter.next() );
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.addressing.pdef.IAePartnerAddressBook#getEndpointReference(javax.xml.namespace.QName, java.lang.String)
    */
   public IAeEndpointReference getEndpointReference( QName aPartnerLinkType,
                                                                  String aRole )
   {
      AePartnerAddressRef ref = getPartnerAddressRef(aPartnerLinkType,aRole);
      if( ref != null )
      {
         return ref.getEndpointRef();
      }
      return null;
   }

   /**
    * Construct a key for mapping partnerlink type/role to 
    * an endpoint reference.
    * @param aPartnerLinkType
    * @param aRole
    */
   protected Object makeKey( QName aPartnerLinkType, String aRole )
   {
      return aPartnerLinkType+"="+aRole; //$NON-NLS-1$
   }
   
   /**
    * Returns true if the address book is "empty".
    */
   public boolean isOkToDelete()
   {
      return getMappings().isEmpty();
   }
   
   /**
    * Convenience method for accessing AePartnerAddressRef.
    * @param aPartnerLinkType
    * @param aRole
    */
   protected AePartnerAddressRef getPartnerAddressRef( QName aPartnerLinkType, String aRole )
   {
      Object key = makeKey(aPartnerLinkType,aRole);
      return (AePartnerAddressRef)getMappings().get(key);
   }

   /**
    * @see org.activebpel.rt.bpel.server.addressing.pdef.IAePartnerAddressBook#getPrincipal()
    */
   public String getPrincipal()
   {
      return mPrincipal;
   }
   
   /**
    * Accessor for reference mappings.
    */
   protected Map getMappings()
   {
      return mMappings;
   }
}

//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/pdd/AePartnerLinkDescriptor.java,v 1.7 2006/06/26 18:28:22 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.pdd;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeEndpointReference;
import org.activebpel.rt.bpel.def.AePartnerLinkDefKey;
import org.activebpel.rt.bpel.server.addressing.AeEndpointReferenceSourceType;
import org.activebpel.rt.bpel.server.deploy.IAePddXmlConstants;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Element;

/**
 * Wraps the pdd partner link information.  This object will also be created in the persistence
 * layer, reconstructed from DB information.
 */
public class AePartnerLinkDescriptor implements IAePddXmlConstants
{
   /** Parnter endpoint reference. */
   protected IAeEndpointReference mPartnerEndpointReference;
   /** The partner role element - may be null */
   protected Element mPartnerRoleElement;
   /** Partner link name. */
   protected String mPartnerLinkName;
   /** Partner link path. */
   protected int mPartnerLinkId;
   /** invoke handler */
   protected String mInvokeHandler;
   /** Partner endpoint reference type. */
   protected AeEndpointReferenceSourceType mPartnerEndpointReferenceType;

   /**
    * Constructor.
    * @param aPartnerLinkName
    * @param aPartnerLinkId
    * @param aInvokeHandler
    * @param aType
    * @param aEndpointReference
    * @throws AeBusinessProcessException
    */
   public AePartnerLinkDescriptor(String aPartnerLinkName, int aPartnerLinkId,
         String aInvokeHandler, AeEndpointReferenceSourceType aType, IAeEndpointReference aEndpointReference)
         throws AeBusinessProcessException
   {
      mPartnerLinkName = aPartnerLinkName;
      mPartnerLinkId = aPartnerLinkId;
      mInvokeHandler = aInvokeHandler;
      mPartnerEndpointReferenceType = aType;
      mPartnerEndpointReference = aEndpointReference;
   }

   /**
    * Constructor.
    * @param aPartnerLinkName
    * @param aPartnerLinkId
    * @param aPartnerRoleElement
    * @throws AeBusinessProcessException
    */
   public AePartnerLinkDescriptor(String aPartnerLinkName, int aPartnerLinkId,
         Element aPartnerRoleElement) throws AeBusinessProcessException
  {
      mPartnerLinkName = aPartnerLinkName;
      mPartnerLinkId = aPartnerLinkId;
      if( aPartnerRoleElement != null )
      {
         mPartnerRoleElement = aPartnerRoleElement;
         mInvokeHandler = getInvokeHandler(aPartnerRoleElement);
         mPartnerEndpointReferenceType = getEndpointReferenceSourceType( aPartnerRoleElement );
         mPartnerEndpointReference = getEndpointReference( mPartnerEndpointReferenceType, aPartnerRoleElement );
      }
   }
   
   /**
    * Gets the value for the invoke handler. This method will look at the invokeHandler
    * attribute first and if not found fall back to the older attribute customInvokerUri.
    * 
    * @param aPartnerRoleElement
    */
   protected String getInvokeHandler(Element aPartnerRoleElement)
   {
      String invokeHandler = aPartnerRoleElement.getAttribute( IAePddXmlConstants.ATT_INVOKE_HANDLER );
      if (AeUtil.isNullOrEmpty(invokeHandler))
      {
         invokeHandler = aPartnerRoleElement.getAttribute(IAePddXmlConstants.ATT_CUSTOM_INVOKER);
      }
      return invokeHandler;
   }
   
   /**
    * Extract the <code>AeEndpointReferenceSourceType</code> from the partner role
    * element.
    * @param aPartnerRoleElement
    */
   protected AeEndpointReferenceSourceType getEndpointReferenceSourceType( Element aPartnerRoleElement )
   {
      String typeName = aPartnerRoleElement.getAttribute( ATT_ENDPOINT_REF );
      return AeEndpointReferenceSourceType.getByName( typeName );
   }
   
   /**
    * Return the <code>IAeEndpointReference</code> if the <code>AeEndpointReferenceSourceType</code> 
    * is static, otherwise return null.
    * @param aType
    * @param aPartnerRoleElement
    * @throws AeBusinessProcessException
    */
   protected IAeEndpointReference getEndpointReference( AeEndpointReferenceSourceType aType, Element aPartnerRoleElement )
   throws AeBusinessProcessException
   {
      IAeEndpointReference ref = null;
      if( aType == AeEndpointReferenceSourceType.STATIC )
      {
         ref = AeEngineFactory.getPartnerAddressing().readFromDeployment( aPartnerRoleElement );
      }
      return ref;
   }

   /**
    * @return Returns the <code>IAeEndpointReference</code> impl.
    */
   public IAeEndpointReference getPartnerEndpointReference()
   {
      return mPartnerEndpointReference;
   }
   
   /**
    * Serialize the WSA endpoint reference to its string value.
    */
   public String getSerializedEndpointReference()
   {
      String retVal = null;
      if( mPartnerRoleElement != null )
      {
         retVal = AeXmlUtil.serialize( mPartnerRoleElement );
      }
      return retVal;
   }

   /**
    * Gets the WSA endpoint reference (in its Element form).
    */
   public Element getEndpointReference()
   {
      return mPartnerRoleElement;
   }

   /**
    * @return Returns the type enum for the partner endpoint reference or null 
    * if there is no partner endpoint reference for this partner link.
    */
   public AeEndpointReferenceSourceType getPartnerEndpointReferenceType()
   {
      return mPartnerEndpointReferenceType;
   }

   /**
    * @return Returns the invoke handler
    */
   public String getInvokeHandler()
   {
      return mInvokeHandler;
   }

   /**
    * @return Returns the partnerLinkName.
    */
   public String getPartnerLinkName()
   {
      return mPartnerLinkName;
   }

   /**
    * @return Returns true if the partner endpoint reference type was specified as invoker.
    */
   public boolean isPartnerEndpointInvoker()
   {
      return AeEndpointReferenceSourceType.INVOKER == getPartnerEndpointReferenceType();
   }

   /**
    * @return Returns true if the partner endpoint reference type was specified as principal.
    */
   public boolean isPartnerEndpointPrincipal()
   {
      return AeEndpointReferenceSourceType.PRINCIPAL == getPartnerEndpointReferenceType();
   }

   /**
    * @return Returns true if this descriptor models a partner role.
    */
   public boolean isPartnerRole()
   {
      return getPartnerEndpointReferenceType() != null;
   }

   /**
    * @return Returns the partnerLinkId.
    */
   public int getPartnerLinkId()
   {
      return mPartnerLinkId;
   }
   
   /**
    * Returns a new partner link def key for this partner link.
    */
   public AePartnerLinkDefKey getPartnerLinkDefKey()
   {
      return new AePartnerLinkDefKey(getPartnerLinkName(), getPartnerLinkId());
   }
}

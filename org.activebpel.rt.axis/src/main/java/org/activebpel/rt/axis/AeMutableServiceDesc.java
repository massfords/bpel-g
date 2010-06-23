//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis/src/org/activebpel/rt/axis/AeMutableServiceDesc.java,v 1.3 2006/07/18 20:09:04 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.axis.description.OperationDesc;

/**
 * A <code>ServiceDesc</code> impl that delegates access to its wsdl related
 * properties to an <code>IAeWsdlReference</code>.  This allows the wsdl reference
 * to cache/update its properties as the catalog is updated.
 */
public class AeMutableServiceDesc extends AeServiceDesc
{
   /**
    * The <code>IAeWsdlReference</code> impl.
    */
   private IAeWsdlReference mWsdlReference;
   
   /**
    * Constructor.
    */
   public AeMutableServiceDesc()
   {
      super();
   }
   
   /**
    * @see org.apache.axis.description.ServiceDesc#getProperty(java.lang.String)
    */
   public Object getProperty(String aName)
   {
      return getWsdlReferenceProperty( aName, getWsdlReference(), getProperties() );
   }
   
   /**
    * Look for wsdl related properties in the <code>IAeWsdlReference</code> member,
    * otherwise use the standard properties map. 
    * @param aPropertyKey
    * @param aReference
    * @param aServiceDescProps
    */
   protected Object getWsdlReferenceProperty( String aPropertyKey, IAeWsdlReference aReference, Map aServiceDescProps )
   {
      Object propertyObj = null;
      
      if( AeHandler.WSDL_DEF_ENTRY.equals( aPropertyKey ) )
      {
         propertyObj = aReference.getWsdlDef();
      }
      else if( AeHandler.PARTNER_LINK_ENTRY.equals( aPropertyKey ) )
      {
         propertyObj = aReference.getPartnerLinkDef();
      }
      else if( AeHandler.PORT_TYPE_ENTRY.equals( aPropertyKey) )
      {
         propertyObj = aReference.getPortTypeQName();
      }
      else if( aServiceDescProps != null )
      {
         propertyObj = aServiceDescProps.get( aPropertyKey );
      }
      
      return propertyObj;
   }
   
   /**
    * @see org.apache.axis.description.ServiceDesc#getOperations()
    */
   public ArrayList getOperations()
   {
      return getWsdlReference().getOperations();
   }

   /**
    * @see org.apache.axis.description.ServiceDesc#getOperationsByName(java.lang.String)
    */
   public OperationDesc[] getOperationsByName(String aMethodName)
   {
      return getWsdlReference().getOperationsByName( aMethodName );
   }

   /**
    * @see org.apache.axis.description.ServiceDesc#getOperationByName(java.lang.String)
    */
   public OperationDesc getOperationByName(String aMethodName)
   {
      return getWsdlReference().getOperationByName( aMethodName );
   }

   /**
    * @see org.apache.axis.description.ServiceDesc#removeOperationDesc(org.apache.axis.description.OperationDesc)
    */
   public void removeOperationDesc(OperationDesc aOperation)
   {
      getWsdlReference().removeOperationDesc( aOperation );
   }

   /**
    * @see org.apache.axis.description.ServiceDesc#getAllowedMethods()
    */
   public List getAllowedMethods()
   {
      return getWsdlReference().getAllowedMethods();
   }
   
   /**
    * @param aWsdlReference The wsdlReference to set.
    */
   public void setWsdlReference(IAeWsdlReference aWsdlReference)
   {
      mWsdlReference = aWsdlReference;
   }
   
   /**
    * @see org.activebpel.rt.axis.AeServiceDesc#setInitialized(boolean)
    */
   public void setInitialized(boolean aFlag)
   {
      super.setInitialized(aFlag);

      if( aFlag )
      {
         AeWsdlReferenceTracker.registerReference( getName(), getWsdlReference() );
      }
   }

   /**
    * @return Returns the wsdlReference.
    */
   public IAeWsdlReference getWsdlReference()
   {
      return mWsdlReference;
   }
}

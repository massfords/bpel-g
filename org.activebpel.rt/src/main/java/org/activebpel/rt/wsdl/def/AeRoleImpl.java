// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/AeRoleImpl.java,v 1.8 2006/06/26 16:46:43 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def;


/**
 * This class represents a Partner Link Type's Role element. Roles contain Port
 * Types elements.
 */
public class AeRoleImpl implements IAeRole, IAeBPELExtendedWSDLConst
{
   /** The name of this Role */
   private String mName;

   /** The port type for this role which has a 1 to 1 relationship */
   private IAePortType mPortType;

   /**
    * Constructor. Creates a new Role with the given name. 
    * @param aName
    */
   public AeRoleImpl(String aName)
   {
      setName(aName);
   }

   /**
    * Get the name attribute value of this Role
    * @return String
    */
   public String getName()
   {
      return mName;
   }
   
   /**
    * Set the name attribute value of this Role
    * @param aName
    */
   public void setName(String aName)
   {
      mName = aName;
   }

   /**
    * Sets the PortType for this Role
    * @param aPortType the port type to be set
    */
   public void setPortType(IAePortType aPortType)
   {
      mPortType = aPortType;
   }

   /**
    * Gets the PortType for this Role.
    */
   public IAePortType getPortType()
   {
      return mPortType;
   }
}

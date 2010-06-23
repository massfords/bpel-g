//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/scope/AePartnerLinkValidator.java,v 1.8 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.scope; 

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;
import org.activebpel.rt.bpel.def.validation.IAePartnerLinkOperationUser;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.wsdl.def.IAePartnerLinkType;
import org.activebpel.rt.wsdl.def.IAeRole;

/**
 * model provides validation for the partnerLink def
 */
public class AePartnerLinkValidator extends AeBaseValidator
{
   /** used to determine if the plink is referenced */
   private boolean mReferenced = false;
   /** list of models that are using the plink */
   private List mUsers = new LinkedList();
   /** def that defines the myRole portType */
   private AeBPELExtendedWSDLDef mMyRolePortTypeDef;
   /** def that defines the partnerRole portType */
   private AeBPELExtendedWSDLDef mPartnerRolePortTypeDef;
   
   /**
    * ctor
    * @param aDef
    */
   public AePartnerLinkValidator(AePartnerLinkDef aDef)
   {
      super(aDef);
   }
   
   /**
    * Getter for the myRole port type def
    */
   protected AeBPELExtendedWSDLDef getMyRolePortTypeDef()
   {
      return mMyRolePortTypeDef;
   }
   
   /**
    * Setter for the myRole port type def
    * @param aDef
    */
   protected void setMyRolePortTypeDef(AeBPELExtendedWSDLDef aDef)
   {
      mMyRolePortTypeDef = aDef;
   }
   
   /**
    * Getter for the partnerRole port type def
    */
   protected AeBPELExtendedWSDLDef getPartnerRolePortTypeDef()
   {
      return mPartnerRolePortTypeDef;
   }
   
   /**
    * Setter for the partnerRole port type def
    * @param aDef
    */
   protected void setPartnerRolePortTypeDef(AeBPELExtendedWSDLDef aDef)
   {
      mPartnerRolePortTypeDef = aDef;
   }
   
   /**
    * Getter for the def
    */
   public AePartnerLinkDef getDef()
   {
      return (AePartnerLinkDef) getDefinition();
   }
   
   /**
    * Getter for the name
    */
   public String getName()
   {
      return getDef().getName();
   }
   
   /**
    * Adds a reference to this plink without specifying a role or operation
    */
   public void addReference()
   {
      mReferenced = true;
   }
   
   /**
    * Returns true if the plink is being referenced by one or more activities
    */
   protected boolean isReferenced()
   {
      return mReferenced || mUsers.size() > 0;
   }
   
   /**
    * Adds a partner link user
    * @param aPartnerLinkUser
    */
   public void addPartnerLinkUser(IAePartnerLinkOperationUser aPartnerLinkUser)
   {
      mUsers.add(aPartnerLinkUser);
   }
   
   /**
    * Validates:
    * 1. warning if not used
    * 2. error if plink type is not resolved
    * 3. error if plink has myRole that's not on the type
    * 4. error if plink has partnerRole that's not on the type
    * 5. validate all of the plink users (validates appropriate portType and operation)
    * 6. validate all of the plink fault users (validates appropriate portType, operation, and fault)
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      if (!isReferenced())
      {
         getReporter().reportProblem( BPEL_PARTNER_LINK_NOT_USED_CODE, 
                                    WARNING_PARTNER_LINK_NOT_USED,  
                                    new String[] { getDef().getName() }, getDef() );
      }
      
      // check that the partner link type exists
      if (getPartnerLinkType() == null)
      {
         addTypeNotFoundError(ERROR_PARTNER_LINK_TYPE_NOT_FOUND, getDef().getPartnerLinkTypeName());
         return;
      }
      
      // validate the roles, if defined.
      if (hasMyRole())
      {
         IAeRole role = getPartnerLinkType().findRole( getDef().getMyRole() );
         if ( role == null )
         {
            // role not found
            getReporter().reportProblem( BPEL_ROLE_NOT_FOUND_CODE,
                                       ERROR_ROLE_NOT_FOUND,
                                       new String[] { getDef().getMyRole(), getPartnerLinkType().getName() },
                                       getDef() );
         }
         else
         {
            setMyRolePortTypeDef(findPortType(role));
         }
      }

      if ( hasPartnerRole() )
      {
         IAeRole role = getPartnerLinkType().findRole( getDef().getPartnerRole() );
         if ( role == null )
         {
            // role not found
            getReporter().reportProblem( BPEL_ROLE_NOT_FOUND_CODE,
                                       ERROR_ROLE_NOT_FOUND,
                                       new String[] { getDef().getPartnerRole(), getPartnerLinkType().getName() },
                                       getDef() );
         }
         else
         {
            setPartnerRolePortTypeDef(findPortType(role));
         }
      }
      else
      {
         // if it doesn't have a partner role then it shouldn't have the 
         // intializePartnerRole attribute
         if (getDef().getInitializePartnerRole() != null)
         {
            // role not found
            getReporter().reportProblem( BPEL_INIT_PARTNER_ROLE_NOT_ALLOWED_CODE, 
                                          ERROR_INIT_PARTNER_ROLE_NOT_ALLOWED,
                                          new String[] { getDef().getName() },
                                          getDef() );
         }
      }
      
      validatePartnerLinkUsers();
   }
   
   /**
    * Gets the def for the given role
    * @param role
    */
   protected AeBPELExtendedWSDLDef findPortType(IAeRole role)
   {
      AeBPELExtendedWSDLDef def = AeWSDLDefHelper.getWSDLDefinitionForPortType( getValidationContext().getContextWSDLProvider(), role.getPortType().getQName() );
      if (def == null)
         addTypeNotFoundError(ERROR_PORT_TYPE_NOT_FOUND, role.getPortType().getQName());
      return def;
   }
   
   /**
    * Returns true if the def defines a myRole portType
    */
   protected boolean hasMyRole()
   {
      String role = getDef().getMyRole();
      return AeUtil.notNullOrEmpty( role );
   }
   
   /**
    * Returns true if the def defines a partnerRole portType
    */
   protected boolean hasPartnerRole()
   {
      String role = getDef().getPartnerRole();
      return AeUtil.notNullOrEmpty( role );
   }
   
   /**
    * Validates all of the plink users
    */
   protected void validatePartnerLinkUsers()
   {
      for(Iterator it=mUsers.iterator(); it.hasNext();)
      {
         IAePartnerLinkOperationUser user = (IAePartnerLinkOperationUser) it.next();
         validatePartnerLinkUser(user);
      }
   }
   
   /**
    * Validates:
    * 1. error if user has myRole without myRole on plink
    * 2. port type is valid for given role
    * 3. operation that matches input/output exists on port type
    * 
    * @param aUser
    */
   protected void validatePartnerLinkUser(IAePartnerLinkOperationUser aUser)
   {
      if (aUser.isMyRole())
      {
         if (!hasMyRole())
         {
            reportMissingRole(aUser, ERROR_PARTNER_LINK_MISSING_MYROLE);
            return;
         }
         
         validateRoleUsage(aUser, getDef().getMyRole());
      }
      
      if (aUser.isPartnerRole())
      {
         if (!hasPartnerRole())
         {
            reportMissingRole(aUser, ERROR_PARTNER_LINK_MISSING_PARTNERROLE);
            return;
         }

         validateRoleUsage(aUser, getDef().getPartnerRole());
      }
   }

   /**
    * Part of the plink user validation, check to make sure that the portType and operation are valid
    * @param aUser
    * @param aRoleName
    */
   protected void validateRoleUsage(IAePartnerLinkOperationUser aUser, String aRoleName)
   {
      IAeRole role = getPartnerLinkType().findRole(aRoleName);
      validatePortType(aUser, role);
   }
   
   /**
    * Reports an error if the port type is not valid. A valid port type is one where the 
    * wsio activity's declared port type matches the role's port type.
    * @param aUser
    * @param aRole
    */
   protected void validatePortType(IAePartnerLinkOperationUser aUser, IAeRole aRole)
   {
      if (aRole == null || aRole.getPortType() == null)
      {
         getReporter().reportProblem( BPEL_ROLE_HAS_NO_PORTTYPE_CODE,
                                 ERROR_ROLE_HAS_NO_PORTTYPE,
                                 new String[] { getDef().getMyRole(), getPartnerLinkType().getName() },
                                 getDefinition() );
      }
      else if (!portTypesMatch(aUser.getPortType(), aRole.getPortType().getQName()))
      {
         String strRolePortType = aRole.getPortType().getQName().toString();
         String strDefPortType = aUser.getPortType() == null ? "": aUser.getPortType().toString(); //$NON-NLS-1$
         getReporter().reportProblem( BPEL_PORTTYPE_MISMATCH_CODE,
                                 ERROR_PORTTYPE_MISMATCH,
                                 new String[] { getDef().getName(), aRole.getName(), strRolePortType, strDefPortType},
                                 aUser.getDefinition() );
      }
   }
   
   /**
    * Gets the myRole port type for the plink or null if not present or not resolveable
    */
   public QName getMyRolePortType()
   {
      QName portType = null;
      if (getPartnerLinkType() != null)
      {
         portType = getDef().getMyRolePortType();
      }
      return portType;
   }
   
   /**
    * Gets the partnerRole port type for the plink or null if not present or not resolvable
    */
   public QName getPartnerRolePortType()
   {
      QName portType = null;
      if (getPartnerLinkType() != null)
      {
         portType = getDef().getPartnerRolePortType();
      }
      return portType;
   }

   /**
    * Returns true if the declared port type matches the role's port type. 
    * @param aDeclaredPortType
    * @param aRolePortType
    */
   protected boolean portTypesMatch(QName aDeclaredPortType, QName aRolePortType)
   {
      if (aDeclaredPortType == null && isPortTypeOptional())
      {
         return true;
      }
      else
      {
         return AeUtil.compareObjects(aDeclaredPortType, aRolePortType);
      }
   }
   
   /**
    * Reports a missing role as an error message
    * @param aUser
    * @param aErrorMessage
    */
   protected void reportMissingRole(IAePartnerLinkOperationUser aUser, String aErrorMessage)
   {
      getReporter().reportProblem( BPEL_MISSING_ROLE_CODE,
                                    aErrorMessage,
                                    new String[] { getDef().getName() },
                                    aUser.getDefinition() );
   }

   /**
    * Getter for the plink type
    */
   protected IAePartnerLinkType getPartnerLinkType()
   {
      return getDef().getPartnerLinkType();
   }
}
 
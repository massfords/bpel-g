//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/IAePartnerLinkOperationUser.java,v 1.1 2006/08/16 22:07:28 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation; 

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.validation.activity.IAeCorrelationUser;

/**
 * Interface for a model that uses a partner link.
 */
public interface IAePartnerLinkOperationUser extends IAeValidator, IAeCorrelationUser
{
   /**
    * True if the model makes use of the partnerRole of the plink
    */
   public boolean isPartnerRole();
   
   /**
    * True if the model makes use of the myRole role of the plink
    */
   public boolean isMyRole();

   /**
    * Getter for the portType of the model
    */
   public QName getPortType();
   
   /**
    * Getter for the operation of the model
    */
   public String getOperation();
}
 
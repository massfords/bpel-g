//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/IAeIdentityProperty.java,v 1.1 2007/02/20 17:47:40 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity;

/**
 * Property entry of an identity.
 */
public interface IAeIdentityProperty
{

   /**
    * Property name.
    * @return returns property name,
    */
   public String getName();
   
   /**
    * Property value.
    * @return returns property value as a string.
    */
   public String getValue();
}

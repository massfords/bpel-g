//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/IAeValidator.java,v 1.3 2007/09/26 02:21:03 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation; 

import org.activebpel.rt.xml.def.AeBaseXmlDef;

/**
 * interface for the model classes.
 */
public interface IAeValidator
{
   /**
    * performs any validation for the model
    */
   public void validate();
   
   /**
    * Getter for the model's definition
    */
   public AeBaseXmlDef getDefinition();
}
 
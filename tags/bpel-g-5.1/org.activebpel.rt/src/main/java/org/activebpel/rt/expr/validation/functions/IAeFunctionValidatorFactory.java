//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/expr/validation/functions/IAeFunctionValidatorFactory.java,v 1.2 2008/02/13 00:22:58 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.expr.validation.functions; 

import javax.xml.namespace.QName;

/**
 * This interface must be implemented by any classes desiring to be a function validator factory.  
 * An function validator factory is responsible for creating appropriate validators for the BPEL namespace
 * and Function QName. 
 */
public interface IAeFunctionValidatorFactory
{
   /**
    * Getter for the validator given the bpel namespace and function def.
    * @param aBpelNamespace
    * @param aQName
    * 
    */
   public IAeFunctionValidator getValidator(String aBpelNamespace, QName aQName);

}
 

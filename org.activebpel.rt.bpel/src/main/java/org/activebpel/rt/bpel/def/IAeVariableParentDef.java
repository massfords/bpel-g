//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/IAeVariableParentDef.java,v 1.1 2006/09/11 23:06:28 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def; 

/**
 * interface for a def that defines a variable 
 */
public interface IAeVariableParentDef
{
   /**
    * Returns the var def if this parent has the variable.
    * @param aVariableName
    */
   public AeVariableDef getVariableDef(String aVariableName);
}
 
//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/IAeVariableContainer.java,v 1.5 2008/02/17 21:37:08 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity; 

import java.util.Iterator;

import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.IAeBpelObject;

/**
 * Provides method for adding a variable to an impl. Added to support special variables on non-scope
 * activities like the catch that defines/declares a variable. 
 */
public interface IAeVariableContainer
{
   /**
    * Gets the variable by its name
    * @param aVariableName
    */
   public IAeVariable findVariable(String aVariableName);
   
   /**
    * Adds the variable
    * @param aVariable
    */
   public void addVariable(IAeVariable aVariable);
   
   /**
    * Gets an iterator over the IAeVariables within this container
    */
   public Iterator iterator();
   
   /**
    * Initializes the variables in the container
    */
   public void initialize() throws AeBpelException;
   
   /**
    * Gets the parent of the variables container
    */
   public IAeBpelObject getParent();
}
 
//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/IAeDynamicScopeParent.java,v 1.2 2006/11/06 23:36:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl; 

import java.util.List;

import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.impl.visitors.IAeVisitable;

/**
 * Interface for BPEL constructs that contain scopes that are created through
 * the execution of an enclosing activity. In each case, the parent will create
 * zero or more scope instances during its normal execution. The parallel forEach
 * will create the proper number of scope instances in order to execute its loop.
 * The onEvent in WS-BPEL will create one instance each time its message arrives. 
 * The onAlarm will create one instance each time a repeatEvery expression causes
 * the onAlarm to execute.
 */
public interface IAeDynamicScopeParent extends IAeActivityParent, IAeVisitable
{
   /**
    * Gets the list of children. 
    */
   public List getChildren();
   
   /**
    * Gets the child scope def. This is the def that is visited to produce the
    * dynamic scope instance.
    */
   public AeActivityScopeDef getChildScopeDef();
   
   /**
    * Gets the list of compensatable children. 
    */
   public List getCompensatableChildren();
   
   /**
    * Setter for the instance value
    * @param aInstanceValue
    */
   public void setInstanceValue(int aInstanceValue);
   
   /**
    * Getter for the instance value
    */
   public int getInstanceValue();
}
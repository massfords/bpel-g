//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/IAeCorrelationPatternIO.java,v 1.1 2006/07/14 15:46:50 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io; 

import org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef;

/**
 * Provides the legal values for the correlation initiation/validation patterns 
 */
public interface IAeCorrelationPatternIO
{
   /**
    * Converts the string to a pattern type
    * @param aValue
    */
   public AeCorrelationDef.AeCorrelationPatternType fromString(String aValue);
   
   /**
    * Converts a pattern type to a string
    * @param aType
    */
   public String toString(AeCorrelationDef.AeCorrelationPatternType aType);
}
 
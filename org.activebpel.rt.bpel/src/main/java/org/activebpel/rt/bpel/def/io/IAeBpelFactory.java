//$Header$
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io;

import org.activebpel.rt.xml.def.io.IAeDefRegistry;

/**
 * Factory for reading and writing BPEL. Each factory deals with a specific version of 
 * BPEL. The factories also contain a registry that maps elements to def objects and
 * def objects to element names. These registries can be overridden if desired. 
 */
public interface IAeBpelFactory
{
   /** Feature ID for writing the portType attribute for WSIO activities. */
   public static final String FEATURE_ID_WRITE_PORTTYPE = "org.activebpel.rt.bpel_writePortType"; //$NON-NLS-1$
   
   /**
    * Gets an object capable of reading BPEL xml into our definition objects.
    */
   public IAeBpelReader createBpelReader();
   
   /**
    * Gets an object capable of writing our def objects into BPEL xml.
    */
   public IAeBpelWriter createBpelWriter();
   
   /**
    * Getter for the currently installed BPEL registry
    */
   public IAeDefRegistry getBpelRegistry();
   
   /**
    * Sets a feature name/value pair.
    * 
    * @param aName feature name key.
    * @param aFlag feature boolean value.
    */
   public void setFeature(String aName, boolean aFlag);
}
 

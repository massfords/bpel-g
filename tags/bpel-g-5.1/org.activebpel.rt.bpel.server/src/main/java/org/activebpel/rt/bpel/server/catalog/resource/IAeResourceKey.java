//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/catalog/resource/IAeResourceKey.java,v 1.1 2006/08/04 17:57:53 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.catalog.resource;

public interface IAeResourceKey
{
   /**
    * @return Returns the location.
    */
   public String getLocation();

   /**
    * @return Returns the type.
    */
   public String getTypeURI();

   /**
    * Return true if this is a wsdl entry.
    */
   public boolean isWsdlEntry();
   
   /**
    * Return true if this is a schema entry.
    */
   public boolean isSchemaEntry();
}
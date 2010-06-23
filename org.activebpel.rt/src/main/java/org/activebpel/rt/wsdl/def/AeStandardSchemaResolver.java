//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/AeStandardSchemaResolver.java,v 1.4 2007/12/14 01:03:41 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def;

import java.io.InputStream;

import org.activebpel.rt.AeException;
import org.activebpel.rt.AeMessages;
import org.activebpel.rt.schemas.AeStandardSchemas;
import org.xml.sax.InputSource;

/**
 * This implemenation of a standard schema resolver knows how to resolve standard schemas
 * from within the context of the AWF Designer.
 */
public class AeStandardSchemaResolver implements IAeStandardSchemaResolver
{
   /**
    * Creates a new standard schema resolver.
    */
   public static IAeStandardSchemaResolver newInstance()
   {
      return new AeStandardSchemaResolver();
   }

   /**
    * Default constructor.
    */
   public AeStandardSchemaResolver()
   {
   }

   /**
    * @see org.activebpel.rt.wsdl.def.IAeStandardSchemaResolver#resolve(java.lang.String)
    */
   public InputSource resolve(String aNamespace)
   {
      try
      {
         InputStream is = AeStandardSchemas.getStandardSchema(aNamespace);
         if (is != null)
         {
            return new InputSource(is);
         }
      }
      catch (Throwable t)
      {
         AeException.logError(t, AeMessages.getString("AeStandardSchemaResolver.ERROR_0")); //$NON-NLS-1$
      }
      return null;
   }

   /**
    * @see org.activebpel.rt.wsdl.def.IAeStandardSchemaResolver#canResolve(java.lang.String)
    */
   public boolean canResolve(String aNamespace)
   {
      return AeStandardSchemas.canResolve(aNamespace);
   }
}

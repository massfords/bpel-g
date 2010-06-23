// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/AeImportDef.java,v 1.4 2006/07/18 20:02:47 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;
import org.activebpel.rt.wsdl.def.IAeBPELExtendedWSDLConst;

/**
 * Definition of a ws-bpel 2.0 import.
 */
public class AeImportDef extends AeBaseDef
{
   /** The import's 'namespace' attribute. */
   private String mNamespace;
   /** The import's 'location' attribute. */
   private String mLocation;
   /** The import's 'importType' attribute. */
   private String mImportType;
   
   /**
    * Default c'tor.
    */
   public AeImportDef()
   {
      super();
   }

   /**
    * @return Returns the importType.
    */
   public String getImportType()
   {
      return mImportType;
   }

   /**
    * @param aImportType The importType to set.
    */
   public void setImportType(String aImportType)
   {
      mImportType = aImportType;
   }

   /**
    * @return Returns the location.
    */
   public String getLocation()
   {
      return mLocation;
   }

   /**
    * @param aLocation The location to set.
    */
   public void setLocation(String aLocation)
   {
      mLocation = aLocation;
   }

   /**
    * @return Returns the namespace.
    */
   public String getNamespace()
   {
      return mNamespace;
   }

   /**
    * @param aNamespace The namespace to set.
    */
   public void setNamespace(String aNamespace)
   {
      mNamespace = aNamespace;
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
   
   /**
    * Convenience method to check if the import is a wsdl import.
    */
   public boolean isWSDL()
   {
      return IAeBPELExtendedWSDLConst.WSDL_NAMESPACE.equals(getImportType());
   }
   
   /**
    * Convenience method to check if the import is a schema import.
    */
   public boolean isSchema()
   {
      return IAeConstants.W3C_XML_SCHEMA.equals(getImportType());
   }
}

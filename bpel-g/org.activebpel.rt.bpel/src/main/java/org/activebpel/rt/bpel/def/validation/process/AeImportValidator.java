//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/process/AeImportValidator.java,v 1.4.2.1 2008/04/21 16:09:44 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.process; 

import java.net.URI;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.AeImportDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;
import org.activebpel.rt.util.AeUtil;

/**
 * Validates the &lt;imports&gt; for the process.
 */
public class AeImportValidator extends AeBaseValidator
{
   /**
    * ctor takes the def
    * @param aDef
    */
   public AeImportValidator(AeImportDef aDef)
   {
      super(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      AeImportDef importDef =  (AeImportDef)getDefinition();
      
      if(AeUtil.isNullOrEmpty(importDef.getImportType()))
      {
         getReporter().reportProblem( BPEL_NO_IMPORT_TYPE_CODE,
                                       AeMessages.getString("AeImportValidator.NO_IMPORT_TYPE"), //$NON-NLS-1$
                                       null, 
                                       getDefinition() ); 
      }
      else
      {
         try
         {
            URI uri = new URI(importDef.getImportType());
            if(! uri.isAbsolute())
               getReporter().reportProblem( BPEL_IMPORT_TYPE_NOT_ABSOLUTE_CODE,
                                           AeMessages.getString("AeImportValidator.IMPORT_TYPE_NOT_ABSOLUTE"), //$NON-NLS-1$
                                           new Object[] {importDef.getImportType() }, 
                                           getDefinition() ); 
         }
         catch (Exception ex)
         {
            getReporter().reportProblem( BPEL_IMPORT_TYPE_INVALID_URI_CODE,
                                         AeMessages.getString("AeImportValidator.IMPORT_TYPE_INVALID_URI"), //$NON-NLS-1$
                                         new Object[] {importDef.getImportType() }, 
                                         getDefinition() ); 
         }
      }
      
      // if the import references a namespace then validate that the location is associated with that namespace
      if(AeUtil.notNullOrEmpty(importDef.getNamespace()) && AeUtil.notNullOrEmpty(importDef.getLocation()))
      {
         // TODO (cck) add check that location matches namespace
      }
   }

}
 
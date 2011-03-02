// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/convert/AeAbstractBpelDefConverter.java,v 1.3 2006/12/14 22:40:23 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.convert;

import org.activebpel.rt.bpel.def.AeProcessDef;

/**
 * An abstract base class that contains some common functionality for all BPEL Def converters.
 */
public abstract class AeAbstractBpelDefConverter implements IAeBpelDefConverter
{
   /** The process def we are converting. */
   private AeProcessDef mProcessDef;

   /**
    * Default c'tor.
    */
   public AeAbstractBpelDefConverter()
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.convert.IAeBpelDefConverter#convert(org.activebpel.rt.bpel.def.AeProcessDef)
    */
   public void convert(AeProcessDef aProcessDef)
   {
      setProcessDef(aProcessDef);
      convertProcessNamespace();
      doConversion();
   }
   
   /**
    * Converts the namespace.
    */
   protected void convertProcessNamespace()
   {
      String newNamespace = getNewBpelNamespace();
      String preferredPrefix = getNewBpelNamespacePrefix();
      getProcessDef().setNamespace(newNamespace);
      getProcessDef().addNamespace("", newNamespace); //$NON-NLS-1$
      getProcessDef().allocateNamespace(preferredPrefix, newNamespace);      
   }

   /**
    * Returns the BPEL namespace of the target format (the BPEL version we are converting TO).
    */
   protected abstract String getNewBpelNamespace();

   /**
    * Returns the <strong>preferred</strong> BPEL namespace prefix for the BPEL namespace 
    * we are converting TO.  For example, if we are converting to WS-BPEL 2.0, this would 
    * return "bpel".  If we are converting to BPEL4WS 1.1, this would return "bpws".
    */
   protected abstract String getNewBpelNamespacePrefix();
   
   /**
    * Called to actually do the conversion steps that will be specific to a particular type of
    * conversion (e.g. from 1.1 to 2.0).
    */
   protected abstract void doConversion();

   /**
    * @return Returns the processDef.
    */
   protected AeProcessDef getProcessDef()
   {
      return mProcessDef;
   }

   /**
    * @param aProcessDef The processDef to set.
    */
   protected void setProcessDef(AeProcessDef aProcessDef)
   {
      mProcessDef = aProcessDef;
   }
}

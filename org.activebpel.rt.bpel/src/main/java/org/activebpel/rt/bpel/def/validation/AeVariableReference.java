// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/AeVariableReference.java,v 1.1 2008/02/27 20:48:28 EWittmann Exp $
// ///////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc. Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
// ///////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.validation;

import org.activebpel.rt.xml.def.AeBaseXmlDef;

/**
 * Represents a reference to a BPEL variable in an extension activity, extension
 * element, or extension attribute.
 */
public class AeVariableReference
{
   /** The name of the variable being referenced. */
   private String mVariableName;
   /** The mode of the reference:  wsio_read, wsio_write, etc */
   private int mMode;
   /** The def that contains the reference - used for reporting errors. */
   private AeBaseXmlDef mDef;
   
   /**
    * C'tor.
    * 
    * @param aVariableName
    * @param aMode
    * @param aDef
    */
   public AeVariableReference(String aVariableName, int aMode, AeBaseXmlDef aDef)
   {
      setVariableName(aVariableName);
      setMode(aMode);
      setDef(aDef);
   }

   /**
    * @return Returns the variableName.
    */
   public String getVariableName()
   {
      return mVariableName;
   }

   /**
    * @param aVariableName the variableName to set
    */
   public void setVariableName(String aVariableName)
   {
      mVariableName = aVariableName;
   }

   /**
    * @return Returns the mode.
    */
   public int getMode()
   {
      return mMode;
   }

   /**
    * @param aMode the mode to set
    */
   public void setMode(int aMode)
   {
      mMode = aMode;
   }

   /**
    * @return Returns the def.
    */
   public AeBaseXmlDef getDef()
   {
      return mDef;
   }

   /**
    * @param aDef the def to set
    */
   public void setDef(AeBaseXmlDef aDef)
   {
      mDef = aDef;
   }
}

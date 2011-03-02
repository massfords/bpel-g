//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/AeSchemaHexBinary.java,v 1.3 2006/10/12 20:04:51 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.schema; 

import org.activebpel.rt.AeMessages;
import org.activebpel.rt.util.AeUtil;

/**
 * Wrapper object for xsd:hexBinary simple data type. This ensures that Axis serializes the data correctly.
 */
public class AeSchemaHexBinary extends AeSchemaWrappedStringType
{
   /** Parsed-out binary data. */
   private byte [] mBinaryData;
   
   /**
    * Ctor accepts the hex binary value.
    * 
    * @param aHexBinaryString
    */
   public AeSchemaHexBinary(String aHexBinaryString)
   {
      super(aHexBinaryString);
      
      parseHexString(aHexBinaryString);
   }

   /**
    * Parses the hex data string into its binary form.
    * 
    * @param aHexBinaryString
    */
   protected void parseHexString(String aHexBinaryString)
   {
      if (AeUtil.isNullOrEmpty(aHexBinaryString) || (aHexBinaryString.length() % 2) == 1)
      {
         throw new AeSchemaTypeParseException(AeMessages.getString("AeSchemaHexBinary.ParseFailureError")); //$NON-NLS-1$
      }

      try
      {
         byte [] data = new byte[aHexBinaryString.length() * 2];
         for (int i = 0; i < aHexBinaryString.length(); i+=2)
         {
            char hexDigit1 = aHexBinaryString.charAt(i);
            char hexDigit2 = aHexBinaryString.charAt(i + 1);
            String hexNumber = String.valueOf(hexDigit1) + String.valueOf(hexDigit2);
            byte hexValue = (byte) Integer.parseInt(hexNumber, 16);
            data[i / 2] = hexValue;
         }
         setBinaryData(data);
      }
      catch (NumberFormatException ex)
      {
         throw new AeSchemaTypeParseException(AeMessages.getString("AeSchemaHexBinary.ParseFailureError")); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.xml.schema.IAeSchemaType#accept(org.activebpel.rt.xml.schema.IAeSchemaTypeVisitor)
    */
   public void accept(IAeSchemaTypeVisitor aVisitor)
   {
      aVisitor.visit(this);
   }

   /**
    * @return Returns the binaryData.
    */
   public byte[] getBinaryData()
   {
      return mBinaryData;
   }

   /**
    * @param aBinaryData The binaryData to set.
    */
   protected void setBinaryData(byte[] aBinaryData)
   {
      mBinaryData = aBinaryData;
   }
}
 
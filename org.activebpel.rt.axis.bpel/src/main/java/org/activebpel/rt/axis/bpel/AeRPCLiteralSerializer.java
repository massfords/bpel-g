// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/AeRPCLiteralSerializer.java,v 1.4 2006/11/07 20:14:58 tzhang Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.ser.ElementSerializer;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;

/**
 * Custom serializer for axis that handles complex types over rpc literal.
 */
public class AeRPCLiteralSerializer extends ElementSerializer
{
   /**
    * Constructor
    */
   public AeRPCLiteralSerializer()
   {
   }

   /**
    * @see org.apache.axis.encoding.Serializer#serialize(javax.xml.namespace.QName, org.xml.sax.Attributes, java.lang.Object, org.apache.axis.encoding.SerializationContext)
    */
   public void serialize(QName aName, Attributes aAttributes, Object aValue, SerializationContext aContext)
      throws IOException
   {
      try
      {
         Document document = (Document) aValue;
         aContext.setWriteXMLType(null);
         aContext.writeDOMElement(document.getDocumentElement());
      }
      catch(Throwable t)
      {
         AeException.logError(t, t.getLocalizedMessage());
         if (t instanceof IOException)
            throw (IOException)t;
         else
            throw new IOException(t.getLocalizedMessage());
      }
   }
}

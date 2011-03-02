//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/AeRPCLiteralDeserializer.java,v 1.3 2006/03/14 18:16:39 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//             PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel;

import org.activebpel.rt.AeException;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.message.MessageElement;
import org.xml.sax.SAXException;

/**
 * Custom deserializer for handling complex types over RPC literal. These types will be
 * deserialized into their literal xml.
 * 
 * RPC Literal deserialization is simpler than Encoded since the inbound message
 * should be complete with namespaces which identify the types. There is also
 * no flattening of the document structure through multiRef encoding. As such,
 * it's a pretty straightforward implementation of copying the SOAP MessageElements
 * into a new DOM.
 * 
 * One might think that this would be as simple as importing the MessageElement
 * into a new Document but this unfortunately does not copy all of the namespace
 * declarations over. As such, we walk the MessageElements recursively and copy
 * their attributes and namespace declarations manually, relying on some facilities
 * in the base class for copying attributes and such.
 * 
 */
public class AeRPCLiteralDeserializer extends AeRPCEncodedDeserializer
{
   // TODO (MF) This class no longer needs to extend AeRPCEncodedDeserializer
   /**
    * Constructor for deserializer.
    */
   public AeRPCLiteralDeserializer(IAeTypesContext aTypesContext)
   {
      super(aTypesContext);
   }
   
   /**
    * @see org.apache.axis.encoding.Deserializer#onEndElement(java.lang.String, java.lang.String, org.apache.axis.encoding.DeserializationContext)
    */
   public void onEndElement(String aNamespace, String aLocalName,
                                  DeserializationContext aContext)
       throws SAXException
   {
      MessageElement msgElem = aContext.getCurElement();
      try
      {
         value = msgElem.getAsDOM().getOwnerDocument();
      }
      catch (Throwable t)
      {
         AeException.logError(t, AeMessages.getString("AeLiteralDeserializer.ERROR_10")); //$NON-NLS-1$
         if (t instanceof Exception)
            throw new SAXException((Exception)t);
         throw new SAXException(t.getLocalizedMessage());
      }
   }
}

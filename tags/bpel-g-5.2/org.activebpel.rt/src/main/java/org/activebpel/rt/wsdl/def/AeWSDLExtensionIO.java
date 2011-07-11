//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/AeWSDLExtensionIO.java,v 1.4 2008/02/17 21:09:21 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def; 

import java.io.PrintWriter;

import javax.wsdl.Definition;
import javax.wsdl.extensions.ExtensionDeserializer;
import javax.wsdl.extensions.ExtensionSerializer;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Base class for reading/writing BPEL WSDL extensions elements 
 */
public abstract class AeWSDLExtensionIO implements ExtensionSerializer, ExtensionDeserializer
{
   /**
    * Creates an element with the given QName
    * @param aQname
    */
   protected Element createElement(QName aQname)
   {
      AeXMLParserBase parser = new AeXMLParserBase();
      parser.setValidating(false);
   
      // Build the Partner Link Type DOM element and populate.
      Document dock = parser.createDocument();
      Element partnerLinkElement = dock.createElementNS( aQname.getNamespaceURI(), aQname.getLocalPart());
      return partnerLinkElement;
   }

   /**
    * Converts the element to a string and writes it to the writer.
    * @param aElement
    * @param aWriter
    * @throws TransformerFactoryConfigurationError
    */
   protected void writeElement(Element aElement, PrintWriter aWriter)
   {
      // Output the temporary element to a string and print into the writer
      aWriter.print(AeXMLParserBase.documentToString(aElement, true));
   }

   /**
    * Attempts to get the namespace to use from the Definition or creates a new one locally on the 
    * element if not found.
    * @param aNamespace
    * @param aDefinition
    * @param aExtensionElement
    * @param aPreferredPrefix
    */
   protected String getPrefixOrCreateLocally(String aNamespace, Definition aDefinition, Element aExtensionElement, String aPreferredPrefix)
   {
      String prefix = aDefinition.getPrefix(aNamespace);
      if (prefix == null)
      {
         prefix = AeXmlUtil.getOrCreatePrefix(aExtensionElement, aNamespace, aPreferredPrefix, false);
      }
      return prefix;
   }

   /**
    * Converts the QName to a string using the prefix from the Definition
    * @param aDefinition
    * @param aQname
    */
   protected String toString(Definition aDefinition, QName aQname)
   {
      String lTypeQName 
         = aDefinition.getPrefix( aQname.getNamespaceURI()) 
           + ":" + aQname.getLocalPart(); //$NON-NLS-1$
      return lTypeQName;
   } 
}
 
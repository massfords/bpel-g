//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/IAeBPELWSDLExtensionIOFactory.java,v 1.1 2007/08/13 17:46:30 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def; 

import javax.wsdl.extensions.ExtensionDeserializer;
import javax.wsdl.extensions.ExtensionSerializer;
import javax.xml.namespace.QName;

/**
 * Interface for factory that provides the wsdl4j impls for reading/writing bpel extensions to wsdl   
 */
public interface IAeBPELWSDLExtensionIOFactory
{
   /**
    * Getter for the qname used for the partnerLinkType element
    */
   public QName getPartnerLinkTypeQName();
   
   /**
    * Getter for the qname used for the property element
    */
   public QName getPropertyQName();
   
   /**
    * Getter for the qname used for the propertyAlias element
    */
   public QName getPropertyAliasQName();
   
   /**
    * Getter for the partnerLinkType serializer
    */
   public ExtensionSerializer getPartnerLinkTypeSerializer();

   /**
    * Getter for the partnerLinkType deserializer
    */
   public ExtensionDeserializer getPartnerLinkTypeDeserializer();
   
   /**
    * Getter for the property serializer
    */
   public ExtensionSerializer getPropertySerializer();
   
   /**
    * Getter for the property deserializer
    */
   public ExtensionDeserializer getPropertyDeserializer();

   /**
    * Getter for the propertyAlias serializer
    */
   public ExtensionSerializer getPropertyAliasSerializer();
   
   /**
    * Getter for the propertyAlias deserializer
    */
   public ExtensionDeserializer getPropertyAliasDeserializer();
}
 
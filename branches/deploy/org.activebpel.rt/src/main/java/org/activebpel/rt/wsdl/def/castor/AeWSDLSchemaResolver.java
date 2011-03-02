//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/castor/AeWSDLSchemaResolver.java,v 1.3 2007/04/23 14:04:31 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def.castor;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.xml.WSDLLocator;

import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.def.IAeStandardSchemaResolver;
import org.exolab.castor.net.URIException;
import org.exolab.castor.net.URILocation;
import org.w3c.dom.Element;


/**
 * The WSDL Schema resolver class is used to resolve schemas within a single WSDL file.  It
 * is possible to construct a WSDL file with multiple &lt;schema&gt; elements, each with
 * different target namespaces.  This resolver is used when one of these schemas tries to
 * import one of the other schemas in the same WSDL.  This is done by using a schema
 * &lt;import&gt; element with a namespace attribute but no schemaLocation attribute.
 */
public class AeWSDLSchemaResolver extends AeURIResolver
{
   /** the definition we are resolving schemas for. */
   protected Definition mWsdlDefinition;

   /**
    * Constructs a WSDL schema resolver which will use the given wsdl locator, definition, and standard resolver.
    * 
    * @param aLocator
    * @param aWsdlDefinition
    * @param aStandardResolver
    */
   public AeWSDLSchemaResolver(WSDLLocator aLocator, Definition aWsdlDefinition, IAeStandardSchemaResolver aStandardResolver)
   {
      super(aLocator, aStandardResolver);
      setWsdlDefinition(aWsdlDefinition);
   }

   /**
    * @see org.exolab.castor.net.URIResolver#resolveURN(java.lang.String)
    */
   public URILocation resolveURN(String aURN) throws URIException
   {
      URILocation loc = getLocalSchemaURILocation(aURN); 
      if (loc == null)
         loc = super.resolveURN(aURN);
      return loc;
   }

   /**
    * This method will return a uri location for schema element with a target namespace that 
    * matches the passed in href. It checks the direct schemas in the wsdl's type areas and direct 
    * imported wsdl types areas as well.
    * 
    * @param aHref
    */
   protected URILocation getLocalSchemaURILocation(String aHref)
   {
      Element schemaElem = getSchemaElement(aHref, getWsdlDefinition());
      if(schemaElem != null)
         return new AeWSDLSchemaURILocation(schemaElem, getWsdlDefinition().getDocumentBaseURI());
      
      // not found as a direct schema in types area is it in a direct imported schema types area
      for(Iterator iter=getWsdlDefinition().getImports().values().iterator(); iter.hasNext(); )
      {
         Object impObj = iter.next(); 
         Vector vecImp = (Vector)impObj;
         for(Enumeration en = vecImp.elements(); en.hasMoreElements(); )
         {
            Import imp = (Import)en.nextElement();
            Definition def = imp.getDefinition(); 
            if(def != null)
            {
               schemaElem = getSchemaElement(aHref, def);
               if(schemaElem != null)
                  return new AeWSDLSchemaURILocation(schemaElem, def.getDocumentBaseURI());
            }
         }
      }
      return null;
   }

   /**
    * This method will return a schema element with a target namespace that matches the passed
    * in href.
    * 
    * @param aHref
    * @return element clone if found, null if not
    */
   protected Element getSchemaElement(String aHref, Definition aWsdlDefinition)
   {
      // short return if no embedded schemas in this definition
      if(aWsdlDefinition.getTypes() == null || aWsdlDefinition.getTypes().getExtensibilityElements() == null)
         return null;

      // find the extension element in the defs type area and return a clone of it
      for (Iterator it = aWsdlDefinition.getTypes().getExtensibilityElements().iterator(); it.hasNext();)
      {
         UnknownExtensibilityElement extElement = (UnknownExtensibilityElement)it.next();
         if("schema".equals(extElement.getElement().getLocalName())) //$NON-NLS-1$
         {
            String ns = extElement.getElement().getAttribute("targetNamespace"); //$NON-NLS-1$
            if (AeUtil.compareObjects(ns, aHref))
               return AeSchemaParserUtil.extractSchemaElement(extElement);
         }
      }
      
      // href is not a target namespace for the embedded schemas
      return null;
   }

   /**
    * @return the wsdlDefinition
    */
   protected Definition getWsdlDefinition()
   {
      return mWsdlDefinition;
   }

   /**
    * @param aWsdlDefinition the wsdlDefinition to set
    */
   protected void setWsdlDefinition(Definition aWsdlDefinition)
   {
      mWsdlDefinition = aWsdlDefinition;
   }

}

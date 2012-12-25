//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/AeWSBPELPropertyIO.java,v 1.2 2006/06/26 16:46:43 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def; 

import javax.wsdl.Definition;

import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Element;

/**
 * Reads and writes WS BPEL property elements
 */
public class AeWSBPELPropertyIO extends AePropertyIO
{
   /**
    * @see org.activebpel.rt.wsdl.def.AePropertyIO#readPropertyData(org.w3c.dom.Element, org.activebpel.rt.wsdl.def.AePropertyImpl)
    */
   protected void readPropertyData(Element aPropertyElem, AePropertyImpl aProp)
   {
      if (aPropertyElem.hasAttribute(ELEMENT_TYPE_ATTRIB))
      {
         aProp.setElementName(AeXmlUtil.createQName(aPropertyElem, aPropertyElem.getAttribute(ELEMENT_TYPE_ATTRIB)));
      }
      else
      {
         super.readPropertyData(aPropertyElem, aProp);
      }
   }

   /**
    * Writes the property's type or element QName to 
    * @see org.activebpel.rt.wsdl.def.AePropertyIO#writePropertyData(org.activebpel.rt.wsdl.def.IAeProperty, javax.wsdl.Definition, org.w3c.dom.Element)
    */
   protected void writePropertyData(IAeProperty aProperty, Definition aDefinition, Element aPropElement)
   {
      if (aProperty.getElementName() != null)
      {
         aPropElement.setAttribute(ELEMENT_TYPE_ATTRIB, toString(aDefinition, aProperty.getElementName()));
      }
      else
      {
         super.writePropertyData(aProperty, aDefinition, aPropElement);
      }
   }
}
 
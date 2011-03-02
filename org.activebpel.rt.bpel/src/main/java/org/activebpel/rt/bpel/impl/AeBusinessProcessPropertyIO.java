//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeBusinessProcessPropertyIO.java,v 1.3 2008/01/17 17:32:35 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import java.util.Map;

import org.activebpel.rt.bpel.impl.fastdom.AeFastElement;
import org.activebpel.rt.bpel.impl.fastdom.AeFastText;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Element;

/**
 * Utility methods for serializing <code>IAeBusinessProcessProperty</code> to an
 * <code>AeFastElement</code> and deserializing from the element back to the
 * object.
 */
public class AeBusinessProcessPropertyIO implements IAeImplStateNames
{
   
   //----------[ serialization methods ]----------------------------------------
   
   /**
    * Serialize to <code>AeFastElement</code>.
    * 
    * @param aName
    * @param aValue
    */
   public static AeFastElement getBusinessProcessPropertyElement( String aName, String aValue )
   {
      AeFastElement propertyElement = new AeFastElement( STATE_PROCESSPROPERTY );
      propertyElement.setAttribute( STATE_NAME, aName );
      AeFastText valueNode = new AeFastText( aValue );
      propertyElement.appendChild( valueNode );
      return propertyElement;
   }
   
   //----------[ deserialization methods ]--------------------------------------

   /**
    * Deserialize the element to it's name/value pair and add it to the 
    * given <code>Map</code> arg.
    * 
    * @param aElement
    * @param aMap
    */
   public static void extractBusinessProcessProperty( Element aElement, Map aMap )
   {
      String name = aElement.getAttribute( STATE_NAME );
      String value = AeXmlUtil.getText( aElement );
      aMap.put( name, value );
   }
}

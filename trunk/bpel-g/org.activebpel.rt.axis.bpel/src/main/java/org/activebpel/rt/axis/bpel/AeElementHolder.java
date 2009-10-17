// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/AeElementHolder.java,v 1.1 2004/09/16 15:31:26 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;

/**
 * A wrapper for an element so Axis won't think we're trying to serialize a primitive type.
 */
public class AeElementHolder
{
   /** The xsi:type value for the element we're holding. */
   private QName mType;
   
   /** element we're holding */
   private Element mElement;
   
   /**
    * Constructor
    * @param aElement
    */
   public AeElementHolder(QName aType, Element aElement)
   {
      mType = aType;
      mElement = aElement;
   }
   
   /**
    * Getter for the element.
    */
   public Element getElement()
   {
      return mElement;
   }
   
   /**
    * Getter for the type that we're holding.
    */
   public QName getType()
   {
      return mType;
   }
}

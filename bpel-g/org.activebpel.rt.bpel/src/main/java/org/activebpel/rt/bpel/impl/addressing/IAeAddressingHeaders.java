//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/addressing/IAeAddressingHeaders.java,v 1.5 2008/02/17 21:37:08 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.addressing;

import java.util.List;

import javax.xml.namespace.QName;

import org.activebpel.wsio.AeWsAddressingException;
import org.activebpel.wsio.IAeWsAddressingHeaders;
import org.w3c.dom.Element;

/**
 * Extension of IAeWsAddressingHeaders that deals with overlapping 
 * endpoint definitions in reference properties
 */
public interface IAeAddressingHeaders extends IAeWsAddressingHeaders
{
   public static final String AE_WSA_HEADERS_PROPERTY = IAeAddressingHeaders.class.getName();
   
   /** 
    * Adds a WS-Addressing header to this instance.  
    * If the element is a known WS-Addressing header, the value of the appropriate member variable is updated. 
    * Otherwise, it is added as a ReferenceProperty.
    *   
    * @param aElement
    * @throws AeWsAddressingException
    */
   public void addHeaderElement(Element aElement) throws AeWsAddressingException;   

   /**
    * Sets the reference properties for this instance.  Any WSA headers embedded
    * in the list of reference properties will override the appropriate member variable
    * as opposed to blindly adding it to the list.
    * 
    * @param aElementList the list of reference properties to serialize as headers.
    */
   public void setReferenceProperties(List aElementList) throws AeWsAddressingException;

   /**
    * Adds elements to the reference properties for this instance.  Any WSA headers embedded
    * in the list of reference properties will override the appropriate member variable
    * as opposed to blindly adding it to the list.
    * 
    * @param aElementList the list of reference properties to serialize as headers.
    */
   public void addReferenceProperties(List aElementList) throws AeWsAddressingException;
 
   /**
    * Gets the Reply relationship type QName for the namespace
    * 
    * @return relationship type attribute
    */
   public QName getReplyRelationshipName();
   
   /**
    * Returns the fully qualified fault action uri
    * @return fault action uri
    */
   public String getFaultAction();
   
   /**
    * Returns the WSA anonymous role URI
    * @return anonymous role uri
    */
   public String getAnonymousRole();
}
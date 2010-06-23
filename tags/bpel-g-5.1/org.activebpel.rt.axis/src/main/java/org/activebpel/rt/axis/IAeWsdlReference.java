//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis/src/org/activebpel/rt/axis/IAeWsdlReference.java,v 1.4 2008/02/17 21:21:04 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.server.catalog.IAeCatalogListener;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.apache.axis.description.OperationDesc;

/**
 * Container class for wsdl properties that are accessed via <code>AeMutableServiceDesc</code>.
 * Will update its properties if the underlying wsdl is replaced.
 */
public interface IAeWsdlReference extends IAeCatalogListener
{
   /**
    * Set the initial state of this object.  This method should be called before
    * the wsdl reference is made available to the <code>AeServiceDesc</code>.
    * @throws AeException
    */
   public void init() throws AeException;
   
   /**
    * Getter for the port type <code>QName</code>.
    */
   public QName getPortTypeQName();
   
   /**
    * Getter for the partner link definition object.
    */
   public AePartnerLinkDef getPartnerLinkDef();
   
   /**
    * Return the list of all available <code>OperationDesc</code> objects.
    */
   public ArrayList getOperations();
   
   /**
    * Remove the given operation.
    * @param aOperation
    */
   public void removeOperationDesc(OperationDesc aOperation);
   
   /**
    * Get all overloaded operations by name.
    * @param aMethodName
    * @return null for no match, or an array of OperationDesc objects
    */
   public OperationDesc[] getOperationsByName(String aMethodName);
   
   /**
    * Return an operation matching the given method name.  Note that if we
    * have multiple overloads for this method, we will return the first one.
    * @return null for no match
    */
   public OperationDesc getOperationByName(String methodName);

   /**
    * Return the list of allowed method names.
    */
   public List getAllowedMethods();
   
   /**
    * Getter for the current global wsdl def mapped to this wsdl reference.
    */
   public AeBPELExtendedWSDLDef getWsdlDef();
   
}

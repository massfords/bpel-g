// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.wsresource/src/org/activebpel/rt/wsresource/validation/IAeWSResourceValidationContext.java,v 1.7 2008/02/17 21:58:06 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.wsresource.validation;

import javax.wsdl.Operation;
import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;

/**
 * The web service resource validation context is the context that
 * web resource validators use when validating a web service resource.
 */
public interface IAeWSResourceValidationContext
{
   /**
    * Gets the classloader that should be used to load a rule.
    */
   public ClassLoader getRuleClassLoader();
   
   /** Returns an iterator over the WSDLs known to this context. */
   public IAeContextWSDLProvider getWSDLProvider();

   /**
    * Returns a javax.wsdl.Operation object from a portType and operation name.
    *
    * An AeException is thrown if either the portType or operation can not be resolved.
    *
    * @param aPortType
    * @param aOperation
    * @throws AeException
    */
   public Operation getOperation(QName aPortType, String aOperation) throws AeException;

   /**
    * Search and return a WSDL containing the port type from the IAeContextWSDLProvider, null if none found.
    *
    * @param aPortType
    */
   public AeBPELExtendedWSDLDef findWsdlByPortType(QName aPortType);
}

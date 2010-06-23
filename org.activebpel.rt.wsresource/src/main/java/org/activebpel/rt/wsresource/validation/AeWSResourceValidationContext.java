// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.wsresource/src/org/activebpel/rt/wsresource/validation/AeWSResourceValidationContext.java,v 1.9 2008/03/26 19:43:55 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.wsresource.validation;

import java.util.Collections;
import java.util.Iterator;

import javax.wsdl.Operation;
import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.wsresource.AeMessages;

/**
 * Implementation of a web resource validation context.
 */
public class AeWSResourceValidationContext implements IAeWSResourceValidationContext
{
   /** A context WSDL provider to provide access to all wsdls in the context */
   private IAeContextWSDLProvider mWSDLProvider;
   /** no-op wsdl provider  */
   private static AeNoOpWsdlProvider sNoOpWSDLProvider = null;
   
   /**
    * C'tor
    */
   public AeWSResourceValidationContext()
   {
      if(sNoOpWSDLProvider == null)
      {
         sNoOpWSDLProvider = new AeNoOpWsdlProvider();
      }
      setWSDLProvider(sNoOpWSDLProvider);
   }

   /**
    * C'tor
    * @param aProvider
    */
   public AeWSResourceValidationContext(IAeContextWSDLProvider aProvider)
   {
      mWSDLProvider = aProvider;
   }
   
   /** 
    * @see org.activebpel.rt.wsresource.validation.IAeWSResourceValidationContext#getRuleClassLoader()
    */
   public ClassLoader getRuleClassLoader()
   {
      return getClass().getClassLoader();
   }

   /**
    * @see org.activebpel.rt.wsresource.validation.IAeWSResourceValidationContext#getWSDLProvider()
    */
   public IAeContextWSDLProvider getWSDLProvider()
   {
      return mWSDLProvider;
   }

   /**
    * Sets the WSDL provider.
    *
    * @param aWSDLProvider
    */
   protected void setWSDLProvider(IAeContextWSDLProvider aWSDLProvider)
   {
      mWSDLProvider = aWSDLProvider;
   }

   /**
    * @see org.activebpel.rt.wsresource.validation.IAeWSResourceValidationContext#getOperation(javax.xml.namespace.QName, java.lang.String)
    */
   public Operation getOperation(QName aPortType, String aOperation) throws AeException
   {
      Operation operation = null;
      AeBPELExtendedWSDLDef wsdl = findWsdlByPortType(aPortType);

      if (wsdl != null)
         operation = wsdl.getPortType(aPortType).getOperation(aOperation, null, null);

      if (operation != null)
         return operation;
      else
      {
         String pt = (aPortType != null ? aPortType.getLocalPart() : "null"); //$NON-NLS-1$
         String exceptionMsg = AeMessages.format("AeWSResourceValidationContext.0", new Object[] {pt, aOperation}); //$NON-NLS-1$

         throw new AeException(exceptionMsg);
      }
   }

   /**
    * @see org.activebpel.rt.wsresource.validation.IAeWSResourceValidationContext#findWsdlByPortType(javax.xml.namespace.QName)
    */
   public AeBPELExtendedWSDLDef findWsdlByPortType(QName aPortType)
   {
      return AeWSDLDefHelper.getWSDLDefinitionForPortType(getWSDLProvider(), aPortType);
   }

   
   /**
    * No-Op wsdlProvider used when the {@link AeWSResourceValidationContext} class
    * is instantiated using the no-arg constructor.
    */
   protected static class AeNoOpWsdlProvider implements IAeContextWSDLProvider
   {
      /**
       * @see org.activebpel.rt.wsdl.IAeContextWSDLProvider#getWSDLIterator()
       */
      public Iterator getWSDLIterator()
      {
         return Collections.EMPTY_LIST.iterator();
      }

      /**
       * @see org.activebpel.rt.wsdl.IAeWSDLProvider#dereferenceIteration(java.lang.Object)
       */
      public AeBPELExtendedWSDLDef dereferenceIteration(Object aIteration)
      {
         return AeBPELExtendedWSDLDef.getDefaultDef();
      }

      /**
       * @see org.activebpel.rt.wsdl.IAeWSDLProvider#getWSDLIterator(java.lang.String)
       */
      public Iterator getWSDLIterator(String aNamespaceUri)
      {
         return Collections.EMPTY_LIST.iterator();
      }
   }
}

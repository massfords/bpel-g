//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeWSBPELInlinePropertyAliasVisitor.java,v 1.3 2008/01/11 19:31:16 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors; 

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.IAeExpressionLanguageFactory;
import org.activebpel.rt.message.AeMessagePartsMap;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.wsdl.def.IAePropertyAlias;

/**
 * Provides WS-BPEL 2.0 logic for inlining propertyAliases
 */
public class AeWSBPELInlinePropertyAliasVisitor extends AeInlinePropertyAliasVisitor
{
   /**
    * Ctor
    * @param aProvider
    * @param aExpressionLanguageFactory
    */
   protected AeWSBPELInlinePropertyAliasVisitor(IAeContextWSDLProvider aProvider, IAeExpressionLanguageFactory aExpressionLanguageFactory)
   {
      super(aProvider, aExpressionLanguageFactory);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeInlinePropertyAliasVisitor#cacheCorrelationPropertyAlias(org.activebpel.rt.message.AeMessagePartsMap, javax.xml.namespace.QName)
    */
   protected boolean cacheCorrelationPropertyAlias(AeMessagePartsMap aMessagePartsMap, QName aPropName)
   {
      boolean found = super.cacheCorrelationPropertyAlias(aMessagePartsMap, aPropName);
      if (!found && aMessagePartsMap.isSinglePartElement())
      {
         found = cachePropertyAlias(IAePropertyAlias.ELEMENT_TYPE, aMessagePartsMap.getSingleElementPart(), aPropName);
      }
      return found;
   }
}
 
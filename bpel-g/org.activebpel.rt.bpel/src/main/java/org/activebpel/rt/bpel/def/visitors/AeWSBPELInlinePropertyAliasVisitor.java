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

import org.activebpel.rt.bpel.IAeExpressionLanguageFactory;
import org.activebpel.rt.message.AeMessagePartsMap;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.wsdl.def.IAePropertyAlias;

import javax.xml.namespace.QName;

/**
 * Provides WS-BPEL 2.0 logic for inlining propertyAliases
 */
public class AeWSBPELInlinePropertyAliasVisitor extends AeInlinePropertyAliasVisitor {
    /**
     * Ctor
     *
     * @param aProvider
     * @param aExpressionLanguageFactory
     */
    protected AeWSBPELInlinePropertyAliasVisitor(IAeContextWSDLProvider aProvider, IAeExpressionLanguageFactory aExpressionLanguageFactory) {
        super(aProvider, aExpressionLanguageFactory);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.AeInlinePropertyAliasVisitor#cacheCorrelationPropertyAlias(org.activebpel.rt.message.AeMessagePartsMap, javax.xml.namespace.QName)
     */
    protected boolean cacheCorrelationPropertyAlias(AeMessagePartsMap messagePartsMap, QName propName) {
        boolean found = super.cacheCorrelationPropertyAlias(messagePartsMap, propName);
        if (!found && messagePartsMap != null && messagePartsMap.isSinglePartElement()) {
            found = cachePropertyAlias(IAePropertyAlias.ELEMENT_TYPE, messagePartsMap.getSingleElementPart(), propName);
        }
        return found;
    }
}
 
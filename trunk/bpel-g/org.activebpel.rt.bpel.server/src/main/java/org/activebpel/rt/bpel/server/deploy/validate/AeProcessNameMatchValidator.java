//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/AeProcessNameMatchValidator.java,v 1.6 2006/07/18 20:05:32 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.validate;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.deploy.bpr.AePddResource;
import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;
import org.activebpel.rt.util.AeUtil;
import org.w3c.dom.Document;

/**
 * Ensure that the process name in the pdd its matching bpel file are the same.
 */
public class AeProcessNameMatchValidator extends AeAbstractPddIterator {

    private static final String NO_MATCH = AeMessages.getString("AeProcessNameMatchValidator.0"); //$NON-NLS-1$

    protected void validateImpl(AePddResource aPdd, IAeBpr aBprFile, IAeBaseErrorReporter aReporter)
            throws AeException {
        QName nameFromPdd = aPdd.getPdd().getName();
        QName nameFromBpel = extractFromBpel(aPdd.getPdd().getLocation(), aBprFile);

        if (!AeUtil.compareObjects(nameFromPdd, nameFromBpel)) {
            String[] args = {aPdd.getName(), aPdd.getPdd().getLocation()};
            aReporter.addError(NO_MATCH, args, null);
        }
    }


    /**
     * Extract the process qname from the bpel <code>Document</code>.
     *
     * @param aLocation BPEL resource location.
     * @param aBprFile
     * @throws AeException
     */
    protected QName extractFromBpel(String aLocation, IAeBpr aBprFile) throws AeException {
        Document bpelDom = aBprFile.getResourceAsDocument(aLocation);
        String localPart = bpelDom.getDocumentElement().getAttribute(NAME_ATTR);
        String namespace = bpelDom.getDocumentElement().getAttribute(TARGET_NAMESPACE_ATTR);
        return new QName(namespace, localPart);
    }
}

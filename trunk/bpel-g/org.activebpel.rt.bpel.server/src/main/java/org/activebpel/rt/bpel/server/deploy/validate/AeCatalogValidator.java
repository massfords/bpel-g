// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/validate/AeCatalogValidator.java,v 1.1 2006/07/18 20:05:32 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.validate;

import java.util.LinkedList;
import java.util.List;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;

import bpelg.services.deploy.types.catalog.BaseCatalogEntryType;
import bpelg.services.deploy.types.catalog.Catalog;

/**
 * Validates that all of the wsdl entries in the catalog.xml (previously
 * wsdlCatalog.xml) file are present in the bpr file.
 */
public class AeCatalogValidator implements IAePredeploymentValidator {
    /**
     * @see org.activebpel.rt.bpel.server.deploy.validate.IAePredeploymentValidator#validate(org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr,
     *      org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter)
     */
    public void validate(IAeBpr aBprFile, IAeBaseErrorReporter aReporter)
            throws AeException {
        Catalog catalogDoc = aBprFile.getCatalogDocument();
        if (catalogDoc == null) {
            aReporter
                    .addWarning(
                            AeMessages.getString("AeCatalogValidator.1"), new String[]{aBprFile.getBprFileName()}, null); //$NON-NLS-1$
        } else {
            List<BaseCatalogEntryType> toValidate = new LinkedList<>();
            toValidate.addAll(catalogDoc.getWsdlEntry());
            toValidate.addAll(catalogDoc.getSchemaEntry());
            toValidate.addAll(catalogDoc.getOtherEntry());
            validate(aBprFile, toValidate, aReporter);
        }
    }

    /**
     * Validates the classpath attributes for the entries
     * name.
     *
     * @param aBprFile
     * @param aEntries
     * @param aReporter
     * @throws AeException
     */
    protected void validate(IAeBpr aBprFile,
                            List<BaseCatalogEntryType> aEntries,
                            IAeBaseErrorReporter aReporter) throws AeException {

        for (BaseCatalogEntryType entry : aEntries) {
            if (!aBprFile.exists(entry.getClasspath())) {
                aReporter
                        .addError(
                                AeMessages.getString("AeCatalogValidator.0"), new String[]{entry.getClasspath(), aBprFile.getBprFileName().toString()}, null); //$NON-NLS-1$
            }
        }
    }
}

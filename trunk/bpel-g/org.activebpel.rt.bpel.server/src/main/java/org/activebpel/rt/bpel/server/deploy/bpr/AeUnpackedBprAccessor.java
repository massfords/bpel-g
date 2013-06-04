//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/bpr/AeUnpackedBprAccessor.java,v 1.2 2005/06/17 21:51:13 PCollins Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.bpr;

import bpelg.services.deploy.types.pdd.Pdd;
import org.activebpel.rt.bpel.server.deploy.AeDeploymentException;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A <code>IAeBprFileStrategy</code> impl where bpr resources are pulled from an
 * unpacked copy of the original bpr file.
 */
public class AeUnpackedBprAccessor extends AeAbstractBprStrategy {
	/**
	 * Constructor.
	 * 
	 * @param aDeploymentContext
	 */
	public AeUnpackedBprAccessor(IAeDeploymentContext aDeploymentContext) {
		super(aDeploymentContext);
	}

	/**
	 * @see org.activebpel.rt.bpel.server.deploy.bpr.IAeBprAccessor#init()
	 */
	public void init() throws AeDeploymentException {
		File rootDir = new File(getDeploymentContext()
				.getTempDeploymentLocation().getFile());

		ArrayList<AePddResource> resources = new ArrayList<>();
		listFiles(resources, rootDir, "", new FileFilter() {
			@Override
			public boolean accept(File aFile) {
				return aFile.isDirectory() || aFile.getName().endsWith(".pdd");
			}
		});
		setPddResources(resources);
	}

	/**
	 * Extract the appropriate files from the directory.
	 * 
	 * @param aMatches
	 *            Container collection for holding matched resources.
	 * @param aDir
	 *            The directory in which to look for matches (recurses).
	 * @param aPath
	 *            Path prefix to add to file names.
	 * @param aFilter
	 *            FileFilter instance.
	 * @throws AeDeploymentException 
	 */
	protected void listFiles(Collection<AePddResource> aMatches, File aDir,
			String aPath, FileFilter aFilter) throws AeDeploymentException {
		File[] files = aDir.listFiles(aFilter);
		if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    try {
                        JAXBContext context = JAXBContext.newInstance(Pdd.class);
                        Unmarshaller u = context.createUnmarshaller();
                        Pdd pdd = (Pdd) u.unmarshal(file);
                        aMatches.add(new AePddResource(aPath + file.getName(),
                                pdd));
                    } catch (JAXBException e) {
                        throw new AeDeploymentException("Error parsing pdd", e);
                    }
                } else {
                    String name = file.getName();
                    if (!name.endsWith(File.separator)) {
                        name += File.separatorChar;
                    }
                    listFiles(aMatches, file, aPath + name, aFilter);
                }
            }
		}
	}
}

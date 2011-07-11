//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/bpr/AeJarFileBprAccessor.java,v 1.5 2006/07/18 20:05:33 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.bpr;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.deploy.AeDeploymentException;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContext;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeJarReaderUtil;
import org.activebpel.rt.util.AeUtil;

import bpelg.services.deploy.types.pdd.Pdd;

/**
 * A <code>IAeBprFileStrategy</code> impl where bpr resources are pulled from
 * directly from the archive file.
 * 
 * Currently this class is only used by <code>AeMain</code> to perform offline
 * bpr validation.
 */
public class AeJarFileBprAccessor extends AeAbstractBprStrategy {

	/**
	 * Constructor.
	 * 
	 * @param aDeploymentContext
	 */
	public AeJarFileBprAccessor(IAeDeploymentContext aDeploymentContext) {
		super(aDeploymentContext);
	}

	/**
	 * Reads through the BPR archive and sets up the internal state.
	 * 
	 * @throws AeDeploymentException
	 */
	public void init() throws AeDeploymentException {
		AeJarReaderUtil jru = null;
		InputStream is = null;
		try {
			jru = new AeJarReaderUtil(getDeploymentContext()
					.getDeploymentLocation());
			List<AePddResource> pddList = new LinkedList<AePddResource>();
			JAXBContext context = JAXBContext.newInstance(Pdd.class);
			Unmarshaller um = context.createUnmarshaller();
			for (JarEntry entry : jru.getEntries(new AeNameFilter("*.pdd"))) {
				is = jru.getInputStream(entry);
				Pdd pdd = (Pdd) um.unmarshal(is);
				pddList.add(new AePddResource(entry.getName(), pdd));
			}
			setPddResources(pddList);
		} catch (Exception e) {

			throw new AeDeploymentException(
					AeMessages
							.format("AeJarFileBprAccessor.ERROR_7", getDeploymentContext().getDeploymentLocation()), e); //$NON-NLS-1$
		} finally {
			AeCloser.close(is);
			AeCloser.close(jru);
		}
	}

	/**
	 * Convience class - impl of FilenameFilter for building deployment
	 * descriptor object.
	 */
	static class AeNameFilter implements FilenameFilter {
		String mExt;

		/**
		 * Constructor
		 * 
		 * @param aExt
		 *            extension to filter on.
		 */
		public AeNameFilter(String aExt) {
			mExt = aExt;
		}

		/**
		 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
		 */
		public boolean accept(File aFile, String aFilename) {
			return !AeUtil.isNullOrEmpty(aFilename)
					&& aFilename.toLowerCase().endsWith(mExt);
		}
	}

}

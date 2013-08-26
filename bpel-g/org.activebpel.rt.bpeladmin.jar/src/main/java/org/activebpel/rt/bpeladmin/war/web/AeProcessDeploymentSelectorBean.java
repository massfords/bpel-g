// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/AeProcessDeploymentSelectorBean.java,v 1.4 2006/01/10 21:11:40 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web;

import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.activebpel.rt.bpeladmin.war.AeEngineManagementFactory;
import org.activebpel.rt.bpeladmin.war.AeMessages;
import org.activebpel.rt.util.AeUtil;

import bpelg.services.deploy.types.pdd.Pdd;
import bpelg.services.processes.types.GetProcessDeployments;
import bpelg.services.processes.types.ProcessDeployment;
import bpelg.services.processes.types.ProcessDeployments;

/**
 * Bean for selecting the info on a specific process deployment.
 */
public class AeProcessDeploymentSelectorBean extends AeAbstractAdminBean {
    /**
     * The process deployment id.
     */
    protected int mProcessDeploymentId;

    /**
     * The specified process deployment
     */
    protected ProcessDeployment mDetail;

    /**
     * Default constructor.
     */
    public AeProcessDeploymentSelectorBean() {

    }

    /**
     * The offset indicating a specific process deployment.
     *
     * @param aOffset
     */
    public void setSelection(int aOffset) {
        ProcessDeployments deployedProcesses = fetchDeployments();
        List<ProcessDeployment> list = deployedProcesses.getProcessDeployment();
        if (list.size() > 0 && aOffset >= 0 && aOffset < list.size()) {
            mDetail = list.get(aOffset);
        } else {
            mDetail = null;
        }
        setProcessDeploymentId(aOffset);
    }

    protected ProcessDeployments fetchDeployments() {
        ProcessDeployments deployedProcesses = AeEngineManagementFactory
                .getProcessManager().getProcessDeployments(
                        new GetProcessDeployments());
        return deployedProcesses;
    }

    /**
     * The qname indicating a specific process deployment.
     *
     * @param aQName
     */
    public void setPlanQName(String aQName) {
        if (!AeUtil.isNullOrEmpty(aQName)) {
            String ns = null;
            String localPart = aQName;

            int colonIndex = aQName.lastIndexOf(':');
            if (colonIndex != -1) {
                ns = aQName.substring(0, colonIndex);
                localPart = aQName.substring(colonIndex + 1);
            }

            QName qname = new QName(ns, localPart);
            mDetail = AeEngineManagementFactory.getProcessManager().getProcessDeploymentByName(qname);
        }
    }

    /**
     * Getter for the process qname local part.
     */
    public String getLocalName() {
        if (mDetail != null) {
            return mDetail.getProcess().getName().getLocalPart();
        } else {
            return AeMessages
                    .getString("AeProcessDeploymentSelectorBean.DETAILS_NOT_AVAILABLE"); //$NON-NLS-1$
        }
    }

    /**
     * Getter for the process qname namespace uri.
     */
    public String getNamespaceURI() {
        if (mDetail != null) {
            return mDetail.getProcess().getName().getNamespaceURI();
        } else {
            return AeMessages
                    .getString("AeProcessDeploymentSelectorBean.DETAILS_NOT_AVAILABLE"); //$NON-NLS-1$
        }
    }

    /**
     * Getter for the process pdd source xml.
     */
    public String getSourceXml() {
        if (mDetail != null) {
            Pdd pdd = mDetail.getProcess();
            try {
                JAXBContext context = JAXBContext.newInstance(Pdd.class);
                Marshaller m = context.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                StringWriter sw = new StringWriter();
                m.marshal(pdd, sw);
                return sw.toString();
            } catch (JAXBException e) {
                return "error generating source";
            }
        } else {
            return AeMessages
                    .getString("AeProcessDeploymentSelectorBean.DETAILS_NOT_AVAILABLE"); //$NON-NLS-1$
        }
    }

    /**
     * Getter for the bpel process source xml.
     */
    public String getBpelSourceXml() {
        if (mDetail != null) {
            return mDetail.getSource();
        } else {
            return AeMessages
                    .getString("AeProcessDeploymentSelectorBean.DETAILS_NOT_AVAILABLE"); //$NON-NLS-1$
        }
    }

    /**
     * @return Returns the processDeploymentId.
     */
    public int getProcessDeploymentId() {
        return mProcessDeploymentId;
    }

    /**
     * @param aProcessDeploymentId The processDeploymentId to set.
     */
    public void setProcessDeploymentId(int aProcessDeploymentId) {
        mProcessDeploymentId = aProcessDeploymentId;
    }
}

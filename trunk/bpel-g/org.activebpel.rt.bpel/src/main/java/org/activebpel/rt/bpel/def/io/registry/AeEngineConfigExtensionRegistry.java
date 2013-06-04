//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/registry/AeEngineConfigExtensionRegistry.java,v 1.3 2008/02/17 21:37:08 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2004-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io.registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.visitors.AeDefVisitorFactory;
import org.activebpel.rt.xml.def.io.AeMapBasedExtensionRegistry;

/**
 * An implementation of the extension Registry for WS-BPEL 2.0. This registry
 * creates a map of extension element, attribute and activity Qnames to their
 * class names that implement IAeExtensionObject interface from the EngineConfig
 * instance and passes it onto it's parent
 */
public class AeEngineConfigExtensionRegistry extends
		AeMapBasedExtensionRegistry {
	private List<AeExtensionEntry> mExtensions;
	
	public void init() {
		Map<QName,String> map = new HashMap<>();
		if (getExtensions() != null) {
			for(AeExtensionEntry entry : getExtensions()) {
				map.put(new QName(entry.getNamespace(), entry.getLocalPart()), entry.getClassName());
			}
		}
		setExtensionObjectMap(map);
		AeDefVisitorFactory.setExtensionRegistry(
				IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI, this);
	}

	public List<AeExtensionEntry> getExtensions() {
		return mExtensions;
	}

	public void setExtensions(List<AeExtensionEntry> aExtensions) {
		mExtensions = aExtensions;
	}
}

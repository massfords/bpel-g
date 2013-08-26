package org.activebpel.rt.bpel.server.catalog.resource;

import javax.xml.XMLConstants;

import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.wsdl.def.IAeBPELExtendedWSDLConst;

import bpelg.services.deploy.types.pdd.ReferenceType;

public class AeReferenceTypeUtil {
    /**
     * Return true if this is a wsdl entry.
     */
    public static boolean isWsdlEntry(ReferenceType aRef) {
        return IAeBPELExtendedWSDLConst.WSDL_NAMESPACE.equals(aRef.getTypeURI());
    }

    /**
     * Return true if this is a schema entry.
     */
    public static boolean isSchemaEntry(ReferenceType aRef) {
        return XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(aRef.getTypeURI());
    }

    /**
     * Returns true if this is an xsl entry.
     */
    public static boolean isXslEntry(ReferenceType aRef) {
        return IAeBPELConstants.XSL_NAMESPACE.equals(aRef.getTypeURI());
    }

}

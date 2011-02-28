package bpelg.packaging.ode;

import javax.xml.XMLConstants;

import org.activebpel.rt.IAeConstants;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

public class BgCatalogTuple {
    public String logicalLocation;
    public String physicalLocation;
    public String namespace;
    public String type;
    
    public BgCatalogTuple(String aLogicalLocation, String aPhysicalLocation, String aNamespace, String aType) {
        logicalLocation = aLogicalLocation;
        physicalLocation = aPhysicalLocation;
        namespace = aNamespace;
        type = aType;
    }
    
    public boolean isWsdl() {
        return IAeConstants.WSDL_NAMESPACE.equals(type);
    }
    
    public boolean isXsd() {
        return XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(type);
    }
    
    public boolean isOther() {
        return !isWsdl() && !isXsd();
    }
    
    public boolean equals(Object aOther) {
        if (aOther instanceof BgCatalogTuple) {
            BgCatalogTuple other = (BgCatalogTuple) aOther;
            boolean b = ObjectUtils.equals(logicalLocation, other.logicalLocation);
            b &= ObjectUtils.equals(physicalLocation, other.physicalLocation);
            b &= ObjectUtils.equals(namespace, other.namespace);
            b &= ObjectUtils.equals(type, other.type);
            return b;
        }
        return super.equals(aOther);
    }
    
    public int hashCode() {
        return logicalLocation.hashCode();
    }
    
    public String toString() {
        return new ToStringBuilder(this).
          append("logical", logicalLocation).
          append("physical", physicalLocation).
          append("namespace", namespace).
          append("type", type).
          toString();
    }
}

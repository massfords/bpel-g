package org.activebpel.rt.xml;

import java.beans.ConstructorProperties;

import javax.xml.namespace.QName;

public class AeQName {
    private static final QName DEFAULT = new QName("ns", "default");
    private QName mQName = DEFAULT;

    @ConstructorProperties({"namespaceURI", "localPart"})
    public AeQName(String aNamespace, String aLocalPart) {
        mQName = new QName(aNamespace, aLocalPart);
    }

    public AeQName() {
    }

    public AeQName(QName aQName) {
        mQName = aQName;
    }

    public String getNamespaceURI() {
        return mQName.getNamespaceURI();
    }

    public String getLocalPart() {
        return mQName.getLocalPart();
    }

    public void setNamespace(String aNamespace) {
        mQName = new QName(aNamespace, mQName.getLocalPart());
    }

    public void setLocalPart(String aLocalPart) {
        mQName = new QName(mQName.getNamespaceURI(), aLocalPart);
    }

    public QName toQName() {
        return mQName;
    }

    public int hashCode() {
        return mQName.hashCode();
    }

    public boolean equals(Object aOther) {
        return aOther instanceof AeQName && ((AeQName) aOther).toQName().equals(mQName);
    }
}

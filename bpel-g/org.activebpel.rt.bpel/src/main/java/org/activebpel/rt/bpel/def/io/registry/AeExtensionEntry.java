package org.activebpel.rt.bpel.def.io.registry;

public class AeExtensionEntry {
    private String mNamespace;
    private String mLocalPart;
    private String mClassName;

    public String getNamespace() {
        return mNamespace;
    }

    public void setNamespace(String aNamespace) {
        mNamespace = aNamespace;
    }

    public String getLocalPart() {
        return mLocalPart;
    }

    public void setLocalPart(String aLocalPart) {
        mLocalPart = aLocalPart;
    }

    public String getClassName() {
        return mClassName;
    }

    public void setClassName(String aClassName) {
        mClassName = aClassName;
    }
}

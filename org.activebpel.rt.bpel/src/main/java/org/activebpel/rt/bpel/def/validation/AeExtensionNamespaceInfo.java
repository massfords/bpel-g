package org.activebpel.rt.bpel.def.validation;

/**
 * The extension namespace information found during the visit.
 */
public class AeExtensionNamespaceInfo implements Comparable
{
   /** The namespace. */
   private String mNamespace;
   /** The preferred prefix. */
   private String mPreferredPrefix;
   /** The must understand flag. */
   private boolean mMustUnderstand;
   
   /**
    * C'tor.
    * 
    * @param aNamespace
    * @param aMustUnderstand
    */
   public AeExtensionNamespaceInfo(String aNamespace, boolean aMustUnderstand)
   {
      this(aNamespace, null, aMustUnderstand);
   }

   /**
    * C'tor.
    * 
    * @param aNamespace
    * @param aPreferredPrefix
    * @param aMustUnderstand
    */
   public AeExtensionNamespaceInfo(String aNamespace, String aPreferredPrefix, boolean aMustUnderstand)
   {
      setNamespace(aNamespace);
      setPreferredPrefix(aPreferredPrefix);
      setMustUnderstand(aMustUnderstand);
   }

   /**
    * @return Returns the mustUnderstand.
    */
   public boolean isMustUnderstand()
   {
      return mMustUnderstand;
   }

   /**
    * @param aMustUnderstand the mustUnderstand to set
    */
   public void setMustUnderstand(boolean aMustUnderstand)
   {
      mMustUnderstand = aMustUnderstand;
   }

   /**
    * @return Returns the namespace.
    */
   public String getNamespace()
   {
      return mNamespace;
   }

   /**
    * @param aNamespace the namespace to set
    */
   public void setNamespace(String aNamespace)
   {
      mNamespace = aNamespace;
   }

   /**
    * @return Returns the preferredPrefix.
    */
   public String getPreferredPrefix()
   {
      return mPreferredPrefix;
   }

   /**
    * @param aPreferredPrefix the preferredPrefix to set
    */
   public void setPreferredPrefix(String aPreferredPrefix)
   {
      mPreferredPrefix = aPreferredPrefix;
   }

   /**
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   public int compareTo(Object aOther)
   {
      AeExtensionNamespaceInfo me = this;
      AeExtensionNamespaceInfo other = (AeExtensionNamespaceInfo) aOther;
      
      return me.getNamespace().compareTo(other.getNamespace());
   }
   
   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object aOther)
   {
      AeExtensionNamespaceInfo me = this;
      AeExtensionNamespaceInfo other = (AeExtensionNamespaceInfo) aOther;
      
      return me.getNamespace().equals(other.getNamespace());
   }
   
   /**
    * @see java.lang.Object#hashCode()
    */
   public int hashCode()
   {
      return getNamespace().hashCode();
   }
}
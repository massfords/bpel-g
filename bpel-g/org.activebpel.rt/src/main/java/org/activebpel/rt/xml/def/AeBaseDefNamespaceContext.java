//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/AeBaseDefNamespaceContext.java,v 1.3 2007/12/05 22:42:36 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def;

import java.util.Iterator;
import java.util.Set;

import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.IAeMutableNamespaceContext;

/**
 * A concrete implementation of a namespace context that uses a def object 
 * as the namespace info source.
 */
public class AeBaseDefNamespaceContext implements IAeMutableNamespaceContext
{
   /** The base def. */
   private AeBaseXmlDef mBaseDef;
   
   /**
    * Constructor.
    * 
    * @param aDef
    */
   public AeBaseDefNamespaceContext(AeBaseXmlDef aDef)
   {
      setBaseDef(aDef);
   }

   /**
    * @see org.activebpel.rt.xml.IAeNamespaceContext#resolveNamespaceToPrefixes(java.lang.String)
    */
   public Set resolveNamespaceToPrefixes(String aNamespace)
   {
      return getBaseDef().findPrefixesForNamespace(aNamespace);
   }
   
   /**
    * @see org.activebpel.rt.xml.IAeNamespaceContext#resolvePrefixToNamespace(java.lang.String)
    */
   public String resolvePrefixToNamespace(String aPrefix)
   {
      return AeXmlDefUtil.translateNamespacePrefixToUri(getBaseDef(), aPrefix);
   }
   
   /**
    * @see org.activebpel.rt.xml.IAeMutableNamespaceContext#getOrCreatePrefixForNamespace(java.lang.String, java.lang.String)
    */
   public String getOrCreatePrefixForNamespace(String aPreferredPrefix, String aNamespace)
   {
      return getOrCreatePrefixForNamespace(aPreferredPrefix, aNamespace, false);
   }

   /**
    * @see org.activebpel.rt.xml.IAeMutableNamespaceContext#getOrCreatePrefixForNamespace(java.lang.String, java.lang.String, boolean)
    */
   public String getOrCreatePrefixForNamespace(String aPreferredPrefix, String aNamespace, boolean aAllowDefaultNamespace)
   {
      Set prefixes = getBaseDef().findPrefixesForNamespace(aNamespace);
      
      if (!prefixes.isEmpty() && !aAllowDefaultNamespace)
         prefixes.remove(""); //$NON-NLS-1$
      
      // If no prefixes are found, then create a new one based on the preferred prefix.
      if (prefixes.isEmpty())
      {
         return createPrefixForNamespace(aPreferredPrefix, aNamespace);
      }
      else
      {
         String prefix = null;
         for (Iterator iter = prefixes.iterator(); iter.hasNext(); )
         {
            prefix = (String) iter.next();
            if (AeUtil.compareObjects(prefix, aPreferredPrefix))
               break;
         }
         return prefix;
      }
   }
   
   /**
    * Creates a new prefix->namespace mapping using the given preferred prefix and the given
    * namespace.
    * 
    * @param aPreferredPrefix
    * @param aNamespace
    */
   protected String createPrefixForNamespace(String aPreferredPrefix, String aNamespace)
   {
      final String preferredPrefix = AeUtil.isNullOrEmpty(aPreferredPrefix) ? "ns" : aPreferredPrefix ; //$NON-NLS-1$
      String prefix = preferredPrefix;

      String mappedNamespace = resolvePrefixToNamespace(prefix);
      if (!AeUtil.compareObjects(aNamespace, mappedNamespace))
      {
         // it's mapped to something else or isn't mapped at all
         // keep going until mappedNamespace is null
         int index = 0;
         while ( mappedNamespace != null )
         {
            prefix = preferredPrefix + String.valueOf( index++ );
            mappedNamespace = resolvePrefixToNamespace(prefix);
         }

         getBaseDef().addNamespace(prefix, aNamespace);
      }
      return prefix;
   }

   /**
    * @return Returns the baseDef.
    */
   protected AeBaseXmlDef getBaseDef()
   {
      return mBaseDef;
   }

   /**
    * @param aBaseDef The baseDef to set.
    */
   protected void setBaseDef(AeBaseXmlDef aBaseDef)
   {
      mBaseDef = aBaseDef;
   }
}

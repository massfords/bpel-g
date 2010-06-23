//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/AeXmlDefUtil.java,v 1.11 2008/03/24 18:43:05 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;

import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;

/**
 * utility methods for working with def objects.
 */
public class AeXmlDefUtil
{
   /**
    * Generates a unique name for the child.
    * 
    * @param aPrefix
    * @param aNamedDefsCollection
    */
   public static String generateUniqueName(String aPrefix, Collection aNamedDefsCollection)
   {
      return generateUniqueName(aPrefix, aNamedDefsCollection, false);
   }

   /**
    * Generates a unique name for the child.
    * 
    * @param aPrefix
    * @param aNamedDefsCollection
    * @param aPreserveIfAlreadyUnique
    */
   public static String generateUniqueName(String aPrefix, Collection aNamedDefsCollection, boolean aPreserveIfAlreadyUnique)
   {
      Set set = new HashSet();
      for (Iterator iter = aNamedDefsCollection.iterator(); iter.hasNext();)
      {
         IAeNamedDef def = (IAeNamedDef) iter.next();
         set.add(def.getName());
      }
      
      return AeUtil.generateUniqueName(aPrefix, set, aPreserveIfAlreadyUnique);
   }

   /**
    * Given a QName, returns a prefix:localpart formatted String representation of that QName.
    *
    * @param aDef
    * @param aQName
    */
   public static String formatQName(AeBaseXmlDef aDef, QName aQName)
   {
      return AeXmlUtil.encodeQName(aQName, new AeBaseDefNamespaceContext(aDef), null);
   }

   /**
    * Given a string which represents a qualified name and a context, this method will parse
    * it and return a QName object. Note this method returns null if the string
    * is not properly formed.
    * @param aDef the def from which to resolve the prefix to a namespace
    * @param aQstr the string to be parsed
    */
   public static QName parseQName(AeBaseXmlDef aDef, String aQstr)
   {
      QName qname;

      int colon = aQstr.indexOf(":"); //$NON-NLS-1$
      if (colon < 0 )
         qname = new QName(null, aQstr);
      else
      {
         String nsURI = translateNamespacePrefixToUri(aDef, aQstr.substring(0, colon));
         qname = new QName(nsURI, aQstr.substring(colon+1));
      }

      return qname;
   }

   /**
    * Returns the namespace associated with the prefix from the associated model.
    */
   private static String translateNamespacePrefixToUriInternal(AeBaseXmlDef aDef, String aPrefix)
   {
      // check def for the prefix we are looking for
      String nsURI = aDef.getNamespace(aPrefix);

      // if we haven't found the prefix ask our parent if it can find it
      if(nsURI == null)
      {
         AeBaseXmlDef parent = aDef.getParentXmlDef();
         if(parent != null)
            nsURI = translateNamespacePrefixToUriInternal(parent, aPrefix);
      }
      return nsURI;
   }

   /**
    * Returns the namespace associated with the prefix from the associated model.
    * TODO we should cache prefixes in process for non-conflicting prefixes and only do brute force search for conflicts.
    */
   public static String translateNamespacePrefixToUri(AeBaseXmlDef aDef, String aPrefix)
   {
      String nsURI = null;

      // if we have a definiton test if it has the prefix we are looking for
      // TODO (MF) get rid of this xmlns: mojo and also the recursion.
      if(aDef != null)
      {
         nsURI = translateNamespacePrefixToUriInternal(aDef, aPrefix);
      }
      return nsURI;
   }

   /**
    * Sets the def on the extension object if the extension object provides an
    * adapter to accept the def.
    * @param aExtensionObject
    * @param aDef
    */
   public static void installDef(IAeExtensionObject aExtensionObject, AeBaseXmlDef aDef)
   {
      if (aExtensionObject != null)
      {
         IAeExtensionDefHolder holder = (IAeExtensionDefHolder) aExtensionObject.getAdapter(IAeExtensionDefHolder.class);
         if (holder != null)
         {
            holder.setExtensionDef(aDef);
         }
      }
   }

   /**
    * Gets anscestor of a Def by its type.
    * @param aDef
    * @param aType
    */
   public static AeBaseXmlDef getAncestorByType(AeBaseXmlDef aDef, Class aType)
   {
      boolean isAssignable = false;
      AeBaseXmlDef def = aDef.getParentXmlDef();
      while (def.getParentXmlDef() != null)
      {
         isAssignable = aType.isAssignableFrom(def.getClass());
         if (isAssignable)
            break;
         else
            def = def.getParentXmlDef();
      }
      if (isAssignable)
         return def;
      else
         return null;
   }

   /**
    * Determines if a Def is of a certain type.
    * @param aDef
    * @param aType
    */
   public static boolean isParentedByType(AeBaseXmlDef aDef, Class aType)
   {
      return getAncestorByType(aDef, aType) != null;

   }
}

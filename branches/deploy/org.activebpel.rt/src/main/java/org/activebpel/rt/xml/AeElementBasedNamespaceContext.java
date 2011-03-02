// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/AeElementBasedNamespaceContext.java,v 1.2 2007/03/14 14:25:38 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xml;

import java.util.HashSet;
import java.util.Set;

import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Element;

/**
 * A mutable namespace context implementation that uses a DOM Element to lookup and 
 * create namespace mappings.
 */
public class AeElementBasedNamespaceContext implements IAeMutableNamespaceContext
{
   /** The DOM Element. */
   private Element mElement;

   /**
    * Constructor.
    * 
    * @param aElement
    */
   public AeElementBasedNamespaceContext(Element aElement)
   {
      setElement(aElement);
   }

   /**
    * @return Returns the element.
    */
   protected Element getElement()
   {
      return mElement;
   }

   /**
    * @param aElement The element to set.
    */
   protected void setElement(Element aElement)
   {
      mElement = aElement;
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
      return AeXmlUtil.getOrCreatePrefix(getElement(), aNamespace, aPreferredPrefix, false, aAllowDefaultNamespace);
   }

   /**
    * @see org.activebpel.rt.xml.IAeNamespaceContext#resolvePrefixToNamespace(java.lang.String)
    */
   public String resolvePrefixToNamespace(String aPrefix)
   {
      return AeXmlUtil.getNamespaceForPrefix(getElement(), aPrefix);
   }

   /**
    * @see org.activebpel.rt.xml.IAeNamespaceContext#resolveNamespaceToPrefixes(java.lang.String)
    */
   public Set resolveNamespaceToPrefixes(String aNamespace)
   {
      // TODO (EPW) improve this implementation to actually return all of the prefixes.
      Set rval = new HashSet();
      String prefix = AeXmlUtil.getPrefixForNamespace(getElement(), aNamespace);
      if (AeUtil.notNullOrEmpty(prefix))
      {
         rval.add(prefix);
      }
      return rval;
   }
}

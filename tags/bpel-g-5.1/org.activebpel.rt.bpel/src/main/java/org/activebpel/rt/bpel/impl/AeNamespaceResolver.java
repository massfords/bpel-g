// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeNamespaceResolver.java,v 1.4 2006/09/27 19:58:41 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.wsdl.def.IAePropertyAlias;
import org.activebpel.rt.xml.IAeNamespaceContext;


/**
 * Class used to resolve namespace prefixes for property aliases. 
 */
public class AeNamespaceResolver implements IAeNamespaceContext
{
   /** Property alias which contains the prefix to namespace mappings */
   IAePropertyAlias mPropAlias;

   /**
    * Constructs a namespace resolver for use with the given property alias
    * @param aPropAlias the alias to use in getting prefix mappings
    */
   public AeNamespaceResolver(IAePropertyAlias aPropAlias)
   {
      mPropAlias = aPropAlias;
   }
   
   /**
    * Returns the namespace associated with the prefix from the associated model.
    * 
    * @see org.activebpel.rt.xml.IAeNamespaceContext#resolvePrefixToNamespace(java.lang.String)
    */
   public String resolvePrefixToNamespace(String aPrefix)
   {
      return (String) mPropAlias.getNamespaces().get(aPrefix);
   }

   /**
    * @see org.activebpel.rt.xml.IAeNamespaceContext#resolveNamespaceToPrefixes(java.lang.String)
    */
   public Set resolveNamespaceToPrefixes(String aNamespace)
   {
      HashSet set = new HashSet();
      Map map = mPropAlias.getNamespaces();
      
      for (Iterator iter = map.entrySet().iterator(); iter.hasNext(); )
      {
         Map.Entry entry = (Map.Entry) iter.next();
         if (entry.getValue().equals(aNamespace))
         {
            set.add(entry.getKey());
         }
      }
      return set;
   }

}

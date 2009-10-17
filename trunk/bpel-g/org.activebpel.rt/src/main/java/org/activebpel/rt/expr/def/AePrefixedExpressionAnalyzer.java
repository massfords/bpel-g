//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/expr/def/AePrefixedExpressionAnalyzer.java,v 1.1 2008/01/25 20:41:50 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.expr.def;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.activebpel.rt.util.AeXmlUtil;

/**
 * This is a based class for the xpath and xquery expression analyzers.
 */
public abstract class AePrefixedExpressionAnalyzer extends AeAbstractExpressionAnalyzer
{
   /** The pattern that <code>getNamespaces</code> will use to find prefixes. */
   protected static Pattern sGetNamespacesPattern = Pattern.compile("(" + AeXmlUtil.NCNAME_PATTERN + "):"); //$NON-NLS-1$ //$NON-NLS-2$
   
   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionAnalyzer#getNamespaces(org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext, java.lang.String)
    */
   public Set getNamespaces(IAeExpressionAnalyzerContext aContext, String aExpression)
   {
      Set set = new LinkedHashSet();

      Pattern p = sGetNamespacesPattern;
      Matcher m = p.matcher(aExpression);
      while (m.find())
      {
         String prefix = m.group(1);
         String ns = aContext.getNamespaceContext().resolvePrefixToNamespace(prefix);
         if (ns != null)
            set.add(ns);
      }

      return set;
   }

   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionAnalyzer#renameNamespacePrefix(org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext, java.lang.String, java.lang.String, java.lang.String)
    */
   public String renameNamespacePrefix(IAeExpressionAnalyzerContext aContext, String aExpression, String aOldPrefix,
         String aNewPrefix)
   {
      // First do ', ", and / cases
      String pattern = "(['\"/\\s\\(<])" + aOldPrefix + ":"; //$NON-NLS-1$ //$NON-NLS-2$
      String replacement = "$1" + aNewPrefix + ":"; //$NON-NLS-1$ //$NON-NLS-2$
      String rval = aExpression.replaceAll(pattern, replacement);
      // Then do /@ case
      pattern = "([/@])" + aOldPrefix + ":"; //$NON-NLS-1$ //$NON-NLS-2$
      rval = rval.replaceAll(pattern, replacement);
      // Now the special case where it's at the beginning of the line
      if (rval.startsWith(aOldPrefix))
      {
         char ch = rval.charAt(aOldPrefix.length());
         if (ch == ':' || ch == '.')
         {
            rval = aNewPrefix + rval.substring(aOldPrefix.length());
         }
      }
      if (aExpression.equals(rval))
      {
         return null;
      }
      else
      {
         return rval;
      }
   }
}

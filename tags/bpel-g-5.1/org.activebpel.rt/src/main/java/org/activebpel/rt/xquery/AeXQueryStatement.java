// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xquery/AeXQueryStatement.java,v 1.1 2008/03/19 16:35:56 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xquery;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.activebpel.rt.AeMessages;

/**
 * A simple class to model an XQuery statement, similar in concept to JDBC's
 * PreparedStatement.  A new statement is constructed by passing a parameterized
 * string to the class's c'tor.  The string is an XQuery with zero or more '?'
 * characters in it.  The '?' characters are intended to be replaced later by
 * real values.
 *
 * All of the expected parameters must be configured via setParameter() or
 * setParameters() otherwise an assertion error will be thrown when getQuery()
 * is called.  Note that parameter indices are one based.
 */
public class AeXQueryStatement
{
   /** Pattern string for an unescaped question mark. */
   private static final String UNESCAPED_QMARK = "([^\\\\])\\?"; //$NON-NLS-1$
   /** Pattern to use when finding the number of params in the xquery. */
   private static final Pattern PARAM_PATTERN = Pattern.compile(AeXQueryStatement.UNESCAPED_QMARK);

   /** The param formatter. */
   private static final AeXQueryStatementParamFormatter sFormatter = new AeXQueryStatementParamFormatter();

   /** The raw xquery (pre-replacement). */
   private String mXQuery;
   /** The query parameter values. */
   private Map mQueryParameters = new HashMap();

   /**
    * C'tor.
    *
    * @param aXQuery
    */
   public AeXQueryStatement(String aXQuery)
   {
      setXQuery(aXQuery);
   }

   /**
    * Sets the parameter with the given index.
    *
    * @param aParameterIndex
    * @param aValue
    */
   public void setParameter(int aParameterIndex, Object aValue)
   {
      if (aParameterIndex < 1)
         throw new AssertionError(AeMessages.getString("AeXQueryStatement.ParamIndexError")); //$NON-NLS-1$
      getQueryParameters().put(new Integer(aParameterIndex), aValue);
   }

   /**
    * Sets multiple parameters.
    *
    * @param aParameters
    */
   public void setParameters(Object [] aParameters)
   {
      for (int i = 0; i < aParameters.length; i++)
      {
         setParameter(i + 1, aParameters[i]);
      }
   }

   /**
    * Does the parameter replacement and returns the resulting XQuery.
    */
   public String getQuery()
   {
      String xquery = getXQuery();

      // Ensure that all parameters that must be set are in fact set.
      validateParameters();

      // Replace the parameters in the XQuery with the parameter values
      // set in the statement.
      Set paramKeys = new TreeSet();
      paramKeys.addAll(getQueryParameters().keySet());
      for (Iterator iter = paramKeys.iterator(); iter.hasNext(); )
      {
         Integer paramIndex = (Integer) iter.next();
         Object value = getQueryParameters().get(paramIndex);
         String formattedValue = sFormatter.formatParameterValue(value);
         formattedValue = Matcher.quoteReplacement(formattedValue);
         xquery = xquery.replaceFirst(UNESCAPED_QMARK, "$1" + formattedValue); //$NON-NLS-1$
      }

      // Un-escape any escaped question marks in the XQuery
      xquery = xquery.replaceAll("\\\\\\?", "?"); //$NON-NLS-1$ //$NON-NLS-2$
      return xquery;
   }

   /**
    * Ensures that the parameters that are specified in the xquery are
    * set in the parameter map.
    */
   protected void validateParameters()
   {
      String xquery = getXQuery();

      // Count the # of expected params in the query
      Matcher matcher = PARAM_PATTERN.matcher(xquery);
      int paramCount = 0;
      while (matcher.find())
         paramCount++;

      // Validate that the correct indexes were configured.
      for (int i = 0; i < paramCount; i++)
      {
         if (!getQueryParameters().containsKey(new Integer(i + 1)))
         {
            String pattern = AeMessages.getString("AeXQueryStatement.ParamNotSet"); //$NON-NLS-1$
            Object [] args = new Object[] { new Integer(i + 1) };
            throw new AssertionError(MessageFormat.format(pattern, args));
         }
      }

      // Validate the number of parameters is correct.
      if (paramCount != getQueryParameters().size())
      {
         String pattern = AeMessages.getString("AeXQueryStatement.IncorrectNumberOfParams"); //$NON-NLS-1$
         Object [] args = new Object[] { new Integer(getQueryParameters().size()), new Integer(paramCount) };
         throw new AssertionError(MessageFormat.format(pattern, args));
      }
   }

   /**
    * @return Returns the xQuery.
    */
   protected String getXQuery()
   {
      return mXQuery;
   }

   /**
    * @param aQuery the xQuery to set
    */
   protected void setXQuery(String aQuery)
   {
      mXQuery = aQuery.trim();
   }

   /**
    * @return Returns the parameters.
    */
   protected Map getQueryParameters()
   {
      return mQueryParameters;
   }

   /**
    * @param aParameters the parameters to set
    */
   protected void setQueryParameters(Map aParameters)
   {
      mQueryParameters = aParameters;
   }
}

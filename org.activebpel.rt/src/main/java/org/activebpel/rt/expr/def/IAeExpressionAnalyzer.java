//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/expr/def/IAeExpressionAnalyzer.java,v 1.2 2008/02/17 21:09:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.expr.def;

import java.util.List;
import java.util.Set;

/**
 * This interface should be implemented in order to provide an implementation of an
 * expression util (used by the AWF Designer to analyze and manipulate expressions).  
 * An expression util object is capable of doing various miscellaneous tasks on an
 * expression found in a BPEL process.  The AWF Designer often has need to analyze the
 * structure of an expression in order to implement certain features (for example, the
 * ability to search for all uses of a Variable).  Each expression language must 
 * provide an implementation of this interface in order for the Designer to be fully
 * functional with respect to BPEL processes that utilize expressions written in that
 * language.
 */
public interface IAeExpressionAnalyzer
{
   /**
    * This method returns a list of AeVariableProperty objects that represent all of the
    * variable properties used in the expression.
    * 
    * @param aExpression
    */
   public List getVarPropertyList(IAeExpressionAnalyzerContext aContext, String aExpression);

   /**
    * This method returns a list of AeVariableData objects that represent all of the 
    * variables used/referenced in the expression.
    * 
    * @param aExpression
    */
   public List getVarDataList(IAeExpressionAnalyzerContext aContext, String aExpression);

   /**
    * This method returns a Set of variable names of all the variables used in the 
    * expression.
    * 
    * @param aExpression
    */
   public Set getVariables(IAeExpressionAnalyzerContext aContext, String aExpression);
   
   /**
    * This method returns a Set of stylesheets that are referred to in calls to bpel:doXslTransform()
    * functions in the expression.
    * 
    * @param aContext
    * @param aExpression
    */
   public Set getStylesheetURIs(IAeExpressionAnalyzerContext aContext, String aExpression);
   
   /**
    * This method is called to rename a variable that is used in an expression.  The method
    * is called with the expression to modify, the old name of the variable, the new name
    * of the variable, and the util context.  The method will return a new expression if it
    * has been altered or null if no changes were made.
    * @param aContext
    * @param aExpression
    * @param aOldVarName
    * @param aNewVarName
    */
   public String renameVariable(IAeExpressionAnalyzerContext aContext, String aExpression, String aOldVarName, String aNewVarName);

   /**
    * This method is called to rename a namespace prefix that is used in the expression.  The method
    * is called with the expression to modify, the old namespace prefix, the new namespace prefix,
    * and the util context.  The method will return a new (modified) expression if it has been
    * altered or null if no changes were made.
    * 
    * @param aContext
    * @param aExpression
    * @param aOldPrefix
    * @param aNewPrefix
    */
   public String renameNamespacePrefix(IAeExpressionAnalyzerContext aContext, String aExpression, String aOldPrefix, String aNewPrefix);

   /**
    * This method gets the set of namespaces found in the expression.
    * @param aContext
    * @param aExpression
    */
   public Set getNamespaces(IAeExpressionAnalyzerContext aContext, String aExpression);

   /**
    * This method is called to parse an expression to-spec and return the expression's component
    * parts.  See the documentation for <code>AeExpressionToSpecDetails</code> for more details.
    * If the expression is not a valid to-spec expression (based on language specific 
    * restrictions), then this method should return null.
    * 
    * @param aContext
    * @param aExpression
    */
   public AeExpressionToSpecDetails parseExpressionToSpec(IAeExpressionAnalyzerContext aContext,
         String aExpression);
   
   /**
    * A class that holds the result of parsing an expression to-spec.  Each expression language
    * will impose restrictions on the form of the expression that can be specified in the
    * to-spec such that the expression can be parsed into the following basic parts:
    * 
    * <ul>
    *   <li>Variable Name</li>
    *   <li>Part Name (optional)</li>
    *   <li>Query Language (optional)</li>
    *   <li>Query (optional)</li>
    * </ul>
    */
   public class AeExpressionToSpecDetails
   {
      /** The variable name. */
      private String mVariableName;
      /** The part name. */
      private String mPartName;
      /** The query language. */
      private String mQueryLanguage;
      /** The query. */
      private String mQuery;

      /**
       * Constructor.
       */
      public AeExpressionToSpecDetails(String aVariableName, String aPartName, String aQueryLanguage, String aQuery)
      {
         mVariableName = aVariableName;
         mPartName = aPartName;
         mQueryLanguage = aQueryLanguage;
         mQuery = aQuery;
      }

      /**
       * @return Returns the partName.
       */
      public String getPartName()
      {
         return mPartName;
      }

      /**
       * @return Returns the query.
       */
      public String getQuery()
      {
         return mQuery;
      }

      /**
       * @return Returns the queryLanguage.
       */
      public String getQueryLanguage()
      {
         return mQueryLanguage;
      }

      /**
       * @return Returns the variableName.
       */
      public String getVariableName()
      {
         return mVariableName;
      }
   };
}

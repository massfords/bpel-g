// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/expr/def/AeScriptVarDef.java,v 1.1 2008/01/25 20:41:50 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.expr.def;

import javax.xml.namespace.QName;

/**
 * This class represents a variable reference found in an expression.
 */
public class AeScriptVarDef
{
   /** The variable's namespace. */
   private String mNamespace;
   /** The variable name. */
   private String mName;
   /** Any relative query path expression that might be associated with this variable. */
   private String mQuery;

   /**
    * Constructs a variable from the variable QName.
    * 
    * @param aNamespace
    * @param aName
    * @param aQuery
    */
   public AeScriptVarDef(String aNamespace, String aName, String aQuery)
   {
      setNamespace(aNamespace);
      setName(aName);
      setQuery(aQuery);
   }

   /**
    * Constructs a variable def from a QName and query.
    * 
    * @param aQName
    * @param aQuery
    */
   public AeScriptVarDef(QName aQName, String aQuery)
   {
      this(aQName.getNamespaceURI(), aQName.getLocalPart(), aQuery);
   }

   /**
    * @return Returns the name.
    */
   public String getName()
   {
      return mName;
   }

   /**
    * @param aName The name to set.
    */
   protected void setName(String aName)
   {
      mName = aName;
   }

   /**
    * @return Returns the namespace.
    */
   public String getNamespace()
   {
      return mNamespace;
   }

   /**
    * @param aNamespace The namespace to set.
    */
   protected void setNamespace(String aNamespace)
   {
      mNamespace = aNamespace;
   }

   /**
    * Gets the function's fully qualified name.
    */
   public QName getQName()
   {
      return new QName(getNamespace(), getName());
   }

   /**
    * @return Returns the query.
    */
   public String getQuery()
   {
      return mQuery;
   }

   /**
    * @param aQuery The query to set.
    */
   protected void setQuery(String aQuery)
   {
      mQuery = aQuery;
   }
}

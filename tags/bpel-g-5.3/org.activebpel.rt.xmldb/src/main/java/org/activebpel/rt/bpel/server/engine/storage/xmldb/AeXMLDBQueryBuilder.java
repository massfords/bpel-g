//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/AeXMLDBQueryBuilder.java,v 1.5 2008/02/07 18:47:30 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb;

import java.text.MessageFormat;
import java.util.List;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.impl.list.IAeListingFilter;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xmldb.AeMessages;

/**
 * A XMLDB query builder creates a XMLDB query (typically an XQuery) from a filter.
 */
public abstract class AeXMLDBQueryBuilder extends AeXMLDBObject
{
   /** the query return pattern for selecting a range of results. */
   protected static final String QUERY_POSITION_PATTERN = "[ position() >= {0,number,#} and position() <= {1,number,#} ]"; //$NON-NLS-1$
   /** the query return pattern to use for the xquery */
   protected static final String QUERY_RETURN_PATTERN = "return ( <Count>'{ count($rset) }'</Count> , $rset{0} )"; //$NON-NLS-1$

   /** The limit on the # of items to retrieve from the DB */
   public static final int LIMIT = 500;

   /** The filter used to form this query (if any). */
   private IAeListingFilter mFilter;
   /** The flag indicating whether we should return only the count (for select statements). */
   private boolean mCountOnlyFlag;
   /** The key to use for locating the statements for this query. */
   private String mKey;
   /** A flag indicating whether to use the query limit. */
   private boolean mUseLimit;

   /**
    * Creates a xmldb query builder.
    * 
    * @param aFilter
    * @param aXMLDBConfig
    * @param aPrefix
    * @param aKey
    * @param aStorageImpl
    */
   public AeXMLDBQueryBuilder(IAeListingFilter aFilter, AeXMLDBConfig aXMLDBConfig, String aPrefix,
         String aKey, IAeXMLDBStorageImpl aStorageImpl)
   {
      super(aXMLDBConfig, aPrefix, aStorageImpl);
      setFilter(aFilter);
      setKey(aKey);
      setUseLimit(aFilter.getMaxReturn() > 0);
   }
   
   /**
    * This method is called to determine which doc types to count when tallying the number of
    * doc instances that have been deleted.  In some cases, we will delete various types of
    * documents, but we only want to return the # of "primary" instances deleted.  If null is
    * returned from this method, all instances will be counted.
    */
   public String getDeletedDocumentType()
   {
      return null;
   }

   /**
    * Formats a List of AND clauses (for use in the where portion of a FLWOR expression).  This amounts
    * to creating a string that starts with 'where' and appends each item in the List to the String, separating
    * each additional clause by the word 'and'.
    * 
    * @param aAndClauses
    */
   protected static String joinAndClauseList(List aAndClauses)
   {
      return joinAndClauseList(aAndClauses, true);
   }
   
   /**
    * Formats a List of AND clauses (for use in the where portion of a FLWOR expression).  This amounts
    * to creating a string that starts with 'where' and appends each item in the List to the String, separating
    * each additional clause by the word 'and'.
    * 
    * @param aAndClauses
    * @param aAppendWhere
    */
   protected static String joinAndClauseList(List aAndClauses, boolean aAppendWhere)
   {
      StringBuffer buff = new StringBuffer();
      if (aAndClauses.size() > 0)
      {
         synchronized (buff)
         {
            if (aAppendWhere)
               buff.append("where "); //$NON-NLS-1$
            else
               buff.append(" and "); //$NON-NLS-1$
            buff.append(AeUtil.joinToStringObjects(aAndClauses, " and ")); //$NON-NLS-1$
         }
      }
      return buff.toString();
   }
   
   /**
    * Appends a string condition to the and clauses.  If the string contains a wild card
    * (*) character it will be processed by this method.  If the string value is null or
    * empty no condition is added.
    * @param aColumn
    * @param aValue
    * @param aAndClauses
    */
   protected void appendStringCondition(String aColumn, String aValue, List aAndClauses)
   {
      if (AeUtil.notNullOrEmpty(aValue))
      {
         if (aValue.indexOf('*') == -1)
            aAndClauses.add(aColumn + " = '" + aValue + "'"); //$NON-NLS-1$ //$NON-NLS-2$
         else
            aAndClauses.add("tf:containsText(" + aColumn + ", \"" + aValue + "\")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      }
   }
   
   /**
    * Appends a string to the buffer.  If the string is required and empty or null, this method will
    * throw an exception.
    * 
    * @param aString
    * @param aBuffer
    * @param aRequired
    */
   protected void appendToBuffer(String aString, StringBuffer aBuffer, boolean aRequired) throws AeException
   {
      if (AeUtil.isNullOrEmpty(aString))
      {
         if (aRequired)
         {
            throw new AeException(AeMessages.getString("AeXMLDBQueryBuilder.REQUIRED_SECTION_OF_FLWOR_MISSING_ERROR")); //$NON-NLS-1$
         }
      }
      else
      {
         aBuffer.append(aString);
         aBuffer.append('\n');
      }
   }

   /**
    * Appends a string to the buffer.  Assumes that the string is not required.
    * 
    * @param aString
    * @param aBuffer
    * @throws AeException
    */
   protected void appendToBuffer(String aString, StringBuffer aBuffer) throws AeException
   {
      appendToBuffer(aString, aBuffer, false);
   }

   /**
    * Appends the Count query to the buffer.
    * 
    * @param aBuffer
    */
   protected void appendCountQuery(StringBuffer aBuffer) throws AeException
   {
      /*
       * Example:
       * 
       * count(
       *    for $proc in input()/AeProcess
       *    where $proc/Name/LocalPart = 'xquery_process'
       *    return
       *       $proc
       * )
       */
      appendToBuffer(getDecl(), aBuffer);
      aBuffer.append("let $count := count("); //$NON-NLS-1$
      appendToBuffer(getFor(), aBuffer, true);
      appendToBuffer(getWhere(), aBuffer);
      appendToBuffer(getReturn(), aBuffer, true);
      aBuffer.append(" ) "); //$NON-NLS-1$
      aBuffer.append("return <Count>{ $count }</Count>"); //$NON-NLS-1$
   }

   /**
    * Appends the Select query to the buffer.
    * 
    * @param aBuffer
    */
   protected void appendSelectQuery(StringBuffer aBuffer) throws AeException
   {
      appendToBuffer(getDecl(), aBuffer);
      aBuffer.append("let $rset := ("); //$NON-NLS-1$
      appendToBuffer(getFor(), aBuffer, true);
      appendToBuffer(getWhere(), aBuffer);
      appendToBuffer(getOrderBy(), aBuffer);
      appendToBuffer(getReturn(), aBuffer, true);
      if (isUseLimit())
      {
         aBuffer.append(" ) [ position() <= " + (LIMIT + 1) + " ]\n"); //$NON-NLS-1$ //$NON-NLS-2$
      }
      else
      {
         aBuffer.append(" ) \n"); //$NON-NLS-1$
      }
      aBuffer.append(createQueryReturn());
   }
   
   /**
    * Builds the query given the filter.
    * 
    * @throws AeException
    */
   public String buildQuery() throws AeException
   {
      StringBuffer buff = new StringBuffer();
      synchronized (buff)
      {
         if (isCountOnlyFlag())
         {
            appendCountQuery(buff);
         }
         else
         {
            appendSelectQuery(buff);
         }
      }
      return buff.toString();
   }

   /**
    * Builds a XMLDB update XQuery capable of deleting the doc instances that match the filter.
    * 
    * @throws AeException
    */
   public String buildDeleteQuery() throws AeException
   {
      StringBuffer buff = new StringBuffer();
      synchronized (buff)
      {
         appendToBuffer(getDecl(), buff);
         appendToBuffer(getUpdate(), buff, true);
         appendToBuffer(getWhere(), buff);
         appendToBuffer(getDo(), buff, true);
      }
      return buff.toString();
   }

   /**
    * Returns the 'decl' portion of the XQuery (FLWOR) query.  This section has namespace 
    * declarations needed by the xquery.
    */
   protected String getDecl()
   {
      String key = resolveXQueryKey(getKey()) + ".decl"; //$NON-NLS-1$
      return getXMLDBConfig().getXQueryStatement(key);
   }

   /**
    * Creates the XQuery return value used to wrap the query result in order to return the values
    * expected by the response handler.  The xquery will return the total count of items in the query
    * but only return the # requested by the filter.
    */
   protected String createQueryReturn()
   {
      String positionCrit = createPositionCriteria();
      return MessageFormat.format(QUERY_RETURN_PATTERN, new Object[] { positionCrit });
   }

   /**
    * Creates the criteria used for choosing only those instances we want based on the positional
    * data in the filter.
    */
   protected String createPositionCriteria()
   {
      if (getFilter().getMaxReturn() > 0)
      {
         Object [] params = {
               new Integer(getFilter().getListStart() + 1),
               new Integer(getFilter().getListStart() + getFilter().getMaxReturn())
         };
         return MessageFormat.format(QUERY_POSITION_PATTERN, params);
      }
      else
      {
         return ""; //$NON-NLS-1$
      }
   }
   
   /**
    * Returns the 'for' (and 'let') portion of the XQuery (FLWOR) query.
    */
   protected String getFor() throws AeException
   {
      String key = resolveXQueryKey(getKey()) + ".for"; //$NON-NLS-1$
      return getXMLDBConfig().getXQueryStatement(key);
   }

   /**
    * Returns the 'update' portion of the XQuery (FLWU) query.
    */
   protected String getUpdate() throws AeException
   {
      String key = resolveXQueryKey(getKey()) + ".update"; //$NON-NLS-1$
      return getXMLDBConfig().getXQueryStatement(key);
   }

   /**
    * Returns the 'where' portion of the XQuery (FLWOR) query.  This will typically be overridden by
    * subclasses.
    */
   protected String getWhere() throws AeException
   {
      String key = resolveXQueryKey(getKey()) + ".where"; //$NON-NLS-1$
      return getXMLDBConfig().getXQueryStatement(key);
   }

   /**
    * Returns the 'order by' portion of the XQuery (FLWOR) query.
    */
   protected String getOrderBy() throws AeException
   {
      String key = resolveXQueryKey(getKey()) + ".orderby"; //$NON-NLS-1$
      return getXMLDBConfig().getXQueryStatement(key);
   }

   /**
    * Returns the 'return' portion of the XQuery (FLWOR) query.
    */
   protected String getReturn() throws AeException
   {
      String key = resolveXQueryKey(getKey()) + ".return"; //$NON-NLS-1$
      return getXMLDBConfig().getXQueryStatement(key);
   }

   /**
    * Returns the 'do' portion of the XQuery (FLWU) query.
    */
   protected String getDo() throws AeException
   {
      String key = resolveXQueryKey(getKey()) + ".do"; //$NON-NLS-1$
      return getXMLDBConfig().getXQueryStatement(key);
   }

   /**
    * @return Returns the key.
    */
   protected String getKey()
   {
      return mKey;
   }
   
   /**
    * @param aKey The key to set.
    */
   protected void setKey(String aKey)
   {
      mKey = aKey;
   }
   
   /**
    * @return Returns the filter.
    */
   protected IAeListingFilter getFilter()
   {
      return mFilter;
   }
   
   /**
    * @param aFilter The filter to set.
    */
   protected void setFilter(IAeListingFilter aFilter)
   {
      mFilter = aFilter;
   }
   
   /**
    * @return Returns the useLimit.
    */
   public boolean isUseLimit()
   {
      return mUseLimit;
   }
   
   /**
    * @param aUseLimit The useLimit to set.
    */
   public void setUseLimit(boolean aUseLimit)
   {
      mUseLimit = aUseLimit;
   }

   /**
    * @return Returns the countOnlyFlag.
    */
   protected boolean isCountOnlyFlag()
   {
      return mCountOnlyFlag;
   }

   /**
    * @param aCountOnlyFlag The countOnlyFlag to set.
    */
   public void setCountOnlyFlag(boolean aCountOnlyFlag)
   {
      mCountOnlyFlag = aCountOnlyFlag;
   }
}

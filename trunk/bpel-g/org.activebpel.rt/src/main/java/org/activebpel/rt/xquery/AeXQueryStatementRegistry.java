// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xquery/AeXQueryStatementRegistry.java,v 1.1 2008/03/19 16:35:56 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xquery;

import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.AeMessages;
import org.activebpel.rt.util.AeXPathUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * This class loads and manages a set of XQuery statements configured
 * in an XML configuration file.
 */
public class AeXQueryStatementRegistry
{
   /** The statement file's namespace. */
   private static final String STATEMENT_FILE_NS = "http://schemas.active-endpoints.com/xqueryStatements/2008/03/xqueryStatements.xsd"; //$NON-NLS-1$

   /** The xqueries loaded from the XML config file. */
   private Map mXQueries;

   /**
    * C'tor.
    */
   protected AeXQueryStatementRegistry()
   {
      setXQueries(new HashMap());
   }

   /**
    * C'tor.
    *
    * @param aDocument
    */
   public AeXQueryStatementRegistry(Document aDocument)
   {
      this();
      loadStatements(aDocument.getDocumentElement());
   }

   /**
    * C'tor.
    *
    * @param aElement
    */
   public AeXQueryStatementRegistry(Element aElement)
   {
      this();
      loadStatements(aElement);
   }

   /**
    * C'tor.
    *
    * @param aClass class to use to find and load the named XML resource
    * @param aResourceName name of an XML file resource on the classpath
    */
   public AeXQueryStatementRegistry(URL aResourceUrl)
   {
      this();
      InputSource iSource = new InputSource(aResourceUrl.toExternalForm());
      try
      {
         Document document = new AeXMLParserBase(true, false).loadDocument(iSource, null);
         loadStatements(document.getDocumentElement());
      }
      catch (AeException ex)
      {
         AeException.logError(ex);
      }
   }

   /**
    * Returns a new xquery statement for the given key into the map
    * of xqueries.
    *
    * @param aXQueryKey
    */
   public AeXQueryStatement getXQueryStatement(String aXQueryKey)
   {
      String query = (String) getXQueries().get(aXQueryKey);
      if (query == null)
         throw new AssertionError(MessageFormat.format(AeMessages.getString("AeXQueryStatementRegistry.StatementNotFound"), new Object[] { aXQueryKey })); //$NON-NLS-1$
      else
         return new AeXQueryStatement(query);
   }

   /**
    * Returns a new xquery statement (with the parameters set to those
    * given) for the given key into the map of xqueries.
    *
    * @param aXQueryKey
    * @param aParameters
    */
   public AeXQueryStatement getXQueryStatement(String aXQueryKey, Object [] aParameters)
   {
      AeXQueryStatement statement = getXQueryStatement(aXQueryKey);
      if (aParameters != null)
         statement.setParameters(aParameters);
      return statement;
   }

   /**
    * Loads all of the statements from the given DOM.
    *
    * @param aElement
    */
   protected void loadStatements(Element aElement)
   {
      try
      {
         String prefix = "aexq"; //$NON-NLS-1$
         List nodes = AeXPathUtil.selectNodes(aElement, "/aexq:statements/aexq:statement", prefix, STATEMENT_FILE_NS); //$NON-NLS-1$
         for (Iterator iter = nodes.iterator(); iter.hasNext(); )
         {
            Element statementElem = (Element) iter.next();
            String name = AeXPathUtil.selectText(statementElem, "aexq:name", prefix, STATEMENT_FILE_NS); //$NON-NLS-1$
            String xquery = AeXPathUtil.selectText(statementElem, "aexq:xquery", prefix, STATEMENT_FILE_NS); //$NON-NLS-1$
            getXQueries().put(name, xquery);
         }
      }
      catch (AeException ex)
      {
         AeException.logError(ex);
      }
   }

   /**
    * @return Returns the xQueries.
    */
   protected Map getXQueries()
   {
      return mXQueries;
   }

   /**
    * @param aQueries the xQueries to set
    */
   protected void setXQueries(Map aQueries)
   {
      mXQueries = aQueries;
   }
}

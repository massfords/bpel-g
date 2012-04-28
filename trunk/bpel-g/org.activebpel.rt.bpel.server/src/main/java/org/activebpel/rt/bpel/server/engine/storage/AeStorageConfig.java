// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/AeStorageConfig.java,v 1.2 2007/09/21 18:13:05 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is a base for the SQL and Tamino config classes.  It abstracts out the
 * work of loading DB statements (SQL or XQuery) from a config file.  The config files
 * for the SQL and Tamino layers are very similar - this file contains the common logic
 * for loading those files.
 */
public abstract class AeStorageConfig
{
   private static final String PARAMETER_ELEM_NAME = "parameter"; //$NON-NLS-1$
   private static final String CONSTANT_ELEM_NAME = "constant"; //$NON-NLS-1$
   private static final String VALUE_ELEM_NAME = "value"; //$NON-NLS-1$
   private static final String NAME_ELEM_NAME = "name"; //$NON-NLS-1$
   private static final String NAME_ATTR_NAME = "name"; //$NON-NLS-1$

   public static final String PARAMETER_HAS_CASCADING_DELETES           = "HasCascadingDeletes"; //$NON-NLS-1$
   public static final String PARAMETER_SET_TRANSACTION_ISOLATION_LEVEL = "SetTransactionIsolationLevel"; //$NON-NLS-1$

   /** The message that is logged when parsing fails. */
   protected static final String ERROR_PARSING_DOCUMENT = AeMessages.getString("AeDBResourceConfig.FAILED_TO_PARSE_CONFIG_ERROR"); //$NON-NLS-1$

   /** The map of DB statement names/keys to DB statements. */
   private Map<String,String> mStatementMap = new HashMap<String, String>();
   /** map of constant names to values */
   private final Properties mConstantsFromFile = new Properties();
   /** map of name value pairs used to override any constant values */
   private Properties mConstantOverrides = new Properties(mConstantsFromFile);
   /** The name of the xml element that is the root of a key/value pair. */
   private String mStatementRootName;
   /** The name of the xml element that contains the statement value. */
   private String mStatementValueName;

   /**
    * Reloads the config with the new overrides which will replace any previously
    * defined overrides. 
    */
   public void reload(Map<String,String> aOverrides)
   {
      if (!AeUtil.compareObjects(aOverrides, getConstantOverrides()))
      {
         getConstantOverrides().clear();
         getConstantOverrides().putAll(aOverrides);
         reload();
      }
   }
   
   /**
    * Reloads the statements from the config file. This should be overridden by subclasses so they can reload
    * from their own files.
    */
   protected void reload()
   {
      loadStatements();
   }

   /**
    * Returns parameter value from configuration.
    *
    * @param aKey
    */
   public String getParameter(String aKey) throws AeStorageException
   {
      String value = (String) getStatementMap().get(aKey);

      if (value == null)
      {
         throw new AeStorageException(MessageFormat.format(
               AeMessages.getString("AeDBResourceConfig.MISSING_PARAM_IN_CONFIG_ERROR"), new Object[] { aKey })); //$NON-NLS-1$
      }

      return value;
   }

   /**
    * Returns <code>boolean</code> parameter value from SQL configuration.
    *
    * @param aKey A key that identifies the parameter value in the config file.
    */
   public boolean getParameterBoolean(String aKey) throws AeStorageException
   {
      return Boolean.valueOf(getParameter(aKey));
   }

   /**
    * Returns <code>int</code> parameter value from SQL configuration.
    *
    * @param aKey A key that identifies the parameter value in the config file.
    */
   public int getParameterInt(String aKey) throws AeStorageException
   {
      try
      {
         return Integer.parseInt(getParameter(aKey));
      }
      catch (NumberFormatException e)
      {
         throw new AeStorageException(MessageFormat.format(AeMessages.getString("AeDBResourceConfig.INVALID_INTEGER_VALUE_ERROR"), //$NON-NLS-1$
               new Object[] { aKey }), e);
      }
   }

   /**
    * Gets a statement given a key (the name of the statement as configured in the file).
    * 
    * @param aKey A key that references a statement in the config file.
    * @return A statement.
    */
   public String getStatement(String aKey)
   {
      return (String) getStatementMap().get(aKey);
   }

   /**
    * Loads the SQL statements into the hashmap.  This method loads two different
    * XML configuration files.  First it loads the common SQL configuration file,
    * followed by the database specific SQL configuration file.
    */
   public void loadStatements()
   {
      List configFilenames = getStatementConfigFilenames();
       for (Object configFilename : configFilenames) {
           AeFilenameClassTuple entry = (AeFilenameClassTuple) configFilename;
           addStatements(loadStatements(entry.getFilename(), entry.getClassForLoad()));
       }
   }
   
   /**
    * Adds the map to the map of the statements and replaces any constants in the map added.
    * @param aMap
    */
   protected void addStatements(Map<String,String> aMap)
   {
      replaceConstants(aMap);
      mStatementMap.putAll(aMap);
   }

   /**
    * Convenience method that returns true if there are constants available for
    * substitution
    */
   private boolean hasConstants()
   {
      return !getConstantOverrides().isEmpty() || !getConstantsFromFile().isEmpty();
   }

   /**
    * Walks the map of statements replacing the values found within with the
    * declared constants.
    * 
    * TODO (EPW) Break this code out into a util for sharing.
    */
   private void replaceConstants(Map<String,String> aMap)
   {
      if (hasConstants())
      {
         Pattern pattern = Pattern.compile("%(\\w+)%"); //$NON-NLS-1$
         
         // run through the sqlstmts and replace any constants with their values
          for (Entry<String, String> entry : aMap.entrySet()) {
              String stmt = entry.getValue();
              Set<String> firstMatches = new HashSet<String>();

              // Loop until there are no more matches. This allows a constant to
              // make nested references to other constants.
              for (boolean done = false; !done; ) {
                  Matcher matcher = pattern.matcher(stmt);
                  StringBuilder sb = new StringBuilder(stmt.length() * 2);
                  int stmtPosition = 0;

                  // Make one pass through the statement, replacing constants.
                  while (matcher.find()) {
                      // extract name of constant
                      String constant = matcher.group(1);
                      String replacementValue = resolveToken(constant);

                      if (replacementValue == null) {
                          // A minor annoyance is that we will report every missing
                          // constant in every pass, but there really shouldn't be
                          // any missing constants.
                          new AeException(AeMessages.format("AeSQLConfig.ERROR_1", constant)).logError(); //$NON-NLS-1$
                      }
                      // If this is the first match of the pass and this constant
                      // was also the first match of a previous pass, then we have
                      // detected an infinite sequence of nested replacements.
                      else if ((stmtPosition == 0) && !firstMatches.add(constant)) {
                          new AeException(AeMessages.format("AeSQLConfig.ERROR_20", constant)).logError(); //$NON-NLS-1$
                          break;
                      } else {
                          sb.append(stmt.substring(stmtPosition, matcher.start()));
                          sb.append(replacementValue);
                          stmtPosition = matcher.end();
                      }
                  }

                  // If there were no matches, then we're done.
                  if (stmtPosition == 0) {
                      done = true;
                  } else {
                      sb.append(stmt.substring(stmtPosition));
                      stmt = sb.toString();
                  }
              }

              entry.setValue(stmt);
          }
      }
   }

   /**
    * This method does the work of parsing the configuration file and loading
    * all of the SQL statements into the hash map.
    *
    * @param aResourceName The name of the configuration file to load (as a resource).
    * @return A map of statement names to statements.
    */
   protected Map<String,String> loadStatements(String aResourceName, Class aClassForLoading)
   {
      InputStream iStream = null;
      try
      {
         // Get the resource stream.
         iStream = aClassForLoading.getResourceAsStream(aResourceName);
         if (iStream == null)
         {
            throw new AeException(MessageFormat.format(AeMessages.getString("AeDBResourceConfig.ERROR_GETTING_CONFIG_RESOURCE"), //$NON-NLS-1$
                  new Object[] { aResourceName }));
         }
         return loadStatements(iStream);
      }
      catch (Exception e)
      {
         AeException.logError(e, ERROR_PARSING_DOCUMENT);
         return Collections.emptyMap();
      }
      finally
      {
         AeCloser.close(iStream);
      }
   }

   /**
    * Given an input stream, this method uses a SAX parser to parse through the
    * XML document to pull out the SQL statements.
    *
    * @param aStream An open input stream that points to the XML document.
    * @return A map of SQL statement names to SQL statements.
    * @throws Exception
    */
   protected Map<String,String> loadStatements(InputStream aStream) throws Exception
   {
      AeXMLParserBase parser = new AeXMLParserBase();
      parser.setValidating(false);
      parser.setNamespaceAware(false);

      Document doc = parser.loadDocument(aStream, null);
      
      extractConstants(doc);

      return extractStatementMap(doc);
   }

   /**
    * Extracts the constants from the Document.
    * 
    * @param aDocument
    */
   private void extractConstants(Document aDocument)
   {
      NodeList nl = aDocument.getElementsByTagName(CONSTANT_ELEM_NAME);
      for (int i = 0; i < nl.getLength(); i++)
      {
         Element constantElem = (Element) nl.item(i);
         String name = constantElem.getAttribute(NAME_ATTR_NAME).trim();
         String constant = AeXmlUtil.getText(constantElem).trim();
         getConstantsFromFile().setProperty(name, constant);
      }      
   }
   
   /**
    * Extracts the SQL statements from the Document.
    * 
    * @param aDocument
    */
   private Map<String, String> extractStatementMap(Document aDocument)
   {
      Map<String, String> rval = new HashMap<String, String>();

      NodeList nl = aDocument.getElementsByTagName(getStatementRootName());
      for (int i = 0; i < nl.getLength(); i++)
      {
         Element elem = (Element) nl.item(i);
         Element nameElem = (Element) elem.getElementsByTagName(NAME_ELEM_NAME).item(0);
         Element statementElem = (Element) elem.getElementsByTagName(getStatementValueName()).item(0);
         String name = AeXmlUtil.getText(nameElem).trim();
         String statement = AeXmlUtil.getText(statementElem).trim();
         rval.put(name, statement);
      }

      nl = aDocument.getElementsByTagName(PARAMETER_ELEM_NAME);
      for (int i = 0; i < nl.getLength(); i++)
      {
         Element elem = (Element) nl.item(i);
         Element nameElem = (Element) elem.getElementsByTagName(NAME_ELEM_NAME).item(0);
         Element valueElem = (Element) elem.getElementsByTagName(VALUE_ELEM_NAME).item(0);
         String name = AeXmlUtil.getText(nameElem).trim();
         String value = AeXmlUtil.getText(valueElem).trim();
         rval.put(name, value);
      }
      
      return rval;
   }

   /**
    * Resolves a token with its constant value. The override map is consulted first
    * for a value and then the constants map. 
    * 
    * @param aToken
    * @return String or null indicating that the no value was found
    */
   private String resolveToken(String aToken)
   {
      return getConstantOverrides().getProperty(aToken);
   }

   /**
    * @return Returns the constantOverrides.
    */
   public Properties getConstantOverrides()
   {
      return mConstantOverrides;
   }
   
   public void setConstantOverrides(Properties aProps) {
       mConstantOverrides =aProps;
   }

   /**
    * @return Returns the constants.
    */
   protected Properties getConstantsFromFile()
   {
      return mConstantsFromFile;
   }
   
   /**
    * @return Returns the statement map.
    */
   protected Map getStatementMap()
   {
      return mStatementMap;
   }

   /**
    * Returns a list of DB resource config filenames.  Each file will be loaded and the
    * statements found therein will be loaded into the statement map.
    */
   protected abstract List<AeFilenameClassTuple> getStatementConfigFilenames();

   /**
    * This internal class is used by the <code>getStatementConfigFilenames</code> method to 
    * return a list of (filename, class-for-loading) pairs.
    */
   protected class AeFilenameClassTuple
   {
      /** The filename of a config file to load. */
      private final String mFilename;
      /** The class to use for finding the config resource. */
      private final Class mClassForLoad;

      /**
       * Constructor.
       * 
       * @param aFilename
       * @param aClassForLoad
       */
      public AeFilenameClassTuple(String aFilename, Class aClassForLoad)
      {
         mFilename = aFilename;
         mClassForLoad = aClassForLoad;
      }
      
      /**
       * @return Returns the filename.
       */
      public String getFilename()
      {
         return mFilename;
      }

      /**
       * @return Returns the classForLoad.
       */
      public Class getClassForLoad()
      {
         return mClassForLoad;
      }
   }

   /**
    * @param aStatementMap The statementMap to set.
    */
   protected void setStatementMap(Map<String,String> aStatementMap)
   {
      mStatementMap = aStatementMap;
   }

   /**
    * @return Returns the statementRootName.
    */
   public String getStatementRootName()
   {
      return mStatementRootName;
   }

   /**
    * @param aStatementRootName The statementRootName to set.
    */
   public void setStatementRootName(String aStatementRootName)
   {
      mStatementRootName = aStatementRootName;
   }

   /**
    * @return Returns the statementValueName.
    */
   public String getStatementValueName()
   {
      return mStatementValueName;
   }

   /**
    * @param aStatementValueName The statementValueName to set.
    */
   public void setStatementValueName(String aStatementValueName)
   {
      mStatementValueName = aStatementValueName;
   }
}

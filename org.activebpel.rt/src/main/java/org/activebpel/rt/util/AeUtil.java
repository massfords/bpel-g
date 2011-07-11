// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeUtil.java,v 1.82.2.3 2008/05/05 21:25:25 KPease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.dgc.VMID;
import java.security.CodeSource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.ProtectionDomain;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.AeMessages;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility methods
 */
public class AeUtil
{
   private static final String ENTITY_AMPERSAND   = "&amp;"; //$NON-NLS-1$
   private static final String ENTITY_GREATERTHAN = "&gt;"; //$NON-NLS-1$
   private static final String ENTITY_LESSTHAN    = "&lt;"; //$NON-NLS-1$
   private static final String ENTITY_QUOTE       = "&quot;"; //$NON-NLS-1$

   /** Separator for project-relative paths. */
   public static final String PROJECT_PATH_SEPARATOR = "/"; //$NON-NLS-1$
   /** Prefix for project-relative URI. */
   public static final String PROJECT_RELATIVE_URI = "project:"; //$NON-NLS-1$
   /** Constant value indicating if the current platform file system is Unix. */
   public static final boolean UNIX_FS = java.io.File.separatorChar == '/';
   /** Unique ID of this VM **/
   private static VMID sVmid = new VMID();
   /** Generates cryptographically strong random numbers **/
   private static SecureRandom sRandom;
   /** SHA message digest */
   private static MessageDigest sShaDigest;
   /** Regular expression to match a number. */
   private static Pattern sNumberRegEx =  Pattern.compile("\\s*[-+]?[0-9]*\\.?[0-9]+", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE); //$NON-NLS-1$

   /**
    * Private ctor to prevent instantiations
    */
   private AeUtil()
   {
   }

   /**
    * Initialize the SHA MessageDigest and SecureRandom
    * instances upfront
    */
   static
   {
      try
      {
         sShaDigest = MessageDigest.getInstance("SHA"); //$NON-NLS-1$
         sRandom = new SecureRandom();
      }
      catch (NoSuchAlgorithmException nsa)
      {
         AeException.logError(nsa);
      }
   }


   /**
    * Convert "true" or "false" to the appropriate boolean value.  If the arg
    * is not null, it will be converted to lowercase before evaluation.
    * Anything other than "true" will return false.
    * @param aTrueFalseString
    */
   public static boolean toBoolean( String aTrueFalseString )
   {
      String value = getSafeString(aTrueFalseString).toLowerCase();
      return Boolean.valueOf( value ).booleanValue();
   }

   /**
    * Returns the location this class was loaded from (which .jar file or class directory)
    * or null if that location cannot be determined.  Note: if the class was loaded by the
    * system classloader, this method will return null.
    *
    * @param aClass
    */
   public static String getLocationForClass(Class<?> aClass)
   {
      try
      {
         ProtectionDomain pDomain = aClass.getProtectionDomain();
         CodeSource cSource = pDomain.getCodeSource();
         URL loc = cSource.getLocation();
         return loc.toString();
      }
      catch (Exception e)
      {
         return null;
      }
   }

   /**
    * Returns true if the qname object is null or empty.
    *
    * @param aQName
    */
   public static boolean isNullOrEmpty(QName aQName)
   {
      return (aQName == null) || (isNullOrEmpty(aQName.getNamespaceURI()) && isNullOrEmpty(aQName.getLocalPart()));
   }

   /**
    * Returns true if the map object is null or of size 0.
    *
    * @param aMap A map that may be empty or null.
    * @return True if the map is null or empty.
    */
   public static boolean isNullOrEmpty(Map<?,?> aMap)
   {
      return (aMap == null) || aMap.isEmpty();
   }

   /**
    * Returns true if the array object is null or of size 0.
    *
    * @param aArray
    * @return boolean
    */
   public static boolean isNullOrEmpty(Object [] aArray)
   {
      return (aArray == null) || (aArray.length == 0);
   }

   /**
    * Returns true if the string object is null or doesn't contain at least one
    * non-space character.
    *
    * @param aString The string to check.
    *
    * @return boolean
    */
   public static boolean isNullOrEmpty( String aString )
   {
      boolean result = true ;
      if ( aString != null )
         if ( aString.trim().length() > 0 )
            result = false ;

      return result ;
   }

   /**
    * Helper method that offers the negation of <code>isNullOrEmpty</code>
    *
    * @param aString
    */
   public static boolean notNullOrEmpty(String aString)
   {
      return !isNullOrEmpty(aString);
   }

   /**
    * Helper method that offers the negation of <code>isNullOrEmpty</code>
    *
    * @param aMap
    */
   public static boolean notNullOrEmpty(Map<?,?> aMap)
   {
      return !isNullOrEmpty(aMap);
   }

   /**
    * Helper method that offers the negation of <code>isNullOrEmpty</code>
    *
    * @param aCollection
    */
   public static boolean notNullOrEmpty(Collection<?> aCollection)
   {
      return !isNullOrEmpty(aCollection);
   }

   /**
    * Helper method that offers the negation of <code>isNullOrEmpty</code>
    *
    * @param aQName
    */
   public static boolean notNullOrEmpty(QName aQName)
   {
      return !isNullOrEmpty(aQName);
   }

   /**
    * Returns true if the collection passed in is either null or empty.
    * @param aCollection
    */
   public static boolean isNullOrEmpty(Collection<?> aCollection)
   {
      return aCollection == null || aCollection.isEmpty();
   }
   
   /**
    * Returns true if the Node passed in is either null or empty.
    * @param aNode
    */
   public static boolean isNullOrEmpty(Node aNode)
   {
      return aNode == null || !(aNode.hasChildNodes() || aNode.hasAttributes());
   }
   
   /**
    * Helper method that offers the negation of <code>isNullOrEmpty</code>
    *
    * @param aNode
    */
   public static boolean notNullOrEmpty(Node aNode)
   {
      return !isNullOrEmpty(aNode);
   }
   

   /**
    * Compares 2 objects for equality and either object or both objects can be
    * null.
    * @param aOne can be null
    * @param aTwo can be null
    * @return true if the objects are equal
    */
   public static boolean compareObjects(Object aOne, Object aTwo)
   {
      boolean same = false;
      if(aOne != null)
      {
         if(aOne.equals(aTwo))
             same = true;
      }
      else if(aTwo == null)
      {
         same = true;
      }
      return same;
   }

   /**
    * Performs String comparison similar to
    * {@link #compareObjects(Object, Object)}, and also provides an option to
    * decide whether null and empty strings are to be treated equal. Whitespaces
    * are trimmed before comparison.
    * 
    * @param aOne
    * @param aTwo
    * @param aNullEqualsEmptyString
    *            If true, null strings and empty strings are considered equal.
    * @return true if the objects are equal, or both strings are null or empty
    */
   public static boolean compareObjects(String aOne, String aTwo,
            boolean aNullEqualsEmptyString)
   {
      if (!aNullEqualsEmptyString)
      {
         // The comparison is straightforward
         return AeUtil.compareObjects(aOne, aTwo);
      }
      else
      {
         if (!AeUtil.compareObjects(aOne, aTwo))
         {
            // The previous compare considers all scenarios except if one of
            // them is null and the other is an empty string
            if ((null == aOne && 0 == aTwo.trim().length())
                     || (null == aTwo && 0 == aOne.trim().length()))
            {
               // There is no change
               return true;
            }
            else
            {
               // There has been a change
               return false;
            }
         }
         else
         {
            // There is no change
            return true;
         }
      }
   }

   /**
    * Returns a <code>java.util.List</code> that contains all of the objects
    * from the iterator passed in.
    * @param aIterator
    */
   public static <T> List<T> toList(Iterator<T> aIterator)
   {
      List<T> list = new ArrayList<T>();
      while(aIterator != null && aIterator.hasNext())
         list.add(aIterator.next());
      return list;
   }

   /**
    * Returns a <code>java.util.List</code> that contains all of the String values
    * from the given comma (or other delimiter) separated value string.
    * @param aCsvList list of string values separated by the given separator
    * @param aSeparator value delimiter
    * @return List containing String objects.
    */
   public static List<String> toList(String aCsvList, String aSeparator)
   {
      List<String> list = new ArrayList<String>();
      StringTokenizer st = new StringTokenizer( getSafeString(aCsvList), aSeparator);
      while (st.hasMoreTokens() )
      {
         list.add( st.nextToken().trim() );
      }
      return list;
   }

   /**
    * Joins an object and an iterator.
    *
    * @param aObject
    * @param aIterator
    */
   public static <T> Iterator<? extends T> join(T aObject, Iterator<? extends T> aIterator)
   {
      if (aObject == null)
      {
         return aIterator;
      }
      return joinIter(Collections.singleton(aObject).iterator(), aIterator);
   }

   /**
    * Joins two iterators together into a third.
    * @param aFirstIterator can be null
    * @param aSecondIterator can be null
    */
   public static <T> Iterator<? extends T> joinIter(Iterator<? extends T> aFirstIterator, Iterator<? extends T> aSecondIterator)
   {
      return AeSequenceIterator.join(aFirstIterator, aSecondIterator);
   }

   /**
    * Creates an iterator that includes the data from the first as well as the
    * object passed in.
    * @param aIter can be null
    * @param aObject can be null
    */
   public static <T> Iterator<? extends T> join(Iterator<? extends T> aIter, T aObject)
   {
      if (aObject == null)
      {
         return aIter;
      }
      return joinIter(aIter, Collections.singleton(aObject).iterator());
   }

   /**
    * Trims text lines within the given string.  Lines are substrings delimited
    * by newline characters.
    * E.g. argument " One  \n  Two  \n \nThree " yields "One\nTwo\n\nThree".
    * @param aText the string to be trimmed.
    * @return String a new trimmed string object.
    */
   public static String trimText(String aText)
   {
      StringBuffer buffer = new StringBuffer(""); //$NON-NLS-1$
      if ( ! isNullOrEmpty(aText) )
      {
         String line;
         BufferedReader buffReader = new BufferedReader(new StringReader(aText));
         try
         {
            String delim = ""; //$NON-NLS-1$
            while ( ((line = buffReader.readLine()) != null) )
            {
               buffer.append(delim);
               buffer.append(line.trim());
               delim = "\n"; //$NON-NLS-1$
            }
         }
         catch (IOException e)
         {
            // eat the exception.
         }
      }
      return buffer.toString();
   }

   /**
    * Returns a &quot;normalized&quot; text string by removing single or repeative occurances of
    * newlines (\n and \r\n) and tabs (\t) with a single white space. The leading
    * and trailing spaces are also trimmed. The returned string is a single line string even
    * if the input was multi-line.
    * @param aText
    * @return Returns the &quot;normalized&quot; text, in a single line.
    */
   public static String normalizeText(String aText)
   {
      aText = getSafeString(aText);
      aText = aText.replaceAll("\\s\\s*", " "); //$NON-NLS-1$ //$NON-NLS-2$
      return aText.trim();
   }

   /**
    * Helper method to return a numeric representation of a string or zero
    * if the given string is not a valid number format.
    * @param aValue the value to be converted.
    */
   public static int getNumeric(String aValue) throws AeException
   {
      int val;
      try
      {
         val = Integer.parseInt(aValue);
      }
      catch (Exception e)
      {
         throw new AeException(AeMessages.getString("AeUtil.ERROR_5"), e); //$NON-NLS-1$
      }

      return val;
   }

   /**
    * Helper method to return a numeric representation of a string or zero
    * if the given string is not a valid number format.
    * @param aValue the value to be converted.
    */
   public static long getBigNumeric(String aValue) throws AeException
   {
      long val;
      try
      {
         val = Long.parseLong(aValue);
      }
      catch (Exception e)
      {
         throw new AeException(AeMessages.getString("AeUtil.ERROR_5"), e); //$NON-NLS-1$
      }

      return val;
   }

   /**
    * Helper method to check for file existence
    * @param aFilename the filename to be checked
    */
   public static boolean fileExists(String aFilename)
   {
      boolean exists = false;

      try
      {
         exists = new File(aFilename).exists();
      }
      catch (Throwable th)
      {
         // Ignore exception, failure is good enough
      }

      return exists;
   }

   /**
    * Takes an input stream for a file and returns the MD5 message digest
    * which represents it. This value can be used to facilitate file comparisons.
    * Note this method will return null if there is an error obtaining the
    * MD5 code.
    * @param aInput the stream to obtain the message digest for
    */
   public static byte[] getMessageDigest(InputStream aInput)
   {
      try
      {
         MessageDigest md = MessageDigest.getInstance("MD5"); //$NON-NLS-1$

         byte[] data = new byte[512];
         for (int bytesRead, i=0; (bytesRead = aInput.read(data)) > 0; i+= bytesRead)
            md.update(data, 0, bytesRead);

         return md.digest();
      }
      catch (Exception e)
      {
         // Do nothing
      }

      return null;
   }

   /**
    * Returns an empty string if the arg value is null or empty, otherwise
    * it returns the value arg untouched.
    * @param aValue the string to check
    */
   public static String getSafeString( String aValue )
   {
      if( isNullOrEmpty( aValue ) )
      {
         return ""; //$NON-NLS-1$
      }
      return aValue;
   }

   /**
    * Gets the stacktrace from the exception as a string.
    *
    * @param t
    */
   public static String getStacktrace(Throwable t)
   {
      AeUnsynchronizedCharArrayWriter w = new AeUnsynchronizedCharArrayWriter();
      PrintWriter pw = new PrintWriter(w);
      t.printStackTrace(pw);
      return w.toString();
   }

   /**
    * Appends additional path components to a project relative path.
    */
   public static String appendProjectRelativePath(String aProjectPath, String aAppendPath)
   {
      // If the additional path is absolute, then it replaces the project path
      // entirely.
      if (aAppendPath.startsWith(PROJECT_PATH_SEPARATOR))
      {
         return PROJECT_RELATIVE_URI + aAppendPath;
      }

      String projectPath = aProjectPath;

      // Remove terminal path separators from project path.
      while (projectPath.endsWith(PROJECT_PATH_SEPARATOR))
      {
         int i = projectPath.lastIndexOf(PROJECT_PATH_SEPARATOR);
         projectPath = projectPath.substring(0, i);
      }

      StringBuffer buffer = new StringBuffer(projectPath);
      StringTokenizer tokenizer = new StringTokenizer(aAppendPath, PROJECT_PATH_SEPARATOR);

      // Iterate through each relative path component.
      while (tokenizer.hasMoreTokens())
      {
         String token = tokenizer.nextToken();

         if ("".equals(token)) //$NON-NLS-1$
         {
            // An empty path component has no effect.
         }
         else if (".".equals(token)) //$NON-NLS-1$
         {
            // A single dot path component has no effect.
         }
         else if ("..".equals(token)) //$NON-NLS-1$
         {
            // A double dot path component strips the preceding path component.
            int i = buffer.lastIndexOf(PROJECT_PATH_SEPARATOR);

            if (i < 0)
            {
               throw new RuntimeException(MessageFormat.format(AeMessages.getString("AeUtil.ERROR_0"), //$NON-NLS-1$
                                                               new Object[] {aAppendPath, aProjectPath}));
            }

            buffer.setLength(i);
         }
         else
         {
            // A normal path component extends the path.
            buffer.append(PROJECT_PATH_SEPARATOR);
            buffer.append(token);
         }
      }

      return buffer.toString();
   }

   /**
    * Returns the project relative base directory for the specified project
    * relative path.
    */
   public static String getProjectRelativeBaseDir(String aProjectPath)
   {
      int i = aProjectPath.lastIndexOf(PROJECT_PATH_SEPARATOR);
      String base = (i >= 0) ? aProjectPath.substring(0, i) : PROJECT_RELATIVE_URI;

      // Remove terminal path separators from base directory path.
      while (base.endsWith(PROJECT_PATH_SEPARATOR))
      {
         i = base.lastIndexOf(PROJECT_PATH_SEPARATOR);
         base = base.substring(0, i);
      }

      return base;
   }

   /**
    * Returns <code>true</code> if and only if the specified path is a project
    * relative path.
    */
   public static boolean isProjectRelativePath(String aPath)
   {
      return !AeUtil.isNullOrEmpty(aPath) && aPath.startsWith(PROJECT_RELATIVE_URI);
   }

   /**
    * Returns absolute location for specified WSDL or XSD import relative to
    * the specified parent document location (i.e., the location of the
    * document doing the importing).
    */
   public static String resolveImport(String aParentLocation, String aImportLocation)
   {
      // If the import location is a project relative path, then use it as is.
      if (isProjectRelativePath(aImportLocation))
      {
         try
         {
            return AeUTF8Util.urlDecode(aImportLocation);
         }
         catch (UnsupportedEncodingException e)
         {
            return aImportLocation;
         }
      }

      try
      {
         // If the import location is an absolute URL, then use it as such.
         new URL(aImportLocation);
         return aImportLocation;
      }
      catch (MalformedURLException e)
      {
         // Ignore this, because the import location may be relative or may be
         // using a URI scheme other than file: and http:.
      }

      try
      {
         // If the import location is an absolute URI, then use it as such.
         if (new URI(aImportLocation).isAbsolute())
         {
            return aImportLocation;
         }
      }
      catch (URISyntaxException e)
      {
         // Ignore this, because the import location may be relative.
      }

      // If the import is a file, and it exists, return the URL for the file.
      File file = new File(aImportLocation);
      if (file.isFile())
      {
         try
         {
            return file.toURI().toURL().toExternalForm();
         }
         catch (Exception e)
         {
         }
      }

      // If the parent location is a project relative path, then append the
      // import location to the base of the parent location.
      if (AeUtil.isProjectRelativePath(aParentLocation))
      {
         try
         {
            String base = AeUtil.getProjectRelativeBaseDir(aParentLocation);
            return AeUTF8Util.urlDecode(AeUtil.appendProjectRelativePath(base, aImportLocation));
         }
         catch (UnsupportedEncodingException e)
         {
            return aImportLocation;
         }
      }

      try
      {
         // Resolve the import location relative to the parent location.
         return new URL(new URL(aParentLocation), aImportLocation).toExternalForm();
      }
      catch (MalformedURLException e)
      {
         // Ignore this, because the location may be using a URI scheme other
         // than file: and http:.
      }

      try
      {
         // Resolve the import location relative to the parent location.
         return new URI(aParentLocation).resolve(aImportLocation).toString();
      }
      catch (URISyntaxException e)
      {
         AeException.logError(e, AeMessages.getString("AeUtil.ERROR_13") + aParentLocation); //$NON-NLS-1$
      }

      return aImportLocation;
   }

   /**
    * Given two dates, this method will return the earliest.
    *
    * @param aDate1
    * @param aDate2
    */
   public static Date getMinDate(Date aDate1, Date aDate2)
   {
      if (aDate1 != null && aDate2 != null)
      {
         if (aDate1.before(aDate2))
         {
            return aDate1;
         }
         else
         {
            return aDate2;
         }
      }
      // If date 1 is null, return date 2 (even if date 2 is null - who cares?)
      if (aDate1 == null)
         return aDate2;
      else
         return aDate1;
   }

   /**
    * Utility method used to convert an object into a byte array.
    * May return null if an exception occurs.
    * @param aObject the object to be serialized
    */
   public static byte[] serializeObject(Serializable aObject)
   {
      try
      {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos);
         oos.writeObject(aObject);
         oos.close();

         return baos.toByteArray();
      }
      catch(IOException e)
      {
         AeException.logError(e, AeMessages.getString("AeUtil.ERROR_14")); //$NON-NLS-1$
         return null;
      }
   }

   /**
    * Utility method used to convert a byte array result into an object.
    * May return null if an exception occurs.
    * @param aData the data to be converted
    */
   public static Object deserializeObject(byte [] aData)
   {
      try
      {
         ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(aData));
         Object object = ois.readObject();
         ois.close();

         return object;
      }
      catch(Exception e)
      {
         AeException.logError(e, AeMessages.getString("AeUtil.ERROR_15")); //$NON-NLS-1$
         return null;
      }
   }

   /**
    * Locate resources on the classpath.  If a thread context
    * classloader is set it will try to load the resource
    * through it.  If it can't and a non-null class is passed
    * it will attempt to use its classloader to load the resource.
    * @param aResourceName
    * @param aClass
    * @return The resource <code>URL</code> or null if the resource could
    * not be located.
    */
   public static URL findOnClasspath( String aResourceName, Class<?> aClass )
   {
      URL resourceURL = null;

      // try the context classloader if available
      if( Thread.currentThread().getContextClassLoader() != null)
      {
         resourceURL = Thread.currentThread().getContextClassLoader().getResource( aResourceName );
      }

      // if no luck with context classloader then use passed class if available
      if( resourceURL == null && aClass != null )
      {
         if (aClass.getClassLoader() != null)
            resourceURL = aClass.getClassLoader().getResource( aResourceName );
      }

      return resourceURL;
   }

   /**
    * Utility method to perform substitution of environment variables within a given string.
    * The substitution variable(s) should be in the format of ${VAR_TO_BE_REPLACED}. There may
    * be multiple substitution variables in an input pattern, but they may not be nested within
    * each other. The caller may optionally supply the Properties to be used for substitution
    * in addition to the System Properties. User properties take precedence over System properties.
    * The user may "escape" a "$" character by preceding it with another "$" character.
    *
    * @param aPattern the string containing environment variables to be replaced with system property values
    * @param aProperties an optional set of properties to be used in prior to using System properties during
    *         substitution, if null System properties will be used
    * @return a new string with substitution variables replaced with their proper environment variables, if
    * a replacement cannot be found, the substitution variable will be left intact.
    */
   public static String replaceAntStyleParams(String aPattern, Map aProperties)
   {
      if (aPattern == null)
         return null;

      boolean getVariable = false;
      StringBuffer buff = new StringBuffer();
      StringTokenizer tok = new StringTokenizer(aPattern, "${}", true); //$NON-NLS-1$
      while (tok.hasMoreTokens())
      {
         String token = tok.nextToken();
         if ("$".equals(token)) //$NON-NLS-1$
         {
            if (tok.hasMoreTokens())
            {
               // Verify next token is "{", if not consider "$" as an escape char and append next token
               token = tok.nextToken();
               if ("{".equals(token)) //$NON-NLS-1$
                  getVariable = true;
               else
                  buff.append(token);
            }
         }
         else if ("}".equals(token) && getVariable) //$NON-NLS-1$
         {
            getVariable = false;
         }
         else
         {
            if (getVariable)
            {
               // First check the user properties if we were given any
               String var = null;
               if (aProperties != null)
                  var = (String) aProperties.get(token);

               // If property not found, check the System properties
               if (var == null)
                  var = System.getProperty(token);

               // If substitution variable not found, use the substitution variable itself
               if (var == null)
                  token = "${" + token + "}"; //$NON-NLS-1$ //$NON-NLS-2$
               else
                  token = var;
            }

            buff.append(token);
         }
      }

      return buff.toString();
   }
   
   /**
    * Traverse the element and replaces all of the bind variables in attributes
    * and element text for this element and all of its descendants. 
    * @param aElement
    * @param aProperties
    */
   public static void replaceXQueryStyleParamsInElement(Element aElement, Map aProperties)
   {
      if (AeUtil.isNullOrEmpty(aProperties) || aElement == null)
      {
         return;
      }
      
      aElement.normalize();
      
      replaceXQueryStyleParamsInElementAttrsAndText(aElement, aProperties);
      NodeList nl = aElement.getElementsByTagName("*"); //$NON-NLS-1$
      for(int i=0; nl.item(i) != null; i++)
      {
         replaceXQueryStyleParamsInElementAttrsAndText((Element) nl.item(i), aProperties);
      }
   }
   
   /**
    * Replaces all of the bind variables in attributes and element text.
    * @param aElement
    * @param aProperties
    */
   private static void replaceXQueryStyleParamsInElementAttrsAndText(Element aElement, Map aProperties)
   {
      // walk all attributes
      NamedNodeMap attrs = aElement.getAttributes();
      if (attrs != null)
      {
         int length = attrs.getLength();
         for(int i=0; i<length; i++)
         {
            Node node = attrs.item(i);
            // skip over xmlns decls
            if (!XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(node.getNamespaceURI()))
            {
               String nodeValue = node.getNodeValue();
               String replaced = AeUtil.replaceXQueryStyleParamsInString(nodeValue, aProperties);
               node.setNodeValue(replaced);
            }
         }
      }
      
      // walk all child nodes (child nodes only, elements are handled above)
      NodeList children = aElement.getChildNodes();
      for(int i=0; children.item(i) != null; i++)
      {
         Node node = children.item(i);
         if (node.getNodeType() == Node.TEXT_NODE)
         {
            String nodeValue = node.getNodeValue();
            String replaced = AeUtil.replaceXQueryStyleParamsInString(nodeValue, aProperties);
            node.setNodeValue(replaced);
         }
      }
   }

   /**
    * Returns the String with variable references of pattern {$var} replaced with values 
    * @param aPattern
    * @param aProperties
    */
   public static String replaceXQueryStyleParamsInString(String aPattern, Map aProperties)
   {
      if (isNullOrEmpty(aPattern))
      {
         return ""; //$NON-NLS-1$
      }
      StringBuffer sb = new StringBuffer();
      String[] strs = aPattern.split("\\{"); //$NON-NLS-1$
      boolean getvar = true;
      boolean prefix = false;
      for (int i = 0; i < strs.length; i++)
      {
         if ( strs[i].equals("") && ( strs[i + 1].equals("") ) ) //$NON-NLS-1$ //$NON-NLS-2$
         {
            sb.append("{"); //$NON-NLS-1$
            prefix=true;
            getvar = false;
            i++;
         }
         else if ( strs[i].startsWith("$") ) //$NON-NLS-1$
         {
            if ( !getvar )
            {
               if (!prefix)
                  sb.append("{");  //$NON-NLS-1$
               sb.append(strs[i].replaceAll("}}", "}")); //$NON-NLS-1$ //$NON-NLS-2$
            }
            else
            {
               if ( strs[i].indexOf("}}") == -1 ) //$NON-NLS-1$
               {
                  if ( ( strs[i].indexOf("}") != -1 ) && i > 0)//$NON-NLS-1$
                  {
                     String[] childstrs = strs[i].substring(1).split("}"); //$NON-NLS-1$
                     if (aProperties.get(childstrs[0]) != null)
                        sb.append(aProperties.get(childstrs[0]));
                     else
                        sb.append("{$"+childstrs[0]+"}"); //$NON-NLS-1$ //$NON-NLS-2$
                     for (int j = 1; j < childstrs.length; j++)
                        sb.append(childstrs[j]);
                  }
                  else
                  {
                     if (!prefix && i>0)
                        sb.append("{");  //$NON-NLS-1$
                     sb.append(strs[i]);
                  }
               }
               else
               {
                  if (!prefix && (strs.length > 1))
                     sb.append("{");  //$NON-NLS-1$
                  sb.append(strs[i].replaceAll("}}", "}")); //$NON-NLS-1$//$NON-NLS-2$
               }
            }
            prefix=false;
            getvar = true;
         }
         else
         {
            sb.append(strs[i]);
         }
      }
      return sb.toString();
   }

   /**
    * if the double can safely be converted to a Long then do it.
    * @param aVal
    */
   public static Object doubleToLong(Double aVal)
   {
      if (Double.compare(aVal.doubleValue(), Math.ceil(aVal.doubleValue()) ) == 0 )
      {
         return new Long(aVal.longValue());
      }
      else
         return aVal;
   }
   
   /**
    * This method creates a temporary file. In addition, it will throw nicely formatted AeExceptions if the
    * temp directory does not exist or is not writable.
    * @param aPrefix
    * @param aSuffix
    * @throws AeException
    */
   public static File getTempFile(String aPrefix, String aSuffix) throws AeException
   {
      File tempDir = getTempDirectory();

      try
      {
         File file = File.createTempFile(aPrefix, aSuffix, tempDir);
         if (!file.canWrite())
         {
            throw new AeException(MessageFormat.format(AeMessages.getString("AeUtil.ERROR_1"), //$NON-NLS-1$
                                                       new Object[] {tempDir}));
         }

         return file;
      }
      catch (IOException ioe)
      {
         throw new AeException(MessageFormat.format(AeMessages.getString("AeUtil.ERROR_1"), //$NON-NLS-1$
                                                    new Object[] {tempDir}));
      }
   }

   /**
    * Returns a <code>File</code> representing the default temporary directory
    * location.
    *
    * @throws AeException
    */
   protected static File getTempDirectory() throws AeException
   {
      // Check the validity of the temp location.  Only do this to show a nice error
      // in the case that the temp location does not exist.
      String path = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
      File tempDir = new File(path);
      if (!tempDir.isDirectory())
      {
         throw new AeException(MessageFormat.format(AeMessages.getString("AeUtil.ERROR_1"), //$NON-NLS-1$
                                                    new Object[] {tempDir}));
      }
      return tempDir;
   }

   /**
    * Stores the contents of the given input stream into a temporary file and
    * returns a <code>File</code> object for the temporary file.
    *
    * @throws AeException
    */
   public static File createTempFile(InputStream aInputStream, String aFilePrefix, String aFileSuffix) throws AeException
   {
      File file = getTempFile(aFilePrefix, aFileSuffix);
      OutputStream output;

      try
      {
         output = new FileOutputStream(file);
      }
      catch (FileNotFoundException e)
      {
         String errMsg = AeMessages.format("AeUtil.ERROR_FileNotFound", file.getAbsolutePath()); //$NON-NLS-1$
         throw new AeException(errMsg, e);
      }

      try
      {
         AeFileUtil.copy(aInputStream, output);
      }
      catch (IOException e)
      {
         String errMsg = AeMessages.format("AeUtil.ERROR_TempFileIO", file.getAbsolutePath()); //$NON-NLS-1$
         throw new AeException(errMsg, e);
      }
      finally
      {
         AeCloser.close(aInputStream);
         AeCloser.close(output);
      }

      return file;
   }

   /**
    * Returns an array of files in the default temporary directory with the
    * given prefix and suffix.
    *
    * @param aPrefix
    * @param aSuffix
    * @throws AeException
    */
   public static File[] listTempFiles(final String aPrefix, final String aSuffix) throws AeException
   {
      // TODO (KR) Warn about prefixes that are too long.
      return getTempDirectory().listFiles(new FileFilter()
      {
         public boolean accept(File aFile)
         {
            return aFile.isFile() && aFile.getName().startsWith(aPrefix) && aFile.getName().endsWith(aSuffix);
         }
      });
   }

   /**
    * Converts the <code>InputStream</code> to a byte array
    *
    * @param aInput
    * @throws AeException
    */
   public static byte[] toByteArray(InputStream aInput) throws AeException
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();

      try
      {
         byte[] buffer = new byte[4096];
         int nBytes = 0;

         while ((nBytes = aInput.read(buffer)) >= 0)
         {
            if (nBytes > 0)
            {
               out.write(buffer, 0, nBytes);
            }
         }

         return out.toByteArray();
      }
      catch (IOException e)
      {
         throw new AeException(AeMessages.getString("AeUtil.ERROR_29"), e); //$NON-NLS-1$
      }
      finally
      {
         AeCloser.close(aInput);
         AeCloser.close(out);
      }
   }

   /**
    * Returns a string representation of the contents of the given Reader.
    * @param aReader
    * @return String
    * @throws AeException
    */
   public static String toString(Reader aReader) throws AeException
   {
      String result = null;
      try
      {
         BufferedReader br = new BufferedReader(aReader);
         String str;
         StringBuffer contents = new StringBuffer();
         synchronized (contents)
         {
            while ((str = br.readLine()) != null)
            {
               contents.append(str);
               contents.append("\n"); //$NON-NLS-1$
            }
         }
         result = contents.toString();
      }
      catch (IOException iox)
      {
         AeException aex = new AeException();
         aex.setRootCause(iox);
         throw aex;
      }
      finally
      {
         AeCloser.close(aReader);
      }
      return result;
   }

   /**
    * Gets the file name given a (potentially) full path.  This method will work no matter
    * what the path separator is.
    *
    * @param aFilePath
    */
   public static String getFilename(String aFilePath)
   {
      if (aFilePath != null)
      {
         String [] split = aFilePath.split("[/\\\\]"); //$NON-NLS-1$
         if (split != null && split.length > 0)
         {
            return split[split.length - 1];
         }
      }
      return aFilePath;
   }
   
   /**
    * This is essentially the same as getFilename, except that it only 
    * gets the file name without the file extension.
    * 
    * @param aFilePath
    */
   public static String getFileNameSansExtension(String aFilePath)
   {
      String fileName = getFilename(aFilePath);
      int indexOf = fileName.lastIndexOf('.');
      if (indexOf > 0)
      {
         fileName = fileName.substring(0, indexOf);
      }
      return fileName;
   }
   
   /**
    * Simple string formatting for qname.
    * @param aQName
    */
   public static String qNameToString(QName aQName)
   {
      StringBuffer sb = new StringBuffer();
      if( AeUtil.notNullOrEmpty(aQName.getNamespaceURI()) )
      {
         sb.append( aQName.getNamespaceURI() );
         sb.append(':');
      }
      sb.append( aQName.getLocalPart() );
      return sb.toString();
   }

   /**
    * Takes a String and removes \r\n formatting.
    *
    * @param aString The string to flatten.
    * @param aReplaceString string to use as a replacement \r\n
    * @return Flattened version of the string.
    */
   public static String flattenString(String aString, String aReplaceString)
   {
      StringBuffer buff = new StringBuffer();
      StringTokenizer tokenizer = new StringTokenizer(aString, "\r\n"); //$NON-NLS-1$
      while (tokenizer.hasMoreTokens())
      {
         String token = tokenizer.nextToken();
         if (!AeUtil.isNullOrEmpty(token))
         {
            buff.append(token.trim()).append(aReplaceString);
         }
      }
      return buff.toString();
   }

   /**
    * Escapes special characters in the specified string. If
    * <code>aIsAttribute</code> is <code>true</code>, then this will perform
    * processing appropriate for attribute values.
    */
   public static String escapeXMLEntities(String aText, boolean aIsAttribute)
   {
      StringBuffer buffer = null;

      // Need to escape quotes for attributes but not for normal text.
      String entityQuote = aIsAttribute ? ENTITY_QUOTE : null;

      char[] chars = aText.toCharArray();
      int length = chars.length;

      for (int i = 0; i < length; ++i)
      {
         char c = chars[i];
         String entity;

         switch (c)
         {
            default:
               entity = null;
               break;
            case '"':
               entity = entityQuote;
               break;
            case '&':
               entity = ENTITY_AMPERSAND;
               break;
            case '\'':
               // Don't need to escape apostrophes, because we surround
               // attribute values with quotes.
               entity = null;
               break;
            case '<':
               entity = ENTITY_LESSTHAN;
               break;
            case '>':
               entity = ENTITY_GREATERTHAN;
               break;
         }

         if (entity != null)
         {
            // If we haven't allocated a buffer yet, do so now in order to
            // handle the entity we just discovered.
            if (buffer == null)
            {
               // Construct a buffer with enough room for the original text and
               // this entity plus extra room for a few more entities.
               buffer = new StringBuffer(length + entity.length() + ENTITY_QUOTE.length() * 3);

               // Append the characters we skipped so far hoping not to find an
               // entity.
               buffer.append(chars, 0, i);
            }

            // Append the entity.
            buffer.append(entity);
         }
         else if (buffer != null)
         {
            // If we found an entity previously, then append this character to
            // the buffer we've been keeping ever since.
            buffer.append(c);
         }
      }

      // If we never allocated a buffer, then return the original string.
      return (buffer == null) ? aText : buffer.toString();
   }

   /**
    * Returns true if the given sting is found in the given comma separated list.
    * @param aString string to look up.
    * @param aCsvList comma separated value list of string.
    * @param aCaseSensitive if true, do a case sensitive comparison.
    * @return true if string is found in list.
    */
   public static boolean isStringInCsvList(String aString, String aCsvList, boolean aCaseSensitive)
   {
      boolean rVal = false;
      if ( notNullOrEmpty(aString) && notNullOrEmpty(aCsvList) )
      {
         aString = aString.trim();
         StringTokenizer st = new StringTokenizer(aCsvList.trim(), ","); //$NON-NLS-1$
         String token = null;
         while (st.hasMoreTokens())
         {
            token = st.nextToken().trim();
            if ( (aCaseSensitive && token.equals(aString))
                  || (!aCaseSensitive && token.equalsIgnoreCase(aString)) )
            {
               rVal = true;
               break;
            }
         }
      }
      return rVal;
   }

   /**
    * Return the short name of the passed url. Essentially by stripping off any
    * preceeding path information from the passed location.
    * TODO (cck) is this the same as getFileName, also we should handle urn format
    * @param aLocation the location to get a short name for.
    */
   public static String getShortNameForLocation(String aLocation)
   {
       String location = aLocation.replace('\\', '/');
       int lastSlash = location.lastIndexOf('/');
       if( lastSlash >= 0 && (lastSlash+1) < location.length())
           return location.substring( lastSlash + 1 );
       else
           return location;
   }

   /**
    * Generates a new UUID based on a Hex encoded SHA digest of:
    * <ol>
    * <li>unique id of this VM</li>
    * <li>time in millis</li>
    * <li>Cryptographically strong random number</li>
    * </ol>
    * @return Formatted UUID URI
    */
   public static synchronized String getNewUUID()
   {
      // Create a message id with VM id, current time,
      // and a random number from our secure random generator
      String separator = "-"; //$NON-NLS-1$
      StringBuffer input = new StringBuffer();
      input.append(sVmid.toString());
      input.append(separator);
      input.append(System.currentTimeMillis());
      input.append(separator);
      input.append(sRandom.nextLong());

      // get the SHA digest of the id input
      byte[] sha = sShaDigest.digest(input.toString().getBytes());

      // get a hex string of the hash
      StringBuffer buffer = new StringBuffer();
      for (int j = 0; j < sha.length; ++j)
      {
         int b = sha[j] & 0xFF;
         if (b < 0x10) buffer.append('0');
         buffer.append(Integer.toHexString(b));
      }
      String hexString = buffer.toString();

      // Format as a uuid URI
      StringBuffer uuid = new StringBuffer();
      uuid.append("uuid:"); //$NON-NLS-1$
      uuid.append(hexString.substring(0,8));
      uuid.append(separator);
      uuid.append(hexString.substring(8,12));
      uuid.append(separator);
      uuid.append(hexString.substring(12,20));
      uuid.append(separator);
      uuid.append(hexString.substring(20));

      return uuid.toString();
   }

   /**
    * Parses the given string into an integer. The default value is returned
    * if the value cannot be parsed into an integer.
    * @param aIntString
    * @param aDefault
    */
   public static int parseInt(String aIntString, int aDefault)
   {
      int rval;
      try
      {
         rval = Integer.parseInt(aIntString.trim());
      }
      catch(Exception e)
      {
         rval = aDefault;
      }
      return rval;
   }

   /**
    * Parses the given string into an long. The default value is returned
    * if the value cannot be parsed into an long.
    * @param aLongString
    * @param aDefault
    */
   public static long parseLong(String aLongString, long aDefault)
   {
      long rval;
      try
      {
         rval = Long.parseLong(aLongString.trim());
      }
      catch(Exception e)
      {
         rval = aDefault;
      }
      return rval;
   }

   /**
    * Returns true if the given string is a number.
    * @param aNumberString
    * @return true if the string is a number.
    */
   public static boolean isNumber(String aNumberString)
   {
      boolean rval = false;
      if ( notNullOrEmpty(aNumberString) )
      {
         Matcher matcher = sNumberRegEx.matcher( aNumberString.trim() );
         rval = matcher.matches();
      }
      return rval;
   }

   /**
    * Converts a string array to a set of strings.
    *
    * @param aStringArray
    */
   public static Set<String> toSet(String [] aStringArray)
   {
      Set<String> set = new LinkedHashSet<String>();
      if (aStringArray != null)
      {
         for (int i = 0; i < aStringArray.length; i++)
         {
            set.add(aStringArray[i]);
         }
      }
      return set;
   }

   /**
    * Returns a token (e.g. comma) separated list of string values in the collection.
    * @param aCollection
    * @param aSeparatorToken
    * @return token separated list of string values.
    */
   public static String toString(Collection aCollection, char aSeparatorToken)
   {
      StringBuffer sb = new StringBuffer();
      if (aCollection != null && aCollection.size() > 0)
      {
         synchronized(sb)
         {
            Iterator it = aCollection.iterator();
            while (it.hasNext())
            {
               sb.append(String.valueOf( it.next()) + aSeparatorToken);
            }
            // remove trailing separator token.
            sb.setLength( sb.length() - 1);
         }
      }
      return sb.toString();
   }

   /**
    * Capitalizes the given word (changes the first letter in the
    * word from lower case to upper case).
    *
    * @param aWord
    */
   public static String capitalizeWord(String aWord)
   {
      return (AeUtil.notNullOrEmpty(aWord)) ? Character.toUpperCase(aWord.charAt(0)) + aWord.substring(1) : aWord;
   }

   /**
    * Joins the collection of strings into a single string using the given
    * separator/delimiter.
    *
    * @param aStrings
    * @param aSeparator
    */
   public static String joinToStringObjects(Collection aStrings, String aSeparator)
   {
      StringBuffer buff = new StringBuffer();
      if (aStrings.size() > 0)
      {
         synchronized (buff)
         {
            Iterator iter = aStrings.iterator();
            while (iter.hasNext())
            {
               Object toStrObj = iter.next();
               buff.append(toStrObj.toString());
               if (iter.hasNext())
               {
                  buff.append(aSeparator);
               }
            }
         }
      }
      return buff.toString();
   }

   /**
    * Determines if the given String represents a value URL.
    *
    * @param aLocation
    * @return true if location is a valid URL, otherwise false.
    */
   public static boolean isUrlLocation(String aLocation)
   {
      try
      {
         new URL(aLocation);
         return true;
      }
      catch (MalformedURLException ex)
      {
         // ignore exception
      }
      return false;
   }
   
   /**
    * Determines if the given URI is available.
    * @param aUri
    */
   public static boolean isUriAvailable(URI aUri)
   {
      InputStream stream = null;
      boolean empty = true;
      
      try 
      {
         stream = aUri.toURL().openStream();
         empty = stream.available() <= 0;
      }
      catch(IOException ex)
      {
         // ignore exception
      }
      finally
      {
         AeCloser.close(stream);
      }
      return !empty;
   }

   /**
    * Generates a unique name for the child.
    * 
    * @param aName
    * @param aStringCollection
    */
   public static String generateUniqueName(String aName, Collection<String> aStringCollection)
   {
      return generateUniqueName(aName, aStringCollection, false);
   }

   /**
    * Generates a unique name for the child.
    * 
    * @param aPrefix
    * @param aStringCollection
    * @param aPreserveIfAlreadyUnique
    */
   public static String generateUniqueName(String aPrefix, Collection<String> aStringCollection, boolean aPreserveIfAlreadyUnique)
   {
      Set<String> set = new HashSet<String>();
      set.addAll(aStringCollection);

      if (aPreserveIfAlreadyUnique && !set.contains(aPrefix))
         return aPrefix;

      int counter = 1;
      while(set.contains(aPrefix + String.valueOf(counter)))
         counter++;

      return aPrefix + counter;
   }
   
   /**
    * Currently, this simply trims a URN.
    */
   public static String normalizeURN(String aURN)
   {
      if(AeUtil.isNullOrEmpty(aURN))
      {
         return ""; //$NON-NLS-1$
      }
      else
      {
         return aURN.trim();
      }
   }
}

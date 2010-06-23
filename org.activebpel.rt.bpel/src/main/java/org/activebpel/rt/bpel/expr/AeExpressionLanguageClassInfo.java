// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.expr;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.util.AeUtil;

/**
 * This class contains information about a class from the engine config.  The class info can be
 * either a String or a Map.  If it is a String, then it represents the fully qualified class 
 * name of the class to instantiate.  If it is a Map, then the map must contain a string entry
 * called "Class" with the fully qualified classname.  In addition, the map may contain a map
 * entry called "Params".  This map will be passed to the class being instantiated during
 * construction, assuming the class has a qualified constructor.
 * 
 * From above, the first form of the XML in the engine config would be:
 * 
 * <pre>
 *   <entry name="Uri" value="urn:active-endpoints:expression-language:my-language" />
 * </pre>
 * 
 * The second form of the XML (with the Params map) would be:
 * 
 * <pre>
 *   <entry name="Uri">
 *      <entry name="Class" value="urn:active-endpoints:expression-language:my-language" />
 *      <entry name="Params">
 *         <entry name="Param1" value="some-value" />
 *         <entry name="Param2" value="some-value" />
 *         <entry name="Param3" value="some-value" />
 *      </entry>
 *   </entry>
 * </pre>
 */
public class AeExpressionLanguageClassInfo
{
   /** The classloader to use. */
   private ClassLoader mClassloader;
   /** The classpath to use to find the class. */
   private String mClasspath;
   /** The constructor for this class. */
   private Constructor mConstructor;
   /** Params to send during construction. */
   private Map mParams;

   /**
    * Constructs the class info given either a String or Map.
    * 
    * @param aInfo
    * @param aClasspath
    * @throws AeException
    */
   public AeExpressionLanguageClassInfo(Object aInfo, String aClasspath, ClassLoader aClassloader) throws AeException
   {
      setClasspath(aClasspath);
      setClassloader(aClassloader);

      if (aInfo instanceof Map)
      {
         initFromMap((Map) aInfo);
      }
      else if (aInfo instanceof String)
      {
         initFromString((String) aInfo);
      }
      else
      {
         throw new AeException(AeMessages.getString("AeExpressionLanguageFactory.INVALID_CLASSINFO_ERROR")); //$NON-NLS-1$
      }
   }

   /**
    * Constructs a class info from a fully qualified classname.
    * 
    * @param aClassname
    * @throws AeException
    */
   public void initFromString(String aClassname) throws AeException
   {
      try
      {
         Class c = null;
         if (AeUtil.isNullOrEmpty(getClasspath()))
         {
            c = getClassForName(aClassname);
         }
         else
         {
            ClassLoader loader = createClassLoader();
            c = loader.loadClass(aClassname);
         }
         setConstructor(c.getConstructor(new Class[] {}));
         setParams(null);
      }
      catch (Throwable t)
      {
         throw new AeException(t);
      }
   }
   
   /**
    * Constructs a class info from a Map.
    * 
    * @param aInfo
    * @throws AeException
    */
   public void initFromMap(Map aInfo) throws AeException
   {
      try
      {
         String classname = (String) aInfo.get("Class"); //$NON-NLS-1$
         if (AeUtil.isNullOrEmpty(classname))
         {
            throw new AeException(AeMessages.getString("AeExpressionLanguageFactory.CLASSNAME_NOT_SPECIFIED_ERROR")); //$NON-NLS-1$
         }
         Map params = (Map) aInfo.get("Params"); //$NON-NLS-1$

         Class c = null;
         if (AeUtil.isNullOrEmpty(getClasspath()))
         {
            c = getClassForName(classname);
         }
         else
         {
            ClassLoader loader = createClassLoader();
            c = loader.loadClass(classname);
         }
         if (AeUtil.isNullOrEmpty(params))
         {
            setConstructor(c.getConstructor(new Class[] {}));
            setParams(null);
         }
         else
         {
            setConstructor(c.getConstructor(new Class[] { Map.class }));
            setParams(params);
         }
      }
      catch (Throwable t)
      {
         throw new AeException(t);
      }
   }

   /**
    * Gets a class object given the full classname.
    * 
    * @param aClassname
    */
   protected Class getClassForName(String aClassname) throws ClassNotFoundException
   {
      if (getClassloader() != null)
      {
         return getClassloader().loadClass(aClassname);
      }
      else
      {
         return Class.forName(aClassname);
      }
   }
   
   /**
    * Creates a class loader from the configured classpath.
    */
   private ClassLoader createClassLoader() throws AeException
   {
      URL [] urls = null;
      try
      {
         StringTokenizer tokenizer = new StringTokenizer(getClasspath(), ";"); //$NON-NLS-1$
         List urlList = new LinkedList();
         while (tokenizer.hasMoreTokens())
         {
            String pathItem = tokenizer.nextToken();
            if (pathItem.startsWith("http")) //$NON-NLS-1$
            {
               URL url = new URL(pathItem);
               urlList.add(url);
            }
            else
            {
               File file = new File(pathItem);
               if (file.exists())
               {
                  URL url = file.toURI().toURL();
                  urlList.add(url);
               }
            }
         }
         urls = (URL []) urlList.toArray(new URL[urlList.size()]);
      }
      catch (Throwable t)
      {
         throw new AeException(t);
      }
      if (urls == null)
      {
         String msg = MessageFormat.format(AeMessages.getString("AeExpressionLanguageFactory.CLASSPATH_NOT_FOUND_ERROR"), //$NON-NLS-1$
               new Object [] { getClasspath() });
         throw new AeException(msg);
      }
      else
      {
         return new URLClassLoader(urls, getClass().getClassLoader());
      }
   }
   
   /**
    * Creates a new instance of the class.
    */
   public Object newInstance() throws AeException
   {
      try
      {
         if (getParams() == null)
         {
            return getConstructor().newInstance(new Object[] {});
         }
         else
         {
            return getConstructor().newInstance(new Object[] { getParams() });
         }
      }
      catch (Throwable t)
      {
         throw new AeException(t);
      }
   }

   /**
    * @return Returns the constructor.
    */
   protected Constructor getConstructor()
   {
      return mConstructor;
   }
   
   /**
    * @param aConstructor The constructor to set.
    */
   protected void setConstructor(Constructor aConstructor)
   {
      mConstructor = aConstructor;
   }
   
   /**
    * @return Returns the params.
    */
   protected Map getParams()
   {
      return mParams;
   }
   
   /**
    * @param aParams The params to set.
    */
   protected void setParams(Map aParams)
   {
      mParams = aParams;
   }

   /**
    * @return Returns the classpath.
    */
   protected String getClasspath()
   {
      return mClasspath;
   }

   /**
    * @param aClasspath The classpath to set.
    */
   protected void setClasspath(String aClasspath)
   {
      mClasspath = aClasspath;
   }

   /**
    * @return Returns the classloader.
    */
   protected ClassLoader getClassloader()
   {
      return mClassloader;
   }

   /**
    * @param aClassloader The classloader to set.
    */
   protected void setClassloader(ClassLoader aClassloader)
   {
      mClassloader = aClassloader;
   }
}
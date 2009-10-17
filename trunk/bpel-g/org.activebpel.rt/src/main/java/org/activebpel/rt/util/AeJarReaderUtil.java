// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeJarReaderUtil.java,v 1.5 2007/06/29 14:30:18 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Utilty class for reading jar files.
 */
public class AeJarReaderUtil
{
   /** jar file being read */
   private JarFile mJarFile;

   /**
    * Constructor.
    * @param aUrl points to a jar resource.
    * @throws IOException
    */
   public AeJarReaderUtil( URL aUrl ) throws IOException
   {
      mJarFile = new JarFile( aUrl.getFile() );
   }

   /**
    * C'tor.
    *
    * @param aFile
    * @throws IOException
    */
   public AeJarReaderUtil(File aFile) throws IOException
   {
      mJarFile = new JarFile( aFile );
   }

   /**
    * Return the JarEntry with the given name.
    * @param aName
    * @return named entry or null if none is found
    */
   public JarEntry getEntry( String aName )
   {
      for( Enumeration e = mJarFile.entries(); e.hasMoreElements(); )
      {
         JarEntry jarEntry = (JarEntry)e.nextElement();

         if( aName.equals(jarEntry.getName())  )
         {
            return jarEntry;
         }
      }
      return null;
   }

   /**
    * Collection of JarEntry names acceptable by the filter.
    * @param aFilter select entries based on filename
    * @return matching jar entry file names
    */
   public Collection getEntryNames( FilenameFilter aFilter )
   {
      List matches = new ArrayList();
      for( Enumeration e = mJarFile.entries(); e.hasMoreElements(); )
      {
         JarEntry jarEntry = (JarEntry)e.nextElement();

         if( aFilter.accept(null, jarEntry.getName() )  )
         {
            matches.add(jarEntry.getName());
         }
      }
      return matches;
   }

   /**
    * Return collection of JarEntry objects acceptable by the filter.
    * @param aFilter select entries based on filename
    * @return match jar etnry objects
    */
   public Collection getEntries( FilenameFilter aFilter )
   {
      List matches = new ArrayList();
      for( Enumeration e = mJarFile.entries(); e.hasMoreElements(); )
      {
         JarEntry jarEntry = (JarEntry)e.nextElement();

         if( aFilter.accept(null, jarEntry.getName() )  )
         {
            matches.add(jarEntry);
         }
      }
      return matches;
   }

   /**
    * Access input stream to jar entry
    * @param aEntry jar entry to read
    * @return input stream to jar entry
    * @throws IOException
    */
   public InputStream getInputStream( JarEntry aEntry ) throws IOException
   {
      return mJarFile.getInputStream( aEntry );
   }

   /**
    * Gets an input stream for a particular name.
    *
    * @param aName
    * @throws IOException
    */
   public InputStream getInputStream( String aName ) throws IOException
   {
      return getInputStream(getEntry(aName));
   }

   /**
    * Release any open resources.
    */
   public void close()
   {
      AeCloser.close(mJarFile);
   }
}

// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeFileUtil.java,v 1.13 2008/02/21 18:09:50 ckeller Exp $
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.activebpel.rt.AeMessages;

/**
 * Common file utility methods.
 */
public class AeFileUtil
{
   /** Default buffer size. */
   public static final int DEFAULT_BUFFER = 1024 * 4;
   public static String PARENT_DIRECTORY_INDICATOR = ".."; //$NON-NLS-1$
   public static String CURRENT_DIRECTORY_INDICATOR = "."; //$NON-NLS-1$

   /** prevent instantiation */
   private AeFileUtil() {}

   /**
    * Copy the source file to the destination file.  Neither file can
    * be a directory.
    * @param aSource The source file.
    * @param aDestination The destination file.
    * @throws IOException
    */
   public static void copyFile( File aSource, File aDestination )
   throws IOException
   {
      if( !aSource.isFile() )
      {
         throw new IOException(MessageFormat.format(AeMessages.getString("AeFileUtil.ERROR_10"), new Object [] {aSource} )); //$NON-NLS-1$
      }

      FileInputStream input = new FileInputStream(aSource);

       try
       {
           FileOutputStream output = new FileOutputStream(aDestination);
           try
           {
               AeFileUtil.copy( input, output );
           }
           finally
           {
               AeCloser.close(output);
           }
       }
       finally
       {
           AeCloser.close(input);
       }

       if(aSource.length() != aDestination.length())
       {
          throw new IOException(MessageFormat.format(AeMessages.getString("AeFileUtil.ERROR_11"), //$NON-NLS-1$
                                                     new Object[] {aSource, aDestination}));
       }
   }


   /**
    * Read the contents of the input stream and write them to the output stream.
    * Uses the default buffer size.  The method returns the number of bytes that
    * copied.
    * @param aIn
    * @param aOut
    * @throws IOException
    */
   public static long copy( InputStream aIn, OutputStream aOut ) throws IOException
   {
      return copy( aIn, aOut, DEFAULT_BUFFER );
   }

   /**
    * Read the contents of the input stream and write them to the output stream.
    * Uses the given buffer size.  Returns the number of bytes copied.
    * @param aIn
    * @param aOut
    * @param aBufferSize The buffer size to use for reading.
    * @return the number of bytes copied
    * @throws IOException
    */
   public static long copy( InputStream aIn, OutputStream aOut, int aBufferSize )
   throws IOException
   {
      byte[] buffer = new byte[ aBufferSize ];
      int read = 0;
      long totalBytes = 0;
      while (-1 != (read = aIn.read(buffer)) )
      {
          aOut.write(buffer, 0, read);
          totalBytes += read;
      }
      return totalBytes;
   }

   /**
    * Recursively deletes the given directory and all of
    * its nested contents. Use carefully!
    * @param aDirectory
    */
   public static void recursivelyDelete( File aDirectory )
   {
      if( !aDirectory.isDirectory() )
      {
         return;
      }

      File[] files = aDirectory.listFiles();

      if( files != null )
      {
         for( int i = 0; i < files.length; i++ )
         {
            if( files[i].isFile() )
            {
               files[i].delete();
            }
            else
            {
               recursivelyDelete( files[i] );
            }
         }
      }
      aDirectory.delete();
   }

   /**
    * Copies a given ZIP entry to the given output stream.
    *
    * @param aIn The ZIP input stream.
    * @param aOut The output stream to write to.
    * @throws IOException
    */
   protected static void copyEntry(ZipInputStream aIn, OutputStream aOut) throws IOException
   {
      try
      {
         AeFileUtil.copy(aIn, aOut);
      }
      finally
      {
         try
         {
            aIn.closeEntry();
         }
         catch (IOException io)
         {
            io.printStackTrace();
         }
      }
   }

   /**
    * Unpacks the contents of a given ZIP file to the given target directory.
    *
    * @param aFile The source ZIP file.
    * @param aTargetDir The target directory to unpack into.
    * @throws IOException
    */
   public static void unpack(File aFile, File aTargetDir) throws IOException
   {
      File rootDir = aTargetDir;
      rootDir.mkdirs();

      ZipInputStream in = null;
      try
      {
         in = new ZipInputStream(new FileInputStream(aFile));
         ZipEntry entry = in.getNextEntry();
         while (entry != null)
         {
            FileOutputStream out = null;
            try
            {
               if (!entry.isDirectory())
               {
                  File newFile = new File(rootDir, entry.getName());
                  newFile.getParentFile().mkdirs();
                  out = new FileOutputStream(newFile);
                  copyEntry(in, out);
               }
               entry = in.getNextEntry();
            }
            catch (IOException io)
            {
               io.printStackTrace();
               throw io;
            }
            finally
            {
               AeCloser.close(out);
            }
         }
      }
      finally
      {
         AeCloser.close(in);
      }
   }

   /**
    * Returns the filename without the extension.  Will return the
    * string passed in if the filename does not contain a '.' character.
    * @param aFileName
    */
   public static String stripOffExtenstion( String aFileName )
   {
      if( aFileName.indexOf('.') != -1 )
      {
         return aFileName.substring( 0, aFileName.lastIndexOf('.') );
      }
      else
      {
         return aFileName;
      }
   }

   /**
    * Returns the file extension given name. The extension string after the last period.
    * @param aFileName
    * @return file extension or empty string if not available.
    */
   public static String getExtension( String aFileName )
   {
      int i = aFileName.lastIndexOf("."); //$NON-NLS-1$
      if (i != -1 && i < aFileName.length()-2)
      {
         return aFileName.substring(i+1).trim();
      }
      else
      {
         return ""; //$NON-NLS-1$
      }
   }

   /**
    * Splits the given filename to filepath and file name.  For example, C:/temp/hello.txt.
    * returns array tuple("C:/temp", "hello.text").
    * @param aAbsoluteFilePath
    * @return String array with two non-null entries for filepath and filename.
    */
   public static String[] splitFilePathAndName(String aAbsoluteFilePath)
   {
      // normalize path and remove repeating file separators.
      aAbsoluteFilePath = AeUtil.getSafeString(aAbsoluteFilePath).replace('\\', '/').replaceAll("/+", "/"); //$NON-NLS-1$ //$NON-NLS-2$
      String rval [] = new String[2];
      int idx = aAbsoluteFilePath.lastIndexOf('/');
      if (idx != -1)
      {
         rval[0] = aAbsoluteFilePath.substring(0, idx);
         rval[1] = aAbsoluteFilePath.substring(idx + 1);
      }
      else
      {
         rval[0] = ""; //$NON-NLS-1$
         rval[1] = aAbsoluteFilePath;
      }
      return rval;
   }

   /**
    * Given a path (either absolute or relative) returns the absolute path.  This method
    * tests to see if the given path is absolute or relative.  If it's absolute, it simply
    * returns it unchanged.  If it is relative, then it assumes the path is relative to
    * the parent location.
    * 
    * 
    * @param aParentLocation
    * @param aChildLocation
    */
   public static String resolveFileLocation(String aParentLocation, String aChildLocation)
   {
      String path = aChildLocation;

      if (AeUtil.isNullOrEmpty(path))
         return null;

      boolean isRelative = true;
      if (path.startsWith("/") || path.startsWith("\\")) //$NON-NLS-1$ //$NON-NLS-2$
      {
         isRelative = false;
      }
      else if (path.charAt(1) == ':')
      {
         isRelative = false;
      }

      if (isRelative)
      {
         String basePath = aParentLocation;
         basePath = basePath.replace('\\', '/');
         basePath = basePath.substring(0, basePath.lastIndexOf('/') + 1);
         path = basePath + path;
      }
      return path;
   }

   /**
    * Determine if the given String represents an absolute path by
    * checking if the string starts with a '/' or 
    * is a URI that starts with a scheme 'scheme:/'.
    * 
    * @param aPath
    * @return true if path is absolute, otherwise false.
    */
   public static boolean isAbsolute(String aPath)
   {
      boolean absolute = false;
      String path = aPath.replace('\\', '/');
      // the path has a scheme if a colon appears before the first '/'
      if ( path.indexOf(':') > 0 && path.indexOf('/') >  path.indexOf(':') )
      {
         absolute = true;
      }
      // starts with a '/'
      else if (path.startsWith("/")) //$NON-NLS-1$
      {
         absolute = true;
      }
      
      return absolute;
   }

   /**
    * Return a location relative to a given base location.  See unit tests for examples.
    * 
    * @param aBaseFile the base location as an absolute path.
    * @param aFile the location of the file.  My have any format, i.e. relative, project, file, URI, etc.
    * @return the location of aFile relative to aBaseFile.
    */
   public static String createRelativeLocation(String aBaseFile, String aFile)
   {
      String location = aFile;
      String base = aBaseFile;
      
      // Return location if either the base of the location are empty.
      if( AeUtil.isNullOrEmpty(base) || AeUtil.isNullOrEmpty(location) )
      {
         return location;
      }
      
      // Return location if it's already a relative location.
      if( ! isAbsolute(location) )
      {
         return location;
      }
   
      location = aFile.replace('\\', '/');
      base = aBaseFile.replace('\\', '/');
      
      // Remove the trailing process filename from the base, leaving a trailing delimitor.
      base = base.substring(0, base.lastIndexOf('/')+1);
      
      // Remove the trailing import filename from the location, leaving a trailing delimitor.
      String locationPath = location.substring(0, location.lastIndexOf('/')+1);
      
      if ( base.equals(locationPath) )
      {
         // Location file is located directly in the base. Just return the filename.
         return location.substring(location.lastIndexOf('/')+1);
      }
      
      if ( isAbsolute(location) )
      {
         // Tokenize the base.
         StringTokenizer baseToken = new StringTokenizer(base.trim(), "/"); //$NON-NLS-1$
         int baseTokenCount = baseToken.countTokens();
         
         // Tokenize the location.
         StringTokenizer locationToken = new StringTokenizer(locationPath.trim(), "/"); //$NON-NLS-1$
         
         // Count the common subfolders from the root.
         int commonFolders = 0;
         while ( baseToken.hasMoreTokens() && locationToken.hasMoreTokens() )
         {
            if ( baseToken.nextToken().equals(locationToken.nextToken() ) )
               commonFolders++;
            else
               break;
         }
         
         // if no common root then leave location alone otherwise process common folders
         if(commonFolders > 0)
         {
            // Remove common root folders from the location.
            locationToken = new StringTokenizer(locationPath.trim(), "/"); //$NON-NLS-1$
            int x = 0;
            StringBuffer buffer = new StringBuffer();
            while ( locationToken.hasMoreTokens() )
            {
               String token = locationToken.nextToken();
               if ( ++x > commonFolders )
               {
                  if ( buffer.length()>0 )
                     buffer.append("/"); //$NON-NLS-1$
                  buffer.append(token);
               }
            }
            
            // Tack on the location file.
            buffer.append("/").append(location.substring(location.lastIndexOf('/')+1)); //$NON-NLS-1$
            
            // insert parent path indicators "../" as needed.
            StringBuffer parents = new StringBuffer();
            for(int i=0; i< (baseTokenCount-commonFolders); i++)
            {
               if (parents.length()>0)
                  parents.append("/"); //$NON-NLS-1$
               parents.append(AeFileUtil.PARENT_DIRECTORY_INDICATOR);
            }
               
            if ( parents.length() > 0 && buffer.indexOf("/") != 0 ) //$NON-NLS-1$
               parents.append("/"); //$NON-NLS-1$
   
            location = parents + buffer.toString();
         }
      }
      
      return location;
   }
}

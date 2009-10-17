//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/provider/ldap/AeLdifFileReader.java,v 1.1 2007/03/26 19:30:23 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity.provider.ldap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.activebpel.rt.identity.AeMessages;

/**
 * Class which reads in a LDIF file.
 */
public class AeLdifFileReader
{
   /** LDIF file. */
   private File mFile;

   /** Stream reader. */
   private BufferedReader mReader = null;;

   /**
    * Ctor
    * @param aFile
    */
   public AeLdifFileReader(File aFile)
   {
      setFile(aFile);
   }

   /**
    * Opens the ldif file stream for reading.
    * @throws IOException
    */
   public void open() throws IOException
   {
      // close if already open.
      close();
      BufferedReader reader = new BufferedReader( new FileReader( getFile() ));
      setReader(reader);
   }

   /**
    * Reads the next LDIF entry.
    * @return LDIF entry or <code>null</code> if not found.
    * @throws IOException
    */
   public AeLdapEntry read() throws IOException
   {
      if (getReader() == null)
      {
         throw new IOException( AeMessages.getString("AeLdifFileReader.STREAM_NOT_OPENED") ); //$NON-NLS-1$
      }
      // ldif entry
      AeLdapEntry entry = null;
      StringBuffer buffer = new StringBuffer();
      String line = null;
      boolean dataFound = false;
      while ( (line = getReader().readLine()) != null)
      {
         if (line.startsWith("#"))//$NON-NLS-1$
         {
            // LDIF comment.
            continue;
         }
         if (dataFound && line.startsWith(" ") && line.trim().length() != 0) //$NON-NLS-1$
         {
            // continuation of previous line.
            buffer.append(line);
            continue;
         }
         line = line.trim();
         if (!dataFound && !line.startsWith("dn:")) //$NON-NLS-1$
         {
            // skip until start of ldif entry is found
            continue;
         }
         if (!dataFound && line.length() == 0)
         {
            // skip through leading blank lines
            continue;
         }
         else if (line.length() == 0)
         {
            // ldif entry delimiter
            dataFound = false;
            // end of record
            break;
         }
         dataFound = true;
         if (buffer.length() > 0)
         {
            if (buffer.toString().startsWith("dn:")) //$NON-NLS-1$
            {
               entry = new AeLdapEntry();
            }
            // collect data each attribute (includes multi-line values).
            addProperty(entry, buffer.toString());
            buffer.setLength(0);
         }
         buffer.append(line);
      }
      // finally, collect the last line.
      addProperty(entry, buffer.toString());
      return entry;
   }

   /**
    * Splits the given LDIF entry line and adds the name and value as a property
    * to the LDIF entry object.
    * @param aEntry
    * @param aNameValueLine
    */
   protected void addProperty(AeLdapEntry aEntry, String aNameValueLine)
   {
      if (aEntry == null)
      {
         return;
      }
      int idx = aNameValueLine.indexOf(':');
      if (idx > 0)
      {
         String name = aNameValueLine.substring(0, idx).trim();
         String value = aNameValueLine.substring(idx + 1).trim();
         aEntry.addProperty(name, value);
      }
   }

   /**
    * Closes the current stream.
    *
    */
   public void close()
   {
      if (getReader() != null)
      {
         try
         {
            getReader().close();
         }
         catch(Exception ioe)
         {
         }
         finally
         {
            setReader(null);
         }
      }
   }

   /**
    * @return the reader
    */
   protected BufferedReader getReader()
   {
      return mReader;
   }

   /**
    * @param aReader the reader to set
    */
   protected void setReader(BufferedReader aReader)
   {
      mReader = aReader;
   }

   /**
    * @return the file
    */
   protected File getFile()
   {
      return mFile;
   }

   /**
    * @param aFile the file to set
    */
   protected void setFile(File aFile)
   {
      mFile = aFile;
   }
}

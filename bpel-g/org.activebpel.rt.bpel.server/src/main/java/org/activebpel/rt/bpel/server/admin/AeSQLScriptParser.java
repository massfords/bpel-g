// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/admin/AeSQLScriptParser.java,v 1.2 2005/02/01 19:56:36 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.activebpel.rt.util.AeUtil;

/**
 * Utility to read in sql script files and return (string) sql statements.
 * NOTE: Sql statements and comments can span multiple lines, but they
 * should not be intertwined.  Multiple sql statements should not appear
 * on the same line.
 */
public class AeSQLScriptParser
{
   /** Reader for the sql file source. */
   protected BufferedReader mReader;
   /** Sql statement string. */
   String mNextStatement;
   
   /**
    * Constructor.
    * @param aReader Reader for the sql file source.  Callers 
    * are responsible for closing this reader after the next() call
    * returns false. 
    */
   public AeSQLScriptParser( Reader aReader )
   {
      mReader = new BufferedReader(aReader);
   }
   
   /**
    * Moves the reader to the next statement.
    * @return Returns true if there is a current statement.
    * @throws IOException
    */
   public boolean next() throws IOException
   {
      StringBuffer buffer = new StringBuffer();
      String line = null;
      String sep = ""; //$NON-NLS-1$
      boolean isLineWithSemiColon = false;
      
      while( !isLineWithSemiColon && (line=getReader().readLine()) != null )
      {
         line = line.trim();
         if( !isCommentOrEmptyLine( line ) )
         {
            buffer.append( sep );
            buffer.append( line );
            sep = " "; //$NON-NLS-1$
            isLineWithSemiColon = line.endsWith(";");       //$NON-NLS-1$
         }
      }
      
      setNextStatement( buffer.toString() );
      return !AeUtil.isNullOrEmpty( getNextStatement() );
   }
   
   /**
    * Returns true if the line is a comment or empty.
    * @param aLine
    */
   protected boolean isCommentOrEmptyLine( String aLine )
   {
      return AeUtil.isNullOrEmpty(aLine) || aLine.startsWith("#"); //$NON-NLS-1$
   }
   
   /**
    * Getter for the sql statement.
    */
   public String getNextStatement()
   {
      return mNextStatement;
   }
   
   /**
    * Setter for the sql statement.
    * @param aStatement
    */
   protected void setNextStatement( String aStatement )
   {
      mNextStatement = aStatement;
   }
   
   /**
    * Getter for the reader.
    */
   protected BufferedReader getReader()
   {
      return mReader;
   }
}

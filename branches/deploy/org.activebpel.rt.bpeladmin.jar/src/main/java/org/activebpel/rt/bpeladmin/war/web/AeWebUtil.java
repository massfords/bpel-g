// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/AeWebUtil.java,v 1.8 2008/02/17 21:43:06 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpeladmin.war.AeMessages;
import org.activebpel.rt.util.AeUtil;

/**
 * Utility class for common bean operations.
 */
public class AeWebUtil
{
   
   /**
    * Convert QName to string.  If the namespace is null/empty,
    * only the local part is returned, otherwise the format is
    * <code>namespaceuri:localpart</code>.
    * @param aQName
    */
   public static String toString( QName aQName )
   {
      String retVal = ""; //$NON-NLS-1$
      if( aQName != null )
      {
         String ns = AeUtil.isNullOrEmpty( aQName.getNamespaceURI() ) ? "" : (aQName.getNamespaceURI()+":"); //$NON-NLS-1$ //$NON-NLS-2$
         retVal = ns+aQName.getLocalPart();
      }
      return retVal;
   }
   
   /**
    * Converts a string to a QName.  
    * Expects the string to be in the <code>namespace:localpart</code> or
    * <code>localpart</code> format. 
    * @param aString
    */
   public static QName toQName( String aString )
   {
      if( !AeUtil.isNullOrEmpty( aString ) )
      {
         // strip leading/trailing spaces.
         aString = aString.trim();
         String nsURI = ""; //$NON-NLS-1$
         String localPart = aString;
         int colonOffset = aString.lastIndexOf(':');
         if( colonOffset != -1 )
         {
            nsURI = aString.substring(0, colonOffset);
            localPart = aString.substring( colonOffset+1 );
         }
         return new QName(nsURI,localPart);   
      }
      else
      {
         return null;
      }
   }
   
   /**
    * Converts the string to a date using the specified 
    * date format pattern.
    * @param aPattern The date format pattern (e.g. MM/dd/yyyy).
    * @param aDate The date string.
    */
   public static Date toDate( String aPattern, String aDate ) throws AeException
   {
      if( AeUtil.isNullOrEmpty(aDate) )
      {
         return null;
      }
      else
      {
         DateFormat dateFormat = null;
      
         if( AeUtil.isNullOrEmpty( aPattern ) )
         {
            dateFormat = DateFormat.getDateInstance( DateFormat.SHORT );   
         }
         else
         {
            dateFormat = new SimpleDateFormat( aPattern );
         }
         dateFormat.setLenient( true );
         try
         {
            return dateFormat.parse( aDate );
         }
         catch (ParseException e)
         {
            throw new AeException(MessageFormat.format(AeMessages.getString("AeWebUtil.ERROR_0"),  //$NON-NLS-1$
                                                       new Object[] {aDate, e})); 
         }
      }
   }
   
   /**
    * Extract string data from map as a string.
    * @param aMap
    */
   public static String toString( Map aMap )
   {
      // @todo - put this in for the ui - may want to revisit this with something
      // that allows more control over th display
      StringBuffer sb = new StringBuffer();
      String sep = ""; //$NON-NLS-1$
      for( Iterator iter = aMap.entrySet().iterator(); iter.hasNext(); )
      {
         Map.Entry entry = (Map.Entry)iter.next();
         sb.append( sep );
         sb.append( entry.getKey() );
         sb.append( "=" ); //$NON-NLS-1$
         sb.append( entry.getValue() );
         sep="\\n"; //$NON-NLS-1$
      }
      return sb.toString();
   }
   
   /**
    * Escapes the single quotes (') character with a back slash.
    * @param aValue string to be escaped.
    * @return escaped string.
    */
   public static String escapeSingleQuotes(String aValue)
   {
      aValue = AeUtil.getSafeString(aValue);
      StringBuffer sb = new StringBuffer();
      
      synchronized(sb)
      {
         for (int j =0; j < aValue.length(); j++)
         {
            if (aValue.charAt(j) == '\'') 
            {
               sb.append("\\");  //$NON-NLS-1$
            }
            sb.append(aValue.charAt(j));
   
         }
      }
      return sb.toString();
   }   
}

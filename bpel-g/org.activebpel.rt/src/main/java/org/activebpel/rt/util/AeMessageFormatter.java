// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeMessageFormatter.java,v 1.6.22.1 2008/04/21 16:05:13 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util;


import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;

import org.activebpel.rt.AeException;
import org.activebpel.rt.AeMessages;

/**
 * <p>
 * This abstract base forms the foundation of indexed, formatted output, which facilitates
 * management of the format strings as localizable resources.</p>
 * <p>
 * 
 * A list of argument values is managed at this level and, when the client requests,
 * those arguments are formatted using a format string that is chosen by a subclass-defined
 * key and map.</p>
 * <p>
 * Subclasses maintain a (usu. static) HashMap of key / IDs to message format strings,
 * as well as a list of argument indices that correspond to the set of format arguments
 * being used.</p>
 */
public abstract class AeMessageFormatter
{
   /** Array of arguments managed by this instance. */
   private Object[] mArguments ;
   
   /**
    * Base ctor to init all args to empty string.
    */
   public AeMessageFormatter()
   {
      for ( int i = 0 ; i < getMaxArgs() ; i++ )
         setArgument( i, "" ); //$NON-NLS-1$
   }
   
   /**
    * Override to populate the format map.  This method should initialize a static
    * map the first time it is called.  The map is maintained at the subclass level,
    * rather than here, so that different subclasses can maintain different format
    * mappings simultaneously, e.g., a Simulator can be running in one editor page,
    * with a set of simulator message formats, while a Live Debugger can be running 
    * in another page using a (different) set of debugger message formats.
    */
   public abstract HashMap getFormatMap();
   
   /**
    * Implementors will provide the format string accessor applicable to their
    * environment.  If no format string is mapped to the specified key, then the
    * key itself is returned (not null, not an empty string).
    *  
    * @param aKey The key that identifies the desired format string.
    * 
    * @return String The corresponding format string, or <code>aKey</code> if not found.
    */
   public abstract String getFormatString( String aKey );
   
   /**
    * Implementors will provide a resource string accessor applicable to their
    * environment.  If no resource string is mapped to the specified key, then the
    * key itself is returned (not null, not an empty string).
    *  
    * @param aKey The key that identifies the desired resource string.
    * 
    * @return String The corresponding resource string, or <code>aKey</code> if not found.
    */
   public abstract String getResourceString( String aKey );
   
   /**
    * Define the maximum number of arguments made available for formatting.  This
    * will be specified by subclasses.
    * 
    * @return int
    */
   public abstract int getMaxArgs();
   
   /**
    * <p>
    * Use the plugin.properties property root to cycle through all format definitions
    * for that group and add the formats to the map.</p>
    * <p>
    * Lists of Message Formatting strings are defined in plugin.properties as:</p>
    * <p>
    * &lt;propertyRoot&gt;.n=m<br/>
    * &lt;propertyRoot&gt;.ID.m=&lt;format string&gt;</p>
    * <p> 
    * e.g., for a &lt;propertyRoot&gt; of <b>MessageFormatting.Debug.Event</b>:</p>
    * <p>
    * MessageFormatting.Debug.Event.0=1004<br/>
    * MessageFormatting.Debug.Event.ID.1004=Link {8}: status is {5} [{4}]</p>
    * <p>
    * In the above case, '1004' should correspond to a message format or event ID
    * in the client environment.  This is the value that will be used to identify
    * the format string later, in AeMessageFormatter.format( int ).</p>
    * <p>
    * 'n' values should be sequential, starting with 0<br/>
    * 'm' values should be integers, and must correspond to IDs for which format
    *   strings are defined by the subclass.
    * </p>
    * @param aPropertyRoot The base key string for the message format group.
    */
   public void populateMap( String aPropertyRoot )
   {
      String idPrefix = aPropertyRoot + "." ; //$NON-NLS-1$
      String fmtPrefix = aPropertyRoot + ".ID." ; //$NON-NLS-1$
      int i = 0 ;
      while ( true )
      {
         // Use the property root to get the ith format key, which is
         //  a resource string.
         //
         String fmtIDKey = idPrefix + Integer.toString(i);
         String fmtKey = getResourceString( fmtIDKey );
         String fmt = ""; //$NON-NLS-1$
         if ( fmtKey == null || fmtKey.equals( fmtIDKey ))
            break ;

         try
         {
            // Create a sequential key to get the format string.
            //
            String idKey = fmtPrefix + fmtKey ;
            fmt = getFormatString( idKey );
            if ( fmt.equals( idKey ))
               throw new AeException( AeMessages.getString("AeMessageFormatter.ERROR_4") + fmtKey ); //$NON-NLS-1$

            mapFormatToID( Integer.parseInt( fmtKey ), fmt );
         }
         catch (Exception e)
         {
            AeException.logError(e, AeMessages.getString("AeMessageFormatter.ERROR_5") + fmtIDKey + "/" + fmtKey + "." ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            break ;
         }
         
         i++ ;
      }
   }
   
   /**
    * Base helper to map an ID to a message format string.
    *  
    * @param aID The ID to use as a key.
    * @param aFormat The format string associated with this key.
    */
   protected void mapFormatToID( int aID, String aFormat )
   {
      if ( getFormatMap() != null )
         getFormatMap().put( new Integer( aID ), aFormat );
   }
   
   /**
    * clears any values from the argument array 
    */
   protected void clearArguments()
   {
      Arrays.fill(getArguments(), null);
   }
   
   /**
    * Lazy initializer / accessor for argument array.
    * 
    * @return Object[]
    */
   private Object[] getArguments()
   {
      if ( mArguments == null )
         mArguments = new Object[getMaxArgs()];
         
      return mArguments ;
   }
   
   /**
    * Returns the string version of the argument stored, or an empty string.
    * 
    * @param aIndex The argument index of the desired string.
    * 
    * @return String
    */
   public String getArgument( int aIndex )
   {
      if ( aIndex >= 0 && aIndex < getMaxArgs() && getArguments()[aIndex] != null )
         return getArguments()[aIndex].toString();
      else
         return "" ; //$NON-NLS-1$
   }
   
   /**
    * Set one of the formatter's argument values, determined by the index provided.
    * 
    * @param aIndex The index of the argument to set.  Must be less than getMaxArgs().
    * @param aArgValue The argument value to set.
    */
   public void setArgument( int aIndex, Object aArgValue )
   {
      if ( aIndex >= 0 && aIndex < getMaxArgs())
         getArguments()[aIndex] = aArgValue ;
   }
   
   /**
    * Overload for boolean argument values.
    * 
    * @param aIndex The index of the argument to set.
    * @param aArgValue The argument value to set.
    */
   public void setArgument( int aIndex, boolean aArgValue )
   {
      setArgument( aIndex, new Boolean( aArgValue ));
   }

   /**
    * Overload for int argument values.
    * 
    * @param aIndex The index of the argument to set.
    * @param aArgValue The argument value to set.
    */
   public void setArgument( int aIndex, int aArgValue )
   {
      setArgument( aIndex, new Integer( aArgValue ));
   }

   /**
    * Overload for float and double argument values.
    * 
    * @param aIndex The index of the argument to set.
    * @param aArgValue The argument value to set.
    */
   public void setArgument( int aIndex, double aArgValue )
   {
      setArgument( aIndex, new Double( aArgValue ));
   }
   
   /**
    * Get the format string for the specified index.
    * 
    * @param aFormatIndex The index key to use for lookup.
    * 
    * @return String
    */
   public String getFormatString( int aFormatIndex )
   {
      return (String)getFormatMap().get( new Integer( aFormatIndex ) );
   }
   
   /**
    * Format the current list of arguments for the message format associated
    * with the index key provided.
    * 
    * @param aFormatIndex
    */
   public String format(int aFormatIndex)
   {
      return format(aFormatIndex, getArguments());
   }
   
   /**
    * Format the current list of arguments for the message format associated
    * with the index key provided.
    * 
    * @param aFormatIndex The key - typically an engine event or engine info event ID - that
    * identifies the message format to use.
    * @param aArguments arguments used in formatting 
    * 
    * @return String
    */
   public String format( int aFormatIndex, Object[] aArguments )
   {
      String msg = "" ; //$NON-NLS-1$
      if ( getFormatMap() == null || getFormatMap().isEmpty())
      {
         msg = AeMessages.getString("AeMessageFormatter.10") ; //$NON-NLS-1$
      }
      else if ( getFormatString( aFormatIndex ) == null )
      {
         // -1 non-status is not a valid format key.
         //
         if ( aFormatIndex != -1 )
            msg = AeMessages.getString("AeMessageFormatter.11") + aFormatIndex ; //$NON-NLS-1$
      }
      else if ( aArguments == null || aArguments.length <= 0 )
      {
         msg = AeMessages.getString("AeMessageFormatter.12") + toString() ; //$NON-NLS-1$
      }
      else
      {
         MessageFormat fmt = new MessageFormat( getFormatString( aFormatIndex ));
         try
         {
            // The following line is the point of all this - it formats the arguments
            //  we've collected using the format string that is dereferenced above.
            //
            msg = fmt.format( aArguments );
         }
         catch ( IllegalArgumentException iae )
         {
            msg = MessageFormat.format(AeMessages.getString("AeMessageFormatter.0"), //$NON-NLS-1$
                                       new Object[] {getFormatString( aFormatIndex ),
                                                     Arrays.asList(aArguments).toString(), iae.getLocalizedMessage()});

            AeException.logError( iae, AeMessages.getString("AeMessageFormatter.ERROR_16") + getClass().getName() ); //$NON-NLS-1$
         }
      }
      
      return msg ;
   }
}

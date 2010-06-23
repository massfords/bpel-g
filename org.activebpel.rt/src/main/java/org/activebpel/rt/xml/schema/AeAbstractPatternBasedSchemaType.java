// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/AeAbstractPatternBasedSchemaType.java,v 1.1 2006/09/07 14:41:12 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xml.schema;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.activebpel.rt.AeMessages;

/**
 * Base class for any schema type that can be constructed from a String and parsed using a regular
 * expression.
 */
public abstract class AeAbstractPatternBasedSchemaType implements IAeSchemaType
{
   /**
    * Default c'tor.
    */
   protected AeAbstractPatternBasedSchemaType()
   {
   }

   /**
    * Constructs the schema regular expression parsed type.  Calls the <code>parseSchemaType</code>
    * method to parse the string value.
    * 
    * @param aValue
    */
   protected AeAbstractPatternBasedSchemaType(String aValue)
   {
      parseSchemaType(aValue);
   }

   /**
    * Parse the schema type string and process the resulting groups.
    * 
    * @param aValue
    */
   protected void parseSchemaType(String aValue)
   {
      Matcher matcher = getInputPattern().matcher(aValue);
      if (!matcher.matches())
      {
         String msg = MessageFormat.format(AeMessages.getString("AeSchemaRegexParsedStringType.InvalidSchemaTypeFormat"), //$NON-NLS-1$
               new Object[] { aValue, getSchemaTypeName() });
         throw new AeSchemaTypeParseException(msg);
      }
      processMatcher(matcher);
   }
   
   /**
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      Object [] args = getOutputPatternArguments();
      return MessageFormat.format(getOutputPattern(), args);
   }

   /**
    * Gets the pattern to use when parsing the value.
    */
   protected abstract Pattern getInputPattern();

   /**
    * Called to process the result of the match (only called when the input matches the regular 
    * expression).
    * 
    * @param aMatcher
    */
   protected abstract void processMatcher(Matcher aMatcher);
   
   /**
    * Returns the name of the schema type represented by this class.  Subclasses should implement
    * this for error reporting purposes.
    */
   protected abstract String getSchemaTypeName();

   /**
    * Returns the arguments used in the output pattern.
    */
   protected abstract Object [] getOutputPatternArguments();
   
   /**
    * Returns the output pattern to use for <code>toString</code>.
    */
   protected abstract String getOutputPattern();
}

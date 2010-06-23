//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeAbstractXpathNodeTextFormatterTag.java,v 1.1 2008/01/11 15:17:44 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.tags;

import java.text.Format;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.activebpel.rt.AeException;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.war.AeMessages;

/**
 * <p>Base class for formatting xpath node text</p>
 */
public abstract class AeAbstractXpathNodeTextFormatterTag extends AeXpathSelectNodeTextTag
{
   /** pattern for the date or number format */
   private String mPattern = null;
   /** The resource key to use to look up the date or number format. */
   private String mPatternKey;

   public int doStartTag() throws JspException
   {
      try
      {
         // call abstract method format the text
         write( formatText( getNodeText() ) );
      }
      catch(AeException ae)
      {
         // display error to the user
         write( ae.getMessage() );
      }
      return SKIP_BODY;
   }

   /**
    * Returns the formatted text to be displayed. Subclasses must implement this
    * method and return the formatted text to be displayed (written out to the pageContext).
    * @return text to be displayed.
    * @throws AeException if error occur during text formatting.
    * @throws JspException
    */
   protected abstract String formatText(String aText) throws AeException, JspException;

   /**
    * @return Returns the pattern.
    */
   public String getPattern()
   {
      return mPattern;
   }

   /**
    * @param aPattern The pattern to set.
    */
   public void setPattern(String aPattern)
   {
      mPattern = aPattern;
   }

   /**
    * @return The resource key.
    */
   public String getPatternKey()
   {
      return mPatternKey;
   }

   /**
    * @param aKey The key to set.
    */
   public void setPatternKey(String aKey)
   {
      mPatternKey = aKey;
   }

   /**
    * Returns the format pattern.  This method checks the 'pattern' attribute first.  If there is
    * no value specified in 'pattern', then it uses the key specified in
    * based on the resource bundle is returned, otherwise, the literal set in the tag is returned.
    *
    * @return format pattern literal set in the tag, or the pattern found in the resource bundle.
    * @throws AeException if pattern is not found in the resource bundle.
    */
   protected String getResolvedPattern() throws AeException
   {
      // Get the pattern set by the user (either directly or indirectly via the resource bundle).
      String pattern = getPattern();
      if (AeUtil.isNullOrEmpty(pattern))
      {
         pattern = getResourceString(getPatternKey());
      }
      if (AeUtil.isNullOrEmpty(pattern))
      {
         throw new AeException(AeMessages.getString("AeAbstractPropertyFormatterTag.NO_PATTERN_ERROR")); //$NON-NLS-1$
      }

      // Return the final pattern.
      return pattern;
   }

   /**
    * Gets the Format object from the pageContext, creating it if it doesn't exist based on the
    * resolved pattern. The Format object is the base class of Number and DataFormat objects.
    * Subclasses must cast this to its appropriate type before using.
    *
    * @return the Format object based on the current pattern.
    * @throws AeException if unable to create and return the propert formatter.
    */
   protected Format getResolvedFormatter() throws AeException
   {
      // Get the resolved pattern.
      String pattern = getResolvedPattern();
      // The key used in the pageContext
      String key = getClass().getName() + "." + pattern; //$NON-NLS-1$
      Format textFormatter = (Format) pageContext.getAttribute(key, PageContext.REQUEST_SCOPE);
      if (textFormatter == null)
      {
         // Ask the subclass to create the specific pattern.
         textFormatter = createFormatter(pattern);
         pageContext.setAttribute(key, textFormatter, PageContext.REQUEST_SCOPE);
      }
      return textFormatter;
   }

   /**
    * Abstract factory method that returns the concrete Format object. Subclass must create
    * and return the proper Format object such as java.text.DateFormat.
    *
    * @param aPattern
    * @return Concrete Format object.
    * @throws AeException if unable to create and return a Formatter object.
    */
   protected abstract Format createFormatter(String aPattern) throws AeException;


}

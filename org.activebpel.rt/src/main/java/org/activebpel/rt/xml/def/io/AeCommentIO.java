// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/io/AeCommentIO.java,v 1.1 2007/09/26 02:17:16 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Handles the details of reading and writing comments
 * during def process serialization/deserialization.
 */
public class AeCommentIO
{

   /** Accumulated comments awaiting an encounter with their corresponding comment-aware def element. */
   private StringBuffer sLastComments = new StringBuffer();

   /**
    * Comments precede activity node.  Check to see if any comment
    * strings preceeded the current activity node and if they did,
    * add them to the <code>AeActivityObject</code> reference.
    * @param aCurrentDef will be populated with any comments stored in the buffer
    */
   public void preserveComments(AeBaseXmlDef aCurrentDef)
   {
      if ( commentBufferHasComments() )
      {
         addCommentToCurrentActivity(aCurrentDef);
      }
   }

   /**
    * Returns, as a new String, the accumulated comments text then clears its value.  
    * @return String
    */
   public String getAndClearLastComments()
   {
      String comment = sLastComments.toString();
      sLastComments.setLength(0);
      return AeUtil.trimText(comment);
   }

   /**
    * Convenience method to set current comment/s on def
    * @param aBaseDef
    */
   private void addCommentToCurrentActivity(AeBaseXmlDef aBaseDef)
   {
      aBaseDef.setComment( getAndClearLastComments() );
   }


   /**
    * Indicates if any comment nodes preceeded the current def
    * @return boolean status indicating if comment node/s preceeded activity
    */
   private boolean commentBufferHasComments()
   {
      return getLastComments().length() > 0;
   }
   
   /**
    * Accessor method for accumulated BPEL comments.
    * @return StringBuffer.
    */
   private StringBuffer getLastComments()
   {
      return sLastComments;
   }
   
   /**
    * Appends the given string to the end of the accumulated comment string. 
    * A newline is appended prior to the given string if the accumulated comment
    * string is not empty.
    * @param aAppendString the comment to append
    */
   public void appendToComments(String aAppendString)
   {
      if ( aAppendString != null )
      {

         if ( sLastComments.length() > 0 )
         {
            sLastComments.append("\n"); //$NON-NLS-1$
         }

         sLastComments.append(aAppendString);
      }
   }

   /**
    * Generates a formatted XML comment node(s) based upon the given comment string argument.
    * The string argument may contain newline characters.  A separate comment node is generated for
    * each newline delimited substring.  When multiple comment nodes are generated each comment 
    * string is right space-padded for matching right margins.  For Document node arguments, 
    * generated comment nodes are added as children.  For Element node arguments, generated comment 
    * nodes are added as siblings.  
    * @param aNode a Document or Element node for parenting generated comment nodes. 
    * @param aComments the comment string. May contain newline characters.
    */
   public static void writeFormattedComments(Node aNode, String aComments)
   {
      ArrayList formattedComments = formatComments(aComments);
      if ( aNode instanceof Document )
      {
         Document doc = (Document)aNode;
         // add comment nodes as child nodes.
         for (Iterator it = formattedComments.iterator(); it.hasNext(); )
         {
            doc.appendChild( doc.createComment((String)it.next()) );
         }
      }
      else if ( aNode instanceof Element )
      {
         Element elem = (Element)aNode;
         // add comment nodes as preceding sibling nodes.
         for (Iterator it = formattedComments.iterator(); it.hasNext(); )
         {
            String comment = (String) it.next();
            Node commentNode = elem.getOwnerDocument().createComment(comment);
            elem.getParentNode().insertBefore( commentNode, elem );
         }
      }
   }  
    
   /**
    * Breaks up a string of newline delimited text lines into a list. Each line is space padded
    * on the right so that line matches the length of the longest line in the list.
    * @param aComments a string of newline delimited text lines.
    * @return ArrayList a list string lines.
    */
   private static ArrayList formatComments(String aComments)
   {
      ArrayList formattedList = new ArrayList();
      if ( ! AeUtil.isNullOrEmpty(aComments) )
      {
         int maxLen = 0;
         ArrayList commentList = new ArrayList();
         String line;
         BufferedReader buffReader = new BufferedReader(new StringReader(aComments));
         try
         {
            while ( ((line = buffReader.readLine()) != null) )
            {
               line = line.trim();
               maxLen = (line.length() > maxLen ? line.length() : maxLen); 
               commentList.add(line);
            }
         }
         catch (IOException e) { }    // eat the exception. 

         if ( maxLen > 0 )
         {
            // Add string lines to the results list.
            for (Iterator it = commentList.iterator(); it.hasNext(); )
            {
               String commentLine = " " + getPadding((String)it.next(), maxLen) + " "; //$NON-NLS-1$ //$NON-NLS-2$
               formattedList.add(commentLine);
            }
         }
      }
      return formattedList;
   }
   
   /**
    * Space pads, as needed, the given string argument.
    * @param aStr The string to be padded.
    * @param aStrMax The length the string needs to be including padding.
    * @return String the original string text right-padded with spaces as necessary.
    */
   private static String getPadding(String aStr, int aStrMax)
   {
      StringBuffer padString = new StringBuffer(aStr);
      for ( int i=0; i < (aStrMax-aStr.length()); i++ )
         padString.append(" "); //$NON-NLS-1$
      return padString.toString();
   }
}

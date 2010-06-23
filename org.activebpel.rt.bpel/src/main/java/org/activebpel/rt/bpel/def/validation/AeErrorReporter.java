// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/AeErrorReporter.java,v 1.8 2007/09/26 02:21:03 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.xml.def.AeBaseXmlDef;

/**
 * Definition layer (run-time) error reporter.
 */
public class AeErrorReporter implements IAeErrorReporter
{
   /** Root logical node for this error report. */
   private AeProcessDef mRootDefNode ;
   
   /**
    * The map of error lists recorded by this instance. Each entry links a
    * logical node model to a List of error codes corresponding to issues
    * related to that node.
    */
   protected HashMap mErrors ;

   /**
    * The map of warning lists recorded by this instance. Each entry links a
    * logical node model to a List of warning codes corresponding to issues
    * related to that node.
    */
   protected HashMap mWarnings ;

   /**
    * The map of information lists recorded by this instance. Each entry links a
    * logical node model to a List of information codes corresponding to issues
    * related to that node.
    */
   protected HashMap mInfos ;

   /**
    * Substitue the values in aArgs for the '{n}'s in the base message text.
    * 
    * @param aBaseMsg The base message, which may contain '{n}'s.
    * @param aArgs The args used to replace '{n}'s.
    * 
    * @return Formatted message string.
    */
   private String formatIssue( String aBaseMsg, Object[] aArgs )
   {
      if ( aArgs != null && aBaseMsg.indexOf("{") >= 0 ) //$NON-NLS-1$
      {
         MessageFormat form = new MessageFormat( aBaseMsg );
         String result = form.format( aArgs );
         return result ;
      }
      else
      {
         return aBaseMsg ;
      }
   }
   
   /**
    * Remove an issues list entry from a map if it exists.
    * 
    * @param aKey The key that identifies the node whose issues should be removed.
    * @param aMap The map to remove from, if found.
    */
   private void removeIssuesFromMap( Object aKey, HashMap aMap )
   {
      if ( aMap.keySet().contains( aKey ))
         aMap.remove( aKey ); 
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeErrorReporter#removeIssues(java.lang.Object)
    */
   public void removeIssues( Object aNode )
   {
      removeIssuesFromMap( aNode, getErrors());
      removeIssuesFromMap( aNode, getWarnings());
      removeIssuesFromMap( aNode, getInfos());
   }
   
   /**
    * Generic method to add an error, warning or info entry.
    * 
    * @param aCode Error code to set.
    * @param aArgs Object array containing substitution args for '{n}'s.
    * @param aNode Node on which error code is set.
    * @param aMap Map (error, warning or info) in which mapping is added.
    */
   private void addIssue( String aCode, Object[] aArgs, AeBaseXmlDef aNode, HashMap aMap )
   {
      // See if there's an entry for this visual node.  Create if not.
      //
      if ( aMap.get( aNode ) == null )
         aMap.put( aNode, new ArrayList() );         
         
      ArrayList list = (ArrayList) aMap.get( aNode );
      list.add( formatIssue( aCode, aArgs ));
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeErrorReporter#addError(java.lang.String, java.lang.Object[], java.lang.Object)
    */
   public void addError(String aErrorCode, Object[] aArgs, Object aNode)
   {
      addIssue( aErrorCode, aArgs, (AeBaseXmlDef)aNode, getErrors() );
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeErrorReporter#addWarning(java.lang.String, java.lang.Object[], java.lang.Object)
    */
   public void addWarning(String aWarnCode, Object[] aArgs, Object aNode)
   {
      addIssue( aWarnCode, aArgs, (AeBaseXmlDef)aNode, getWarnings() );
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeErrorReporter#addInfo(java.lang.String, java.lang.Object[], java.lang.Object)
    */
   public void addInfo(String aInfoCode, Object[] aArgs, Object aNode)
   {
      addIssue( aInfoCode, aArgs, (AeBaseXmlDef)aNode, getInfos() );
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeErrorReporter#reportErrors()
    */
   public void reportErrors()
   {

   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeErrorReporter#reportWarnings()
    */
   public void reportWarnings()
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeErrorReporter#reportInfo()
    */
   public void reportInfo()
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeErrorReporter#reportAll()
    */
   public void reportAll()
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeErrorReporter#hasErrors()
    */
   public boolean hasErrors()
   {
      return ! getErrors().isEmpty();
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeErrorReporter#hasWarnings()
    */
   public boolean hasWarnings()
   {
      return ! getWarnings().isEmpty();
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeErrorReporter#isProcessErrors()
    */
   public boolean isProcessErrors()
   {
      return ! getErrors().isEmpty();
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeErrorReporter#isProcessWarnings()
    */
   public boolean isProcessWarnings()
   {
      return ! getWarnings().isEmpty();
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeErrorReporter#isProcessInfos()
    */
   public boolean isProcessInfos()
   {
      return ! getInfos().isEmpty();
   }

   /**
    * Get the errors map.
    * 
    * @return HashMap
    */
   public HashMap getErrors()
   {
      if ( mErrors == null )
         mErrors = new HashMap();

      return mErrors;
   }

   /**
    * Get the warnings map.
    * 
    * @return HashMap
    */
   public HashMap getWarnings()
   {
      if ( mWarnings == null )
         mWarnings = new HashMap();

      return mWarnings;
   }

   /**
    * Get the info map.
    * 
    * @return HashMap
    */
   public HashMap getInfos()
   {
      if ( mInfos == null )
         mInfos = new HashMap();

      return mInfos;
   }

   /**
    * Get the root node for the process def we're validating.
    * 
    * @return AeProcessDef
    */
   public AeProcessDef getRootDefNode()
   {
      return mRootDefNode;
   }

   /**
    * Set the root process def node.
    * 
    * @param aDef The AeProcessDef node to set.
    */
   public void setRootDefNode(AeProcessDef aDef)
   {
      mRootDefNode = aDef;
   }
}

// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeDefVariableTypeVisitor.java,v 1.16 2008/01/11 19:31:16 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors;

import java.text.MessageFormat;
import java.util.Iterator;

import javax.wsdl.Part;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.exolab.castor.xml.schema.ElementDecl;

/**
 * Visitor that sets the Part and Type data for each variable.
 */
public class AeDefVariableTypeVisitor extends AeAbstractDefVisitor
{
   /** The WSDL provider set during visitor creation. */
   private IAeContextWSDLProvider mWSDLProvider;
   /** Flag used to track if errors have occurred. */
   private boolean mHasErrors;
   /** Flag indicating if errors should be reported during visit. */
   private boolean mReportErrors;

   /**
    * Constructor for visitor used to set type info for variables.
    * @param aProvider the WSDL provider to facilitate in finding type info.
    */
   public AeDefVariableTypeVisitor(IAeContextWSDLProvider aProvider)
   {
      this(aProvider, true);
   }

   /**
    * Constructor for visitor used to set type info for variables.
    * @param aProvider the WSDL provider to facilitate in finding type info.
    * @param aReportErrors flag indicating if errors should be reported
    */
   public AeDefVariableTypeVisitor(IAeContextWSDLProvider aProvider, boolean aReportErrors)
   {
      mWSDLProvider = aProvider;
      setReportErrors(aReportErrors);
      setTraversalVisitor(new AeTraversalVisitor(new AeDefTraverser(), this));
   }

   /**
    * Initiates the visitation process.
    * @param aProcess the process to be visited.
    */
   public void findTypeInfo(AeProcessDef aProcess) throws AeBusinessProcessException
   {
      visit(aProcess);
      
      if (isHasErrors() && isReportErrors())
         throw new AeBusinessProcessException(AeMessages.getString("AeDefVariableTypeVisitor.ERROR_0")); //$NON-NLS-1$
   }

   /**
    * Handle visitation to the variable. 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeVariableDef)
    */
   public void visit(AeVariableDef aVariable)
   {
      // Skip if already processed.
      if (aVariable.getXMLType() == null)
      {
         try
         {
            // only assign types for messages (element and type vars are not validated)
            if(aVariable.isMessageType() && !aVariable.hasParts())
            {
               AeBPELExtendedWSDLDef def = AeWSDLDefHelper.getWSDLDefinitionForMsg(mWSDLProvider, aVariable.getMessageType());
               if (def != null)
               {
                  for (Iterator iter=def.getMessageParts(aVariable.getMessageType()); iter.hasNext();)
                     aVariable.addPartTypeInfo((Part)iter.next(), def);
               }
               else
               { 
                  throw new AeException(MessageFormat.format(AeMessages.getString("AeDefVariableTypeVisitor.ERROR_1"), //$NON-NLS-1$
                                                             new Object[] {aVariable.getName(), aVariable.getMessageType()}));
               }
            }
            else if (aVariable.isType() && aVariable.getXMLType() == null)
            {
               AeBPELExtendedWSDLDef def = AeWSDLDefHelper.getWSDLDefinitionForType(mWSDLProvider, aVariable.getType());
               if (def != null)
               {
                  aVariable.setXMLType(def.findType(aVariable.getType()));
               }
               else
               { 
                  throw new AeException(MessageFormat.format(AeMessages.getString("AeDefVariableTypeVisitor.ERROR_1"), //$NON-NLS-1$
                                                             new Object[] {aVariable.getName(), aVariable.getType()}));
               }
            }
            else if (aVariable.isElement())
            {
               AeBPELExtendedWSDLDef def = AeWSDLDefHelper.getWSDLDefinitionForElement(mWSDLProvider, aVariable.getElement());
               if (def != null)
               {
                  ElementDecl elemDecl = def.findElement(aVariable.getElement());
                  if (elemDecl == null)
                  {
                     throw new AeException(MessageFormat.format(
                           AeMessages.getString("AeDefVariableTypeVisitor.ERROR_4"), //$NON-NLS-1$
                           new Object[] { aVariable.getName(), aVariable.getElement() }));
                  }
                  else
                  {
                     aVariable.setXMLType(elemDecl.getType());
                  }
               }
               else
               { 
                  throw new AeException(MessageFormat.format(AeMessages.getString("AeDefVariableTypeVisitor.ERROR_1"), //$NON-NLS-1$
                                                             new Object[] {aVariable.getName(), aVariable.getElement()}));
               }
            }
         }
         catch (AeException e)
         {
            setHasErrors(true);
            reportErrors(e, AeMessages.getString("AeDefVariableTypeVisitor.ERROR_3") + aVariable.getName()); //$NON-NLS-1$
         }
      }
      
      traverse(aVariable);
   }

   /**
    * Method to centralize logging of errors.
    * @param aException
    * @param aMessage
    */
   private void reportErrors(AeException aException, String aMessage)
   {
      if (isReportErrors())
         AeException.logError(aException, aMessage); 
   }

   /**
    * Setter for the hasErrors flag
    * @param hasErrors
    */
   protected void setHasErrors(boolean hasErrors)
   {
      mHasErrors = hasErrors;
   }

   /**
    * Getter for the hasErrors flag
    */
   protected boolean isHasErrors()
   {
      return mHasErrors;
   }

   /**
    * Setter for the reportErrors flag
    * @param reportErrors
    */
   protected void setReportErrors(boolean reportErrors)
   {
      mReportErrors = reportErrors;
   }

   /**
    * Getter for the reportErrors flag
    */
   protected boolean isReportErrors()
   {
      return mReportErrors;
   }
}

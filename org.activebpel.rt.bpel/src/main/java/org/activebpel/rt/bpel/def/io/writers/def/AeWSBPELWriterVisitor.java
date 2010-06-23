// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/writers/def/AeWSBPELWriterVisitor.java,v 1.24 2008/03/11 14:47:08 JPerrotto Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.io.writers.def;

import java.util.Collections;
import java.util.Map;

import org.activebpel.rt.bpel.AeExpressionLanguageFactory;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AeCatchDef;
import org.activebpel.rt.bpel.def.AeExtensionActivityDef;
import org.activebpel.rt.bpel.def.AeExtensionDef;
import org.activebpel.rt.bpel.def.AeExtensionsDef;
import org.activebpel.rt.bpel.def.AeImportDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.AeTerminationHandlerDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.IAeExpressionDef;
import org.activebpel.rt.bpel.def.activity.AeAbstractExtensionActivityDef;
import org.activebpel.rt.bpel.def.activity.AeActivityAssignDef;
import org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityIfDef;
import org.activebpel.rt.bpel.def.activity.AeActivityOpaqueDef;
import org.activebpel.rt.bpel.def.activity.AeActivityRepeatUntilDef;
import org.activebpel.rt.bpel.def.activity.AeActivityRethrowDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityValidateDef;
import org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef;
import org.activebpel.rt.bpel.def.activity.support.AeAssignCopyDef;
import org.activebpel.rt.bpel.def.activity.support.AeConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeExtensibleAssignDef;
import org.activebpel.rt.bpel.def.activity.support.AeForDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachBranchesDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromPartDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef;
import org.activebpel.rt.bpel.def.activity.support.AeJoinConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.activity.support.AeQueryDef;
import org.activebpel.rt.bpel.def.activity.support.AeRepeatEveryDef;
import org.activebpel.rt.bpel.def.activity.support.AeSourcesDef;
import org.activebpel.rt.bpel.def.activity.support.AeTargetsDef;
import org.activebpel.rt.bpel.def.activity.support.AeToDef;
import org.activebpel.rt.bpel.def.activity.support.AeToPartDef;
import org.activebpel.rt.bpel.def.activity.support.AeToPartsDef;
import org.activebpel.rt.bpel.def.activity.support.AeTransitionConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeUntilDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.AeElementBasedNamespaceContext;
import org.activebpel.rt.xml.IAeMutableNamespaceContext;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.AeDocumentationDef;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * A WS-BPEL 2.0 implementation of a writer visitor.
 */
public class AeWSBPELWriterVisitor extends AeWriterVisitor
{
   private static final Map sPreferredPrefixes = Collections.singletonMap(IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI, "bpel"); //$NON-NLS-1$
   
   /** Flag indicating if the portType attribute for the WSIO activities should written. */
   private boolean mWritePortTypeAttrib;
   
   /**
    * Constructs a ws-bpel 2.0 writer visitor.
    * 
    * @param aDef
    * @param aParentElement
    * @param aNamespace
    * @param aTagName
    * @param aWritePortTypeAttrib indicates the portType attribute should be written for the WSIO Activities. 
    */
   public AeWSBPELWriterVisitor(AeBaseXmlDef aDef, Element aParentElement, String aNamespace, String aTagName,
                                boolean aWritePortTypeAttrib)
   {
      super(aDef, aParentElement, aNamespace, aTagName, sPreferredPrefixes);
      mWritePortTypeAttrib = aWritePortTypeAttrib;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#writeMessageExchange(java.lang.String)
    */
   protected void writeMessageExchange(String aMessageExchangeValue)
   {
      setAttribute(TAG_MESSAGE_EXCHANGE, aMessageExchangeValue);
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachBranchesDef)
    */
   public void visit(AeForEachBranchesDef aDef)
   {
      super.visit(aDef);
      setAttribute(TAG_FOREACH_BRANCH_COUNTCOMPLETED, aDef.isCountCompletedBranchesOnly(), false);
   }

   /**
    * Write attributes to the Element.
    * @param aDef
    */
   protected void writeAssignToAttributes(AeToDef aDef)
   {
      super.writeAssignToAttributes(aDef);

      if (AeUtil.notNullOrEmpty( aDef.getExpression() ))
      {
         writeExpressionLang(aDef);
         
         Text textNode = getElement().getOwnerDocument().createTextNode(aDef.getExpression());
         getElement().appendChild(textNode);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#writeAssignFromAttributes(org.activebpel.rt.bpel.def.activity.support.AeFromDef)
    */
   protected void writeAssignFromAttributes(AeFromDef aDef)
   {
      super.writeAssignFromAttributes(aDef);

      if (AeUtil.notNullOrEmpty( aDef.getExpression() ))
      {
         writeExpressionLang(aDef);
         
         Text textNode = getElement().getOwnerDocument().createTextNode(aDef.getExpression());
         getElement().appendChild(textNode);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
    */
   public void visit(AeProcessDef aDef)
   {
      super.visit(aDef);

      if(aDef.getExitOnStandardFault() != null)
      {
         setAttribute(TAG_EXIT_ON_STANDARD_FAULT, aDef.getExitOnStandardFault().booleanValue(), false);
      }
      
      IAeMutableNamespaceContext nsContext = new AeElementBasedNamespaceContext( getElement() );
      setAttributeNS(nsContext, IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_QUERY_HANDLING,
            IAeBPELConstants.AE_EXTENSION_PREFIX, TAG_CREATE_TARGET_XPATH, aDef.isCreateTargetXPath(), false);
      setAttributeNS(nsContext, IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_QUERY_HANDLING,
            IAeBPELConstants.AE_EXTENSION_PREFIX, TAG_DISABLE_SELECTION_FAILURE, aDef.isDisableSelectionFailure(), false);

      if (AeUtil.notNullOrEmpty(aDef.getAbstractProcessProfile()))
      {
         writeAbstractProcessProfileAttribute(aDef, nsContext);
      }
   }
   
   /**
    * Writes out the abstract process profile attribute.
    * @param aDef
    * @param aNsContext
    */
   protected void writeAbstractProcessProfileAttribute(AeProcessDef aDef, IAeMutableNamespaceContext aNsContext)
   {
      // do not write process profile for executable ns. 
      //setAttributeNS(aNsContext, IAeBPELConstants.WSBPEL_2_0_ABSTRACT_NAMESPACE_URI, 
      //      IAeBPELConstants.ABSTRACT_PROC_PREFIX, IAeBPELConstants.TAG_ABSTRACT_PROCESS_PROFILE, aDef.getAbstractProcessProfile());
      
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.AeImportDef)
    */
   public void visit(AeImportDef aDef)
   {
      writeStandardAttributes(aDef);
      
      setAttribute(TAG_NAMESPACE, aDef.getNamespace());
      setAttribute(TAG_LOCATION, aDef.getLocation());
      setAttribute(TAG_IMPORT_TYPE, aDef.getImportType());
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.xml.def.AeDocumentationDef)
    */
   public void visit(AeDocumentationDef aDef)
   {
      writeStandardAttributes(aDef);

      setAttribute(ATTR_DOCUMENTATION_SOURCE, aDef.getSource());
      getElement().setAttributeNS(W3C_XML_NAMESPACE, "xml:" +  ATTR_DOCUMENTATION_LANG, aDef.getLanguage()); //$NON-NLS-1$
      if (AeUtil.notNullOrEmpty( aDef.getValue() ))
      {
         Text textNode = getElement().getOwnerDocument().createTextNode(aDef.getValue());
         getElement().appendChild(textNode);
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.AePartnerLinkDef)
    */
   public void visit(AePartnerLinkDef aDef)
   {
      super.visit(aDef);

      if (aDef.getInitializePartnerRole() != null)
         setAttribute(TAG_INITIALIZE_PARTNER_ROLE, aDef.getInitializePartnerRole().booleanValue(), true);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeAssignCopyDef)
    */
   public void visit(AeAssignCopyDef aDef)
   {
      super.visit(aDef);
      
      setAttribute(TAG_KEEP_SRC_ELEMENT_NAME, aDef.isKeepSrcElementName(), false);
      setAttribute(TAG_IGNORE_MISSING_FROM_DATA, aDef.isIgnoreMissingFromData(), false);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityAssignDef)
    */
   public void visit(AeActivityAssignDef aDef)
   {
      super.visit(aDef);
      
      setAttribute(TAG_VALIDATE, aDef.isValidate(), false);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityValidateDef)
    */
   public void visit(AeActivityValidateDef aDef)
   {
      writeAttributes(aDef);

      setAttribute(TAG_VARIABLES, aDef.getVariablesAsString());
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionDef)
    */
   public void visit(AeExtensionDef aDef)
   {
      writeStandardAttributes(aDef);

      setAttribute(TAG_MUST_UNDERSTAND, aDef.isMustUnderstand(), true);
      setAttribute(TAG_NAMESPACE, aDef.getNamespace());
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef)
    */
   public void visit(AeFromPartsDef aDef)
   {
      writeStandardAttributes(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartsDef)
    */
   public void visit(AeToPartsDef aDef)
   {
      writeStandardAttributes(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartDef)
    */
   public void visit(AeFromPartDef aDef)
   {
      writeStandardAttributes(aDef);
      
      setAttribute(TAG_PART, aDef.getPart());
      setAttribute(TAG_TO_VARIABLE, aDef.getToVariable());
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartDef)
    */
   public void visit(AeToPartDef aDef)
   {
      writeStandardAttributes(aDef);

      setAttribute(TAG_PART, aDef.getPart());
      setAttribute(TAG_FROM_VARIABLE, aDef.getFromVariable());
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeSourcesDef)
    */
   public void visit(AeSourcesDef aDef)
   {
      writeStandardAttributes(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTargetsDef)
    */
   public void visit(AeTargetsDef aDef)
   {
      writeStandardAttributes(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTransitionConditionDef)
    */
   public void visit(AeTransitionConditionDef aDef)
   {
      writeStandardAttributes(aDef);
      writeExpressionDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeJoinConditionDef)
    */
   public void visit(AeJoinConditionDef aDef)
   {
      super.visit(aDef);
      
      writeStandardAttributes(aDef);
      writeExpressionDef(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForDef)
    */
   public void visit(AeForDef aDef)
   {
      super.visit(aDef);
      
      writeExpressionDef(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeUntilDef)
    */
   public void visit(AeUntilDef aDef)
   {
      super.visit(aDef);

      writeExpressionDef(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(AeChildExtensionActivityDef)
    */
   public void visit(AeChildExtensionActivityDef aDef)
   {
      writeExtensionActivities(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeConditionDef)
    */
   public void visit(AeConditionDef aDef)
   {
      writeStandardAttributes(aDef);
      
      writeExpressionDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeRepeatEveryDef)
    */
   public void visit(AeRepeatEveryDef aDef)
   {
      writeStandardAttributes(aDef);
      
      writeExpressionDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef)
    */
   public void visit(AeActivityScopeDef aDef)
   {
      super.visit(aDef);

      setAttribute(TAG_ISOLATED, aDef.isIsolated(), false);

      if (aDef.getExitOnStandardFault() != null)
      {
         setAttribute(TAG_EXIT_ON_STANDARD_FAULT, aDef.getExitOnStandardFault().booleanValue(), true);
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef aDef)
   {
      super.visit(aDef);

      setAttribute(TAG_MESSAGE_TYPE, aDef.getMessageType());
      setAttribute(TAG_ELEMENT, aDef.getElement());
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.AeCatchDef)
    */
   public void visit(AeCatchDef aDef)
   {
      super.visit(aDef);

      setAttribute(TAG_FAULT_MESSAGE_TYPE, aDef.getFaultMessageType());
      setAttribute(TAG_FAULT_ELEMENT, aDef.getFaultElementName());
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityRepeatUntilDef)
    */
   public void visit(AeActivityRepeatUntilDef aDef)
   {
      writeAttributes(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeExtensibleAssignDef)
    */
   public void visit(AeExtensibleAssignDef aDef)
   {
      writeStandardAttributes(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionsDef)
    */
   public void visit(AeExtensionsDef aDef)
   {
      writeStandardAttributes(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionActivityDef)
    */
   public void visit(AeExtensionActivityDef aDef)
   {
      writeAttributes(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityIfDef)
    */
   public void visit(AeActivityIfDef aDef)
   {
      writeAttributes(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseDef)
    */
   public void visit(AeElseDef aDef)
   {
      writeStandardAttributes(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseIfDef)
    */
   public void visit(AeElseIfDef aDef)
   {
      writeStandardAttributes(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityRethrowDef)
    */
   public void visit(AeActivityRethrowDef aDef)
   {
      writeAttributes(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.AeTerminationHandlerDef)
    */
   public void visit(AeTerminationHandlerDef aDef)
   {
      writeStandardAttributes(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef)
    */
   public void visit(AeActivityCompensateScopeDef aDef)
   {
      writeAttributes(aDef);
      setAttribute(TAG_TARGET, aDef.getTarget());
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeQueryDef)
    */
   public void visit(AeQueryDef aDef)
   {
      writeStandardAttributes(aDef);
      writeQueryDef(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityOpaqueDef)
    */
   public void visit(AeActivityOpaqueDef aDef)
   {
      writeAttributes(aDef);
   }

   /**
    * Visits a query def in order to write out the queryLanguage attribute
    * and the value of the query.
    *
    * @param aDef
    */
   protected void writeQueryDef(AeQueryDef aDef)
   {
      setAttribute(TAG_QUERY_LANGUAGE, aDef.getQueryLanguage());
      Text textNode = getElement().getOwnerDocument().createTextNode(aDef.getQuery());
      getElement().appendChild(textNode);
   }


   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#writeExpressionLang(org.activebpel.rt.bpel.def.IAeExpressionDef)
    */
   protected void writeExpressionLang(IAeExpressionDef aDef)
   {
      // No need to do anything unless an expression language is specified
      String exprLang = aDef.getExpressionLanguage();
      if (AeUtil.notNullOrEmpty(exprLang))
      {
         // Get the default expression language for the process
         AeProcessDef proc = AeDefUtil.getProcessDef((AeBaseDef)aDef);
         String defaultLang = proc.getExpressionLanguage();
         if (AeUtil.isNullOrEmpty(defaultLang))
         {
            // No language specified at process level, so need to get BPEL default from expression factory
            AeExpressionLanguageFactory exprFactory = new AeExpressionLanguageFactory();
            defaultLang = exprFactory.getBpelDefaultLanguage(IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI);
         }
         
         // If expression lang is different than default, we will need to write it out
         if (! exprLang.equals(defaultLang))
            setAttribute(TAG_EXPRESSION_LANGUAGE, exprLang);
      }
   }
   
   /**
    * Common method to write understood and not understood extension activities
    */
   private void writeExtensionActivities(AeAbstractExtensionActivityDef aDef)
   {
      writeAttributes(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#writePortTypeAttrib()
    */
   protected boolean writePortTypeAttrib()
   {
      return mWritePortTypeAttrib;
   }
   
}
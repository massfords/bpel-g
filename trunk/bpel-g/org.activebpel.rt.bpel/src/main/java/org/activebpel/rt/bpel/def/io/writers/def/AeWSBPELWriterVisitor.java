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

import javax.xml.XMLConstants;

import org.activebpel.rt.AeException;
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
   private static final Map<String,String> sPreferredPrefixes = Collections.singletonMap(IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI, "bpel"); //$NON-NLS-1$
   
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
   public void visit(AeForEachBranchesDef def)
   {
      super.visit(def);
      setAttribute(TAG_FOREACH_BRANCH_COUNTCOMPLETED, def.isCountCompletedBranchesOnly(), false);
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
   public void visit(AeProcessDef def)
   {
      super.visit(def);

      if(def.getExitOnStandardFault() != null)
      {
         setAttribute(TAG_EXIT_ON_STANDARD_FAULT, def.getExitOnStandardFault().booleanValue(), false);
      }
      
      IAeMutableNamespaceContext nsContext = new AeElementBasedNamespaceContext( getElement() );
      setAttributeNS(nsContext, IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_QUERY_HANDLING,
            IAeBPELConstants.AE_EXTENSION_PREFIX, TAG_CREATE_TARGET_XPATH, def.isCreateTargetXPath(), false);
      setAttributeNS(nsContext, IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_QUERY_HANDLING,
            IAeBPELConstants.AE_EXTENSION_PREFIX, TAG_DISABLE_SELECTION_FAILURE, def.isDisableSelectionFailure(), false);

      if (AeUtil.notNullOrEmpty(def.getAbstractProcessProfile()))
      {
         writeAbstractProcessProfileAttribute(def, nsContext);
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
      //      IAeBPELConstants.ABSTRACT_PROC_PREFIX, IAeBPELConstants.TAG_ABSTRACT_PROCESS_PROFILE, def.getAbstractProcessProfile());
      
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.AeImportDef)
    */
   public void visit(AeImportDef def)
   {
      writeStandardAttributes(def);
      
      setAttribute(TAG_NAMESPACE, def.getNamespace());
      setAttribute(TAG_LOCATION, def.getLocation());
      setAttribute(TAG_IMPORT_TYPE, def.getImportType());
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.xml.def.AeDocumentationDef)
    */
   public void visit(AeDocumentationDef aDef)
   {
      writeStandardAttributes(aDef);

      setAttribute(ATTR_DOCUMENTATION_SOURCE, aDef.getSource());
      getElement().setAttributeNS(XMLConstants.XML_NS_URI, "xml:" +  ATTR_DOCUMENTATION_LANG, aDef.getLanguage()); //$NON-NLS-1$
      if (AeUtil.notNullOrEmpty( aDef.getValue() ))
      {
         Text textNode = getElement().getOwnerDocument().createTextNode(aDef.getValue());
         getElement().appendChild(textNode);
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.AePartnerLinkDef)
    */
   public void visit(AePartnerLinkDef def)
   {
      super.visit(def);

      if (def.getInitializePartnerRole() != null)
         setAttribute(TAG_INITIALIZE_PARTNER_ROLE, def.getInitializePartnerRole().booleanValue(), true);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeAssignCopyDef)
    */
   public void visit(AeAssignCopyDef def)
   {
      super.visit(def);
      
      setAttribute(TAG_KEEP_SRC_ELEMENT_NAME, def.isKeepSrcElementName(), false);
      setAttribute(TAG_IGNORE_MISSING_FROM_DATA, def.isIgnoreMissingFromData(), false);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityAssignDef)
    */
   public void visit(AeActivityAssignDef def)
   {
      super.visit(def);
      
      setAttribute(TAG_VALIDATE, def.isValidate(), false);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityValidateDef)
    */
   public void visit(AeActivityValidateDef def)
   {
      writeAttributes(def);

      setAttribute(TAG_VARIABLES, def.getVariablesAsString());
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionDef)
    */
   public void visit(AeExtensionDef def)
   {
      writeStandardAttributes(def);

      setAttribute(TAG_MUST_UNDERSTAND, def.isMustUnderstand(), true);
      setAttribute(TAG_NAMESPACE, def.getNamespace());
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef)
    */
   public void visit(AeFromPartsDef def)
   {
      writeStandardAttributes(def);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartsDef)
    */
   public void visit(AeToPartsDef def)
   {
      writeStandardAttributes(def);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartDef)
    */
   public void visit(AeFromPartDef def)
   {
      writeStandardAttributes(def);
      
      setAttribute(TAG_PART, def.getPart());
      setAttribute(TAG_TO_VARIABLE, def.getToVariable());
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartDef)
    */
   public void visit(AeToPartDef def)
   {
      writeStandardAttributes(def);

      setAttribute(TAG_PART, def.getPart());
      setAttribute(TAG_FROM_VARIABLE, def.getFromVariable());
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeSourcesDef)
    */
   public void visit(AeSourcesDef def)
   {
      writeStandardAttributes(def);
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTargetsDef)
    */
   public void visit(AeTargetsDef def)
   {
      writeStandardAttributes(def);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTransitionConditionDef)
    */
   public void visit(AeTransitionConditionDef def)
   {
      writeStandardAttributes(def);
      writeExpressionDef(def);
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeJoinConditionDef)
    */
   public void visit(AeJoinConditionDef def)
   {
      super.visit(def);
      
      writeStandardAttributes(def);
      writeExpressionDef(def);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForDef)
    */
   public void visit(AeForDef def)
   {
      super.visit(def);
      
      writeExpressionDef(def);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeUntilDef)
    */
   public void visit(AeUntilDef def)
   {
      super.visit(def);

      writeExpressionDef(def);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(AeChildExtensionActivityDef)
    */
   public void visit(AeChildExtensionActivityDef def)
   {
      writeExtensionActivities(def);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeConditionDef)
    */
   public void visit(AeConditionDef def)
   {
      writeStandardAttributes(def);
      
      writeExpressionDef(def);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeRepeatEveryDef)
    */
   public void visit(AeRepeatEveryDef def)
   {
      writeStandardAttributes(def);
      
      writeExpressionDef(def);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef)
    */
   public void visit(AeActivityScopeDef def)
   {
      super.visit(def);

      setAttribute(TAG_ISOLATED, def.isIsolated(), false);

      if (def.getExitOnStandardFault() != null)
      {
         setAttribute(TAG_EXIT_ON_STANDARD_FAULT, def.getExitOnStandardFault().booleanValue(), true);
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef def)
   {
      super.visit(def);

      setAttribute(TAG_MESSAGE_TYPE, def.getMessageType());
      setAttribute(TAG_ELEMENT, def.getElement());
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.AeCatchDef)
    */
   public void visit(AeCatchDef def)
   {
      super.visit(def);

      setAttribute(TAG_FAULT_MESSAGE_TYPE, def.getFaultMessageType());
      setAttribute(TAG_FAULT_ELEMENT, def.getFaultElementName());
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityRepeatUntilDef)
    */
   public void visit(AeActivityRepeatUntilDef def)
   {
      writeAttributes(def);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeExtensibleAssignDef)
    */
   public void visit(AeExtensibleAssignDef def)
   {
      writeStandardAttributes(def);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionsDef)
    */
   public void visit(AeExtensionsDef def)
   {
      writeStandardAttributes(def);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionActivityDef)
    */
   public void visit(AeExtensionActivityDef def)
   {
      writeAttributes(def);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityIfDef)
    */
   public void visit(AeActivityIfDef def)
   {
      writeAttributes(def);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseDef)
    */
   public void visit(AeElseDef def)
   {
      writeStandardAttributes(def);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseIfDef)
    */
   public void visit(AeElseIfDef def)
   {
      writeStandardAttributes(def);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityRethrowDef)
    */
   public void visit(AeActivityRethrowDef def)
   {
      writeAttributes(def);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.AeTerminationHandlerDef)
    */
   public void visit(AeTerminationHandlerDef def)
   {
      writeStandardAttributes(def);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef)
    */
   public void visit(AeActivityCompensateScopeDef def)
   {
      writeAttributes(def);
      setAttribute(TAG_TARGET, def.getTarget());
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.io.writers.def.AeWriterVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeQueryDef)
    */
   public void visit(AeQueryDef def)
   {
      writeStandardAttributes(def);
      writeQueryDef(def);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityOpaqueDef)
    */
   public void visit(AeActivityOpaqueDef def)
   {
      writeAttributes(def);
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
            try {
                defaultLang = exprFactory.getBpelDefaultLanguage(IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI);
            } catch (AeException e) {
                // previous behavior of getBpelDEfaultLanguage was to not throw and return null
            }
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
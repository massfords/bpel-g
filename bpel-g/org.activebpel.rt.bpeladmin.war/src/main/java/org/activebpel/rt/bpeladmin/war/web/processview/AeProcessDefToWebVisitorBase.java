//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/processview/AeProcessDefToWebVisitorBase.java,v 1.23 2008/02/17 21:43:06 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.processview;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AeCatchAllDef;
import org.activebpel.rt.bpel.def.AeCatchDef;
import org.activebpel.rt.bpel.def.AeCompensationHandlerDef;
import org.activebpel.rt.bpel.def.AeCorrelationSetDef;
import org.activebpel.rt.bpel.def.AeCorrelationSetsDef;
import org.activebpel.rt.bpel.def.AeCorrelationsDef;
import org.activebpel.rt.bpel.def.AeEventHandlersDef;
import org.activebpel.rt.bpel.def.AeExtensionActivityDef;
import org.activebpel.rt.bpel.def.AeExtensionDef;
import org.activebpel.rt.bpel.def.AeExtensionsDef;
import org.activebpel.rt.bpel.def.AeFaultHandlersDef;
import org.activebpel.rt.bpel.def.AeImportDef;
import org.activebpel.rt.bpel.def.AeMessageExchangeDef;
import org.activebpel.rt.bpel.def.AeMessageExchangesDef;
import org.activebpel.rt.bpel.def.AePartnerDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AePartnerLinksDef;
import org.activebpel.rt.bpel.def.AePartnersDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.AeScopeDef;
import org.activebpel.rt.bpel.def.AeTerminationHandlerDef;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.AeVariablesDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.activity.AeActivityAssignDef;
import org.activebpel.rt.bpel.def.activity.AeActivityBreakDef;
import org.activebpel.rt.bpel.def.activity.AeActivityCompensateDef;
import org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityContinueDef;
import org.activebpel.rt.bpel.def.activity.AeActivityEmptyDef;
import org.activebpel.rt.bpel.def.activity.AeActivityExitDef;
import org.activebpel.rt.bpel.def.activity.AeActivityFlowDef;
import org.activebpel.rt.bpel.def.activity.AeActivityForEachDef;
import org.activebpel.rt.bpel.def.activity.AeActivityIfDef;
import org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityOpaqueDef;
import org.activebpel.rt.bpel.def.activity.AeActivityPickDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef;
import org.activebpel.rt.bpel.def.activity.AeActivityRepeatUntilDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReplyDef;
import org.activebpel.rt.bpel.def.activity.AeActivityRethrowDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivitySequenceDef;
import org.activebpel.rt.bpel.def.activity.AeActivitySuspendDef;
import org.activebpel.rt.bpel.def.activity.AeActivityThrowDef;
import org.activebpel.rt.bpel.def.activity.AeActivityValidateDef;
import org.activebpel.rt.bpel.def.activity.AeActivityWaitDef;
import org.activebpel.rt.bpel.def.activity.AeActivityWhileDef;
import org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef;
import org.activebpel.rt.bpel.def.activity.support.AeAssignCopyDef;
import org.activebpel.rt.bpel.def.activity.support.AeConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeExtensibleAssignDef;
import org.activebpel.rt.bpel.def.activity.support.AeForDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachBranchesDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachCompletionConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachFinalDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachStartDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromPartDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef;
import org.activebpel.rt.bpel.def.activity.support.AeIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeJoinConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeLinkDef;
import org.activebpel.rt.bpel.def.activity.support.AeLinksDef;
import org.activebpel.rt.bpel.def.activity.support.AeLiteralDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef;
import org.activebpel.rt.bpel.def.activity.support.AeQueryDef;
import org.activebpel.rt.bpel.def.activity.support.AeRepeatEveryDef;
import org.activebpel.rt.bpel.def.activity.support.AeSourceDef;
import org.activebpel.rt.bpel.def.activity.support.AeSourcesDef;
import org.activebpel.rt.bpel.def.activity.support.AeTargetDef;
import org.activebpel.rt.bpel.def.activity.support.AeTargetsDef;
import org.activebpel.rt.bpel.def.activity.support.AeToDef;
import org.activebpel.rt.bpel.def.activity.support.AeToPartDef;
import org.activebpel.rt.bpel.def.activity.support.AeToPartsDef;
import org.activebpel.rt.bpel.def.activity.support.AeTransitionConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeUntilDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;
import org.activebpel.rt.bpel.impl.IAeImplStateNames;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.AeDocumentationDef;
import org.activebpel.rt.xml.def.AeExtensionAttributeDef;
import org.activebpel.rt.xml.def.AeExtensionElementDef;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Base visitor responsible for building the web visual model or definition properties.
 * The <code>visit</code> methods in this class are empty. Subclasses should override
 * these methods when needed.
 */
public class AeProcessDefToWebVisitorBase implements IAeDefVisitor
{
   /** The optional xml state document of the active process. */
   private Document mStateDoc = null;
	/** Map containing bpel state document's element node name and a
    * collection of location path to elements. */
   private Map stateElementMap;

   /** Indicates the process is based on ws-bpel 1.1 */
   private boolean mBpelVersion11;

   /**
    * Default ctor.
    */
   public AeProcessDefToWebVisitorBase()
   {
      stateElementMap = new HashMap();
   }

   /**
    * @return Returns the bpelVersion11.
    */
   protected boolean isBpelVersion11()
   {
      return mBpelVersion11;
   }

   /**
    * @param aBpelVersion11 The bpelVersion11 to set.
    */
   protected void setBpelVersion11(boolean aBpelVersion11)
   {
      mBpelVersion11 = aBpelVersion11;
   }

   /**
    * Flags the process as a BPWS 1.1 flag based on the proces def.
    * @param aDef
    */
   protected void setBpelVersion11(AeBaseXmlDef aDef)
   {
      AeProcessDef processDef = AeDefUtil.getProcessDef(aDef);
      setBpelVersion11( processDef != null && IAeBPELConstants.BPWS_NAMESPACE_URI.equals(processDef.getNamespace()) );
   }

   /**
    * @return Returns the stateElementMap.
    */
   protected Map getStateElementMap()
   {
      return stateElementMap;
   }

   /**
    * Builds an internal map of maps containing bpelObject and link state elements keyed by its location path.
    */
   protected void buildStateElementMap()
   {
      if (getStateDocument() != null)
      {
         // build list of bpelObject elements
         addElementList(IAeImplStateNames.STATE_ACTY);
         // map of link element state information.
         addElementList(IAeImplStateNames.STATE_LINK);
      }
   }

   /**
    * Builds an internal map of maps containing the named elements keyed by its location path.
    * @param aElementName state document element tag name.
    */
   protected void addElementList(String aElementName)
   {
      try
      {
         List eleList = selectElements("//" + aElementName); //$NON-NLS-1$
         if (eleList.size() > 0)
         {
            addElementList(eleList, aElementName);
         }
      }
      catch(Exception e)
      {
         AeException.logError(e, e.getLocalizedMessage());
      }
   }

   /**
    * Adds the list of elements to the state element map.
    * @param aElementList
    * @param aElementName
    */
   protected void addElementList(List aElementList, String aElementName)
   {
      Map map = new HashMap();
      getStateElementMap().put(aElementName, map);
      Iterator it= aElementList.iterator();
      while (it.hasNext())
      {
         Element ele = (Element) it.next();
         map.put(ele.getAttribute(IAeImplStateNames.STATE_LOC), ele);
      }
   }

   /**
    * Returns the state element object given its location path and element tag name.
    * @param aLocationPath
    * @param aElementName
    */
   protected Element getElement(String aLocationPath, String aElementName)
   {
      Element rEle = null;
      Map map = (Map) getStateElementMap().get(aElementName);
      if (map != null)
      {
         rEle = (Element) map.get(aLocationPath);
      }
      return rEle;
   }

   /**
    * Sets the state document.
    * @param aStateDoc
    */
   protected void setStateDocument(Document aStateDoc)
   {
      mStateDoc = aStateDoc;
      buildStateElementMap();
   }

   /**
    * @return The xml document containing the current state information.
    */
   public Document getStateDocument()
   {
      return mStateDoc;
   }

   /**
    * Returns the local part name given the QName or returns empty string if the QName is null.
    * @param aQName
    * @return local part or empty string.
    */
   protected String getLocalName(QName aQName)
   {
      // return QName's localpart if QName is not null. (in some cases e.g FaultHandlerDef's QName maybe null).
      String name = ""; //$NON-NLS-1$
      if (aQName != null)
      {
         name = aQName.getLocalPart();
      }
      return name;
   }


   /**
    * Returns a prefixed name given the QName and the reference def.
    * @param aRefDef
    * @param aQName
    * @return prefixed name.
    */
   protected String getPrefixedName(AeBaseDef aRefDef, QName aQName)
   {
      if (aQName != null)
      {
         String name = aQName.getLocalPart();
         Iterator it = aRefDef.findPrefixesForNamespace(aQName.getNamespaceURI()).iterator();
         if (it.hasNext())
         {
            name = it.next() + ":" + name;  //$NON-NLS-1$
         }
         return name;
      }
      else
      {
         return "";  //$NON-NLS-1$
      }
   }

   /**
    * Selects the corresponding 'bpelObject' element in the state document given the BPEL definition. The
    * BPEL definition's location path is used to query the state document.
    * @param aDef bpel definition which has the location path.
    * @return element in the state document or not if not found.
    * @throws AeException
    */
   protected Element selectSingleElement(AeBaseXmlDef aDef) throws AeException
   {
      return selectSingleElement(aDef, IAeImplStateNames.STATE_ACTY);
   }

   /**
    * Selects the corresponding named element in the state document given the BPEL definition. The
    * BPEL definition's location path is used to query the state document.
    * @param aDef bpel definition which has the location path.
    * @param aStateTagName state element tag name.
    * @return element in the state document or not if not found.
    * @throws AeException
    */
   protected Element selectSingleElement(AeBaseXmlDef aDef, String aStateTagName) throws AeException
   {
      Element ele = getElement(aDef.getLocationPath(), aStateTagName);
      if (ele == null && !IAeImplStateNames.STATE_ACTY.equals(aStateTagName) && !IAeImplStateNames.STATE_LINK.equals(aStateTagName))
      {
         // if the element was not found in the in memory cache, the locate it via xpath query.
         ele = selectSingleElement(aDef.getLocationPath(), aStateTagName);
      }
      return ele;
   }

   /**
    * Selects the corresponding named element in the state document given the location path.
    * @param aLocationPath the location path.
    * @param aStateTagName state element tag name.
    * @return element in the state document or not if not found.
    * @throws AeException
    */
   protected Element selectSingleElement(String aLocationPath, String aStateTagName) throws AeException
   {
      String expression = "//" + aStateTagName + "[@" + IAeImplStateNames.STATE_LOC + "=\"" + aLocationPath + "\"]";  //$NON-NLS-1$ //$NON-NLS-2$  //$NON-NLS-3$ //$NON-NLS-4$
      return selectElement(expression);
   }

   /**
    * Selects and returns the attribute value of a element in the state document given the element's
    * tag name, attribute name and the locationPath value.
    * @param aLocationPath location path value of the element.
    * @param aStateTagName element tag name.
    * @param aAttributeName element attribute name.
    * @return attribute value or empty string if not found.
    */
   protected String selectSingleAttribute(String aLocationPath, String aStateTagName, String aAttributeName)
   {
      String rVal = ""; //$NON-NLS-1$
      try
      {
         Element ele = selectSingleElement(aLocationPath, aStateTagName);
         if (ele != null)
         {
            rVal = ele.getAttribute(aAttributeName);
         }
      }
      catch(Exception e)
      {
         // ignore
      }
      return rVal;
   }

   /**
    * Selects and returns the attribute value of a element in the state document given the element's
    * tag name, attribute name and the locationPath value.
    * @param aLocationPath location path value of the element.
    * @param aStateTagName element tag name.
    * @param aAttributeNames array of element attribute names.
    * @return attribute values.
    */
   protected String[] selectAttributes(String aLocationPath, String aStateTagName, String aAttributeNames[])
   {
      String rVal[] = new String[aAttributeNames.length];
      // assign default value
      for (int i = 0; i < rVal.length; i++)
      {
         rVal[i] = ""; //$NON-NLS-1$
      }
      try
      {
         Element ele = selectSingleElement(aLocationPath, aStateTagName);
         if (ele != null)
         {
            for (int i = 0; i < aAttributeNames.length; i++)
            {
               rVal[i] = ele.getAttribute(aAttributeNames[i]);
            }
         }
      }
      catch(Exception e)
      {
         // ignore
      }
      return rVal;
   }

   /**
    * Convenience method to return a list of elements given the xpath expression.
    * @param aXpathExpression
    * @throws AeException
    */
   protected List selectElements(String aXpathExpression) throws AeException
   {
      try
      {
         XPath xPath = new DOMXPath(aXpathExpression);
         return (List) xPath.selectNodes(getStateDocument());
      }
      catch(JaxenException je)
      {
         throw new AeException(je);
      }
   }

   /**
    * Convenience method to return a single element given the xpath expression .
    * @param aXpathExpression
    * @throws AeException
    */
   protected Element selectElement(String aXpathExpression) throws AeException
   {
      return selectElement(aXpathExpression, getStateDocument());
   }

   /**
    * Convenience method to return a single element given the xpath expression and the xpath context.
    * @param aXpathExpression
    * @param aContext xpath context.
    * @throws AeException
    */
   protected Element selectElement(String aXpathExpression, Object aContext) throws AeException
   {
      try
      {
         XPath xPath = new DOMXPath(aXpathExpression);
         return (Element) xPath.selectSingleNode(aContext);
      }
      catch(JaxenException je)
      {
         throw new AeException(je);
      }
   }


   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
    */
   public void visit(AeProcessDef aDef)
   {
      setBpelVersion11(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityAssignDef)
    */
   public void visit(AeActivityAssignDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateDef)
    */
   public void visit(AeActivityCompensateDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef)
    */
   public void visit(AeActivityCompensateScopeDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityEmptyDef)
    */
   public void visit(AeActivityEmptyDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityFlowDef)
    */
   public void visit(AeActivityFlowDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef)
    */
   public void visit(AeActivityInvokeDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityPickDef)
    */
   public void visit(AeActivityPickDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef)
    */
   public void visit(AeActivityReceiveDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReplyDef)
    */
   public void visit(AeActivityReplyDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivitySuspendDef)
    */
   public void visit(AeActivitySuspendDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef)
    */
   public void visit(AeActivityScopeDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityContinueDef)
    */
   public void visit(AeActivityContinueDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityBreakDef)
    */
   public void visit(AeActivityBreakDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationSetDef)
    */
   public void visit(AeCorrelationSetDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCatchDef)
    */
   public void visit(AeCatchDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCatchAllDef)
    */
   public void visit(AeCatchAllDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeVariableDef)
    */
   public void visit(AeVariableDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeVariablesDef)
    */
   public void visit(AeVariablesDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeEventHandlersDef)
    */
   public void visit(AeEventHandlersDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCompensationHandlerDef)
    */
   public void visit(AeCompensationHandlerDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationSetsDef)
    */
   public void visit(AeCorrelationSetsDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeFaultHandlersDef)
    */
   public void visit(AeFaultHandlersDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef)
    */
   public void visit(AeOnMessageDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef)
    */
   public void visit(AeOnAlarmDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivitySequenceDef)
    */
   public void visit(AeActivitySequenceDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityExitDef)
    */
   public void visit(AeActivityExitDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityThrowDef)
    */
   public void visit(AeActivityThrowDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWaitDef)
    */
   public void visit(AeActivityWaitDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWhileDef)
    */
   public void visit(AeActivityWhileDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityRepeatUntilDef)
    */
   public void visit(AeActivityRepeatUntilDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityForEachDef)
    */
   public void visit(AeActivityForEachDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachFinalDef)
    */
   public void visit(AeForEachFinalDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachStartDef)
    */
   public void visit(AeForEachStartDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachBranchesDef)
    */
   public void visit(AeForEachBranchesDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachCompletionConditionDef)
    */
   public void visit(AeForEachCompletionConditionDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerDef)
    */
   public void visit(AePartnerDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerLinkDef)
    */
   public void visit(AePartnerLinkDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeScopeDef)
    */
   public void visit(AeScopeDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeMessageExchangesDef)
    */
   public void visit(AeMessageExchangesDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeMessageExchangeDef)
    */
   public void visit(AeMessageExchangeDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeAssignCopyDef)
    */
   public void visit(AeAssignCopyDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef)
    */
   public void visit(AeCorrelationDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLinkDef)
    */
   public void visit(AeLinkDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeSourcesDef)
    */
   public void visit(AeSourcesDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeSourceDef)
    */
   public void visit(AeSourceDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTargetsDef)
    */
   public void visit(AeTargetsDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTargetDef)
    */
   public void visit(AeTargetDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerLinksDef)
    */
   public void visit(AePartnerLinksDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnersDef)
    */
   public void visit(AePartnersDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLinksDef)
    */
   public void visit(AeLinksDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationsDef)
    */
   public void visit(AeCorrelationsDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromDef)
    */
   public void visit(AeFromDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToDef)
    */
   public void visit(AeToDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeImportDef)
    */
   public void visit(AeImportDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.xml.def.AeDocumentationDef)
    */
   public void visit(AeDocumentationDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityValidateDef)
    */
   public void visit(AeActivityValidateDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeExtensibleAssignDef)
    */
   public void visit(AeExtensibleAssignDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionDef)
    */
   public void visit(AeExtensionDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionsDef)
    */
   public void visit(AeExtensionsDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef)
    */
   public void visit(AeFromPartsDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartsDef)
    */
   public void visit(AeToPartsDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartDef)
    */
   public void visit(AeFromPartDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartDef)
    */
   public void visit(AeToPartDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeJoinConditionDef)
    */
   public void visit(AeJoinConditionDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTransitionConditionDef)
    */
   public void visit(AeTransitionConditionDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForDef)
    */
   public void visit(AeForDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeUntilDef)
    */
   public void visit(AeUntilDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(AeChildExtensionActivityDef)
    */
   public void visit(AeChildExtensionActivityDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionActivityDef)
    */
   public void visit(AeExtensionActivityDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityIfDef)
    */
   public void visit(AeActivityIfDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeConditionDef)
    */
   public void visit(AeConditionDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseDef)
    */
   public void visit(AeElseDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseIfDef)
    */
   public void visit(AeElseIfDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeIfDef)
    */
   public void visit(AeIfDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityRethrowDef)
    */
   public void visit(AeActivityRethrowDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeRepeatEveryDef)
    */
   public void visit(AeRepeatEveryDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeTerminationHandlerDef)
    */
   public void visit(AeTerminationHandlerDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLiteralDef)
    */
   public void visit(AeLiteralDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeQueryDef)
    */
   public void visit(AeQueryDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityOpaqueDef)
    */
   public void visit(AeActivityOpaqueDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.xml.def.AeExtensionAttributeDef)
    */
   public void visit(AeExtensionAttributeDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.xml.def.AeExtensionElementDef)
    */
   public void visit(AeExtensionElementDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor#visit(org.activebpel.rt.xml.def.AeBaseXmlDef)
    */
   public void visit(AeBaseXmlDef aDef)
   {
   }
}

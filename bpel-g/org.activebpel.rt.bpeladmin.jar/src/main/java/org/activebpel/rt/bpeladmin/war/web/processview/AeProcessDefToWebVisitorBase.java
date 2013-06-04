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

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.*;
import org.activebpel.rt.bpel.def.activity.*;
import org.activebpel.rt.bpel.def.activity.support.*;
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

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
   private final Map<String, Map<String, Element>> stateElementMap;

   /** Indicates the process is based on ws-bpel 1.1 */
   private boolean mBpelVersion11;

   /**
    * Default ctor.
    */
   public AeProcessDefToWebVisitorBase()
   {
      stateElementMap = new HashMap<>();
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
   protected Map<String, Map<String, Element>> getStateElementMap()
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
         List<Element> eleList = selectElements("//" + aElementName); //$NON-NLS-1$
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
   protected void addElementList(List<Element> aElementList, String aElementName)
   {
      Map<String, Element> map = new HashMap<>();
      getStateElementMap().put(aElementName, map);
      Iterator<Element> it= aElementList.iterator();
      while (it.hasNext())
      {
         Element ele = it.next();
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
      Map map = getStateElementMap().get(aElementName);
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
   @SuppressWarnings("unchecked")
   protected List<Element> selectElements(String aXpathExpression) throws AeException
   {
      try
      {
         XPath xPath = new DOMXPath(aXpathExpression);
         return xPath.selectNodes(getStateDocument());
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
   public void visit(AeProcessDef def)
   {
      setBpelVersion11(def);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityAssignDef)
    */
   public void visit(AeActivityAssignDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateDef)
    */
   public void visit(AeActivityCompensateDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef)
    */
   public void visit(AeActivityCompensateScopeDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityEmptyDef)
    */
   public void visit(AeActivityEmptyDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityFlowDef)
    */
   public void visit(AeActivityFlowDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef)
    */
   public void visit(AeActivityInvokeDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityPickDef)
    */
   public void visit(AeActivityPickDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef)
    */
   public void visit(AeActivityReceiveDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReplyDef)
    */
   public void visit(AeActivityReplyDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivitySuspendDef)
    */
   public void visit(AeActivitySuspendDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef)
    */
   public void visit(AeActivityScopeDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityContinueDef)
    */
   public void visit(AeActivityContinueDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityBreakDef)
    */
   public void visit(AeActivityBreakDef def)
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
   public void visit(AeCatchDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCatchAllDef)
    */
   public void visit(AeCatchAllDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeVariableDef)
    */
   public void visit(AeVariableDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeVariablesDef)
    */
   public void visit(AeVariablesDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeEventHandlersDef)
    */
   public void visit(AeEventHandlersDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCompensationHandlerDef)
    */
   public void visit(AeCompensationHandlerDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationSetsDef)
    */
   public void visit(AeCorrelationSetsDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeFaultHandlersDef)
    */
   public void visit(AeFaultHandlersDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef)
    */
   public void visit(AeOnMessageDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef)
    */
   public void visit(AeOnAlarmDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivitySequenceDef)
    */
   public void visit(AeActivitySequenceDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityExitDef)
    */
   public void visit(AeActivityExitDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityThrowDef)
    */
   public void visit(AeActivityThrowDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWaitDef)
    */
   public void visit(AeActivityWaitDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWhileDef)
    */
   public void visit(AeActivityWhileDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityRepeatUntilDef)
    */
   public void visit(AeActivityRepeatUntilDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityForEachDef)
    */
   public void visit(AeActivityForEachDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachFinalDef)
    */
   public void visit(AeForEachFinalDef def)
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
   public void visit(AeForEachBranchesDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachCompletionConditionDef)
    */
   public void visit(AeForEachCompletionConditionDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerDef)
    */
   public void visit(AePartnerDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerLinkDef)
    */
   public void visit(AePartnerLinkDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeScopeDef)
    */
   public void visit(AeScopeDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeMessageExchangesDef)
    */
   public void visit(AeMessageExchangesDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeMessageExchangeDef)
    */
   public void visit(AeMessageExchangeDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeAssignCopyDef)
    */
   public void visit(AeAssignCopyDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef)
    */
   public void visit(AeCorrelationDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLinkDef)
    */
   public void visit(AeLinkDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeSourcesDef)
    */
   public void visit(AeSourcesDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeSourceDef)
    */
   public void visit(AeSourceDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTargetsDef)
    */
   public void visit(AeTargetsDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTargetDef)
    */
   public void visit(AeTargetDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerLinksDef)
    */
   public void visit(AePartnerLinksDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnersDef)
    */
   public void visit(AePartnersDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLinksDef)
    */
   public void visit(AeLinksDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationsDef)
    */
   public void visit(AeCorrelationsDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromDef)
    */
   public void visit(AeFromDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToDef)
    */
   public void visit(AeToDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeImportDef)
    */
   public void visit(AeImportDef def)
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
   public void visit(AeActivityValidateDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeExtensibleAssignDef)
    */
   public void visit(AeExtensibleAssignDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionDef)
    */
   public void visit(AeExtensionDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionsDef)
    */
   public void visit(AeExtensionsDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef)
    */
   public void visit(AeFromPartsDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartsDef)
    */
   public void visit(AeToPartsDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartDef)
    */
   public void visit(AeFromPartDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartDef)
    */
   public void visit(AeToPartDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeJoinConditionDef)
    */
   public void visit(AeJoinConditionDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTransitionConditionDef)
    */
   public void visit(AeTransitionConditionDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForDef)
    */
   public void visit(AeForDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeUntilDef)
    */
   public void visit(AeUntilDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(AeChildExtensionActivityDef)
    */
   public void visit(AeChildExtensionActivityDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionActivityDef)
    */
   public void visit(AeExtensionActivityDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityIfDef)
    */
   public void visit(AeActivityIfDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeConditionDef)
    */
   public void visit(AeConditionDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseDef)
    */
   public void visit(AeElseDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseIfDef)
    */
   public void visit(AeElseIfDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeIfDef)
    */
   public void visit(AeIfDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityRethrowDef)
    */
   public void visit(AeActivityRethrowDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeRepeatEveryDef)
    */
   public void visit(AeRepeatEveryDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeTerminationHandlerDef)
    */
   public void visit(AeTerminationHandlerDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLiteralDef)
    */
   public void visit(AeLiteralDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeQueryDef)
    */
   public void visit(AeQueryDef def)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityOpaqueDef)
    */
   public void visit(AeActivityOpaqueDef def)
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

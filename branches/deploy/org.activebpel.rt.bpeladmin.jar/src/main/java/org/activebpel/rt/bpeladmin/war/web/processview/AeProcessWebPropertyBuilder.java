//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/processview/AeProcessWebPropertyBuilder.java,v 1.40 2008/03/11 21:44:46 vvelusamy Exp $
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
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.AeActivityPartnerLinkBaseDef;
import org.activebpel.rt.bpel.def.AeBaseContainer;
import org.activebpel.rt.bpel.def.AeCatchDef;
import org.activebpel.rt.bpel.def.AeCorrelationSetDef;
import org.activebpel.rt.bpel.def.AeCorrelationSetsDef;
import org.activebpel.rt.bpel.def.AeCorrelationsDef;
import org.activebpel.rt.bpel.def.AeMessageExchangeDef;
import org.activebpel.rt.bpel.def.AeMessageExchangesDef;
import org.activebpel.rt.bpel.def.AePartnerDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AePartnerLinksDef;
import org.activebpel.rt.bpel.def.AePartnersDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.AeVariablesDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.IAeExpressionDef;
import org.activebpel.rt.bpel.def.activity.AeActivityAssignDef;
import org.activebpel.rt.bpel.def.activity.AeActivityBreakDef;
import org.activebpel.rt.bpel.def.activity.AeActivityCompensateDef;
import org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityContinueDef;
import org.activebpel.rt.bpel.def.activity.AeActivityExitDef;
import org.activebpel.rt.bpel.def.activity.AeActivityForEachDef;
import org.activebpel.rt.bpel.def.activity.AeActivityIfDef;
import org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityPickDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef;
import org.activebpel.rt.bpel.def.activity.AeActivityRepeatUntilDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReplyDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivitySequenceDef;
import org.activebpel.rt.bpel.def.activity.AeActivitySuspendDef;
import org.activebpel.rt.bpel.def.activity.AeActivityThrowDef;
import org.activebpel.rt.bpel.def.activity.AeActivityValidateDef;
import org.activebpel.rt.bpel.def.activity.AeActivityWaitDef;
import org.activebpel.rt.bpel.def.activity.AeActivityWhileDef;
import org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef;
import org.activebpel.rt.bpel.def.activity.support.AeAssignCopyDef;
import org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromPartDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef;
import org.activebpel.rt.bpel.def.activity.support.AeIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeLinkDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef;
import org.activebpel.rt.bpel.def.activity.support.AeToDef;
import org.activebpel.rt.bpel.def.activity.support.AeToPartDef;
import org.activebpel.rt.bpel.def.activity.support.AeToPartsDef;
import org.activebpel.rt.bpel.def.activity.support.AeVarDef;
import org.activebpel.rt.bpel.def.activity.support.IAeSpecExtension;
import org.activebpel.rt.bpel.def.io.AeCorrelationPatternIOFactory;
import org.activebpel.rt.bpel.def.io.IAeCorrelationPatternIO;
import org.activebpel.rt.bpel.def.io.readers.def.IAeFromStrategyKeys;
import org.activebpel.rt.bpel.def.io.readers.def.IAeToStrategyKeys;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.bpel.impl.IAeImplStateNames;
import org.activebpel.rt.bpeladmin.war.AeMessages;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.AeExtensionElementDef;
import org.activebpel.rt.xml.def.AeXmlDefUtil;
import org.activebpel.rt.xml.def.graph.IAeXmlDefGraphNodeProperty;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Base class which builds the properties for the selected BPEL activity in the admin console.
 */
public abstract class AeProcessWebPropertyBuilder extends AeProcessDefToWebVisitorBase
{
   /** Map containing non-activity container element name to display name. */
   protected static final Map NON_ACTIVITY_CONTAINERS = new HashMap();

   static
   {
      NON_ACTIVITY_CONTAINERS.put("partnerLinks", "partnerLink"); //$NON-NLS-1$ //$NON-NLS-2$
      NON_ACTIVITY_CONTAINERS.put("partners", "partner"); //$NON-NLS-1$ //$NON-NLS-2$
      NON_ACTIVITY_CONTAINERS.put("variables", "variable"); //$NON-NLS-1$ //$NON-NLS-2$
      NON_ACTIVITY_CONTAINERS.put("correlationSets", "correlationSet"); //$NON-NLS-1$ //$NON-NLS-2$
      NON_ACTIVITY_CONTAINERS.put("correlations", "correlations"); //$NON-NLS-1$ //$NON-NLS-2$
      NON_ACTIVITY_CONTAINERS.put("fromParts", "fromPart"); //$NON-NLS-1$ //$NON-NLS-2$
      NON_ACTIVITY_CONTAINERS.put("toParts", "toPart"); //$NON-NLS-1$ //$NON-NLS-2$
      NON_ACTIVITY_CONTAINERS.put("messageExchanges", "messageExchange"); //$NON-NLS-1$ //$NON-NLS-2$
   }

   /** Currently selected BPEL web model. */
   private AeBpelObjectBase mBpelModel;
   /** Web process model. */
   private AeBpelProcessObject mBpelProcessModel;

   /**
    * ctor.
    * @param aBpelModel selected BPEL web model.
    */
   public AeProcessWebPropertyBuilder(AeBpelObjectBase aBpelModel)
   {
      setBpelModel(aBpelModel);
   }

   /**
    * Overrides method to return the state document from the BPEL process web model.
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebVisitorBase#getStateDocument()
    */
   public Document getStateDocument()
   {
      if (getBpelProcessModel().isHasState())
      {
         return getBpelProcessModel().getStateDoc();
      }
      else
      {
         return null;
      }
   }

   /**
    * Adds a name value detailed property.
    * @param aName
    * @param aValue
    * @param aEditFlag
    */
   protected void addDetails(String aName, String aValue,  boolean aEditFlag)
   {
      AePropertyNameValue pd = new AePropertyNameValue(aName, aValue, aEditFlag);
      addDetails(pd);
   }

   /**
    * Adds the given name value property. Subclasses should implement this method to display this property.
    * @param aDetails
    */
   protected abstract void addDetails(AePropertyNameValue aDetails);

   /**
    * Removes the named property.
    * @param aName property name.
    */
   protected abstract void removeProperty(String aName);

   /**
    * Adds a simple property.
    * @param aName
    * @param aValue
    */
   protected void addProperty(String aName, String aValue)
   {
      addProperty(aName, aName, aValue, false, null, false);
   }

   /**
    * Adds a list of simple properties.
    * @param aNames
    * @param aValues
    * @param aIsDate
    */
   protected void addProperties(String aNames[], String aValues[], boolean aIsDate)
   {
      for (int i = 0; i < aNames.length; i++)
      {
         addProperty(aNames[i], aValues[i], aIsDate, null, false);
      }
   }

   /**
    * Adds a simple property.
    * @param aName
    * @param aValue
    * @param aLocationPath
    */
   protected void addProperty(String aName, String aValue, String aLocationPath)
   {
      addProperty(aName, aName, aValue, aLocationPath);
   }

   /**
    * Adds the property with the key
    * @param aKey
    * @param aName
    * @param aValue
    * @param aLocationPath
    */
   protected void addProperty(String aKey, String aName, String aValue, String aLocationPath)
   {
      addProperty(aKey, aName, aValue, false, aLocationPath, false);
   }


   /**
    * Adds a simple property.
    * @param aName property name
    * @param aValue property value
    * @param aIsDate indicates that the property is a date.
    * @param aLocationPath location path of the activity
    * @param aEditFlag indicates that the property is editiable.
    */
   protected void addProperty(String aName, String aValue, boolean aIsDate, String aLocationPath, boolean aEditFlag)
   {
      addProperty(aName, aName, aValue, aIsDate, aLocationPath, aEditFlag);
   }

   /**
    * Adds a simple property.
    * @param aKey property key
    * @param aName property name
    * @param aValue property value
    * @param aIsDate indicates that the property is a date.
    * @param aLocationPath location path of the activity
    * @param aEditFlag indicates that the property is editiable.
    */
   protected void addProperty(String aKey, String aName, String aValue, boolean aIsDate, String aLocationPath, boolean aEditFlag)
   {
      if (AeUtil.notNullOrEmpty(aValue))
      {
         AePropertyNameValue nv = new AePropertyNameValue(aKey, aName, aValue, aIsDate, aLocationPath, aEditFlag);
         addProperty(nv);
      }
   }

   /**
    * Adds a simple property.
    * @param aNameValue
    */
   protected abstract void addProperty(AePropertyNameValue aNameValue);

   /**
    * Returns the string literal 'yes' or 'no' based on the boolean value.
    * @param aValue
    */
   protected String getYesNo(boolean aValue)
   {
      return aValue ? "yes" : "no"; //$NON-NLS-1$ //$NON-NLS-2$
   }

   /**
    * Returns the location path of the current model.
    */
   public String getLocationPath()
   {
      return getBpelModel().getLocationPath();
   }

   /**
    * @return Returns the bpelProcessModel.
    */
   public AeBpelProcessObject getBpelProcessModel()
   {
      if (mBpelProcessModel == null)
      {
         mBpelProcessModel = getBpelModel().getRootProcess();
      }
      return mBpelProcessModel;
   }

   /**
    * @return Returns the bpelModel.
    */
   public AeBpelObjectBase getBpelModel()
   {
      return mBpelModel;
   }

   /**
    * @param aBpelModel The bpelModel to set.
    */
   public void setBpelModel(AeBpelObjectBase aBpelModel)
   {
      mBpelModel = aBpelModel;
   }

   /**
    * @return Returns the hasState.
    */
   public boolean isHasState()
   {
      return getBpelProcessModel().isHasState();
   }

   /**
    * Returns the location path to a variable given its name.
    * @param aVariableName name of variable.
    * @return location path of variable if found, or null otherwise.
    */
   protected String findVariableLocationPath(String aVariableName)
   {
      AeBpelObjectBase bpelObj = getBpelModel();
      AeBpelObjectBase bpelVariable = bpelObj.findVariable(aVariableName);
      if (bpelVariable != null)
      {
         return bpelVariable.getLocationPath();
      }
      else
      {
         return null;
      }
   }

   /**
    * Returns the location path of a correlation set given the set name.
    * @param aSetName
    * @return location path of set or null if not found.
    */
   protected String findCorrelationSetLocationPath(String aSetName)
   {
      String rVal = null;
      AeBpelObjectBase corrSet = getBpelModel().findCorrelationSet(aSetName);
      if (corrSet != null)
      {
         rVal = corrSet.getLocationPath();
      }
      return rVal;
   }


   /**
    * Returns the location path to a partner link given its  name.
    * @param aPartnerLinkName name of partner link.
    * @return location path of partner link if found, or null otherwise.
    */
   protected String findPartnerLinkLocationPath(String aPartnerLinkName)
   {
      AeBpelObjectBase bpelObj = getBpelModel();
      AeBpelObjectBase partnerLink = bpelObj.findPartnerLink(aPartnerLinkName);
      if (partnerLink != null)
      {
         return partnerLink.getLocationPath();
      }
      else
      {
         return null;
      }
   }

   /**
    * Builds the properties associated with the current model.
    */
   public void build()
   {
      // build common properties such as name and state.
      buildCommonProperties();
      
      // get Adapter from the model and if there is an adapter ask it for the properties
      if (getBpelModel().getAdapter() != null)
      {
         buildPropertiesFromAdapter();
      }
      else
      {
         // get the definition for the current web model and let it visit this class.
         AeBaseXmlDef def = getBpelModel().getDef();
         if (def != null)
         {
            try
            {
               setBpelVersion11(def);
               def.accept(this);
            }
            catch(Throwable t)
            {
               AeException.logError(t, t.getMessage());
            }
         }
      }
   }

   /**
    * This method delegates to adapter  
    */
   protected void buildPropertiesFromAdapter()
   {
      IAeXmlDefGraphNodeProperty[] props =  getBpelModel().getAdapter().getProperties(getBpelModel().getDef(), getStateDocument(), getBpelModel().getLocationPath());
      if (props == null)
         return;
      
      for(int i=0; i<props.length; i++)
      {
         if (props[i].isRemove())
         {
            removeProperty(props[i].getName());
         }
         else
         {
            AePropertyNameValue nv = new AePropertyNameValue(props[i].getName(), props[i].getValue(), false);
            // Set location path if this is a variable
            if (props[i].isVariable())
               nv.setLocationPath(findVariableLocationPath(props[i].getValue()));
            
            // Set location path if present in node property
            if (props[i].isHasLocationPath())
               nv.setLocationPath(props[i].getLocationPath());
            
            if (props[i].isDetail())
               addDetails(nv);
            else
               addProperty(nv);
         }
      }
   }
   
   /**
    * Builds the common properties such as name and state.
    */
   protected void buildCommonProperties()
   {
      String name = AeUtil.notNullOrEmpty(getBpelModel().getName())? getBpelModel().getName() : AeMessages.getString("AeProcessViewPropertyBuilder.0"); //$NON-NLS-1$
      // activity name
      AePropertyNameValue nv = new AePropertyNameValue("name", name, null); //$NON-NLS-1$
      addProperty(nv);
      // location path
      nv = new AePropertyNameValue("path", getBpelModel().getLocationPath(), null); //$NON-NLS-1$
      addProperty(nv);
      // add state
      if (isHasState() && AeUtil.notNullOrEmpty(getBpelModel().getDisplayStateKey()))
      {
         addProperty(IAeImplStateNames.STATE_STATE, getBpelModel().getDisplayStateKey());
      }
   }

   /**
    * Builds the non-activity container properties.
    * @param aDef
    */
   protected void buildNonActivityContainerProperties(AeBaseContainer aDef)
   {
      // non-activity containers do not need to show the activity name.
      removeProperty("name"); //$NON-NLS-1$
      String name = ""; //$NON-NLS-1$
      if (NON_ACTIVITY_CONTAINERS.keySet().contains(getBpelModel().getTagName()))
      {
         name = ((String)NON_ACTIVITY_CONTAINERS.get(getBpelModel().getTagName())) + "Count";  //$NON-NLS-1$
      }
      String value = String.valueOf( aDef.getSize());
      addProperty(name, value);
   }

   /**
    * Builds the common properties associated with a BPEL activity.
    * @param aDef
    */
   protected void buildActivityBase(AeActivityDef aDef)
   {
      // add base activity properties.
      buildExpressionProperty("joinCondition", aDef.getJoinConditionDef());  //$NON-NLS-1$
      if (aDef.getSuppressFailure() != null)
      {
         addProperty("suppressJoinFailure", getYesNo(aDef.getSuppressFailure().booleanValue())); //$NON-NLS-1$
      }
   }

   /**
    * Builds the common properties for activities which have partner link information (e.g. Receive, Reply, Invoke).
    * @param aDef
    */
   protected void build(AeActivityPartnerLinkBaseDef aDef, boolean aMyRoleFlag)
   {
      addProperty("operation", aDef.getOperation()); //$NON-NLS-1$
      addProperty("partnerLink", aDef.getPartnerLink(), findPartnerLinkLocationPath(aDef.getPartnerLink())); //$NON-NLS-1$
      if (aMyRoleFlag)
      {
         addProperty("portType", getLocalName(aDef.getPartnerLinkDef().getMyRolePortType())); //$NON-NLS-1$
      }
      else
      {
         addProperty("portType", getLocalName(aDef.getPartnerLinkDef().getPartnerRolePortType())); //$NON-NLS-1$
      }
   }

   /**
    * Builds the fault information based on the data in the state document.
    * @param aDef process def.
    */
   protected void buildFault(AeBaseXmlDef aDef)
   {
      try
      {
         // get fault element
         Element ele = selectElement("//" + IAeImplStateNames.STATE_FAULT); //$NON-NLS-1$
         if (ele != null)
         {
            // getPrefixesForNamespace
            String faultNs = ele.getAttribute(IAeImplStateNames.STATE_NAMESPACEURI);
            String faultName = ele.getAttribute(IAeImplStateNames.STATE_NAME);
            Iterator it = aDef.findPrefixesForNamespace(faultNs).iterator();
            if (it.hasNext())
            {
               faultName = it.next() + ":" + faultName;  //$NON-NLS-1$
            }
            addProperty("faultName",  faultName); //$NON-NLS-1$
            addProperty(IAeImplStateNames.STATE_NAMESPACEURI, faultNs);
            String faultSourcePath = ele.getAttribute(IAeImplStateNames.STATE_SOURCE);
            if (AeUtil.notNullOrEmpty(faultSourcePath))
            {
               addProperty(IAeImplStateNames.STATE_SOURCE, faultSourcePath,faultSourcePath);
            }
            if ("true".equalsIgnoreCase(ele.getAttribute(IAeImplStateNames.STATE_HASMESSAGEDATA))) //$NON-NLS-1$
            {
               Element msgDataEle = selectElement(IAeImplStateNames.STATE_MESSAGEDATA, ele);
               AePropertyNameValue pd = createNameValuePair(msgDataEle,false);
               if (pd != null)
               {
                  addDetails(pd);
               }
            }
         }
      }
      catch(Exception e)
      {
         AeException.logError(e, e.getLocalizedMessage());
      }
   }

   /**
    * Builds simple property for a "for",  "until" or "repeatEvery" attributes.
    * @param aForDef
    * @param aUntilDef
    * @param aRepeatEveryDef
    */
   protected void buildForUntilRepeat(IAeExpressionDef aForDef, IAeExpressionDef aUntilDef, IAeExpressionDef aRepeatEveryDef)
   {
      if (aForDef != null)
      {
         buildExpressionProperty("for", aForDef);  //$NON-NLS-1$
      }
      else if (aUntilDef != null)
      {
         buildExpressionProperty("until", aUntilDef);  //$NON-NLS-1$
      }
	  if (aRepeatEveryDef != null)
      {
         buildExpressionProperty("repeatEvery", aRepeatEveryDef);  //$NON-NLS-1$
      }
   }

   /**
    * Builds the expression and expression language properties.
    * @param aPropertyKey
    * @param aExrDef
    */
   protected void buildExpressionProperty(String aPropertyKey, IAeExpressionDef aExrDef)
   {
      if (aExrDef != null)
      {
         addProperty(aPropertyKey, aExrDef.getExpression().trim());
         if (AeUtil.notNullOrEmpty(aExrDef.getExpressionLanguage()))
         {
            addProperty("expressionLanguage", aExrDef.getExpressionLanguage()); //$NON-NLS-1$
         }
      }
   }

   /**
    * Convenience method that returns the first child element.
    * @param aNode
    * @return first child element or null if not found.
    */
   protected Element getFirstChildElement(Node aNode)
   {
      Node node = aNode.getFirstChild();
      while (node != null)
      {
         if ( node.getNodeType() == Node.ELEMENT_NODE )
         {
            return (Element)node;
         }
      }
      return null;
   }

   /**
    * Adds additional properties based on the element.
    * @param aNode current element
    */
   protected void buildDetail(Node aNode)
   {
      Node node = aNode;
      while (node != null)
      {
         if ( node.getNodeType() == Node.ELEMENT_NODE )
         {
            AePropertyNameValue pd = createNameValuePair( (Element)node, false);
            if (pd != null)
            {
               addDetails(pd);
            }
         }
         node = node.getNextSibling();
      }
   }

   /**
    * Constructs a <code>AePropertyNaveValue</code> from the the given
    * <code>Element</code>.
    * @param aElement
    * @param aEditFlag
    */
   protected AePropertyNameValue createNameValuePair(Element aElement, boolean aEditFlag )
   {
      AePropertyNameValue pd = null;
      if (aElement != null)
      {
         String name = aElement.getNodeName();
         String value = AeXMLParserBase.documentToString(aElement, true);
         pd = new AePropertyNameValue(name, value, aEditFlag);
      }
      return pd;
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
    */
   public void visit(AeProcessDef aDef)
   {
      super.visit(aDef);
      addProperty("bpelNamespace", aDef.getNamespace()); //$NON-NLS-1$
      addProperty("targetNamespace", aDef.getTargetNamespace()); //$NON-NLS-1$
      addProperty("queryLanguage", aDef.getQueryLanguage()); //$NON-NLS-1$
      addProperty("expressionLanguage", aDef.getExpressionLanguage()); //$NON-NLS-1$
      addProperty("suppressJoinFailure", getYesNo(aDef.getSuppressJoinFailure())); //$NON-NLS-1$

      if (isHasState())
      {
         String attrs[] = {IAeImplStateNames.STATE_STARTDATE,IAeImplStateNames.STATE_ENDDATE};
         String values[] = selectAttributes(aDef.getLocationPath(),IAeImplStateNames.STATE_PROCESSSTATE, attrs);
         addProperties(attrs, values,true);
         // build fault info
         buildFault(aDef);
      }
   }

   /**
    * Convenience method to append name and value strings to the given buffer.
    * @param aName
    * @param aValue
    * @param aBuffer
    */
   protected void appendData(String aName, String aValue, StringBuffer aBuffer)
   {
      if (AeUtil.notNullOrEmpty(aValue))
      {
         aBuffer.append(aName + "=" + aValue + " "); //$NON-NLS-1$ //$NON-NLS-2$
      }
   }

   /**
    * Builds the variable, property, part and query of a Assign activity.
    * @param aDef
    * @param aBuffer
    */
   protected void buildAssignVarData(AeVarDef aDef, StringBuffer aBuffer)
   {
      appendData(IAeBPELConstants.TAG_VARIABLE, aDef.getVariable(), aBuffer);
      if (aDef.getProperty() != null)
      {
         appendData(IAeBPELConstants.TAG_PROPERTY, AeXmlDefUtil.formatQName(aDef, aDef.getProperty()), aBuffer);
      }
      appendData(IAeBPELConstants.TAG_PART, aDef.getPart(), aBuffer);
      appendData(IAeBPELConstants.TAG_QUERY, aDef.getQuery(), aBuffer);
      appendData(IAeBPELConstants.TAG_PARTNER_LINK, aDef.getPartnerLink(), aBuffer);
      appendData(IAeBPELConstants.TAG_EXPRESSION, aDef.getExpression(), aBuffer);
   }

   /**
    * Builds the property data associated with a From definition.
    * @param aDef
    * @param aBuffer
    */
   protected void buildFromData(AeFromDef aDef, StringBuffer aBuffer)
   {
      buildAssignVarData(aDef, aBuffer);
      appendData(IAeBPELConstants.TAG_ENDPOINT_REFERENCE, aDef.getEndpointReference(), aBuffer);
      if (aDef.getLiteral() != null)
      {
         aBuffer.append("\n"); //$NON-NLS-1$
         aBuffer.append(AeXMLParserBase.documentToString(aDef.getLiteral(), true));
         aBuffer.append("\n"); //$NON-NLS-1$
      }
      if (aDef.isOpaque())
      {
         appendData(IAeBPELConstants.TAG_OPAQUE_ATTR, getYesNo(aDef.isOpaque()), aBuffer);
      }
      
      if (IAeFromStrategyKeys.KEY_FROM_EXTENSION.getStrategyName().equals(aDef.getStrategyKey().getStrategyName()))
      {
         IAeSpecExtension adapter = (IAeSpecExtension) aDef.getAdapterFromAttributes(IAeSpecExtension.class);
         appendData(adapter.getPropertyName(), adapter.getPropertyValue(aDef), aBuffer);
      }
   }

   /**
    * Builds the property data associated with a To definition.
    * @param aDef
    * @param aBuffer
    */
   protected void buildToData(AeToDef aDef, StringBuffer aBuffer)
   {
      buildAssignVarData(aDef, aBuffer);
      
      if (IAeToStrategyKeys.KEY_TO_EXTENSION.getStrategyName().equals(aDef.getStrategyKey().getStrategyName()))
      {
         IAeSpecExtension adapter = (IAeSpecExtension) aDef.getAdapterFromAttributes(IAeSpecExtension.class);
         appendData(adapter.getPropertyName(), adapter.getPropertyValue(aDef), aBuffer);
      }
   }

   /**
    * Overrides method to build the list of Copy definition properties.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityAssignDef)
    */
   public void visit(AeActivityAssignDef aDef)
   {
      removeProperty("name"); //$NON-NLS-1$
      buildActivityBase(aDef);
      addProperty("validate", String.valueOf(aDef.isValidate())); //$NON-NLS-1$
      StringBuffer sb = new StringBuffer();
      Iterator iter = aDef.getCopyDefs();
      while (iter.hasNext())
      {
         AeAssignCopyDef copyDef = (AeAssignCopyDef) iter.next();
         AeFromDef fromDef = copyDef.getFromDef();
         AeToDef toDef = copyDef.getToDef();
         sb.setLength(0);
         sb.append(AeMessages.getString("AeProcessWebPropertyBuilder.copyfrom") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$  //
         buildFromData(fromDef, sb);
         sb.append("\n" + AeMessages.getString("AeProcessWebPropertyBuilder.copyto") + "\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //
         buildToData(toDef, sb);
         sb.append("\n"); //$NON-NLS-1$
         addDetails(AeAssignCopyDef.TAG_COPY, sb.toString().trim(), false);
      }
   }

   /**
    * Overrides method to build the Copy from and to values.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeAssignCopyDef)
    */
   public void visit(AeAssignCopyDef aDef)
   {
      removeProperty("name"); //$NON-NLS-1$
      addProperty("keepSrcElementName", getYesNo( aDef.isKeepSrcElementName())); //$NON-NLS-1$
      addProperty("ignoreMissingFromData", getYesNo( aDef.isIgnoreMissingFromData())); //$NON-NLS-1$
      // FROM construct.
      AeFromDef fromDef = aDef.getFromDef();
      StringBuffer sb = new StringBuffer();
      buildFromData(fromDef, sb);
      addDetails(AeFromDef.TAG_FROM, sb.toString().trim(), false);
      // TO construct
      sb.setLength(0);
      AeToDef toDef = aDef.getToDef();
      buildToData(toDef, sb);
      addDetails(AeToDef.TAG_TO, sb.toString().trim(), false);
   }

   /**
    * Overrides method to add a simple property for named scope (if available).
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateDef)
    */
   public void visit(AeActivityCompensateDef aDef)
   {
      buildActivityBase(aDef);
   }

   /**
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebVisitorBase#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef)
    */
   public void visit(AeActivityCompensateScopeDef aDef)
   {
      buildActivityBase(aDef);
      if (AeUtil.notNullOrEmpty(aDef.getTarget()))
      {
         addProperty("target", aDef.getTarget()); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef)
    */
   public void visit(AeActivityInvokeDef aDef)
   {
      buildActivityBase(aDef);
      build( (AeActivityPartnerLinkBaseDef) aDef, false);
      addProperty("oneWay", getYesNo(isOneWay(aDef))); //$NON-NLS-1$
      addProperty("inputVariable", aDef.getInputVariable(), findVariableLocationPath(aDef.getInputVariable())); //$NON-NLS-1$
      if (AeUtil.notNullOrEmpty(aDef.getOutputVariable()))
      {
         addProperty("outputVariable", aDef.getOutputVariable(), findVariableLocationPath(aDef.getOutputVariable())); //$NON-NLS-1$
      }
   }

   /**
    * Returns true if this is a one-way invoke.
    */
   public boolean isOneWay(AeActivityInvokeDef aDef)
   {
      return aDef.getConsumerMessagePartsMap() == null;
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityPickDef)
    */
   public void visit(AeActivityPickDef aDef)
   {
      buildActivityBase(aDef);
      addProperty("createInstance", getYesNo(aDef.isCreateInstance()));       //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef)
    */
   public void visit(AeActivityReceiveDef aDef)
   {
      buildActivityBase(aDef);
      build( (AeActivityPartnerLinkBaseDef) aDef, true);
      addProperty("createInstance", getYesNo(aDef.isCreateInstance())); //$NON-NLS-1$
      if (AeUtil.notNullOrEmpty(aDef.getMessageExchange()))
      {
         addProperty("messageExchange",aDef.getMessageExchange()); //$NON-NLS-1$
      }
      addProperty("variable", aDef.getVariable(), findVariableLocationPath(aDef.getVariable()));       //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReplyDef)
    */
   public void visit(AeActivityReplyDef aDef)
   {
      buildActivityBase(aDef);
      build( (AeActivityPartnerLinkBaseDef) aDef, true);
      if (AeUtil.notNullOrEmpty(aDef.getMessageExchange()))
      {
         addProperty("messageExchange",aDef.getMessageExchange()); //$NON-NLS-1$
      }

      addProperty("variable", aDef.getVariable(), findVariableLocationPath(aDef.getVariable())); //$NON-NLS-1$
      if (aDef.getFaultName()!= null)
      {
         addProperty("faultName", getLocalName(aDef.getFaultName())); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivitySuspendDef)
    */
   public void visit(AeActivitySuspendDef aDef)
   {
      buildActivityBase(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef)
    */
   public void visit(AeActivityScopeDef aDef)
   {
      buildActivityBase(aDef);
      if ( isBpelVersion11() )
      {
         addProperty("variableAccessSerializable", getYesNo(aDef.isIsolated()) ); //$NON-NLS-1$
      }
      else
      {
         addProperty("isolated", getYesNo(aDef.isIsolated()) ); //$NON-NLS-1$
      }
      if (aDef.getScopeDef().hasMessageExchanges())
      {
         Set messageExSet = aDef.getScopeDef().getMessageExchangesDef().getMessageExchangeValues();
         StringBuffer buffer = new StringBuffer();
         int size = messageExSet.size();
         int i = 0;
         Iterator it = messageExSet.iterator();
         while (it.hasNext())
         {
            buffer.append( (String) it.next() );
            buffer.append( i < (size-1)? ", " : ""); //$NON-NLS-1$ //$NON-NLS-2$
         }
         addProperty("messageExchange", buffer.toString()); //$NON-NLS-1$
      }

   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityContinueDef)
    */
   public void visit(AeActivityContinueDef aDef)
   {
      removeProperty("name"); //$NON-NLS-1$
      buildActivityBase(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityBreakDef)
    */
   public void visit(AeActivityBreakDef aDef)
   {
      removeProperty("name"); //$NON-NLS-1$
      buildActivityBase(aDef);
   }

   /**
    * Implements the visit of the corr set by getting the definition properties.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationSetDef)
    */
   public void visit(AeCorrelationSetDef aDef)
   {
      // properties.
      if (!isHasState() && aDef.getProperties().size() > 0)
      {
         StringBuffer sb = new StringBuffer();
         Iterator it = aDef.getPropertiesList();
         while (it.hasNext())
         {
            QName property = (QName) it.next();
            sb.append(getLocalName(property) + "\n"); //$NON-NLS-1$
         }
         addDetails("properties", sb.toString(), false); //$NON-NLS-1$
      }
   }

   /**
    * Add correlation set details.
    * @param aNvPair
    */
   protected void addCorrelationSetProperty(AePropertyNameValue aNvPair)
   {
      addDetails(aNvPair);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCatchDef)
    */
   public void visit(AeCatchDef aDef)
   {
      // catch
      if (aDef.getFaultName() != null)
      {
         String faultName = getPrefixedName(aDef, aDef.getFaultName());
         addProperty("faultName",  faultName); //$NON-NLS-1$
         addProperty(IAeImplStateNames.STATE_NAMESPACEURI, aDef.getFaultName().getNamespaceURI());
      }
      if (AeUtil.notNullOrEmpty(aDef.getFaultVariable()))
      {
         addProperty("variable", aDef.getFaultVariable(),   //$NON-NLS-1$
               aDef.getFaultVariableDef() != null? aDef.getFaultVariableDef().getLocationPath() : findVariableLocationPath(aDef.getFaultVariable()));
      }
   }


   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeVariableDef)
    */
   public void visit(AeVariableDef aDef)
   {
      String propertyName = null;
      QName typeQName = null;
      if (aDef.isMessageType())
      {
         propertyName = "messageType"; //$NON-NLS-1$
         typeQName =  aDef.getMessageType();
      }
      else if (aDef.isElement())
      {
         propertyName = "elementType"; //$NON-NLS-1$
         typeQName =  aDef.getElement();
      }
      else if (aDef.isType())
      {
         propertyName = "variableType"; //$NON-NLS-1$
         typeQName = aDef.getType();
      }

      if (typeQName != null)
      {
         addProperty(propertyName,  getPrefixedName(aDef, typeQName));
         addProperty("typeNamespace",  typeQName.getNamespaceURI()); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeVariablesDef)
    */
   public void visit(AeVariablesDef aDef)
   {
      buildNonActivityContainerProperties((AeBaseContainer)aDef);
   }


   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationSetsDef)
    */
   public void visit(AeCorrelationSetsDef aDef)
   {
      buildNonActivityContainerProperties((AeBaseContainer)aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeMessageExchangesDef)
    */
   public void visit(AeMessageExchangesDef aDef)
   {
      buildNonActivityContainerProperties((AeBaseContainer)aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeMessageExchangeDef)
    */
   public void visit(AeMessageExchangeDef aDef)
   {
      addProperty("name", aDef.getName()); //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef)
    */
   public void visit(AeOnMessageDef aDef)
   {
      addProperty("operation", aDef.getOperation()); //$NON-NLS-1$
      addProperty("partnerLink", aDef.getPartnerLink(), findPartnerLinkLocationPath(aDef.getPartnerLink())); //$NON-NLS-1$
      addProperty("portType", getLocalName(aDef.getPartnerLinkDef().getMyRolePortType())); //$NON-NLS-1$
      addProperty("variable", aDef.getVariable(), findVariableLocationPath(aDef.getVariable()));  //$NON-NLS-1$
      if (AeUtil.notNullOrEmpty(aDef.getMessageExchange()))
      {
         addProperty("messageExchange",aDef.getMessageExchange()); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebVisitorBase#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef aDef)
   {
      visit((AeOnMessageDef) aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef)
    */
   public void visit(AeOnAlarmDef aDef)
   {
      buildForUntilRepeat(aDef.getForDef(), aDef.getUntilDef(), aDef.getRepeatEveryDef());
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivitySequenceDef)
    */
   public void visit(AeActivitySequenceDef aDef)
   {
      buildActivityBase(aDef);
   }

   /**
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebVisitorBase#visit(org.activebpel.rt.bpel.def.activity.AeActivityIfDef)
    */
   public void visit(AeActivityIfDef aDef)
   {
      buildActivityBase(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityExitDef)
    */
   public void visit(AeActivityExitDef aDef)
   {
      buildActivityBase(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityThrowDef)
    */
   public void visit(AeActivityThrowDef aDef)
   {
      buildActivityBase(aDef);
      addProperty("faultName", getLocalName(aDef.getFaultName()) ); //$NON-NLS-1$
      if (AeUtil.notNullOrEmpty(aDef.getFaultVariable()))
      {
         addProperty("faultVariable", aDef.getFaultVariable(), findVariableLocationPath(aDef.getFaultVariable())); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityValidateDef)
    */
   public void visit(AeActivityValidateDef aDef)
   {
      buildActivityBase(aDef);
      //hyperlink space separated list of variables - one per row.
      int count = 0;
      for (Iterator iter = aDef.getVariables(); iter.hasNext(); )
      {
         String var = (String) iter.next();
         addProperty("variables" + count, "variable", var, findVariableLocationPath(var) ); //$NON-NLS-1$ //$NON-NLS-2$
         count++;
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWaitDef)
    */
   public void visit(AeActivityWaitDef aDef)
   {
      buildActivityBase(aDef);
      buildForUntilRepeat(aDef.getForDef(), aDef.getUntilDef(), null);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWhileDef)
    */
   public void visit(AeActivityWhileDef aDef)
   {
      buildActivityBase(aDef);
      buildExpressionProperty("condition", aDef.getConditionDef());  //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityRepeatUntilDef)
    */
   public void visit(AeActivityRepeatUntilDef aDef)
   {
      buildActivityBase(aDef);
      buildExpressionProperty("condition", aDef.getConditionDef());  //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityForEachDef)
    */
   public void visit(AeActivityForEachDef aDef)
   {
      buildActivityBase(aDef);
      addProperty("counterName", aDef.getCounterName()); //$NON-NLS-1$
      addProperty("startCounterExpression", aDef.getStartDef().getExpression()); //$NON-NLS-1$
      addProperty("finalCounterExpression", aDef.getFinalDef().getExpression()); //$NON-NLS-1$
      addProperty("parallelExecution", getYesNo(aDef.isParallel())); //$NON-NLS-1$
      if (aDef.hasCompletionCondition())
      {
         addProperty("completionCondition", aDef.getCompletionCondition().getExpression()); //$NON-NLS-1$
         addProperty("countCompletedBranchesOnly", getYesNo(aDef.getCompletionCondition().isCountCompletedBranchesOnly())); //$NON-NLS-1$
      }
      if (aDef.isParallel() && isHasState())
      {
         String attrs[] = {IAeImplStateNames.STATE_FOREACH_START,IAeImplStateNames.STATE_FOREACH_FINAL, IAeImplStateNames.STATE_FOREACH_COUNTER};
         String values[] = selectAttributes(getLocationPath(),IAeImplStateNames.STATE_ACTY, attrs);
         for (int i = 0; i < attrs.length; i++)
         {
            // if the counter values are -1, then display string 'Not initialized'.
            values[i] = "-1".equals(values[i]) ? AeMessages.getString("AeProcessViewPropertyBuilder.7") : values[i];  //$NON-NLS-1$//$NON-NLS-2$
         }
         addProperties(attrs, values,false);
      }
   }


   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartDef)
    */
   public void visit(AeToPartDef aDef)
   {
      addProperty("part", aDef.getPart()); //$NON-NLS-1$
      addProperty("fromVariable", aDef.getFromVariable()); //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartDef)
    */
   public void visit(AeFromPartDef aDef)
   {
      addProperty("part", aDef.getPart()); //$NON-NLS-1$
      addProperty("toVariable", aDef.getToVariable()); //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef)
    */
   public void visit(AeFromPartsDef aDef)
   {
      buildNonActivityContainerProperties((AeBaseContainer)aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartsDef)
    */
   public void visit(AeToPartsDef aDef)
   {
      buildNonActivityContainerProperties((AeBaseContainer)aDef);
   }

   /**
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebVisitorBase#visit(org.activebpel.rt.bpel.def.activity.support.AeElseIfDef)
    */
   public void visit(AeElseIfDef aDef)
   {
      removeProperty("name"); //$NON-NLS-1$
      buildExpressionProperty("condition", aDef.getConditionDef());  //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebVisitorBase#visit(org.activebpel.rt.bpel.def.activity.support.AeIfDef)
    */
   public void visit(AeIfDef aDef)
   {
      visit((AeElseIfDef) aDef);
   }

   /**
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebVisitorBase#visit(org.activebpel.rt.bpel.def.activity.support.AeElseDef)
    */
   public void visit(AeElseDef aDef)
   {
      removeProperty("name"); //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerDef)
    */
   public void visit(AePartnerDef aDef)
   {
      Iterator iter = aDef.getPartnerLinks();
      while (iter.hasNext())
      {
         String partnerLink = (String) iter.next();
         String plLocationPath = findPartnerLinkLocationPath(partnerLink);
         addProperty("parnerLink", partnerLink, plLocationPath); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerLinkDef)
    */
   public void visit(AePartnerLinkDef aDef)
   {
      addProperty("myRole", aDef.getMyRole()); //$NON-NLS-1$
      addProperty("partnerRole", aDef.getPartnerRole()); //$NON-NLS-1$
      addProperty("partnerLinkType", getLocalName(aDef.getPartnerLinkTypeName())); //$NON-NLS-1$
      if (isHasState())
      {
         try
         {
            Element ele = selectSingleElement(aDef, IAeImplStateNames.STATE_PLINK);
            if (ele != null)
            {
               Element myRoleEle = selectElement(IAeImplStateNames.STATE_ROLE, ele);
               AePropertyNameValue pd = createNameValuePair(myRoleEle,false);
               if (pd != null)
               {
                  addPartnerLinkRole(pd,IAeImplStateNames.STATE_ROLE);
               }
               Element partnerRoleEle = selectElement(IAeImplStateNames.STATE_PROLE, ele);
               pd = createNameValuePair(partnerRoleEle,false);
               if (pd != null)
               {
                  addPartnerLinkRole(pd,IAeImplStateNames.STATE_PROLE);
               }
            }
         }
         catch(Exception e)
         {
            AeException.logError(e, e.getLocalizedMessage());
         }
      }
   }

   /**
    * Adds a partnerRole or myRole data.
    * @param aNvPair
    */
   protected void addPartnerLinkRole(AePropertyNameValue aNvPair, String aRole)
   {
      addDetails(aNvPair);
   }


   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef)
    */
   public void visit(AeCorrelationDef aDef)
   {
      IAeCorrelationPatternIO patternIO = AeCorrelationPatternIOFactory.getInstance(AeDefUtil.getProcessDef(aDef).getNamespace());

      removeProperty("name"); //$NON-NLS-1$
      addProperty("pattern",  aDef.getPattern() != null? patternIO.toString(aDef.getPattern()) : ""); //$NON-NLS-1$ //$NON-NLS-2$
      addProperty("set",  aDef.getCorrelationSetName(), findCorrelationSetLocationPath(aDef.getCorrelationSetName())); //$NON-NLS-1$
      addProperty("initiate",  aDef.getInitiate()); //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLinkDef)
    */
   public void visit(AeLinkDef aDef)
   {
      AeBpelLinkObject linkModel = (AeBpelLinkObject) getBpelModel();
      if (isHasState())
      {
         addProperty("linkStatus", AeProcessViewUtil.formatLabel(linkModel.getStatus()) ); //$NON-NLS-1$
      }

      addProperty("transitionCondition", linkModel.getCondition()); //$NON-NLS-1$
      addProperty("link.source", linkModel.getSource().getLocationPath(), linkModel.getSource().getLocationPath()); //$NON-NLS-1$
      addProperty("link.target", linkModel.getTarget().getLocationPath(), linkModel.getTarget().getLocationPath()); //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerLinksDef)
    */
   public void visit(AePartnerLinksDef aDef)
   {
      buildNonActivityContainerProperties((AeBaseContainer)aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnersDef)
    */
   public void visit(AePartnersDef aDef)
   {
      buildNonActivityContainerProperties((AeBaseContainer)aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationsDef)
    */
   public void visit(AeCorrelationsDef aDef)
   {
      buildNonActivityContainerProperties((AeBaseContainer)aDef);
   }

   /**
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebVisitorBase#visit(org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef)
    */
   public void visit(AeChildExtensionActivityDef aDef)
   {
      // Ignore - since if adapter is present then build() will handle it via buildPropertiesFromAdapter()
   }

   /**
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebVisitorBase#visit(org.activebpel.rt.xml.def.AeExtensionElementDef)
    */
   public void visit(AeExtensionElementDef aDef)
   {
      // Ignore - since if adapter is present then build() will handle it via buildPropertiesFromAdapter()
   }
}

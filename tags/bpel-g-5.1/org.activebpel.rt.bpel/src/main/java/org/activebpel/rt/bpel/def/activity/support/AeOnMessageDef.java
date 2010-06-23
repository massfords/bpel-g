// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/support/AeOnMessageDef.java,v 1.27 2008/03/15 22:13:09 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.activity.support;

import java.util.Collections;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AeCorrelationsDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDefKey;
import org.activebpel.rt.bpel.def.AePartnerLinkDelegate;
import org.activebpel.rt.bpel.def.AePartnerLinkOpKey;
import org.activebpel.rt.bpel.def.AeSingleActivityParentBaseDef;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.IAeActivityCreateInstanceDef;
import org.activebpel.rt.bpel.def.IAeCorrelationsParentDef;
import org.activebpel.rt.bpel.def.IAeFromPartsParentDef;
import org.activebpel.rt.bpel.def.IAeSingleActivityContainerDef;
import org.activebpel.rt.bpel.def.activity.AeActivityPickDef;
import org.activebpel.rt.bpel.def.activity.IAeMessageDataConsumerDef;
import org.activebpel.rt.bpel.def.activity.IAeReceiveActivityDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;
import org.activebpel.rt.message.AeMessagePartsMap;

/** The onMessage element used within the Pick activity */
public class AeOnMessageDef extends AeSingleActivityParentBaseDef implements IAeSingleActivityContainerDef,
      IAeReceiveActivityDef, IAeActivityCreateInstanceDef, IAeFromPartsParentDef, IAeCorrelationsParentDef,
      IAeMessageDataConsumerDef
{
   /** message variable */
   private String mVariable;
   /** on message one-way flag. */
   private boolean mOneWay = false;
   /** Message exchange value */
   private String mMessageExchange;
   /** delegate which handle the partner link and correlation information. */
   private AePartnerLinkDelegate mDelegate = new AePartnerLinkDelegate();
   /** The fromParts child def. */
   private AeFromPartsDef mFromPartsDef;
   /** name of the strategy used to consumer the message data */
   private String mMessageDataConsumerStrategy;
   /** the type of activity for display in errors */
   private static String sDisplayTypeText = AeMessages.getString("AeActivityReceiveDef.onMessage"); //$NON-NLS-1$
   
   /**
    * Default constructor
    */
   public AeOnMessageDef()
   {
      super();
   }

   /**
    * Returns the delegate.
    */
   protected AePartnerLinkDelegate getDelegate()
   {
      return mDelegate;
   }

   /**
    * Accessor method to obtain the name of the partner link.
    *
    * @return name of partner link
    */
   public String getPartnerLink()
   {
      return getDelegate().getPartnerLink();
   }

   /**
    * Mutator method to set the name of the partner link.
    *
    * @param aPartnerLink name of partner link
    */
   public void setPartnerLink(String aPartnerLink)
   {
      getDelegate().setPartnerLink( aPartnerLink );
   }

   /**
    * Accessor method to obtain the port type for the message.
    *
    * @return port type of message
    */
   public QName getPortType()
   {
      return getDelegate().getPortType();
   }

   /**
    * Mutator method to set the port type of the message.
    *
    * @param aPortType port type of the message
    */
   public void setPortType(QName aPortType)
   {
      getDelegate().setPortType(aPortType);
   }

   /**
    * Accessor method to obtain the operation for the message.
    *
    * @return operation of message
    */
   public String getOperation()
   {
      return getDelegate().getOperation();
   }

   /**
    * Mutator method to set the operation of the message.
    *
    * @param aOperation operation of the message
    */
   public void setOperation(String aOperation)
   {
      getDelegate().setOperation(aOperation);
   }

   /**
    * Accessor method to obtain the variable for the message.
    *
    * @return variable for message
    */
   public String getVariable()
   {
      return mVariable;
   }

   /**
    * Mutator method to set the variable for the message.
    *
    * @param aVariable the variable for the message
    */
   public void setVariable(String aVariable)
   {
      mVariable = aVariable;
   }

   /**
    * Provides the ability to add a correlation element to the correlation list.
    *
    * @param aCorrelation the correlation to be added
    */
   public void addCorrelationDef(AeCorrelationDef aCorrelation)
   {
      getDelegate().addCorrelation(aCorrelation);
   }

   /**
    * Provide a list of the Correlation objects for the user to iterate .
    *
    * @return iterator of AeCorrelationDef objects or null if none defined
    */
   public Iterator getCorrelationDefs()
   {
      return getDelegate().getCorrelationDefs();
   }

   /**
    * Utility method to determine if a message has a correlation list
    * @return true if there are elements in the correlation list.
    */
   public boolean hasCorrelationList()
   {
      return getDelegate().hasCorrelationList();
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }

   /**
    * Accessor method to obtain the oneWay flag.
    */
   public boolean isOneWay()
   {
      return mOneWay;
   }

   /**
    * Mutator method to set the one way flag for the activity.
    *
    * @param aOneWay boolean flag indicating if the instance should be one way.
    */
   public void setOneWay(boolean aOneWay)
   {
      mOneWay = aOneWay;
   }

   /**
    * @return Returns the messageExchange.
    */
   public String getMessageExchange()
   {
      return mMessageExchange;
   }

   /**
    * @param aMessageExchange The messageExchange to set.
    */
   public void setMessageExchange(String aMessageExchange)
   {
      mMessageExchange = aMessageExchange;
   }

   /**
    * Overrides method to return the state of the parent def (Pick) activity's createInstance attribute.
    * @see org.activebpel.rt.bpel.def.IAeActivityCreateInstanceDef#isCreateInstance()
    */
   public boolean isCreateInstance()
   {
      if ( getParent() instanceof IAeActivityCreateInstanceDef )
      {
         return ( (IAeActivityCreateInstanceDef) getParent()).isCreateInstance();
      }
      else
      {
         return false;
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeFromPartsParentDef#setFromPartsDef(AeFromPartsDef)
    */
   public void setFromPartsDef(AeFromPartsDef aDef)
   {
      mFromPartsDef = aDef;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.IAeFromPartsParentDef#getFromPartsDef()
    */
   public AeFromPartsDef getFromPartsDef()
   {
      return mFromPartsDef;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeFromPartsParentDef#getFromPartDefs()
    */
   public Iterator getFromPartDefs()
   {
      if (getFromPartsDef() == null)
         return Collections.EMPTY_LIST.iterator();
      else
         return getFromPartsDef().getFromPartDefs();
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeCorrelationsParentDef#getCorrelationsDef()
    */
   public AeCorrelationsDef getCorrelationsDef()
   {
      return getDelegate().getCorrelationsDef();
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeCorrelationsParentDef#setCorrelationsDef(org.activebpel.rt.bpel.def.AeCorrelationsDef)
    */
   public void setCorrelationsDef(AeCorrelationsDef aDef)
   {
      getDelegate().setCorrelationsDef(aDef);
   }

   /**
    * Gets the partner link key.
    */
   private AePartnerLinkDefKey getPartnerLinkKey()
   {
      AePartnerLinkDef plinkDef = getPartnerLinkDef();
      if (plinkDef == null)
         return null;

      return new AePartnerLinkDefKey(plinkDef);
   }

   /**
    * Gets the partner link def
    */
   public AePartnerLinkDef getPartnerLinkDef()
   {
      AePartnerLinkDef plinkDef = AeDefUtil.findPartnerLinkDef(this, getPartnerLink());
      return plinkDef;
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAePartnerLinkActivityDef#getPartnerLinkOperationKey()
    */
   public AePartnerLinkOpKey getPartnerLinkOperationKey()
   {
      AePartnerLinkDefKey defKey = getPartnerLinkKey();
      if (defKey == null)
         return null;

      return new AePartnerLinkOpKey(defKey, getOperation());
   }

   /**
    * Sets the message parts map for the input message.
    */
   public void setConsumerMessagePartsMap(AeMessagePartsMap aInputMessagePartsMap)
   {
      getDelegate().setConsumerMessagePartsMap(aInputMessagePartsMap);
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeMessageDataConsumerDef#getConsumerMessagePartsMap()
    */
   public AeMessagePartsMap getConsumerMessagePartsMap()
   {
      return getDelegate().getConsumerMessagePartsMap();
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeMessageDataConsumerDef#getMessageDataConsumerVariable()
    */
   public AeVariableDef getMessageDataConsumerVariable()
   {
      return AeDefUtil.getVariableByName(getVariable(), this);
}

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeMessageDataConsumerDef#setMessageDataConsumerStrategy(java.lang.String)
    */
   public void setMessageDataConsumerStrategy(String aStrategy)
   {
      mMessageDataConsumerStrategy = aStrategy;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeMessageDataConsumerDef#getMessageDataConsumerStrategy()
    */
   public String getMessageDataConsumerStrategy()
   {
      return mMessageDataConsumerStrategy;
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeReceiveActivityDef#getContext()
    */
   public AeBaseDef getContext()
   {
      return this;
   }
   
   /**
    * Convenience method to return if this onMessage is nested within a pick
    * or a BPWS 1.1 scope activity.
    */
   public boolean isPickMessage()
   {
      return getParent() instanceof AeActivityPickDef;
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeMessageDataConsumerDef#getConsumerOperation()
    */
   public String getConsumerOperation()
   {
      return getOperation();
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeMessageDataConsumerDef#getConsumerPortType()
    */
   public QName getConsumerPortType()
   {
      return getPortType();
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeReceiveActivityDef#getTypeDisplayText()
    */
   public String getTypeDisplayText()
   {
      return sDisplayTypeText;
   }
}

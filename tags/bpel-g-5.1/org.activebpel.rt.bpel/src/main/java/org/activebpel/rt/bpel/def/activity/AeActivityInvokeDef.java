// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/AeActivityInvokeDef.java,v 1.18 2008/02/19 14:13:31 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AeActivityPartnerLinkBaseDef;
import org.activebpel.rt.bpel.def.AeCatchAllDef;
import org.activebpel.rt.bpel.def.AeCatchDef;
import org.activebpel.rt.bpel.def.AeCompensationHandlerDef;
import org.activebpel.rt.bpel.def.AeFaultHandlersDef;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.IAeCatchParentDef;
import org.activebpel.rt.bpel.def.IAeCompensationHandlerParentDef;
import org.activebpel.rt.bpel.def.IAeFromPartsParentDef;
import org.activebpel.rt.bpel.def.IAeToPartsParentDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef;
import org.activebpel.rt.bpel.def.activity.support.AeToPartsDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;
import org.activebpel.rt.util.AeUtil;

/**
 * Definition for bpel invoke activity.
 */
public class AeActivityInvokeDef extends AeActivityPartnerLinkBaseDef implements IAeCatchParentDef,
      IAeFromPartsParentDef, IAeToPartsParentDef, IAeCompensationHandlerParentDef, IAeMessageDataProducerDef, IAeMessageDataConsumerDef
{
   /** The input variable. */
   private String mInputVariable;
   /** The output variable. */
   private String mOutputVariable;
   /** The fromParts child def. */
   private AeFromPartsDef mFromPartsDef;
   /** The toParts child def. */
   private AeToPartsDef mToPartsDef;
   /**  The scope which holds fault and compensation information if necessary (may be null). */
   private AeActivityScopeDef mScopeDef;
   /** The strategy hint for how we produce the message data for the invoke */
   private String mMessageDataProducerStrategy;
   /** The strategy hint for how we consume the message data for the invoke */
   private String mMessageDataConsumerStrategy;

   /**
    * Default constructor
    */
   public AeActivityInvokeDef()
   {
      super();
   }
   
   /**
    * Adds to the list passed in, returning a new list if the list param was null
    * @param aObject
    * @param aListToAddTo
    */
   protected List addToList(Object aObject, List aListToAddTo)
   {
      if (aListToAddTo == null)
         aListToAddTo = new ArrayList();
      aListToAddTo.add(aObject);
      return aListToAddTo;
   }

   /**
    * Returns the input Variable of the invoke activity.
    * @return input variable of invoke activity
    */
   public final String getInputVariable()
   {
      return mInputVariable;
   }

   /**
    * Sets the input variable of the invoke activity.
    * @param aInputVariable input variable of the invoke activity 
    */
   public void setInputVariable(String aInputVariable)
   {
      mInputVariable = aInputVariable;
   }

   /**
    * Returns the output variable of the invoke activity.
    * @return output variable of invoke activity
    */
   public String getOutputVariable()
   {
      return mOutputVariable;
   }

   /**
    * Sets the output variable of the invoke activity.
    * @param aOutputVariable output variable of the invoke activity 
    */
   public final void setOutputVariable(String aOutputVariable)
   {
      mOutputVariable = aOutputVariable;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeCatchParentDef#addCatchDef(org.activebpel.rt.bpel.def.AeCatchDef)
    */
   public void addCatchDef(AeCatchDef aDef)
   {
      // Add the catch def to the implicit scope's faultHandlers def.
      AeFaultHandlersDef faultHandlersDef = getOrCreateImiplicitScopeDef().getScopeDef().getFaultHandlersDef();
      if (faultHandlersDef == null)
      {
         faultHandlersDef = new AeFaultHandlersDef();
         getOrCreateImiplicitScopeDef().getScopeDef().setFaultHandlersDef(faultHandlersDef);
      }
      faultHandlersDef.addCatchDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeCatchParentDef#setCatchAllDef(org.activebpel.rt.bpel.def.AeCatchAllDef)
    */
   public void setCatchAllDef(AeCatchAllDef aDef)
   {
      // Set the catchAll def on the implicit scope's faultHandlers def.
      AeFaultHandlersDef faultHandlersDef = getOrCreateImiplicitScopeDef().getScopeDef().getFaultHandlersDef();
      if (faultHandlersDef == null)
      {
         faultHandlersDef = new AeFaultHandlersDef();
         getOrCreateImiplicitScopeDef().getScopeDef().setFaultHandlersDef(faultHandlersDef);
      }
      faultHandlersDef.setCatchAllDef(aDef);
   }

   /**
    * Sets the implicit scope def.
    * 
    * @param aScopeDef
    */
   public void setImplicitScopeDef(AeActivityScopeDef aScopeDef)
   {
      mScopeDef = aScopeDef;
   }
   
   /**
    * Removes and returns the implicit scope def.  Sort of 'detaches' the implicit scope.  This
    * is used when the implicit scope gets 'pulled up' into an explicit parent of the invoke.
    */
   public AeActivityScopeDef removeImplicitScopeDef()
   {
      AeActivityScopeDef rval = mScopeDef;
      mScopeDef = null;
      return rval;
   }

   /**
    * Simply returns the implicit scope def or null if there isn't one.
    */
   public AeActivityScopeDef getImplicitScopeDef()
   {
      return mScopeDef;
   }
   
   /**
    * Returns true if an implicit scope exists for this activity.
    */
   public boolean hasImplicitScopeDef()
   {
      return mScopeDef != null;
   }

   /**
    * Creates an implicit scope for use by the invoke if one has not already been created. 
    */
   protected AeActivityScopeDef getOrCreateImiplicitScopeDef()
   {
      if (mScopeDef == null)
      {
         mScopeDef = new AeActivityScopeDef();
      }
      return mScopeDef;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeCompensationHandlerParentDef#getCompensationHandlerDef()
    */
   public AeCompensationHandlerDef getCompensationHandlerDef()
   {
      return getOrCreateImiplicitScopeDef().getCompensationHandlerDef();
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeCompensationHandlerParentDef#setCompensationHandlerDef(org.activebpel.rt.bpel.def.AeCompensationHandlerDef)
    */
   public void setCompensationHandlerDef(AeCompensationHandlerDef aDef)
   {
      getOrCreateImiplicitScopeDef().setCompensationHandlerDef(aDef);
   }

   /**
    * Create implicit scope (if one doesn't extist) and return its 
    * fault handler container def.
    * @return implicit scope's fault handler container def
    */
   public AeFaultHandlersDef getFaultHandlersDef()
   {
      return getOrCreateImiplicitScopeDef().getScopeDef().getFaultHandlersDef();
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
    * @see org.activebpel.rt.bpel.def.IAeToPartsParentDef#setToPartsDef(AeToPartsDef)
    */
   public void setToPartsDef(AeToPartsDef aDef)
   {
      mToPartsDef = aDef;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.IAeToPartsParentDef#getToPartsDef()
    */
   public AeToPartsDef getToPartsDef()
   {
      return mToPartsDef;
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeToPartsParentDef#getToPartDefs()
    */
   public Iterator getToPartDefs()
   {
      if (getToPartsDef() == null)
         return Collections.EMPTY_LIST.iterator();
      else
         return getToPartsDef().getToPartDefs();
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeCatchParentDef#getCatchDefs()
    */
   public Iterator getCatchDefs()
   {
      return getFaultHandlersDef().getCatchDefs();
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeCatchParentDef#getCatchAllDef()
    */
   public AeCatchAllDef getCatchAllDef()
   {
      return getFaultHandlersDef().getCatchAllDef();
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeActivityDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef#getMessageDataProducerVariable()
    */
   public AeVariableDef getMessageDataProducerVariable()
   {
      return AeDefUtil.getVariableByName(getInputVariable(), this);
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeMessageDataConsumerDef#getMessageDataConsumerVariable()
    */
   public AeVariableDef getMessageDataConsumerVariable()
   {
      return AeDefUtil.getVariableByName(getOutputVariable(), this);
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef#setMessageDataProducerStrategy(java.lang.String)
    */
   public void setMessageDataProducerStrategy(String aStrategy)
   {
      mMessageDataProducerStrategy = aStrategy;
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef#getMessageDataProducerStrategy()
    */
   public String getMessageDataProducerStrategy()
   {
      return mMessageDataProducerStrategy;
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
    * @see org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef#getProducerOperation()
    */
   public String getProducerOperation()
   {
      return getOperation();
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef#getProducerPortType()
    */
   public QName getProducerPortType()
   {
      QName portType = getPortType();
      if (AeUtil.isNullOrEmpty(portType))
      {
         portType = getPartnerLinkDef().getPartnerRolePortType();
      }
      return portType;
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
      QName portType = getPortType();
      if (AeUtil.isNullOrEmpty(portType))
      {
         portType = getPartnerLinkDef().getPartnerRolePortType();
      }
      return portType;
   }
   
   
}

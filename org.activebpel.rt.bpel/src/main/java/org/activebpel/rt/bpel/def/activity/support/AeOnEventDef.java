// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.activity.support;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Models the 'onEvent' bpel construct introduced in WS-BPEL 2.0.
 */
public class AeOnEventDef extends AeOnMessageDef
{
   /** The 'messageType' attribute. */
   private QName mMessageType;
   /** The 'element' attribute. */
   private QName mElement;
   /** the type of activity for display in errors */
   private static String sDisplayTypeText = AeMessages.getString("AeActivityReceiveDef.onEvent"); //$NON-NLS-1$
   
   /**
    * Default c'tor.
    */
   public AeOnEventDef()
   {
      super();
   }

   /**
    * @return Returns the element.
    */
   public QName getElement()
   {
      return mElement;
   }

   /**
    * @param aElement The element to set.
    */
   public void setElement(QName aElement)
   {
      mElement = aElement;
   }

   /**
    * @return Returns the messageType.
    */
   public QName getMessageType()
   {
      return mMessageType;
   }

   /**
    * @param aMessageType The messageType to set.
    */
   public void setMessageType(QName aMessageType)
   {
      mMessageType = aMessageType;
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }

   /**
    * Returns the <code>onEvent</code>'s scope definition. 
    */
   public AeActivityScopeDef getChildScope()
   {
      // fixme (MF) not safe for bpws 1.1 but only called by ws-bpel classes at this point
      if (getActivityDef() instanceof AeActivityScopeDef)
      {
         return (AeActivityScopeDef) getActivityDef(); 
      }
      return null;
   }

   /**
    * Overrides in order to resolve the variable to the associated scope as opposed
    * to the anscestor scopes.
    * @see org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef#getMessageDataConsumerVariable()
    */
   public AeVariableDef getMessageDataConsumerVariable()
   {
      return AeDefUtil.getVariableByName(getVariable(), getContext());
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef#getPartnerLinkDef()
    */
   public AePartnerLinkDef getPartnerLinkDef()
   {
      AePartnerLinkDef plinkDef = AeDefUtil.findPartnerLinkDef(getContext(), getPartnerLink());
      return plinkDef;
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef#getContext()
    */
   public AeBaseDef getContext()
   {
   // fixme (MF) come back to this and create separate def class for ws-bpel onEvent
      if (getActivityDef() == null)
      {
         return this;
      }
      else
      {
         if (AeDefUtil.isBPWS(this))
         {
            return this;
         }
         else
         {
            return getActivityDef();
         }
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef#getTypeDisplayText()
    */
   public String getTypeDisplayText()
   {
      return sDisplayTypeText;
   }
}

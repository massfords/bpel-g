//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/AeMessageExchangeValidationVisitor.java,v 1.7 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation; 

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AeMessageExchangesDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.IAeMessageExchangesParentDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReplyDef;
import org.activebpel.rt.bpel.def.activity.IAeReceiveActivityDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor;
import org.activebpel.rt.bpel.def.visitors.AeDefTraverser;
import org.activebpel.rt.bpel.def.visitors.AeTraversalVisitor;
import org.activebpel.rt.util.AeUtil;

/**
 * Visits the receives and replies to ensure that they're matched up with their
 * plink, operation, and message exchange values. 
 */
public class AeMessageExchangeValidationVisitor extends AeAbstractDefVisitor
{
   /** error message for unmatched message exchange value */
   protected static final String UNMATCHED_MESSAGE_EXCHANGE = AeMessages.getString("AeMessageExchangeValidationVisitor.UnmatchedMessageExchange"); //$NON-NLS-1$
   
   /** error message for a missing reply */
   protected static final String MISSING_REPLY = AeMessages.getString("AeMessageExchangeValidationVisitor.MissingReply"); //$NON-NLS-1$

   /** error message for a receive/onMessage/reply that references a message exchange value that is not declared w/in the enclosing scope(s) */
   protected static final String UNDECLARED_MESSAGE_EXCHANGE = AeMessages.getString("AeMessageExchangeValidationVisitor.UndeclaredMessageExchange"); //$NON-NLS-1$
   
   /** error message for a receive/onMessage/reply that references a message exchange value that is not declared w/in the enclosing scope(s) */
   protected static final String MESSAGE_EXCHANGE_NOT_ALLOWED = AeMessages.getString("AeMessageExchangeValidationVisitor.MessageExchangeNotAllowed"); //$NON-NLS-1$

   /** Error reporter used during validation process. */
   private IAeValidationProblemReporter mReporter ;

   /** set of plink.operation.messageExchange, all replies are matched against
    *  this set to assert that we have matched receives/replies */
   private Set mMessageExchangeReceives = new HashSet();

   /** Collection of replies that have been visited - used to assert a match w/ receives/replies for messageExchange */
   private Collection mReplies = new LinkedList();
   
   /** collection of receives and onMessages, mapped to the context def used to resolve resources*/
   private Map mReceiveDefsToContexts = new HashMap();
   
   /** process being visited */
   private AeProcessDef mProcessDef;

   /**
    * Ctor
    * @param aErrorReporter
    */
   public AeMessageExchangeValidationVisitor(IAeValidationProblemReporter aErrorReporter)
   {
      setTraversalVisitor( new AeTraversalVisitor( new AeDefTraverser(), this ) );
      setReporter(aErrorReporter);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef)
    */
   public void visit(AeActivityReceiveDef aDef)
   {
      validateReceive(aDef, aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef)
    */
   public void visit(AeOnMessageDef aDef)
   {
      validateReceive(aDef, aDef);
      super.visit(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef aDef)
   {
      if (IAeBPELConstants.BPWS_NAMESPACE_URI.equals(mProcessDef.getNamespace()))
      {
         validateReceive(aDef, aDef);
      }
      else
      {
         validateReceive(aDef, aDef.getActivityDef());
      }
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReplyDef)
    */
   public void visit(AeActivityReplyDef aDef)
   {
      // Note: the message exchange values for replies are validated after the 
      //       whole process has been visited.
      mReplies.add(aDef);
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
    */
   public void visit(AeProcessDef aDef)
   {
      mProcessDef = aDef;
      super.visit(aDef);
      validateMessageExchangeForReplies();
   }
   
   /**
    * Validates the message exchange for the receive, using the context def to resolve any resources.
    * @param aDef - receive or onMessage or onEvent
    * @param aContextDef - def to resolve references to partner links or message exchanges
    */
   protected void validateReceive(IAeReceiveActivityDef aDef, AeBaseDef aContextDef)
   {
      if (aDef.isOneWay() && AeUtil.notNullOrEmpty(aDef.getMessageExchange()))
      {
         Object[] args = { aDef.getLocationPath(), aDef.getMessageExchange() };
         addError( MESSAGE_EXCHANGE_NOT_ALLOWED, args, aDef );
      }
      else if (!aDef.isOneWay())
      {
         String fullPath = getFullPathForMessageExchange(aDef.getMessageExchange(), aContextDef);
         if (fullPath == null)
         {
            Object[] args = { aDef.getMessageExchange() };
            addError( UNDECLARED_MESSAGE_EXCHANGE, args, aDef );
         }
         else
         {
            AePartnerLinkDef plinkDef = AeDefUtil.findPartnerLinkDef(aContextDef, aDef.getPartnerLink());
            // if the plinkDef is null then we'll have reported an error elsewhere
            if (plinkDef != null)
               mMessageExchangeReceives.add(makeMessageExchangeKey(plinkDef.getLocationPath(), aDef.getOperation(), fullPath));
            mReceiveDefsToContexts.put(aDef, aContextDef);
         }
      }
   }
   
   /**
    * Makes a key for the message exchange set.
    * @param aPartnerLink
    * @param aOperation
    * @param aMessageExchange
    */
   protected String makeMessageExchangeKey(String aPartnerLink, String aOperation, String aMessageExchange)
   {
      StringBuffer buffer = new StringBuffer(aPartnerLink);
      buffer.append('.');
      buffer.append(aOperation);
      buffer.append('.');
      buffer.append(aMessageExchange);
      return buffer.toString();
   }

   /**
    * walks all of the replies and asserts that they're properly matched to receives.
    */
   protected void validateMessageExchangeForReplies()
   {
      // validates that the replies match to receives. This will find any replies that have invalid messageExchange values
      Set replyPaths = new HashSet();
      for (Iterator iter = mReplies.iterator(); iter.hasNext();)
      {
         AeActivityReplyDef def = (AeActivityReplyDef) iter.next();
         
         String fullMessageExchangePath = getFullPathForMessageExchange(def.getMessageExchange(), def);
         AePartnerLinkDef plinkDef = AeDefUtil.findPartnerLinkDef(def, def.getPartnerLink());
         // if the plinkDef is null then we'll have reported an error elsewhere
         if (plinkDef != null)
         {
            String key = makeMessageExchangeKey(plinkDef.getLocationPath(), def.getOperation(), fullMessageExchangePath);
            if (!mMessageExchangeReceives.contains(key))
            {
               Object[] args = { def.getPartnerLink(), def.getOperation(), def.getMessageExchange() };
               addError( UNMATCHED_MESSAGE_EXCHANGE, args, def );
            }
            else
            {
               replyPaths.add(key);
            }
         }
      }
      
      // walk the receives that had valid messageExchange values and make sure that
      // they match to replies
      for (Iterator iter = mReceiveDefsToContexts.entrySet().iterator(); iter.hasNext();)
      {
         Map.Entry entry = (Entry) iter.next();
         IAeReceiveActivityDef receiveDef = (IAeReceiveActivityDef) entry.getKey();
         AeBaseDef contextDef = (AeBaseDef) entry.getValue();
         String fullMessageExchangePath = getFullPathForMessageExchange(receiveDef.getMessageExchange(), contextDef);
         AePartnerLinkDef plinkDef = AeDefUtil.findPartnerLinkDef(contextDef, receiveDef.getPartnerLink());
         if (plinkDef != null)
         {
            String key = makeMessageExchangeKey(plinkDef.getLocationPath(), receiveDef.getOperation(), fullMessageExchangePath);
            if (!replyPaths.contains(key))
            {
               Object[] args = { receiveDef.getPartnerLink(), receiveDef.getOperation(), receiveDef.getMessageExchange(), receiveDef.getTypeDisplayText() };
               addError( MISSING_REPLY, args, receiveDef );
            }
         }
      }
   }

   /**
    * @return Returns the reporter.
    */
   protected IAeValidationProblemReporter getReporter()
   {
      return mReporter;
   }

   /**
    * @param aReporter The reporter to set.
    */
   protected void setReporter(IAeValidationProblemReporter aReporter)
   {
      mReporter = aReporter;
   }
   
   /**
    * Adds an error for ws-bpel and a warning for bpws
    * @param aKey
    * @param aArgs
    * @param aDef
    */
   protected void addError(String aKey, Object[] aArgs, Object aDef)
   {
      if (mProcessDef.getNamespace().equals(IAeBPELConstants.BPWS_NAMESPACE_URI))
      {
         getReporter().reportProblem(IAeValidationProblemCodes.BPEL_BPWS_MESSAGE_EXCHANGE_CODE, aKey, aArgs, aDef);
      }
      else
      {
         getReporter().reportProblem(IAeValidationProblemCodes.BPEL_WSBPEL_MESSAGE_EXCHANGE_CODE, aKey, aArgs, aDef);
      }
   }

   /**
    * Gets the path for the scope (or process) that declares the message exchange value
    * and appends the message exchange value to create a fully qualified path for
    * the message exchange.
    * @param aMessageExchange
    * @param aDef - the def here is the receive, onMessage, or reply def
    */
   public static String getFullPathForMessageExchange(String aMessageExchange, AeBaseDef aDef)
   {
      // Search all enclosing scopes.
      //
      for(AeBaseDef current = aDef; current != null; current = current.getParent())
      {
         if (current instanceof IAeMessageExchangesParentDef)
         {
            AeMessageExchangesDef messageExchangesDef = ((IAeMessageExchangesParentDef)current).getMessageExchangesDef();
            if (messageExchangesDef != null && messageExchangesDef.declaresMessageExchange(aMessageExchange))
            {
               return current.getLocationPath() + "/" + aMessageExchange; //$NON-NLS-1$
            }
         }
      }
      return null;
   }
}
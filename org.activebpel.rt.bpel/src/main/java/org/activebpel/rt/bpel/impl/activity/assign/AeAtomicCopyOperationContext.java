//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/AeAtomicCopyOperationContext.java,v 1.4 2006/12/14 22:55:10 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign; 

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAePartnerLink;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.AePartnerLink;
import org.w3c.dom.Document;

/**
 * The context for the copy operation.
 */
public class AeAtomicCopyOperationContext extends AeCopyOperationContext
{
   /** contains copies of the original unmodified variables/partner links in case we need to rollback */
   private Map mRollbackMap;
   
   /**
    * Constructor which takes the abstract Bpel object to delegate through and a rollback map
    * to record changes of variables and partner links.
    * @param aContextBase
    */
   public AeAtomicCopyOperationContext(AeAbstractBpelObject aContextBase)
   {
      super (aContextBase);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperationContext#getVariableForUpdate(java.lang.String, java.lang.String)
    */
   public IAeVariable getVariableForUpdate(String aName, String aPartName)
   {
      IAeVariable variable = super.getVariableForUpdate(aName, aPartName);
      storeVariableForRollback(variable);
      variable.incrementVersionNumber();
      return variable;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperationContext#getPartnerLinkForUpdate(java.lang.String)
    */
   public AePartnerLink getPartnerLinkForUpdate(String aName)
   {
      AePartnerLink plink = getPartnerLink(aName);
      storePartnerLinkForRollback(plink);
      plink.incrementVersionNumber();
      return plink;
   }
   
   /**
    * Rolls back any changes made to variables or partnerLinks by the copy operations 
    */
   public void rollback()
   {
      for (Iterator iter = getRollbackMap().entrySet().iterator(); iter.hasNext();)
      {
         Map.Entry entry = (Map.Entry) iter.next();
         if (entry.getKey() instanceof IAeVariable)
         {
            IAeVariable var = (IAeVariable) entry.getKey();
            var.restore((IAeVariable) entry.getValue());
         }
         else
         {
            IAePartnerLink plink = (IAePartnerLink) entry.getKey();
            Document doc = (Document) entry.getValue();
            try
            {
               plink.getPartnerReference().setReferenceData(doc.getDocumentElement());
            }
            catch (AeBusinessProcessException e)
            {
               // seems unlikely that we'd be unable to deserialize the epr data if we just
               // serialized it moments ago
               e.logError();
            }
         }
      }
      
      clearRollback();
   }
   
   /**
    * Allows ability to clear rollback back.
    */
   public void clearRollback()
   {
      getRollbackMap().clear();
   }
   
   /**
    * Getter for the rollback map
    */
   public Map getRollbackMap()
   {
      // If we were not provided a rollback map, create one now
      if (mRollbackMap == null)
         mRollbackMap = new HashMap();
      
      return mRollbackMap;
   }
   
   /**
    * Makes a clone of the variable for rollback purposes if we haven't already done so.
    * 
    * @param aVariable
    */
   protected void storeVariableForRollback(IAeVariable aVariable)
   {
      if (!getRollbackMap().containsKey(aVariable))
         getRollbackMap().put(aVariable, aVariable.clone());
   }

   /**
    * Makes a copy of the partner link's endpointReference data for rollback purposes if we haven't already done so.
    * 
    * @param aLink
    */
   protected void storePartnerLinkForRollback(IAePartnerLink aLink)
   {
      if (!getRollbackMap().containsKey(aLink))
         getRollbackMap().put(aLink, aLink.getPartnerReference().toDocument());
   }
}
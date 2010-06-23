//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/to/AeToVariableMessagePartWithQuery.java,v 1.5 2006/12/14 22:59:39 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign.to; 

import org.activebpel.rt.bpel.def.activity.support.AeToDef;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.activity.assign.IAeVariableDataWrapper;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Uses a query to select a node within the message part to receive the data 
 */
public class AeToVariableMessagePartWithQuery extends AeToVariableMessagePart
{
   /** query for the message part */
   private String mQuery;
   /** query language for the message part */
   private String mQueryLanguage;
   
   /**
    * Ctor accepts def 
    * @param aToDef
    */
   public AeToVariableMessagePartWithQuery(AeToDef aToDef)
   {
      super(aToDef);
      setQuery(aToDef.getQuery());
      setQueryLanguage(aToDef.getQueryDef().getQueryLanguage());
   }
   
   /**
    * Ctor accepts variable, part, and query
    * 
    * @param aVariable
    * @param aPart
    * @param aQuery
    * @param aQueryLanguage
    */
   public AeToVariableMessagePartWithQuery(String aVariable, String aPart, String aQuery, String aQueryLanguage)
   {
      super(aVariable, aPart);
      setQuery(aQuery);
      setQueryLanguage(aQueryLanguage);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.to.AeToVariableMessagePart#getTarget()
    */
   public Object getTarget() throws AeBpelException
   {
      IAeVariableDataWrapper messagePart = (IAeVariableDataWrapper) super.getTarget();

      // Dealing with query, make sure we're operating against a DOM
      Object partData = messagePart.getValue();

      if (partData instanceof Element)
      {
         Element targetDocElement = (Element) partData;
         
         Node targetNode = AeToQueryRunner.selectValue(getCopyOperation(), getQuery(), targetDocElement);
         return targetNode;
      }
      else
      {
         // TODO (MF) static analysis should catch using a query w/ a simple type for the to-spec
         return null;
      }
   }

   /**
    * @return Returns the query.
    */
   public String getQuery()
   {
      return mQuery;
   }

   /**
    * @param aQuery The query to set.
    */
   public void setQuery(String aQuery)
   {
      mQuery = aQuery;
   }

   /**
    * @return Returns the queryLanguage.
    */
   protected String getQueryLanguage()
   {
      return mQueryLanguage;
   }

   /**
    * @param aQueryLanguage The queryLanguage to set.
    */
   protected void setQueryLanguage(String aQueryLanguage)
   {
      mQueryLanguage = aQueryLanguage;
   }
}
 
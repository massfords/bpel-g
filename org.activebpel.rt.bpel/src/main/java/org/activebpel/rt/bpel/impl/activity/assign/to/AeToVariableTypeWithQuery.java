// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/to/AeToVariableTypeWithQuery.java,v 1.3 2006/09/20 17:01:42 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.impl.activity.assign.to;

import org.activebpel.rt.bpel.def.activity.support.AeToDef;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.w3c.dom.Element;

/**
 * Selects data from a complex type variable using a query.  This is an implicit strategy that
 * does not correspond to an actual to-spec.  It is generated in BPEL 2.0 processes for certain
 * forms of the query to-spec.
 */
public class AeToVariableTypeWithQuery extends AeToBase
{
   /** query for the element */
   private String mQuery;
   /** query language for the type */
   private String mQueryLanguage;
   
   /**
    * Ctor accepts the to def.
    * 
    * @param aToDef
    */
   public AeToVariableTypeWithQuery(AeToDef aToDef)
   {
      this(aToDef.getVariable(), aToDef.getQuery(), aToDef.getQueryDef().getQueryLanguage());
   }
   
   /**
    * Ctor accepts variable type and query
    * 
    * @param aVariable
    * @param aQuery
    * @param aQueryLanguage
    */
   public AeToVariableTypeWithQuery(String aVariable, String aQuery, String aQueryLanguage)
   {
      setVariableName(aVariable);
      setQuery(aQuery);
      setQueryLanguage(aQueryLanguage);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeTo#getTarget()
    */
   public Object getTarget() throws AeBpelException
   {
      AeVariableComplexTypeDataWrapper wrapper = new AeVariableComplexTypeDataWrapper(getVariable());

      // this will initialize the type for us if it's null
      Element element = (Element) wrapper.getValue();
      
      return AeToQueryRunner.selectValue(getCopyOperation(), getQuery(), element);
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

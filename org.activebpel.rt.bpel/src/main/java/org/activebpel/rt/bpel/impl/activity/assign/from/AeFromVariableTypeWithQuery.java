// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/from/AeFromVariableTypeWithQuery.java,v 1.3 2007/10/03 12:39:51 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.impl.activity.assign.from;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.xpath.AeXPathHelper;

/**
 * Handles selecting a piece of an element using an XPath query.
 */
public class AeFromVariableTypeWithQuery extends AeFromVariableType
{
   /** query for the element */
   private String mQuery;

   /**
    * Ctor accepts def
    *
    * @param aDef
    */
   public AeFromVariableTypeWithQuery(AeFromDef aDef)
   {
      super(aDef);
      setQuery(aDef.getQuery());
   }

   /**
    * Ctor accepts variable namd and query
    * @param aVariableName
    * @param aQuery
    */
   public AeFromVariableTypeWithQuery(String aVariableName, String aQuery)
   {
      super(aVariableName);
      setQuery(aQuery);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.from.AeFromVariableElement#getFromData()
    */
   public Object getFromData() throws AeBusinessProcessException
   {
      Object data = super.getFromData();

      data = getCopyOperation().getContext().executeQuery(getQuery(), data, false);
      data = AeXPathHelper.getInstance(getCopyOperation().getContext().getBPELNamespace()).unwrapXPathValue(data);

      return data;
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
}

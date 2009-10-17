// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Models the validate activity introduced in WS-BPEL 2.0 (in section 8.1 of the spec).
 */
public class AeActivityValidateDef extends AeActivityDef
{
   /** The activity's 'variables' attribute. */
   private List mVariables = new ArrayList();

   /**
    * Default c'tor.
    */
   public AeActivityValidateDef()
   {
      super();
   }

   /**
    * @return Returns the variables.
    */
   public String getVariablesAsString()
   {
      StringBuffer buff = new StringBuffer();
      for (Iterator iter = getVariables(); iter.hasNext(); )
      {
         String variable = (String) iter.next();
         buff.append(variable);
         buff.append(" "); //$NON-NLS-1$
      }
      return buff.toString().trim();
   }

   /**
    * Gets an iterator over the variables.
    */
   public Iterator getVariables()
   {
      return mVariables.iterator();
   }
   
   /**
    * Returns the number of variables being validated
    */
   public int getVariablesCount()
   {
      return mVariables.size();
   }

   /**
    * @param aVariablesString The variables to set.
    */
   public void setVariables(String aVariablesString)
   {
      parseVariablesString(aVariablesString);
   }
   
   /**
    * Parses the variables string value into a list of variables.
    * 
    * @param aVariablesString
    */
   private void parseVariablesString(String aVariablesString)
   {
      for (StringTokenizer tokenizer = new StringTokenizer(aVariablesString); tokenizer.hasMoreTokens(); )
      {
         String variable = tokenizer.nextToken();
         addVariable(variable);
      }
   }

   /**
    * Adds a variable to the list of variables associated with this activity.
    * @param aVariableName
    */
   public void addVariable(String aVariableName)
   {
      mVariables.add(aVariableName);
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}

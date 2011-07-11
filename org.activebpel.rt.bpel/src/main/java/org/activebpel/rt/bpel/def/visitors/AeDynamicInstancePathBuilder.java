//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeDynamicInstancePathBuilder.java,v 1.2 2008/02/17 21:37:08 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors; 

import org.activebpel.rt.bpel.def.io.readers.AeBpelLocationPathVisitor;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.IAePathSegmentBuilder;

/**
 * Builds paths for dynamic scope instances that includes an additional
 * predicate on the dynamic scope to include its instance value within its path. 
 */
public class AeDynamicInstancePathBuilder extends AeBpelLocationPathVisitor
{
   /** our root node that gets the special instance path */
   private AeBaseXmlDef mInstanceDef;
   
   /** value used to construct the path information */
   private int mInstanceValue;
   
   /**
    * Ctor
    * @param aSegmentBuilder
    * @param aInstanceDef
    */
   public AeDynamicInstancePathBuilder(IAePathSegmentBuilder aSegmentBuilder, AeBaseXmlDef aInstanceDef)
   {
      super(aSegmentBuilder);
      mInstanceDef = aInstanceDef;
   }
   
   /**
    * Returns the root instance def.
    */
   protected AeBaseXmlDef getInstanceDef()
   {
      return mInstanceDef;
   }

   /**
    * @see org.activebpel.rt.xml.def.visitors.AeLocationPathVisitor#createUniquePath(org.activebpel.rt.xml.def.AeBaseXmlDef, java.lang.String)
    */
   protected String createUniquePath(AeBaseXmlDef aDef, String aAppendPath)
   {
      if (aDef == getInstanceDef())
      {
         StringBuffer buffer = new StringBuffer(aAppendPath);
         appendInstancePredicate(buffer, getInstanceValue()); 
         return super.createUniquePath(aDef, buffer.toString());
      }
      else
      {
         return super.createUniquePath(aDef, aAppendPath);
      }
   }

   /**
    * Setter for the counter
    * 
    * @param aCounter
    */
   public void setInstanceValue(int aCounter)
   {
      mInstanceValue = aCounter;
   }

   /**
    * Getter for the counter
    */
   public int getInstanceValue()
   {
      return mInstanceValue;
   }

   /**
    * Appends the scope instance counter as predicate.
    * @param aBuffer location path buffer
    * @param aInstanceCount current instance counter value.
    */
   public static void appendInstancePredicate(StringBuffer aBuffer, int aInstanceCount)
   {
      //
      // Note: The Admin console (for the process viewer)'s javascript  does some
      // pattern matching to determine if an activity selected in the outline (tree) view
      // is of a forEach parallel instance. The java script uses the pattern used below
      // e.g: [instance()=N]
      // If the pattern of the predicate is changed, then it must also be reflected in the
      // admin console's ae_graphview.js java script file.
      //
      
      aBuffer.append("[instance()="); //$NON-NLS-1$
      aBuffer.append(String.valueOf(aInstanceCount));
      aBuffer.append(']');      
   }
} 
// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/AeBPWSUtil.java,v 1.3 2006/09/07 15:06:26 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.io;

import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.activity.support.AeJoinConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeSourceDef;
import org.activebpel.rt.bpel.def.activity.support.AeSourcesDef;
import org.activebpel.rt.bpel.def.activity.support.AeTargetDef;
import org.activebpel.rt.bpel.def.activity.support.AeTargetsDef;
import org.activebpel.rt.util.AeUtil;

/**
 * A Util class that has some convenience methods for working with the def layer for BPEl4WS 1.1.
 * 
 * TODO this class can probably go away (with its functionality moving to the BPEL4WS 1.1 reader visitor) once bpep has been updated to use 2.0 compatible visual models
 */
public class AeBPWSUtil
{
   /**
    * Sets the join condition on the activity by first finding/creating the targets container, then
    * finding/creating the join condition def, then setting the expression on the join condition def.
    * 
    * @param aJoinCondition
    * @param aJoinConditionLang
    * @param aActivityDef
    */
   public static void setJoinConditionOnActivity(String aJoinCondition, String aJoinConditionLang, AeActivityDef aActivityDef)
   {
      if (AeUtil.notNullOrEmpty(aJoinCondition))
      {
         AeTargetsDef targetsDef = aActivityDef.getTargetsDef();
         if (targetsDef == null)
         {
            targetsDef = new AeTargetsDef();
            aActivityDef.setTargetsDef(targetsDef);
         }
   
         AeJoinConditionDef joinConditionDef = targetsDef.getJoinConditionDef();
         if (joinConditionDef == null)
         {
            joinConditionDef = new AeJoinConditionDef();
            targetsDef.setJoinConditionDef(joinConditionDef);
         }
         joinConditionDef.setExpression(aJoinCondition);
         joinConditionDef.setExpressionLanguage(aJoinConditionLang);
      }
   }

   /**
    * Adds the source def to the given activity.
    * 
    * @param aSourceDef
    * @param aActivityDef
    */
   public static void addSourceToActivity(AeSourceDef aSourceDef, AeActivityDef aActivityDef)
   {
      AeSourcesDef sourcesDef = aActivityDef.getSourcesDef();
      if (sourcesDef == null)
      {
         sourcesDef = new AeSourcesDef();
         aActivityDef.setSourcesDef(sourcesDef);
      }
      sourcesDef.addSourceDef(aSourceDef);
   }

   /**
    * Adds the target def to the given activity.
    * 
    * @param aTargetDef
    * @param aActivityDef
    */
   public static void addTargetToActivity(AeTargetDef aTargetDef, AeActivityDef aActivityDef)
   {
      AeTargetsDef targetsDef = aActivityDef.getTargetsDef();
      if (targetsDef == null)
      {
         targetsDef = new AeTargetsDef();
         aActivityDef.setTargetsDef(targetsDef);
      }
      targetsDef.addTargetDef(aTargetDef);
   }
}

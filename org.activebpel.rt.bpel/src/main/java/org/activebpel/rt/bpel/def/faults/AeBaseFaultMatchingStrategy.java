//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/faults/AeBaseFaultMatchingStrategy.java,v 1.5 2008/01/11 19:31:16 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.faults;

import java.util.Iterator;

import org.activebpel.rt.bpel.IAeFaultTypeInfo;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;

/**
 * Base impl of the fault matching strategy. This interface provides the common framework for matching thrown
 * faults to catch or catchAll fault handlers.
 */
public abstract class AeBaseFaultMatchingStrategy implements IAeFaultMatchingStrategy
{
  /* ---------------------------------------------------------------------------------------------
   * This table provides an overview of the rules used for selecting a match in this AeBaseFaultMatchingStrategy class.
   *
   *                        best      catch has   catch            
   * Rule                   match     faultName   var type           faultData
   * --------------------------------------------------------------------------------------------------------------------
   * AeFaultNameOnly        yes       yes         none                none
   * AeFaultNameAndData     yes       yes         message             message
   * AeVariableOnly         no        no          message             message
   * --------------------------------------------------------------------------------------------------------------------
   */
   
   /**
    * Base class for matching rules. Provides the best match flag.
    */
   protected static abstract class AeFaultMatchingRule implements IAeFaultMatchingRule
   {
      /** 
       * Priority order of this rule. 
       */
      private int mPriority;

      /**
       * Creates the rule w/ the best match flag
       */
      protected AeFaultMatchingRule()
      {
      }

      /**
       * @param aPriority
       */
      public void setPriority(int aPriority)
      {
         mPriority = aPriority;
      }

      /**
       * Get the priority of this rule.
       */
      public int getPriority()
      {
         return mPriority;
      }
   }

   /**
    * Matches a fault w/o data to a catch with a matching fault name
    */
   protected static class AeFaultNameOnly extends AeFaultMatchingRule
   {
      /**
       * @see org.activebpel.rt.bpel.def.faults.IAeFaultMatchingRule#getMatch( org.activebpel.rt.wsdl.IAeContextWSDLProvider, 
       * org.activebpel.rt.bpel.def.faults.IAeCatch, org.activebpel.rt.bpel.IAeFaultTypeInfo)
       */
      public IAeMatch getMatch(IAeContextWSDLProvider aProvider, IAeCatch aCatch, IAeFaultTypeInfo aFault)
      {
         boolean matched = !aFault.hasData() && !aCatch.hasFaultVariable()
               && AeUtil.compareObjects(aFault.getFaultName(), aCatch.getFaultName());

         IAeMatch match = null;
         if ( matched )
         {
            match = new AeMatch(true, getPriority());
         }

         return match;
      }
   }

   /**
    * Matches a fault w/ data to a catch with a matching fault name and data
    */
   protected static class AeFaultNameAndData extends AeFaultMatchingRule
   {
      /**
       * @see org.activebpel.rt.bpel.def.faults.IAeFaultMatchingRule#getMatch( org.activebpel.rt.wsdl.IAeContextWSDLProvider, 
       * org.activebpel.rt.bpel.def.faults.IAeCatch, org.activebpel.rt.bpel.IAeFaultTypeInfo)
       */
      public IAeMatch getMatch(IAeContextWSDLProvider aProvider, IAeCatch aCatch, IAeFaultTypeInfo aFault)
      {
         boolean matched = false;
         if ( aFault.hasMessageData() && aCatch.hasFaultVariable() )
         {
            matched = AeUtil.compareObjects(aCatch.getFaultMessageType(), aFault.getMessageType())
                  && AeUtil.compareObjects(aCatch.getFaultName(), aFault.getFaultName());
         }

         IAeMatch match = null;
         if ( matched )
         {
            match = new AeMatch(true, getPriority());
         }
         return match;
      }
   }

   /**
    * Matches a fault with data to a catch w/o a name but with matching data
    */
   protected static class AeVariableOnly extends AeFaultMatchingRule
   {
      /**
       * @see org.activebpel.rt.bpel.def.faults.IAeFaultMatchingRule#getMatch( org.activebpel.rt.wsdl.IAeContextWSDLProvider, 
       * org.activebpel.rt.bpel.def.faults.IAeCatch, org.activebpel.rt.bpel.IAeFaultTypeInfo)
       */
      public IAeMatch getMatch(IAeContextWSDLProvider aProvider, IAeCatch aCatch, IAeFaultTypeInfo aFault)
      {
         boolean matched = false;
         if ( aFault.hasMessageData() && aCatch.hasFaultVariable() && aCatch.getFaultName() == null )
         {
            matched = AeUtil.compareObjects(aFault.getMessageType(), aCatch.getFaultMessageType());
         }

         IAeMatch match = null;
         if ( matched )
         {
            match = new AeMatch(false, getPriority());
         }

         return match;

      }
   }

   /**
    * Gets all of the rules for the fault matching strategy.
    */
   protected abstract IAeFaultMatchingRule[] getRules();

   /**
    * @see org.activebpel.rt.bpel.def.faults.IAeFaultMatchingStrategy#selectMatchingCatch(org.activebpel.rt.wsdl.IAeContextWSDLProvider, 
    *   java.util.Iterator, org.activebpel.rt.bpel.IAeFaultTypeInfo)
    */
   public IAeCatch selectMatchingCatch(IAeContextWSDLProvider aProvider, Iterator aIterOfCatches, IAeFaultTypeInfo aFault)
   {
      // The catch that we've matched
      IAeCatch matched = null;
      // A catch that has been matched by a rule and may become the one that we're going to match on unless a better one is found
      IAeMatch previousMatch = null;

      // loop over all of the available catches
      while (aIterOfCatches.hasNext())
      {
         // the current katch we're analyzing
         IAeCatch katch = (IAeCatch)aIterOfCatches.next();
         // array of rules to match against
         IAeFaultMatchingRule[] rules = getRules();
         for (int i = 0; i < rules.length; i++)
         {
            IAeFaultMatchingRule rule = rules[i];
            IAeMatch match = rule.getMatch(aProvider, katch, aFault);
            // if the rule produced a match, then see if it's the best match or perhaps better than a previous match
            if ( match != null )
            {
               // if the rule is a best match, then we won't encounter a better match so stop looping
               if ( match.isBestMatch() )
               {
                  return katch;
               }
               // if there is no previous match, then record the match as our current match which may end up 
               // being what we match on if no better match is found
               if ( previousMatch == null )
               {
                  previousMatch = match;
                  matched = katch;
               }
               // if there is a previous match, then replace it if the new match is a better 
               // match than the previous match.
               // One match is a better than another if it has a higher priority OR if it's the same priority
               // but the new match is closer to an SG head than the previous match. This logic is encapsulated
               // within the match impl classes.
               else if ( match.isBetterMatchThan(previousMatch) )
               {
                  previousMatch = match;
                  matched = katch;
               }
            }
         }
      }
      return matched;
   }
}
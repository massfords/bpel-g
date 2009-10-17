// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/readers/def/AeSpecStrategyKey.java,v 1.2 2007/11/10 03:36:08 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.io.readers.def;

/**
 * A simple key that is used to indicate the strategy to use for the From/To portion of a 
 * copy operation.
 */
public class AeSpecStrategyKey
{
   /** The name of the strategy. */
   private String mStrategyName;
   /** Optional arguments that will get passed to the strategy when it is created. */
   private Object [] mStrategyArguments;

   /**
    * Constructs a strategy key with the given name.
    * 
    * @param aStrategyName
    */
   public AeSpecStrategyKey(String aStrategyName)
   {
      this(aStrategyName, null);
   }
   
   /**
    * Constructs a strategy key with the given name and arguments.
    * 
    * @param aStrategyName
    * @param aStrategyArguments
    */
   protected AeSpecStrategyKey(String aStrategyName, Object [] aStrategyArguments)
   {
      setStrategyName(aStrategyName);
      setStrategyArguments(aStrategyArguments);
   }
   
   /**
    * Returns true if the key contains some arguments.
    */
   public boolean hasArguments()
   {
      return getStrategyArguments() != null;
   }

   /**
    * @return Returns the strategyName.
    */
   public String getStrategyName()
   {
      return mStrategyName;
   }

   /**
    * @param aStrategyName The strategyName to set.
    */
   protected void setStrategyName(String aStrategyName)
   {
      mStrategyName = aStrategyName;
   }

   /**
    * @return Returns the strategyArguments.
    */
   public Object[] getStrategyArguments()
   {
      return mStrategyArguments;
   }

   /**
    * @param aStrategyArguments The strategyArguments to set.
    */
   protected void setStrategyArguments(Object[] aStrategyArguments)
   {
      mStrategyArguments = aStrategyArguments;
   }
}

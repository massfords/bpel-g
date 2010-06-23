// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/function/AeUtilityFunctionContext.java,v 1.2 2006/10/25 16:08:07 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.function.AeFunctionCallException;
import org.activebpel.rt.bpel.function.AeUnresolvableException;
import org.activebpel.rt.bpel.function.IAeFunction;
import org.activebpel.rt.bpel.function.IAeFunctionContext;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;

/**
 * Implements a Jaxen function context that provides a few simple utility
 * functions.
 */
public class AeUtilityFunctionContext implements IAeFunctionContext
{
   private static final String FUNCTION_PRINTLN = "println"; //$NON-NLS-1$
   private static final String FUNCTION_READLN  = "readln"; //$NON-NLS-1$
   private static final String FUNCTION_SLEEP   = "sleep"; //$NON-NLS-1$

   /** Serializes execution of the readln function. */
   private static final Object mReadlnMutex = new Object(); 

   /** <code>Map</code> from function names to function implementations. */
   private final Map mFunctionMap = new HashMap();

   /**
    * Constructs the function context.
    */
   public AeUtilityFunctionContext()
   {
      mFunctionMap.put(FUNCTION_PRINTLN, new AePrintlnFunction());
      mFunctionMap.put(FUNCTION_READLN , new AeReadlnFunction());
      mFunctionMap.put(FUNCTION_SLEEP  , new AeSleepFunction());
   }

   /**
    * Convenience routine that concatenates the items in the specified list to
    * a <code>String</code>.
    */
   protected static String concatenate(List aList)
   {
      StringBuffer buffer = new StringBuffer();
      Iterator i = aList.iterator();

      if (i.hasNext())
      {
         // Get the first item.
         buffer.append(i.next());

         // Concatenate succeeding items separated by spaces.
         while (i.hasNext())
         {
            buffer.append(' ');
            buffer.append(i.next());
         }
      }

      return buffer.toString();
   }

   /**
    * @see org.activebpel.rt.bpel.function.IAeFunctionContext#getFunction(java.lang.String)
    */
   public IAeFunction getFunction(String aLocalName) throws AeUnresolvableException
   {
      IAeFunction function = (IAeFunction) mFunctionMap.get(aLocalName.toLowerCase());
      if (function == null)
      {
         throw new AeUnresolvableException(
               AeMessages.getString("AeUtilityFunctionContext.ERROR_0") + aLocalName); //$NON-NLS-1$ 
      }

      return function;
   }

   /**
    * Implements a simple Jaxen function that writes its arguments to the Java console.
    */
   protected static class AePrintlnFunction implements IAeFunction
   {
      /**
       * @see org.jaxen.Function#call(org.jaxen.Context, java.util.List)
       */
      public Object call(IAeFunctionExecutionContext aContext, List aArgs) throws AeFunctionCallException
      {
         String output = concatenate(aArgs);

         System.out.println("[PRINTLN] " + output); //$NON-NLS-1$

         // Return the concatenated arguments for kicks.
         return output;
      }
   }


   /**
    * Implements a simple Jaxen function that prompts the user for console
    * input and returns the user's input.
    */
   protected static class AeReadlnFunction implements IAeFunction
   {
      /**
       * @see org.jaxen.Function#call(org.jaxen.Context, java.util.List)
       */
      public Object call(IAeFunctionExecutionContext aContext, List aArgs) throws AeFunctionCallException
      {
         // Synchronize on a mutex object to prevent concurrent console reads.
         synchronized (mReadlnMutex)
         {
            String prompt = concatenate(aArgs);
            if (!prompt.endsWith(" ")) //$NON-NLS-1$
            {
               prompt = prompt + " "; //$NON-NLS-1$
            }
   
            System.out.print("[READLN] " + prompt); //$NON-NLS-1$
   
            try
            {
               // Return the next line typed by the console user.
               return new BufferedReader(new InputStreamReader(System.in)).readLine();
            }
            catch (IOException e)
            {
               throw new AeFunctionCallException(e);
            }
         }
      }
   }

   /**
    * Implements a simple Jaxen function that puts the current thread to sleep.
    */
   protected static class AeSleepFunction implements IAeFunction
   {
      /**
       * @see org.jaxen.Function#call(org.jaxen.Context, java.util.List)
       */
      public Object call(IAeFunctionExecutionContext aContext, List aArgs) throws AeFunctionCallException
      {
         if (aArgs.size() > 0)
         {
            try
            {
               long millis = Double.valueOf(aArgs.get(0).toString()).longValue(); 
               System.out.println("[SLEEP] " + millis + " milliseconds"); //$NON-NLS-1$ //$NON-NLS-2$
               Thread.sleep(millis);
            }
            catch (Exception e)
            {
               throw new AeFunctionCallException(e);
            }
         }

         // Return the 2nd argument, if it exists.
         return (aArgs.size() > 1) ? aArgs.get(1) : null;
      }
   }
}

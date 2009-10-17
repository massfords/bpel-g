// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/support/AeCorrelationDef.java,v 1.15 2006/10/26 14:01:36 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.activity.support;

import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/** Holds individual correlation elements */
public class AeCorrelationDef extends AeBaseDef
{
   // Initiate possible values
   public static final String INITIATE_YES = "yes"; //$NON-NLS-1$
   public static final String INITIATE_NO = "no"; //$NON-NLS-1$
   public static final String INITIATE_JOIN = "join"; //$NON-NLS-1$
   
   /** Correlation set name from set attribute. */
   private String mCorrelationSetName;
   /** Correlation patter (out-in, in-out, both) */
   private AeCorrelationPatternType mPattern;
   /** Value of the initiate attribute. */
   private String mInitiate;

   /**
    * Default constructor
    */
   public AeCorrelationDef()
   {
      super();
   }

   /**
    * Returns the initiate value.
    */
   public String getInitiate()
   {
      return mInitiate;
   }

   /**
    * Sets the initiate value.
    * 
    * @param aInitiation initiation to set
    */
   public void setInitiate(String aInitiation)
   {
      mInitiate = aInitiation;
   }

   /**
    * Returns the correlation pattern.
    * 
    * @return the correlation pattern
    */
   public AeCorrelationPatternType getPattern()
   {
      return mPattern;
   }

   /**
    * Sets the correlation pattern
    * 
    * @param aPattern pattern to set
    */
   public void setPattern(AeCorrelationPatternType aPattern)
   {
      mPattern = aPattern;
   }

   /**
    * Returns the correlation set name, which identifies the associated
    * correlation set.
    * @return the correlation set attribute
    */
   public String getCorrelationSetName()
   {
      return mCorrelationSetName;
   }

   /**
    * Sets the correlation set attribute.
    * 
    * @param aSet the name of the correlation set
    */
   public void setCorrelationSetName(String aSet)
   {
      mCorrelationSetName = aSet;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }

   /**
    * Convenience method that returns true if the pattern is either 'response' 
    * or 'request-response'.
    * Inbound data is what the invoke receives as a response from the
    * outside world. As per the spec, the inbound data is extracted from the
    * invoke's outputVariable.
    */
   public boolean isResponseDataUsedForCorrelation()
   {
      return getPattern() != null && getPattern().isResponseDataUsed();
   }

   /**
    * Convenience method that returns true if the pattern is either 'request' or 
    * 'request-response'.
    * Outbound data is what the invoke sends along with its invocation
    * of the web service. As per the spec, the outbound data is extracted from
    * the invoke's inputVariable.
    */
   public boolean isRequestDataUsedForCorrelation()
   {
      return getPattern() != null && getPattern().isRequestDataUsed();
   }

   /**
    * Convenience method for determining if initiate is set to 'yes' or 'join'.
    */
   public boolean isInitiate()
   {
      return INITIATE_JOIN.equals(getInitiate()) || INITIATE_YES.equals(getInitiate());
   }
   
   /**
    * Convenience method for determining if a given correlation set should be 
    * initialized by the time the activity goes to use it. Any usage where the
    * initiate flag is set to no or not set at all assumes that the correlation
    * set has already been initialized through the execution of some other 
    * activity.
    */
   public boolean shouldAlreadyBeInitiated()
   {
      return INITIATE_NO.equals(getInitiate());
   }
   
   /**
    * Type for the correlation pattern.
    */
   public static class AeCorrelationPatternType
   {
      /** value of the pattern */
      private String mValue;
      
      /** invoke will initiate/validate correlation set with its request messages */
      public static final AeCorrelationPatternType REQUEST = new AeCorrelationPatternType("request"); //$NON-NLS-1$
      /** invoke will initiate/validate correlation set with its response messages */
      public static final AeCorrelationPatternType RESPONSE = new AeCorrelationPatternType("response"); //$NON-NLS-1$
      /** invoke will initiate/validate correlation set with its request and response messages */
      public static final AeCorrelationPatternType REQUEST_RESPONSE = new AeCorrelationPatternType("request-response"); //$NON-NLS-1$
      
      /**
       * Private ctor to force use of constants
       * @param aValue
       */
      protected AeCorrelationPatternType(String aValue)
      {
         mValue = aValue;
      }
      
      /**
       * @see java.lang.Object#toString()
       */
      public String toString()
      {
         return mValue;
      }
      
      /**
       * Convenience method to determine if the pattern uses request data for 
       * correlation
       */
      public boolean isRequestDataUsed()
      {
         return this == REQUEST || this == REQUEST_RESPONSE;
      }
      
      /**
       * Convenience method to determine if the pattern uses response data for 
       * correlation
       */
      public boolean isResponseDataUsed()
      {
         return this == RESPONSE || this == REQUEST_RESPONSE;
      }
   }
   
   /**
    * Type for an invalid correlation pattern. This is simply a placeholder for 
    * the string value so we can report the invalid pattern value during static 
    * analysis.
    */
   public static class AeInvalidCorrelationPatternType extends AeCorrelationPatternType
   {
      /**
       * Ctor
       * @param aValue
       */
      public AeInvalidCorrelationPatternType(String aValue)
      {
         super(aValue);
      }
   }
}


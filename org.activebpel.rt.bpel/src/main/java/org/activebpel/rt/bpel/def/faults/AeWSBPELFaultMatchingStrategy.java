//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/faults/AeWSBPELFaultMatchingStrategy.java,v 1.6.4.1 2008/04/21 16:09:42 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.faults;

import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.IAeFaultTypeInfo;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;

/**
 * Fault matching strategy for WS-BPEL 2.0
 */
public class AeWSBPELFaultMatchingStrategy extends AeBaseFaultMatchingStrategy
{
   /* ---------------------------------------------------------------------------------------------
    * This table provides an overview of the rules used for selecting a
    * fault handler for WS-BPEL 2.0
    * 
    * best match = a best match flag on the rule means that if the rule matches a handler then the
    *              search will stop since it found the best match. There are some rules below that
    *              are not best matches. For example, a fault thrown with a single message element part 
    *              will match a catch with the same name and an element variable. However, the best
    *              match for this scenario is the exact match of the data to a catch with the same
    *              message variable.
    *              
    *                                   best      catch has    catch            
    * Rule                              match     faultName    var type             faultData
    * --------------------------------------------------------------------------------------------------------------------
    * AeFaultNameOnly                     yes       yes         none                none 
    * AeFaultNameAndData_WSBPEL           yes       yes         msg or element      msg or element    
    * AeFaultNameAndData_WSBPEL_SG        no        yes         element             element  
    * AeFaultNameAndSingleElement         no        yes         element             single part element message                        
    * AeFaultNameAndSingleElement_SG      no        yes         element             single part element message                        
    * AeFaultNameOnlyIgnoreData           no        yes         none                none 
    * AeVariableOnly_WSBPEL               no        no          msg or element      msg or element    
    * AeVariableOnly_WSBPEL_SG            no        no          element             element    
    * AeVariableOnlyAndSingleElement      no        no          element             single part element message  
    * AeVariableOnlyAndSingleElement_SG   no        no          element             single part element message
    * ------------------------------------------------------------------------------------------
    * 
    *  Notice that there are no duplicate rows above. In the case where multiple &lt;catch&gt; elements
    *  are matched to a fault, there will only be one "best" match. There should never be multiple
    *  &lt;catch&gt; elements that match to the same fault using the same rule. This should be detected during
    *  static analysis and rejected.
    *  
    */

   /**
    * The order of these rules is important. If more than one &lt;catch&gt; is matched to a fault, then the rule
    * with the higher priority takes precedence.
    */
   private static final IAeFaultMatchingRule[] RULES = {
   /*
    * 0.    If there is a &lt;catch&gt; construct with a matching faultName value that does not specify a faultVariable
    *       attribute then the fault is passed to the identified catch activity.
    */
   new AeFaultNameOnly(),

   /*
    * 1.    If there is a &lt;catch&gt; construct with a matching faultName value that has a faultVariable whose type
    *       matches the type of the fault data then the fault is passed to the identified &lt;catch&gt; construct.
    */
   new AeFaultNameAndData_WSBPEL(),

   /*
    * 2.    If there is a &lt;catch&gt; construct with a matching faultName value that has a faultVariable whose type
    *       includes the type of the fault data in a substitution group then the fault is passed to the identified 
    *       &lt;catch&gt; construct.
    */   
   new AeFaultNameAndData_WSBPEL_SG(),

   /*
    * 3.    Otherwise if the fault data is a WSDL message type where the message contains a single part defined
    *       by an element and there exists a &lt;catch&gt; construct with a matching faultName value that has a
    *       faultVariable whose associated faultElement's QName matches the QName of the element used to define the
    *       part then the fault is passed to the identified &lt;catch&gt; construct with the faultVariable initialized to
    *       the value in the single part's element.
    */
   new AeFaultNameAndSingleElement(),
   
   /*
    * 4.    Otherwise if the fault data is a WSDL message type where the message contains a single part defined
    *       by an element and there exists a &lt;catch&gt; construct with a matching faultName value that has a
    *       faultVariable whose associated faultElement's QName includes the QName of the element used to define the
    *       part in a substitution group then the fault is passed to the identified &lt;catch&gt; construct with the 
    *       faultVariable initialized to the value in the single part's element.
    */
   new AeFaultNameAndSingleElement_SG(),

   /*
    * 5.    Otherwise if there is a &lt;catch&gt; construct with a matching faultName value that does not specify a
    *       faultVariable attribute then the fault is passed to the identified &lt;catch&gt; construct. Note that in this
    *       case the fault value will not be available from within the fault handler but will be available to the
    *       &lt;rethrow&gt; activity.
    */
   new AeFaultNameOnlyIgnoreData(),

   /*
    * 6.    Otherwise if there is a &lt;catch&gt; construct without a faultName attribute that has a faultVariable
    *       whose type matches the type of the fault data then the fault is passed to the identified &lt;catch&gt;
    *       construct.
    */
   new AeVariableOnly_WSBPEL(),
   
   /*
    * 7.    Otherwise if there is a &lt;catch&gt; construct without a faultName attribute that has a faultVariable
    *       whose type includes the type of the fault data in a substitution group then the fault is passed to 
    *       the identified &lt;catch&gt; construct.
    */
   new AeVariableOnly_WSBPEL_SG(),

   /*
    * 8.    Otherwise if the fault data is a WSDL message type where the message contains a single part defined
    *       by an element and there exists a &lt;catch&gt; construct without a faultName attribute that has a
    *       faultVariable whose associated faultElement's QName matches the QName of the element used to define the
    *       part then the fault is passed to the identified &lt;catch&gt; construct with the faultVariable initialized to
    *       the value in the single part's element.
    */
   new AeVariableOnlyAndSingleElement(), 
   
   /*
    * 9.    Otherwise if the fault data is a WSDL message type where the message contains a single part defined
    *       by an element and there exists a &lt;catch&gt; construct without a faultName attribute that has a
    *       faultVariable whose associated faultElement's QName includes the QName of the element used to define the
    *       part in a substitution group then the fault is passed to the identified &lt;catch&gt; construct with the 
    *       faultVariable initialized to the value in the single part's element.
    */
   new AeVariableOnlyAndSingleElement_SG(), 
   };

   /**
    * Initialize the rule's priority.  
    */
   static
   {
      for (int i = 0; i < RULES.length; i++)
      {
         RULES[i].setPriority(i);
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.faults.AeBaseFaultMatchingStrategy#getRules()
    */
   protected IAeFaultMatchingRule[] getRules()
   {
      return RULES;
   }
   
   
   /**
    * Matches if the fault name and data are exact matches. This is the same as the BPEL 1.1 rule except that
    * it also checks for element data.
    */
   protected static class AeFaultNameAndData_WSBPEL extends AeFaultNameAndData
   {
      /**
       * @see org.activebpel.rt.bpel.def.faults.IAeFaultMatchingRule#getMatch( org.activebpel.rt.wsdl.IAeContextWSDLProvider, 
       * org.activebpel.rt.bpel.def.faults.IAeCatch, org.activebpel.rt.bpel.IAeFaultTypeInfo)
       */
      public IAeMatch getMatch(IAeContextWSDLProvider aProvider, IAeCatch aCatch, IAeFaultTypeInfo aFault)
      {
         IAeMatch match = super.getMatch(aProvider, aCatch, aFault);
         if ( match == null )
         {
            // check for element type match
            if ( aFault.hasElementData() && aCatch.hasFaultVariable() )
            {
               boolean matched = AeUtil.compareObjects(aCatch.getFaultElementName(), aFault.getElementType())
                     && AeUtil.compareObjects(aCatch.getFaultName(), aFault.getFaultName());
               if ( matched )
               {
                  match = new AeMatch(true, getPriority());
               }
            }
         }
         return match;
      }
   }
  
   /**
    * Matches if the fault has element data that is a member of a substituion group headed by the 
    * element in the catch varaible. Otherwise similar to above AeFaultNameAndData_WSBPEL.
    * This is not a best match rule.
    */
   protected static class AeFaultNameAndData_WSBPEL_SG extends AeFaultMatchingRule
   {
      /**
       * @see org.activebpel.rt.bpel.def.faults.IAeFaultMatchingRule#getMatch( org.activebpel.rt.wsdl.IAeContextWSDLProvider, 
       * org.activebpel.rt.bpel.def.faults.IAeCatch, org.activebpel.rt.bpel.IAeFaultTypeInfo)
       */
      public IAeMatch getMatch(IAeContextWSDLProvider aProvider, IAeCatch aCatch, IAeFaultTypeInfo aFault)
      {
         IAeMatch match = null;
         // check for element type match
         if ( aFault.hasElementData() && aCatch.hasFaultVariable() 
               && AeUtil.compareObjects(aCatch.getFaultName(), aFault.getFaultName()))
         {
            int sgLevel = AeWSDLDefHelper.getSubstitutionGroupLevel(aProvider, aCatch.getFaultElementName(), aFault.getElementType());
            if ( sgLevel > 0 )
            {
               match = new AeMatch(false, getPriority());
               match.setSGLevel(sgLevel);
            }
         }
         return match;
      }
   }
   
   /**
    * Matches if the catch has the same name as the fault and the fault data is a single element message that matches the element variable for the
    * catch. This is not a best match rule.
    */
   protected static class AeFaultNameAndSingleElement extends AeFaultMatchingRule
   {
      /**
       * @see org.activebpel.rt.bpel.def.faults.IAeFaultMatchingRule#getMatch( org.activebpel.rt.wsdl.IAeContextWSDLProvider, 
       * org.activebpel.rt.bpel.def.faults.IAeCatch, org.activebpel.rt.bpel.IAeFaultTypeInfo)
       */
      public IAeMatch getMatch(IAeContextWSDLProvider aProvider, IAeCatch aCatch, IAeFaultTypeInfo aFault)
      {
         boolean matched = false;
         if ( aFault.hasMessageData() && aCatch.hasFaultVariable() && aCatch.getFaultElementName() != null
               && AeUtil.compareObjects(aFault.getFaultName(), aCatch.getFaultName()) )
         {
            matched = AeUtil.compareObjects(aFault.getSinglePartElementType(), aCatch.getFaultElementName());
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
    * Matches if the fault has message data that is a single part element. The element is a memeber of
    * a substitution group headed by the element in the catch varaible. Otherwise similar to above 
    * AeFaultNameAndSingleElement.
    * This is not a best match rule.
    */
   protected static class AeFaultNameAndSingleElement_SG extends AeFaultMatchingRule
   {
      /**
       * @see org.activebpel.rt.bpel.def.faults.IAeFaultMatchingRule#getMatch( org.activebpel.rt.wsdl.IAeContextWSDLProvider, 
       * org.activebpel.rt.bpel.def.faults.IAeCatch, org.activebpel.rt.bpel.IAeFaultTypeInfo)
       */
      public IAeMatch getMatch(IAeContextWSDLProvider aProvider, IAeCatch aCatch, IAeFaultTypeInfo aFault)
      {
         IAeMatch match = null;         
         if ( aFault.hasMessageData() && aCatch.hasFaultVariable() && aCatch.getFaultElementName() != null
               && AeUtil.compareObjects(aFault.getFaultName(), aCatch.getFaultName()) )
         {
            int sgLevel = AeWSDLDefHelper.getSubstitutionGroupLevel(aProvider, aCatch.getFaultElementName(), aFault.getSinglePartElementType());               
            if ( sgLevel > 0 )
            {
               match = new AeMatch(false, getPriority());
               match.setSGLevel(sgLevel);
            }               
         }
         return match;
      }
   }
   
   /**
    * Matches a catch by fault name to a fault with the same name and data. This is not a best match rule.
    */
   protected static class AeFaultNameOnlyIgnoreData extends AeFaultMatchingRule
   {
      /**
       * @see org.activebpel.rt.bpel.def.faults.IAeFaultMatchingRule#getMatch( org.activebpel.rt.wsdl.IAeContextWSDLProvider, 
       * org.activebpel.rt.bpel.def.faults.IAeCatch, org.activebpel.rt.bpel.IAeFaultTypeInfo)
       */
      public IAeMatch getMatch(IAeContextWSDLProvider aProvider, IAeCatch aCatch, IAeFaultTypeInfo aFault)
      {
         boolean matched = false;
         if ( aFault.hasData() && !aCatch.hasFaultVariable() )
         {
            matched = AeUtil.compareObjects(aCatch.getFaultName(), aFault.getFaultName());
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
    * Matches a catch with a message/element variable to a fault with identical data
    */
   protected static class AeVariableOnly_WSBPEL extends AeVariableOnly
   {
      /**
       * @see org.activebpel.rt.bpel.def.faults.IAeFaultMatchingRule#getMatch( org.activebpel.rt.wsdl.IAeContextWSDLProvider, 
       * org.activebpel.rt.bpel.def.faults.IAeCatch, org.activebpel.rt.bpel.IAeFaultTypeInfo)
       */
      public IAeMatch getMatch(IAeContextWSDLProvider aProvider, IAeCatch aCatch, IAeFaultTypeInfo aFault)
      {
         IAeMatch match = super.getMatch(aProvider, aCatch, aFault);
         if ( match == null && aCatch.getFaultName() == null )
         {
            // check for element type match
            if ( aFault.hasElementData() && aCatch.hasFaultVariable() )
            {
               boolean matched = AeUtil.compareObjects(aCatch.getFaultElementName(), aFault.getElementType());
               if ( matched )
               {
                  match = new AeMatch(false, getPriority());
               }
            }
         }
         return match;
      }
   }

   /**
    * Matches if the fault has an element that is a member of a substitution group headed by the catch element.
    * The catch has no fault name. Otherwise similar to above AeVariableOnly_WSBPEL. This is not a best match 
    * rule.    
    */
   protected static class AeVariableOnly_WSBPEL_SG extends AeFaultMatchingRule
   {
      /**
       * @see org.activebpel.rt.bpel.def.faults.IAeFaultMatchingRule#getMatch( org.activebpel.rt.wsdl.IAeContextWSDLProvider, 
       * org.activebpel.rt.bpel.def.faults.IAeCatch, org.activebpel.rt.bpel.IAeFaultTypeInfo)
       */
      public IAeMatch getMatch(IAeContextWSDLProvider aProvider, IAeCatch aCatch, IAeFaultTypeInfo aFault)
      {
         IAeMatch match = null;
         if ( aFault.hasElementData() && aCatch.hasFaultVariable() && aCatch.getFaultName() == null )
         {
            int sgLevel = AeWSDLDefHelper.getSubstitutionGroupLevel(aProvider, aCatch.getFaultElementName(), aFault.getElementType());
            if ( sgLevel > 0 )
            {
               match = new AeMatch(false, getPriority());
               match.setSGLevel(sgLevel);
            }
         }
         return match;
      }
   }
   
   /**
    * Matches a catch with an element variable and no name to a single element part message. This is not a best match rule.
    */
   protected static class AeVariableOnlyAndSingleElement extends AeFaultMatchingRule
   {
      /**
       * @see org.activebpel.rt.bpel.def.faults.IAeFaultMatchingRule#getMatch( org.activebpel.rt.wsdl.IAeContextWSDLProvider, 
       * org.activebpel.rt.bpel.def.faults.IAeCatch, org.activebpel.rt.bpel.IAeFaultTypeInfo)
       */
      public IAeMatch getMatch(IAeContextWSDLProvider aProvider, IAeCatch aCatch, IAeFaultTypeInfo aFault)
      {
         boolean matched = false;
         if ( aFault.hasMessageData() && aCatch.hasFaultVariable() && aCatch.getFaultElementName() != null
               && aCatch.getFaultName() == null )
         {
            matched = AeUtil.compareObjects(aFault.getSinglePartElementType(), aCatch.getFaultElementName());
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
    * Matches a catch with an element variable and no name to a single element part message if the fault is in a substitution
    * group of catch. Otherwise similar to the above AeFaultNameAndSingleElement. This is not a
    * best match rule.
    */
   protected static class AeVariableOnlyAndSingleElement_SG extends AeFaultMatchingRule
   {
      /**
       * @see org.activebpel.rt.bpel.def.faults.IAeFaultMatchingRule#getMatch( org.activebpel.rt.wsdl.IAeContextWSDLProvider, 
       * org.activebpel.rt.bpel.def.faults.IAeCatch, org.activebpel.rt.bpel.IAeFaultTypeInfo)
       */
      public IAeMatch getMatch(IAeContextWSDLProvider aProvider, IAeCatch aCatch, IAeFaultTypeInfo aFault)
      {
         IAeMatch match = null;
         if ( aFault.hasMessageData() && aCatch.hasFaultVariable() && aCatch.getFaultElementName() != null
               && aCatch.getFaultName() == null )
         {            
            int sgLevel = AeWSDLDefHelper.getSubstitutionGroupLevel(aProvider, aCatch.getFaultElementName(), aFault.getSinglePartElementType());               
            if ( sgLevel > 0 )
            {
               match = new AeMatch(false, getPriority());
               match.setSGLevel(sgLevel);
            }              
         }
         
         return match;
      }
   }
      
}
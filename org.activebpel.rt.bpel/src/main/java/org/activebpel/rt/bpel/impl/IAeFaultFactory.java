//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/IAeFaultFactory.java,v 1.11.4.1 2008/04/21 16:09:42 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl; 

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.IAeFault;

/**
 * Interface for creating faults that get propagated to activities. 
 * 
 * The faults defined here include AE faults, all of the faults for the current 
 * version of the BPEL spec as well as a few faults from earlier versions. In 
 * cases where a fault has been removed from the spec, it should be removed from 
 * this interface and put into a subclass of the interface that is only known to 
 * those impl objects.
 */
public interface IAeFaultFactory
{
   /** constant for indicating sublanguage execution error was due to execution of XPath */
   public static final String XPATH_FUNCTION_ERROR = "xpathFunctionError"; //$NON-NLS-1$
   /** constant for indicating sublanguage execution error was due to execution of JavaScript */
   public static final String JSCRIPT_FUNCTION_ERROR = "javascriptFunctionError"; //$NON-NLS-1$
   /** constant for indicating sublanguage execution error was due to execution of JavaScript */
   public static final String XQUERY_FUNCTION_ERROR = "xqueryFunctionError"; //$NON-NLS-1$
   /** constant for indicating sublanguage execution error was due to execution of XSLT */
   public static final String XSLT_FUNCTION_ERROR = "xsltFunctionError"; //$NON-NLS-1$

   /** constant for indicating failure in generating boolean value from an expression */
   public static final String TYPE_BOOLEAN = "xsd:boolean";  //$NON-NLS-1$
   /** constant for indicating failure in generating datetime or date value from an expression */
   public static final String TYPE_DEADLINE = "xsd:datetime/xsd:date";  //$NON-NLS-1$
   /** constant for indicating failure in generating duration value from an expression */
   public static final String TYPE_DURATION = "xsd:duration";  //$NON-NLS-1$
   /** constant for indicating failure in generating unsignedInt value from an expression */
   public static final String TYPE_UNSIGNEDINT = "xsd:unsignedInt";  //$NON-NLS-1$
   
   /**
    * AE specific error for reporting sub process cancellation signal from 
    * parent
    */
   public IAeFault getProcessCancelled();
   
   /**
    * AE specific error for reporting process termination
    */
   public IAeFault getProcessTerminated();
   
   /**
    * AE specific error for reporting that a sub process compensation handler 
    * was terminated
    */
   public IAeFault getCoordinatedCompensationTerminated();
   
   /**
    * AE specific error for reporting that a message sent to the engine was not 
    * matched to an existing receiver and could not create a new process.
    */
   public IAeFault getUnmatchedRequest();
   
   /**
    * AE specific error for reporting that a request-response style operation
    * invoke will not be responded to. The process that is failing to reply will
    * have faulted with a bpel:missingReply fault but the service that invoked 
    * us will get this fault.
    */
   // fixme (MF 3.1) will come back to use this fault after the 3.0 release 
   public IAeFault getExternalMissingReply();
   
   /**
    * AE internal error thrown to an activity to indicate that it is being 
    * retried.
    */
   public IAeFault getRetryFault();
   
   /**
    * AE internal error thrown to an activity to indicate that it should 
    * terminate as the result of a continue or break activity executing
    */
   public IAeFault getEarlyTerminationFault();
   
   /**
    * Returns true if the fault is the retry fault which is a special internal 
    * fault used for retrying activities
    * @param aFault
    */
   public boolean isRetryFault(IAeFault aFault);
   
   /**
    * Returns true if the fault is the early termination fault which is a 
    * special internal fault used for implementing the break/continue
    * @param aFault
    */
   public boolean isEarlyTerminationFault(IAeFault aFault);
   
   /**
    * Returns true if the fault is the standard fault, in that case the process 
    * will exit immediately when exitOnStatndardFault="yes".
    * @param aFault
    */
   public boolean isStandardFaultForExit(IAeFault aFault);
   
   /**
    * Returns true if the factory requires that a fault is reported for an 
    * ambiguous receive
    */
   public boolean isAmbiguousReceiveFaultSupported();

   /**
    * Gets the namespace for the standard faults
    */
   public String getNamespace();
   
   /**
    * Gets a list of the standard fault names
    */
   public String[] getStandardFaults();
   
   /**
    * Thrown when a selection operation performed either in a function such as 
    * bpel:getVariableProperty, or in an assignment, encounters an error.
    */
   public IAeFault getSelectionFailure();
   
   /**
    * Thrown when more than one receive activity or equivalent are enabled 
    * simultaneously for the same partnerLink, portType, operation and 
    * correlationSet(s).
    */
   public IAeFault getConflictingReceive();
   
   /**
    * Thrown when more than one inbound message activity on the same 
    * partnerLink, operation and messageExchange is open. 
    */
   public IAeFault getConflictingRequest();
   
   /**
    * Thrown when incompatible types or incompatible XML infoset structure are 
    * encountered in an assign activity. 
    */
   public IAeFault getMismatchedAssignmentFailure();
   
   /**
    * Thrown when the join condition of an activity evaluates to false. 
    */
   public IAeFault getJoinFailure();
   
   /**
    * Thrown when the contents of the messages that are processed in an invoke, 
    * receive, or reply activity do not match specified correlation information.  
    */
   public IAeFault getCorrelationViolation();
   
   /**
    * Thrown when there is an attempt to access the value of an uninitialized 
    * variable or in the case of a message type variable one of its 
    * uninitialized parts.
    */
   public IAeFault getUninitializedVariable();
   
   /**
    * Thrown when a reply activity cannot be associated with an incomplete 
    * receive activity by matching the partnerLink, operation and 
    * messageExchange tuple.
    */
   public IAeFault getMissingRequest();

   /**
    * Thrown when a receive has been executed, and the process instance or scope 
    * instance reaches the end of its execution without a corresponding reply 
    * having been executed.
    */
   public IAeFault getMissingReply();
   
   /**
    * Thrown when the forEach counter expressions evaluate to something other 
    * than an unsigned int
    */
   // TODO (MF) move this to the 1.1 interface which requires separate impl of forEach for 1.1 and 2.0
   public IAeFault getForEachCounterError();
   
   /**
    * Thrown if the integer value used in &lt;branches&gt; completion condition is 
    * larger than the number of directly enclosed activities. 
    */
   public IAeFault getInvalidBranchCondition();
   
   /**
    * Thrown if upon completion of a directly enclosed scope activity within 
    * &lt;forEach&gt; activity it can be determined that the completion condition can 
    * never be true. 
    */
   public IAeFault getCompletionConditionFailure();
   
   // 2.0
   /**
    * Thrown when the execution of an expression results in an unhandled 
    * expression / query language execution fault.
    * @param aLanguage
    * @param aThrowable
    * @param aErrorMessage
    */
   public IAeFault getSubLanguageExecutionFault(String aLanguage, Throwable aThrowable, String aErrorMessage);
   
   /**
    * Thrown when a WS-BPEL implementation fails to interpret the combination of 
    * the "reference-scheme" attribute and the content element OR just the 
    * content element alone
    * @param aElementName
    */
   public IAeFault getUnsupportedReference(QName aElementName);
   
   /**
    * Thrown when any XML validation (implicit or explicit: e.g. &lt;validate&gt; or 
    * &lt;assign validate="yes"&gt;) fails.
    * @param aInfo Information describing the problem
    */
   public IAeFault getInvalidVariables(String aInfo);
   
   /**
    * Thrown when an Invoke activity is used on a partnerLink whose partnerRole 
    * endpoint reference is not initialized.
    */
   public IAeFault getUninitializedPartnerRole();
   
   /**
    * Thrown if there is any problem creating any of the objects defined as part 
    * of scope initialization. This fault is always caught by the parent scope 
    * of the faulted scope.
    */
   public IAeFault getScopeInitializationFailure();
   
   /**
    * The named style sheet in a bpel:doXslTransform function call was not found
    */
   public IAeFault getXsltStyleSheetNotFound();
   
   /**
    * The transformation source provided in a bpel:doXslTransform function call 
    * was not legal (i.e., not an EII).
    */
   public IAeFault getXsltInvalidSource();
   
   /**
    * Thrown when an expression fails to produce the expected data type 
    * (xsd:boolean, xsd:unsignedInt).
    */
   public IAeFault getInvalidExpressionValue(String aType);
   
   /**
    * Thrown when we encounter a problem that should have been discovered by 
    * static analysis.
    * 
    * @param aMessage
    */
   public IAeFault getStaticAnalysisFailure(String aMessage);

   /**
    * Returns a fault for repeated compensation or null if allowed by the bpel 
    * version
    */
   public IAeFault getRepeatedCompensation();
   
   /**
    * Returns a fault for ambiguous receive.
    */
   public IAeFault getAmbiguousReceive();
   
   /**
    * Returns a fault for ambiguous receives that will get sent to whatever
    * service sent the ambigious receive.
    */
   public IAeFault getExternalAmbiguousReceive();
}
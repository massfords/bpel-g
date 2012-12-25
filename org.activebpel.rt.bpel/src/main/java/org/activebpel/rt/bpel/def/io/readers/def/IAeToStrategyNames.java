//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/readers/def/IAeToStrategyNames.java,v 1.6.4.1 2008/04/21 16:09:42 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io.readers.def; 

/**
 * Names of the strategies for selecting the "L" value in a copy operation
 */
public interface IAeToStrategyNames
{
   /** 
    * selects variable of simple or complex type 
    * 
    * &lt;to variable='V1'/&gt;
    */
   public static final String TO_VARIABLE_TYPE = "to-variable-type"; //$NON-NLS-1$

   /**
    * selects variable of simple or complex type with a query 
    * 
    * &lt;to variable="typeVar"&gt;
    *   &lt;query queryLanguage="urn:queryLang:uri"&gt;path/to/data&lt;/query&gt;
    * &lt;/to&gt;
    */
   public static final String TO_VARIABLE_TYPE_QUERY = "to-variable-type-query"; //$NON-NLS-1$

   /** 
    * selects data within a message part using a query 
    * 
    * &lt;to variable='msg' part='part' query='/some/xpath'/&gt;                [bpel 1.1]
    *    or
    * &lt;to variable='msg' part='part' query='/some/xpath'&gt;
    *    &lt;query queryLanguage="urn:queryLang:uri"&gt;path/to/data&lt;/query&gt;    [bpel 2.0]
    * &lt;/to&gt;
    */
   public static final String TO_VARIABLE_MESSAGE_PART_QUERY = "to-variable-message-part-query"; //$NON-NLS-1$
   
   /** 
    * selects a message part 
    * 
    * &lt;to variable='msg' part='part'/&gt; 
    */
   public static final String TO_VARIABLE_MESSAGE_PART = "to-variable-message-part"; //$NON-NLS-1$
   
   /** 
    * selects whole message 
    * 
    * &lt;to variable='msg'/&gt; 
    */
   public static final String TO_VARIABLE_MESSAGE = "to-variable-message"; //$NON-NLS-1$
   
   /** 
    * selects data within an element using a query 
    * 
    * &lt;to variable='myElement'/&gt;
    *    or
    * &lt;to variable='myElement'&gt;
    *    &lt;query queryLanguage="urn:queryLang:uri"&gt;path/to/data&lt;/query&gt;    [bpel 2.0]
    * &lt;/to&gt;
    * 
    */
   public static final String TO_VARIABLE_ELEMENT_QUERY = "to-variable-element-query"; //$NON-NLS-1$
   
   /** 
    * selects variable that's an element 
    * 
    * &lt;to variable='myElement'/&gt; 
    */
   public static final String TO_VARIABLE_ELEMENT = "to-variable-element"; //$NON-NLS-1$
   
   /** 
    * selects variable of simple or complex type using a property alias 
    * 
    * WSBPEL 2.0 
    *
    * &lt;to variable='V1' property='ns:prop'/&gt;
    */
   public static final String TO_PROPERTY_TYPE = "to-property-type"; //$NON-NLS-1$
   
   /** 
    * selects data from a message variable using a property alias 
    * 
    * &lt;to variable='msg' property='ns:prop'/&gt;
    */
   public static final String TO_PROPERTY_MESSAGE = "to-property-message"; //$NON-NLS-1$
   
   /** 
    * selects variable element using a property alias 
    * 
    * WSBPEL 2.0
    * 
    *  &lt;to variable='myElement' property='ns:prop'/&gt;
    */
   public static final String TO_PROPERTY_ELEMENT = "to-property-element"; //$NON-NLS-1$
   
   /** 
    * selects a partner link to assign to its partner role 
    * 
    * &lt;to partnerLink='myLink'/&gt; 
    */
   public static final String TO_PARTNER_LINK = "to-partnerLink"; //$NON-NLS-1$

   /**
    * Executes expression to select lValue 
    * 
    * &lt;to expressionLanguage="urn:expressionLang:uri"&gt;$varName.partName/path/to/data&lt;/to&gt; 
    */
   public static final String TO_EXPRESSION = "to-expression"; //$NON-NLS-1$

   /**
    * Extension selects lValue 
    * 
    * &lt;to ns:myExtension="foo" /&gt; 
    */
   public static final String TO_EXTENSION = "to-extension"; //$NON-NLS-1$
}
 
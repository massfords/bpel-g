//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/readers/def/IAeFromStrategyNames.java,v 1.4.4.1 2008/04/21 16:09:42 ppatruni Exp $
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
 * Names for the from strategies 
 */
public interface IAeFromStrategyNames
{
   /** 
    * Selects a simple or complex type variable
    *  
    * &lt;from variable="V1"/&gt;
    */
   public static final String FROM_VARIABLE_TYPE = "from-variable-type"; //$NON-NLS-1$

   /** 
    * Selects a simple or complex type variable
    *  
    * &lt;from variable="typeVar"&gt;
    *    &lt;query queryLanguage="urn:language:uri"&gt;/path/to/data&lt;/query&gt;
    * &lt;/from&gt;
    */
   public static final String FROM_VARIABLE_TYPE_QUERY = "from-variable-type-query"; //$NON-NLS-1$

   /** 
    * Selects data from a message variable part using a query 
    * 
    *   &lt;from variable="msg" part="partName" query="/some/xpath"/&gt;       [bpel 1.1]
    *      or
    *   &lt;from variable="msg" part="partName"&gt;
    *      &lt;query queryLanguage="urn:language:uri"&gt;/some/xpath&lt;/query&gt;   [bpel 2.0]
    *   &lt;/from&gt;
    */
   public static final String FROM_VARIABLE_MESSAGE_PART_QUERY = "from-variable-message-part-query"; //$NON-NLS-1$

   /**
    * Selects a single part from a message variable 
    * 
    * &lt;from variable="msg" part="partName"/&gt;
    */
   public static final String FROM_VARIABLE_MESSAGE_PART = "from-variable-message-part"; //$NON-NLS-1$

   /** 
    * Selects the whole message variable 
    * 
    * &lt;from variable="msg"/&gt; 
    */
   public static final String FROM_VARIABLE_MESSAGE = "from-variable-message"; //$NON-NLS-1$
   
   /** 
    * Selects data from element using a query  
    *  
    * &lt;from variable="element" query="/ns:some/xpath"/&gt;                [bpel 1.1]
    *    or
    * &lt;from variable="element"&gt;
    *    &lt;query queryLanguage="urn:language:uri"&gt;/some/xpath&lt;/query&gt;   [bpel 2.0]
    * &lt;/from&gt;
    */
   public static final String FROM_VARIABLE_ELEMENT_QUERY = "from-variable-element-query"; //$NON-NLS-1$
   
   /** 
    * Selects element variable 
    * 
    * &lt;from variable="element"/&gt; 
    */
   public static final String FROM_VARIABLE_ELEMENT = "from-variable-element"; //$NON-NLS-1$
   
   /** 
    * Selects data from a complex type or simple type variable using a property alias 
    * 
    * &lt;from variable="V1" property="ns:propName"/&gt; 
    */
   public static final String FROM_PROPERTY_TYPE = "from-property-type"; //$NON-NLS-1$
   
   /** 
    * Selects data from a message variable using a property alias 
    * 
    * &lt;from variable="msg" property="ns:propName"/&gt; 
    */
   public static final String FROM_PROPERTY_MESSAGE = "from-property-message"; //$NON-NLS-1$
   
   /** 
    * Selects data from an element variable using a property alias 
    * 
    * &lt;from variable="element" property="ns:propName"/&gt;
    */
   public static final String FROM_PROPERTY_ELEMENT = "from-property-element"; //$NON-NLS-1$
   
   /** 
    * Selects endpoint reference data from a partner link
    * 
    * &lt;from partnerLink='plink' endpointReference='myRole|partnerRole'/&gt; 
    */
   public static final String FROM_PARTNER_LINK = "from-partnerLink"; //$NON-NLS-1$
   
   /** 
    * Selects an EII or TII from literal xml 
    * 
    * &lt;from&gt;&lt;literal&gt;Hello&lt;/literal&gt;&lt;/from&gt;
    * or 
    * &lt;from&gt;&lt;literal&gt;&lt;myLiteralElement xmlns=''&gt;Hello&lt;/myLiteralElement&gt;&lt;/literal&gt;&lt;/from&gt; 
    */
   public static final String FROM_LITERAL = "from-literal"; //$NON-NLS-1$
   
   /** 
    * Executes expression to generate data for copy
    * 
    * &lt;from expressionLanguage='some-lang'&gt;$myVariable * Math.random()&lt;/from&gt; 
    */
   public static final String FROM_EXPRESSION = "from-expression"; //$NON-NLS-1$
   
   /** 
    * Executes some extension behavior to generate data for copy
    * 
    * &lt;from ns:myExtension='some-value' /&gt; 
    */
   public static final String FROM_EXTENSION = "from-extension"; //$NON-NLS-1$
}
 
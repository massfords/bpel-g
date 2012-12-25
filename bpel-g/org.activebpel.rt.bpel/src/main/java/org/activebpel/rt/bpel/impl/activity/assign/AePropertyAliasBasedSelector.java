//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/AePropertyAliasBasedSelector.java,v 1.6 2007/10/03 12:39:51 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.AeNamespaceResolver;
import org.activebpel.rt.bpel.xpath.AeXPathHelper;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.def.IAePropertyAlias;
import org.activebpel.rt.xml.IAeNamespaceContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Selects a value from a variable using a property alias.
 */
public class AePropertyAliasBasedSelector
{
   /**
    * Selects a value from a variable using a property alias
    * @param aPropAlias property alias to use for the selction
    * @param aDataContext context to use for the propertyAlias query
    * @param aContext provides means to execute the query and any additional contextual info like config settings
    * @throws AeBusinessProcessException
    */
   public static Object selectValue(IAePropertyAlias aPropAlias, Object aDataContext, IAeCopyOperationContext aContext) throws AeBusinessProcessException
   {
      Object data = aDataContext;
      if (data instanceof Node && AeUtil.notNullOrEmpty(aPropAlias.getQuery()))
      {
         Node docElem = data instanceof Document? ((Document) data).getDocumentElement() : (Node)data;
         IAeNamespaceContext namespaceResolver = new AeNamespaceResolver(aPropAlias);
         data = aContext.executeQuery(aPropAlias.getQuery(), docElem, namespaceResolver, false);
         data = AeXPathHelper.getInstance(aContext.getBPELNamespace()).unwrapXPathValue(data);

         if (data == null && aContext.isCreateXPathAllowed())
            data = AeCreateXPathUtil.findOrCreateXPath(aPropAlias.getQuery(), docElem.getOwnerDocument(), aContext, namespaceResolver);
      }
      
      return data;
   }
}
 
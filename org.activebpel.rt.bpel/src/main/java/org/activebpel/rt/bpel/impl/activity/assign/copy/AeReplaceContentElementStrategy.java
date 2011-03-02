//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/copy/AeReplaceContentElementStrategy.java,v 1.4 2007/01/27 14:41:57 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign.copy; 

import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperation;
import org.activebpel.rt.bpel.impl.activity.assign.IAeVariableDataWrapper;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * Specialized version of ReplaceContent strategy that involves copying a text
 * type to an element. 
 */
public class AeReplaceContentElementStrategy extends AeReplaceContentStrategy
{
   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyStrategy#copy(org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperation, java.lang.Object, java.lang.Object)
    */
   public void copy(IAeCopyOperation aCopyOperation, Object aFromData, Object aToData) throws AeBpelException
   {
      String from = toString(aCopyOperation, aFromData);

      // we're copying text to an element
      Element target = getElement(aToData);
      AeXmlUtil.removeNodeContents(target, false);
      
      Text text = target.getOwnerDocument().createTextNode(from);
      target.appendChild(text);
   }
   
   /**
    * Type safe getter for the target element
    * @param aToData
    * @throws AeBpelException
    */
   protected Element getElement(Object aToData) throws AeBpelException
   {
      if (aToData instanceof IAeVariableDataWrapper)
      {
         return (Element) ((IAeVariableDataWrapper)aToData).getValue();
      }
      return (Element)aToData;
   }
}
 
//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/copy/AeReplaceElementStrategy.java,v 1.7 2008/01/27 15:14:12 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign.copy;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.activity.assign.AeMismatchedAssignmentException;
import org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperation;
import org.activebpel.rt.bpel.impl.activity.assign.IAeCopyStrategy;
import org.activebpel.rt.bpel.impl.activity.assign.IAeVariableDataWrapper;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.w3c.dom.Element;

/**
 * Handles copying one element to another. In this strategy, the src element's attributes and children replace
 * all of the content of the target elements.
 */
public class AeReplaceElementStrategy implements IAeCopyStrategy
{
   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyStrategy#copy(org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperation,
    *      java.lang.Object, java.lang.Object)
    */
   public void copy(IAeCopyOperation aCopyOperation, Object aFromData, Object aToData) throws AeBpelException
   {
      Element src = (Element)aFromData;
      Element target = null;
      if ( aToData instanceof IAeVariableDataWrapper )
      {
         target = (Element)((IAeVariableDataWrapper)aToData).getValue();
      }
      else
         target = (Element)aToData;

      boolean isIdenticalElement = AeUtil.compareObjects(new QName(AeUtil.getSafeString(src.getNamespaceURI()), src.getLocalName()), new QName(target.getNamespaceURI(), target.getLocalName()));
      if ( aCopyOperation.isKeepSrcElementName() && !isIdenticalElement )
      {
         if ( target.getOwnerDocument().getDocumentElement() == target )
         {
            IAeContextWSDLProvider provider = aCopyOperation.getContext().getContextWSDLProvider();
            QName aMemberElementName = AeXmlUtil.getElementType(src);
            QName aHeadElementName = AeXmlUtil.getElementType(target);
            AeBPELExtendedWSDLDef wsdlDef = AeWSDLDefHelper.getWSDLDefinitionForElement(provider, aMemberElementName);
            if ( wsdlDef == null || !wsdlDef.isCompatibleSGElement(aHeadElementName, aMemberElementName) )
            {
               throw new AeMismatchedAssignmentException(aCopyOperation.getContext().getBPELNamespace());
            }
         }

         // create a new element using the QName of the src
         Element e = target.getOwnerDocument().createElementNS(src.getNamespaceURI(), src.getNodeName());
         // replace the old node
         target.getParentNode().replaceChild(e, target);
         // set our new target in place
         target = e;
      }
      else
      {
         AeXmlUtil.removeNodeContents(target, true);
      }
      AeXmlUtil.copyNodeContents(src, target);
   }

}

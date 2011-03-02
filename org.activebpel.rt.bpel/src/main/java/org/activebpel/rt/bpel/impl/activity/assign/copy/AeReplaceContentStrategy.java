//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/copy/AeReplaceContentStrategy.java,v 1.6 2007/01/27 14:41:57 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign.copy; 

import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.activity.assign.AeMismatchedAssignmentException;
import org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperation;
import org.activebpel.rt.bpel.impl.activity.assign.IAeCopyStrategy;
import org.activebpel.rt.bpel.impl.activity.assign.IAeVariableDataWrapper;
import org.activebpel.rt.bpel.xpath.AeXPathHelper;
import org.activebpel.rt.util.AeXmlUtil;
import org.exolab.castor.xml.schema.XMLType;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Handles copying the content of an Element, Attribute, Text, or String into a CII container
 * like an Attribute, Text, or Message Part
 */
public class AeReplaceContentStrategy implements IAeCopyStrategy
{
   // TODO (MF) refactor into more specific strategies in order to remove some conditional logic
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyStrategy#copy(org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperation, java.lang.Object, java.lang.Object)
    */
   public void copy(IAeCopyOperation aCopyOperation, Object aFromData, Object aToData) throws AeBpelException
   {
      String from = toString(aCopyOperation, aFromData);

      // target is an attribute or text node, set node value as the from text
      if (aToData instanceof Attr || aToData instanceof Text)
      {
         Node target = (Node) aToData;
         target.setNodeValue(from);
      }
      // target is a simple type of some kind wrapped with our data wrapper
      else if (aToData instanceof IAeVariableDataWrapper && !(((IAeVariableDataWrapper)aToData).getValue() instanceof Element))
      {
         /*
          *       this code should only be run when the data wrapper is for a simple type
          *       in this case, we want to serialize the src data to a string and
          *       then convert it back to the target's type.
          *       
          *       for example:
          *       
          *       source data        target type        desired target value
          *       -------------      ------------       ---------------------
          *       <src>100</src>     xsd:int            Integer(100)
          *       "100"              xsd:int            Integer(100)
          *       @value="100"       xsd:int            Integer(100)
          *       <src>100</src>     xsd:integer        Long(100) or BigInteger(100)?
          *       <src>100</src>     xsd:string         "100"
          *       <src>true</src>    xsd:boolean        Boolean.TRUE
          *       "true"             xsd:boolean        Boolean.TRUE
          *       <src>true</src>    xsd:string         "true"
          *       "true"             xsd:string         "true"
          *       
          *       Running through this code when the target is NOT a simple type
          *       is a waste since it results in unnecessary conversions
          *       
          *       object -> string -> object -> string
          *       
          *       when we could simply go from object -> string
          */
         
         IAeVariableDataWrapper target = (IAeVariableDataWrapper) aToData;
         
         // fixme make IAeSchemaType impls immutable
         /*
          * TODO (MF) if the src is an IAeSchemaType and the target is the same type, then avoid the serialization
          */
         XMLType type = target.getXMLType();
         Object value = aCopyOperation.getContext().getTypeMapping().deserialize(type, from);
         target.setValue(value);
      }
      else 
      {
         throw new AeMismatchedAssignmentException(aCopyOperation.getContext().getBPELNamespace());
      }
   }

   /**
    * Converts the from data to a string.
    * @param aCopyOperation
    * @param aFromData
    * @throws AeBpelException
    */
   protected String toString(IAeCopyOperation aCopyOperation, Object aFromData) throws AeBpelException
   {
      if (aFromData instanceof Node)
         ((Node)aFromData).normalize();
      
      if (aFromData instanceof String)
      {
         return (String)aFromData;
      }
      else if (aFromData instanceof Element)
      {
         // For BPWS1.1/Defect 1737 and BPEL 1.1 - for backward compatibility, do not use text nodes from descendent elements.          
         if (IAeBPELConstants.BPWS_NAMESPACE_URI.equals(aCopyOperation.getContext().getBPELNamespace() ))
         {
            String s = AeXmlUtil.getText((Element) aFromData);
            return s;
         }
         else
         {
            // For BPWS 2.x use Xpath conversion as per spec.
            AeXPathHelper xpathHelper = AeXPathHelper.getInstance(aCopyOperation.getContext().getBPELNamespace());
            Object retValue = xpathHelper.executeXPathExpression("string()", aFromData, null); //$NON-NLS-1$
            return xpathHelper.unwrapXPathValue(retValue).toString();
         }
      }
      else if (aFromData instanceof Attr || aFromData instanceof Text)
      {
         Node node = (Node) aFromData;
         return node.getNodeValue();
      }
      else
      {
         return aCopyOperation.getContext().getTypeMapping().serialize(aFromData);
      }
   }
}
 
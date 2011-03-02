//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/AeAbstractCopyStrategyFactoryImpl.java,v 1.2.14.1 2008/04/21 16:09:43 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign;

import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.IAePartnerLink;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.AeSelectionFailureException;
import org.activebpel.rt.bpel.impl.activity.assign.copy.AeCopyWholeMessageStrategy;
import org.activebpel.rt.bpel.impl.activity.assign.copy.AeEmptySelectionStrategy;
import org.activebpel.rt.bpel.impl.activity.assign.copy.AeMismatchedAssignmentStrategy;
import org.activebpel.rt.bpel.impl.activity.assign.copy.AeReplaceContentElementStrategy;
import org.activebpel.rt.bpel.impl.activity.assign.copy.AeReplaceContentStrategy;
import org.activebpel.rt.bpel.impl.activity.assign.copy.AeReplaceElementStrategy;
import org.activebpel.rt.bpel.impl.activity.assign.copy.AeSelectionFailureStrategy;
import org.activebpel.rt.bpel.impl.activity.assign.copy.AeSetPartnerLinkStrategy;
import org.activebpel.rt.bpel.impl.activity.assign.copy.AeSrefToEprReplaceElementStrategy;
import org.activebpel.rt.bpel.impl.endpoints.AeEndpointFactory;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.schema.IAeSchemaType;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * Base factory used to create IAeCopyStrategy instances. These instances are used 
 * to implement the logic of the given copy operation.
 */
public class AeAbstractCopyStrategyFactoryImpl implements IAeCopyStrategyFactory
{
   /** array of strategies used to implement the logic for the copying of data in a copy operation */
   private static final IAeCopyStrategy[] STRATEGIES =
   {
      // fixme restore the static analysis failure once sa is impl'd, same w/ error
      /* 0  */ new AeReplaceElementStrategy(),
      /* 1  */ new AeReplaceContentStrategy(),
      /* 2  */ new AeReplaceContentElementStrategy(),
      /* 3  */ new AeSetPartnerLinkStrategy(),
      /* 4  */ new AeCopyWholeMessageStrategy(),
      /* 5  */ new AeEmptySelectionStrategy(),
      /* 6  */ new AeSelectionFailureStrategy(),
      /* 7  */ new AeMismatchedAssignmentStrategy(), //      new AeStaticAnalysisFailureStrategy(),
      /* 8  */ new AeMismatchedAssignmentStrategy(),
      /* 9  */ new AeSrefToEprReplaceElementStrategy(),
      /* 10 */ new AeMismatchedAssignmentStrategy() //      new AeErrorStrategy()
   };

   /** constant for the index of the strategy that replaces the element */
   private static final int RE = 0;
   /** constant for the index of the strategy that replaces the content of a variable */
   private static final int RC = 1;
   /** constant for the index of the strategy that replaces the content of an element variable */
   private static final int RC_ELEMENT = 2;
   /** constant for the index of the strategy that copies a partner link service-ref to an element */
   private static final int ELEMENT_TO_PLINK = 3;
   /** constant for the index of the strategy that copies a whole message to another message */
   private static final int MSG_COPY = 4;
   /** constant for the index of the strategy that handles selection failures, either removes target node or throws */
   private static final int REMOVE = 5;
   /** constant for the index of the strategy that handles selection failures */
   private static final int SEL_FAIL = 6;
   /** constant for the index of the strategy that represents a static analysis failure */
   private static final int SA_FAIL = 7;
   /** constant for the index of the strategy that represents a mismatched assignment failure */
   private static final int MM_ASSIGN = 8;
   /** constant for the index of the strategy that represents copying from source's sub-element to the target (eg: service-ref element to wsa:epr element). */
   private static final int SREF_EPR_RE = 9;
   /** constant for the index of the strategy that represents an error condition */
   private static final int ERROR = 10;

   /** constant for an element data type */
   protected static final int ELEMENT_TYPE = 0;
   /** constant for an attribute data type */
   protected static final int ATTR_TYPE = 1;
   /** constant for an text data type */
   protected static final int TEXT_TYPE = 2;
   /** constant for an partner link data type */
   protected static final int PLINK_TYPE = 3;
   /** constant for message data */
   protected static final int MSG_DATA = 4;
   /** constant for null data type, only used for the &lt;from&gt; and allowed when selectionFailure is disabled */
   protected static final int NULL = 5;
   /** constant for when the result is a List */
   protected static final int LIST_TYPE = 6;
   /** constant for bpel &lt;service-ref&gt; element type with &lt;wsa:epr&gt; child element*/
   protected static final int ELEMENT_SREF_EPR = 7;
   /** constant for &lt;wsa:EndpointReference&gt; element type */
   protected static final int ELEMENT_EPR = 8;
   /** constant for an unknown data type (which represents an error) */
   protected static final int UNKNOWN_TYPE = 9;

   /** Table drives which strategy is used when copying from one data type to another */
   private static final int[][] STRATEGY_TABLE =
   {
      // -----------------------------------------------------------------------------------------------------------------------------------------------
      // |             |             Copying to...                                                                                                      |
      // |             |-------------------------------------------------------------------------------------------------------------------------------------|
      // | From Type   |  Element,       Attribute,    Text,        PLink,              MsgData,    Null,      List,       SRefEprEle, EprEle,       Unknown |
      // ----------------------------------------------------------------------------------------------------------------------------------------------------|
      /*  0. Element   */  {RE,            RC,          RC,         ELEMENT_TO_PLINK,   SA_FAIL,    SEL_FAIL,  SEL_FAIL,   RE,          RE,           ERROR  },
      /*  1. Attribute */  {ERROR,         ERROR,       ERROR,      ERROR,              ERROR,      ERROR,     ERROR,      ERROR,       ERROR,        ERROR  },
      /*  2. Text      */  {RC_ELEMENT,    RC,          RC,         MM_ASSIGN,          SA_FAIL,    SEL_FAIL,  SEL_FAIL,   RC_ELEMENT,  RC_ELEMENT,   ERROR  },
      /*  3. PLink     */  {ERROR,         ERROR,       ERROR,      ERROR,              ERROR,      ERROR,     ERROR,      ERROR,       ERROR,        ERROR  },
      /*  4. MsgData   */  {SA_FAIL,       SA_FAIL,     SA_FAIL,    SA_FAIL,            MSG_COPY,   SEL_FAIL,  SEL_FAIL,   SA_FAIL,     SA_FAIL,      ERROR  },
      /*  5. Null      */  {REMOVE,        REMOVE,      REMOVE,     MM_ASSIGN,          SA_FAIL,    SEL_FAIL,  SEL_FAIL,   REMOVE,      REMOVE,       ERROR  },
      /*  6. List      */  {SEL_FAIL,      SEL_FAIL,    SEL_FAIL,   SEL_FAIL,           SA_FAIL,    SEL_FAIL,  SEL_FAIL,   SEL_FAIL,    SEL_FAIL,     ERROR  },
      /*  7. SRefEprEle*/  {RE,            RC,          RC,         ELEMENT_TO_PLINK,   SA_FAIL,    SEL_FAIL,  SEL_FAIL,   RE,          SREF_EPR_RE,  ERROR  },
      /*  8. EprEle    */  {RE,            RC,          RC,         ELEMENT_TO_PLINK,   SA_FAIL,    SEL_FAIL,  SEL_FAIL,   RE,          RE,           ERROR  },
      /*  9. Unknown   */  {ERROR,         ERROR,       ERROR,      ERROR,              ERROR,      ERROR,     ERROR,      ERROR,       ERROR,        ERROR  },

      // Note: PLink can never be a src since the &lt;from&gt; variant MUST specify either the myRole or the partnerRole
      // Note: Attribute will never be src since we'll convert it to string in copy operation
   };

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyStrategyFactory#createStrategy(org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperation, java.lang.Object, java.lang.Object)
    */
   public IAeCopyStrategy createStrategy(IAeCopyOperation aCopyOperation, Object aFromData, Object aToData) throws AeBpelException
   {
      int fromType = getFromTypeCode(aFromData);
      int toType = getToTypeCode(aToData);
      AeStrategyIndex index = new AeStrategyIndex(aFromData, fromType, aToData, toType);
      adjustIndex(index);
      int strategyIndex = STRATEGY_TABLE[index.getFromType()][index.getToType()];
      // If the copy operation is a virtual copy operation then it we won't
      // throw a fault if the keepSrcElementName is enabled and it's not an
      // element to element copy operation. The reason for this is that it is
      // impossible to tell in all cases of a fromPart/toPart if the data will
      // be an element. For example: an anytype variable that is being used in
      // a toPart. The variable may or may not contain element data.
      if (!aCopyOperation.isVirtual() && aCopyOperation.isKeepSrcElementName() && strategyIndex != RE)
      {
         throw new AeSelectionFailureException(aCopyOperation.getContext().getBPELNamespace(),
               AeSelectionFailureException.KEEP_SRC_ELEMENT_ERROR);
      }
      return STRATEGIES[strategyIndex];
   }
   
   /**
    * Adjusts the index. No op here but subclasses may provide adjustments
    * @param aIndex
    */
   protected void adjustIndex(AeStrategyIndex aIndex)
   {
   }
   
   /**
    * Simple struct with getters and setters for the from and to specs and data
    */
   protected static class AeStrategyIndex
   {
      /** type of the from data */
      private int mFromType;
      /** from data */
      private Object mFromData;
      /** type of the to data */
      private int mToType;
      /** to data */
      private Object mToData;
      
      /**
       * Ctor
       * @param aFromData
       * @param aFromType
       * @param aToData
       * @param aToType
       */
      public AeStrategyIndex(Object aFromData, int aFromType, Object aToData, int aToType)
      {
         setFromData(aFromData);
         setFromType(aFromType);
         setToData(aToData);
         setToType(aToType);
      }
      
      /**
       * @return the fromType
       */
      public int getFromType()
      {
         return mFromType;
      }
      /**
       * @param aFromType the fromType to set
       */
      public void setFromType(int aFromType)
      {
         mFromType = aFromType;
      }
      /**
       * @return the toType
       */
      public int getToType()
      {
         return mToType;
      }
      
      /**
       * @param aToType the toType to set
       */
      public void setToType(int aToType)
      {
         mToType = aToType;
      }

      /**
       * @return the fromData
       */
      public Object getFromData()
      {
         return mFromData;
      }

      /**
       * @param aFromData the fromData to set
       */
      public void setFromData(Object aFromData)
      {
         mFromData = aFromData;
      }

      /**
       * @return the toData
       */
      public Object getToData()
      {
         return mToData;
      }

      /**
       * @param aToData the toData to set
       */
      public void setToData(Object aToData)
      {
         mToData = aToData;
      }
   }

   /**
    * Returns the data type code for the given object. This must be
    * an L-value capable of receiving the data from the copy operation.
    *
    * @param aToData
    */
   protected int getToTypeCode(Object aToData) throws AeBpelException
   {
      Object data = aToData;

      if (aToData instanceof IAeVariableDataWrapper)
      {
         IAeVariableDataWrapper wrapper = (IAeVariableDataWrapper) aToData;
         data = wrapper.getValue();
      }

      int type = getCommonTypeCode(data);
      // if element, then do a secondary check for <wsa:epr> element type
      if (type == ELEMENT_TYPE && isWsaEprElement( (Element) data ) )
      {
         type = ELEMENT_EPR;
      }
      else if (type == UNKNOWN_TYPE)
      {
         if (data instanceof Attr)
            type = ATTR_TYPE;
         else if (data instanceof IAePartnerLink)
            type = PLINK_TYPE;
      }
      return type;
   }

   /**
    * Returns the data type code for our source object.
    *
    * @param aFromData
    */
   protected int getFromTypeCode(Object aFromData) throws AeBpelException
   {
      int type = getCommonTypeCode(aFromData);
      // if element, then do a secondary check for service-ref with child wsa:epr.
      if (type == ELEMENT_TYPE && isServiceRefElement( (Element)aFromData )
            && isWsaEprElement( AeXmlUtil.getFirstSubElement( (Element)aFromData ) ) )
      {
         type = ELEMENT_SREF_EPR;
      }
      return type;
   }

   /**
    * Returns the data type code for the types common to &lt;from&gt; and &lt;to&gt; targets
    * @param data
    */
   protected int getCommonTypeCode(Object data)
   {
      if (data == null)
         return NULL;
      else if (data instanceof Element)
         return ELEMENT_TYPE;
      else if (data instanceof List)
         return LIST_TYPE;
      else if (
            data instanceof QName ||
            data instanceof String ||
            data instanceof Text ||
            data instanceof Number ||
            data instanceof Boolean ||
            data instanceof IAeSchemaType ||
            data instanceof Date)
         return TEXT_TYPE;
      else if (data instanceof IAeMessageVariableWrapper)
         return MSG_DATA;
      else
         return UNKNOWN_TYPE;
   }

   /**
    * Returns true if the given element is a ws-bpel service-ref element.
    * @param aElement
    * @return true if the element is service-ref element.
    */
   protected static boolean isServiceRefElement(Element aElement)
   {
      return aElement != null
         && IAeBPELConstants.WS_BPEL_SERVICE_REF.getLocalPart().equals(aElement.getLocalName())
         && IAeBPELConstants.WS_BPEL_SERVICE_REF.getNamespaceURI().equals(aElement.getNamespaceURI()) ;

   }

   /**
    * Returns true if the given element is a wsa-epr element.
    * @param aElement
    * @return true if the element is a EndpointReference element.
    */
   protected static boolean isWsaEprElement(Element aElement)
   {
     return AeEndpointFactory.isEndpointReferenceElement(aElement);
   }
}

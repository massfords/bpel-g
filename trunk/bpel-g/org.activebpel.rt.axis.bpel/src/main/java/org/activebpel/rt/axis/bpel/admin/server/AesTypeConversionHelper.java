//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/admin/server/AesTypeConversionHelper.java,v 1.4 2008/02/17 21:29:26 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.admin.server;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.axis.bpel.admin.types.AesAttachmentAttribute;
import org.activebpel.rt.axis.bpel.admin.types.AesAttachmentAttributeList;
import org.activebpel.rt.axis.bpel.admin.types.AesAttachmentItemNumberList;
import org.activebpel.rt.axis.bpel.admin.types.AesBreakpointInstanceDetail;
import org.activebpel.rt.axis.bpel.admin.types.AesBreakpointList;
import org.activebpel.rt.axis.bpel.admin.types.AesProcessFilter;
import org.activebpel.rt.axis.bpel.admin.types.AesProcessInstanceDetail;
import org.activebpel.rt.axis.bpel.admin.types.AesProcessListResult;
import org.activebpel.rt.axis.bpel.admin.types.AesProcessListResultRowDetails;
import org.activebpel.rt.bpel.impl.list.AeProcessFilter;
import org.activebpel.rt.bpel.impl.list.AeProcessInstanceDetail;
import org.activebpel.rt.bpel.impl.list.AeProcessListResult;
import org.activebpel.rt.bpel.server.admin.rdebug.server.AeAttachmentAttributeList;
import org.activebpel.rt.bpel.server.admin.rdebug.server.AeBreakpointInstanceDetail;
import org.activebpel.rt.bpel.server.admin.rdebug.server.AeBreakpointList;

public class AesTypeConversionHelper
{
   /**
    * Converts internal process instance detail to one that can be handled by EngineAdmin API.
    * @param aDetail
    */
   public static AesProcessInstanceDetail convertProcessDetail(AeProcessInstanceDetail aDetail)
   {
      if (aDetail == null)
         return null;
      
      AesProcessInstanceDetail tgtDetail = new AesProcessInstanceDetail();
      tgtDetail.setName(aDetail.getName());
      tgtDetail.setProcessId(aDetail.getProcessId());
      tgtDetail.setState(aDetail.getState());
      tgtDetail.setStateReason(aDetail.getStateReason());
      
      if (aDetail.getStarted() != null)
      {
         Calendar cal = Calendar.getInstance();
         cal.setTimeInMillis(aDetail.getStarted().getTime());
         tgtDetail.setStarted(cal);
      }
      
      if (aDetail.getEnded() != null)
      {
         Calendar cal = Calendar.getInstance();
         cal.setTimeInMillis(aDetail.getEnded().getTime());
         tgtDetail.setEnded(cal);
      }
      
      return tgtDetail;
   }
   
   /**
    * Converts internal process list result to one that can be handled by EngineAdmin API.
    * @param aResult
    */
   public static AesProcessListResult convertProcessListResult(AeProcessListResult aResult)
   {
      if (aResult == null)
         return null;
      
      int rowCount = (aResult.getRowDetails() != null ? aResult.getRowDetails().length : 0);
      AesProcessInstanceDetail detailArray[] = new AesProcessInstanceDetail[rowCount];
      for (int i=0; i < rowCount; i++)
         detailArray[i] = convertProcessDetail(aResult.getRowDetails()[i]);

      AesProcessListResultRowDetails details = new AesProcessListResultRowDetails();
      details.setItem(detailArray);      
      
      AesProcessListResult tgtResult = new AesProcessListResult();
      tgtResult.setCompleteRowCount(aResult.isCompleteRowCount());
      tgtResult.setEmpty(aResult.isEmpty());
      tgtResult.setTotalRowCount(aResult.getTotalRowCount());
      tgtResult.setRowDetails(details);
      
      return tgtResult;
   }

   /**
    * Converts process filter from EngineAdmin API to internal version.  
    * @param aFilter
    */
   public static AeProcessFilter convertProcessFilter(AesProcessFilter aFilter)
   {
      if (aFilter == null)
         return null;
      
      AeProcessFilter filter = new AeProcessFilter();
      filter.setAdvancedQuery(aFilter.getAdvancedQuery());
      filter.setListStart(aFilter.getListStart());
      filter.setMaxReturn(aFilter.getMaxReturn());
      filter.setProcessName(aFilter.getProcessName());
      filter.setProcessState(aFilter.getProcessState());
      
      if (aFilter.getProcessCreateStart() != null)
         filter.setProcessCreateStart(aFilter.getProcessCreateStart().getTime());
      if (aFilter.getProcessCreateEnd() != null)
         filter.setProcessCreateEnd(aFilter.getProcessCreateEnd().getTime());
      if (aFilter.getProcessCompleteStart() != null)
         filter.setProcessCompleteStart(aFilter.getProcessCompleteStart().getTime());
      if (aFilter.getProcessCompleteEnd() != null)
         filter.setProcessCompleteEnd(aFilter.getProcessCompleteEnd().getTime());
      
      return filter;
   }

   /**
    * Converts breakpoint list from EngineAdmin API to internal version.
    * @param aList
    */
   public static AeBreakpointList convertBreakpoints(AesBreakpointList aList)
   {
      if (aList == null)
         return null;
      
      int rowCount = aList.getTotalRowCount();
      AesBreakpointInstanceDetail srcDetails[] = aList.getRowDetails().getItem();
      AeBreakpointInstanceDetail tgtDetails[] = new AeBreakpointInstanceDetail[rowCount];
      for (int i=0; i<rowCount; i++)
      {
         tgtDetails[i] = new AeBreakpointInstanceDetail();
         tgtDetails[i].setProcessName(srcDetails[i].getProcessName());
         tgtDetails[i].setNodePath(srcDetails[i].getNodePath());
      }
      
      AeBreakpointList breakpoints = new AeBreakpointList();
      breakpoints.setRowDetails(tgtDetails);
      breakpoints.setTotalRowCount(rowCount);
      
      return breakpoints;
   }
   
  
   /**
    * Converts attachment attributes from a Map tp EngineAdmin API.
    * @param aList
    * @return Map 
    */
   public static Map convertAttachmentAttributes(AesAttachmentAttributeList aList)
   {
      if (aList == null)
         return null;
      
      int rowCount = aList.getAttribute().length;
      AesAttachmentAttribute srcAttributes[] = aList.getAttribute();
      Map attributes = new HashMap();
      for (int i=0; i<rowCount; i++)
      {
         attributes.put(srcAttributes[i].getAttributeName(),srcAttributes[i].getAttributeValue());
      }
      return attributes;
   }
   
   /**
    * Converts EngineAdmin API AeAttachmentAttributeList to AesAttachmentAttributeList
    * @param aList
    */
   public static AesAttachmentAttributeList convertAttachmentAttributes(AeAttachmentAttributeList aList)
   {  
      // Convert attribute AeAttachmentAttributeList to AesAttachmentAttributeList
      AesAttachmentAttribute tgtAttributes[] = new AesAttachmentAttribute[aList.getAttributeName().length];
    
      for(int i = 0;i < aList.getAttributeName().length;i++)
      {
         tgtAttributes[i] = new AesAttachmentAttribute();
         tgtAttributes[i].setAttributeName((String)aList.getAttributeName()[i].getAttributeName());
         tgtAttributes[i].setAttributeValue((String)aList.getAttributeName()[i].getAttributeValue());
      }
      AesAttachmentAttributeList attributes = new AesAttachmentAttributeList();
      attributes.setAttribute(tgtAttributes);
     
      return attributes;  
   }
   
   /**
    * Converts attachment item numbers from EngineAdmin API to internal version.
    * Externally item numbers start from one, internally item numbers start from zero
    * @param aList
    */
   public static int[] convertAttachmentItemNumbers(AesAttachmentItemNumberList aList)
   {
      if (aList == null)
         return null;
      
      int rowCount = aList.getItemNumber().length;
      Integer srcItemNumbers[] = aList.getItemNumber();
      int tgtItemNumbers[] = new int[rowCount];
      for (int i=0; i<rowCount; i++)
      {
         tgtItemNumbers[i] = srcItemNumbers[i].intValue() - 1;
      }
      return tgtItemNumbers;
   }
}
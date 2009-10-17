<%@page contentType="text/html; charset=UTF-8" import="org.activebpel.rt.bpel.impl.*,org.activebpel.rt.bpel.server.engine.*,org.activebpel.rt.bpel.server.admin.*,javax.xml.namespace.QName,java.text.*"  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>

<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

   <%-- Use UTF-8 to decode request parameters. --%>
   <ae:RequestEncoding value="UTF-8" />

   <%-- initialize the process bean which drives the --%>
   <%-- dynamic content of the page              --%>
   <jsp:useBean id="processBean" class="org.activebpel.rt.bpeladmin.war.web.AeProcessListingBean" />

   <%-- if the page is being reloaded as the result --%>
   <%-- of a submit, update the bean with the form  --%>
   <%-- properties                          --%>
   <ae:IfParamMatches property="isSubmit" value="true">
     <jsp:setProperty name="processBean" property="state" param="processState" />
     <jsp:setProperty name="processBean" property="rowStart" param="rowOffset" />
     <jsp:setProperty name="processBean" property="qname" param="processQName" />
     <jsp:setProperty name="processBean" property="rowCount" param="processCount" />
     <ae:SetDate name="processBean" property="createStartDate" patternKey="date_pattern" param='processCreateStart'/>
     <ae:SetDate name="processBean" property="createEndDate" patternKey="date_pattern" param='processCreateEnd'/>
     <ae:SetDate name="processBean" property="completeStartDate" patternKey="date_pattern" param='processCompleteStart'/>
     <ae:SetDate name="processBean" property="completeEndDate" patternKey="date_pattern" param='processCompleteEnd'/>
   </ae:IfParamMatches>

   <%-- this signals the bean that all of the form --%>
   <%-- properties have been set - it will now    --%>
   <%-- fetch the detail rows                 --%>
   <jsp:setProperty name="processBean" property="finished" value="true" />

   <jsp:include page="header_head.jsp" />

   <jsp:include page="calendarScript.jsp" />

   <script>
      function clearForm()
      {
         document.forms["filterForm"].processQName.value="";
         document.forms["filterForm"].processCreateStart.value="";
         document.forms["filterForm"].processCreateEnd.value="";
         document.forms["filterForm"].processCompleteStart.value="";
         document.forms["filterForm"].processCompleteEnd.value="";
         document.forms["filterForm"].processState[0].checked = true;
      }
   </script>


<body>

     <table border="0" cellpadding="0" cellspacing="0" width="100%" align="center">
       <tr>
           <td valign="top" width="20%">
             <jsp:include page="header_nav.jsp" />
           </td>

           <!-- spacer between nav and main -->
           <td width="3%"></td>

           <td valign="top">
             <form name="filterForm" action="active_processes.jsp" method="get">
               <table border="0" cellpadding="0" cellspacing="0" width="100%" align="left">
                 <tr>
                   <th colspan="5" class="pageHeaders" align="left" nowrap="true"><ae:GetResource name="active_processes" /></th>
                 </tr>
                 <tr height="1">
                   <td height="1" colspan="5" class="gridLines"></td>
                 </tr>

                 <!-- If records found -->
                 <ae:IfTrue name="processBean" property="populated" >
                   <tr>
                     <th width="10%" class="columnHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="id" />&nbsp;</th>
                     <th width="30%" class="columnHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="process_name" />&nbsp;</th>
                     <th width="20%" class="columnHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="start_date" />&nbsp;</th>
                     <th width="20%" class="columnHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="end_date" />&nbsp;</th>
                     <th width="20%" class="columnHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="state" />&nbsp;</th>
                   </tr>

                   <ae:IndexedProperty name="processBean" id="rowDetail" property="instanceDetail" indexedClassName="org.activebpel.rt.bpeladmin.war.web.AeProcessInstanceDetailWrapper" >
                     <tr>
                       <td width="10%">&nbsp;<a target="aeprocessview" href='processview/processview_detail.jsp?pid=<jsp:getProperty name="rowDetail" property="processId" />'><jsp:getProperty name="rowDetail" property="processId" /></a></td>
                       <td width="30%">&nbsp;<a target="aeprocessview" href='processview/processview_detail.jsp?pid=<jsp:getProperty name="rowDetail" property="processId" />'><jsp:getProperty name="rowDetail" property="localPart" /></a></td>
                       <td width="20%">&nbsp;<ae:DateFormatter name="rowDetail" property="started" patternKey="date_time_pattern" /></td>
                       <td width="20%">&nbsp;<ae:DateFormatter name="rowDetail" property="ended" patternKey="date_time_pattern" /></td>
                       <td width="20%">&nbsp;<jsp:getProperty name="rowDetail" property="stateString" /></td>
                     </tr>
                     <tr height="1">
                       <td colspan="5" height="1" class="tabular"></td>
                     </tr>
                   </ae:IndexedProperty>

                   <tr height="1">
                     <td height="1" colspan="5" class="gridLines"></td>
                   </tr>
                   <tr>
                     <td colspan="2">
                      <table border="0" cellpadding="0" cellspacing="0">
                        <tr>
                          <td nowrap="true" class="filterFields"><select name="processCount" id="records per page" class="filterFields" onChange='document.forms["filterForm"].submit()'>
                             <option value="10" <ae:IfPropertyMatches name="processBean" property="rowCount" value="10" classType="int">selected="true"</ae:IfPropertyMatches>>10</option>
                             <option value="20" <ae:IfPropertyMatches name="processBean" property="rowCount" value="20" classType="int">selected="true"</ae:IfPropertyMatches>>20</option>
                             <option value="30" <ae:IfPropertyMatches name="processBean" property="rowCount" value="30" classType="int">selected="true"</ae:IfPropertyMatches>>30</option>
                             <option value="40" <ae:IfPropertyMatches name="processBean" property="rowCount" value="40" classType="int">selected="true"</ae:IfPropertyMatches>>40</option>
                             <option value="50" <ae:IfPropertyMatches name="processBean" property="rowCount" value="50" classType="int">selected="true"</ae:IfPropertyMatches>>50</option></select></td>
                          <td nowrap="true" class="iconText">&nbsp;<label for="records per page"><ae:GetResource name="records_per_page" /></label></td>
                        </tr>
                      </table>
                     </td>
                     <td colspan="1" align="center" nowrap="true" class="iconText"><ae:GetResource name="results" />&nbsp;<jsp:getProperty name="processBean" property="displayStart" />&nbsp;-&nbsp;<jsp:getProperty name="processBean" property="displayEnd" />&nbsp;<ae:GetResource name="of" />&nbsp;<jsp:getProperty name="processBean" property="totalRowCount" /><jsp:getProperty name="processBean" property="totalRowCountSuffix" /></td>
                     <td align="right" nowrap="true" class="iconText">
                        &nbsp;
                        <ae:IfTrue name="processBean" property="previousPage" >
                          <span style="font-size:10pt" >
                            <a href='javascript:document.forms["filterForm"].rowOffset.value=0;document.forms["filterForm"].submit()' title='<ae:GetResource name="first_page" />'>&laquo;&laquo;&nbsp;</a>
                            <a href='javascript:document.forms["filterForm"].rowOffset.value=<jsp:getProperty name="processBean" property="previousPageOffset" />;document.forms["filterForm"].submit()' title='<ae:GetResource name="previous_page" />'>&laquo;&nbsp;</a>
                          </span>
                        </ae:IfTrue>
                     </td>
                     <td align="right" nowrap="true" class="iconText">
                        &nbsp;
                        <ae:IfTrue name="processBean" property="nextPage" >
                          <span style="font-size:10pt" >
                            <a href='javascript:document.forms["filterForm"].rowOffset.value=<jsp:getProperty name="processBean" property="nextPageOffset" />;document.forms["filterForm"].submit()' title='<ae:GetResource name="next_page" />'>&raquo;</a>&nbsp;
                            <a href='javascript:gotoLast(<jsp:getProperty name="processBean" property="totalRowCount" />,<jsp:getProperty name="processBean" property="rowCount" />);' title='<ae:GetResource name="last_page" />'>&nbsp;&raquo;&raquo;</a>
                          </span>
                        </ae:IfTrue>
                     </td>
                   </tr>
                 </ae:IfTrue>

                 <!-- If no records found -->
                 <ae:IfFalse name="processBean" property="populated" >
                   <tr>
                     <th colspan="5" class="titleHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="no_processes_matching" />&nbsp;</th>
                   </tr>
                   <tr height="1">
                     <td height="1" colspan="5" class="gridLines"></td>
                   </tr>
                 </ae:IfFalse>

                 <!-- Spacer between list and form -->
                 <tr height="1">
                   <td height="1" colspan="5" class="gridLines"></td>
                 </tr>
                 <tr height="1">
                 <tr>
                   <td height="10" colspan="5"></td>
                 </tr>


                 <!-- Selection Form -->
                 <tr><td colspan="5" align="right">
                   <table width="65%">
                      <tr>
                        <td colspan="2" class="titleHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="selection_filter" />&nbsp;</th>
                      <tr>
                      <tr height="1">
                        <td height="1" colspan="2" class="gridLines"></td>
                      </tr>
                      <tr>
                        <td class="labelHeaders" align="left" nowrap="true" width=20%">&nbsp;<ae:GetResource name="state_colon" />&nbsp;</td>
                        <td align="left"><input type="radio" name="processState" <ae:IfTrue name="processBean" property="activeState" >checked</ae:IfTrue> value="0"/><ae:GetResource name="all" />&nbsp;<input type="radio" name="processState" <ae:IfTrue name="processBean" property="runningState" >checked</ae:IfTrue>  value="1"/><ae:GetResource name="running" />&nbsp;<input type="radio" name="processState" <ae:IfTrue name="processBean" property="completeState" >checked</ae:IfTrue> value="2"/><ae:GetResource name="complete" />&nbsp;<input type="radio" name="processState" <ae:IfTrue name="processBean" property="compensatableState" >checked</ae:IfTrue> value="9"/><ae:GetResource name="compensatable" />&nbsp;<input type="radio" name="processState" <ae:IfTrue name="processBean" property="faultedState" >checked</ae:IfTrue> value="3"/><ae:GetResource name="faulted" />&nbsp;</td>
                      </tr>
                      <tr height="1">
                        <td colspan="2" height="1" class="tabular"></td>
                      </tr>
                      <tr>
                        <td class="labelHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="created_between" />&nbsp;</td>
                        <td align="left"><input type="text" name="processCreateStart" value='<ae:DateFormatter name="processBean" property="createStartDate" patternKey="date_pattern" />' size="10"/>&nbsp;<img src="images/calendar.gif" onClick="javascript:displayCalendar(document.forms['filterForm'].processCreateStart)" />&nbsp;<ae:GetResource name="and" />&nbsp;<input type="text" name="processCreateEnd" value='<ae:DateFormatter name="processBean" property="createEndDate" patternKey="date_pattern" />' size="10"/>&nbsp;<image src="images/calendar.gif" onClick="javascript:displayCalendar(document.forms['filterForm'].processCreateEnd)" />&nbsp;<span class="iconText"><ae:GetResource name="date_format_example" /></span></td>
                      </tr>
                      <tr height="1">
                        <td colspan="2" height="1" class="tabular"></td>
                      </tr>
                      <tr>
                        <td class="labelHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="completed_between" />&nbsp;</td>
                        <td align="left"><input type="text" name="processCompleteStart" value='<ae:DateFormatter name="processBean" property="completeStartDate" patternKey="date_pattern" />' size="10"/>&nbsp;<img src="images/calendar.gif" onClick="javascript:displayCalendar(document.forms['filterForm'].processCompleteStart)" />&nbsp;<ae:GetResource name="and" />&nbsp;<input type="text" name="processCompleteEnd" value='<ae:DateFormatter name="processBean" property="completeEndDate" patternKey="date_pattern" />' size="10"/>&nbsp;<image src="images/calendar.gif" onClick="javascript:displayCalendar(document.forms['filterForm'].processCompleteEnd)" />&nbsp;<span class="iconText"><ae:GetResource name="date_format_example" /></span></td>
                      </tr>
                      <tr height="1">
                        <td colspan="2" height="1" class="tabular"></td>
                      </tr>
                      <tr>
                        <td class="labelHeaders" align="left">&nbsp;<ae:GetResource name="name_colon" />&nbsp;</td>
                        <td align="left"><input type="text" name="processQName" value='<jsp:getProperty name="processBean" property="qname" />' size="30"/></td>
                      </tr>
                      <tr height="1">
                        <td colspan="2" height="1" class="tabular"></td>
                      </tr>
                      <ae:IfTrue name="processBean" property="statusDetailAvailable">
                        <tr>
                          <td align="left" colspan="2"><jsp:getProperty name="processBean" property="statusDetail"/></td>
                        </tr>
                        <tr height="1">
                          <td height="1" colspan="2" class="tabular"></td>
                        </tr>
                      </ae:IfTrue>
                      <tr height="1">
                        <td colspan="2" height="1"></td>
                      </tr>
                      <tr>
                        <td colspan="2" align="center"><input type="submit" value=<ae:GetResource name="submit" /> />&nbsp;<input type="button" value=<ae:GetResource name="clear" /> onclick="clearForm()"/></td>
                      </tr>
                      <tr height="1">
                        <td colspan="2" height="1"></td>
                      </tr>
                      <tr height="1">
                        <td height="1" colspan="2" class="gridLines"></td>
                      </tr>
                      <tr>
                        <td colspan="2" height="1"></td>
                      </tr>
                   </table>
                 </td></tr>
               </table>
                   <input type="hidden" name="rowOffset" value="0" />
                   <input type="hidden" name="isSubmit" value="true" />
             </form>
           </td>


         <!-- main and right margin      -->
         <td width="3%"></td>
       </tr>
     </table>

   <br>
   <jsp:include page="footer.jsp" />
</body>
</html>

<%@page contentType="text/html; charset=UTF-8" import="org.activebpel.rt.bpel.impl.*,org.activebpel.rt.bpel.server.engine.*,org.activebpel.rt.bpel.server.admin.*,javax.xml.namespace.QName,java.text.*"  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>

<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

   <%-- Use UTF-8 to decode request parameters. --%>
   <ae:RequestEncoding value="UTF-8" />

   <%-- initialize the process bean which drives the --%>
   <%-- dynamic content of the page              --%> 
   <jsp:useBean id="alarmBean" class="org.activebpel.rt.bpeladmin.war.web.AeAlarmListingBean" />
   
   <%-- if the page is being reloaded as the result --%>
   <%-- of a submit, update the bean with the form  --%>
   <%-- properties                          --%>
   <ae:IfParamMatches property="isSubmit" value="true">
     <jsp:setProperty name="alarmBean" property="rowCount" param="alarmCount" />
     <jsp:setProperty name="alarmBean" property="rowStart" param="rowOffset" />
     <jsp:setProperty name="alarmBean" property="qname" param="processQName" />
     <jsp:setProperty name="alarmBean" property="processId" param="processId" />
     <ae:SetDate name="alarmBean" property="startDate" param="alarmStart" patternKey="date_time_pattern4" />
     <ae:SetDate name="alarmBean" property="endDate" param="alarmEnd" patternKey="date_time_pattern4" />
   </ae:IfParamMatches>
   
   <%-- this signals the bean that all of the form --%>
   <%-- properties have been set - it will now    --%>
   <%-- fetch the detail rows                 --%>
   <jsp:setProperty name="alarmBean" property="finished" value="true" />

   <jsp:include page="header_head.jsp" />
   
   <script>
   
     function clearForm()
     {
       document.forms["filterForm"].processId.value="";
       document.forms["filterForm"].processQName.value="";
       document.forms["filterForm"].alarmStart.value="";
       document.forms["filterForm"].alarmEnd.value="";
     }
     
     function gotoLast( aTotalRows, aRowCount )
     {
       var lastRows = aTotalRows%aRowCount;
       if( lastRows == 0 )
       {
         lastRows = aRowCount;
       }
       document.forms["filterForm"].rowOffset.value=aTotalRows-lastRows;
       document.forms["filterForm"].submit();
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
             <form name="filterForm" action="alarm_queue.jsp" method="get">
               <table border="0" cellpadding="0" cellspacing="0" width="100%" align="left">
                 <tr>
                   <th colspan="3" class="pageHeaders" align="left" nowrap="true"><ae:GetResource name="alarm_queue" /></th>
                 </tr>
                 <tr height="1">
                   <td height="1" colspan="3" class="gridLines"></td>
                 </tr>

                 <!-- If records found -->
                 <ae:IfTrue name="alarmBean" property="populated" >
                   <tr>
                     <th class="columnHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="process_id" />&nbsp;</th>
                     <th class="columnHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="process_name" />&nbsp;</th>
                     <th class="columnHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="deadline" />&nbsp;</th>
                   </tr>

                   <ae:IndexedProperty name="alarmBean" id="rowDetail" property="alarmInstance" indexedClassName="org.activebpel.rt.bpel.impl.list.AeAlarmExt" >
                     <tr>
                       <td>&nbsp;<a target="aeprocessview" href='processview/processview_detail.jsp?pid=<jsp:getProperty name="rowDetail" property="processId" />&pathid=<jsp:getProperty name="rowDetail" property="pathId" />'><jsp:getProperty name="rowDetail" property="processId" /></a></td>
                       <td>&nbsp;<a target="aeprocessview" href='processview/processview_detail.jsp?pid=<jsp:getProperty name="rowDetail" property="processId" />&pathid=<jsp:getProperty name="rowDetail" property="pathId" />'><jsp:getProperty name="rowDetail" property="processName" /></a></td>
                       <td>&nbsp;<ae:DateFormatter name="rowDetail" property="deadline" patternKey="date_time_pattern4" /></td>
                     </tr>
                     <tr height="1">
                       <td colspan="3" height="1" class="tabular"></td>
                     </tr>
                   </ae:IndexedProperty>

                   <tr height="1">
                     <td height="1" colspan="3" class="gridLines"></td>
                   </tr>
                   <tr>
                     <td>
                      <table border="0" cellpadding="0" cellspacing="0">
                        <tr>
                          <td nowrap="true" class="filterFields"><select name="alarmCount" id="records per page" class="filterFields" onChange='document.forms["filterForm"].submit()'>
                             <option value="10" <ae:IfPropertyMatches name="alarmBean" property="rowCount" value="10" classType="int">selected="true"</ae:IfPropertyMatches>>10</option>
                             <option value="20" <ae:IfPropertyMatches name="alarmBean" property="rowCount" value="20" classType="int">selected="true"</ae:IfPropertyMatches>>20</option>
                             <option value="30" <ae:IfPropertyMatches name="alarmBean" property="rowCount" value="30" classType="int">selected="true"</ae:IfPropertyMatches>>30</option>
                             <option value="40" <ae:IfPropertyMatches name="alarmBean" property="rowCount" value="40" classType="int">selected="true"</ae:IfPropertyMatches>>40</option>
                             <option value="50" <ae:IfPropertyMatches name="alarmBean" property="rowCount" value="50" classType="int">selected="true"</ae:IfPropertyMatches>>50</option></select></td>
                          <td nowrap="true" class="iconText">&nbsp;<label for="records per page"><ae:GetResource name="records_per_page" /></label></td>
                        </tr>
                      </table>
                     </td>
                     <td colspan="1" align="center" nowrap="true" class="iconText"><ae:GetResource name="results" />&nbsp;<jsp:getProperty name="alarmBean" property="displayStart" />&nbsp;-&nbsp;<jsp:getProperty name="alarmBean" property="displayEnd" />&nbsp;<ae:GetResource name="of" />&nbsp;<jsp:getProperty name="alarmBean" property="totalRowCount" /></td>
                     <td align="right" nowrap="true" class="iconText">
                        &nbsp;
                        <ae:IfTrue name="alarmBean" property="previousPage" >
                          <span style="font-size:10pt" >
                            <a href='javascript:document.forms["filterForm"].rowOffset.value=0;document.forms["filterForm"].submit()' title='<ae:GetResource name="first_page" />'>&laquo;&laquo;&nbsp;</a>
                            <a href='javascript:document.forms["filterForm"].rowOffset.value=<jsp:getProperty name="alarmBean" property="previousPageOffset" />;document.forms["filterForm"].submit()' title='<ae:GetResource name="previous_page" />'>&laquo;&nbsp;</a>
                          </span>
                        </ae:IfTrue>
                     </td>
                     <td align="right" nowrap="true" class="iconText">
                        &nbsp;
                        <ae:IfTrue name="alarmBean" property="nextPage" >
                          <span style="font-size:10pt" >
                            <a href='javascript:document.forms["filterForm"].rowOffset.value=<jsp:getProperty name="alarmBean" property="nextPageOffset" />;document.forms["filterForm"].submit()' title='<ae:GetResource name="next_page" />'>&raquo;</a>&nbsp;
                            <a href='javascript:gotoLast(<jsp:getProperty name="alarmBean" property="totalRowCount" />,<jsp:getProperty name="alarmBean" property="rowCount" />);' title='<ae:GetResource name="last_page" />'>&nbsp;&raquo;&raquo;</a>
                          </span>
                        </ae:IfTrue>
                     </td>
                   </tr>
                 </ae:IfTrue>

                 <!-- If no records found -->
                 <ae:IfFalse name="alarmBean" property="populated" >
                   <tr>
                     <th colspan="3" class="titleHeaders" align="left" nowrap>&nbsp;<ae:GetResource name="no_matching_alarms" />&nbsp;</th>
                   </tr>
                   <tr height="1">
                     <td height="1" colspan="3" class="gridLines"></td>
                   </tr>
                 </ae:IfFalse>

                 <!-- Spacer between list and form -->
                 <tr height="1">
                   <td height="1" colspan="3" class="gridLines"></td>
                 </tr>
                 
                 <!-- Selection Form -->
                 <tr><td colspan="3" align="right">
                   <table width="60%">
                      <tr>
                        <td colspan="2" class="titleHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="selection_filter" />&nbsp;</th>
                      <tr>
                      <tr height="1">
                        <td height="1" colspan="2" class="gridLines"></td>
                      </tr>
                      <tr>
                        <td class="labelHeaders" align="left" nowrap>&nbsp;<ae:GetResource name="deadline_between" />&nbsp;</td>
                        <td align="left"><input type="text" name="alarmStart" value='<ae:DateFormatter name="alarmBean" property="startDate" patternKey="date_time_pattern4" />' size="16"/>&nbsp;<ae:GetResource name="and" />&nbsp;<input type="text" name="alarmEnd" value='<ae:DateFormatter name="alarmBean" property="endDate" patternKey="date_time_pattern4" />' size="16"/><br/>&nbsp;<span class="iconText" align="right"><ae:GetResource name="alarm_deadline_example" /></span></td>
                      </tr>
                      <tr height="1">
                        <td colspan="2" height="1" class="tabular"></td>
                      </tr>
                       <tr>
                         <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="process_id_colon" />&nbsp;</td>
                         <td align="left" colspan="3"><input type="text" name="processId" size="20" value='<jsp:getProperty name="alarmBean" property="processId" />'/></td>
                       </tr>
                      <tr height="1">
                        <td colspan="2" height="1" class="tabular"></td>
                      </tr>
                      <tr>
                        <td class="labelHeaders" align="left" nowrap>&nbsp;<ae:GetResource name="process_name_colon" />&nbsp;</td>
                        <td align="left"><input type="text" name="processQName" value='<jsp:getProperty name="alarmBean" property="qname" />' size="30"/></td>
                      </tr>
                      <tr height="1">
                        <td colspan="2" height="1" class="tabular"></td>
                      </tr>
                      <tr height="1">
                        <td colspan="2" height="1"></td>
                      </tr>
                      <ae:IfTrue name="alarmBean" property="statusDetailAvailable">
                      <tr>
                        <td align="left" colspan="2"><jsp:getProperty name="alarmBean" property="statusDetail"/></td>
                      </tr>
                      <tr height="1">
                        <td height="1" colspan="2" class="tabular"></td>
                      </tr>
                    </ae:IfTrue>
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

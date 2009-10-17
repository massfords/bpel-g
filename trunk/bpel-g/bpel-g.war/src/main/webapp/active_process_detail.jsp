<%@page contentType="text/html; charset=UTF-8" import="org.activebpel.rt.bpel.impl.*,org.activebpel.rt.bpel.server.engine.*,org.activebpel.rt.bpel.server.admin.*,javax.xml.namespace.QName,java.text.*"  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>

<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

   <%-- Use UTF-8 to decode request parameters. --%>
   <ae:RequestEncoding value="UTF-8" />

   <jsp:useBean id="processBean" class="org.activebpel.rt.bpeladmin.war.web.AeProcessInstanceDetailWrapper" >
      <jsp:setProperty name="processBean" property="strProcessId" param="pid" />
      <jsp:setProperty name="processBean" property="message" value="Log file not available for Process ID:" />
   </jsp:useBean>

   <ae:IfParamMatches property="ProcessAction" value="Terminate">
      <jsp:setProperty name="processBean" property="terminate" value="true" />
   </ae:IfParamMatches>
   <ae:IfParamMatches property="ProcessAction" value="Suspend">
      <jsp:setProperty name="processBean" property="suspend" value="true" />
   </ae:IfParamMatches>
   <ae:IfParamMatches property="ProcessAction" value="Resume">
      <jsp:setProperty name="processBean" property="resume" value="true" />
   </ae:IfParamMatches>

   <jsp:include page="header_head.jsp" />

   <body> 
   
      <table border="0" cellpadding="0" cellspacing="0" width="100%" align="center">
         <tr>
            <td valign="top" width="20%">
               <jsp:include page="header_nav.jsp" />
            </td>
      
            <!-- spacer between nav and main -->
            <td width="3%"></td>
   
            <td valign="top">
               <table border="0" cellpadding="0" cellspacing="0" width="100%" align="left">
                  <tr>
                     <th colspan="2" class="pageHeaders" align="left" nowrap="true"><ae:GetResource name="process_detail" /></th>
                  </tr>
                  <tr height="1">
                     <td height="1" colspan="2" class="gridLines"></td>
                  </tr>
                  <ae:IfTrue name="processBean" property="empty" >
                     <tr>
                        <th colspan="2" class="titleHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="process_id_colon" />&nbsp;<%= request.getParameter("pid") %>&nbsp;<ae:GetResource name="was_not_found" />&nbsp;</th>
                     </tr>
                  </ae:IfTrue>

                  <ae:IfFalse name="processBean" property="empty" >
                     <tr>
                        <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="id" />&nbsp;</td>
                        <td align="left">&nbsp;<jsp:getProperty name="processBean" property="processId" /></td>
                     </tr>
                     <tr height="1">
                        <td colspan="2" height="1" class="tabular"></td>
                     </tr>
                     <tr>
                        <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="name_colon" />&nbsp;</td>
                        <td align="left">&nbsp;<jsp:getProperty name="processBean" property="localPart" /></td>
                     </tr>
                     <tr height="1">
                        <td colspan="2" height="1" class="tabular"></td>
                     </tr>
                     <tr>
                       <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="namespace_colon" />&nbsp;</td>
                       <td align="left">&nbsp;<jsp:getProperty name="processBean" property="namespaceURI" /></td>
                     </tr>
                     <tr height="1">
                       <td colspan="2" height="1" class="tabular"></td>
                     </tr>
                     <tr>
                       <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="started" />&nbsp;</td>
                       <td align="left">&nbsp;<ae:DateFormatter name="processBean" property="started" patternKey="date_time_pattern2" /></td>
                     </tr>
                     <tr height="1">
                       <td colspan="2" height="1" class="tabular"></td>
                     </tr>
                     <tr>
                       <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="ended" />&nbsp;</td>
                       <td align="left">&nbsp;<ae:DateFormatter name="processBean" property="ended" patternKey="date_time_pattern2" /></td>
                     </tr>
                     <tr height="1">
                       <td colspan="2" height="1" class="tabular"></td>
                     </tr>
                     <tr>
                       <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="state_colon" />&nbsp;</td>
                       <td align="left">&nbsp;<jsp:getProperty name="processBean" property="stateString" /></td>
                     </tr>
                     <tr height="1">
                       <td colspan="2" height="1" class="tabular"></td>
                     </tr>
                     <tr height="1">
                       <td height="1" colspan="2" class="gridLines"></td>
                     </tr>
                     <tr>
                       <td colspan="4" height="1"></td>
                     </tr>
		             <!-- Process dispoisition form -->
			         <form name="ec_form" method="post" action="active_process_detail.jsp">
	                   <tr><td colspan="2" align="right">
			             <!-- Suspend process button  -->
		                 <ae:IfTrue name="processBean" property="suspendable" >
		                    <input type="submit" name="suspend" value=<ae:GetResource name="suspend" /> onClick='document.ec_form.ProcessAction.value="Suspend"' />
		                 </ae:IfTrue>
			             <!-- Resume process button  -->
		                 <ae:IfTrue name="processBean" property="resumable" >
  		                    <input type="submit" name="resume" value=<ae:GetResource name="resume" /> onClick='document.ec_form.ProcessAction.value="Resume"' />
		                 </ae:IfTrue>
			             <!-- Terminate process button  -->
		                 <ae:IfTrue name="processBean" property="terminatable" >
		                    <input type="submit" name="terminate" value=<ae:GetResource name="terminate" /> onClick='document.ec_form.ProcessAction.value="Terminate"' />
		                 </ae:IfTrue>
 	  	                 <input type="hidden" name="pid" value='<%=request.getParameter("pid")%>'/>
                       </td></tr>
                       
	                      <input type="hidden" name="ProcessAction" value="" />                       
   		             </form>
                     <tr><td colspan="2"></td></tr>
                     <tr>
                        <th colspan="2" class="titleHeaders" align="left" nowrap="true"><ae:GetResource name="log" /></th>
                     </tr>
                     <tr>
                        <td scope="col" colspan="2"><textarea name="textarea" style="width:99%" rows="15" wrap="OFF" readonly><jsp:getProperty name="processBean" property="log" /></textarea></td>
                     </tr>
                     <tr>
						<th colspan="2" align="left" nowrap="true"><ae:IfTrue name="processBean" property="logAvailable"><a href="getLog?pid=<%=request.getParameter("pid")%>"><ae:GetResource name="download_entire_log" /></a></ae:IfTrue></th>
                     </tr>
                  </ae:IfFalse>
               </table>
            </td>
      
            <!-- main and right margin       -->
            <td width="3%"></td>
         </tr>
      </table>
      <br> 
      <jsp:include page="footer.jsp" />
   </body>
</html>

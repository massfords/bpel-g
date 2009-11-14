<%@page contentType="text/html; charset=UTF-8" import="org.activebpel.rt.util.*,org.activebpel.rt.bpel.server.engine.*,org.activebpel.rt.bpel.server.admin.*,javax.xml.namespace.QName,java.text.*,org.activebpel.rt.bpel.*"  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>

<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

   <%-- Use UTF-8 to decode request parameters. --%>
   <ae:RequestEncoding value="UTF-8" />

   <jsp:useBean id="statusBean" class="org.activebpel.rt.bpeladmin.war.web.AeEngineStatusBean" />

   <ae:IfParamMatches property="action" value="start">
      <jsp:setProperty name="statusBean" property="start" value="true" />
   </ae:IfParamMatches>
   <ae:IfParamMatches property="action" value="stop">
      <jsp:setProperty name="statusBean" property="stop" value="true" />
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
                  <th colspan="3" class="pageHeaders" align="left" nowrap="true"><ae:GetResource name="home" /></th>
               </tr>
               <tr height="1">
                 <td height="1" colspan="3" class="gridLines"></td>
               </tr>
               <tr>
                 <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="date_started" />&nbsp;</td>
                 <td align="left" colspan="2"><ae:DateFormatter name="statusBean" property="startDate" patternKey="date_time_pattern" /></td>
               </tr>
               <tr height="1">
                 <td colspan="3" height="1" class="tabular"></td>
               </tr>
               <tr>
                 <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="deployed_processes_colon" />&nbsp;</td>
                 <td align="left" colspan="2"><jsp:getProperty name="statusBean" property="deployedProcessesSize" /></td>
               </tr>
               <tr height="1">
                 <td colspan="3" height="1" class="tabular"></td>
               </tr>
               <tr>
                 <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="description" />&nbsp;</td>
                 <td align="left" colspan="2"><jsp:getProperty name="statusBean" property="engineDescription" /></td>
               </tr>
               <tr height="1">
                 <td colspan="3" height="1" class="tabular"></td>
               </tr>
               <tr>
                 <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="status_colon" />&nbsp;</td>
                 <td align="left" colspan="2"><jsp:getProperty name="statusBean" property="engineStatus" /></td>
               </tr>
               <tr height="1">
                 <td colspan="3" height="1" class="tabular"></td>
               </tr>
               <tr>
                 <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="version_colon" />&nbsp;</td>
                 <td align="left" colspan="2"><jsp:getProperty name="statusBean" property="engineVersion" /></td>
               </tr>
               <tr height="1">
                 <td height="1" colspan="3" class="gridLines"></td>
               </tr>
               
               <tr height="10">
                 <td align="left" height="10" colspan="3"></td>
               </tr>
               
               <!-- Start and stop form -->
               <tr>
                 <td align="right" colspan="3">
		            <form name="start" method="post" action="home.jsp">
					   <ae:IfTrue name="statusBean" property="engineRunning">
				            <input type="hidden" name="action" value="stop" />
				            <input type="Submit" name="Stop" value='<ae:GetResource name="stop_engine" />' />
					   </ae:IfTrue>
					   <ae:IfFalse name="statusBean" property="engineRunning">
				            <input type="hidden" name="action" value="start" />
				            <input type="Submit" name="Start" value='<ae:GetResource name="start_engine" />' />
					   </ae:IfFalse>
		            </form>
                 </td>
               </tr>

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

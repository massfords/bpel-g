<%@page contentType="text/html; charset=UTF-8" import="org.activebpel.rt.bpel.impl.*,org.activebpel.rt.bpel.server.engine.*,org.activebpel.rt.bpel.server.admin.*,javax.xml.namespace.QName,java.text.*"  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>

<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

   <%-- Use UTF-8 to decode request parameters. --%>
   <ae:RequestEncoding value="UTF-8" />

   <jsp:useBean id="logBean" class="org.activebpel.rt.bpeladmin.war.web.AeDeploymentLogsBean">
   </jsp:useBean>
      
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
                     <th class="pageHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="deployment_log" /></th>
                  </tr>
                  <tr>
                    <td><textarea name="textarea" style="width:99%; height:100%" rows="25" wrap="OFF" readonly><jsp:getProperty name="logBean" property="logFile" /></textarea></td>
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

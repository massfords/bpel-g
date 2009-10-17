<%@page contentType="text/html; charset=UTF-8" import="org.activebpel.rt.util.*,org.activebpel.rt.bpel.server.engine.*,org.activebpel.rt.bpel.server.admin.*,javax.xml.namespace.QName,java.text.*,org.activebpel.rt.bpel.*"  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

<%-- Use UTF-8 to decode request parameters. --%>
<ae:RequestEncoding value="UTF-8" />

<jsp:useBean id="processBean" class="org.activebpel.rt.bpeladmin.war.web.AeDeployedProcessesBean" />


<html>

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
                  <th colspan="3" class="pageHeaders" align="left" nowrap="true"><ae:GetResource name="deployed_processes" /></th>
                  <tr height="1">
                    <td height="1" colspan="3" class="gridLines"></td>
                  </tr>
                  <tr>
                     <th class="columnHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="name" /></th>
                  </tr>

                  <ae:IndexedProperty name="processBean" id="rowDetail" property="detail" indexedClassName="org.activebpel.rt.bpel.server.admin.AeProcessDeploymentDetail" >
                     <tr>
                       <td align="left">&nbsp;<a href='deployed_process_detail.jsp?pdid=<jsp:getProperty name="processBean" property="currentIndex" />&tab=0'><jsp:getProperty name="rowDetail" property="localName" /></a></td>
                     </tr>
                     <tr height="1">
                       <td colspan="3" height="1" class="tabular"></td>
                     </tr>
                  </ae:IndexedProperty>

                  <tr height="1">
                    <td height="1" colspan="3" class="gridLines"></td>
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

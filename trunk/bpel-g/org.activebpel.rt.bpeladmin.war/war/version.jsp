<%@page contentType="text/html; charset=UTF-8" import="org.activebpel.rt.util.*,org.activebpel.rt.bpel.server.engine.*,org.activebpel.rt.bpel.server.admin.*,javax.xml.namespace.QName,java.text.*,org.activebpel.rt.bpel.*"  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>

<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

<%-- Use UTF-8 to decode request parameters. --%>
<ae:RequestEncoding value="UTF-8" />

<jsp:useBean id="configBean" class="org.activebpel.rt.bpeladmin.war.web.AeEngineConfigBean" />
<jsp:useBean id="statusBean" class="org.activebpel.rt.bpeladmin.war.web.AeEngineStatusBean" />

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
                 <td class="pageHeaders" align="left" nowrap="true" colspan="3"><ae:GetResource name="version_detail" /></td>
               </tr>
               <tr>
                  <td class="columnHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="library_name" />&nbsp;</th>
                  <td class="columnHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="version_number" />&nbsp;</th>
                  <td class="columnHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="build_date" />&nbsp;</th>
               </tr>
               
               <ae:IndexedProperty name="configBean" id="buildInfoRow" property="buildInfo" indexedClassName="org.activebpel.rt.bpel.server.admin.AeBuildInfo" >
                  <tr>
                     <td align="left">&nbsp;<jsp:getProperty name="buildInfoRow" property="projectName" /></td>
                     <td align="left">&nbsp;<jsp:getProperty name="statusBean" property="engineVersion" />.<jsp:getProperty name="buildInfoRow" property="buildNumber" /></td>
                     <td align="left">&nbsp;<jsp:getProperty name="buildInfoRow" property="buildDate" /></td>
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

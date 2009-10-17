<%@page contentType="text/html; charset=UTF-8" import="org.activebpel.rt.util.*,org.activebpel.rt.bpel.server.engine.*,org.activebpel.rt.bpel.server.admin.*,javax.xml.namespace.QName,java.text.*,org.activebpel.rt.bpel.*"  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

<%-- Use UTF-8 to decode request parameters. --%>
<ae:RequestEncoding value="UTF-8" />

<jsp:useBean id="serviceBean" class="org.activebpel.rt.bpeladmin.war.web.AeDeployedServicesBean" />

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
                  <th colspan="4" class="pageHeaders" align="left" nowrap="true"><ae:GetResource name="deployed_services" /></th>
                  <tr height="1">
                    <td height="1" colspan="4" class="gridLines"></td>
                  </tr>
                  <tr>
                     <th class="columnHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="name" /></th>
                     <th class="columnHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="process_name" /></th>
                     <th class="columnHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="binding" /></th>
                     <th class="columnHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="partner_link_header" /></th>
                  </tr>

                  <ae:IndexedProperty name="serviceBean" id="rowDetail" property="detail" indexedClassName="org.activebpel.rt.bpel.server.deploy.AeServiceDeploymentInfo" >
                     <tr>
                       <ae:IfPropertyMatches name="rowDetail" property="binding" value="EXTERNAL" classType="java.lang.String">
                          <td align="left">&nbsp;<jsp:getProperty name="rowDetail" property="serviceName" /></td>
                       </ae:IfPropertyMatches>
                       <ae:IfPropertyNotMatches name="rowDetail" property="binding" value="EXTERNAL" classType="java.lang.String">
                          <td align="left">&nbsp;<a href='/active-bpel/services/<jsp:getProperty name="rowDetail" property="serviceName" />?wsdl'><jsp:getProperty name="rowDetail" property="serviceName" /></a></td>
                       </ae:IfPropertyNotMatches>
                       <%
                         String procDetail = response.encodeURL("deployed_process_detail.jsp?planQname=" + rowDetail.getProcessQName().getNamespaceURI() + ":" + rowDetail.getProcessQName().getLocalPart() + "&tab=0");
                       %>
                       <td align="left">&nbsp;<a href="<%= procDetail %>"><jsp:getProperty name="rowDetail" property="processName" /></a></td>
                       <td align="left">&nbsp;<jsp:getProperty name="rowDetail" property="binding" /></a></td>
                       <td align="left">&nbsp;<jsp:getProperty name="rowDetail" property="partnerLinkName" /></a></td>
                     </tr>
                     <tr height="1">
                       <td colspan="4" height="1" class="tabular"></td>
                     </tr>
                  </ae:IndexedProperty>

                  <tr height="1">
                    <td height="1" colspan="4" class="gridLines"></td>
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

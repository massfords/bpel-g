<%@page contentType="text/html; charset=UTF-8" import="org.activebpel.rt.util.*,org.activebpel.rt.bpel.server.engine.*,org.activebpel.rt.bpel.server.admin.*,javax.xml.namespace.QName,java.text.*,org.activebpel.rt.bpel.*"  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>

<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

   <%-- Use UTF-8 to decode request parameters. --%>
   <ae:RequestEncoding value="UTF-8" />

   <jsp:useBean id="recsBean" class="org.activebpel.rt.bpeladmin.war.web.AeInboundReceivesBean" />

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
                  <ae:IfTrue name="recsBean" property="empty">
                     <tr>
                        <th class="pageHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="no_unmatched_inbound_queued_receives" />&nbsp;</th>
                     </tr>
                  </ae:IfTrue>
                  <ae:IfFalse name="recsBean" property="empty">
                     <tr>
                        <th colspan="4" class="titleHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="unmatched_inbound_queued_receives" />&nbsp;</th>
                     </tr>
                     <tr height="1">
                       <td height="1" colspan="4" class="gridLines"></td>
                     </tr>
                     <tr>
                       <td class="columnHeaders">Partner Link</td>
                       <td class="columnHeaders" align="center">Port Type</td>
                       <td class="columnHeaders">Operation</td>
                       <td class="columnHeaders">Queued</td>
                     </tr>
                     <tr height="1">
                       <td height="1" colspan="4" class="gridLines"></td>
                     </tr>
                     <ae:IndexedProperty name="recsBean" id="recRow" property="detail" indexedClassName="org.activebpel.rt.bpel.server.admin.AeQueuedReceiveDetail" >
                        <tr>
                           <td>&nbsp;<a href='inbound_detail.jsp?id=<jsp:getProperty name="recsBean" property="identifier" />'><jsp:getProperty name="recRow" property="partnerLinkName" /></a></td>
                           <td>&nbsp;<jsp:getProperty name="recRow" property="portTypeAsString" /></td>
                           <td>&nbsp;<jsp:getProperty name="recRow" property="operation" /></td>
                           <td>&nbsp;<jsp:getProperty name="recsBean" property="queuedReceiveCount" /></td>
                        </tr>
                        <tr height="1">
                          <td colspan="4" height="1" class="tabular"></td>
                        </tr>
                     </ae:IndexedProperty>
                     <tr height="1">
                       <td height="1" colspan="4" class="gridLines"></td>
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

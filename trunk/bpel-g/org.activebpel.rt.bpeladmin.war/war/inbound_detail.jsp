<%@page contentType="text/html; charset=UTF-8" import="org.activebpel.rt.util.*,org.activebpel.rt.bpel.server.engine.*,org.activebpel.rt.bpel.server.admin.*,javax.xml.namespace.QName,java.text.*,org.activebpel.rt.bpel.*"  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>

<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

   <%-- Use UTF-8 to decode request parameters. --%>
   <ae:RequestEncoding value="UTF-8" />

   <jsp:useBean id="recDetailBean" class="org.activebpel.rt.bpeladmin.war.web.AeInboundReceiveDetailBean" >
      <jsp:setProperty name="recDetailBean" property="key" param="id" />      
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
                  <ae:IfTrue name="recDetailBean" property="empty">
                     <tr>
                        <th class="titleHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="no_queued_details" />&nbsp;</th>
                     </tr>
                  </ae:IfTrue>
                  <ae:IfFalse name="recDetailBean" property="empty">
                     <tr>
                        <th colspan="3" class="titleHeaders" align="left" nowrap="true">
                              &nbsp;<ae:GetResource name="unmatched_inbound_queued_receives" />&nbsp;
                        </th>
                     </tr>
                     <tr height="1">
                       <td height="1" colspan="3" class="gridLines"></td>
                     </tr>
                     <tr>
                       <td class="columnHeaders"><ae:GetResource name="partner_link" /></td>
                       <td class="columnHeaders" align="center"><ae:GetResource name="port_type" /></td>
                       <td class="columnHeaders"><ae:GetResource name="operation" /></td>
                     </tr>
                     <tr height="1">
                       <td height="1" colspan="3" class="gridLines"></td>
                     </tr>
                     <tr>
                        <td>&nbsp;<jsp:getProperty name="recDetailBean" property="partnerLinkName" /></a></td>
                        <td>&nbsp;<jsp:getProperty name="recDetailBean" property="portTypeAsString" /></td>
                        <td>&nbsp;<jsp:getProperty name="recDetailBean" property="operation" /></td>
                     </tr>
                     <tr height="1">
                       <td height="1" colspan="3" class="gridLines"></td>
                     </tr>
                     <tr>
                       <td colspan="3"></td>
                     </tr>
                     <tr>
                        <th colspan="3" class="titleHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="queued_details" />&nbsp;</th>
                     </tr>
                     <tr height="1">
                       <td height="1" colspan="3" class="gridLines"></td>
                     </tr>
                     <tr>
                       <td class="columnHeaders">&nbsp;<ae:GetResource name="correlation_data" /></td>
                       <td class="columnHeaders" align="center">&nbsp;<ae:GetResource name="message_data" /></td>
                       <td class="columnHeaders"></td>
                     </tr>
                     <tr height="1">
                       <td height="1" colspan="3" class="gridLines"></td>
                     </tr>

                     <ae:IndexedProperty name="recDetailBean" id="recRow" property="detail" indexedClassName="org.activebpel.rt.bpel.server.admin.AeQueuedReceiveDetail" >
                        <tr>
                           <td align="center">
                              <ae:IfTrue name="recRow" property="correlated">
                                 &nbsp;<a href="javascript:alert('<jsp:getProperty name="recRow" property="correlatedDataAsString" />')"><ae:GetResource name="view" /></a>
                              </ae:IfTrue>
                              <ae:IfFalse name="recRow" property="correlated">
                                 &nbsp;None
                              </ae:IfFalse>
                           </td>
                           <td align="center">
                              <ae:IfTrue name="recRow" property="messageDataAvailable">
                                 &nbsp;<a href="javascript:alert('<jsp:getProperty name="recRow" property="messageDataAsString" />')"><ae:GetResource name="view" /></a>
                              </ae:IfTrue>
                              <ae:IfFalse name="recRow" property="messageDataAvailable">
                                 &nbsp;<ae:GetResource name="none" />
                              </ae:IfFalse>
                           </td>
                           <td></td>
                        </tr>
                        <tr height="1">
                          <td colspan="3" height="1" class="tabular"></td>
                        </tr>
                     </ae:IndexedProperty>
                     <tr height="1">
                       <td height="1" colspan="3" class="gridLines"></td>
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

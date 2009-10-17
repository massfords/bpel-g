<%@page contentType="text/html; charset=UTF-8" import="org.activebpel.rt.util.*,org.activebpel.rt.bpel.server.engine.*,org.activebpel.rt.bpel.server.admin.*,javax.xml.namespace.QName,java.text.*,org.activebpel.rt.bpel.*"  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>

<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

   <%-- Use UTF-8 to decode request parameters. --%>
   <ae:RequestEncoding value="UTF-8" />

   <jsp:useBean id="recDetailBean" class="org.activebpel.rt.bpeladmin.war.web.AeMessageReceiverDetailBean" >
      <jsp:setProperty name="recDetailBean" property="identifier" param="id" />      
   </jsp:useBean>

   <jsp:include page="header_head.jsp" />

   <body> 

      <table border="0" cellpadding="0" cellspacing="0" width="100%" align="center">
         <tr>
            <td valign="top" width="20%">
               <jsp:include page="header_nav.jsp" />
            </td>
      
            <!-- spacer between nav and main -->
            <td width="3%"><img src="images/clear.gif" alt="" height="1" width="1" border="0"></td>
   
            <td valign="top">
               <table border="0" cellpadding="0" cellspacing="0" width="100%" align="left">
               
                  <!-- Page header -->
                  <tr>
                     <th colspan="3" class="pageHeaders" align="left" nowrap="true"><ae:GetResource name="queued_receive_detail" /></th>
                  </tr>
                  <tr height="1">
                    <td height="1" colspan="3" class="gridLines"><img src="images/clear.gif" alt="" height="1" width="1" border="0"></td>
                  </tr>
                  
                  <!-- Record not found -->
                  <ae:IfTrue name="recDetailBean" property="empty">
                     <tr>
                        <th class="titleHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="no_queued_details" />&nbsp;</th>
                     </tr>
                  </ae:IfTrue>
                  
                  <!-- Record found -->
                  <ae:IfFalse name="recDetailBean" property="empty">
                     <tr>
                       <td class="columnHeaders">Partner Link</td>
                       <td class="columnHeaders" align="center">Port Type</td>
                       <td class="columnHeaders">Operation</td>
                     </tr>
                     <tr height="1">
                       <td height="1" colspan="3" class="gridLines"><img src="images/clear.gif" alt="" height="1" width="1" border="0"></td>
                     </tr>
                     <tr>
                        <td>&nbsp;<jsp:getProperty name="recDetailBean" property="partnerLinkTypeName" /></a></td>
                        <td>&nbsp;<jsp:getProperty name="recDetailBean" property="portType" /></td>
                        <td>&nbsp;<jsp:getProperty name="recDetailBean" property="operation" /></td>
                     </tr>
                     <tr height="1">
                       <td height="1" colspan="3" class="gridLines"><img src="images/clear.gif" alt="" height="1" width="1" border="0"></td>
                     </tr>
                     <tr>
                       <td colspan="3"><img src="images/clear.gif" alt="" height="1" width="1" border="0"></td>
                     </tr>
                     <tr>
                        <th class="titleHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="queued_details" />&nbsp;</th>
                     </tr>
                     <tr height="1">
                       <td height="1" colspan="3" class="gridLines"><img src="images/clear.gif" alt="" height="1" width="1" border="0"></td>
                     </tr>
                     <tr>
                       <td class="columnHeaders"><ae:GetResource name="pid" /></td>
                       <td class="columnHeaders" ><ae:GetResource name="location" /></td>
                       <td class="columnHeaders"><ae:GetResource name="correlation_data" /></td>
                     </tr>
                     <tr height="1">
                       <td height="1" colspan="3" class="gridLines"><img src="images/clear.gif" alt="" height="1" width="1" border="0"></td>
                     </tr>

                     <ae:IndexedProperty name="recDetailBean" id="recRow" property="receiver" indexedClassName="org.activebpel.rt.bpeladmin.war.web.AeMessageReceiverDetailWrapper" >
                        <tr>
                           <td>&nbsp;<jsp:getProperty name="recRow" property="processId" /></a></td>
                           <td>&nbsp;<jsp:getProperty name="recRow" property="locationPath" /></td>
                           <td align="center">
                              <ae:IfTrue name="recRow" property="correlated">
                                 &nbsp;<a href="javascript:alert('<jsp:getProperty name="recRow" property="correlationData" />')"><ae:GetResource name="view" /></a>
                              </ae:IfTrue>
                              <ae:IfFalse name="recRow" property="correlated">
                                 &nbsp;None
                              </ae:IfFalse>
                           </td>
                        </tr>
                        <tr height="1">
                          <td colspan="4" height="1" class="tabular"><img src="images/clear.gif" alt="" height="1" width="1" border="0"></td>
                        </tr>
                     </ae:IndexedProperty>
                     <tr height="1">
                       <td height="1" colspan="4" class="gridLines"><img src="images/clear.gif" alt="" height="1" width="1" border="0"></td>
                     </tr>
                  </ae:IfFalse>
               </table>
            </td>
      
            <!-- main and right margin       -->
            <td width="3%"><img src="images/clear.gif" alt="" height="1" width="1" border="0"></td>
         </tr>
      </table>
   <br> 
   <jsp:include page="footer.jsp" />
</body>
</html>

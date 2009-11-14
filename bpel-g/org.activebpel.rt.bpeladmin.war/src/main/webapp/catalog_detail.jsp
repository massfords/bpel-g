<%@page contentType="text/html; charset=UTF-8" import="org.activebpel.rt.util.*,org.activebpel.rt.bpel.server.engine.*,org.activebpel.rt.bpel.server.admin.*,javax.xml.namespace.QName,java.text.*,org.activebpel.rt.bpel.*"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>

   <%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae"%>
   
   <%-- Use UTF-8 to decode request parameters. --%>
   <ae:RequestEncoding value="UTF-8" />
   
   <jsp:useBean id="detailBean" class="org.activebpel.rt.bpeladmin.war.web.AeCatalogItemDetailBean">
      <jsp:setProperty name="detailBean" property="location" param="locationHint" />
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
                     <td>
                     <table border="0" cellpadding="0" cellspacing="0" width="100%" align="left">
                        <tr>
                           <th colspan="2" class="pageHeaders" align="left" nowrap="true"><ae:GetResource name="resource_detail" /></th>
                        </tr>
                        <tr height="1">
                           <td height="1" colspan="2" class="gridLines"></td>
                        </tr>
                        <tr>
                           <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="type_colon" />&nbsp;</td>
                           <td align="left">&nbsp;<jsp:getProperty name="detailBean" property="typeDisplay" /></td>
                        </tr>
                        <tr height="1">
                           <td colspan="2" height="1" class="tabular"></td>
                        </tr>
                        <tr>
                           <td class="labelHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="location_colon" />&nbsp;</td>
                           <td align="left">&nbsp;<jsp:getProperty name="detailBean" property="location" /></td>
                        </tr>
                        <tr height="1">
                           <td colspan="2" height="1" class="tabular"></td>
                        </tr>
                        <tr>
                           <td class="labelHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="namespace_colon" />&nbsp;</td>
                           <td align="left">&nbsp;<jsp:getProperty name="detailBean" property="namespace" /></td>
                        </tr>
                        <tr height="1">
                           <td colspan="2" height="1" class="tabular"></td>
                        </tr>
                        <tr height="1">
                           <td height="1" colspan="2" class="gridLines"></td>
                        </tr>
         
                        <tr>
                           <td colspan="2" height="10"></td>
                        </tr>
                        <tr>
                           <td colspan="2" align="left" nowrap="true">&nbsp;<b><ae:GetResource name="resource_definition" /></b>&nbsp;</td>
                        </tr>
                        <tr>
                           <td colspan="2"><textarea style="width:99%; height=100%" rows="20" wrap="off" readonly="true" cols=""><ae:XMLStringFormatter name="detailBean" property="text" /></textarea></td>
                        </tr>
                        <tr>
                           <td colspan="2" height="10"></td>
                        </tr>
                     </table>
                     </td>
                  </tr>
                  <tr>
                     <td>
                     <table border="0" cellpadding="0" cellspacing="0" width="100%" align="left">
         
                        <%-- ------------------------------------------------------- --%>
                        <%-- only display the plan info if there are details to show --%>
                        <%-- ------------------------------------------------------- --%>
                        <ae:IfFalse name="detailBean" property="detailArrayEmpty">
                           <tr>
                              <td colspan="2" align="left" nowrap="true">&nbsp;<b><ae:GetResource name="referenced_by_header" /></b>&nbsp;</td>
                           </tr>
                           <tr height="1">
                              <td height="1" colspan="2" class="gridLines"></td>
                           </tr>
         
                           <tr height="1">
                              <td height="1" colspan="2" class="gridLines"></td>
                           </tr>
                           <tr>
                              <td class="labelHeaders" align="left" nowrap width="30%">&nbsp;<ae:GetResource name="process" />&nbsp;</td>
                              <td class="labelHeaders" align="left" width="70%">&nbsp;<ae:GetResource name="namespace" />&nbsp;</td>
                           </tr>
                           <tr height="1">
                              <td height="1" colspan="2" class="gridLines"></td>
                           </tr>
         
                           <ae:IndexedProperty name="detailBean" id="detailRow" property="planReferenceDetail" indexedClassName="org.activebpel.rt.bpel.impl.list.AeCatalogItemPlanReference">
                              <tr>
                                 <td align="left" nowrap>&nbsp;<a href="deployed_process_detail.jsp?planQname=<ae:QName name='detailRow' property='planQName' part='uri' />:<ae:QName name='detailRow' property='planQName' part='local' />"><ae:QName name="detailRow"
                                    property="planQName" part="local" /></a>&nbsp;</td>
                                 <td align="left">&nbsp;<a href="deployed_process_detail.jsp?planQname=<ae:QName name='detailRow' property='planQName' part='uri' />:<ae:QName name='detailRow' property='planQName' part='local' />"><ae:QName name="detailRow"
                                    property="planQName" part="uri" /></a>&nbsp;</td>
                              </tr>
                              <tr height="1">
                                 <td colspan="2" height="1" class="tabular"></td>
                              </tr>
                           </ae:IndexedProperty>
                           <tr height="1">
                              <td height="1" colspan="2" class="gridLines"></td>
                           </tr>
                        </ae:IfFalse>
                     </table>
                     </td>
                  </tr>
               </table>
            </td>
      
            <!-- main and right margin       -->
            <td width="3%"></td>
         </tr>
      </table>
      
      <br>
      <jsp:include page="footer.jsp"/>
   </body>
</html>

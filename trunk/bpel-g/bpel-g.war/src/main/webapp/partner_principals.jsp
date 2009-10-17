<%@page contentType="text/html; charset=UTF-8" import="org.activebpel.rt.util.*,org.activebpel.rt.bpel.server.engine.*,org.activebpel.rt.bpel.server.admin.*,javax.xml.namespace.QName,java.text.*,org.activebpel.rt.bpel.*"  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>

<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

   <%-- Use UTF-8 to decode request parameters. --%>
   <ae:RequestEncoding value="UTF-8" />

   <jsp:useBean id="pdefsBean" class="org.activebpel.rt.bpeladmin.war.web.AePartnerDefsBean" />

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
                     <th class="pageHeaders" align="left" nowrap="true"><ae:GetResource name="partner_definitions" /></th>
                  </tr>
                  <tr height="1">
                    <td height="1" class="gridLines"></td>
                  </tr>
                  <!-- No record found -->
                  <ae:IfTrue name="pdefsBean" property="empty">
                    <tr>
                       <th class="titleHeaders" colspan="4" align="left" nowrap="true">&nbsp;<ae:GetResource name="no_matching_partner_definitions" />&nbsp;</th>
                    </tr>
                  </ae:IfTrue>
                  <!-- Records found -->
                  <ae:IfFalse name="pdefsBean" property="empty">
	                  <tr>
	                    <td class="labelHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="principal" />&nbsp;</td>
	                  </tr>
	                  <tr height="1">
	                    <td height="1" class="tabular"></td>
	                  </tr>
	                  <ae:IndexedProperty name="pdefsBean" id="principalRow" property="principal" indexedClassName="org.activebpel.rt.bpeladmin.war.web.AeJavaTypesWrapper" >
	                     <tr>
	                        <td>&nbsp;<a href='partner_details.jsp?principal=<jsp:getProperty name="principalRow" property="string" />'><jsp:getProperty name="principalRow" property="string" /></a></td>
	                     </tr>
	                     <tr height="1">
	                       <td height="1" class="tabular"></td>
	                     </tr>
	                  </ae:IndexedProperty>                  
                  </ae:IfFalse>                                    
                  <tr height="1">
                    <td height="1" class="gridLines"></td>
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

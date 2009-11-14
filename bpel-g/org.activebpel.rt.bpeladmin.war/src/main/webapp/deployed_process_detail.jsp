<%@page contentType="text/html; charset=UTF-8" import="org.activebpel.rt.bpel.impl.*,org.activebpel.rt.bpel.server.engine.*,org.activebpel.rt.bpel.server.admin.*,javax.xml.namespace.QName"  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

<%-- Use UTF-8 to decode request parameters. --%>
<ae:RequestEncoding value="UTF-8" />
<jsp:useBean id="tabBean" scope="request" class="org.activebpel.rt.war.web.tabs.AeTabBean"/>
<ae:TabTag name="deployed_process_detail" beanName="tabBean" />
<jsp:setProperty name="tabBean" property="selectedTabString" param="tab" />

<jsp:useBean id="detailBean" scope="request" class="org.activebpel.rt.bpeladmin.war.web.AeProcessDeploymentSelectorBean" >
   <jsp:setProperty name="detailBean" property="selection" param="pdid" />
   <jsp:setProperty name="detailBean" property="planQName" param="planQname" />
</jsp:useBean>

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
               <tr>
                  <th colspan="2" class="pageHeaders" align="left" nowrap="true"><ae:GetResource name="deployed_process_detail" /></th>
               </tr>
               <tr height="1">
                 <td height="1" colspan="2" class="gridLines"></td>
               </tr>
               <tr>
                 <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="name_colon" />&nbsp;</td>
                 <td align="left" colspan="2">&nbsp;<jsp:getProperty name="detailBean" property="localName" /></td>
               </tr>
               <tr height="1">
                 <td height="1" colspan="2" class="tabular"></td>
               </tr>
               <tr>
                 <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="namespace_colon" />&nbsp;</td>
                 <td align="left" colspan="2">&nbsp;<jsp:getProperty name="detailBean" property="namespaceURI" /></td>
               </tr>
               <tr height="1">
                 <td height="1" colspan="2" class="tabular"></td>
               </tr>
               <tr height="1">
                 <td height="1" colspan="2" class="gridLines"></td>
               </tr>
               <tr>
                 <td align="left" nowrap="true" width="20%" colspan="2"><img src="images/clear.gif" alt="" width="1" border="0"></td>
               </tr>
               <tr>
                  <td colspan="2">
							<a target="ae_dpid" href="deployedprocess/processview_detail.jsp?pdid=<jsp:getProperty name="detailBean" property="processDeploymentId" />"><ae:GetResource name="view_process_graph" /></a>
							<br/>
							<br/> 
                     <!-- begin tab ui -->
                     <div style="margin:5px">
                        <ul class="tabnav">
			                  <ae:IndexedProperty name="tabBean" id="tab" property="tab" indexedClassName="org.activebpel.rt.war.web.tabs.AeTab" >
			                     <ae:IfTrue name="tab" property="selected" >
			                        <li class="seltab"><span> &nbsp;<jsp:getProperty name="tab" property="name" />&nbsp;</span></li>
			                     </ae:IfTrue>
			                     <ae:IfFalse name="tab" property="selected" >
			                        <li><a href="deployed_process_detail.jsp?pdid=<jsp:getProperty name="detailBean" property="processDeploymentId" />&tab=<jsp:getProperty name="tab" property="offset" />"> &nbsp; <jsp:getProperty name="tab" property="name" /> &nbsp;</a></li>
			                     </ae:IfFalse>
			                  </ae:IndexedProperty>
                        </ul>
                        <div class="tabcontainer">
                           <div style="padding:5px;margin:5px 0 0 0;">
                              <br/>
                              <jsp:include page="<%=tabBean.getSelectedPage()%>"/>
                           </div>
                        </div><!-- end tab container -->
                     </div><!-- end tab ui -->
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

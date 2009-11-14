<%@page contentType="text/html; charset=UTF-8" import="org.activebpel.rt.util.*,org.activebpel.rt.bpel.server.engine.*,org.activebpel.rt.bpel.server.admin.*,javax.xml.namespace.QName,java.text.*,org.activebpel.rt.bpel.*"  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

   <%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

   <%-- Use UTF-8 to decode request parameters. --%>
   <ae:RequestEncoding value="UTF-8" />

   <jsp:useBean id="pdefBean" class="org.activebpel.rt.bpeladmin.war.web.AePartnerDefLinkTypeBean" >
      <jsp:setProperty name="pdefBean" property="principal" param="principal" />
      <jsp:setProperty name="pdefBean" property="partnerLinkLP" param="lp" />
      <jsp:setProperty name="pdefBean" property="partnerLinkNS" param="ns" />
      <jsp:setProperty name="pdefBean" property="finished" value="true" />
   </jsp:useBean>
   
   <head>
     <title><ae:GetResource name="partner_def_details" /></title>
     <link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
     <STYLE TYPE="text/css">@import url(css/ae.css);</STYLE>
   </head>

   <body>

      <table border="0" cellpadding="0" cellspacing="0" width="100%" align="center">
         <tr>
           <td valign="top">
            <table border="0" cellpadding="0" cellspacing="0" width="100%" align="left">
					<tr>
						<td colspan="2" class="titleHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="partner_details" />&nbsp;</td>
						<td align="right"><span class="iconText"><a href="javascript:window.close()" title="<ae:GetResource name="close_window" />"><span style="font-weight:bold"><ae:GetResource name="close" /></span></a></span>&nbsp;</td>
					</tr>
               <tr height="1">
                 <td height="1" colspan="3" class="gridLines"></td>
               </tr>
               <tr>
                 <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="partner_link_type_colon" />&nbsp;</td>
                 <td align="left" colspan="2"><ae:QName name="pdefBean" property="partnerLinkType" part="local"/></td>
               </tr>
               <tr height="1">
                 <td colspan="3" height="1" class="tabular"></td>
               </tr>               
               <tr>
                 <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="namespace_colon" />&nbsp;</td>
                 <td align="left" colspan="2"><ae:QName name="pdefBean" property="partnerLinkType" part="uri"/></td>
               </tr>
               <tr height="1">
                 <td colspan="3" height="1" class="tabular"></td>
               </tr>                                            
               <tr>
                 <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="role_colon" />&nbsp;</td>
                 <td align="left" colspan="2"><jsp:getProperty name="pdefBean" property="role" /></td>
               </tr>
               <tr height="1">
                 <td height="1" colspan="3" class="gridLines"></td>
               </tr>
					<tr height="40">
						<td height="40" colspan="2" class="titleHeaders" align="left" valign="bottom" nowrap="true">&nbsp;<ae:GetResource name="endpoint_reference" />&nbsp;</td>
					</tr>
               <tr height="1">
                 <td height="1" colspan="3" class="gridLines"></td>
               </tr>
            </table>
           </td>
         </tr>
      </table>
      <pre>
<ae:XMLStringFormatter name="pdefBean" property="endpointReference" />
      </pre>
   </body>
</html>

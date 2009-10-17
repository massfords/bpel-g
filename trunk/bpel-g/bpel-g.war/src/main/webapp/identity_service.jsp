<%@page contentType="text/html; charset=UTF-8" import="org.activebpel.rt.util.*,org.activebpel.rt.bpel.server.engine.*,org.activebpel.rt.bpel.server.admin.*,javax.xml.namespace.QName,java.text.*,org.activebpel.rt.bpel.*"  %>
<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>
<ae:RequestEncoding value="UTF-8" />   
<jsp:useBean id="configBean" class="org.activebpel.rt.bpeladmin.war.web.AeIdentityServiceBean" />

<ae:IfParamMatches property="issubmit" value="true">
   <ae:SetCheckboxProperty name="configBean" property="enabled" param="enabled" />
   <ae:SetStringProperty name="configBean" property="fileName" param="fileName" />
   <ae:SetStringProperty name="configBean" property="providerType" param="providerType" />
   <ae:SetStringProperty name="configBean" property="testPrincipalName" param="testconfig.principal" />
   <jsp:setProperty name="configBean" property="update" param="update" />
   <jsp:setProperty name="configBean" property="testConfiguration" param="testConfiguration" />
</ae:IfParamMatches>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
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
            
            <!--  === Begin Identity Service Header Section === -->
            <table border="0" cellpadding="0" cellspacing="0" width="100%">
               <tr>
                  <th colspan="3" class="pageHeaders" align="left" nowrap="true"><ae:GetResource name="identity_service" /></th>
               </tr>
               <tr height="1">
                 <td height="1" colspan="3" class="gridLines"></td>
               </tr>          
            </table>
            <!--  === End Identity Service Header Section === -->
            
            <!--  === Begin Config Form Section === -->
            <form name="aeconfig" method="post" onsubmit="return true" action="identity_service.jsp" >
               <input type="hidden" name="issubmit" value="true"/>
               <table border="0" cellpadding="0" cellspacing="0" width="100%">               
                  <tr>
                    <td colspan="2" class="columnHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="identity_provider_config" />&nbsp;</td>
                  </tr>   

                  <!--  enabled  -->
                  <tr>
                     <td class="labelHeaders" align="left" nowrap="true" width="40%">&nbsp;<ae:GetResource name="enable" />&nbsp;</td>
                     <td align="left">
                         <input type="checkbox" name="enabled" value="true" <ae:IfTrue name="configBean" property="enabled" >checked</ae:IfTrue> /> 
                     </td>
                  </tr>
                  <tr height="1">
                    <td colspan="2" height="1" class="tabular"></td>
                  </tr>      
                                 
                   <!--  Select the config type  -->
                  <tr>
                     <td class="labelHeaders" align="left" nowrap="true" width="40%">&nbsp;<ae:GetResource name="provider_type_colon" />&nbsp;</td>
                     <td align="left">
                      <select name="providerType">
                         <option value="ldif" <ae:IfTrue name="configBean" property="ldif" >selected="true"</ae:IfTrue>><ae:GetResource name="ldif" /></option>
                         <option value="tomcat" <ae:IfTrue name="configBean" property="tomcat" >selected="true"</ae:IfTrue>><ae:GetResource name="tomcat" /></option>
                      </select>                        
                     </td>
                  </tr>
                  <tr height="1">
                    <td colspan="2" height="1" class="tabular"></td>
                  </tr>      
                                 
                  <!--  === Begin file provider options Section === -->
                  <tr><td colspan="2"><div id="file_params" style="width:100%"><table border="0" cellpadding="0" cellspacing="0" width="100%">               
                     <!--  filename  -->
                     <tr>
                        <td class="labelHeaders" align="left" nowrap="true" width="40%">&nbsp;<ae:GetResource name="file_colon" />&nbsp;</td>
                        <td align="left">
                           <input type="text" name="fileName" value='<jsp:getProperty name="configBean" property="fileName" />' size="40"/>&nbsp;                          
                        </td>
                     </tr>
                  </table></div></td></tr>
                  <!--  === End file provider options Section === -->
                  <!-- Test Config Settings -->
                  <tr><td colspan="2">&nbsp;</td></tr>
                  <tr>
                    <td colspan="2" class="columnHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="test_identity_service_config" />&nbsp;</td>
                  </tr>
                  <!--  user dn  -->
                  <tr>
                     <td class="labelHeaders" align="left" nowrap="true" width="40%">&nbsp;<ae:GetResource name="test_identity_service_principal" />&nbsp;</td>
                     <td align="left">
                       <input type="text" name="testconfig.principal" value='<jsp:getProperty name="configBean" property="testPrincipalName" />' size="20"/>&nbsp;
                       <input type="submit" name="testConfiguration" value="<ae:GetResource name="test_settings" />" />
                     </td>
                  </tr>
                  <tr height="1">
                     <td colspan="2" height="1" class="tabular"></td>
                  </tr>                   
                  <tr>
                     <td colspan="2">&nbsp;</td>
                  </tr>     
                  <!--  submit button -->
                  <tr>
                     <td colspan="2" align="left">                        
                        <input type="submit" name="update" value="<ae:GetResource name="update" />" />
                     </td>
                  </tr>                 
                   
                  <ae:IfTrue name="configBean" property="errorDetail">
                     <!-- EXPIRE ERROR FEEDBACK -->
                     <tr>
                        <td align="left" colspan="2">
                            <ul>                        
                                <ae:CollectionIterator name="configBean" property="messageList" id="msg" >
                                   <li><%= pageContext.getAttribute("msg") %></li>
                                </ae:CollectionIterator>                    
                             </ul>                              
                        </td>
                     </tr>
                  </ae:IfTrue>                           
                  <ae:IfFalse name="configBean" property="errorDetail">
                     <ae:IfTrue name="configBean" property="statusDetailAvailable">
                     <!-- EXPIRE NORMAL FEEDBACK -->
                        <tr>
                           <td align="left" colspan="2">
                               <ul>                        
                                   <ae:CollectionIterator name="configBean" property="messageList" id="msg" >
                                      <li><%= pageContext.getAttribute("msg") %></li>
                                   </ae:CollectionIterator>                    
                                </ul>                              
                           </td>                        
                        </tr>
                     </ae:IfTrue>
                  </ae:IfFalse>                                                                                                
                       
               </table>
            </form>
            <!--  === End Config Form Section === -->
                        
         </td>   
         <!-- main and right margin       -->
         <td width="3%"></td>
      </tr>
   </table>
   <br> 
   <jsp:include page="footer.jsp" />
</body>
</html>

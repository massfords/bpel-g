<%@page contentType="text/html; charset=UTF-8" import="org.activebpel.rt.util.*,org.activebpel.rt.bpel.server.engine.*,org.activebpel.rt.bpel.server.admin.*,javax.xml.namespace.QName,java.text.*,org.activebpel.rt.bpel.*"  %>
<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>
<ae:RequestEncoding value="UTF-8" />   
<jsp:useBean id="configBean" class="org.activebpel.rt.bpeladmin.war.web.AeTaskManagerBean" />
<ae:IfParamMatches property="issubmit" value="true">
	<ae:SetIntProperty name="configBean" property="cacheSize" param="cacheSize" default="-1"/>
	<ae:SetIntProperty name="configBean" property="cacheDuration" param="cacheDuration" default="-1"/>
	<ae:SetIntProperty name="configBean" property="finalizationDuration" param="finalizationDuration" default="-1"/>
	<jsp:setProperty name="configBean" property="update" param="issubmit" />	
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
         	<!--  === Begin Task Manager Header Section === -->
            <table border="0" cellpadding="0" cellspacing="0" width="100%">
               <tr>
                  <th colspan="3" class="pageHeaders" align="left" nowrap="true"><ae:GetResource name="task_manager" /></th>
               </tr>
               <tr height="1">
                 <td height="1" colspan="3" class="gridLines"></td>
               </tr>                                                                       
            </table>
				<!--  === End Task Manager Header Section === -->
				
				<!--  === Begin Task Manager Form Section === -->
				<form id="taskmanager" name="taskmanager" method="post" action="task_manager.jsp" >
					<input type="hidden" name="issubmit" value="true"/>					
					<table border="0" cellpadding="0" cellspacing="0" width="100%">
																	
						<!--  finalization duration -->
 
						<tr>
							<td class="labelHeaders" align="left" nowrap="true" width="40%">&nbsp;<ae:GetResource name="finalizationDuration" />&nbsp;</td>
				         <td align="left">
								<input type="text" name="finalizationDuration" value='<jsp:getProperty name="configBean" property="finalizationDuration" />' size="10"/>&nbsp;  								
				         </td>
						</tr>
						<tr height="1">
						  <td colspan="2" height="1" class="tabular"></td>
						</tr>		

						<!--  cache size-->
						<tr>
							<td class="labelHeaders" align="left" nowrap="true" width="40%">&nbsp;<ae:GetResource name="inbox_cache_size" />&nbsp;
							</td>
				         <td align="left">
								<input type="text" name="cacheSize" value='<jsp:getProperty name="configBean" property="cacheSize" />' size="10"/>&nbsp;  								
								&nbsp;<ae:GetResource name="inbox_cache_size_hint" />
				         </td>
						</tr>
						<tr height="1">
						  <td colspan="2" height="1" class="tabular"></td>
						</tr>		
												
						<!--  cache duration-->
						<tr>
							<td class="labelHeaders" align="left" nowrap="true" width="40%">&nbsp;<ae:GetResource name="inbox_cache_duration" />&nbsp;</td>
				         <td align="left">
								<input type="text" name="cacheDuration" value='<jsp:getProperty name="configBean" property="cacheDuration" />' size="10"/>&nbsp;  	
								&nbsp;<ae:GetResource name="inbox_cache_duration_hint" />															
				         </td>
						</tr>
						<tr height="1">
						  <td colspan="2" height="1" class="tabular"></td>
						</tr>		
												
						<!--  submit and test config button -->
						<tr>
							<td>
								<input type="submit" name="update" value="<ae:GetResource name="update" />" />
							</td>
						</tr>
						<tr>
						  <td colspan="2">&nbsp;</td>
						</tr>	
					  <!-- Spacer between list and form -->
                 <tr height="1">
                   <td height="1" colspan="2" class="gridLines"></td>
                 </tr>
				      <ae:IfTrue name="configBean" property="errorDetail">
					      <!-- ERROR FEEDBACK -->
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
							<!-- NORMAL FEEDBACK -->
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
				<!--  === End Task Manager Form Section === -->
            
         </td>   
         <!-- main and right margin       -->
         <td width="3%"></td>
      </tr>
   </table>
   <br> 
   <jsp:include page="footer.jsp" />
</body>
</html>

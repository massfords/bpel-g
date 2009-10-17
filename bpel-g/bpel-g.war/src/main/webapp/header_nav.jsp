<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

<%-- Use UTF-8 to decode request parameters. --%>
<ae:RequestEncoding value="UTF-8" />

<!--------------->
<!-- nav table -->
<!--------------->
<table border="0" cellpadding="0" cellspacing="0" width="100%" align="left">

   <tr><td><img src="images/logo.gif"></td></tr>

   <tr height="10"><td></td></tr>

   <tr height="1">
     <td height="1" colspan="2" class="gridLines"><img src="images/clear.gif" alt="" height="1" width="1" border="0"></td>
   </tr>
           <tr>
             <td class="navItem" align="left" nowrap="true" width="20%">&nbsp;<a href="home.jsp"><ae:GetResource name="home" /></a>&nbsp;</td>
           </tr>

	<!-- ============================= -->
	<!-- Engine Config Section     -->
	<!-- ============================= -->
   <tr height="10">
     <td class="navItem" align="left" height="10" nowrap="true" width="20%"><img src="images/clear.gif" alt="" height="10" width="1" border="0"></td>
   </tr>
   <tr>
     <td class="navHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="engine" /></td>
   </tr>
   <tr>
     <td class="navItem" align="left" nowrap="true" width="20%">&nbsp;<a href="config.jsp"><ae:GetResource name="configuration" /></a>&nbsp;</td>
   </tr>
   <tr>
     <td class="navItem" align="left" nowrap="true" width="20%">&nbsp;<a href="storage.jsp"><ae:GetResource name="storage" /></a>&nbsp;</td>
   </tr>
   <tr>
     <td class="navItem" align="left" nowrap="true" width="20%">&nbsp;<a href="version.jsp"><ae:GetResource name="version_detail" /></a>&nbsp;</td>
   </tr>
   
	<!-- ============================= -->
	<!-- Extended Services Section     -->
	<!-- ============================= -->
	<tr height="10">
	  <td class="navItem" align="left" height="10" nowrap="true" width="20%"><img src="images/clear.gif" alt="" height="5" width="1" border="0"></td>
	</tr>		
	<tr>
	  <td class="navHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="extended_services" /></td>
	</tr>
	<tr>
	  <td class="navItem" align="left" nowrap="true" width="20%">&nbsp;<a href="identity_service.jsp"><ae:GetResource name="identity_service" /></a>&nbsp;</td>
	</tr>

	<!-- ============================= -->
	<!-- Deployment Status Section     -->
	<!-- ============================= -->
   <tr height="10">
     <td class="navItem" align="left" height="10" nowrap="true" width="20%"><img src="images/clear.gif" alt="" height="10" width="1" border="0"></td>
   </tr>
   <tr>
     <td class="navHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="deployment_status" /></td>
   </tr>
   <tr>
     <td class="navItem" align="left" nowrap="true" width="20%">&nbsp;<a href="deployment_log_detail.jsp"><ae:GetResource name="deployment_log" />&nbsp;</a></td>
   </tr>
   <tr>
     <td class="navItem" align="left" nowrap="true" width="20%">&nbsp;<a href="deployed_processes.jsp"><ae:GetResource name="deployed_processes" />&nbsp;</a></td>
   </tr>
   <tr>
     <td class="navItem" align="left" nowrap="true" width="20%">&nbsp;<a href="deployed_services.jsp"><ae:GetResource name="deployed_services" />&nbsp;</a></td>
   </tr>
   <tr>
     <td class="navItem" align="left" nowrap="true" width="20%">&nbsp;<a href="partner_principals.jsp"><ae:GetResource name="partner_definitions" /></a>&nbsp;</td>
   </tr>
   <tr>
     <td class="navItem" align="left" nowrap="true" width="20%">&nbsp;<a href="catalog_listing.jsp"><ae:GetResource name="resource_catalog" /></a>&nbsp;</td>
   </tr>

	<!-- ============================= -->
	<!-- Process Status Section        -->
	<!-- ============================= -->
   <tr height="10">
     <td class="navItem" align="left" height="10" nowrap="true" width="20%"><img src="images/clear.gif" alt="" height="10" width="1" border="0"></td>
   </tr>
   <tr>
     <td class="navHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="process_status" /></td>
   </tr>
   <tr>
     <td class="navItem" align="left" nowrap="true" width="20%">&nbsp;<a href="active_processes.jsp"><ae:GetResource name="active_processes" /></a></td>
   </tr>
   <tr>
     <td class="navItem" align="left" nowrap="true" width="20%">&nbsp;<a href="alarm_queue.jsp"><ae:GetResource name="alarm_queue" /></a></td>
   </tr>
   <tr>
     <td class="navItem" align="left" nowrap="true" width="20%">&nbsp;<a href="message_rec_queue.jsp"><ae:GetResource name="receive_queue" /></a></td>
   </tr>

	<!-- ============================= -->
	<!-- Process ID Form Section            -->
	<!-- ============================= -->
   <tr height="10">
     <td class="navItem" align="left" height="10" nowrap="true" width="20%"><img src="images/clear.gif" alt="" height="10" width="1" border="0"></td>
   </tr>
   <tr>
     <td class="navHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="process_id" /></td>
   </tr>
   <form name="process_status_form" onsubmit="return openProcessView()">
      <tr>
         <td class="navItem" align="left" nowrap="true" width="20%">
            &nbsp;<input type="text" cols="5" name="pid"/>&nbsp;<input class="smallButton" type="submit" name="<ae:GetResource name="go" />" value="<ae:GetResource name="go" />" />
         </td>
      </tr>
   </form>

   <tr height="10">
     <td class="navItem" align="left" height="10" nowrap="true" width="20%"><img src="images/clear.gif" alt="" height="10" width="1" border="0"></td>
   </tr>
   <tr height="1">
     <td height="1" colspan="2" class="gridLines"><img src="images/clear.gif" alt="" height="1" width="1" border="0"></td>
   </tr>
   <tr>
     <td class="navItem" align="left" nowrap="true" width="20%">&nbsp;<a href="../BpelAdminHelp/" target="aeAdminHelp"><ae:GetResource name="help" /></a></td>
   </tr>
   <tr height="1">
     <td height="1" colspan="2" class="gridLines"><img src="images/clear.gif" alt="" height="1" width="1" border="0"></td>
   </tr>
</table>

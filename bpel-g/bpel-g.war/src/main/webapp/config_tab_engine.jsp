<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

<jsp:useBean id="configBean" class="org.activebpel.rt.bpeladmin.war.web.AeEngineConfigBean" />
<jsp:useBean id="tabBean" scope="request" class="org.activebpel.rt.war.web.tabs.AeTabBean" />

<jsp:setProperty name="tabBean" property="selectedTabString" param="tab" /> 

   <ae:IfParamMatches property="isSubmit" value="true">
      <jsp:setProperty name="configBean" property="allowCreateXPath" param="ec_allow_create_xpath" />
      <jsp:setProperty name="configBean" property="allowEmptyQuery" param="ec_allow_empty_query" />
      <jsp:setProperty name="configBean" property="validateServiceMessages" param="ec_validate_service_messages" />
      <jsp:setProperty name="configBean" property="loggingFilter" param="ec_logging" />
      <jsp:setProperty name="configBean" property="unmatchedCorrelatedReceiveTimeout" param="ec_unmatch_timeout" />
      <jsp:setProperty name="configBean" property="webServiceInvokeTimeout" param="ec_web_service_invoke_timeout" />
      <jsp:setProperty name="configBean" property="webServiceReceiveTimeout" param="ec_web_service_receive_timeout" />
      <jsp:setProperty name="configBean" property="threadPoolMin" param="ec_thread_min" />
      <jsp:setProperty name="configBean" property="threadPoolMax" param="ec_thread_max" />
      <jsp:setProperty name="configBean" property="resourceCacheMax" param="ec_resource_cache_max" />
      <jsp:setProperty name="configBean" property="resourceReplaceEnabled" param="ec_resource_replace" />
      <jsp:setProperty name="configBean" property="alarmMaxWorkCount" param="ec_alarm_max_work_count" />
      <jsp:setProperty name="configBean" property="processWorkCount" param="ec_process_work_count" />
      <ae:SetIntProperty name="configBean" property="taskFinalizationDuration" param="ec_task_finalization_duration" min="1" />
      <jsp:setProperty name="configBean" property="finished" value="true" />
   </ae:IfParamMatches>

   <!-- engine info table -->
   <table border="0" cellpadding="0" cellspacing="0" width="100%">
   <form name="ec_form" method="post" action="config.jsp">
      <input type="hidden" name="tab" value="<jsp:getProperty name="tabBean" property="selectedOffset"/>"/>
      <tr>
        <th class="columnHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="property" />&nbsp;</th>
        <th class="columnHeaders" align="left" nowrap="true" colspan="2">&nbsp;<ae:GetResource name="value" />&nbsp;</th>
      </tr>
      <tr>
        <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="create_path" />&nbsp;</td>
        <td align="left" colspan="2"><input type="checkbox" tabIndex="1" name="ec_allow_create_xpath" value="true" <ae:IfTrue name="configBean" property="allowCreateXPath" >checked</ae:IfTrue> /></td>
      </tr>
      <tr height="1">
        <td colspan="3" height="1" class="tabular"></td>
      </tr>               
      <tr>
        <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="disable_selection_fault" />&nbsp;</td>
        <td align="left" colspan="2"><input type="checkbox" tabIndex="2" name="ec_allow_empty_query" value="true" <ae:IfTrue name="configBean" property="allowEmptyQuery" >checked</ae:IfTrue> /></td>
      </tr> 
      <tr height="1">
        <td colspan="3" height="1" class="tabular"></td>
      </tr>                                            
      <tr>
        <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="logging_filter" />&nbsp;</td>
       <td align="left" colspan="2">
       <select name="ec_logging" tabIndex="3">
          <option value="urn:ae:full" <ae:IfPropertyMatches name="configBean" property="loggingFilter" value="urn:ae:full" classType="java.lang.String">selected="true"</ae:IfPropertyMatches>><ae:GetResource name="logging.filter.full" /></option>
          <option value="urn:ae:execution" <ae:IfPropertyMatches name="configBean" property="loggingFilter" value="urn:ae:execution" classType="java.lang.String">selected="true"</ae:IfPropertyMatches>><ae:GetResource name="logging.filter.execution" /></option>
          <option value="urn:ae:none" <ae:IfPropertyMatches name="configBean" property="loggingFilter" value="urn:ae:none" classType="java.lang.String">selected="true"</ae:IfPropertyMatches>><ae:GetResource name="logging.filter.none" /></option>
       </select>
       </td>
      </tr>
      <tr height="1">
        <td colspan="3" height="1" class="tabular"></td>
      </tr>
      <tr>
        <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="replace_resources" />&nbsp;</td>
        <td align="left" colspan="2"><input type="checkbox" tabIndex="5" name="ec_resource_replace" value="true" <ae:IfTrue name="configBean" property="resourceReplaceEnabled" >checked</ae:IfTrue> /></td>
      </tr>
      <tr height="1">
      <td colspan="3" height="1" class="tabular"></td>
     </tr>                                            
      <tr>
        <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="validate_against_schema" />&nbsp;</td>
        <td align="left" colspan="2"><input type="checkbox" tabIndex="6" name="ec_validate_service_messages" value="true" <ae:IfTrue name="configBean" property="validateServiceMessages" >checked</ae:IfTrue> /></td>
      </tr>
      <tr height="1">
          <td colspan="3" height="1" class="tabular"></td>
      </tr>
      <tr>
         <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="resource_cache_max" />&nbsp;</td>
         <td align="left" colspan="2"><input type="text" cols="5" tabIndex="11" name="ec_resource_cache_max" value='<jsp:getProperty name="configBean" property="resourceCacheMax" />'/></td>
       </tr>
      <tr height="1">
        <td colspan="3" height="1" class="tabular"></td>
      </tr>
      <tr>
        <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="unmatched_correlated_receive_timeout" />&nbsp;</td>
        <td align="left" colspan="2"><input type="text" cols="5" tabIndex="14" name="ec_unmatch_timeout" value='<jsp:getProperty name="configBean" property="unmatchedCorrelatedReceiveTimeout" />'/></td>
      </tr>
      <tr height="1">
         <td colspan="3" height="1" class="tabular"></td>
      </tr>
      <tr>
         <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="web_service_invoke_timeout" />&nbsp;</td>
         <td align="left" colspan="2"><input type="text" cols="5" tabIndex="16" name="ec_web_service_invoke_timeout" value='<jsp:getProperty name="configBean" property="webServiceInvokeTimeout" />'/></td>
      </tr>
      <tr height="1">
        <td colspan="3" height="1" class="tabular"></td>
      </tr>
      <tr>
         <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="web_service_receive_timeout" />&nbsp;</td>
         <td align="left" colspan="2"><input type="text" cols="5" tabIndex="16" name="ec_web_service_receive_timeout" value='<jsp:getProperty name="configBean" property="webServiceReceiveTimeout" />'/></td>
      </tr>
      <tr height="1">
        <td colspan="3" height="1" class="tabular"></td>
      </tr>
      
      <ae:IfTrue name="configBean" property="internalWorkManager">
         <tr>
           <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="thread_pool_min" />&nbsp;</td>
           <td align="left" colspan="2"><input type="text" cols="5" tabIndex="18" name="ec_thread_min" value='<jsp:getProperty name="configBean" property="threadPoolMin" />'/></td>
         </tr>
         <tr height="1">
           <td colspan="3" height="1" class="tabular"></td>
         </tr>
         <tr>
           <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="thread_pool_max" />&nbsp;</td>
           <td align="left" colspan="2"><input type="text" cols="5" tabIndex="20" name="ec_thread_max" value='<jsp:getProperty name="configBean" property="threadPoolMax" />'/></td>
         </tr>
         <tr height="1">
            <td colspan="3" height="1" class="tabular"></td>
         </tr>
      </ae:IfTrue>
      
      <tr>
         <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="alarm_max_work_count" />&nbsp;</td>
         <td align="left" colspan="2"><input type="text" cols="5" tabIndex="21" name="ec_alarm_max_work_count" value='<jsp:getProperty name="configBean" property="alarmMaxWorkCount" />'/></td>
      </tr>
      <tr>
         <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="process_work_count" />&nbsp;</td>
         <td align="left" colspan="2"><input type="text" cols="5" tabIndex="22" name="ec_process_work_count" value='<jsp:getProperty name="configBean" property="processWorkCount" />'/></td>
      </tr>
      <tr>
         <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="task_finalization_duration" />&nbsp;</td>
         <td align="left" colspan="2"><input type="text" cols="5" tabIndex="23" name="ec_task_finalization_duration" value='<jsp:getProperty name="configBean" property="taskFinalizationDuration" />'/></td>
      </tr>
      
      <tr height="1">
        <td colspan="3" height="1"></td>
      </tr>
      <tr height="1">
        <td colspan="3" height="1"></td>
      </tr>
      <tr height="1">
        <td height="1" colspan="3" class="gridLines"></td>
      </tr>
      <tr height="5">
         <td height="5" colspan="3"></td>
      </tr>

      <tr>
        <td colspan="3" align="left"><input type="submit" tabIndex="24" value=<ae:GetResource name="update" /> /></td>
      </tr>

      <input type="hidden" name="isSubmit" value="true" />
      </ form>
   </table>

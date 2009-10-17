<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

<jsp:useBean id="addMappingBean" class="org.activebpel.rt.bpeladmin.war.web.urn.AeAddMappingBean" />
<jsp:useBean id="tabBean" scope="request" class="org.activebpel.rt.war.web.tabs.AeTabBean" />

<jsp:setProperty name="tabBean" property="selectedTabString" param="tab" /> 

<ae:IfParamMatches property="isAdd" value="true">
   <jsp:setProperty name="addMappingBean" property="URN" param="URN_input" />
   <jsp:setProperty name="addMappingBean" property="URL" param="URL_input" />
   <jsp:setProperty name="addMappingBean" property="finished" value="true" />
</ae:IfParamMatches>

<ae:IfParamMatches property="isDelete" value="true">
   <jsp:useBean id="removeMappingsBean" class="org.activebpel.rt.bpeladmin.war.web.urn.AeRemoveMappingsBean" />
   <jsp:setProperty name="removeMappingsBean" property="deleteKey" value="delete" />
   <% removeMappingsBean.setFormData( request.getParameterMap() ); %>
</ae:IfParamMatches>

<script>
function editMapping(offset)
{
   document.forms["addForm"].elements["URN_input"].value=eval("document.forms['deletesForm'].elements['urn_' + offset]").value
   document.forms["addForm"].elements["URL_input"].value=eval("document.forms['deletesForm'].elements['url_' + offset]").value
}
</script>

<jsp:useBean id="listingBean" class="org.activebpel.rt.bpeladmin.war.web.urn.AeListMappingsBean" />

      <table border="0" cellpadding="0" cellspacing="0" width="100%">
      <!-- EXISTING MAPPINGS -->
      <ae:IfTrue name="listingBean" property="empty">
         <tr height="10">
            <td height="10" colspan="5 class="gridLines"></td>
         </tr>
         <tr>
            <th colspan="5" class="titleHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="urn-no-mappings" />&nbsp;</th>
         </tr>
         <tr height="1">
            <td height="1" colspan="5" class="gridLines"></td>
         </tr>
      </ae:IfTrue>
      <ae:IfFalse name="listingBean" property="empty">
         <form name="deletesForm" method="POST" action="config.jsp">
         <input type="hidden" name="tab" value="<jsp:getProperty name="tabBean" property="selectedOffset"/>"/>
         <tr>
            <th class="columnHeaders" align="left" nowrap="true"><ae:GetResource name="delete" /></th>
            <th class="columnHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="urn-header" />&nbsp;</th>
            <th class="columnHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="url-header" />&nbsp;</th>
         </tr>
         <ae:IndexedProperty name="listingBean" id="pair" property="URNMapping" indexedClassName="org.activebpel.rt.bpeladmin.war.web.urn.AeURNMapping" >
            <tr>
               <td align="left">&nbsp;
                  <input type="checkbox" name="delete" value="<jsp:getProperty name="pair" property="encodedURN" />"/> 
               </td>
               <td align="left">&nbsp;
                  <a href="javascript:editMapping(<jsp:getProperty name="pairIndex" property="index" />)"><jsp:getProperty name="pair" property="displayURN" /></a>
               </td>
               <td align="left">&nbsp;
                  <jsp:getProperty name="pair" property="displayURL" />
                  <input type="hidden" name="urn_<jsp:getProperty name="pairIndex" property="index" />" value="<jsp:getProperty name="pair" property="displayURN" />"/>
                  <input type="hidden" name="url_<jsp:getProperty name="pairIndex" property="index" />" value="<jsp:getProperty name="pair" property="displayURL" />"/>                  
               </td>
            </tr>
            <tr height="1">
              <td colspan="3" height="1" class="tabular"></td>
            </tr>
         </ae:IndexedProperty>
            
         <tr height="5">
            <td height="5" colspan="5"></td>
         </tr>
         <tr>
            <td colspan="3" align="left">
               <input type="hidden" name="isDelete" value="true"/>
               <input type="submit" value="<ae:GetResource name="urn-delete-selected" />"/>
            </td>
         </tr>
         </form>
      </ae:IfFalse>

            <!-- "Space between the list and the add form" -->
            <tr>
              <td colspan="5"></td>
            </tr>

            <!-- "Additional space with message text if needed" -->
            <ae:IfTrue name="addMappingBean" property="statusDetailAvailable" >
               <tr>
                 <td colspan="5"><em><jsp:getProperty name="addMappingBean" property="statusDetail"/></em></td>
               </tr>
            </ae:IfTrue>
            
            
            <!-- Add Mapping Form -->
            <form name="addForm" action="config.jsp" method="POST">
            <input type="hidden" name="tab" value="<jsp:getProperty name="tabBean" property="selectedOffset"/>"/>
            <tr>
               <th colspan="3"  class="columnHeaders" align="left" nowrap="true">&nbsp;Add new config</th>
            </tr>
            <tr height="1">
               <td height="1" colspan="3"</td>
            </tr>
            <tr>
               <td colspan="1" align="left" nowrap="true">&nbsp;<b><ae:GetResource name="urn-header"/></b></td>
               <td colspan="2" align="left" nowrap="true">&nbsp;<input type="text" name="URN_input" size="80" value=""/></td>
            </tr>
            <tr height="1">
               <td colspan="5" height="1" class="tabular"></td>
            </tr>
            <tr>
               <td colspan="1" align="left" nowrap="true">&nbsp;<b><ae:GetResource name="url-header"/></b></td>
               <td colspan="2" align="left" nowrap="true">&nbsp;<input type="text" name="URL_input" size="80" value=""/></td>
            </tr>
         <tr height="1">
            <td height="1" colspan="3"</td>
         </tr>
         <tr height="1">
           <td height="1" colspan="3" class="gridLines"></td>
         </tr>
         <tr height="1">
           <td height="5" colspan="5"></td>
         </tr>
         <tr>           
           <td colspan="3">
              <input type="hidden" name="isAdd" value="true" />
              <input type="submit" value="<ae:GetResource name="urn-add"/>" />
           </td>
         </tr>
         </form>
         </table>
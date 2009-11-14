<%@page contentType="text/html; charset=UTF-8" import="org.activebpel.rt.util.*,org.activebpel.rt.bpel.server.engine.*,org.activebpel.rt.bpel.server.admin.*,javax.xml.namespace.QName,java.text.*,org.activebpel.rt.bpel.*"  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>

<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

   <%-- Use UTF-8 to decode request parameters. --%>
   <ae:RequestEncoding value="UTF-8" />

   <jsp:useBean id="storageBean" class="org.activebpel.rt.bpeladmin.war.web.AeStorageBean" />
   <jsp:useBean id="pruningBean" class="org.activebpel.rt.bpeladmin.war.web.AeProcessPruningBean" />

   <ae:IfParamMatches property="updateStorage" value="true">
      <jsp:setProperty name="storageBean" property="jndiLocation" param="jndi_location" />
      <jsp:setProperty name="storageBean" property="databaseType" param="database_type" />
      <jsp:setProperty name="storageBean" property="username" param="username" />
      <jsp:setProperty name="storageBean" property="password" param="password" />
      <jsp:setProperty name="storageBean" property="passwordConfirm" param="password_confirm" />
      <jsp:setProperty name="storageBean" property="taminoDomain" param="tamino_domain" />
      <jsp:setProperty name="storageBean" property="taminoUrl" param="tamino_url" />
      <jsp:setProperty name="storageBean" property="taminoDatabaseName" param="tamino_database" />
      <jsp:setProperty name="storageBean" property="taminoCollection" param="tamino_collection" />
      <jsp:setProperty name="storageBean" property="taminoPoolsize" param="tamino_poolsize" />
      <jsp:setProperty name="storageBean" property="finished" value="true" />
   </ae:IfParamMatches>

   <ae:IfParamMatches property="pruneStorage" value="true">
      <ae:SetDate name="pruningBean" property="pruneDate" param="pruneDate" patternKey="date_pattern" />

      <!-- At most one of pruneSubmitQuery, pruneSubmitDelete or pruneSubmitCancel will be non-empty. -->
      <jsp:setProperty name="pruningBean" property="pruneCommandQuery"  param="pruneSubmitQuery" />
      <jsp:setProperty name="pruningBean" property="pruneCommandDelete" param="pruneSubmitDelete" />
      <jsp:setProperty name="pruningBean" property="pruneCommandCancel" param="pruneSubmitCancel" />

      <jsp:setProperty name="pruningBean" property="finished" value="true" />
   </ae:IfParamMatches>

   <jsp:include page="calendarScript.jsp" />

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
            <th colspan="3" class="pageHeaders" align="left" nowrap="true"><ae:GetResource name="storage" /></th>
          </tr>

          <!-- If persistent configuration. -->
          <ae:IfTrue name="storageBean" property="persistent" >
            <tr>
              <td>
                <!-- engine info table -->
                <form name="ec_form" method="post" action="storage.jsp">
                  <tr>
                    <th class="columnHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="property_name" />&nbsp;</th>
                    <th class="columnHeaders" align="left" nowrap="true" colspan="2">&nbsp;<ae:GetResource name="property_value" />&nbsp;</th>
                  </tr>
	               <!-- ***************** JNDI LOCATION ***************** -->
                  <ae:IfTrue name="storageBean" property="jndiDataSource" >
                  <tr height="1">
                    <td colspan="3" height="1" class="tabular"></td>
                  </tr>
                  <tr>
                    <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="jndi_location" />&nbsp;</td>
                    <td align="left" colspan="2">
                      <ae:IfTrue name="storageBean" property="engineRunning" >
                        &nbsp;<jsp:getProperty name="storageBean" property="jndiLocation" />
                      </ae:IfTrue>
                      <ae:IfFalse name="storageBean" property="engineRunning" >
                        &nbsp;<input type="text" size="50" name="jndi_location" value='<jsp:getProperty name="storageBean" property="jndiLocation" />'/>
                      </ae:IfFalse>
                    </td>
                  </tr>
	               </ae:IfTrue>
	               <!-- ***************** DB TYPE ***************** -->
                  <tr height="1">
                    <td colspan="3" height="1" class="tabular"></td>
                  </tr>
                  <tr>
                    <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="database_type" />&nbsp;</td>
                    <td align="left" colspan="2">
                      <ae:IfTrue name="storageBean" property="engineRunning" >
                        &nbsp;<jsp:getProperty name="storageBean" property="databaseType" />
                      </ae:IfTrue>
                      <ae:IfFalse name="storageBean" property="engineRunning" >
                        &nbsp;<input type="text" cols="50" name="database_type" value='<jsp:getProperty name="storageBean" property="databaseType" />'/>
                      </ae:IfFalse>
                    </td>
                  </tr>
	               <!-- ***************** URL ***************** -->
                  <ae:IfTrue name="storageBean" property="tamino" >
	                  <tr height="1">
	                    <td colspan="3" height="1" class="tabular"></td>
	                  </tr>
	                  <tr>
	                    <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="url" />&nbsp;</td>
	                    <td align="left" colspan="2">
	                      <ae:IfTrue name="storageBean" property="engineRunning" >
	                        &nbsp;<jsp:getProperty name="storageBean" property="taminoUrl" />
	                      </ae:IfTrue>
	                      <ae:IfFalse name="storageBean" property="engineRunning" >
	                        &nbsp;<input type="text" size="40" name="tamino_url" value='<jsp:getProperty name="storageBean" property="taminoUrl" />'/>
	                      </ae:IfFalse>
	                    </td>
	                  </tr>
	               </ae:IfTrue>
	               <!-- ***************** DB NAME ***************** -->
                  <ae:IfTrue name="storageBean" property="tamino" >
	                  <tr height="1">
	                    <td colspan="3" height="1" class="tabular"></td>
	                  </tr>
	                  <tr>
	                    <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="database_name" />&nbsp;</td>
	                    <td align="left" colspan="2">
	                      <ae:IfTrue name="storageBean" property="engineRunning" >
	                        &nbsp;<jsp:getProperty name="storageBean" property="taminoDatabaseName" />
	                      </ae:IfTrue>
	                      <ae:IfFalse name="storageBean" property="engineRunning" >
	                        &nbsp;<input type="text" size="25" name="tamino_database" value='<jsp:getProperty name="storageBean" property="taminoDatabaseName" />'/>
	                      </ae:IfFalse>
	                    </td>
	                  </tr>
	               </ae:IfTrue>
	               <!-- ***************** COLLECTION ***************** -->
                  <ae:IfTrue name="storageBean" property="tamino" >
	                  <tr height="1">
	                    <td colspan="3" height="1" class="tabular"></td>
	                  </tr>
	                  <tr>
	                    <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="collection" />&nbsp;</td>
	                    <td align="left" colspan="2">
	                      <ae:IfTrue name="storageBean" property="engineRunning" >
	                        &nbsp;<jsp:getProperty name="storageBean" property="taminoCollection" />
	                      </ae:IfTrue>
	                      <ae:IfFalse name="storageBean" property="engineRunning" >
	                        &nbsp;<input type="text" size="25" name="tamino_collection" value='<jsp:getProperty name="storageBean" property="taminoCollection" />'/>
	                      </ae:IfFalse>
	                    </td>
	                  </tr>
	               </ae:IfTrue>
	               <!-- ***************** POOL SIZE ***************** -->
                  <ae:IfTrue name="storageBean" property="tamino" >
	                  <tr height="1">
	                    <td colspan="3" height="1" class="tabular"></td>
	                  </tr>
	                  <tr>
	                    <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="connection_pool_size" />&nbsp;</td>
	                    <td align="left" colspan="2">
	                      <ae:IfTrue name="storageBean" property="engineRunning" >
	                        &nbsp;<jsp:getProperty name="storageBean" property="taminoPoolsize" />
	                      </ae:IfTrue>
	                      <ae:IfFalse name="storageBean" property="engineRunning" >
	                        &nbsp;<input type="text" size="5" name="tamino_poolsize" value='<jsp:getProperty name="storageBean" property="taminoPoolsize" />'/>
	                      </ae:IfFalse>
	                    </td>
	                  </tr>
	               </ae:IfTrue>
	               <!-- ***************** DOMAIN ***************** -->
                  <ae:IfTrue name="storageBean" property="tamino" >
	                  <tr height="1">
	                    <td colspan="3" height="1" class="tabular"></td>
	                  </tr>
	                  <tr>
	                    <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="domain" />&nbsp;</td>
	                    <td align="left" colspan="2">
	                      <ae:IfTrue name="storageBean" property="engineRunning" >
	                        &nbsp;<jsp:getProperty name="storageBean" property="taminoDomain" />
	                      </ae:IfTrue>
	                      <ae:IfFalse name="storageBean" property="engineRunning" >
	                        &nbsp;<input type="text" size="16" name="tamino_domain" value='<jsp:getProperty name="storageBean" property="taminoDomain" />'/>
	                      </ae:IfFalse>
	                    </td>
	                  </tr>
	               </ae:IfTrue>
	               <!-- ***************** USERNAME ***************** -->
                  <ae:IfFalse name="storageBean" property="engineRunning" >
	                  <tr height="1">
	                    <td colspan="3" height="1" class="tabular"></td>
	                  </tr>
	                  <tr>
	                    <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="username" />&nbsp;</td>
	                    <td align="left" colspan="2">
	                        &nbsp;<input type="text" size="16" name="username" value='<jsp:getProperty name="storageBean" property="username" />'/>
	                    </td>
	                  </tr>
	               </ae:IfFalse>
                  <ae:IfTrue name="storageBean" property="engineRunning" >
                     <ae:IfTrue name="storageBean" property="credentials" >
		                  <tr height="1">
		                    <td colspan="3" height="1" class="tabular"></td>
		                  </tr>
		                  <tr>
		                    <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="username" />&nbsp;</td>
		                    <td align="left" colspan="2">
		                        &nbsp;<jsp:getProperty name="storageBean" property="username" />
		                    </td>
		                  </tr>
		               </ae:IfTrue>
		            </ae:IfTrue>
	               <!-- ***************** PASSWORD ***************** -->
                  <!-- If engine running they can't change the password. -->
                  <ae:IfFalse name="storageBean" property="engineRunning" >
                    <tr height="1">
                      <td colspan="3" height="1" class="tabular"></td>
                    </tr>
                    <tr>
                      <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="password_optional" />&nbsp;</td>
                      <td align="left" colspan="2">&nbsp;<input type="password" cols="50" name="password" value='<jsp:getProperty name="storageBean" property="password" />'/></td>
                    </tr>
                    <tr height="1">
                      <td colspan="3" height="1" class="tabular"></td>
                    </tr>
                    <tr>
                      <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="confirm_password" />&nbsp;</td>
                      <td align="left" colspan="2">&nbsp;<input type="password" cols="50" name="password_confirm" value='<jsp:getProperty name="storageBean" property="password" />'/></td>
                    </tr>
                  </ae:IfFalse>

                  <!-- If persistent configuration is not available for use display an error. -->
                  <ae:IfFalse name="storageBean" property="available" >
                    <tr height="1">
                      <td colspan="3" height="1" class="tabular"></td>
                    </tr>
                    <tr>
                      <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="error_message" />&nbsp;</td>
                      <td align="left" colspan="2">&nbsp;<jsp:getProperty name="storageBean" property="errorMessage"/></td>
                    </tr>
                  </ae:IfFalse>
                  <tr height="1">
                    <td colspan="3" height="1"></td>
                  </tr>
                  <tr height="1">
                    <td height="1" colspan="3" class="gridLines"></td>
                  </tr>

                  <!-- Allow update only if engine is offline  -->
                  <ae:IfTrue name="storageBean" property="engineRunning" >
                    <tr>
                      <td colspan="3" align="right"><ae:GetResource name="stop_engine_to_change_storage_settings" /></td>
                    </tr>
                  </ae:IfTrue>
                  <ae:IfFalse name="storageBean" property="engineRunning" >
                    <tr>
                      <td colspan="3" align="right"><input type="submit" value=<ae:GetResource name="update" />></td>
                    </tr>
                  </ae:IfFalse>

                  <input type="hidden" name="updateStorage" value="true" />
                </form>
              </td>
            </tr>
          </ae:IfTrue>

          <!-- If not persistent configuration. -->
          <ae:IfFalse name="storageBean" property="persistent" >
            <tr height="1">
              <td height="1" colspan="3" class="gridLines"></td>
            </tr>
            <tr>
              <td colspan="3"><ae:GetResource name="no_persistent_storage" /></td>
            </tr>
          </ae:IfFalse>

          <ae:IfTrue name="pruningBean" property="storageAvailable">
            <tr>
              <td colspan="3"></td>
            </tr>
            <!-- Prune Form -->
            <tr>
              <td colspan="3" align="left">
                <form name="prune_form" method="post" action="storage.jsp">
                  <table width="50%">
                    <tr>
                      <td colspan="2" class="titleHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="delete_completed_processes" />&nbsp;</td>
                    <tr>
                    <tr height="1">
                      <td height="1" colspan="2" class="gridLines"></td>
                    </tr>
                    <tr>
                      <td class="labelHeaders" align="left" nowrap="true" width="40%">&nbsp;<ae:GetResource name="completed_before" />&nbsp;</td>
                      <ae:IfFalse name="pruningBean" property="prunePending">
                        <td align="left"><input type="text" name="pruneDate" value='<ae:DateFormatter name="pruningBean" property="pruneDate" patternKey="date_pattern" />' size="10"/>&nbsp;<img src="images/calendar.gif" onClick="javascript:displayCalendar(document.forms['prune_form'].pruneDate)" />&nbsp;<span class="iconText"><ae:GetResource name="date_format_example" /></span></td>
                      </ae:IfFalse>
                      <ae:IfTrue name="pruningBean" property="prunePending">
                         <td align="left"><ae:DateFormatter name="pruningBean" property="pruneDate" patternKey="date_pattern" /><input type="hidden" name="pruneDate" value='<ae:DateFormatter name="pruningBean" property="pruneDate" patternKey="date_pattern" />'/></td>                        
                      </ae:IfTrue>
                    </tr>
                    <tr height="1">
                      <td colspan="2" height="1" class="tabular"></td>
                    </tr>
                    <ae:IfTrue name="pruningBean" property="statusDetailAvailable">
                      <tr>
                        <td align="left" colspan="2">&nbsp;<jsp:getProperty name="pruningBean" property="statusDetail"/></td>
                      </tr>
                      <tr height="1">
                        <td height="1" colspan="2" class="tabular"></td>
                      </tr>
                    </ae:IfTrue>
                    <tr height="1">
                      <td colspan="2" height="1"></td>
                    </tr>
                    <tr>
                      <ae:IfFalse name="pruningBean" property="prunePending">
                        <td colspan="2" align="center"><input type="submit" name="pruneSubmitQuery" value=<ae:GetResource name="delete" /> /></td>
                      </ae:IfFalse>
                      <ae:IfTrue name="pruningBean" property="prunePending">
                        <td colspan="2" align="center"><ae:IfTrue name="pruningBean" property="pruneValid"><input type="submit" name="pruneSubmitDelete" value=<ae:GetResource name="ok" /> />&nbsp;</ae:IfTrue><input type="submit" name="pruneSubmitCancel" value=<ae:GetResource name="cancel" /> /></td>
                      </ae:IfTrue>
                    </tr>
                    <tr height="1">
                      <td height="1" colspan="2" class="gridLines"></td>
                    </tr>
                  </table>
                  <input type="hidden" name="pruneStorage" value="true" />
                </form>
              </td>
            </tr>
          </ae:IfTrue>
        </table>
      </td>

      <!-- main and right margin -->
      <td width="3%"></td>
    </tr>
  </table>
  <br>
  <jsp:include page="footer.jsp" />
</body>
</html>

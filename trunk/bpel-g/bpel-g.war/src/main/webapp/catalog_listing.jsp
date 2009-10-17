<%@page contentType="text/html; charset=UTF-8" 
    import="org.activebpel.rt.util.*,org.activebpel.rt.bpel.server.engine.*,org.activebpel.rt.bpel.server.admin.*,javax.xml.namespace.QName,java.text.*,org.activebpel.rt.bpel.*,org.activebpel.rt.bpel.impl.list.*"  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>

<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

   <%-- Use UTF-8 to decode request parameters. --%>
   <ae:RequestEncoding value="UTF-8" />

   <jsp:useBean id="catalogItemBean" class="org.activebpel.rt.bpeladmin.war.web.AeCatalogListingBean" />
   
   <%-- if the page is being reloaded as the result --%>
   <%-- of a submit, update the bean with the form  --%>
   <%-- properties                          --%>
   <ae:IfParamMatches property="isSubmit" value="true">
     <jsp:setProperty name="catalogItemBean" property="filterType" param="filterType" />
     <jsp:setProperty name="catalogItemBean" property="filterResource" param="filterResource" />
     <jsp:setProperty name="catalogItemBean" property="filterNamespace" param="filterNamespace" />
     <jsp:setProperty name="catalogItemBean" property="rowStart" param="rowOffset" />
     <jsp:setProperty name="catalogItemBean" property="rowCount" param="resourceCount" />
   </ae:IfParamMatches>

   <%-- this signals the bean that all of the form --%>
   <%-- properties have been set - it will now    --%>
   <%-- fetch the detail rows                 --%>
   <jsp:setProperty name="catalogItemBean" property="finished" value="true" />


   <jsp:include page="header_head.jsp" />
   
   <script>
   
     function gotoLast( aTotalRows, aRowCount )
     {
       var lastRows = aTotalRows%aRowCount;
       if( lastRows == 0 )
       {
         lastRows = aRowCount;
       }
       document.forms["filterForm"].rowOffset.value=aTotalRows-lastRows;
       document.forms["filterForm"].submit();
     }

     function clearForm()
     {
       document.forms["filterForm"].filterType[0].selected = true;
       document.forms["filterForm"].filterResource.value = "";
       document.forms["filterForm"].filterNamespace.value = "";
     }
     
   </script>

   
   <body> 
   
      <table border="0" cellpadding="0" cellspacing="0" width="100%" align="center">
         <tr>
            <td valign="top" width="20%">
               <jsp:include page="header_nav.jsp" />
            </td>
      
            <!-- spacer between nav and main -->
            <td width="3%"></td>
   
            <td valign="top">
              <form action="catalog_listing.jsp" name="filterForm" method="get">
                <table border="0" cellpadding="0" cellspacing="0" width="100%" align="left">

                  <!-- Resource header area -->
                  <tr>
                   <td>
                    <table border="0" cellpadding="0" cellspacing="0" width="100%" align="left">
                      <tr>
                         <td colspan="2" class="pageHeaders" align="left" nowrap="true"><ae:GetResource name="resource_catalog" /></th>
                      <tr>
                      <tr height="1">
                        <td colspan="2" height="1" class="gridLines"></td>
                      </tr>
                      <tr>
                         <td width="20%" class="labelHeaders">&nbsp;<ae:GetResource name="total_reads" /></td>
                         <td width="80%" >&nbsp;<jsp:getProperty name="catalogItemBean" property="totalReads" /></td>
                      </tr>
                      <tr height="1">
                        <td height="1" colspan="2" class="tabular"></td>
                      </tr>
                      <tr>
                         <td width="20%" class="labelHeaders">&nbsp;<ae:GetResource name="disk_reads" /></td>
                         <td width="80%" >&nbsp;<jsp:getProperty name="catalogItemBean" property="diskReads" />&nbsp;&nbsp;(<jsp:getProperty name="catalogItemBean" property="diskReadsPercent" />)</td>
                      </tr>
                      <tr height="1">
                         <td height="1" colspan="2" class="tabular"></td>
                      </tr>
                      <tr>
                         <td width="20%" class="labelHeaders">&nbsp;<ae:GetResource name="cache_size" /></td>
                         <td width="80%" ><jsp:getProperty name="catalogItemBean" property="cacheSize" /></td>
                      </tr>
                      <tr height="1">
                         <td colspan="2" height="1" class="gridLines"></td>
                      </tr>
                      <tr height="4">
                         <td colspan="2"></td>
                      </tr>
    
                      <tr>
                         <td colspan="2" bgcolor="white"></td>
                      </tr>
                    </table>
                   </td>
                  </tr>
                  <!-- End resource header area -->
                   
                  <!-- List and selection Form -->
                  <tr><td>
                    <table border="0" cellpadding="0" cellspacing="0" width="100%" align="left">
                      <tr>
                         <td colspan="3" class="titleHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="deployed_resources" />&nbsp;</th>
                      <tr>
                      <tr height="1">
                         <td height="1" colspan="3" class="gridLines"></td>
                      </tr>
                      
                      <!-- ONLY APPEARS IF THERE ARE NO RESULTS -->
                      <ae:IfTrue name="catalogItemBean" property="empty" >
                          <tr>
                              <td colspan="3" align="left" nowrap="true">&nbsp;<b><ae:GetResource name="no_resources_matching" /></b>&nbsp;</th>
                          <tr>
                      </ae:IfTrue>
                      
                      <!-- ONLY APPEARS IF THERE ARE RESULTS -->
                      <ae:IfFalse name="catalogItemBean" property="empty" >
                          <tr>
                             <td class="labelHeaders">&nbsp;<ae:GetResource name="type" /></td>
                             <td class="labelHeaders">&nbsp;<ae:GetResource name="resource" /></td>
                             <td class="labelHeaders">&nbsp;<ae:GetResource name="namespace" /></td>
                          </tr>
                          <tr height="1">
                             <td height="1" colspan="3" class="gridLines"></td>
                          </tr>
                          <ae:IndexedProperty name="catalogItemBean" id="resourceRow" property="detail" indexedClassName="org.activebpel.rt.bpel.impl.list.AeCatalogItem" >
                             <tr>
                                <td width="15%">&nbsp;<a title="<jsp:getProperty name='resourceRow' property='location' />" href='catalog_detail.jsp?locationHint=<jsp:getProperty name="resourceRow" property="location" />'><jsp:getProperty name="resourceRow" property="typeDisplay" /></a></td>
                                <td width="30%">&nbsp;<a title="<jsp:getProperty name='resourceRow' property='location' />" href='catalog_detail.jsp?locationHint=<jsp:getProperty name="resourceRow" property="location" />'><jsp:getProperty name="resourceRow" property="formattedName" /></a></td>
                                <td width="55%">&nbsp;<a href='catalog_detail.jsp?locationHint=<jsp:getProperty name="resourceRow" property="location" />'><jsp:getProperty name="resourceRow" property="namespace" /></a></td>
                             </tr>
                             <tr height="1">
                               <td height="1" colspan="3" class="tabular"></td>
                             </tr>
                          </ae:IndexedProperty>
                          
                        <!-- Navigation controls -->
                        <tr height="1">
                          <td height="1" colspan="5" class="gridLines"></td>
                        </tr>
                        
                        <tr>
                          <td colspan="3">
                             <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                <tr>
                                  <td>
                                     <table border="0" cellpadding="0" cellspacing="0">
                                         <tr>
                                            <td nowrap="true" class="filterFields"><select name="resourceCount" id="records per page" class="filterFields" onChange='document.forms["filterForm"].submit()'>
                                            <option value="10" <ae:IfPropertyMatches name="catalogItemBean" property="rowCount" value="10" classType="int">selected="true"</ae:IfPropertyMatches>>10</option>
                                            <option value="20" <ae:IfPropertyMatches name="catalogItemBean" property="rowCount" value="20" classType="int">selected="true"</ae:IfPropertyMatches>>20</option>
                                            <option value="30" <ae:IfPropertyMatches name="catalogItemBean" property="rowCount" value="30" classType="int">selected="true"</ae:IfPropertyMatches>>30</option>
                                            <option value="40" <ae:IfPropertyMatches name="catalogItemBean" property="rowCount" value="40" classType="int">selected="true"</ae:IfPropertyMatches>>40</option>
                                            <option value="50" <ae:IfPropertyMatches name="catalogItemBean" property="rowCount" value="50" classType="int">selected="true"</ae:IfPropertyMatches>>50</option></select></td>
                                            <td nowrap="true" class="iconText">&nbsp;<label for="records per page"><ae:GetResource name="records_per_page" /></label></td>
                                         </tr>
                                     </table>
                                  </td>
                                  <td colspan="1" align="left" nowrap="true" class="iconText"><ae:GetResource name="results" />&nbsp;<jsp:getProperty name="catalogItemBean" property="displayStart" />&nbsp;-&nbsp;<jsp:getProperty name="catalogItemBean" property="displayEnd" />&nbsp;<ae:GetResource name="of" />&nbsp;<jsp:getProperty name="catalogItemBean" property="totalRowCount" /></td>
                                  <td align="right" nowrap="true" class="iconText">
                                     &nbsp;
                                     <ae:IfTrue name="catalogItemBean" property="previousPage" >
                                       <span style="font-size:10pt" >
                                         <a href='javascript:document.forms["filterForm"].rowOffset.value=0;document.forms["filterForm"].submit()' title='<ae:GetResource name="first_page" />'>&laquo;&laquo;&nbsp;</a>
                                         <a href='javascript:document.forms["filterForm"].rowOffset.value=<jsp:getProperty name="catalogItemBean" property="previousPageOffset" />;document.forms["filterForm"].submit()' title='<ae:GetResource name="previous_page" />'>&laquo;&nbsp;</a>
                                       </span>
                                     </ae:IfTrue>
                                  </td>
                                  <td align="right" nowrap="true" class="iconText">
                                      &nbsp;
                                     <ae:IfTrue name="catalogItemBean" property="nextPage" >
                                       <span style="font-size:10pt" >
                                         <a href='javascript:document.forms["filterForm"].rowOffset.value=<jsp:getProperty name="catalogItemBean" property="nextPageOffset" />;document.forms["filterForm"].submit()' title='<ae:GetResource name="next_page" />'>&raquo;</a>&nbsp;
                                         <a href='javascript:gotoLast(<jsp:getProperty name="catalogItemBean" property="totalRowCount" />,<jsp:getProperty name="catalogItemBean" property="rowCount" />);' title='<ae:GetResource name="last_page" />'>&nbsp;&raquo;&raquo;</a>
                                       </span>
                                     </ae:IfTrue>
                                  </td>
                                </tr>
                             </table>
                          </td>
                        </tr>
                      </ae:IfFalse>

                      <!-- Spacer between list and form -->
                      <tr height="1">
                        <td height="1" colspan="5" class="gridLines"></td>
                      </tr>
                      <tr height="1">
                        <td height="10" colspan="5"></td>
                      </tr>

                      <!-- Selection Form -->
                      <tr>
                        <td colspan="3" align="right">
                          <table width="65%">
                            <tr>
                              <td colspan="2" class="titleHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="selection_filter" />&nbsp;</td>
                            <tr>
                            <tr height="1">
                              <td height="1" colspan="2" class="gridLines"></td>
                            </tr>
                            <tr>
                              <td class="labelHeaders" align="left">&nbsp;<ae:GetResource name="type_colon" />&nbsp;</td>
                              <td align="left">
                                <select name="filterType" class="filterFields">
                                   <ae:IndexedProperty name="catalogItemBean" id="typeRow" property="itemType" indexedClassName="org.activebpel.rt.bpeladmin.war.web.AeCatalogItemType" >
                                      <option value="<jsp:getProperty name='typeRow' property='typeNumber' />" <ae:IfTrue name="typeRow" property="selected" >selected="true"</ae:IfTrue>><jsp:getProperty name='typeRow' property='typeDisplay' /></option>
                                   </ae:IndexedProperty>
                                </select>                        
                              </td>
                            </tr>
                            <tr height="1">
                              <td height="1" colspan="2" class="tabular"></td>
                            </tr>
                            <tr>
                              <td class="labelHeaders" align="left">&nbsp;<ae:GetResource name="resource_colon" />&nbsp;</td>
                              <td align="left"><input type="text" name="filterResource" value='<jsp:getProperty name="catalogItemBean" property="filterResource" />' size="50" maxlength="255"/></td>
                            </tr>
                            <tr height="1">
                              <td colspan="2" height="1" class="tabular"></td>
                            </tr>
                            <tr>
                              <td class="labelHeaders" align="left">&nbsp;<ae:GetResource name="namespace_colon" />&nbsp;</td>
                              <td align="left"><input type="text" name="filterNamespace" value='<jsp:getProperty name="catalogItemBean" property="filterNamespace" />' size="50" maxlength="255"/></td>
                            </tr>
                            <tr height="1">
                              <td colspan="2" height="1" class="tabular"></td>
                            </tr>
                            <tr>
                              <td colspan="2" align="center"><input type="submit" value=<ae:GetResource name="submit" /> />&nbsp;<input type="button" value=<ae:GetResource name="clear" /> onclick="clearForm()"/></td>
                            </tr>
                            <tr height="1">
                              <td colspan="2" height="1"></td>
                            </tr>
                            <tr height="1">
                              <td height="1" colspan="2" class="gridLines"></td>
                            </tr>
                            <tr>
                              <td colspan="2" height="1"></td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <!-- End Selection Form -->
                      
                    </table>
                   </td>
                  </tr>
                  <!-- End List -->
                </table>
                <input type="hidden" name="rowOffset" value="0" />
                <input type="hidden" name="isSubmit" value="true" />
               </form>
               
            </td>

            <!-- main and right margin       -->
            <td width="3%"></td>
         </tr>
      </table>
      
      <br> 
      <jsp:include page="footer.jsp" />
   </body>
</html>

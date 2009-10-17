<%@page contentType="text/html; charset=UTF-8" import="org.activebpel.rt.util.*,org.activebpel.rt.bpel.server.engine.*,org.activebpel.rt.bpel.server.admin.*,javax.xml.namespace.QName,java.text.*,org.activebpel.rt.bpel.*"  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>

<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

   <%-- Use UTF-8 to decode request parameters. --%>
   <ae:RequestEncoding value="UTF-8" />

   <jsp:useBean id="recsBean" class="org.activebpel.rt.bpeladmin.war.web.AeMessageReceiversListingBean" />

   <ae:IfParamMatches property="isSubmit" value="true">
      <jsp:setProperty name="recsBean" property="rowCount" param="rowCount" />
      <jsp:setProperty name="recsBean" property="rowStart" param="rowOffset" />
      <jsp:setProperty name="recsBean" property="partnerLinkTypeName" param="partnerLinkTypeName" />
      <jsp:setProperty name="recsBean" property="portType" param="portType" />
      <jsp:setProperty name="recsBean" property="operation" param="operation" />
      <jsp:setProperty name="recsBean" property="processId" param="processId" />
   </ae:IfParamMatches>

   <jsp:setProperty name="recsBean" property="finished" value="true" />

   <jsp:include page="header_head.jsp" />
   <script type="text/javascript" language="JavaScript" src="script/ae_winutil.js"></script>
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

      var detailWindow = null;
      
      
      function clearAll()
      {
         var form = document.forms["filterForm"];
         form.partnerLinkTypeName.value="";
         form.portType.value="";
         form.operation.value="";
         form.processId.value="";
      }
      
      function openDetailWindow( aPartnerLinkName, aPortType, aOperation, aPID, aCorrelationData )
      {
         closeDetailWindow();
		 detailWindow = aePopupWindow(self, '', 'AeQueueReceiveDetail', 640, 480, true, false,null);
         var doc = detailWindow.document;
         doc.write("<html>\n");
         doc.write('<head><title>Message Receiver Details</title><STYLE TYPE="text/css">@import url(css/ae.css);</STYLE></head>\n');
         doc.write("<body>\n" );

         doc.write('<table border="0" cellpadding="0" cellspacing="0" width="100%" align="left">\n');
         doc.write('<tr>');
         doc.write('<th colspan="2" class="titleHeaders" align="right" nowrap="true">&nbsp;<span class="iconText"><a href="javascript:window.close()" title="Close this window"><span style="font-weight:bold">Close</span></a></span>&nbsp;</th>\n');
         doc.write('</tr>\n');
         
         writeHeader( doc, 'Receiver Details' );
         writeGridline( doc );
         writeRow( doc, 'PartnerLink Type:', aPartnerLinkName );
         writeRow( doc, 'Port Type:', aPortType );
         writeRow( doc, 'Operation:', aOperation );   
         writeRow( doc, 'Process ID:', aPID );
         writeGridline( doc );
         writeEmptyRow( doc );
         
         writeHeader( doc, 'Correlation Data' );
         writeGridline( doc );
         
         doc.write('<tr>');
         doc.write('<td colspan="2" align="left"><textarea style="width:96%;overflow:auto;padding:5px;margin:5px;" wrap="off" readonly="readonly" rows="5" wrap="off" readonly="readonly">' + aCorrelationData + '</textarea></td>\n'); 
         doc.write('</tr>\n');
         doc.write('</table>\n');
         doc.write("</body></html>" );
         doc.close(); 
         
         detailWindow.focus();        
      }
      
      function writeHeader( aDoc, aTitle )
      {
         aDoc.write('<tr>');
         aDoc.write('<td colspan="2" class="titleHeaders" align="left" nowrap="true">&nbsp;' + aTitle + '&nbsp;</td>\n');
         aDoc.write('</tr>\n');
      }         

      function writeEmptyRow( aDoc )
      {
         aDoc.write('<tr>');
         aDoc.write('<td colspan="2" ></td>');
         aDoc.write('</tr>');
      }

      function writeRow( aDoc, aLabel, aValue )
      {
         var lineDelim = '<tr height="1">' +
                         '<td colspan="2" height="1" class="tabular"></td>'+
                         '</tr>';

         aDoc.write('<tr>\n');
         aDoc.write('<td class="labelHeaders">&nbsp;' + aLabel + '</td>\n');
         aDoc.write('<td>&nbsp;' + aValue + '</td>\n');
         aDoc.write('</tr>\n');
         aDoc.write( lineDelim );
      }
      
      function writeGridline( aDoc )
      {
         var gridLine = '<tr height="1">\n' +
                        '<td height="1" colspan="2" class="gridLines"></td>\n'+
                        '</tr>\n';
         aDoc.write( gridLine );
      }

      function closeDetailWindow()
      {
         if( detailWindow != null )
         {
            detailWindow.close();
            detailWindow = null;
         }
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
              <form name="filterForm" action="message_rec_queue.jsp" method="get">
                <table border="0" cellpadding="0" cellspacing="0" width="100%" align="left">
                     <tr>
                        <th colspan="4" class="pageHeaders" align="left" nowrap="true"><ae:GetResource name="receive_queue" /></th>
                     </tr>
                     <tr height="1">
                       <td height="1" colspan="4" class="gridLines"></td>
                     </tr>
                     
                     <!-- No record found -->
                     <ae:IfTrue name="recsBean" property="empty">
                        <tr>
                           <th class="titleHeaders" colspan="4" align="left" nowrap="true">&nbsp;<ae:GetResource name="no_matching_receives_in_queue" />&nbsp;</th>
                        </tr>
                        <tr height="1">
                          <td height="1" colspan="4" class="gridLines"></td>
                        </tr>
                     </ae:IfTrue>

                     <!-- Records found -->
                     <ae:IfFalse name="recsBean" property="empty">
                        <tr>
                          <td width="15%" class="columnHeaders" align="left">&nbsp;<ae:GetResource name="process_id" /></td> 
                          <td class="columnHeaders" align="left">&nbsp;<ae:GetResource name="partner_link_header" /></td>
                          <td class="columnHeaders" align="left">&nbsp;<ae:GetResource name="port_type_header" /></td>
                          <td class="columnHeaders" align="left">&nbsp;<ae:GetResource name="operation_header" /></td>
                        </tr>
                        <tr height="1">
                          <td height="1" colspan="4" class="gridLines"></td>
                        </tr>
                        <ae:IndexedProperty name="recsBean" id="recRow" property="messageReceiver" indexedClassName="org.activebpel.rt.bpeladmin.war.web.AeMessageReceiverDetailWrapper" >
                           <tr>
                              <td>&nbsp;<a target="aeprocessview" href='processview/processview_detail.jsp?pid=<jsp:getProperty name="recRow" property="processId" />&pathid=<jsp:getProperty name="recRow" property="locationPathId" />'><jsp:getProperty name="recRow" property="processId" /></a></td>
                              <td>&nbsp;<a href="javascript:openDetailWindow('<jsp:getProperty name="recRow" property="partnerLinkTypeName" />','<jsp:getProperty name="recRow" property="portType" />','<jsp:getProperty name="recRow" property="operation" />','<jsp:getProperty name="recRow" property="processId" />','<jsp:getProperty name="recRow" property="correlationData" />')"><jsp:getProperty name="recRow" property="partnerLinkTypeName" /></a></td>
                              <td>&nbsp;<jsp:getProperty name="recRow" property="portTypeLocal" /></td>
                              <td>&nbsp;<jsp:getProperty name="recRow" property="operation" /></td>
                           </tr>
                           <tr height="1">
                             <td colspan="4" height="1" class="tabular"></td>
                           </tr>
                        </ae:IndexedProperty>
                        <tr height="1">
                          <td height="1" colspan="4" class="gridLines"></td>
                        </tr>
                        <tr>
                          <td>
                            <table border="0" cellpadding="0" cellspacing="0">
                              <tr>
                                 <td nowrap="true" class="filterFields"><select name="rowCount" id="records per page" class="filterFields" onChange='document.forms["filterForm"].submit()'>
                                     <option value="10" <ae:IfPropertyMatches name="recsBean" property="rowCount" value="10" classType="int">selected="true"</ae:IfPropertyMatches>>10</option>
                                     <option value="20" <ae:IfPropertyMatches name="recsBean" property="rowCount" value="20" classType="int">selected="true"</ae:IfPropertyMatches>>20</option>
                                     <option value="30" <ae:IfPropertyMatches name="recsBean" property="rowCount" value="30" classType="int">selected="true"</ae:IfPropertyMatches>>30</option>
                                     <option value="40" <ae:IfPropertyMatches name="recsBean" property="rowCount" value="40" classType="int">selected="true"</ae:IfPropertyMatches>>40</option>
                                     <option value="50" <ae:IfPropertyMatches name="recsBean" property="rowCount" value="50" classType="int">selected="true"</ae:IfPropertyMatches>>50</option></select></td>
                                 <td nowrap="true" class="iconText">&nbsp;<label for="records per page"><ae:GetResource name="records_per_page" /></label></td>
                              </tr>
                            </table>
                          </td>
                          <td colspan="1" align="center" nowrap="true" class="iconText"><ae:GetResource name="results" />&nbsp;<jsp:getProperty name="recsBean" property="displayStart" />&nbsp;-&nbsp;<jsp:getProperty name="recsBean" property="displayEnd" />&nbsp;<ae:GetResource name="of" />&nbsp; <jsp:getProperty name="recsBean" property="totalRowCount" /></td>
                          <td align="right" nowrap="true" class="iconText">
                              &nbsp;
                              <ae:IfTrue name="recsBean" property="previousPage" >
                                 <span style="font-size:10pt" >
                                    <a href='javascript:document.forms["filterForm"].rowOffset.value=0;document.forms["filterForm"].submit()' title='<ae:GetResource name="first_page" />'>&laquo;&laquo;&nbsp;</a>
                                    <a href='javascript:document.forms["filterForm"].rowOffset.value=<jsp:getProperty name="recsBean" property="previousPageOffset" />;document.forms["filterForm"].submit()' title='<ae:GetResource name="previous_page" />'>&laquo;&nbsp;</a>
                                 </span>
                              </ae:IfTrue>
                          </td>
                           <td align="right" nowrap="true" class="iconText">
                              &nbsp;
                              <ae:IfTrue name="recsBean" property="nextPage" >
                                 <span style="font-size:10pt" >
                                    <a href='javascript:document.forms["filterForm"].rowOffset.value=<jsp:getProperty name="recsBean" property="nextPageOffset" />;document.forms["filterForm"].submit()' title='<ae:GetResource name="next_page" />'>&raquo;</a>&nbsp;
                                    <a href='javascript:gotoLast(<jsp:getProperty name="recsBean" property="totalRowCount" />,<jsp:getProperty name="recsBean" property="rowCount" />);' title='<ae:GetResource name="last_page" />'>&nbsp;&raquo;&raquo;</a>
                                 </span>

                              </ae:IfTrue>
                          </td>
                        </tr>
                  </ae:IfFalse>
                  
                  <!-- Gap  -->
                  <tr>
                    <td height="1" colspan="4"></td>
                  </tr>
                  
                  <!-- Filter specification -->
                  <tr><td colspan="4" align="right">
                    <table width="50%">
                       <tr>
                          <th colspan="4" class="titleHeaders" align="left" nowrap="true"><ae:GetResource name="selection_filter" /></th>
                       </tr>
                       <tr height="1">
                         <td height="1" colspan="4" class="gridLines"></td>
                       </tr>
                       <tr>
                         <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="partner_link" />&nbsp;</td>
                         <td align="left" colspan="3"><input type="text" name="partnerLinkTypeName" size="20" value='<jsp:getProperty name="recsBean" property="partnerLinkTypeName" />'/></td>
                       </tr>
                       <tr height="1">
                         <td colspan="4" height="1" class="tabular"></td>
                       </tr>
                       <tr>
                         <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="port_type" />&nbsp;</td>
                         <td align="left" colspan="3"><input type="text" name="portType" size="20" value='<jsp:getProperty name="recsBean" property="portType" />'/></td>
                       </tr>
                       <tr height="1">
                         <td colspan="4" height="1" class="tabular"></td>
                       </tr>
                       <tr>
                         <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="operation" />&nbsp;</td>
                         <td align="left" colspan="3"><input type="text" name="operation" size="20" value='<jsp:getProperty name="recsBean" property="operation" />'/></td>
                       </tr>
                       <tr height="1">
                         <td colspan="4" height="1" class="tabular"></td>
                       </tr>
                       <tr>
                         <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<ae:GetResource name="process_id_colon" />&nbsp;</td>
                         <td align="left" colspan="3"><input type="text" name="processId" size="20" value='<jsp:getProperty name="recsBean" property="processId" />'/></td>
                       </tr>
                       <tr height="1">
                         <td colspan="4" height="1" class="tabular"></td>
                       </tr>
                       <tr>
                         <td colspan="4" align="center"><input type="submit" value=<ae:GetResource name="submit" />>&nbsp;<input type="button" value=<ae:GetResource name="clear" /> onClick="clearAll()"/></td>
                       </tr>
                       <tr height="1">
                         <td colspan="4" height="1"></td>
                       </tr>
                       <tr height="1">
                         <td height="1" colspan="4" class="gridLines"></td>
                       </tr>
                       <tr>
                         <td colspan="4"></td>
                       </tr>
                    </table>
                  </td></tr>
                </table>
                <input type="hidden" name="isSubmit" value="true" />
                <input type="hidden" name="rowOffset" value="0" />
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

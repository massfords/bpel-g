<%@page contentType="text/html; charset=UTF-8" import="org.activebpel.rt.util.*,org.activebpel.rt.bpel.server.engine.*,org.activebpel.rt.bpel.server.admin.*,javax.xml.namespace.QName,java.text.*,org.activebpel.rt.bpel.*"  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

   <%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

   <%-- Use UTF-8 to decode request parameters. --%>
   <ae:RequestEncoding value="UTF-8" />

   <jsp:useBean id="pdefBean" class="org.activebpel.rt.bpeladmin.war.web.AePartnerDetailBean" >
      <jsp:setProperty name="pdefBean" property="principal" param="principal" />
   </jsp:useBean>
   
   <jsp:include page="header_head.jsp" />

   <script>

      var detailWindow = null;

      function openDetailWindow( plLocalPart, plNamespace )
      {
         closeDetailWindow();

         var xMax = 640;
         var yMax = 480;

         if (document.all)
         {
             xMax = screen.width;
             yMax = screen.height;
         }
         else if (document.layers)
         {
            xMax = window.outerWidth
            yMax = window.outerHeight;
         }

         var xOffset = (xMax - 500)/2;
         var yOffset = (yMax - 400)/2;

         detailWindow = window.open('','PDefDetails','resizable=yes,scrollbars=yes,width=500,height=350,screenX='+xOffset+',screenY='+yOffset+',top='+yOffset+',left='+xOffset);
         var doc = detailWindow.document;
         doc.write('<html>\n');
         doc.write('<head><title><ae:GetResource name="partner_def_details" /></title><STYLE TYPE="text/css">@import url(css/ae.css);</STYLE></head>\n');
         doc.write('<body onLoad="document.popupForm.submit()">\n' );

         doc.write('<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%">\n');
         doc.write('<tr height="100%">\n');
         doc.write('  <td valign="center" align="center"><b><ae:GetResource name="loading" /></b></td>\n');
         doc.write('</tr>\n');
         doc.write('<form id="popupForm" name="popupForm" method="post" action="partner_details_popup.jsp">\n');
         doc.write('<input type="hidden" name="principal" value="<jsp:getProperty name="pdefBean" property="principal" />" />\n');
         doc.write('<input type="hidden" name="ns" value="' + plNamespace + '" />\n');
         doc.write('<input type="hidden" name="lp" value="' + plLocalPart + '" />\n');
         doc.write('</form>\n');

         doc.write('</body></html>\n' );
         doc.close();

         detailWindow.focus();
      }
      
      function closeDetailWindow()
      {
         if( detailWindow != null )
         {
            detailWindow.close();
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
               <table border="0" cellpadding="0" cellspacing="0" width="100%" align="left">
                  <tr>
                     <th colspan="4" class="pageHeaders" align="left" nowrap="true"><ae:GetResource name="partner_definition_detail_for" />&nbsp;<jsp:getProperty name="pdefBean" property="principal" /></th>
                  </tr>
                  <tr height="1">
                    <td height="1" colspan="4" class="gridLines"></td>
                  </tr>
                  <tr>
                     <th class="columnHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="partner_link_type" />&nbsp;</th>
                     <th class="columnHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="role" />&nbsp;</th>
                  </tr>
                  <tr height="1">
                    <td height="1" colspan="4" class="gridLines"></td>
                  </tr>
                  
                  <ae:IndexedProperty name="pdefBean" id="pdefRow" property="detail" indexedClassName="org.activebpel.rt.bpeladmin.war.web.AePartnerDetailWrapper" >
                     <tr>
                        <td nowrap="true">&nbsp;<a href="javascript:openDetailWindow('<jsp:getProperty name="pdefRow" property="partnerLinkLocalPart" />', '<jsp:getProperty name="pdefRow" property="partnerLinkNamespace" />')"><jsp:getProperty name="pdefRow" property="partnerLinkLocalPart" /></a></td>
                        <td nowrap="true">&nbsp;<a href="javascript:openDetailWindow('<jsp:getProperty name="pdefRow" property="partnerLinkLocalPart" />', '<jsp:getProperty name="pdefRow" property="partnerLinkNamespace" />')"><jsp:getProperty name="pdefRow" property="role" /></a>&nbsp;</td>
                     </tr>
                     <tr height="1">
                       <td colspan="4" height="1" class="tabular"></td>
                     </tr>
                  </ae:IndexedProperty>
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

<%@page contentType="text/html; charset=UTF-8" import="org.activebpel.rt.util.*,org.activebpel.rt.bpel.server.engine.*,org.activebpel.rt.bpel.server.admin.*,javax.xml.namespace.QName,java.text.*,org.activebpel.rt.bpel.*"  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>

<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

<%-- Use UTF-8 to decode request parameters. --%>
<ae:RequestEncoding value="UTF-8" />

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
                  <th colspan="3" class="pageHeaders" align="left" nowrap="true"><ae:GetResource name="summary" /></th>
               </tr>
               <tr height="1">
                 <td height="1" colspan="3" class="gridLines"></td>
               </tr>
               <tr>
                 <td colspan="3"><ae:GetResource name="under_construction" /></td>
               </tr>

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

<%@page contentType="text/html; charset=UTF-8" import="org.activebpel.rt.util.*,org.activebpel.rt.bpel.server.engine.*,org.activebpel.rt.bpel.server.admin.*,javax.xml.namespace.QName,java.text.*,org.activebpel.rt.bpel.*"  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>

<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

   <%-- Use UTF-8 to decode request parameters. --%>
   <ae:RequestEncoding value="UTF-8" />

<jsp:useBean id="tabBean" scope="request" class="org.activebpel.rt.war.web.tabs.AeTabBean"/>

<ae:TabTag name="config" beanName="tabBean"/>
<jsp:setProperty name="tabBean" property="selectedTabString" param="tab" />

<%--
   Possible WebLogic 9.0 bug: can't seem to access request parameters after this
   include, so please examine all request parameters before this point.
--%>
<jsp:include page="header_head.jsp" />

<script type="text/javascript" language="JavaScript">
   function showTab(param)
   {
      window.location = "config.jsp?tab=" + param;
      return true;
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
            <div>
               <ul class="tabnav">
                  <ae:IndexedProperty name="tabBean" id="tab" property="tab" indexedClassName="org.activebpel.rt.war.web.tabs.AeTab" >
                     <ae:IfTrue name="tab" property="selected" >
                        <%-- "selected tabs get the seltab property on id and no hyperlink around their name" --%>
                        <li class="seltab"><span><jsp:getProperty name="tab" property="name" /></span></li>
                     </ae:IfTrue>
                     <ae:IfFalse name="tab" property="selected" >
                        <%-- "otherwise tab gets hyperlink around its name" --%>
                        <li><a href="javascript:showTab('<jsp:getProperty name="tab" property="offset" />')">&nbsp;<jsp:getProperty name="tab" property="name" />&nbsp;</a></li>
                     </ae:IfFalse>
                  </ae:IndexedProperty>
               </ul>
            </div>
            <div class="tabcontainer">
               <div style="margin:10px">
                  <jsp:include page="<%=tabBean.getSelectedPage()%>"/>
               </div>
            </div>
         </td>
   
         <!-- main and right margin -->
         <td width="3%"></td>
      </tr>
   </table>
   <br> 
   <jsp:include page="footer.jsp" />
</body>
</html>

<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>
<%-- Use UTF-8 to decode request parameters. --%>
<ae:RequestEncoding value="UTF-8" />
<%@ include file="incl_processview_log_declaration.jsp" %>
   <head>
      <%@ include file="incl_pagetitle.jsp" %>
      <link rel="shortcut icon" href="../images/favicon.ico" type="image/x-icon" />
      <link rel="STYLESHEET" type="text/css" href="../css/ae_processView.css">
      <link rel="STYLESHEET" type="text/css" href="../css/ae.css">
   </head>
   <body>
      <div id="header">
         <img  id="logo" src="../images/logo.gif">
         <div id="headertitle">
            <h1>
            <ae:IfTrue name="propertyBean" property="valid" >
               <ae:GetResource name="process_details_colon" /> &nbsp;
               <span><jsp:getProperty name="propertyBean" property="processName" />(ID <%= request.getParameter("pid") %>)</span>
            </ae:IfTrue>
            <ae:IfFalse name="propertyBean" property="valid" >
               <ae:GetResource name="process_details_colon" /> <%= request.getParameter("pid") %>
            </ae:IfFalse>
            </h1>
            <p>
               <a  href="../../BpelAdminHelp" target="aeAdminHelp"><ae:GetResource name="help" /></a>&nbsp;|&nbsp;
               <a  href="javascript:parent.close();"><ae:GetResource name="close" /></a>
            </p>
         </div>
      </div>
      <div id="propertiesdiv">
         <ae:IfTrue name="propertyBean" property="valid" >
	          <ae:IfTrue name="propertyBean" property="hasLogData" >
	             <p>
	                <a href="../<%-- ASP_Conversion_Start:Substitute getLog.aspx?--%>getLog?<%-- ASP_Conversion_Stop --%>pid=<%=request.getParameter("pid")%>"><ae:GetResource name="download_entire_log" /></a>
	             </p>
	          </ae:IfTrue>         
            <ae:IndexedProperty name="propertyBean" id="nvPair" property="details" indexedClassName="org.activebpel.rt.bpeladmin.war.web.processview.AePropertyNameValue" >
               <div class="propertydetails">
                  <h1 class="titleHeaders" ><jsp:getProperty name="nvPair" property="displayName" /></h1>
                  <textarea rows="<jsp:getProperty name="nvPair" property="rowCount" />" wrap="off" readonly="readonly"><ae:XMLStringFormatter name="nvPair" property="value" /></textarea>
               </div>
            </ae:IndexedProperty>
         </ae:IfTrue>
         <ae:IfFalse name="propertyBean" property="valid" >
                  <jsp:getProperty name="propertyBean" property="message" />
         </ae:IfFalse>
      </div>
      <!-- begin footer -->
      <div id="footer">
         <jsp:include page="../footer.jsp" />
      </div>
      <!-- end footer -->

   </body>
</html>

<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>
<%-- Use UTF-8 to decode request parameters. --%>
<ae:RequestEncoding value="UTF-8" />
<%@ include file="incl_processview_header_declaration.jsp" %>
   <head>
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
               <ae:GetResource name="active_process_colon" /> &nbsp;
               <span><ae:GetProperty name="propertyBean" property="processName" /> &nbsp; (ID <%= request.getParameter("pid") %>)</span>
            </ae:IfTrue>
            <ae:IfFalse name="propertyBean" property="valid" >
               <ae:GetResource name="active_process_colon" /> <%= request.getParameter("pid") %>
            </ae:IfFalse>
            </h1>
            <p>
               <a  href="processview_detail.jsp?pid=<%= request.getParameter("pid") %>" target="_top"><ae:GetResource name="refresh" /></a>&nbsp;|
               <a  href="../../BpelAdminHelp" target="aeAdminHelp"><ae:GetResource name="help" /></a>&nbsp;|&nbsp;
               <a  href="javascript:parent.close();"><ae:GetResource name="close" /></a>
            </p>
         </div>
      </div>
		   
   </body>
</html>

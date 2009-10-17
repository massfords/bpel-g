<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>
<%-- Use UTF-8 to decode request parameters. --%>
<ae:RequestEncoding value="UTF-8" />
<%@ include file="incl_processview_detail_declaration.jsp" %>
   <head>
      <%@ include file="incl_pagetitle.jsp" %>
      <link rel="shortcut icon" href="../favicon.ico" type="image/x-icon" />
      <link rel="STYLESHEET" type="text/css" href="../css/ae_processView.css">
   </head>
   <frameset rows="50px,*,30px" frameborder="yes" framespacing="1" border="1">
      <frame src="processview_header.jsp?<jsp:getProperty name="processViewBean" property="pidParamName" />=<jsp:getProperty name="processViewBean" property="pidParamValue" />" name="pvheader" id="pvheader" scrolling="no">
      <frameset cols="25%,*" frameborder="yes" framespacing="2" border="2">
         <frame src="processview_outline.jsp?<jsp:getProperty name="processViewBean" property="pidParamName" />=<jsp:getProperty name="processViewBean" property="pidParamValue" />" name="pvoutline" id="pvoutline" />
         <ae:IfTrue name="processViewBean" property="graphingEnabled" >
            <frameset rows="70%,*" frameborder="yes" framespacing="2" border="2">
               <frame src="processview_graph.jsp?<jsp:getProperty name="processViewBean" property="pidParamName" />=<jsp:getProperty name="processViewBean" property="pidParamValue" />&part=0&t=<%= System.currentTimeMillis() %>" name="pvgraph" id="pvgraph" />
               <frame src="processview_properties.jsp?<jsp:getProperty name="processViewBean" property="pidParamName" />=<jsp:getProperty name="processViewBean" property="pidParamValue" />" name="pvproperties" id="pvproperties" />
            </frameset>
         </ae:IfTrue>
         <ae:IfFalse name="processViewBean" property="graphingEnabled" >
            <frame src="processview_properties.jsp?<jsp:getProperty name="processViewBean" property="pidParamName" />=<jsp:getProperty name="processViewBean" property="pidParamValue" />" name="pvproperties" id="pvproperties" />
         </ae:IfFalse>
      </frameset>
      <frame src="processview_footer.jsp" name="pvfooter" id="pvfooter" scrolling="no">
      <noframes>
         This view requires your browser support HTML Frames.
      </noframes>
   </frameset>
</html>

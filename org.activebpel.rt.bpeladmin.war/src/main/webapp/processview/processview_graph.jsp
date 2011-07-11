<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

<%-- Use UTF-8 to decode request parameters. --%>
<ae:RequestEncoding value="UTF-8" />
<%@ include file="incl_processview_graph_declaration.jsp" %>
   <head>
      <%@ include file="incl_pagetitle.jsp" %>
      <link rel="shortcut icon" href="../images/favicon.ico" type="image/x-icon" />
      <!-- BEGIN STYLE SHEET IMPORT -->
      <link rel="stylesheet" type="text/css" href="../css/ae.css" />
      <link rel="stylesheet" type="text/css" href="../css/ae_graphView.css" />
      <!-- END STYLE SHEET IMPORTS -->
      <script type="text/javascript" language="JavaScript" src="../script/ae_graphview.js"></script>

      <script type="text/javascript" language="JavaScript">
         // the location path of the most recent 'mouse over' activity.
         lastPath = "";
         // current active or deployed process id
         currId = "<ae:GetProperty name="graphBean" property="pidParamValue" />";
         // part or tab
         currPart = "<%= request.getParameter("part") %>";
         // activity location xpath
         currPath = "<%= request.getParameter("path") %>";
			// pivot path
			currPivotPath = "<%= request.getParameter("pivot") %>";
			// init path - passed via pathid.
			initialPath = "<ae:GetProperty name="graphBean" property="path" />";

         tileWidth = <ae:GetProperty name="graphBean" property="tileWidth" />;
         tileHeight = <ae:GetProperty name="graphBean" property="tileHeight" />;
         imageWidth = <ae:GetProperty name="graphBean" property="width" />;
         imageHeight = <ae:GetProperty name="graphBean" property="height" />;
         nCols = 0;
         nRows = 0;
         processImageUriBase = "<%-- ASP_Conversion_Start:Substitute graphimage.aspx?--%>graphimage?<%-- ASP_Conversion_Stop --%><ae:GetProperty name="graphBean" property="pidParamName" />=<ae:GetProperty name="graphBean" property="pidParamValue" />&part=<ae:GetProperty name="graphBean" property="partId" />";
         processImageSid ="<%= System.currentTimeMillis() %>";
         processImagePivot = "<ae:GetProperty name="graphBean" property="pivotPath" />";
         reloadUriBase = "processview_graph.jsp?<ae:GetProperty name="graphBean" property="pidParamName" />=" + currId;
         propertiesUriBase = "processview_properties.jsp?<ae:GetProperty name="graphBean" property="pidParamName" />=<ae:GetProperty name="graphBean" property="pidParamValue" />";

         function initGraphview()
         {
            layoutImages();
         }
      </script>
   </head>
   <body OnLoad="initGraphview();">
		<%@ include file="incl_processview_graph_body.jsp" %>   
   </body>
</html>

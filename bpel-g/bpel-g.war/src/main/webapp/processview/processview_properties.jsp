<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

<jsp:useBean id="addAttachmentBean" class="org.activebpel.rt.bpeladmin.war.web.processview.attachment.AeAddAttachmentBean">
     <jsp:setProperty name="addAttachmentBean" property="pid" param="pid" />
	  <jsp:setProperty name="addAttachmentBean" property="path" param="path" />
</jsp:useBean>
<jsp:useBean id="deleteAttachmentBean" class="org.activebpel.rt.bpeladmin.war.web.processview.attachment.AeDeleteAttachmentBean" />
<ae:IfParamMatches property="isDeleteAttachment" value="true">
       <!-- Delete remote attachment -->
       <jsp:setProperty name="deleteAttachmentBean" property="pid" param="pid" />
	    <jsp:setProperty name="deleteAttachmentBean" property="path" param="path" />
	    <jsp:setProperty name="deleteAttachmentBean" property="deleteItem" param="itemNumber" />
 </ae:IfParamMatches>
 
<%-- Use UTF-8 to decode request parameters. --%>
<ae:RequestEncoding value="UTF-8" />
<%@ include file="incl_processview_properties_declaration.jsp" %>
   <head>
		<%@ include file="incl_pagetitle.jsp" %>
      <link rel="shortcut icon" href="../images/favicon.ico" type="image/x-icon" />
      <style type="text/css">
         @import url(../css/ae.css);
      </style>
      <style type="text/css">
         @import url(../css/ae_processView.css);
      </style>

      <script type="text/javascript" language="JavaScript">
         function notifyOutline()
         {
            var outlineWindow = parent.pvoutline;
            if (outlineWindow)
            {
               var xPath = "<%=request.getParameter("path")%>";
               if (xPath && xPath != "null")
               {
                  outlineWindow.onPropertyViewChange(xPath);
               }
            }
         }
         
         function showPath(xpath)
         {
            if (xpath)
            {
               var graphWindow = parent.pvgraph;
               if (graphWindow && graphWindow.onOutlineSelect)
               {
                  graphWindow.onOutlineSelect(xpath);
               }            
               url = "processview_properties.jsp?<jsp:getProperty name="propertyBean" property="pidParamName" />=<jsp:getProperty name="propertyBean" property="pidParamValue" />&path=" + encodeURI(xpath);
               window.location = url;
            }
         }         
      </script>
   </head>
   <body onLoad="notifyOutline();">
      <div id="propertiesdiv">
         <ae:IfTrue name="propertyBean" property="valid" >

            <!-- display object name, icon and xpath -->
            <div id="propTitleSection">
               <h1 class="titleHeaders" >
                  <img id="bpelIcon" src="<jsp:getProperty name="propertyBean" property="bpelImagePath" />"> <jsp:getProperty name="propertyBean" property="tagName" />
               </h1>
            </div>
            <table border="0" cellpadding="0" cellspacing="0" width="100%" >
               <ae:IfFalse name="propertyBean" property="hasProperties" >
                  <!-- top border border line when propery table is not shown  -->
                  <tr height="1">
                     <td style="padding:0;margin:0" height="1" colspan="2" class="gridLines"></td>
                  </tr>
               </ae:IfFalse>
               <!-- begin top level property details -->
               <ae:IfTrue name="propertyBean" property="hasProperties" >
                  <tr>
                    <th class="columnHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="property" /> &nbsp;</th>
                    <th class="columnHeaders" align="left" nowrap="true" >&nbsp;<ae:GetResource name="value" /> &nbsp;</th>
                  </tr>
                  <ae:IndexedProperty name="propertyBean" id="nvPair" property="properties" indexedClassName="org.activebpel.rt.bpeladmin.war.web.processview.AePropertyNameValue" >
                     <tr>
                       <td class="labelHeaders" align="left" nowrap="true" width="20%">&nbsp;<jsp:getProperty name="nvPair" property="displayName" />&nbsp;</td>
                       <td align="left">&nbsp;
                           <ae:IfTrue name="nvPair" property="dateValue" >
                           <ae:DateFormatter name="nvPair" property="date" patternKey="date_time_pattern2" />
                           </ae:IfTrue>
                            <ae:IfFalse name="nvPair" property="dateValue" >
                              <ae:IfTrue name="nvPair" property="hasLocationPath" >
                                <a href="#" onClick="showPath('<jsp:getProperty name="nvPair" property="escapedLocationPath" />')"><span class="pathlink"><jsp:getProperty name="nvPair" property="value" /></span></a>
                              </ae:IfTrue>
                              <ae:IfFalse name="nvPair" property="hasLocationPath" >
                                <jsp:getProperty name="nvPair" property="value" />
                              </ae:IfFalse>
                           </ae:IfFalse>
                       </td>
                     </tr>
                     <tr height="1">
                        <td colspan="2" height="1" class="tabular"></td>
                     </tr>
                  </ae:IndexedProperty>
                  <!-- bottom border on table -->
                  <tr height="1">
                     <td style="padding:0;margin:0" height="1" colspan="2" class="gridLines"></td>
                  </tr>
               <!-- end top level property details -->
               </ae:IfTrue>
            </table>

            <!-- begin additional property details -->
            <ae:IndexedProperty name="propertyBean" id="nvPair" property="details" indexedClassName="org.activebpel.rt.bpeladmin.war.web.processview.AePropertyNameValue" >
            <div class="propertydetails">
               <h1 class="titleHeaders" ><jsp:getProperty name="nvPair" property="displayName" /></h1>
               <textarea rows="<jsp:getProperty name="nvPair" property="rowCount" />" wrap="off" readonly="readonly"><ae:XMLStringFormatter name="nvPair" property="value" /></textarea>
               <!-- begin attachments -->
               <ae:IfPropertyMatches name="propertyBean" property="tagName" value="Variable" classType="java.lang.String">
                  <%@ include file="processview_attachments.jsp" %>
               </ae:IfPropertyMatches>
               <!-- end attachments -->
            </div>
            
            </ae:IndexedProperty>
            <!-- end additional property details -->
         </ae:IfTrue>
         <ae:IfFalse name="propertyBean" property="valid" >
            <jsp:getProperty name="propertyBean" property="message" />
         </ae:IfFalse>
      </div>
   </body>
</html>

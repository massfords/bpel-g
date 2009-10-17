<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
	<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>
	<%-- Use UTF-8 to decode request parameters. --%>
	
  <ae:RequestEncoding value="UTF-8" />
  <jsp:useBean id="addAttachmentBean" class="org.activebpel.rt.bpeladmin.war.web.processview.attachment.AeAddAttachmentBean"/>
  
  <ae:IfParamMatches property="isAddAttachment" value="true">
       <!-- Delete remote attachment -->
       <% addAttachmentBean.setResponse(response); %>
       <% addAttachmentBean.setRequest(request); %>
  </ae:IfParamMatches>
 
  <%@ include file="incl_processview_properties_declaration.jsp" %>
  
  <head>
      <title><ae:GetResource name="page_title_standard" /> - <ae:GetResource name="activity_property_edit" /></title>
      <link rel="shortcut icon" href="../images/favicon.ico" type="image/x-icon" />
      <style type="text/css">
		      @import url(../css/ae.css);
		</style>
		<style type="text/css">
		      @import url(../css/ae_processView.css);
      </style>
      <style type="text/css">
				@import url(../css/ae_attachment.css);
		</style>
		<%@ include file="incl_processview_attachment_update.jsp" %>

		<script type="text/javascript" language="JavaScript">
	      var mPid  = "<jsp:getProperty name='propertyBean' property='pidParamValue' />";
	      var mPath = "<jsp:getProperty name='propertyBean' property='path' />";
	      <ae:IfParamMatches property="isAddAttachment" value="true">
		      var mPid  = "<jsp:getProperty name='addAttachmentBean' property='pid' />";
		      var mPath = "<jsp:getProperty name='addAttachmentBean' property='path' />";
	       </ae:IfParamMatches>
	      var mQs   = "<jsp:getProperty name='propertyBean' property='pidParamName' />=" + mPid + "&path=" + encodeURI(mPath);
	      var mBeanStatus = "<jsp:getProperty name='addAttachmentBean' property='statusCode' />";
	      var mBeanStatusDetail = "<jsp:getProperty name='addAttachmentBean' property='statusDetail' />";
	      var mInvalidFileMsg = "<ae:GetResource name='attachment_invalid_file_location' />"
	      var mAttributeConfirmDelete = "<ae:GetResource name='attachment_attribute_confirm_delete' />"
		</script>
	</head>
	<body>
		<div id="attachmentDialog" >
		   <!-- display object name, icon and xpath -->
			<div id="propTitleSection" style="border-bottom:1px solid #000;">
			   <h1 class="titleHeaders" >
			   	<img id="bpelIcon" src="<jsp:getProperty name="propertyBean" property="bpelImagePath" />"> <ae:GetResource name="attachment_add_caption" />
			   </h1>
			   <br/>
			   <span id="titleLocation"></span>
			</div>
	      <div class="propertyedit center" >
				<fieldset class="legend">
					<legend><ae:GetResource name="edit" /> <ae:GetResource name="property" /></legend>    
					<form name="editproperty" id="editPropertyForm">
						<table border="0" cellspacing="1" cellpadding="1" width="96%" align="center">
							<tr>
								<td width="40px"><ae:GetResource name="property_colon" /> </td>
								<td><input id="editName" size="45" type="text" name="property"/></td>
						   </tr>
							<tr>
							    <td width="40px"><ae:GetResource name="value_colon" /> </td>
							    <td><input id="editValue" size="45" type="text" name="value"/></td>
							    <td rowspan="2">
									<div id="propertyeditdialog" style="text-align:right;margin: 5px;">
										<button id="acceptEditPropertyBtn" value="ok" /><ae:GetResource name="ok" /></button>
										<button id="cancelEditPropertyBtn" value="cancel"/><ae:GetResource name="cancel" /></button>
									</div>
								</td>
							</tr>
						</table>  
					</form>
				</fieldset>
        </div>
			<table class="propertyview" border="0" cellspacing="1" cellpadding="1" width="95%">
	         <tr class="locationview">
		         <td>
						<div class="locations">
							<fieldset class="legend" id="ajaxAccept">
								 <legend><ae:GetResource name="location" /></legend>
								 <form id="attachmentAcceptForm" action="processview_attachments_add.jsp?isAddAttachment=true" method="POST" enctype="multipart/form-data">
								     <input id="processid" name="pid" type="hidden" />
   								  <input id="variablexpath" name="path" type="hidden" />
   								  <input id="filepath" name="filePath" type="hidden"/>
								     <input id="xmloutput" name="attributeXml" type="hidden"/>
								     <input id="attachmentFilePath" type="file"  class="filebrowse" size="90" name="filepath" />
								 </form>
					      </fieldset>
						</div>
					</td>
	         </tr>
			   <tr>
				   <td>
					      <br>
					      <fieldset class="legend">
								 <legend><ae:GetResource name="properties" /></legend>
								 <div class="properties" >
								   <table border="0" cellspacing="0">
								   <tr>
								   <td>
									<table border="0" cellspacing="0" class="properties">
										 <tr>
											 <th class="columnHeaders" align="left" nowrap="true">&nbsp;<ae:GetResource name="property" />&nbsp;</th>
											 <th width="65%" class="columnHeaders" align="left" nowrap="true" >&nbsp;<ae:GetResource name="value" />&nbsp;</th>
											 <th width="2%"class="columnHeaders">&nbsp;&nbsp;</th>
										 </tr>
										 <!-- Properties will be inserted here -->
										 <tr class="addabove" style="display:none;"/>
									</table>
									</td>
									<td>
								   
								   <div class="propertyButtons" >
										 <button class="vertical" id="addPropertyBtn" name="padd" value="add"/><ae:GetResource name="add_ellipsis" /></button>
										 <button class="vertical" id="editPropertyBtn" name="pedit" value="edit" disabled/><ae:GetResource name="edit" /></button>
										 <button class="vertical" id="deletePropertyBtn" name="pdelete" value="delete" disabled/><ae:GetResource name="delete" /></button>
								   </div>
								   </td>
								   </tr>
								   </table>
					           </div>
					      </fieldset>
				   </td>
			   </tr>
			</table>

        <table border="0" cellspacing="1" cellpadding="1" width="95%">
			   <tr>
				   <td>
				     <div id="busywait" >
                     <img src="../images/attachments/busywait.gif" style="display:inline;"/>&nbsp;<span style="font-size:0.8em"><ae:GetResource name="uploading_attachment" /></span>
                 </div>
				     <div class="aeAcceptButtons">
						<button id="addAttachmentBtn" value="ok"><ae:GetResource name="ok" /></button>
						<button id="cancelAttachmentBtn" value="cancel"><ae:GetResource name="close" /></button>
					  </div>
					</td>
				</tr>
		  </table>
		 
		  <div class="footer">
	         <div>
	            <table border="0" cellpadding="0" cellspacing="0" width="100%" align="center">
	               <tr>
	                  <td align="center" class="iconText"><ae:GetResource name="copyright" /></td>
	               </tr>
	            </table>
	         </div>
      	</div>
		</div>
	</body>
</html>
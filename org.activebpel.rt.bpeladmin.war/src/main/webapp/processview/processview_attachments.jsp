<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

<!-- begin attachments -->
<script type="text/javascript" language="JavaScript">
	 var addAttachmentWindow = null;

    // aPid - the process id
    // aLocationPath - the location path used on the server to identify the node to update
	 var aPid = <%= request.getParameter("pid") %>;
	 var aLocationPath = "<%= request.getParameter("path") %>";

	 function openAddAttachmentWindow( )
	 {
		 closeAddAttachmentWindow();
		 url = "processview_attachments_add.jsp?pid=" + aPid;
		 url = url + "&path=" + encodeURI(aLocationPath);
		 location = url;

		 var xMax = 1000;
		 var yMax = 1000;

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

		 addAttachmentWindow = window.open(url,'InputAttachment','resizable,scrollbars=no,width=610,height=475,screenX='+xOffset+',screenY='+yOffset+',top='+yOffset+',left='+xOffset);
		 addAttachmentWindow.focus();
	 }

	 function closeAddAttachmentWindow()
	 {
		 if( addAttachmentWindow != null )
		 {
			 addAttachmentWindow.close();
		 }
	 }
	 
	 // Remove attachment from the variable on remote engine
	 function deleteAttachment(aItemNumber)
	 {
	    var answer = confirm("<ae:GetResource name="attachment_confirm_delete" />" + " [#" + aItemNumber + "] ?");
	    if(answer)
	    {
		    var url = "processview_properties.jsp?pid=" + aPid;
			 url = url + "&path=" + encodeURI(aLocationPath);
			 url = url + "&isDeleteAttachment=true";
			 url = url + "&itemNumber=" + aItemNumber;
			 window.location = url;
		 }
	 }

</script>

 <!-- begin attachment view -->
 <form name="attachmentViewForm" method="POST" action="processview_properties.jsp">
   <input name="pid" type="hidden" value="<%= request.getParameter("pid") %>" />
   <input name="path" type="hidden" value="<%= request.getParameter("path") %>" />
   <div class="propertydetails">
      <ae:IfTrue name="addAttachmentBean" property="editable">
         <h1 class="titleHeaders" ><ae:GetResource name="attachments" />
             &nbsp;
		       <button onclick="openAddAttachmentWindow();" ><ae:GetResource name="add_ellipsis" /></button>
         </h1>
      </ae:IfTrue>
      <ae:IfFalse name="addAttachmentBean" property="editable">
         <ae:IfTrue name="propertyBean" property="hasAttachments" >
             <h1 class="titleHeaders" ><ae:GetResource name="attachments" /></h1>
         </ae:IfTrue>
      </ae:IfFalse>

      <table border="0" cellpadding="1" cellspacing="0" width="100%" >
         <ae:IndexedProperty name="propertyBean" id="attachmentView" property="attachments" indexedClassName="org.activebpel.rt.bpeladmin.war.web.processview.AeAttachmentViewBean" >
            <tr>
               <td><ae:GetResource name="attachment_num" />&nbsp;&nbsp;<ae:GetProperty name="attachmentView" property="index" /></td>
                <td><a id="linkbutton" href="../<%-- ASP_Conversion_Start:Substitute getAttachment.aspx?--%>getAttachment?<%-- ASP_Conversion_Stop --%>id=<ae:GetProperty name="attachmentView" property="attachmentId" />&type=<ae:GetProperty name="attachmentView" property="mimeTypeEncoded" />&file=<ae:GetProperty name="attachmentView" property="fileName" />"><ae:GetResource name="attachment_download_selected" /></a> 
					     <ae:IfTrue name="addAttachmentBean" property="editable">
					       &nbsp;
					       <button onclick="deleteAttachment(<ae:GetProperty name="attachmentView" property="index" />);" ><ae:GetResource name="delete" /></button>
					     </ae:IfTrue>
               </td>
               <td>&nbsp;</td>
            </tr>

            <ae:CollectionIterator name="attachmentView" property="headers" id="attachmentHeader" >
	            <tr>
                  <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
 		             <td><ae:GetProperty name="attachmentHeader" property="name" /></td>
	                <td><ae:GetProperty name="attachmentHeader" property="value" /></td>
	            </tr>
	         </ae:CollectionIterator>

            <tr height="1"><td colspan="3" height="1" class="tabular"></td></tr>
         </ae:IndexedProperty>
      </table>
   </div>
   
  </form>
  <!-- end  attachment view -->

<!-- end attachments -->

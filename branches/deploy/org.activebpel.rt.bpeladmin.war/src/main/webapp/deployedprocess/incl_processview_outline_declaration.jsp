<jsp:useBean id="treeViewBean" class="org.activebpel.rt.bpeladmin.war.web.processview.AeProcessViewBean" >
   <jsp:setProperty name="treeViewBean" property="pidParamName" value="pdid" />
   <jsp:setProperty name="treeViewBean" property="pidParamValue" param="pdid" />
   <jsp:setProperty name="treeViewBean" property="imagePath"value="../images/processview" />
   <jsp:setProperty name="treeViewBean" property="processDeploymentIdString" param="pdid" />
</jsp:useBean>

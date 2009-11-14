<jsp:useBean id="propertyBean" class="org.activebpel.rt.bpeladmin.war.web.processview.AeProcessViewBean" >
   <jsp:setProperty name="propertyBean" property="pidParamName" value="pdid" />
   <jsp:setProperty name="propertyBean" property="pidParamValue" param="pdid" />
   <jsp:setProperty name="propertyBean" property="imagePath"value="../images/processview" />
   <jsp:setProperty name="propertyBean" property="processDeploymentIdString" param="pdid" />
</jsp:useBean>

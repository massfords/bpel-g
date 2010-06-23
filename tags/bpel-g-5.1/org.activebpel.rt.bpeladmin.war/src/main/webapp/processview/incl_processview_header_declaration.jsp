<jsp:useBean id="propertyBean" class="org.activebpel.rt.bpeladmin.war.web.processview.AeProcessViewBean" >
   <jsp:setProperty name="propertyBean" property="pidParamName" value="pid" />
   <jsp:setProperty name="propertyBean" property="pidParamValue" param="pid" />
   <jsp:setProperty name="propertyBean" property="imagePath"value="../images/processview" />
   <jsp:setProperty name="propertyBean" property="processIdString" param="pid" />
</jsp:useBean>

<jsp:useBean id="graphBean" class="org.activebpel.rt.bpeladmin.war.web.processview.AeProcessViewBean" >
   <jsp:setProperty name="graphBean" property="pidParamName" value="pdid" />
   <jsp:setProperty name="graphBean" property="pidParamValue" param="pdid" />
   <jsp:setProperty name="graphBean" property="processDeploymentIdString" param="pdid" />
   <jsp:setProperty name="graphBean" property="partIdString" param="part" />
</jsp:useBean>
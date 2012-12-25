<jsp:useBean id="graphBean" class="org.activebpel.rt.bpeladmin.war.web.processview.AeProcessViewBean" >
   <jsp:setProperty name="graphBean" property="pivotPath" param="pivot" />
   <jsp:setProperty name="graphBean" property="pidParamName" value="pid" />
   <jsp:setProperty name="graphBean" property="pidParamValue" param="pid" />
   <jsp:setProperty name="graphBean" property="processIdString" param="pid" />
   <jsp:setProperty name="graphBean" property="partIdString" param="part" />
   <jsp:setProperty name="graphBean" property="pathIdString" param="pathid" />
</jsp:useBean>
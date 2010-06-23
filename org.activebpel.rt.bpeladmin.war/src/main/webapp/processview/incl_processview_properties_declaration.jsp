<jsp:useBean id="propertyBean" class="org.activebpel.rt.bpeladmin.war.web.processview.AeProcessViewBean" >
   <jsp:setProperty name="propertyBean" property="pivotPath" param="path" />
   <jsp:setProperty name="propertyBean" property="pidParamName" value="pid" />
   <jsp:setProperty name="propertyBean" property="pidParamValue" param="pid" />
   <jsp:setProperty name="propertyBean" property="message" value="Select activity from the outline on the left side of this window." />
   <jsp:setProperty name="propertyBean" property="imagePath"value="../images/processview" />
   <jsp:setProperty name="propertyBean" property="processIdString" param="pid" />
   <jsp:setProperty name="propertyBean" property="path" param="path" />
</jsp:useBean>

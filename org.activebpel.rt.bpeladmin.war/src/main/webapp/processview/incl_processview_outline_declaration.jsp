<jsp:useBean id="treeViewBean" class="org.activebpel.rt.bpeladmin.war.web.processview.AeProcessViewBean" >
   <ae:IfParamMatches property="ProcessAction" value="Terminate">
      <jsp:setProperty name="treeViewBean" property="terminate" value="true" />
   </ae:IfParamMatches>
   <ae:IfParamMatches property="ProcessAction" value="Suspend">
      <jsp:setProperty name="treeViewBean" property="suspend" value="true" />
   </ae:IfParamMatches>
   <ae:IfParamMatches property="ProcessAction" value="Resume">
      <jsp:setProperty name="treeViewBean" property="resume" value="true" />
   </ae:IfParamMatches>
   <ae:IfParamMatches property="ProcessAction" value="Restart">
      <jsp:setProperty name="treeViewBean" property="restart" value="true" />
   </ae:IfParamMatches>
   <jsp:setProperty name="treeViewBean" property="singleInstanceString" value="false" />
   <jsp:setProperty name="treeViewBean" property="pidParamName" value="pid" />
   <jsp:setProperty name="treeViewBean" property="pidParamValue" param="pid" />
   <jsp:setProperty name="treeViewBean" property="imagePath"value="../images/processview" />
   <jsp:setProperty name="treeViewBean" property="processIdString" param="pid" />
   <jsp:setProperty name="treeViewBean" property="pathIdString" param="pathid" />
</jsp:useBean>

<%@ taglib uri="http://activebpel.org/aetaglib" prefix="ae" %>

<jsp:useBean id="configBean" class="org.activebpel.rt.bpeladmin.war.web.AeEngineConfigBean" />
<jsp:useBean id="tabBean" scope="request" class="org.activebpel.rt.war.web.tabs.AeTabBean" />
<jsp:useBean id="detailBean" scope="request" class="org.activebpel.rt.bpeladmin.war.web.AeProcessDeploymentSelectorBean" />
<textarea  style="width:100%; height=100%" rows="20" wrap="off" readonly="readonly"><ae:GetProperty name="detailBean" property="sourceXml" /></textarea>

<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">

	<xsl:output method="xml" encoding="UTF-8" indent="yes" />

	<xsl:variable name="bpeladmin"
		select="document('../../../org.activebpel.rt.bpeladmin.war/src/main/webapp/WEB-INF/web.xml')" />
	<xsl:variable name="axisbpel"
		select="document('../../../org.activebpel.rt.axis.bpel.web/src/main/webapp/WEB-INF/web.xml')" />


	<xsl:template match="/">
		<web-app>
		    <xsl:copy-of select="/web-app/display-name"/>

            <xsl:copy-of select="/web-app/description"/>

	        <xsl:copy-of select="$bpeladmin//context-param" />

	        <xsl:copy-of select="$bpeladmin//listener" />

            <xsl:copy-of select="/web-app/listener" />

	        <xsl:copy-of select="$bpeladmin//servlet"/>
	        <xsl:copy-of select="$axisbpel//servlet"/>
            <xsl:copy-of select="/web-app/servlet"/>

	        <xsl:copy-of select="$bpeladmin//servlet-mapping"/>
	        <xsl:copy-of select="$axisbpel//servlet-mapping"/>
	        <xsl:copy-of select="/web-app/servlet-mapping" />

	        <xsl:copy-of select="$axisbpel//session-config"/>

	        <xsl:copy-of select="$bpeladmin//mime-mapping"/>
	        <xsl:copy-of select="$axisbpel//mime-mapping"/>

	        <xsl:copy-of select="$bpeladmin//welcome-file-list"/>

	        <xsl:copy-of select="$bpeladmin//error-page"/>

	        <xsl:copy-of select="$bpeladmin//taglib"/>

	        <xsl:copy-of select="$axisbpel//security-constraint"/>

	        <xsl:copy-of select="$axisbpel//login-config"/>

            <xsl:copy-of select="/web-app/resource-ref" />
		</web-app>
	</xsl:template>
</xsl:stylesheet>

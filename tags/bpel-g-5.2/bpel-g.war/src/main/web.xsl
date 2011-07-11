<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:javaee="http://java.sun.com/xml/ns/javaee"
	version="1.0">

	<xsl:output method="xml" encoding="UTF-8" indent="yes" />

	<xsl:param name="includeLog4JListener">true</xsl:param>

	<xsl:variable name="bpeladmin"
		select="document('../../../org.activebpel.rt.bpeladmin.war/src/main/webapp/WEB-INF/web.xml')" />
	<xsl:variable name="axisbpel"
		select="document('../../../org.activebpel.rt.axis.bpel.web/src/main/webapp/WEB-INF/web.xml')" />


	<xsl:template match="/">
		<web-app version="2.5" xmlns="http://java.sun.com/xml/ns/javaee"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd">
			
		    <xsl:copy-of select="/javaee:web-app/javaee:display-name"/>

            <xsl:copy-of select="/javaee:web-app/javaee:description"/>

			<!-- skip over jmx context-params -->
			<xsl:copy-of select="$bpeladmin//context-param[not(starts-with(param-name, 'jmx.'))]" />
			<xsl:copy-of select="$axisbpel//context-param"/>
			
			<xsl:copy-of select="/javaee:web-app/javaee:context-param" />
			<xsl:copy-of select="/javaee:web-app/javaee:listener" />
			<xsl:choose>
		    	<xsl:when test="$includeLog4JListener = 'false'">
		      		<xsl:copy-of select="$axisbpel//listener[not(contains(listener-class, 'org.springframework.web.util.Log4jConfigListener'))]"/>
		    	</xsl:when>
		    	<xsl:otherwise>
		      		<xsl:copy-of select="$axisbpel//listener"/>
		    	</xsl:otherwise>
		  	</xsl:choose>
			
	        <xsl:copy-of select="$bpeladmin//listener" />

	        <xsl:copy-of select="$bpeladmin//servlet"/>
	        <xsl:copy-of select="$axisbpel//servlet"/>
            <xsl:copy-of select="/javaee:web-app/javaee:servlet"/>

	        <xsl:copy-of select="$bpeladmin//servlet-mapping"/>
	        <xsl:copy-of select="$axisbpel//servlet-mapping"/>
	        <xsl:copy-of select="/javaee:web-app/javaee:servlet-mapping" />

	        <xsl:copy-of select="$axisbpel//session-config"/>

	        <xsl:copy-of select="$bpeladmin//mime-mapping"/>
	        <xsl:copy-of select="$axisbpel//mime-mapping"/>

	        <xsl:copy-of select="$bpeladmin//welcome-file-list"/>

	        <xsl:copy-of select="$bpeladmin//error-page"/>

	        <xsl:copy-of select="$bpeladmin//jsp-config"/>

	        <xsl:copy-of select="$axisbpel//security-constraint"/>

			<xsl:copy-of select="/javaee:web-app/javaee:resource-ref" />

			<xsl:copy-of select="$axisbpel//login-config"/>
		</web-app>
	</xsl:template>
</xsl:stylesheet>

<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:pdd="http://schemas.active-endpoints.com/pdd/2006/08/pdd.xsd"
    xmlns:wsa="http://schemas.xmlsoap.org/ws/2003/03/addressing"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs"
    version="2.0">
    
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="pdd:partnerLink[@name='RiskAssessment']/pdd:partnerRole">
        <wsa:EndpointReference>
            <wsa:Address>camel:direct:riskAssessment</wsa:Address>        
        </wsa:EndpointReference>
    </xsl:template>
    
</xsl:stylesheet>

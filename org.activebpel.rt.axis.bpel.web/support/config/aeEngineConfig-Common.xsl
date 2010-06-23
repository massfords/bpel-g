<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
   xmlns:xalan="http://xml.apache.org/xslt">

   <xsl:output method="xml" encoding="UTF-8" indent="yes" xalan:indent-amount="3"/>

   <!-- Default entry template - simply outputs the entry as-is. -->
   <xsl:template match="entry">
      <entry>
         <xsl:attribute name="name">
            <xsl:value-of select="@name"/>
         </xsl:attribute>
         <xsl:choose>
            <xsl:when test="@value">
               <xsl:attribute name="value">
                  <xsl:value-of select="@value"/>
               </xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
               <xsl:apply-templates select="* | comment() | text()"/>
            </xsl:otherwise>
         </xsl:choose>
      </entry>
   </xsl:template>

   <!-- Preserve comments. -->
   <xsl:template match="comment()">
      <xsl:comment><xsl:value-of select="."/></xsl:comment>
   </xsl:template>

   <!-- Gets the ball rolling, matches the root node and starts processing. -->
   <xsl:template match="/config">
      <config>
         <xsl:apply-templates select="* | comment() | text()"/>
      </config>
   </xsl:template>

</xsl:stylesheet>

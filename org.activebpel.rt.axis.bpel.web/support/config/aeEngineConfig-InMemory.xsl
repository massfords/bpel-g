<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
   xmlns:xalan="http://xml.apache.org/xslt">

   <xsl:include href="aeEngineConfig-Common.xsl"/>

   <!-- Set the Description of the config. -->
   <xsl:template match="/config/entry[@name = 'Description']">
      <entry value="ActiveBPEL In-Memory Configuration" name="Description"/>
   </xsl:template>

   <!-- Remove the Persistent Store -->
   <xsl:template match="/config/entry[@name = 'PersistentStore']"/>

</xsl:stylesheet>

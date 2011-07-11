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

   <!-- Preserve comments (unlikely to work, but can't hurt). -->
   <xsl:template match="comment()">
      <xsl:comment><xsl:value-of select="."/></xsl:comment>
   </xsl:template>

   <!-- Gets the ball rolling, matches the root node and starts processing. -->
   <xsl:template match="/config">
      <config>
         <xsl:apply-templates select="* | comment() | text()"/>
      </config>
   </xsl:template>

   <!-- Set the Description of the config. -->
   <xsl:template match="/config/entry[@name = 'Description']">
      <entry value="ActiveBPEL eXist Configuration" name="Description"/>
   </xsl:template>
   
   <!-- Change the Storage Provider. -->
   <xsl:template match="/config/entry[@name = 'PersistentStore']/entry[@name = 'Factory']/entry[@name = 'StorageProviderFactory']">
      <entry name="StorageProviderFactory">
         <entry name="Class" value="org.activebpel.rt.bpel.server.engine.storage.exist.AeExistStorageProviderFactory"/>
         <entry name="DatabaseType" value="exist"/>
         <entry name="Version" value="@DB_VERSION@"/>
         <entry name="DataSource">
            <entry name="Class" value="org.activebpel.rt.bpel.server.engine.storage.exist.AeExistDataSource"/>
            <entry name="URL" value="@EXIST_URL@"/>
            <entry name="Collection" value="ActiveBPEL_collection"/>
            <entry name="Username" value="admin"/>
            <entry name="Password" value=""/>
            <entry name="Embedded" value="@EXIST_EMBEDDED_FLAG@"/>
            <entry name="DBLocation" value="@EXIST_EMBEDDED_DB_LOCATION@"/>
         </entry>
         <entry name="CustomProviders">
            <entry name="AeB4PTaskStorageProvider">
               <entry name="Class" value="org.activebpel.rt.b4p.server.storage.exist.AeExistTaskStorageProvider" />
            </entry>
         </entry>
      </entry>
   </xsl:template>

   <!-- Change the Transaction Manager Factory -->
   <xsl:template match="/config/entry[@name = 'TransactionManagerFactory']/entry[@name = 'Class']">
      <entry name="Class" value="org.activebpel.rt.bpel.server.engine.storage.exist.tx.AeExistTransactionManagerFactory"/>
   </xsl:template>

</xsl:stylesheet>

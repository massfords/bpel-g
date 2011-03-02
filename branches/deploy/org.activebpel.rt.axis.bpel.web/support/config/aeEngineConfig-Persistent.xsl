<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
   xmlns:xalan="http://xml.apache.org/xslt">

   <xsl:include href="aeEngineConfig-Common.xsl"/>

   <!-- Set the Description of the config. -->
   <xsl:template match="/config/entry[@name = 'Description']">
      <entry value="ActiveBPEL Persistent Configuration" name="Description"/>
   </xsl:template>
   
   <!-- Set the Persistent Store -->
   <xsl:template match="/config/entry[@name = 'PersistentStore']">
      <entry name="PersistentStore">
         <entry name="Factory">
            <entry value="org.activebpel.rt.bpel.server.engine.storage.AeStorageFactory" name="Class"/>
	         <entry name="CustomStorages">
	            <entry name="AeB4PTaskStorage">
	               <entry name="Class" value="org.activebpel.rt.b4p.server.storage.AePersistentTaskStorage" />
	               <entry name="Provider" value="AeB4PTaskStorageProvider" />
	            </entry>
	         </entry>            
            <entry name="StorageProviderFactory">
               <entry value="org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLStorageProviderFactory" name="Class"/>
               <entry value="mysql" name="DatabaseType"/>
               <entry value="@DB_VERSION@" name="Version"/>
   
               <entry name="DataSource">
                  <entry value="org.activebpel.rt.bpel.server.engine.storage.sql.AeJNDIDataSource" name="Class"/>
                  <entry value="java:comp/env/jdbc/ActiveBPELDB" name="JNDILocation"/>
                  <xsl:comment> Optional Username and Password for the DataSource </xsl:comment>
                  <xsl:comment>
                     &lt;entry name="Username" value=""/>
                     &lt;entry name="Password" value=""/>
                  </xsl:comment>
               </entry>
					<entry name="CustomProviders">
						<entry name="AeB4PTaskStorageProvider">
							<entry name="Class" value="org.activebpel.rt.b4p.server.storage.sql.AeSQLTaskStorageProvider" />
               	</entry>
            	</entry>               
            </entry>
            <xsl:comment> Whether to log hash collisions as a counter in the database. </xsl:comment>
            <entry value="true" name="LogHashCollisions"/>
            <entry name="CounterStore">
               <entry value="org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLCounterStore" name="Class"/>
            </entry>
         </entry>
      </entry>
   </xsl:template>

   <!-- Change the Transaction Manager -->
   <xsl:template match="/config/entry[@name = 'TransactionManagerFactory']">
      <entry name="TransactionManagerFactory">
         <entry value="org.activebpel.rt.bpel.server.engine.transaction.sql.AeSQLTransactionManagerFactory" name="Class"/>
      </entry>
   </xsl:template>

   <!-- Change the Process Manager -->
   <xsl:template match="/config/entry[@name = 'ProcessManager']">
      <entry name="ProcessManager">
         <entry value="org.activebpel.rt.bpel.server.engine.AePersistentProcessManager" name="Class"/>
         <xsl:comment>
            ProcessCount is the maximum number of processes allowed in memory.
            The default value is 50. A value of 0 allows an unlimited number of processes.
         </xsl:comment>
         <entry value="50" name="ProcessCount"/>
   
         <xsl:comment>
            ReleaseLag is the number of seconds to wait after process goes
            quiescent before releasing the process from memory. The default value
            is 10. The utility of this setting is that a non-zero value gives a
            chance for invokes and other asynchronous events to complete before
            persisting a process.
         </xsl:comment>
         <entry value="10" name="ReleaseLag"/>
         <entry value="false" name="Debug"/>
      </entry>
   </xsl:template>

   <!-- Change the Queue Manager -->
   <xsl:template match="/config/entry[@name = 'QueueManager']/entry[@name = 'Class']">
      <entry value="org.activebpel.rt.bpel.server.engine.storage.AePersistentQueueManager" name="Class"/>
   </xsl:template>

   <!-- Change the Attachment Manager -->
   <xsl:template match="/config/entry[@name = 'AttachmentManager']/entry[@name = 'Class']">
      <entry value="org.activebpel.rt.bpel.server.engine.storage.AePersistentAttachmentManager" name="Class"/>
   </xsl:template>

   <!-- Change the Coordination Manager -->
   <xsl:template match="/config/entry[@name = 'CoordinationManager']/entry[@name = 'Class']">
      <entry value="org.activebpel.rt.bpel.server.coord.AePersistentCoordinationManager" name="Class"/>
   </xsl:template>

   <!-- Change the Transmission Tracker -->
   <xsl:template match="/config/entry[@name = 'TransmissionTracker']/entry[@name = 'Class']">
      <entry value="org.activebpel.rt.bpel.server.transreceive.AePersistentTransmissionTracker" name="Class"/>
   </xsl:template>
   
   <!-- Remove the URN Resolver Storage -->
   <xsl:template match="/config/entry[@name = 'URNResolver']/entry[@name = 'Storage']"/>

   <!-- Change the Process Logger -->
   <xsl:template match="/config/entry[@name = 'ProcessLogger']/entry[@name = 'Class']">
      <entry value="org.activebpel.rt.bpel.server.logging.AePersistentLogger" name="Class"/>
   </xsl:template>

   <!-- Change the replace.existing Catalog option -->
   <xsl:template match="/config/entry[@name = 'Catalog']/entry[@name = 'replace.existing']">
      <entry value="false" name="replace.existing"/>
   </xsl:template>

   <!-- Change CustomManagers/BPEL4PeopleManager class to use persistent version  -->
   <xsl:template match="/config/entry[@name = 'CustomManagers']/entry[@name = 'BPEL4PeopleManager']/entry[@name = 'Class']">
      <entry name="Class" value="org.activebpel.rt.b4p.server.AePersistentB4PManager"/>
   </xsl:template>

</xsl:stylesheet>

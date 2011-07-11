-- $Header: /Development/AEDevelopment/projects/ddl.org.activebpel/ddl/ActiveBPEL-SQLServer.sql,v 1.24 2007/05/24 21:14:46 KRoe Exp $
--
-- Creates tables in MS SQL Server for process and variable persistence.
--
-- This script should be run to set up the database and tables prior to
-- running the Active BPEL engine for the first time with JDBC persistence
-- enabled.

-- Note: Probably best if you run this script as the database user who will
-- access the database at runtime. It may work with separate dbo and runtime
-- users, but that hasn't been tested.

BEGIN
   DECLARE @db NVARCHAR(255);
   SET @db = N'ActiveBPEL';

   USE master;

   -- Create the database if it doesn't exist.
   IF NOT EXISTS (SELECT name FROM master.dbo.sysdatabases WHERE name = @db)
      EXECUTE('CREATE DATABASE ' + @db);

   -- Make sure columns default to NULL-able.
   EXECUTE('ALTER DATABASE ' + @db + ' SET ANSI_NULL_DEFAULT ON');
END;
GO

USE ActiveBPEL;
GO

--
-- Drop existing tables.
--
BEGIN
   -- First, drop foreign key constraints, so we can drop the tables.
   DECLARE fkcursor CURSOR FOR
      SELECT fk.name, parent.name parent
      FROM dbo.sysobjects fk JOIN dbo.sysobjects parent ON fk.parent_obj = parent.id
      WHERE OBJECTPROPERTY(fk.id, N'IsForeignKey') = 1 AND parent.name LIKE 'Ae%'
   DECLARE @name NVARCHAR(255)
   DECLARE @parent NVARCHAR(255)

   OPEN fkcursor
   FETCH NEXT FROM fkcursor INTO @name, @parent

   WHILE @@FETCH_STATUS = 0
   BEGIN
      EXECUTE('ALTER TABLE ' + @parent + ' DROP CONSTRAINT ' + @name)
      FETCH NEXT FROM fkcursor INTO @name, @parent
   END

   CLOSE fkcursor
   DEALLOCATE fkcursor

   -- Now drop the tables.
   DECLARE tbcursor CURSOR FOR
      SELECT name
      FROM dbo.sysobjects
      WHERE OBJECTPROPERTY(id, N'IsUserTable') = 1 AND name LIKE 'Ae%'

   OPEN tbcursor
   FETCH NEXT FROM tbcursor INTO @name

   WHILE @@FETCH_STATUS = 0
   BEGIN
      EXECUTE('DROP TABLE ' + @name)
      FETCH NEXT FROM tbcursor INTO @name
   END

   CLOSE tbcursor
   DEALLOCATE tbcursor
END
GO

-- Create the Meta Information table
CREATE TABLE AeMetaInfo (
   PropertyName NVARCHAR(255) NOT NULL,
   PropertyValue NVARCHAR(255) NOT NULL,
   PRIMARY KEY (PropertyName)
);

-- Version column must be kept in sync with the aeEngineConfig entry used to check the schema
INSERT INTO AeMetaInfo VALUES ('Version', '@DB_VERSION@');
INSERT INTO AeMetaInfo VALUES ('DatabaseType', 'sqlserver');

-- Create the Counters table.
CREATE TABLE AeCounter (
   CounterName NVARCHAR(255) NOT NULL,
   CounterValue BIGINT NOT NULL,
   PRIMARY KEY (CounterName)
);

-- Create the Process table
CREATE TABLE AeProcess
(
   ProcessId BIGINT NOT NULL,
   PlanId INT NOT NULL,
   ProcessName NVARCHAR(255) NOT NULL,
   ProcessNamespace NVARCHAR(255) NOT NULL,
   ProcessDocument NTEXT,
   ProcessState INT NOT NULL,
   ProcessStateReason INT,
   StartDate DATETIME,
   EndDate DATETIME,
   PendingInvokesCount INT,
   ModifiedDate DATETIME,
   ModifiedCount INT DEFAULT 0,
   PRIMARY KEY (ProcessId)
);

-- Note on AeProcess.ProcessState
--   value must be one of the following from org.activebpel.rt.bpel.IAeBusinessProcess
--   PROCESS_LOADED     = 0;
--   PROCESS_RUNNING    = 1;
--   PROCESS_SUSPENDED  = 2;
--   PROCESS_COMPLETE   = 3;
--   PROCESS_FAULTED    = 4;

CREATE INDEX AeProcessByName on AeProcess(ProcessName);
CREATE INDEX AeProcessByState on AeProcess(ProcessState);
CREATE INDEX AeProcessByStartDate on AeProcess(StartDate DESC, ProcessId DESC);
CREATE INDEX AeProcessByEndDate on AeProcess(EndDate);
CREATE INDEX AeProcessByPendingInvokesCount on AeProcess(PendingInvokesCount);

-- Create the Process Log table
CREATE TABLE AeProcessLog
(
   ProcessId BIGINT NOT NULL,
   ProcessLog NTEXT,
   Counter INT NOT NULL IDENTITY,
   LineCount INT NOT NULL,
   FOREIGN KEY (ProcessId) REFERENCES AeProcess(ProcessId) ON DELETE CASCADE,
   PRIMARY KEY (ProcessId, Counter)
);

-- Create the Variable table
CREATE TABLE AeVariable
(
   ProcessId BIGINT NOT NULL,
   LocationId INT NOT NULL,
   VersionNumber INT NOT NULL,
   VariableDocument NTEXT NOT NULL,
   FOREIGN KEY (ProcessId) REFERENCES AeProcess(ProcessId) ON DELETE CASCADE,
   PRIMARY KEY (ProcessId, LocationId, VersionNumber)
);

-- Create the Alarm table
CREATE TABLE AeAlarm (
   ProcessId BIGINT NOT NULL,
   LocationPathId INT NOT NULL,
   Deadline DATETIME NOT NULL,
   DeadlineMillis BIGINT NOT NULL,
   GroupId INT NOT NULL,
   AlarmId INT NOT NULL,
   FOREIGN KEY (ProcessId) REFERENCES AeProcess(ProcessId) ON DELETE CASCADE,
   PRIMARY KEY (ProcessId, LocationPathId, AlarmId)
);

CREATE INDEX AeAlarmByGroup ON AeAlarm(ProcessId, GroupId);

-- Create the Receive Queue table
CREATE TABLE AeQueuedReceive (
   QueuedReceiveId INT NOT NULL,
   ProcessId BIGINT NOT NULL,
   LocationPathId INT NOT NULL,
   Operation NVARCHAR(255) NOT NULL,
   PartnerLinkName NVARCHAR(255) NOT NULL,
   PortTypeNamespace NVARCHAR(255) NOT NULL,
   PortTypeLocalPart NVARCHAR(255) NOT NULL,
   CorrelationProperties NTEXT NOT NULL,
   MatchHash INT NOT NULL,
   CorrelateHash INT NOT NULL,
   GroupId INT NOT NULL,
   PartnerLinkId INT NOT NULL,
   AllowsConcurrency TINYINT NOT NULL,
   FOREIGN KEY (ProcessId) REFERENCES AeProcess(ProcessId) ON DELETE CASCADE,
   PRIMARY KEY (QueuedReceiveId)
);

CREATE INDEX AeQueuedReceiveByLocation ON AeQueuedReceive(ProcessId, LocationPathId);
CREATE INDEX AeQueuedReceiveByGroup ON AeQueuedReceive(ProcessId, GroupId);
CREATE INDEX AeQueuedReceiveByCorrelateHash ON AeQueuedReceive(MatchHash, CorrelateHash);

-- Create the Process Journal table
CREATE TABLE AeProcessJournal (
   JournalId BIGINT NOT NULL,
   ProcessId BIGINT NOT NULL,
   Counter BIGINT IDENTITY NOT NULL,
   LocationId INT NOT NULL,
   EntryType TINYINT NOT NULL,
   EntryDocument NTEXT,
   FOREIGN KEY (ProcessId) REFERENCES AeProcess(ProcessId) ON DELETE CASCADE,
   PRIMARY KEY (JournalId)
);

-- This index is used only for recovery queries, and extra indexes on the
-- AeProcessJournal table cause deadlocks with some databases, so we'll do
-- without for now.
-- CREATE INDEX AeProcessJournalByProcessId ON AeProcessJournal(ProcessId, Counter);

-- Create the URN mapping table
CREATE TABLE AeURNValues (
   URN NVARCHAR(255) NOT NULL,
   URL NTEXT NOT NULL,
   PRIMARY KEY (URN)
);

-- Create the Coordination table
-- Note on AeCoordination.CoordinationRole column:
--   value must be one of the following:
--   SUBPROCESS_COORDINATOR  = 0;
--   SUBPROCESS_PARICIPANT   = 1;

CREATE TABLE AeCoordination
(
   CoordinationPk BIGINT NOT NULL,
   CoordinationType NVARCHAR(255) NOT NULL,
   CoordinationRole TINYINT NOT NULL,
   CoordinationId NVARCHAR(255) NOT NULL,
   State NVARCHAR(255) NOT NULL,
   ProcessId BIGINT NOT NULL,
   LocationPath NTEXT NOT NULL,
   CoordinationDocument NTEXT,
   StartDate DATETIME,
   ModifiedDate DATETIME,
   PRIMARY KEY (CoordinationPk),
   FOREIGN KEY (ProcessId) REFERENCES AeProcess(ProcessId) ON DELETE CASCADE
);

CREATE INDEX AeCoordByCoordId ON AeCoordination(CoordinationId, ProcessId);
CREATE INDEX AeCoordByProcessId ON AeCoordination(ProcessId);

-- -----------------------------------------------------------------------------
-- TransmissionTracker table - stores transmission id and data needed for durable invokes and durable reply.
-- -----------------------------------------------------------------------------
CREATE TABLE AeTransmissionTracker
(
   TransmissionId BIGINT NOT NULL,
   State TINYINT NOT NULL,
   MessageId NVARCHAR(255),
   PRIMARY KEY (TransmissionId)
);

-- ------------------------------------------------------------------------
-- AeProcessAttachment - Attachments accociated to processes
-- ------------------------------------------------------------------------
CREATE TABLE AeProcessAttachment (
   AttachmentGroupId BIGINT NOT NULL,
   ProcessId BIGINT,
   PRIMARY KEY (AttachmentGroupId),
   FOREIGN Key (ProcessId) REFERENCES AeProcess (ProcessId) ON DELETE CASCADE
);

-- ------------------------------------------------------------------------
-- AeAttachment - Attachment Item Entries (Headers and Content)
-- ------------------------------------------------------------------------
CREATE TABLE AeAttachment (
   AttachmentGroupId BIGINT NOT NULL,
   AttachmentItemId BIGINT NOT NULL,
   AttachmentHeader NTEXT,
   AttachmentContent IMAGE NOT NULL,
   PRIMARY KEY (AttachmentItemId),
   FOREIGN Key (AttachmentGroupId) REFERENCES AeProcessAttachment (AttachmentGroupId) ON DELETE CASCADE
);

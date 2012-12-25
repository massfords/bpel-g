-- $Header: /Development/AEDevelopment/projects/ddl.org.activebpel/ddl/ActiveBPEL-MySQL.sql,v 1.20 2007/05/24 21:14:46 KRoe Exp $
--
-- Creates tables in MySQL for process and variable persistence.
--
-- This script should be run to set up the database and tables prior to
-- running the Active BPEL engine for the first time with JDBC persistence
-- enabled.

-- Drop the database if it already exists.
DROP DATABASE IF EXISTS ActiveBPEL;

-- Create the database (and specify utf8 as the default character set for all
-- tables)
CREATE DATABASE ActiveBPEL DEFAULT CHARACTER SET utf8;
USE ActiveBPEL;

-- Create the Meta Information table
CREATE TABLE AeMetaInfo (
   PropertyName VARCHAR(255) NOT NULL,
   PropertyValue VARCHAR(255) NOT NULL,
   PRIMARY KEY (PropertyName)
) TYPE = INNODB;

-- Version column must be kept in sync with the aeEngineConfig entry used to check the schema
INSERT INTO AeMetaInfo VALUES ('Version', '@DB_VERSION@');
INSERT INTO AeMetaInfo VALUES ('DatabaseType', 'mysql');

-- Create the Counters table.
CREATE TABLE AeCounter (
   CounterName VARCHAR(255) NOT NULL,
   CounterValue BIGINT UNSIGNED NOT NULL,
   PRIMARY KEY (CounterName)
) TYPE = INNODB;

-- Create the Process table
CREATE TABLE AeProcess
(
   ProcessId BIGINT UNSIGNED NOT NULL,
   PlanId INT UNSIGNED NOT NULL,
   ProcessName VARCHAR(255) NOT NULL,
   ProcessNamespace TEXT NOT NULL,
   ProcessDocument LONGTEXT,
   ProcessState INT NOT NULL,
   ProcessStateReason INT,
   StartDate DATETIME,
   EndDate DATETIME,
   PendingInvokesCount INT UNSIGNED,
   ModifiedDate DATETIME,
   ModifiedCount INT UNSIGNED DEFAULT 0,
   PRIMARY KEY (ProcessId)
) TYPE = INNODB;

-- Note on AeProcess.ProcessState
--   value must be one of the following from org.activebpel.rt.bpel.IAeBusinessProcess
--   PROCESS_LOADED     = 0;
--   PROCESS_RUNNING    = 1;
--   PROCESS_SUSPENDED  = 2;
--   PROCESS_COMPLETE   = 3;
--   PROCESS_FAULTED    = 4;

CREATE INDEX AeProcessByName on AeProcess(ProcessName);
CREATE INDEX AeProcessByState on AeProcess(ProcessState);
CREATE INDEX AeProcessByStartDate on AeProcess(StartDate);
CREATE INDEX AeProcessByEndDate on AeProcess(EndDate);
CREATE INDEX AeProcessByPendingInvokesCount on AeProcess(PendingInvokesCount);

-- Create the Process Log table
CREATE TABLE AeProcessLog
(
   ProcessId BIGINT UNSIGNED NOT NULL,
   ProcessLog LONGTEXT NOT NULL,
   Counter INT UNSIGNED NOT NULL AUTO_INCREMENT,
   LineCount INT NOT NULL,
   KEY (ProcessId, Counter),
   FOREIGN KEY (ProcessId) REFERENCES AeProcess(ProcessId) ON DELETE CASCADE,
   PRIMARY KEY (Counter)
) TYPE = INNODB;

-- Create the Variable table
CREATE TABLE AeVariable
(
   ProcessId BIGINT UNSIGNED NOT NULL,
   LocationId INT NOT NULL,
   VersionNumber INT NOT NULL,
   VariableDocument LONGTEXT NOT NULL,
   FOREIGN KEY (ProcessId) REFERENCES AeProcess(ProcessId) ON DELETE CASCADE,
   PRIMARY KEY (ProcessId, LocationId, VersionNumber)
) TYPE = INNODB;

-- Create the Alarm table
CREATE TABLE AeAlarm (
   ProcessId BIGINT UNSIGNED NOT NULL,
   LocationPathId INT NOT NULL,
   Deadline DATETIME NOT NULL,
   DeadlineMillis BIGINT NOT NULL,
   GroupId INT NOT NULL,
   AlarmId INT NOT NULL,
   FOREIGN KEY (ProcessId) REFERENCES AeProcess(ProcessId) ON DELETE CASCADE,
   PRIMARY KEY (ProcessId, LocationPathId, AlarmId)
) TYPE = INNODB;

CREATE INDEX AeAlarmByGroup ON AeAlarm(ProcessId, GroupId);

-- Create the Receive Queue table
CREATE TABLE AeQueuedReceive (
   QueuedReceiveId INT UNSIGNED NOT NULL,
   ProcessId BIGINT UNSIGNED NOT NULL,
   LocationPathId INT NOT NULL,
   Operation VARCHAR(255) NOT NULL,
   PartnerLinkName VARCHAR(255) NOT NULL,
   PortTypeNamespace VARCHAR(255) NOT NULL,
   PortTypeLocalPart VARCHAR(255) NOT NULL,
   CorrelationProperties LONGTEXT NOT NULL,
   MatchHash INT NOT NULL,
   CorrelateHash INT NOT NULL,
   GroupId INT NOT NULL,
   PartnerLinkId INT NOT NULL,
   AllowsConcurrency TINYINT NOT NULL,
   KEY (ProcessId, LocationPathId),
   FOREIGN KEY (ProcessId) REFERENCES AeProcess(ProcessId) ON DELETE CASCADE,
   PRIMARY KEY (QueuedReceiveId)
) TYPE = INNODB;

CREATE INDEX AeQueuedReceiveByLocation ON AeQueuedReceive(ProcessId, LocationPathId);
CREATE INDEX AeQueuedReceiveByGroup ON AeQueuedReceive(ProcessId, GroupId);

-- Add QueuedReceiveId to this index to avoid sort for GetCorrelatedReceives query.
CREATE INDEX AeQueuedReceiveByCorrelateHash ON AeQueuedReceive(MatchHash, CorrelateHash, QueuedReceiveId);

-- Create the Process Journal table
CREATE TABLE AeProcessJournal (
   JournalId BIGINT UNSIGNED NOT NULL,
   ProcessId BIGINT UNSIGNED NOT NULL,
   Counter BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
   LocationId INT NOT NULL,
   EntryType TINYINT NOT NULL,
   EntryDocument LONGTEXT,
   KEY (Counter),
   KEY (ProcessId),
   FOREIGN KEY (ProcessId) REFERENCES AeProcess(ProcessId) ON DELETE CASCADE,
   PRIMARY KEY (JournalId)
) TYPE = INNODB;

-- Create the URN mapping table
CREATE TABLE AeURNValues (
   URN VARCHAR(255) NOT NULL,
   URL LONGTEXT NOT NULL,
   PRIMARY KEY (URN)
) TYPE = INNODB;

-- Create the Coordination table
-- Note on AeCoordination.CoordinationRole column:
--   value must be one of the following:
--   SUBPROCESS_COORDINATOR  = 0;
--   SUBPROCESS_PARICIPANT   = 1;

CREATE TABLE AeCoordination
(
   CoordinationPk BIGINT UNSIGNED NOT NULL,
   CoordinationType VARCHAR(255) NOT NULL,
   CoordinationRole TINYINT UNSIGNED NOT NULL,
   CoordinationId VARCHAR(255) NOT NULL,
   State VARCHAR(255) NOT NULL,
   ProcessId BIGINT UNSIGNED NOT NULL,
   LocationPath TEXT NOT NULL,
   CoordinationDocument LONGTEXT,
   StartDate DATETIME,
   ModifiedDate DATETIME,
   KEY AeCoordByProcessId (ProcessId),
   PRIMARY KEY (CoordinationPk),
   FOREIGN KEY (ProcessId) REFERENCES AeProcess(ProcessId) ON DELETE CASCADE
) TYPE = INNODB;

CREATE INDEX AeCoordByCoordId ON AeCoordination(CoordinationId, ProcessId);

-- -----------------------------------------------------------------------------
-- TransmissionTracker table - stores transmission id and data needed for durable invokes and durable reply.
-- -----------------------------------------------------------------------------
CREATE TABLE AeTransmissionTracker
(
   TransmissionId BIGINT UNSIGNED NOT NULL,
   State INT UNSIGNED NOT NULL,
   MessageId VARCHAR(255),
   PRIMARY KEY (TransmissionId)
) TYPE = INNODB;

-- ------------------------------------------------------------------------
-- AeProcessAttachment - Attachments accociated to processes
-- ------------------------------------------------------------------------
CREATE TABLE AeProcessAttachment (
   AttachmentGroupId BIGINT UNSIGNED NOT NULL,
   ProcessId BIGINT UNSIGNED,
   PRIMARY KEY (AttachmentGroupId),
   CONSTRAINT aeprocess_attachments FOREIGN Key (ProcessId) REFERENCES AeProcess (ProcessId) ON DELETE CASCADE
) TYPE = INNODB;

-- ------------------------------------------------------------------------
-- AeAttachment - Attachment Item Entries (Headers and Content)
-- ------------------------------------------------------------------------
CREATE TABLE AeAttachment (
   AttachmentGroupId BIGINT UNSIGNED NOT NULL,
   AttachmentItemId BIGINT UNSIGNED NOT NULL,
   AttachmentHeader LONGTEXT,
   AttachmentContent LONGBLOB NOT NULL,
   PRIMARY KEY (AttachmentItemId),
   CONSTRAINT attachment_items FOREIGN Key (AttachmentGroupId) REFERENCES AeProcessAttachment (AttachmentGroupId) ON DELETE CASCADE
) TYPE = INNODB;

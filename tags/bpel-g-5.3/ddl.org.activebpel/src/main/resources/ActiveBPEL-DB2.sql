CONNECT TO ABPEL USER bpeluser;
-- The preceding line must be at the top of the file to work.
--
-- The user specified in the CONNECT statement must be the same as the user
-- that will connect to the database at runtime, because the user determines
-- the default schema used by DB2.

-- $Header: /Development/AEDevelopment/projects/ddl.org.activebpel/ddl/ActiveBPEL-DB2.sql,v 1.24 2007/05/24 21:14:46 KRoe Exp $
--
-- Creates tables in DB2 for process and variable persistence.
--
-- This script should be run to set up the tables prior to running the Active
-- BPEL engine for the first time with JDBC persistence enabled.

-- Modify configuration settings so that statistics are automatically updated.
-- We recommend enabling this, to increase query performance. Uncomment to enable in config.
-- UPDATE DATABASE CONFIGURATION USING AUTO_MAINT On;
-- UPDATE DATABASE CONFIGURATION USING AUTO_TBL_MAINT On;
-- UPDATE DATABASE CONFIGURATION USING AUTO_RUNSTATS On;
-- UPDATE DATABASE CONFIGURATION USING AUTO_STATS_PROF On;
-- UPDATE DATABASE CONFIGURATION USING AUTO_PROF_UPD On;

-- Increase Max Application connections for our application
-- UPDATE DATABASE CONFIGURATION USING MAXAPPLS 160;
-- UPDATE DATABASE CONFIGURATION USING LOCKLIST 800;

-- Drop existing tables.
DROP TABLE AeMetaInfo;
DROP TABLE AeCounter;
DROP TABLE AeProcess;
DROP TABLE AeProcessLog;
DROP TABLE AeVariable;
DROP TABLE AeAlarm;
DROP TABLE AeQueuedReceive;
DROP TABLE AeProcessJournal;
DROP TABLE AeURNValues;
DROP TABLE AeCoordination;
DROP TABLE AeTransmissionTracker;
DROP TABLE AeProcessAttachment;
DROP TABLE AeAttachment;

-- Create the Meta Information table
CREATE TABLE AeMetaInfo (
   PropertyName VARGRAPHIC(255) NOT NULL,
   PropertyValue VARGRAPHIC(255) NOT NULL,
   PRIMARY KEY (PropertyName)
);

-- Version column must be kept in sync with the aeEngineConfig entry used to check the schema
INSERT INTO AeMetaInfo VALUES ('Version', '@DB_VERSION@');
INSERT INTO AeMetaInfo VALUES ('DatabaseType', 'db2');

-- Create the Counters table.
CREATE TABLE AeCounter (
   CounterName VARGRAPHIC(255) NOT NULL,
   CounterValue DECIMAL(19, 0) NOT NULL,
   PRIMARY KEY (CounterName)
);

-- Create the Process table
CREATE TABLE AeProcess
(
   ProcessId DECIMAL(19, 0) NOT NULL,
   PlanId INT NOT NULL,
   ProcessName VARGRAPHIC(255) NOT NULL,
   ProcessNamespace VARGRAPHIC(255) NOT NULL,
   ProcessDocument DBCLOB(512M),
   ProcessState INT NOT NULL,
   ProcessStateReason INT,
   StartDate TIMESTAMP,
   EndDate TIMESTAMP,
   PendingInvokesCount INT,
   ModifiedDate TIMESTAMP,
   ModifiedCount INT DEFAULT 0,
   PRIMARY KEY (ProcessId)
);

-- Note on AeProcess.ProcessState
--   value must be one of the following from org.activebpel.rt.bpel.IAeBusinessProcess
--   PROCESS_LOADED         = 0;
--   PROCESS_RUNNING     = 1;
--   PROCESS_SUSPENDED     = 2;
--   PROCESS_COMPLETE     = 3;
--   PROCESS_FAULTED     = 4;

-- Index names in DB2 are limited to 18 characters.
CREATE INDEX AeProcessName on AeProcess(ProcessName);
CREATE INDEX AeProcessState on AeProcess(ProcessState);
CREATE INDEX AeProcessStartDate on AeProcess(StartDate DESC, ProcessId DESC);
CREATE INDEX AeProcessEndDate on AeProcess(EndDate);
CREATE INDEX AeProcessInvokes on AeProcess(PendingInvokesCount);

-- Create the Process Log table
CREATE TABLE AeProcessLog
(
   ProcessId DECIMAL(19, 0) NOT NULL,
   ProcessLog DBCLOB(512M),
   Counter INTEGER GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
   LineCount INT NOT NULL,
   FOREIGN KEY (ProcessId) REFERENCES AeProcess(ProcessId) ON DELETE CASCADE,
   PRIMARY KEY (ProcessId, Counter)
);

-- Create the Variable table
CREATE TABLE AeVariable
(
   ProcessId DECIMAL(19, 0) NOT NULL,
   LocationId INT NOT NULL,
   VersionNumber INT NOT NULL,
   VariableDocument DBCLOB(512M) NOT NULL,
   FOREIGN KEY (ProcessId) REFERENCES AeProcess(ProcessId) ON DELETE CASCADE,
   PRIMARY KEY (ProcessId, LocationId, VersionNumber)
);

-- Create the Alarm table
CREATE TABLE AeAlarm (
   ProcessId DECIMAL(19, 0) NOT NULL,
   LocationPathId INT NOT NULL,
   Deadline TIMESTAMP NOT NULL,
   DeadlineMillis DECIMAL(19, 0) NOT NULL,
   GroupId INT NOT NULL,
   AlarmId INT NOT NULL,
   FOREIGN KEY (ProcessId) REFERENCES AeProcess(ProcessId) ON DELETE CASCADE,
   PRIMARY KEY (ProcessId, LocationPathId, AlarmId)
);

CREATE INDEX AeAlrmGroup ON AeAlarm(ProcessId, GroupId);

-- Create the Receive Queue table
CREATE TABLE AeQueuedReceive (
   QueuedReceiveId INT NOT NULL,
   ProcessId DECIMAL(19, 0) NOT NULL,
   LocationPathId INT NOT NULL,
   Operation VARGRAPHIC(255) NOT NULL,
   PartnerLinkName VARGRAPHIC(255) NOT NULL,
   PortTypeNamespace VARGRAPHIC(255) NOT NULL,
   PortTypeLocalPart VARGRAPHIC(255) NOT NULL,
   CorrelationProperties DBCLOB(512M) NOT NULL,
   MatchHash INT NOT NULL,
   CorrelateHash INT NOT NULL,
   GroupId INT NOT NULL,
   PartnerLinkId DECIMAL(19, 0) NOT NULL,
   AllowsConcurrency SMALLINT NOT NULL,
   FOREIGN KEY (ProcessId) REFERENCES AeProcess(ProcessId) ON DELETE CASCADE,
   PRIMARY KEY (QueuedReceiveId)
);

-- Index names in DB2 are limited to 18 characters.
CREATE INDEX AeQLocation ON AeQueuedReceive(ProcessId, LocationPathId);
CREATE INDEX AeQGroup ON AeQueuedReceive(ProcessId, GroupId);

-- Add QueuedReceiveId to this index to avoid sort for GetCorrelatedReceives query.
CREATE INDEX AeQCorrelateHash ON AeQueuedReceive(MatchHash, CorrelateHash, QueuedReceiveId);

-- Create the Process Journal table
CREATE TABLE AeProcessJournal (
   JournalId DECIMAL(19, 0) NOT NULL,
   ProcessId DECIMAL(19, 0) NOT NULL,
   Counter DECIMAL(19,0) GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
   LocationId INT NOT NULL,
   EntryType SMALLINT NOT NULL,
   EntryDocument DBCLOB(512M),
   FOREIGN KEY (ProcessId) REFERENCES AeProcess(ProcessId) ON DELETE CASCADE,
   PRIMARY KEY (JournalId)
);

-- This index is used only for recovery queries, and extra indexes on the
-- AeProcessJournal table cause deadlocks with some databases, so we'll do
-- without for now.
-- CREATE INDEX AeJournalByProcess ON AeProcessJournal(ProcessId, Counter);

-- Create the URN mapping table
CREATE TABLE AeURNValues (
   URN VARGRAPHIC(255) NOT NULL,
   URL DBCLOB(512M) NOT NULL,
   PRIMARY KEY (URN)
);

-- Create the Coordination table
-- Note on AeCoordination.CoordinationRole column:
--   value must be one of the following:
--   SUBPROCESS_COORDINATOR  = 0;
--   SUBPROCESS_PARICIPANT   = 1;

CREATE TABLE AeCoordination
(
   CoordinationPk DECIMAL(19, 0) NOT NULL,
   CoordinationType VARGRAPHIC(255) NOT NULL,
   CoordinationRole SMALLINT NOT NULL,
   CoordinationId VARGRAPHIC(255) NOT NULL,
   State VARGRAPHIC(255) NOT NULL,
   ProcessId DECIMAL(19, 0) NOT NULL,
   LocationPath  DBCLOB(512M) NOT NULL,
   CoordinationDocument DBCLOB(512M),
   StartDate TIMESTAMP,
   ModifiedDate TIMESTAMP,
   FOREIGN KEY (ProcessId) REFERENCES AeProcess(ProcessId) ON DELETE CASCADE,   
   PRIMARY KEY (CoordinationPk)
);

-- Index names in DB2 are limited to 18 characters.
CREATE INDEX AeCoordByCoordId ON AeCoordination(CoordinationId, ProcessId);
CREATE INDEX AeCoordByProcessId ON AeCoordination(ProcessId);

-- -----------------------------------------------------------------------------
-- TransmissionTracker table - stores transmission id and data needed for durable invokes and durable reply.
-- -----------------------------------------------------------------------------
CREATE TABLE AeTransmissionTracker
(
   TransmissionId DECIMAL(19, 0) NOT NULL,
   State INT NOT NULL,
   MessageId VARGRAPHIC(255),
   PRIMARY KEY (TransmissionId)
);

-- ------------------------------------------------------------------------
-- AeProcessAttachment - Attachments accociated to processes
-- ------------------------------------------------------------------------
CREATE TABLE AeProcessAttachment (
   AttachmentGroupId DECIMAL(19, 0) NOT NULL,
   ProcessId DECIMAL(19, 0),
   PRIMARY KEY (AttachmentGroupId),
   FOREIGN Key (ProcessId) REFERENCES AeProcess (ProcessId) ON DELETE CASCADE
);

-- ------------------------------------------------------------------------
-- AeAttachment - Attachment Item Entries (Headers and Content)
-- ------------------------------------------------------------------------
CREATE TABLE AeAttachment (
   AttachmentGroupId DECIMAL(19, 0) NOT NULL,
   AttachmentItemId DECIMAL(19, 0) NOT NULL,
   AttachmentHeader DBCLOB(512M),
   AttachmentContent BLOB(1G) NOT NULL,
   PRIMARY KEY (AttachmentItemId),
   FOREIGN Key (AttachmentGroupId) REFERENCES AeProcessAttachment (AttachmentGroupId) ON DELETE CASCADE
);

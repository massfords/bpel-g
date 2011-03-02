-- $Header: /Development/AEDevelopment/projects/ddl.org.activebpel/ddl/ActiveBPEL-Oracle.sql,v 1.21 2008/04/02 01:42:21 mford Exp $
--
-- Creates tables in Oracle for process and variable persistence.
--
-- This script should be run to set up the database and tables prior to
-- running the Active BPEL engine for the first time with JDBC persistence
-- enabled.
--
-- NOTE: The user connecting to create the database schema here must be the
-- same as the user that will connect to the database at runtime, because the
-- user determines the default schema used by Oracle.

-- Drop existing tables and their indexes.
BEGIN
   FOR i IN ( SELECT table_name FROM dba_tables WHERE UPPER(owner) = UPPER(USER) AND dropped = 'NO')
   LOOP
      EXECUTE IMMEDIATE 'DROP TABLE ' || i.table_name || ' CASCADE CONSTRAINTS PURGE';
   END LOOP;
END;
/
BEGIN
   FOR i IN ( SELECT sequence_name FROM dba_sequences WHERE UPPER(sequence_owner) = UPPER(USER) )
   LOOP
      EXECUTE IMMEDIATE 'DROP SEQUENCE ' || i.sequence_name;
   END LOOP;
END;
/

-- Notice that the semi-colons for this file are all on their own line. This is
-- due to a limitation in the ant sql task that we're using to run this script.
-- The ant sql task requires its delimiter to be anywhere in the line or on its
-- own line. We're using the latter since this script has PL/SQL commands above
-- which require semi-colons within the command.
COMMIT
;

-- Create the Meta Information table
CREATE TABLE AeMetaInfo (
   PropertyName VARCHAR(255) NOT NULL,
   PropertyValue VARCHAR(255) NOT NULL,
   PRIMARY KEY (PropertyName)
)
;

-- Version column must be kept in sync with the aeEngineConfig entry used to check the schema
INSERT INTO AeMetaInfo VALUES ('Version', '@DB_VERSION@')
;
INSERT INTO AeMetaInfo VALUES ('DatabaseType', 'oracle')
;

-- Create the Counters table.
CREATE TABLE AeCounter (
   CounterName VARCHAR(255) NOT NULL,
   CounterValue INT NOT NULL,
   PRIMARY KEY (CounterName)
)
;

-- Create the Process table
CREATE TABLE AeProcess
(
   ProcessId INT NOT NULL,
   PlanId INT NOT NULL,
   ProcessName VARCHAR(255) NOT NULL,
   ProcessNamespace VARCHAR(255) NOT NULL,
   ProcessDocument CLOB,
   ProcessState INT NOT NULL,
   ProcessStateReason INT,
   StartDate TIMESTAMP,
   EndDate TIMESTAMP,
   PendingInvokesCount INT,
   ModifiedDate TIMESTAMP,
   ModifiedCount INT DEFAULT 0,
   PRIMARY KEY (ProcessId)
)
;

-- Note on AeProcess.ProcessState
--   value must be one of the following from org.activebpel.rt.bpel.IAeBusinessProcess
--   PROCESS_LOADED         = 0;
--   PROCESS_RUNNING     = 1;
--   PROCESS_SUSPENDED     = 2;
--   PROCESS_COMPLETE     = 3;
--   PROCESS_FAULTED     = 4;

-- Add all columns for Active Processes query to StartDate index to optimize
-- Active Processes query.
CREATE INDEX AeProcessByStartDate on AeProcess(StartDate DESC, ProcessId DESC, ProcessName, ProcessNamespace, ProcessState, ProcessStateReason, EndDate)
;

-- Oracle executes the Active Processes query most quickly when all of the
-- query columns are in a single index (see the AeProcessByStartDate index
-- above), because then Oracle performs the query by scanning the index without
-- looking at the much larger AeProcess table. However, if the number of rows is
-- large enough, then even the index will be too large to scan quickly, and it
-- will be necessary to restrict the query ahead of time. At that point, one or
-- more of the following indexes will be useful.
-- CREATE INDEX AeProcessByName on AeProcess(ProcessName);
-- CREATE INDEX AeProcessByState on AeProcess(ProcessState);
-- CREATE INDEX AeProcessByStartDate on AeProcess(StartDate);
-- CREATE INDEX AeProcessByEndDate on AeProcess(EndDate);

CREATE INDEX AeProcessByPendingInvokesCount on AeProcess(PendingInvokesCount)
;

-- This index optimizes joins for the GetCorrelatedReceives query.
CREATE INDEX AeProcessForQueuedReceive ON AeProcess(ProcessId, ProcessName, ProcessNamespace)
;

CREATE SEQUENCE AeProcessLogSequence
;

-- Create the Process Log table
CREATE TABLE AeProcessLog
(
   ProcessId INT NOT NULL,
   ProcessLog CLOB,
   Counter INT NOT NULL,
   LineCount INT NOT NULL,
   FOREIGN KEY (ProcessId) REFERENCES AeProcess(ProcessId) ON DELETE CASCADE,
   PRIMARY KEY (ProcessId, Counter)
)
;

-- Create the Variable table
CREATE TABLE AeVariable
(
   ProcessId INT NOT NULL,
   LocationId INT NOT NULL,
   VersionNumber INT NOT NULL,
   VariableDocument CLOB NOT NULL,
   FOREIGN KEY (ProcessId) REFERENCES AeProcess(ProcessId) ON DELETE CASCADE,
   PRIMARY KEY (ProcessId, LocationId, VersionNumber)
)
;

-- Create the Alarm table
CREATE TABLE AeAlarm (
   ProcessId INT NOT NULL,
   LocationPathId INT NOT NULL,
   Deadline TIMESTAMP NOT NULL,
   DeadlineMillis INT NOT NULL,
   GroupId INT NOT NULL,
   AlarmId INT NOT NULL,
   FOREIGN KEY (ProcessId) REFERENCES AeProcess(ProcessId) ON DELETE CASCADE,
   PRIMARY KEY (ProcessId, LocationPathId, AlarmId)
)
;

CREATE INDEX AeAlarmByGroup ON AeAlarm(ProcessId, GroupId)
;

-- Create the Receive Queue table
CREATE TABLE AeQueuedReceive (
   QueuedReceiveId INT NOT NULL,
   ProcessId INT NOT NULL,
   LocationPathId INT NOT NULL,
   Operation VARCHAR(255) NOT NULL,
   PartnerLinkName VARCHAR(255) NOT NULL,
   PortTypeNamespace VARCHAR(255) NOT NULL,
   PortTypeLocalPart VARCHAR(255) NOT NULL,
   CorrelationProperties CLOB NOT NULL,
   MatchHash INT NOT NULL,
   CorrelateHash INT NOT NULL,
   GroupId INT NOT NULL,
   PartnerLinkId INT NOT NULL,
   AllowsConcurrency NUMBER(1) NOT NULL,
   FOREIGN KEY (ProcessId) REFERENCES AeProcess(ProcessId) ON DELETE CASCADE,
   PRIMARY KEY (QueuedReceiveId)
)
;

CREATE INDEX AeQueuedReceiveByLocation ON AeQueuedReceive(ProcessId, LocationPathId)
;
CREATE INDEX AeQueuedReceiveByGroup ON AeQueuedReceive(ProcessId, GroupId)
;

-- Add QueuedReceiveId to this index to avoid sort for GetCorrelatedReceives query.
CREATE INDEX AeQueuedReceiveByCorrelateHash ON AeQueuedReceive(MatchHash, CorrelateHash, QueuedReceiveId)
;

-- Create the Process Journal table
CREATE TABLE AeProcessJournal (
   JournalId INT NOT NULL,
   ProcessId INT NOT NULL,
   Counter INT NOT NULL,
   LocationId INT NOT NULL,
   EntryType NUMBER(3) NOT NULL,
   EntryDocument CLOB,
   FOREIGN KEY (ProcessId) REFERENCES AeProcess(ProcessId) ON DELETE CASCADE,
   PRIMARY KEY (JournalId)
)
;

-- This index is used only for recovery queries, and extra indexes on the
-- AeProcessJournal table cause deadlocks with some databases, so we'll do
-- without for now.
-- CREATE INDEX AeProcessJournalByProcessId ON AeProcessJournal(ProcessId, Counter);

-- Create the Oracle sequence for AeProcessJournal.Counter
CREATE SEQUENCE AeProcessJournalSequence
;

-- Create the URN mapping table
CREATE TABLE AeURNValues (
   URN VARCHAR(255) NOT NULL,
   URL CLOB NOT NULL,
   PRIMARY KEY (URN)
)
;

-- Create the Coordination table
-- Note on AeCoordination.CoordinationRole column:
--   value must be one of the following:
--   SUBPROCESS_COORDINATOR  = 0;
--   SUBPROCESS_PARICIPANT   = 1;

CREATE TABLE AeCoordination
(
   CoordinationPk INT NOT NULL,
   CoordinationType VARCHAR(255) NOT NULL,
   CoordinationRole INT NOT NULL,
   CoordinationId VARCHAR(255) NOT NULL,
   State VARCHAR(255) NOT NULL,
   ProcessId INT NOT NULL,
   LocationPath VARCHAR2(4000) NOT NULL,
   CoordinationDocument CLOB,
   StartDate TIMESTAMP,
   ModifiedDate TIMESTAMP,
   FOREIGN KEY (ProcessId) REFERENCES AeProcess(ProcessId) ON DELETE CASCADE,
   PRIMARY KEY (CoordinationPk)   
)
;

CREATE INDEX AeCoordByCoordId ON AeCoordination(CoordinationId, ProcessId)
;
CREATE INDEX AeCoordByProcessId ON AeCoordination(ProcessId)
;

-- -----------------------------------------------------------------------------
-- TransmissionTracker table - stores transmission id and data needed for durable invokes and durable reply.
-- -----------------------------------------------------------------------------
CREATE TABLE AeTransmissionTracker
(
   TransmissionId INT NOT NULL,
   State INT NOT NULL,
   MessageId  VARCHAR(255),
   PRIMARY KEY (TransmissionId)
)
;

-- ------------------------------------------------------------------------
-- AeProcessAttachment - Attachments accociated to processes
-- ------------------------------------------------------------------------
CREATE TABLE AeProcessAttachment (
   AttachmentGroupId INT NOT NULL,
   ProcessId INT,
   PRIMARY KEY (AttachmentGroupId),
   FOREIGN Key (ProcessId) REFERENCES AeProcess (ProcessId) ON DELETE CASCADE
)
;

-- ------------------------------------------------------------------------
-- AeAttachment - Attachment Item Entries (Headers and Content)
-- ------------------------------------------------------------------------
CREATE TABLE AeAttachment (
   AttachmentGroupId INT NOT NULL,
   AttachmentItemId INT NOT NULL,
   AttachmentHeader CLOB,
   AttachmentContent BLOB,
   PRIMARY KEY (AttachmentItemId),
   FOREIGN Key (AttachmentGroupId) REFERENCES AeProcessAttachment (AttachmentGroupId) ON DELETE CASCADE
)
;

COMMIT
;

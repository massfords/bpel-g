//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/process/AeXMLDBFilteredProcessListQueryBuilder.java,v 1.4 2008/02/07 18:47:30 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.process;

import java.text.MessageFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.impl.AeSuspendReason;
import org.activebpel.rt.bpel.impl.list.AeProcessFilter;
import org.activebpel.rt.bpel.impl.list.IAeListingFilter;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBConfig;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBQueryBuilder;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBStorageImpl;
import org.activebpel.rt.xml.schema.AeSchemaDateTime;

/**
 * A filtered process list query builder.
 */
public class AeXMLDBFilteredProcessListQueryBuilder extends AeXMLDBQueryBuilder
{
   /** The min process ID to consider when deleting. */
   private int mMinProcessID = -1;
   /** The max process ID to consider when deleting. */
   private int mMaxProcessID = -1;

   /**
    * Constructs a process list query builder.
    * 
    * @param aFilter
    * @param aXMLDBConfig
    * @param aStorageImpl
    */
   public AeXMLDBFilteredProcessListQueryBuilder(IAeListingFilter aFilter, AeXMLDBConfig aXMLDBConfig,
         IAeXMLDBStorageImpl aStorageImpl)
   {
      super(aFilter, aXMLDBConfig, AeXMLDBProcessStateStorageProvider.CONFIG_PREFIX,
            IAeProcessConfigKeys.GET_PROCESS_LIST, aStorageImpl);
   }
   
   /**
    * Override the config key to use for the query.
    * 
    * @param aConfigKey
    */
   public void setConfigKey(String aConfigKey)
   {
      setKey(aConfigKey);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBQueryBuilder#getDeletedDocumentType()
    */
   public String getDeletedDocumentType()
   {
      return "AeProcess"; //$NON-NLS-1$
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBQueryBuilder#getWhere()
    */
   protected String getWhere() throws AeException
   {
      AeProcessFilter filter = (AeProcessFilter) getFilter();

      List andClauses = new LinkedList();
      appendAndConditions(filter, andClauses);
      
      return joinAndClauseList(andClauses);
   }

   /**
    * Creates and appends the and conditions to the List of and conditions.
    * 
    * @param aFilter
    * @param aAndClauses
    */
   protected void appendAndConditions(AeProcessFilter aFilter, List aAndClauses) throws AeException
   {
      // Process state condition
      switch (aFilter.getProcessState())
      {
         case AeProcessFilter.STATE_COMPLETED:
            aAndClauses.add("$proc/ProcessState = " + IAeBusinessProcess.PROCESS_COMPLETE); //$NON-NLS-1$
            break;
         case AeProcessFilter.STATE_COMPENSATABLE:
            aAndClauses.add("$proc/ProcessState = " + IAeBusinessProcess.PROCESS_COMPENSATABLE); //$NON-NLS-1$
            break;
         case AeProcessFilter.STATE_RUNNING:
            aAndClauses.add("$proc/ProcessState = " + IAeBusinessProcess.PROCESS_RUNNING); //$NON-NLS-1$
            break;
         case AeProcessFilter.STATE_FAULTED:
            aAndClauses.add("$proc/ProcessState = " + IAeBusinessProcess.PROCESS_FAULTED); //$NON-NLS-1$
            break;
         case AeProcessFilter.STATE_SUSPENDED:
            aAndClauses.add("$proc/ProcessState = " + IAeBusinessProcess.PROCESS_SUSPENDED); //$NON-NLS-1$
            break;
         case AeProcessFilter.STATE_SUSPENDED_FAULTING:
            aAndClauses.add("$proc/ProcessState = " + IAeBusinessProcess.PROCESS_SUSPENDED); //$NON-NLS-1$
            aAndClauses.add("$proc/ProcessStateReason = " + new Integer(AeSuspendReason.SUSPEND_CODE_AUTOMATIC)); //$NON-NLS-1$
            break;
         case AeProcessFilter.STATE_SUSPENDED_PROGRAMMATIC:
            aAndClauses.add("$proc/ProcessState = " + IAeBusinessProcess.PROCESS_SUSPENDED); //$NON-NLS-1$
            aAndClauses.add("$proc/ProcessStateReason = " + new Integer(AeSuspendReason.SUSPEND_CODE_LOGICAL)); //$NON-NLS-1$
            break;
         case AeProcessFilter.STATE_SUSPENDED_MANUAL:
            aAndClauses.add("$proc/ProcessState = " + IAeBusinessProcess.PROCESS_SUSPENDED); //$NON-NLS-1$
            aAndClauses.add("$proc/ProcessStateReason = " + new Integer(AeSuspendReason.SUSPEND_CODE_MANUAL)); //$NON-NLS-1$
            break;
         case AeProcessFilter.STATE_SUSPENDED_INVOKE_RECOVERY:
            aAndClauses.add("$proc/ProcessState = " + IAeBusinessProcess.PROCESS_SUSPENDED); //$NON-NLS-1$
            aAndClauses.add("$proc/ProcessStateReason = " + new Integer(AeSuspendReason.SUSPEND_CODE_INVOKE_RECOVERY)); //$NON-NLS-1$
            break;
         case AeProcessFilter.STATE_COMPLETED_OR_FAULTED:
            Object [] params = {
               new Integer(IAeBusinessProcess.PROCESS_COMPLETE),
               new Integer(IAeBusinessProcess.PROCESS_FAULTED)
            };
            aAndClauses.add(MessageFormat.format("($proc/ProcessState = {0,number,#} or $proc/ProcessState = {1,number,#})", params)); //$NON-NLS-1$
            break;
         case AeProcessFilter.STATE_RUNNING_OR_SUSPENDED:
            aAndClauses.add(
                  "(" + //$NON-NLS-1$
                  "$proc/ProcessState = " + IAeBusinessProcess.PROCESS_RUNNING +  //$NON-NLS-1$
                  " or $proc/ProcessState = " + IAeBusinessProcess.PROCESS_SUSPENDED +  //$NON-NLS-1$
                  ")" //$NON-NLS-1$
            );
            break;
         default:
            break;
      }

      // Process name condition
      if (aFilter.getProcessName() != null)
      {
         appendStringCondition("$proc/ProcessName/LocalPart", aFilter.getProcessName().getLocalPart(), aAndClauses); //$NON-NLS-1$ 
      }
      // Process complete start time condition
      if (aFilter.getProcessCompleteStart() != null)
      {
         AeSchemaDateTime sdt = new AeSchemaDateTime(aFilter.getProcessCompleteStart());
         aAndClauses.add("$proc/EndDate >= xsd:dateTime(\"" + sdt.toString() + "\")"); //$NON-NLS-1$ //$NON-NLS-2$
      }
      // Process complete end time condition
      if (aFilter.getProcessCompleteEnd() != null)
      {
         AeSchemaDateTime sdt = new AeSchemaDateTime(aFilter.getProcessCompleteEndNextDay());
         aAndClauses.add("$proc/EndDate <= xsd:dateTime(\"" + sdt.toString() + "\")"); //$NON-NLS-1$ //$NON-NLS-2$
      }

      // Process created start time condition
      if (aFilter.getProcessCreateStart() != null)
      {
         AeSchemaDateTime sdt = new AeSchemaDateTime(aFilter.getProcessCreateStart());
         aAndClauses.add("$proc/StartDate >= xsd:dateTime(\"" + sdt.toString() + "\")"); //$NON-NLS-1$ //$NON-NLS-2$
      }
      // Process created end time condition
      if (aFilter.getProcessCreateEnd() != null)
      {
         AeSchemaDateTime sdt = new AeSchemaDateTime(aFilter.getProcessCreateEndNextDay());
         aAndClauses.add("$proc/StartDate <= xsd:dateTime(\"" + sdt.toString() + "\")"); //$NON-NLS-1$ //$NON-NLS-2$
      }

      // Handle the planId specified in the filter
      int planId = aFilter.getPlanId();
      if (planId != 0)
      {
         aAndClauses.add("$proc/PlanID = " + planId); //$NON-NLS-1$
      }
      
      // Handle the endDate in the filter, when deletableDate > endDate, it can be deleted, where deletableDate = currentDeleteDate - retentationDays.
      Date deletableDate = aFilter.getDeletableDate();
      if (deletableDate != null)
      {
         AeSchemaDateTime sdt = new AeSchemaDateTime(deletableDate);
         aAndClauses.add("$proc/EndDate <= xsd:dateTime(\"" + sdt.toString() + "\")"); //$NON-NLS-1$ //$NON-NLS-2$
      }

      // Handle the deleteRange of processes specified in the filter
      long[] processIdRange = aFilter.getProcessIdRange();
      if ( processIdRange != null )
      {
         Long fromIndex = new Long(processIdRange[0]);
         Long toIndex = new Long(processIdRange[1]);
         aAndClauses.add("$procId >= " + fromIndex + " and $procId <= " + toIndex); //$NON-NLS-1$ //$NON-NLS-2$
      }

      if (getMinProcessID() != -1)
      {
         aAndClauses.add("$procId >= " + getMinProcessID()); //$NON-NLS-1$
      }
      if (getMaxProcessID() != -1)
      {
         aAndClauses.add("$procId <= " + getMaxProcessID()); //$NON-NLS-1$
      }
   }

   /**
    * @return Returns the maxProcessID.
    */
   protected int getMaxProcessID()
   {
      return mMaxProcessID;
   }
   
   /**
    * @param aMaxProcessID The maxProcessID to set.
    */
   public void setMaxProcessID(int aMaxProcessID)
   {
      mMaxProcessID = aMaxProcessID;
   }
   
   /**
    * @return Returns the minProcessID.
    */
   protected int getMinProcessID()
   {
      return mMinProcessID;
   }
   
   /**
    * @param aMinProcessID The minProcessID to set.
    */
   public void setMinProcessID(int aMinProcessID)
   {
      mMinProcessID = aMinProcessID;
   }
}

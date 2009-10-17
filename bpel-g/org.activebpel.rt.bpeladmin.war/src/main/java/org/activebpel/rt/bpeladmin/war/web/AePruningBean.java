//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/AePruningBean.java,v 1.5 2005/02/08 15:38:09 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web; 

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpeladmin.war.AeMessages;
import org.activebpel.rt.util.AeUtil;

/**
 * Provides a facility for deleting data from the database. The user selects a
 * criteria, hits the delete button, and then gets a count of how many rows will
 * be affected and then must either confirm the deletions or cancel them.
 */
public abstract class AePruningBean extends AeAbstractAdminBean
{

   /** Values for <code>mPruneCommand</code>. */
   protected static final int PRUNE_COMMAND_NULL = 0;
   protected static final int PRUNE_COMMAND_CANCEL = 1;
   protected static final int PRUNE_COMMAND_DELETE = 2;
   protected static final int PRUNE_COMMAND_QUERY = 3;
   /** The command to perform. */
   private int mPruneCommand;
   /** Whether a prune request is pending and requires user confirmation. */
   private boolean mPrunePending;
   /** Whether the pending request is valid. */
   protected boolean mPruneValid;
   /** Whether persistent storage is ready for use. */
   private boolean mStorageAvailable;
   
   /**
    * No arg ctor for JavaBean compliance. 
    */
   public AePruningBean()
   {
      setStorageAvailable(AeEngineFactory.isPersistentStoreReadyForUse());
   }
   
   /**
    * Returns the command to perform.
    */
   protected int getPruneCommand()
   {
      return mPruneCommand;
   }
   
   /**
    * Returns the flag indicating whether a prune request is pending and
    * requires user confirmation.
    */
   public boolean isPrunePending()
   {
      return mPrunePending;
   }
   
   /**
    * Returns flag indicating whether persistent storage is ready for use.
    */
   public boolean isStorageAvailable()
   {
      return mStorageAvailable;
   }
   
   /**
    * Sets the command to perform.
    */
   protected void setPruneCommand(int aPruneCommand)
   {
      if (getPruneCommand() == PRUNE_COMMAND_NULL)
      {
         mPruneCommand = aPruneCommand;
      }
      else
      {
         // This should not happen.
         AeException.logError(new Exception(), AeMessages.getString("AePruningBean.ERROR_0")); //$NON-NLS-1$
      }
   }
   
   /**
    * Sets the current command to <code>PRUNE_COMMAND_CANCEL</code> if the
    * specified argument is non-empty.
    */
   public void setPruneCommandCancel(String aPruneCommandCancel)
   {
      if (AeUtil.notNullOrEmpty(aPruneCommandCancel))
      {
         setPruneCommand(PRUNE_COMMAND_CANCEL);
      }
   }
   
   /**
    * Sets the current command to <code>PRUNE_COMMAND_DELETE</code> if the
    * specified argument is non-empty.
    */
   public void setPruneCommandDelete(String aPruneCommandDelete)
   {
      if (AeUtil.notNullOrEmpty(aPruneCommandDelete))
      {
         setPruneCommand(PRUNE_COMMAND_DELETE);
      }
   }
   
   /**
    * Sets the current command to <code>PRUNE_COMMAND_QUERY</code> if the
    * specified argument is non-empty.
    */
   public void setPruneCommandQuery(String aPruneCommandQuery)
   {
      if (AeUtil.notNullOrEmpty(aPruneCommandQuery))
      {
         setPruneCommand(PRUNE_COMMAND_QUERY);
      }
   }
   
   /**
    * Sets the flag indicating whether a prune request is pending and requires
    * user confirmation.
    */
   protected void setPrunePending(boolean aPrunePending)
   {
      mPrunePending = aPrunePending;
   }
   
   /**
    * Sets the flag indicating whether the pending request is valid.
    */
   protected void setPruneValid(boolean aPruneValid)
   {
      mPruneValid = aPruneValid;
   }
 
   /**
    * Returns the flag indicating whether the pending request is valid.
    */
   public boolean isPruneValid()
   {
      return mPruneValid;
   }
   
   /**
    * Sets flag indicating whether persistent storage is ready for use.
    */
   protected void setStorageAvailable(boolean aStorageAvailable)
   {
      mStorageAvailable = aStorageAvailable;
   }

   /**
    * Indicates that all updates have taken place and this bean can process its
    * inputs.
    *
    * @param aIgnored This value is ignored.
    */
   public void setFinished(boolean aIgnored)
   {
      setPrunePending(false);
      setPruneValid(false);
   
      switch (getPruneCommand())
      {
         case PRUNE_COMMAND_QUERY:
            executeQuery();
            break;
   
         case PRUNE_COMMAND_DELETE:
            executeDelete();
            break;
   
         case PRUNE_COMMAND_NULL:
         case PRUNE_COMMAND_CANCEL:
         default:
            executeCancel();
            break;
      }
   }

   /**
    * Called from the setFinished() method when the command is to cancel the
    * operation.
    */
   protected void executeCancel()
   {
      // do nothing.
   }

   /**
    * Gets called from the setFinished() method when the command is to delete
    * the resources from storage that match the specified criteria. 
    */
   protected abstract void executeDelete();

   /**
    * Gets called from the setFinished() method when the command is to
    * query the number of resources that match the specified criteria. 
    */
   protected abstract void executeQuery();
}
 
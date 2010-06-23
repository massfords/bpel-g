//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/filters/AeSQLReceiverFilter.java,v 1.2 2006/02/10 21:51:14 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql.filters;

import org.activebpel.rt.bpel.impl.list.AeMessageReceiverFilter;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLConfig;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLFilter;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeSQLQueueStorageProvider;
import org.activebpel.rt.util.AeUtil;


/**
 * Wraps the AeMessageReceiverFilter and uses its selection criteria to format the select statement and the
 * matching criteria parameters.
 */
public class AeSQLReceiverFilter extends AeSQLFilter
{
   /** Query key. */
   private static final String SQL_GET_MESSAGE_RECEIVERS = "GetQueuedMessageReceivers"; //$NON-NLS-1$
   private static final String SQL_GET_MESSAGE_RECEIVERS_WHERE = "GetQueuedMessageReceiversWhere"; //$NON-NLS-1$

   /** Group By clause. */
   private static final String SQL_MESSAGE_RECEIVERS_ORDER_BY = "GetQueuedMessageReceiversOrderBy"; //$NON-NLS-1$

   /** Column constants. */
   private static final String SQL_PROCESS_ID = "AeQueuedReceive.ProcessId"; //$NON-NLS-1$
   private static final String SQL_PARTNER_LINK_NAME = "AeQueuedReceive.PartnerLinkName"; //$NON-NLS-1$
   private static final String SQL_OPERATION = "AeQueuedReceive.Operation"; //$NON-NLS-1$
   private static final String SQL_PORT_TYPE_NAMESPACE = "AeQueuedReceive.PortTypeNamespace"; //$NON-NLS-1$
   private static final String SQL_PORT_TYPE_LOCAL_PART = "AeQueuedReceive.PortTypeLocalPart"; //$NON-NLS-1$

   /**
    * Constructor.
    * @param aFilter The selection criteria.
    */
   public AeSQLReceiverFilter(AeMessageReceiverFilter aFilter, AeSQLConfig aConfig) throws AeStorageException
   {
      super(aFilter, aConfig, AeSQLQueueStorageProvider.SQLSTATEMENT_PREFIX);
      setOrderBy(getSQLStatement(SQL_MESSAGE_RECEIVERS_ORDER_BY));
      setSelectClause(getSQLStatement(SQL_GET_MESSAGE_RECEIVERS));
   }

   /**
    * Builds the sql statement.
    */
   protected void processFilter()
   {
      AeMessageReceiverFilter filter = (AeMessageReceiverFilter) getFilter();
      
      appendCondition(getSQLStatement(SQL_GET_MESSAGE_RECEIVERS_WHERE));
      
      if( filter != null )
      {
         if( !filter.isNullProcessId() )
         {
            appendCondition( SQL_PROCESS_ID + " = ?", new Long( filter.getProcessId() ) ); //$NON-NLS-1$
         }
         
         checkStringParam( filter.getPartnerLinkName(), SQL_PARTNER_LINK_NAME );
         checkStringParam( filter.getOperation(), SQL_OPERATION );            

         if( filter.getPortType() != null )
         {
            checkStringParam( filter.getPortType().getNamespaceURI(), SQL_PORT_TYPE_NAMESPACE );            
            checkStringParam( filter.getPortType().getLocalPart(), SQL_PORT_TYPE_LOCAL_PART );            
         }
      }
   }

   /**
    * Convenience method for adding where conditions to the 
    * sql.
    * @param aValue The value to match on.
    * @param aColumnName The corresponding column name.
    */
   protected void checkStringParam( String aValue, String aColumnName )
   {
      if( !AeUtil.isNullOrEmpty(aValue) )
      {
         appendCondition( aColumnName + " = ?", aValue ); //$NON-NLS-1$
      }
   }
}

//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/queue/AeXMLDBFilteredMessageReceiverListQueryBuilder.java,v 1.1 2007/08/17 00:40:55 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.queue;

import java.util.LinkedList;
import java.util.List;

import org.activebpel.rt.bpel.impl.list.AeMessageReceiverFilter;
import org.activebpel.rt.bpel.impl.list.IAeListingFilter;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBConfig;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBQueryBuilder;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBStorageImpl;
import org.activebpel.rt.util.AeUtil;

/**
 * A filtered queued receive list query builder.
 */
public class AeXMLDBFilteredMessageReceiverListQueryBuilder extends AeXMLDBQueryBuilder
{
   /**
    * Constructs a filtered alarm list query builder.
    * 
    * @param aFilter
    * @param aConfig
    * @param aStorageImpl
    */
   public AeXMLDBFilteredMessageReceiverListQueryBuilder(IAeListingFilter aFilter, AeXMLDBConfig aConfig,
         IAeXMLDBStorageImpl aStorageImpl)
   {
      super(aFilter, aConfig, AeXMLDBQueueStorageProvider.CONFIG_PREFIX, IAeQueueConfigKeys.GET_QUEUED_RECEIVES_FILTERED, aStorageImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBQueryBuilder#getWhere()
    */
   protected String getWhere()
   {
      AeMessageReceiverFilter filter = (AeMessageReceiverFilter) getFilter();
      return joinAndClauseList(getWhereFromFilter(filter));
   }

   /**
    * This creates a list of and clauses based on the passed filter. Derived classes
    * can extend here to add their own 'and' clauses.
    * @param aFilter
    * @return List of and clauses generated from filter.
    */
   protected List getWhereFromFilter(AeMessageReceiverFilter aFilter)
   {
      List andClauses = new LinkedList();
      if (!aFilter.isNullProcessId())
         andClauses.add("$rec/ProcessID = " + aFilter.getProcessId()); //$NON-NLS-1$
      if (!AeUtil.isNullOrEmpty(aFilter.getPartnerLinkName()))
         andClauses.add("$rec/PartnerLinkName = '" + aFilter.getPartnerLinkName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
      if (aFilter.getPortType() != null)
         andClauses.add("$rec/PortType/LocalPart = '" + aFilter.getPortType().getLocalPart() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
      if (!AeUtil.isNullOrEmpty(aFilter.getOperation()))
         andClauses.add("$rec/Operation = '" + aFilter.getOperation() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
      return andClauses;
   }

}

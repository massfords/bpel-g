//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/registry/AeAbstractBpelWriterRegistry.java,v 1.3 2007/11/15 22:31:11 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io.registry;

import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.io.IAeBpelClassConstants;
import org.activebpel.rt.bpel.def.io.IAeBpelLegacyConstants;
import org.activebpel.rt.bpel.def.io.writers.def.AeDispatchWriter;
import org.activebpel.rt.xml.def.io.writers.AeBaseDefWriterRegistry;
import org.activebpel.rt.xml.def.io.writers.IAeDefWriter;
import org.activebpel.rt.xml.def.io.writers.IAeDefWriterFactory;

/**
 * Abstract writer registry for bpel defs
 */
public abstract class AeAbstractBpelWriterRegistry extends AeBaseDefWriterRegistry implements
      IAeBpelClassConstants, IAeBPELConstants
{
   /**
    * Ctor
    * @param aDefaultNamespace
    * @param aFactory
    */
   public AeAbstractBpelWriterRegistry(String aDefaultNamespace, IAeDefWriterFactory aFactory)
   {
      super(aDefaultNamespace, aFactory);
   }

   /**
    * inits the registry with mappings for def class to element qname. Contains
    * entries that are common to all versions of bpel.
    *
    * @see org.activebpel.rt.xml.def.io.writers.AeBaseDefWriterRegistry#init()
    */
   protected void init()
   {
      super.init();

      registerWriter( PROCESS_CLASS,              TAG_PROCESS );

      // pulled up from bpws in order to support conversions from bpws to wsbpel
      registerWriter( PARTNERS_CLASS,             IAeBpelLegacyConstants.TAG_PARTNERS );

      registerWriter( ACTIVITY_ASSIGN_CLASS,      TAG_ASSIGN );
      registerWriter( ACTIVITY_COMPENSATE_CLASS,  TAG_COMPENSATE );
      registerWriter( ACTIVITY_EMPTY_CLASS,       TAG_EMPTY );
      registerWriter( ACTIVITY_FLOW_CLASS,        TAG_FLOW );
      registerWriter( ACTIVITY_INVOKE_CLASS,      TAG_INVOKE );
      registerWriter( ACTIVITY_PICK_CLASS,        TAG_PICK );
      registerWriter( ACTIVITY_RECEIVE_CLASS,     TAG_RECEIVE );
      registerWriter( ACTIVITY_REPLY_CLASS,       TAG_REPLY );
      registerWriter( ACTIVITY_SCOPE_CLASS,       TAG_SCOPE );
      registerWriter( ACTIVITY_SEQUENCE_CLASS,    TAG_SEQUENCE );
      registerWriter( ACTIVITY_THROW_CLASS,       TAG_THROW );
      registerWriter( ACTIVITY_WAIT_CLASS,        TAG_WAIT );
      registerWriter( ACTIVITY_WHILE_CLASS,       TAG_WHILE );
      registerWriter( ACTIVITY_CONTINUE_CLASS, createWriter(IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ACTIVITY, TAG_CONTINUE));
      registerWriter( ACTIVITY_BREAK_CLASS,    createWriter(IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ACTIVITY, TAG_BREAK));
      registerWriter( ACTIVITY_SUSPEND_CLASS,  createWriter(IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ACTIVITY, TAG_SUSPEND));

      registerWriter( LINK_CLASS,                 TAG_LINK );
      registerWriter( TARGET_CLASS,               TAG_TARGET );
      registerWriter( SOURCE_CLASS,               TAG_SOURCE );
      registerWriter( ON_ALARM_CLASS,             TAG_ON_ALARM );
      registerWriter( ON_MESSAGE_CLASS,           TAG_ON_MESSAGE );
      registerWriter( ASSIGN_COPY_CLASS,          TAG_COPY );
      registerWriter( CORRELATION_CLASS,          TAG_CORRELATION );
      registerWriter( CATCH_CLASS,                TAG_CATCH );
      registerWriter( CATCH_ALL_CLASS,            TAG_CATCH_ALL );

      registerWriter( ASSIGN_FROM_CLASS,          TAG_FROM );
      registerWriter( ASSIGN_TO_CLASS,            TAG_TO );

      registerWriter( PARTNER_LINK_CLASS,         TAG_PARTNER_LINK );
      registerWriter( VARIABLE_CLASS,             TAG_VARIABLE );
      registerWriter( EVENT_HANDLERS_CLASS,       TAG_EVENT_HANDLERS );
      registerWriter( CORRELATION_SET_CLASS,      TAG_CORRELATION_SET );
      registerWriter( CORRELATIONS_CLASS,         TAG_CORRELATIONS );
      registerWriter( LINKS_CLASS,                TAG_LINKS );
      registerWriter( VARIABLES_CLASS,            TAG_VARIABLES );
      registerWriter( CORRELATION_SETS_CLASS,     TAG_CORRELATION_SETS );

      registerWriter( PARNTER_LINKS_CLASS,        TAG_PARTNER_LINKS );
      registerWriter( FAULT_HANDLERS_CLASS,       TAG_FAULT_HANDLERS );
      registerWriter( COMPENSATION_HANDLER_CLASS, TAG_COMPENSATION_HANDLER );
   }

   /**
    * Creates a dispatch writer with the given tag name (in the given namespace).
    *
    * @param aNamespace
    * @param aTagName
    */
   protected IAeDefWriter createWriter(String aNamespace, String aTagName)
   {
      return new AeDispatchWriter(aNamespace, aTagName, getWriterFactory());
   }
}

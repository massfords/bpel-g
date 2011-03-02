//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/registry/AeAbstractBpelReaderRegistry.java,v 1.4 2007/11/15 22:31:11 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.io.IAeBpelClassConstants;
import org.activebpel.rt.xml.def.io.readers.AeDefReaderRegistry;
import org.activebpel.rt.xml.def.io.readers.IAeDefReader;
import org.activebpel.rt.xml.def.io.readers.IAeReaderFactory;

/**
 * Abstract registry for reading BPEL defs from xml
 */
public abstract class AeAbstractBpelReaderRegistry extends AeDefReaderRegistry
   implements IAeBpelClassConstants, IAeBPELConstants
{
   /**
    * Ctor
    * @param aDefaultNamespace
    * @param aFactory
    */
   public AeAbstractBpelReaderRegistry(String aDefaultNamespace, IAeReaderFactory aFactory)
   {
      super(aDefaultNamespace, aFactory);
   }

   /**
    * @see org.activebpel.rt.xml.def.io.readers.AeDefReaderRegistry#init()
    */
   protected void init()
   {
      super.init();

      initBpelActivities();
      initCommonActivityChildrenElements();
      initExtensionActivityRegistry();
   }

   /**
    * Populates the extension activity registry with entries.
    */
   protected void initExtensionActivityRegistry()
   {
   }

   /**
    * Populates the parent/child mappings for elements that are common to all
    * activities. In bpws, this is target and source. In wsbpel, this is
    * targets and sources.
    */
   protected void initCommonActivityChildrenElements()
   {
   }

   /**
    * Gets a list of the common activity containers for bpws and wsbpel.
    */
   protected List getActivityContainers()
   {
      Class[] containers =
      {
            ACTIVITY_FLOW_CLASS,
            ACTIVITY_FOREACH_CLASS,
            ACTIVITY_SCOPE_CLASS,
            ACTIVITY_SEQUENCE_CLASS,
            ACTIVITY_WHILE_CLASS,
            ON_ALARM_CLASS,
            ON_EVENT_CLASS,
            ON_MESSAGE_CLASS,
            CATCH_ALL_CLASS,
            CATCH_CLASS,
            COMPENSATION_HANDLER_CLASS,
            ELSE_CLASS,
            ELSEIF_CLASS,
            IF_CLASS,
            SCOPE_CLASS,
            PROCESS_CLASS,
      };

      return Arrays.asList(containers);
   }

   /**
    * Inner class used to model a mapping of an element QName to a reader.
    */
   protected static class AeRegistryMapping
   {
      /** name of the element */
      private QName mName;
      /** reader */
      private IAeDefReader mReader;

      /**
       * Ctor
       * @param aName
       * @param aReader
       */
      public AeRegistryMapping(QName aName, IAeDefReader aReader)
      {
         mName = aName;
         mReader = aReader;
      }

      /**
       * @return the name
       */
      public QName getName()
      {
         return mName;
      }

      /**
       * @return the reader
       */
      public IAeDefReader getReader()
      {
         return mReader;
      }
   }

   /**
    * Returns a list of {@link AeRegistryMapping} for all of the activities
    * supported by bpel. bpws and wsbpel override this method to call super
    * and then add their own mappings. This list contains entries common to all
    * versions of bpel.
    */
   protected List getBpelActivityMappings()
   {
      List list = new ArrayList();
      list.add(new AeRegistryMapping(makeDefaultQName(TAG_ASSIGN), createReader(ACTIVITY_ASSIGN_CLASS)));
      list.add(new AeRegistryMapping(makeDefaultQName(TAG_EMPTY), createReader(ACTIVITY_EMPTY_CLASS)));
      list.add(new AeRegistryMapping(makeDefaultQName(TAG_FLOW), createReader(ACTIVITY_FLOW_CLASS) ));
      list.add(new AeRegistryMapping(makeDefaultQName(TAG_INVOKE), createReader(ACTIVITY_INVOKE_CLASS) ));
      list.add(new AeRegistryMapping(makeDefaultQName(TAG_PICK), createReader(ACTIVITY_PICK_CLASS) ));
      list.add(new AeRegistryMapping(makeDefaultQName(TAG_RECEIVE), createReader(ACTIVITY_RECEIVE_CLASS) ));
      list.add(new AeRegistryMapping(makeDefaultQName(TAG_REPLY), createReader(ACTIVITY_REPLY_CLASS) ));
      list.add(new AeRegistryMapping(makeDefaultQName(TAG_SCOPE), createReader(ACTIVITY_SCOPE_CLASS) ));
      list.add(new AeRegistryMapping(makeDefaultQName(TAG_SEQUENCE), createReader(ACTIVITY_SEQUENCE_CLASS) ));
      list.add(new AeRegistryMapping(makeDefaultQName(TAG_THROW), createReader(ACTIVITY_THROW_CLASS) ));
      list.add(new AeRegistryMapping(makeDefaultQName(TAG_WAIT), createReader(ACTIVITY_WAIT_CLASS) ));
      list.add(new AeRegistryMapping(makeDefaultQName(TAG_WHILE), createReader(ACTIVITY_WHILE_CLASS) ));
      // Our custom BPEL extensions
      list.add(new AeRegistryMapping(new QName(IAeBPELConstants.ABX_2_0_NAMESPACE_URI, TAG_BREAK), createReader(ACTIVITY_BREAK_CLASS) ));
      list.add(new AeRegistryMapping(new QName(IAeBPELConstants.ABX_2_0_NAMESPACE_URI, TAG_CONTINUE), createReader(ACTIVITY_CONTINUE_CLASS) ));
      list.add(new AeRegistryMapping(new QName(IAeBPELConstants.ABX_2_0_NAMESPACE_URI, TAG_SUSPEND), createReader(ACTIVITY_SUSPEND_CLASS) ));
      list.add(new AeRegistryMapping(new QName(IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ACTIVITY, TAG_BREAK), createReader(ACTIVITY_BREAK_CLASS) ));
      list.add(new AeRegistryMapping(new QName(IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ACTIVITY, TAG_CONTINUE), createReader(ACTIVITY_CONTINUE_CLASS) ));
      list.add(new AeRegistryMapping(new QName(IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ACTIVITY, TAG_SUSPEND), createReader(ACTIVITY_SUSPEND_CLASS) ));

      return list;
   }

   /**
    * Adds mappings for all combinations of container and child activity
    */
   protected void initBpelActivities()
   {
      List bpelActivities = getBpelActivityMappings();

      for (Iterator outerIter = getActivityContainers().iterator(); outerIter.hasNext();)
      {
         Class container = (Class) outerIter.next();

         for (Iterator innerIter = bpelActivities.iterator(); innerIter.hasNext();)
         {
            AeRegistryMapping mapping = (AeRegistryMapping) innerIter.next();

            registerReader(container, mapping.getName(), mapping.getReader());
         }
      }
   }

   /**
    * Inits the main parent def/qname mappings
    *
    * @see org.activebpel.rt.xml.def.io.readers.AeDefReaderRegistry#initParentRegistry()
    */
   protected void initParentRegistry()
   {
      // maps readers for children of process element
      // exclusive of activity (which is handled sep)
      registerReader(PROCESS_CLASS,  TAG_VARIABLES,            createReader(VARIABLES_CLASS));
      registerReader(PROCESS_CLASS,  TAG_FAULT_HANDLERS,       createReader(FAULT_HANDLERS_CLASS));
      registerReader(PROCESS_CLASS,  TAG_EVENT_HANDLERS,       createReader(EVENT_HANDLERS_CLASS));
      registerReader(PROCESS_CLASS,  TAG_PARTNER_LINKS,        createReader(PARTNER_LINKS_CLASS));
      registerReader(PROCESS_CLASS,  TAG_CORRELATION_SETS,     createReader(CORRELATION_SETS_CLASS));

      // map readers for children of copy element
      registerReader(ACTIVITY_ASSIGN_CLASS, TAG_COPY,  createReader(ASSIGN_COPY_CLASS));
      registerReader(ASSIGN_COPY_CLASS, TAG_TO,        createReader(ASSIGN_TO_CLASS));
      registerReader(ASSIGN_COPY_CLASS, TAG_FROM,      createReader(ASSIGN_FROM_CLASS));

      // maps readers for children of pick activity
      // exclusive of standard readers (source/target)
      registerReader(ACTIVITY_PICK_CLASS, TAG_ON_MESSAGE, createReader(ON_MESSAGE_CLASS));
      registerReader(ACTIVITY_PICK_CLASS, TAG_ON_ALARM,   createReader(ON_ALARM_CLASS));

      // maps readers for children of scope activity
      // exclusive of standard readers (source/target)
      registerReader(ACTIVITY_SCOPE_CLASS, TAG_COMPENSATION_HANDLER, createReader(COMPENSATION_HANDLER_CLASS));
      registerReader(ACTIVITY_SCOPE_CLASS, TAG_FAULT_HANDLERS,       createReader(FAULT_HANDLERS_CLASS));
      registerReader(ACTIVITY_SCOPE_CLASS, TAG_VARIABLES,            createReader(VARIABLES_CLASS));
      registerReader(ACTIVITY_SCOPE_CLASS, TAG_CORRELATION_SETS,     createReader(CORRELATION_SETS_CLASS));
      registerReader(ACTIVITY_SCOPE_CLASS, TAG_EVENT_HANDLERS,       createReader(EVENT_HANDLERS_CLASS));

      // maps readers for children of invoke activity
      // exclusive of standard readers (source/target)
      registerReader(ACTIVITY_INVOKE_CLASS, TAG_CATCH,                createReader(CATCH_CLASS));
      registerReader(ACTIVITY_INVOKE_CLASS, TAG_CATCH_ALL,            createReader(CATCH_ALL_CLASS));
      registerReader(ACTIVITY_INVOKE_CLASS, TAG_COMPENSATION_HANDLER, createReader(COMPENSATION_HANDLER_CLASS));
      registerReader(ACTIVITY_INVOKE_CLASS, TAG_CORRELATIONS,         createReader(CORRELATIONS_CLASS));

      // maps readers for children of eventHandlers element
      registerReader(EVENT_HANDLERS_CLASS, TAG_ON_MESSAGE, createReader(ON_EVENT_CLASS)); // treat onMessage as an onEvent in the unified def
      registerReader(EVENT_HANDLERS_CLASS, TAG_ON_ALARM,   createReader(ON_ALARM_CLASS));
      // we're using the same def for process/scope non-alarm events even though these were called onMessage in 1.1
      registerReader(ON_EVENT_CLASS,         TAG_CORRELATIONS,        createReader(CORRELATIONS_CLASS));

      // maps readers for children of faultHandlers element
      registerReader(FAULT_HANDLERS_CLASS, TAG_CATCH,     createReader(CATCH_CLASS));
      registerReader(FAULT_HANDLERS_CLASS, TAG_CATCH_ALL, createReader(CATCH_ALL_CLASS));

      // all of the various class to child element mappings
      registerReader(ACTIVITY_FLOW_CLASS,    TAG_LINKS,               createReader(LINKS_CLASS));
      registerReader(ACTIVITY_RECEIVE_CLASS, TAG_CORRELATIONS,        createReader(CORRELATIONS_CLASS));
      registerReader(ACTIVITY_REPLY_CLASS,   TAG_CORRELATIONS,        createReader(CORRELATIONS_CLASS));

      registerReader(PARNTER_LINKS_CLASS,    TAG_PARTNER_LINK,        createReader(PARTNER_LINK_CLASS));
      registerReader(PARTNER_CLASS,          TAG_PARTNER_LINK,        createReader(PARTNER_LINK_CLASS));
      registerReader(VARIABLES_CLASS,        TAG_VARIABLE,            createReader(VARIABLE_CLASS));

      registerReader(LINKS_CLASS,            TAG_LINK,                createReader(LINK_CLASS));
      registerReader(ON_MESSAGE_CLASS,       TAG_CORRELATIONS,        createReader(CORRELATIONS_CLASS));

      registerReader(CORRELATIONS_CLASS,     TAG_CORRELATION,         createReader(CORRELATION_CLASS));
      registerReader(CORRELATION_SETS_CLASS, TAG_CORRELATION_SET,     createReader(CORRELATION_SET_CLASS));
   }

   /**
    * Populates the mGenericReadersMap with any generic readers.
    *
    * @see org.activebpel.rt.xml.def.io.readers.AeDefReaderRegistry#initGenericElementRegistry()
    */
   protected void initGenericElementRegistry()
   {
      getGenericReadersMap().put( makeDefaultQName(TAG_PROCESS), createReader(PROCESS_CLASS));
   }

}
//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeDefVisitorFactory.java,v 1.6 2007/10/12 16:09:48 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors; 

import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.io.readers.AeBpelLocationPathVisitor;
import org.activebpel.rt.xml.def.IAePathSegmentBuilder;
import org.activebpel.rt.xml.def.io.IAeExtensionRegistry;
import org.activebpel.rt.xml.def.visitors.AeLocationPathVisitor;
import org.activebpel.rt.xml.def.visitors.IAeDefPathVisitor;

/**
 * Factory for creating def visitors 
 */
public abstract class AeDefVisitorFactory implements IAeDefVisitorFactory
{
   /** impl for BPEL4WS 1.1 */
   private static final IAeDefVisitorFactory BPWS_FACTORY = new AeBPWSDefVisitorFactory();
   /** impl for WS-BPEL 2.0 */
   private static final IAeExtensionAwareDefVisitorFactory WSBPEL20_FACTORY = new AeWSBPELDefVisitorFactory();
   /** impl for WS-BPEL 2.0 Abstract Processes */
   private static final IAeExtensionAwareDefVisitorFactory WSBPEL20_ABSTRACT_PROCESS_FACTORY = new AeWSBPELAbstractProcessDefVisitorFactory();
   
   /**
    * Factory method returns a factory specific to the namespace
    * @param aNamespace
    */
   public static IAeDefVisitorFactory getInstance(String aNamespace)
   {
      if (IAeBPELConstants.BPWS_NAMESPACE_URI.equals(aNamespace))
      {
         return BPWS_FACTORY;
      }
      else if (IAeBPELConstants.WSBPEL_2_0_ABSTRACT_NAMESPACE_URI.equals(aNamespace))
      {
         return WSBPEL20_ABSTRACT_PROCESS_FACTORY;
      }
      else
      {
         return WSBPEL20_FACTORY;
      }
   }
   
   /**
    * protected ctor to force static getInstance() usage 
    */
   protected AeDefVisitorFactory()
   {
      super();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createDefPathVisitor()
    */
   public IAeDefPathVisitor createDefPathVisitor()
   {
      IAePathSegmentBuilder segmentBuilder = createPathSegmentBuilder();
      AeLocationPathVisitor pathBuilder = new AeBpelLocationPathVisitor(segmentBuilder);
      return pathBuilder;
   }

   /**
    * If the namespace is WSBPEL 2.0 or WSBPEL Abstract process then delegates call to corresponding factory 
    * @param aNamespace
    * @param aExtensionRegistry
    */
   public static void setExtensionRegistry(String aNamespace, IAeExtensionRegistry aExtensionRegistry)
   {
      if (aNamespace.equals(IAeBPELConstants.BPWS_NAMESPACE_URI))
         throw new UnsupportedOperationException();
      else if (aNamespace.equals(IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI))
         WSBPEL20_FACTORY.setExtensionRegistry(aExtensionRegistry);
      else if (aNamespace.equals(IAeBPELConstants.WSBPEL_2_0_ABSTRACT_NAMESPACE_URI))
         WSBPEL20_ABSTRACT_PROCESS_FACTORY.setExtensionRegistry(aExtensionRegistry);
   }
}
 
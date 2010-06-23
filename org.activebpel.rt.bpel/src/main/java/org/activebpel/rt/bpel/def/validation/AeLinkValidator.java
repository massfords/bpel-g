// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/AeLinkValidator.java,v 1.19 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AeCompensationHandlerDef;
import org.activebpel.rt.bpel.def.AeEventHandlersDef;
import org.activebpel.rt.bpel.def.AeFaultHandlersDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.IAeUncrossableLinkBoundary;
import org.activebpel.rt.bpel.def.activity.AeActivityFlowDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityWhileDef;
import org.activebpel.rt.bpel.def.activity.support.AeLinkDef;
import org.activebpel.rt.bpel.def.activity.support.AeSourceDef;
import org.activebpel.rt.bpel.def.activity.support.AeTargetDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;

/**
 * Validation helper for link collection and testing.
 */
public class AeLinkValidator implements IAeValidationDefs
{
   /** Error reporting instance. */
   private IAeValidationProblemReporter mReporter ;

   /** Map holding link composites to test for validity. */
   private Map mLinkMap = new HashMap();

   /** Map of source composites to test for cycles. */
   private Map mLinkSourcesMap = new HashMap();
   
   /** Process Def. */
   private AeProcessDef mProcessDef;
   
   /**
    * Ctor to set the error reporter.
    *
    * @param aReporter The reporter to set.
    */
   public AeLinkValidator( AeProcessDef aProcessDef, IAeValidationProblemReporter aReporter )
   {
      mProcessDef = aProcessDef;
      setReporter( aReporter );
   }

   /**
    * Get the error reporter for this instance.
    *
    * @return IAeErrorReporter
    */
   public IAeValidationProblemReporter getReporter()
   {
      return mReporter;
   }

   /**
    * Set the error reporter for this instance.
    *
    * @param aReporter The reporter to set.
    */
   public void setReporter(IAeValidationProblemReporter aReporter)
   {
      mReporter = aReporter;
   }

   /**
    * Find the element's enclosing flow, or null if there is no enclosing flow.
    *
    * @return AeActivityFlowDef
    */
   private AeActivityFlowDef getParentFlow( AeBaseDef aChild )
   {
      AeBaseDef parent = aChild.getParent();
      if ( parent != null )
      {
         if ( parent instanceof AeActivityFlowDef )
            return (AeActivityFlowDef)parent ;
         else
            return getParentFlow( parent );
      }

      return null ;
   }

   /**
    * Check for cycles and illegal boundary crosses in the graph's links.
    *
    * Note: this should only be called after the validation visitor has traversed
    * the entire def graph, to ensure that all links and their sources/targets have
    * been resolved.  Wherever this hasn't occurred, the composite will be incomplete,
    * which signifies an erroneously defined link.
    */
   public void checkLinks()
   {
      for ( Iterator iter = getLinks().iterator() ; iter.hasNext() ; )
      {
         AeLinkComposite comp = (AeLinkComposite) iter.next();
         if (comp.isUsed())
         {
            if ( comp.isComplete() )
            {
               checkBoundaryCrossings( comp );
               checkBadTarget( comp );
            }
            else
            {
               getReporter().reportProblem( IAeValidationProblemCodes.BPEL_BAD_LINK_CODE, 
                                             ERROR_BAD_LINK,
                                             new String[] { comp.getLink().getLocationPath() },
                                             comp.getLink());
            }
         }
         else
         {
            getReporter().reportProblem( IAeValidationProblemCodes.BPEL_LINK_NOT_USED_CODE, 
                                          WARNING_LINK_NOT_USED,
                                          new String[] { comp.getLink().getName() },
                                          comp.getLink());
         }
      }

      checkLinkCycles();
   }

   /**
    * Check for cycles in the graph's links.
    */
   private void checkLinkCycles()
   {
      // Find links that participate in control cycles.
      Set cycleLinks = AeLinkCycleVisitor.findCycleLinks(this);

      // Report any such links.
      for (Iterator iter = cycleLinks.iterator(); iter.hasNext(); )
      {
         AeLinkDef link = ((AeLinkComposite)iter.next()).getLink();
         getReporter().reportProblem( IAeValidationProblemCodes.BPEL_LINK_CYCLE_CODE, ERROR_LINK_CYCLE, new String[]{link.getName()}, link );
      }
   }

   /**
    * Returns a string array containing the appropriate error text.
    *
    * @param aParent Node type involved in the error.
    *
    * return String
    */
   private String getCrossingError( AeBaseDef aParent )
   {
      if ( aParent instanceof AeActivityWhileDef )
      {
         return AeMessages.getString("AeLinkValidator.0"); //$NON-NLS-1$
      }
      else if ( aParent instanceof AeActivityScopeDef && ((AeActivityScopeDef)aParent).isIsolated())
      {
         return AeMessages.getString("AeLinkValidator.1"); //$NON-NLS-1$
      }
      else if ( aParent instanceof AeFaultHandlersDef )
      {
         return AeMessages.getString("AeLinkValidator.2"); //$NON-NLS-1$
      }
      else if ( aParent instanceof AeEventHandlersDef )
      {
         return AeMessages.getString("AeLinkValidator.3"); //$NON-NLS-1$
      }
      else if ( aParent instanceof AeCompensationHandlerDef )
      {
         return AeMessages.getString("AeLinkValidator.4"); //$NON-NLS-1$
      }

      return AeMessages.getString("AeLinkValidator.5") + aParent.getClass().toString(); //$NON-NLS-1$
   }
   
   /**
    * Starting with the def passed, keep walking up its parent defs until we
    * encounter a container that cannot be crossed with a link or until we
    * reach the process root. 
    * @param aDef
    * @param aDirection - specifies the direction of the link, either inbound or outbound
    * @return AeBaseDef - the first uncrossable container encountered or null if none
    */
   private AeBaseDef getInnermostUncrossableContainer(AeBaseDef aDef, AeLinkDirection aDirection)
   {
      // start with the parent
      AeBaseDef parent = aDef.getParent();
      while(parent != null)
      {
         if (parent instanceof IAeUncrossableLinkBoundary)
         {
            IAeUncrossableLinkBoundary boundary = (IAeUncrossableLinkBoundary) parent;
            if (aDirection == AeLinkDirection.INBOUND && !boundary.canCrossInbound())
               return parent;
            if (aDirection == AeLinkDirection.OUTBOUND && !boundary.canCrossOutbound())
               return parent;
         }
         parent = parent.getParent();
      }
      return parent;
   }

   /**
    * Verify that a link doesn't cross an uncrossable boundary.  Flag an error
    * if so.
    *
    * @param aLink The link to check.
    */
   private void checkBoundaryCrossings( AeLinkComposite aLink )
   {
      AeActivityDef src = aLink.getSource();
      AeActivityDef target = aLink.getTarget();
      AeBaseDef srcParent = getInnermostUncrossableContainer( src, AeLinkDirection.OUTBOUND );
      AeBaseDef targetParent = getInnermostUncrossableContainer( target, AeLinkDirection.INBOUND );
      
      // if the srcParent is not null, then the src of the link is nested within
      // a container that cannot be crossed. We should report an error unless the
      // destination link is nested within the same container.
      if (srcParent != null && srcParent != targetParent && !isDefNestedWithinContainer(target, srcParent))
      {
         getReporter().reportProblem( IAeValidationProblemCodes.BPEL_LINK_CROSSING_CODE,
                                 ERROR_LINK_CROSSING,
                                 new String[] { aLink.getLink().getName(), getCrossingError( srcParent )},
                                 aLink.getLink());
      }
      // if the targetParent is not null, then the target of the link is nested 
      // within a container that cannot be crossed. We should report an error 
      // unless the destination link is nested within the same container.
      if ( targetParent != null && targetParent != srcParent && !isDefNestedWithinContainer(src, targetParent))
      {
         getReporter().reportProblem( IAeValidationProblemCodes.BPEL_LINK_CROSSING_CODE,
                                 ERROR_LINK_CROSSING,
                                 new String[] { aLink.getLink().getName(),getCrossingError( targetParent )},
                                 aLink.getLink());
      }
   }
   
   /**
    * Returns true if the first def is nested within the second def
    * @param aDef
    * @param aContainerDef
    */
   protected boolean isDefNestedWithinContainer(AeActivityDef aDef, AeBaseDef aContainerDef)
   {
      if (aDef == null || aContainerDef == null)
         return false;
         
      for(AeBaseDef parent = aDef.getParent(); parent != null; parent = parent.getParent())
      {
         if (parent == aContainerDef)
         {
            return true;
         }
      }
      return false;
   }

   /**
    * Verify that link target is not nested in src.
    * @param aLink The link to check.
    */
   private void checkBadTarget( AeLinkComposite aLink )
   {
      AeActivityDef src = aLink.getSource();
      AeActivityDef dst = aLink.getTarget();

      // If the source of this link is a parent of the destination it is an error
      for(AeBaseDef dstModel = dst; dstModel != null; dstModel = dstModel.getParent() )
      {
         if ( dstModel == src )
         {
            getReporter().reportProblem( IAeValidationProblemCodes.BPEL_SCOPE_LINK_CODE, 
                                    ERROR_SCOPE_LINK, 
                                    new String[] {aLink.getLink().getLocationPath()},
                                    aLink.getLink() );
            break;
         }
      }
   }

   /**
    * Find an AeLinkDef by name within an enclosing Flow.
    *
    * @param aLinkName The name of the link to search for.
    * @param aDef The def object 'above' which the search starts.
    *
    * @return AeLinkDef
    */
   protected AeLinkDef findLink( String aLinkName, AeBaseDef aDef )
   {
      AeActivityFlowDef parent = getParentFlow( aDef );
      while ( parent != null )
      {
         for ( Iterator iter = parent.getLinkDefs() ; iter.hasNext() ; )
         {
            AeLinkDef link = (AeLinkDef)iter.next();
            if ( link.getName().equals( aLinkName ))
               return( link );
         }

         parent = getParentFlow( parent );
      }

      return null ;
   }

   /**
    * Returns the Collection of AeLinkComposite instances.
    * 
    * @return Collection
    */
   public Collection getLinks()
   {
      return getLinkMap().values();
   }

   /**
    * Returns the Map used to manage the AeLinkComposite instances.
    *
    * @return Map
    */
   protected Map getLinkMap()
   {
      return mLinkMap ;
   }

   /**
    * Returns the Collection of AeLinkSource instances.
    *
    * @return Collection
    */
   protected Collection getLinkSources()
   {
      return getLinkSourcesMap().values();
   }

   /**
    * Returns the Map used to manage the AeLinkSource instances.
    *
    * @return Map
    */
   protected Map getLinkSourcesMap()
   {
      return mLinkSourcesMap ;
   }

   /**
    * Get the link composite from the map, if there, otherwise create it.
    *
    * @param aLink link to search the map.
    *
    * @return AeLinkComposite
    */
   protected AeLinkComposite getLinkComposite( AeLinkDef aLink )
   {
      AeLinkComposite comp = (AeLinkComposite) getLinkMap().get( aLink );
      if ( comp == null )
      {
         comp = new AeLinkComposite( aLink );
         getLinkMap().put( aLink, comp );
      }

      return comp ;
   }

   /**
    * Add an AeLinkDef to the map.
    *
    * @param aLink The link to add.
    */
   public void addLink( AeLinkDef aLink )
   {
      getLinkComposite(aLink);
   }

   /**
    * Add a source activity to the map.
    *
    * @param aSource The source to add.
    * @param aDef The activity def that owns the source.
    */
   public void addSource( AeSourceDef aSource, AeActivityDef aDef )
   {
      AeLinkDef link = findLink(aSource.getLinkName(), aDef);
      if ( link != null )
      {
         AeLinkComposite comp = getLinkComposite( link );

         // Make certain that this source doesn't already exist.
         if ( comp.mSource != null)
         {
            // Source already exists, therefore this link erroreously has multiple sources!
            getReporter().reportProblem( IAeValidationProblemCodes.BPEL_MULTI_SRC_LINK_CODE,
                                    ERROR_MULTI_SRC_LINK,
                                    new String[] { comp.getLink().getLocationPath() },
                                    comp.getLink() );
            return;
         }

         comp.mSource = aDef ;

         // Record this link source def for later cycle check.
         //
         AeLinkSource src = getLinkSource( aDef );
         if ( src == null )
         {
            src = addLinkSource( aDef );
         }

         src.addConnection( comp );
      }
      else
      {
         // the key was null so we didn't find our link definition
         // this is a problem.
         getReporter().reportProblem( IAeValidationProblemCodes.BPEL_BAD_LINK_CODE,
                                 ERROR_BAD_LINK,
                                 new String[] { aDef.getLocationPath() }, aDef );
      }

   }

   /**
    * Add a target activity to the map.
    *
    * @param aTarget The target to add.
    * @param aDef The activity def that owns the target.
    */
   public void addTarget( AeTargetDef aTarget, AeActivityDef aDef )
   {
      AeLinkDef link = findLink(aTarget.getLinkName(), aDef);
      if ( link != null )
      {
         AeLinkComposite comp = getLinkComposite( link );

         // Make certain that this target doesn't already exist.
         if ( comp.mTarget != null)
         {
            // Target already exists, therefore this link erroreously has multiple targets!
            getReporter().reportProblem( IAeValidationProblemCodes.BPEL_MULTI_TARGET_LINK_CODE,
                                    ERROR_MULTI_TARGET_LINK,
                                    new String[] { comp.getLink().getLocationPath() },
                                    comp.getLink() );
            return;
         }
         comp.mTarget = aDef ;
      }
      else
      {
         // the key was null so we didn't find our link definition
         // this is a problem.
         getReporter().reportProblem( IAeValidationProblemCodes.BPEL_BAD_LINK_CODE,
                                 ERROR_BAD_LINK,
                                 new String[] { aDef.getLocationPath() }, aDef );
      }
   }

   /**
    * Returns the link source that corresponds to the def provided.
    *
    * @param aDef The source def to search for.
    *
    * @return AeLinkSource
    */
   public AeLinkSource getLinkSource( AeActivityDef aDef )
   {
      return (AeLinkSource) getLinkSourcesMap().get(aDef);
   }

   /**
    * Adds a link source instance to the map.
    *
    * @param aDef The source def for the new link source instance.
    *
    * @return AeLinkSource
    */
   private AeLinkSource addLinkSource( AeActivityDef aDef )
   {
      AeLinkSource src = new AeLinkSource( aDef );
      getLinkSourcesMap().put(aDef, src);
      return src;
   }

   /**
    * Accessor for the root process def.
    */
   private AeProcessDef getProcessDefNode()
   {
      return mProcessDef;
   }

   /**
    * Returns <code>true</code> if and only if this is a WS-BPEL process.
    */
   public boolean isWsBpelProcess()
   {
      return !AeDefUtil.isBPWS(getProcessDefNode());
   }
   
   /**
    * Inner class that serves as a flag for the direction of a link 
    */
   private static class AeLinkDirection
   {
      /** An inbound link is one that is crossing a boundary on its way to  */
      public static final AeLinkDirection INBOUND = new AeLinkDirection();
      public static final AeLinkDirection OUTBOUND = new AeLinkDirection();
      
      private AeLinkDirection()
      {
      }
   }

   /**
    * Inner class to facilitate cycle checking.
    */
   public static class AeLinkSource
   {
      /** List of link composites for which this instance is a source. */
      private List mSources = new ArrayList();

      /** Activity associated with this source. */
      private AeActivityDef mSource ;

      /**
       * Ctor to set the source def.
       *
       * @param aSource The source activity to set.
       */
      public AeLinkSource( AeActivityDef aSource )
      {
         mSource = aSource ;
      }

      /**
       * Add a link / connection to this source's list of links.
       *
       * @param aConnection The link to add.
       */
      public void addConnection( AeLinkComposite aConnection )
      {
         mSources.add( aConnection );
      }

      /**
       * Get the iterator over current list of link sources.
       *
       * @return Iterator
       */
      public Iterator getSourceConnections()
      {
         return mSources.iterator();
      }

      /**
       * Get the activity associated with this list of source links.
       *
       * @return AeActivityDef
       */
      public AeActivityDef getSourceDef()
      {
         return mSource ;
      }
   }

   /**
    * Composite link inner class used to manage validation of link definitions.
    */
   public static class AeLinkComposite
   {
      private AeActivityDef mSource ;
      private AeActivityDef mTarget ;
      private final AeLinkDef mLink ;

      /**
       * Create a composite with link name.
       *
       * @param aLink The link for this composite.
       */
      public AeLinkComposite( AeLinkDef aLink )
      {
         mLink = aLink;
      }

      /**
       * Returns true if the link is in use.  For a link to be in use it must
       * be associated with a source or a target (or both).
       */
      public boolean isUsed()
      {
         return mSource != null || mTarget != null;
      }
      
      /**
       * Indicate whether the composite has all its parts.
       *
       * @return boolean <code>true</code> if the source, target and link elements are defined.
       */
      public boolean isComplete()
      {
         if ( mSource == null || mTarget == null || mLink == null )
            return false ;

         return true ;
      }

      /**
       * Get the link for this composite.
       *
       * @return AeLinkDef
       */
      public AeLinkDef getLink()
      {
         return mLink;
      }

      /**
       * Get the source node for this composite.
       *
       * @return AeActivityDef
       */
      public AeActivityDef getSource()
      {
         return mSource;
      }

      /**
       * Get the target node for this composite.
       *
       * @return AeActivityDef
       */
      public AeActivityDef getTarget()
      {
         return mTarget;
      }
   }
}

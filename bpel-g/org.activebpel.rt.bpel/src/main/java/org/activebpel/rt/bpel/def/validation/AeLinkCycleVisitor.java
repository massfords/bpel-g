// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/AeLinkCycleVisitor.java,v 1.3 2007/09/26 02:21:03 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivitySequenceDef;
import org.activebpel.rt.bpel.def.validation.AeLinkValidator.AeLinkComposite;
import org.activebpel.rt.bpel.def.validation.AeLinkValidator.AeLinkSource;
import org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor;
import org.activebpel.rt.bpel.def.visitors.AeDefTraverser;
import org.activebpel.rt.bpel.def.visitors.AeTraversalVisitor;
import org.activebpel.rt.xml.def.AeBaseXmlDef;

/**
 * Locates links that participate in control cycles.
 *
 * WS-BPEL changed the way links leaving an isolated scope behave. These links
 * will not fire their notification until the entire isolated scope completes.
 * This introduces another check for a control cycle since any outbound link
 * crossing an isolated scope must not link to a graph that contains a link 
 * back into the scope. This creates a cycle since the isolated scope will
 * not fire its links until it completes - but it can't complete until it 
 * fires its links.
 * 
 */
public class AeLinkCycleVisitor extends AeAbstractDefVisitor
{
   /** The {@link AeLinkValidator} that defines the links and link sources. */
   private final AeLinkValidator mLinkValidator;

   /**
    * Maps each activity to the next activity in a sequence, or
    * <code>null</code> if there is no such activity.
    */
   private final Map mNextSequenceActivityMap = new HashMap();

   /** The <code>Set</code> of links that participate in control cycles. */
   private Set mCycleLinks;

   /**
    * A <code>Set</code> containing the source activity for the current
    * traversal and its ancestors.
    * 
    * @see #enclosesSourceActivity(AeActivityDef)
    */
   private Set mSourceAndAncestors;

   /**
    * <code>true</code> if and only if we have reached the source activity
    * during the current traversal.
    */
   private boolean mSourceReached;
   
   /**
    * The <code>Set</code> of activities that we have visited during the current
    * traversal.
    */
   private Set mVisitedActivities;
   
   /** 
    * The isolated scope for a source link that is currently being traversed. 
    * If we encounter a target in our traversal that is nested within the same 
    * scope then we have a cycle.
    */
   private AeActivityScopeDef mIsolatedScope;

   /**
    * Constructs visitor for the given link validator.
    * 
    * @param aLinkValidator
    */
   protected AeLinkCycleVisitor(AeLinkValidator aLinkValidator)
   {
      mLinkValidator = aLinkValidator;

      // This traversal implementation visits a container's children recursively
      // while visiting the container. We depend on this to implement the notion
      // that control passes from a container to its children for the purpose of
      // detecting control cycles.
      setTraversalVisitor(new AeTraversalVisitor(new AeDefTraverser(), this));
   }

   /**
    * Finds the links from the given {@link AeLinkValidator} that participate in
    * control cycles, and returns a <code>Set</code> containing those links. 
    *
    * @param aLinkValidator
    */
   public static Set findCycleLinks(AeLinkValidator aLinkValidator)
   {
      AeLinkCycleVisitor visitor = new AeLinkCycleVisitor(aLinkValidator);
      return visitor.findCycleLinks();
   }

   /**
    * Adds the given link to the set of cycle links.
    * 
    * @param aLink
    */
   protected void addCycleLink(AeLinkComposite aLink)
   {
      getCycleLinks().add(aLink);
   }

   /**
    * Adds the given activity to the set of visited activities for the current
    * traversal.
    *
    * @param aActivityDef
    */
   protected void addVisitedActivity(AeActivityDef aActivityDef)
   {
      getVisitedActivities().add(aActivityDef);
   }

   /**
    * Returns <code>true</code> if and only if the given activity encloses the
    * source activity.
    *
    * @param aActivityDef
    */
   protected boolean enclosesSourceActivity(AeActivityDef aActivityDef)
   {
      return getSourceAndAncestors().contains(aActivityDef);
   }

   /**
    * Finds the links that participate in control cycles, and returns a
    * <code>Set</code> containing those links. 
    */
   protected Set findCycleLinks()
   {
      setCycleLinks(new LinkedHashSet());

      // Iterate over the links in the link validator. Add a link to the set of
      // cycle links if it is not already in the set and the link participates
      // in a control cycle. A link may be added to the set here or during the
      // search traversal for a previous link (see traverseOutgoingLinks()).
      for (Iterator i = getLinkValidator().getLinks().iterator(); i.hasNext(); )
      {
         AeLinkComposite link = (AeLinkComposite) i.next();

         if (!getCycleLinks().contains(link) && isInCycle(link))
         {
            addCycleLink(link);
         }
      }

      return getCycleLinks();
   }

   /**
    * Returns the <code>Set</code> of links that participate in control cycles. 
    */
   protected Set getCycleLinks()
   {
      return mCycleLinks;
   }

   /**
    * Returns the
    * {@link org.activebpel.rt.bpel.def.validation.AeLinkValidator.AeLinkSource}
    * instance corresponding to the given activity or <code>null</code> if there
    * is no such instance.
    *
    * @param aActivityDef
    */
   protected AeLinkSource getLinkSource(AeActivityDef aActivityDef)
   {
      return getLinkValidator().getLinkSource(aActivityDef);
   }

   /**
    * Returns the {@link AeLinkValidator} that owns this visitor.
    */
   protected AeLinkValidator getLinkValidator()
   {
      return mLinkValidator;
   }

   /**
    * Returns the activity that follows the activity in a sequence, or
    * <code>null</code> if there is no such activity.
    *
    * @param aActivityDef
    */
   protected AeActivityDef getNextSequenceActivity(AeActivityDef aActivityDef)
   {
      Map map = getNextSequenceActivityMap();
      if (!map.containsKey(aActivityDef))
      {
         AeBaseDef parent = aActivityDef.getParent();

         // If this activity does not belong to a sequence, then there is no
         // next sequence activity.
         if (!(parent instanceof AeActivitySequenceDef))
         {
            map.put(aActivityDef, null);
         }
         // If this activity belongs to a sequence, then put all the sequence
         // activities into the map.
         else
         {
            AeActivitySequenceDef sequence = (AeActivitySequenceDef) parent;
            Iterator i = sequence.getActivityDefs();
            
            // We know that we can call i.next() at least once, because
            // aActivityDef belongs to the sequence.
            AeActivityDef activity = (AeActivityDef) i.next();
            
            while (i.hasNext())
            {
               AeActivityDef nextActivity = (AeActivityDef) i.next();
               map.put(activity, nextActivity);
               activity = nextActivity;
            }

            map.put(activity, null);
         }
      }
      
      return (AeActivityDef) map.get(aActivityDef);
   }

   /**
    * Returns the <code>Map</code> from each activity to the next activity in a
    * sequence, or <code>null</code> if there is no such activity.
    */
   protected Map getNextSequenceActivityMap()
   {
      return mNextSequenceActivityMap;
   }

   /**
    * Returns the <code>Set</code> containing the source activity for the
    * current traversal and its ancestors.
    */
   protected Set getSourceAndAncestors()
   {
      return mSourceAndAncestors;
   }

   /**
    * Returns the <code>Set</code> of activities that we have visited during the
    * current traversal.
    */
   protected Set getVisitedActivities()
   {
      return mVisitedActivities;
   }

   /**
    * Returns <code>true</code> if and only if the given link participates in a
    * control cycle.
    *
    * @param aLink
    */
   protected boolean isInCycle(AeLinkComposite aLink)
   {
      setSourceReached(false);
      setIsolatedScope(null);
      
      if (aLink.isComplete())
      {
         setSourceActivity(aLink.getSource());
         setVisitedActivities(new HashSet());
         recordIsolatedScopeReference(aLink);

         // Visit the target of the link. If we reach the source of the link
         // while visiting the target, then isSourceReached() will be set to
         // true.
         aLink.getTarget().accept(this);
      }
      
      return isSourceReached();
   }

   /**
    * Records the isolated scope for the source of the link iff the target is
    * not within the same isolated scope. This is to check for a link that 
    * leaves an isolated scope and returns.
    * @param aIsolatedScope
    */
   private void recordIsolatedScopeReference(AeLinkComposite aLinkComposite)
   {
      if (isWsBpelProcess())
      {
         AeActivityScopeDef sourceIsoScope = aLinkComposite.getSource().getIsolatedScope();
         AeActivityScopeDef targetIsoScope = aLinkComposite.getTarget().getIsolatedScope();
         if (sourceIsoScope != null && sourceIsoScope != targetIsoScope)
         {
            mIsolatedScope = sourceIsoScope;
         }
      }
   }

   /**
    * Returns <code>true</code> if and only if we reached the source activity
    * during the current traversal.
    */
   protected boolean isSourceReached()
   {
      return mSourceReached;
   }

   /**
    * Returns <code>true</code> if and only if we visited the given activity
    * during the current traversal.
    *
    * @param aActivityDef
    */
   protected boolean isVisitedActivity(AeActivityDef aActivityDef)
   {
      return getVisitedActivities().contains(aActivityDef);
   }

   /**
    * Returns <code>true</code> if and only if this is a WS-BPEL process.
    */
   protected boolean isWsBpelProcess()
   {
      return getLinkValidator().isWsBpelProcess();
   }

   /**
    * Sets the <code>Set</code> of links that participate in control cycles.
    *
    * @param aCycleLinks
    */
   protected void setCycleLinks(Set aCycleLinks)
   {
      mCycleLinks = aCycleLinks;
   }

   /**
    * Sets the source activity for the current traversal.
    *
    * @param aActivityDef
    */
   protected void setSourceActivity(AeActivityDef aActivityDef)
   {
      // Create a set containing the source activity and its ancestors.
      Set set = new HashSet();

      for (AeBaseDef def = aActivityDef; def != null; def = def.getParent())
      {
         set.add(def);
      }

      // Save the set containing the activity and its ancestors for use by
      // enclosesSourceActivity().
      setSourceAndAncestors(set);
   }

   /**
    * Sets the <code>Set</code> containing the source activity for the current
    * traversal and its ancestors.
    * 
    * @param aSourceAndAncestors
    */
   protected void setSourceAndAncestors(Set aSourceAndAncestors)
   {
      mSourceAndAncestors = aSourceAndAncestors;
   }

   /**
    * Sets the value to return for {@link #isSourceReached()}.
    *
    * @param aSourceReached
    */
   protected void setSourceReached(boolean aSourceReached)
   {
      mSourceReached = aSourceReached;
   }

   /**
    * Sets the <code>Set</code> of activities that we have visited during the
    * current traversal.
    *
    * @param aVisitedActivities
    */
   protected void setVisitedActivities(Set aVisitedActivities)
   {
      mVisitedActivities = aVisitedActivities;
   }

   /**
    * Overrides method for two reasons:
    * <ol>
    * <li>short-circuit traversal if we reach the source activity, and</li>
    * <li>call {@link #traverseActivity(AeActivityDef)} to traverse activities.</li>
    * </ol>
    * 
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#traverse(org.activebpel.rt.xml.def.AeBaseXmlDef)
    */
   protected void traverse(AeBaseXmlDef aDef)
   {
      // Continue traversal only if we have not yet reached the source activity.
      if (!isSourceReached())
      {
         if (aDef instanceof AeActivityDef)
         {
            traverseActivity((AeActivityDef) aDef);
         }
         else
         {
            super.traverse(aDef);
         }
      }
   }

   /**
    * Traverses the given activity.
    *
    * @param aActivityDef
    */
   protected void traverseActivity(AeActivityDef aActivityDef)
   {
      // We may reach a given activity through multiple paths. Skip this
      // activity if we have already traversed it.
      if (!isVisitedActivity(aActivityDef))
      {
         addVisitedActivity(aActivityDef);

         // If this activity encloses the source activity, then we are done
         // traversing and can report that we have a control cycle.
         // Also check to see if the target activity has crossed back into an
         // isolated scope.
         if (enclosesSourceActivity(aActivityDef) || crossesBackIntoIsolatedScope(aActivityDef))
         {
            setSourceReached(true);
         }
         else
         {
            // Try to reach the source activity by traversing this activity's
            // outgoing links.
            traverseOutgoingLinks(aActivityDef);

            // If we did not reach the source activity just by traversing
            // explicit outgoing links, then traverse the next activity in the
            // sequence, if any.
            if (!isSourceReached())
            {
               traverseNextSequenceActivity(aActivityDef);
            }

            // If we did not reach the source activity just by traversing links,
            // then use the base class to traverse this activity's children, if
            // any.
            if (!isSourceReached())
            {
               super.traverse(aActivityDef);
            }
         }
      }
   }
   
   /**
    * Returns true if the activity passed in is nested within the isolated scope
    * recorded when the link was first traversed. This represents a case where
    * a link crosses an isolated scope and then eventually comes back in through
    * some other activity. This creates a cycle since the isolated scope will 
    * not fire its links until it completes - but it can't complete until it 
    * fires its links.
    * @param aActivityDef
    */
   protected boolean crossesBackIntoIsolatedScope(AeActivityDef aActivityDef)
   {
      return getIsolatedScope() != null && getIsolatedScope() == aActivityDef.getIsolatedScope();
   }

   /**
    * Traverses the activity that follows the activity in a sequence, if any.
    *
    * @param aActivityDef
    */
   protected void traverseNextSequenceActivity(AeActivityDef aActivityDef)
   {
      AeActivityDef nextActivity = getNextSequenceActivity(aActivityDef);
      if (nextActivity != null)
      {
         nextActivity.accept(this);
      }
   }

   /**
    * Traverses the outgoing links for the given activity, if any.
    *
    * @param aActivityDef
    */
   protected void traverseOutgoingLinks(AeActivityDef aActivityDef)
   {
      AeLinkSource source = getLinkSource(aActivityDef);
      if (source != null)
      {
         for (Iterator i = source.getSourceConnections(); i.hasNext(); )
         {
            AeLinkComposite link = (AeLinkComposite) i.next();
            if (link.isComplete())
            {
               // Visit the target of this link.
               link.getTarget().accept(this);

               if (isSourceReached())
               {
                  // If we reach the original source activity through this link,
                  // then this link is also part of the control cycle. Add this
                  // link to the set of cycle links now, so that we can collect
                  // multiple links with a single traversal.
                  addCycleLink(link);

                  // Stop traversing once we reach the source activity.
                  break;
               }
            }
         }
      }
   }
   
   /**
    * Getter for the isolated scope
    */
   protected AeActivityScopeDef getIsolatedScope()
   {
      return mIsolatedScope;
   }
   
   /**
    * Setter for the isolated scope.
    * @param aScope
    */
   protected void setIsolatedScope(AeActivityScopeDef aScope)
   {
      mIsolatedScope = aScope;
   }
}
//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/visitors/AeLocationPathVisitor.java,v 1.3 2008/02/17 21:09:22 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def.visitors; 

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.AeMessages;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.AeDocumentationDef;
import org.activebpel.rt.xml.def.AeExtensionAttributeDef;
import org.activebpel.rt.xml.def.AeExtensionElementDef;
import org.activebpel.rt.xml.def.IAeNamedDef;
import org.activebpel.rt.xml.def.IAePathSegmentBuilder;

/**
 * Visits the def model and assigns location paths to each of the defs within
 * the model. 
 * 
 *  This class relies on having a {@link IAePathSegmentBuilder} in order to 
 *  construct the path for each def. It also relies on a traversal visitor
 *  in the form of a {@link IAeBaseXmlDefVisitor} in order to traverse the
 *  object model.  
 */
public class AeLocationPathVisitor implements IAeDefPathVisitor
{
   /** Current path */
   private String mPath=""; //$NON-NLS-1$
   /** Path Maps */
   private Set mPaths = new HashSet();
   /** Next available location id */
   private int mNextLocationId = 1;
   /** Maps location paths to location ids */
   private Map mLocationPathMap = new HashMap();
   /** visitor to build a single segment of the path for a def */
   private IAePathSegmentBuilder mSegmentBuilder;
   /** visitor for traversing defs */
   private IAeBaseXmlDefVisitor mTraversalVisitor;
   
   /**
    * Ctor
    * @param aSegmentPathBuilder
    */
   public AeLocationPathVisitor(IAePathSegmentBuilder aSegmentPathBuilder)
   {
      setSegmentBuilder(aSegmentPathBuilder);
   }
  
   /**
    * All of the base classes's visit methods call this traverse method. This gives us
    * a single place to determine the segment for the current def and then create
    * a unique location path for the def using this segment and its parent's location path.
    * 
    * If this def doesn't have a segment, then we'll traverse it and continue visiting
    * its children.
    * 
    * If this def is a named def, then we'll create a path using its name in addition to its
    * parent's path and its segment path and then traverse it.
    * 
    * If this def is not a named def, then we'll create a path using only its parent's path
    * and its segment path and then traverse it.
    * 
    */
   protected void traverse(AeBaseXmlDef aDef)
   {
      if (getTraversalVisitor() == null)
         throw new IllegalStateException(AeMessages.getString("AeLocationPathVisitor.MissingTraverser")); //$NON-NLS-1$
      String segment = getSegmentBuilder().createPathSegment(aDef);
      
      // if we got a segment, then create a path and traverse
      if (segment != null)
      {
         // If it's the root def, then don't include the name. 
         if (aDef instanceof IAeNamedDef && AeUtil.notNullOrEmpty(getPath()))
         {
            createPathAndTraverse((IAeNamedDef) aDef, segment);
         }
         else
         {
            createPathAndTraverse(aDef, segment, null);
         }
      }
      else
      {
         // there is no path to set on this def. Use the traversal visitor to move onto the next path
         aDef.accept(getTraversalVisitor());
      }
   }
   
   /**
    * Appends the def's name to the xpath to make a more human readable xpath.
    * @param aDef
    * @param aAppendPath
    */
   protected void createPathAndTraverse(IAeNamedDef aDef, String aAppendPath)
   {
      String name = aDef.getName();
      if (!AeUtil.isNullOrEmpty(name))
      {
         StringBuffer buffer = new StringBuffer(aAppendPath);
         buffer.append("[@name='"); //$NON-NLS-1$
         buffer.append(name);
         buffer.append("']");  //$NON-NLS-1$
         createPathAndTraverse((AeBaseXmlDef)aDef, aAppendPath, buffer.toString());
      }
      else
      {
         createPathAndTraverse((AeBaseXmlDef)aDef, aAppendPath, null);
      }
   }

   /**
    * Accepts the extra arg of a name path which will be non null for AeNamedDef
    * instances that had a non-null value for their name. This allows for more
    * user friendly xpath names while not running into problem of generating
    * inaccurate xpath.
    * @param aDef - the definition object we're visiting
    * @param aAppendPath - the path to the def object
    * @param aNamePath - the path with the name attribute in it
    */
   protected void createPathAndTraverse(AeBaseXmlDef aDef, String aAppendPath, String aNamePath)
   {
      if(aAppendPath != null)
      {
         String savePath = getPath();
         if (AeUtil.isNullOrEmpty(aNamePath))
         {
            setPath( createUniquePath(aDef, aAppendPath) );
         }
         else
         {
            // calling createUniquePath TWICE here so we'll have a record of the
            // path in both its named and unnamed form. This addresses the issue
            // of having multiple named objects as child elements and only having
            // SOME of them contain names.
            // i.e.
            // <scope name="one"/>
            // <scope/>
            // <scope name="three"/>
            // without this extra call, the path for the second scope will be
            // "/scope" since there was no other scope recorded with that path.
            // It SHOULD be /scope[2].
            createUniquePath(aDef, aAppendPath);
            setPath(createUniquePath(aDef, aNamePath));
         }
         updateLocationId(aDef);
         aDef.accept(getTraversalVisitor());
         setPath(savePath);
      }
      else
      {
         aDef.accept(getTraversalVisitor());
      }
   }

   /**
    * Generates a new location id for the current path and puts this value into a map.
    * @param aDef
    */
   protected void updateLocationId(AeBaseXmlDef aDef)
   {
      int locationId = getNextLocationId();
      setNextLocationId(locationId + 1);
      getLocationPathMap().put(getPath(), new Integer(locationId));
      recordLocationPathAndId(aDef, getPath(), locationId);
   }   
   
   /**
    * Sets the location path and id on the def object.
    * @param aDef
    * @param aLocationPath
    * @param aLocationId
    */
   protected void recordLocationPathAndId(AeBaseXmlDef aDef, String aLocationPath, int aLocationId)
   {
      aDef.setLocationPath(aLocationPath);
      aDef.setLocationId(aLocationId);
   }

   /**
    * Checks map of paths created by vistors and appends indexes to make unique
    * @param aDef The def object we're creating the path for
    * @param aAppendPath path to append to main path
    * @return the unique path
    */
   protected String createUniquePath(AeBaseXmlDef aDef, String aAppendPath)
   {
      StringBuffer testPath = new StringBuffer(getPath());
      testPath.append("/"); //$NON-NLS-1$
      testPath.append(aAppendPath);
      int initLen = testPath.length();

      // test path for uniqueness in set and loop until unique
      for(int i = 2; getPaths().contains(testPath.toString()); ++i)
      {
         testPath.setLength(initLen);
         testPath.append("[").append(i).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
      }

      // good path found so save the path in set and return the good path
      String goodPath = testPath.toString();
      getPaths().add(goodPath);
      return goodPath;
   }

   /**
    * @return the nextLocationId
    */
   public int getNextLocationId()
   {
      return mNextLocationId;
   }
   /**
    * @param aNextLocationId the nextLocationId to set
    */
   public void setNextLocationId(int aNextLocationId)
   {
      mNextLocationId = aNextLocationId;
   }
   /**
    * @return the path
    */
   public String getPath()
   {
      return mPath;
   }
   /**
    * @param aPath the path to set
    */
   public void setPath(String aPath)
   {
      mPath = aPath;
   }

   public IAePathSegmentBuilder getSegmentBuilder()
   {
      return mSegmentBuilder;
   }
   /**
    * @param aSegmentBuilder the segmentVisitor to set
    */
   public void setSegmentBuilder(IAePathSegmentBuilder aSegmentBuilder)
   {
      mSegmentBuilder = aSegmentBuilder;
   }
   /**
    * @return the locationPathMap
    */
   public Map getLocationPathMap()
   {
      return mLocationPathMap;
   }
   /**
    * @return the paths
    */
   public Set getPaths()
   {
      return mPaths;
   }
   
   /**
    * @return the traversalVisitor
    */
   public IAeBaseXmlDefVisitor getTraversalVisitor()
   {
      return mTraversalVisitor;
   }
   /**
    * @param aTraversalVisitor the traversalVisitor to set
    */
   public void setTraversalVisitor(IAeBaseXmlDefVisitor aTraversalVisitor)
   {
      mTraversalVisitor = aTraversalVisitor;
   }

   /**
    * @see org.activebpel.rt.xml.def.visitors.IAeDefPathVisitor#getLocationId(java.lang.String)
    */
   public int getLocationId(String aLocationPath)
   {
      Integer id = (Integer) getLocationPathMap().get(aLocationPath);
      return id.intValue();
   }

   /**
    * @see org.activebpel.rt.xml.def.visitors.IAeDefPathVisitor#getLocationPaths()
    */
   public Set getLocationPaths()
   {
      return Collections.unmodifiableSet(getLocationPathMap().keySet());
   }

   /**
    * @see org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor#visit(org.activebpel.rt.xml.def.AeBaseXmlDef)
    */
   public void visit(AeBaseXmlDef aDef)
   {
      traverse(aDef);
   }

   /**
    * @see org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor#visit(org.activebpel.rt.xml.def.AeDocumentationDef)
    */
   public void visit(AeDocumentationDef aDef)
   {
      traverse(aDef);
   }

   /**
    * @see org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor#visit(org.activebpel.rt.xml.def.AeExtensionAttributeDef)
    */
   public void visit(AeExtensionAttributeDef aDef)
   {
      traverse(aDef);
   }

   /**
    * @see org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor#visit(org.activebpel.rt.xml.def.AeExtensionElementDef)
    */
   public void visit(AeExtensionElementDef aDef)
   {
      traverse(aDef);
   }
}
 
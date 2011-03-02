//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/util/AeLocationPathUtils.java,v 1.10 2008/02/17 21:37:11 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.util; 

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.util.AeUtil;

/**
 * Utils for extracting info from the bpel location paths. 
 */
public class AeLocationPathUtils
{
   /**
    * Helper routine to get the path to the forEach.
    * @param aPath
    */
   public static String getParentPath(String aPath)
   {
      int idx = aPath.lastIndexOf('/');
      return aPath.substring(0, idx);
   }

   /**
    * Helper method. If the given node path is terminated with an instance number, ie "[instance()=XXX]",
    * then that instance number is return otherwise -1 is returned.
    * @param aNodePath
    * @return int the node path's intance number or -1 if not specified.
    */
   public static int getNodePathInstanceNum(String aNodePath)
   {
      final String INSTANCE_TOKEN = "[instance()="; //$NON-NLS-1$

      if ( AeUtil.isNullOrEmpty(aNodePath) )
        return -1;

      int start = aNodePath.lastIndexOf(INSTANCE_TOKEN);

      if ( start != -1 )
      {
         int end = aNodePath.substring(start).indexOf("]"); //$NON-NLS-1$
         if ( end != -1 )
         {
            String instStr = aNodePath.substring( start + INSTANCE_TOKEN.length(), start+end);
            try
            {
               Integer instInt = Integer.valueOf(instStr);
               return instInt.intValue();
            }
            catch (NumberFormatException e)
            {
            }
         }
      }
      return -1;
   }

   /**
    * Converts the paths in the collection to include instance information
    * if the referenced fall within a parallel forEach scope.
    *
    * Use case:
    *
    * The variables that need to be locked for a given activity are determined by
    * visiting the process def when the process definition is first loaded from the
    * src xml. This process does not take into account that a variable might be
    * declared within a parallel forEach. If this variable is declared within a
    * parallel forEach, then the actual runtime path to the variable will include
    * the instance information. The conversion here is necessary in order to insert
    * the instance info into the path so we can be sure that we're locking the proper
    * variable.
    *
    * i.e. Consider the following collection of variable paths to lock:
    *
    *    /process/variables/variable[@name='p1_v1']
    *    /process/forEach/scope[@name='s1']/variables/variable[@name='s1_v1']
    *    /process/forEach/scope[@name='s1']/sequence/scope[@name='s2']/variables/variable[@name='s2_v1']
    *    /process/forEach/scope[@name='s1']/sequence/scope[@name='s2']/flow/scope[@name='s3']/variables/variable[@name='s3_v1']
    *
    *    The variable p1_v1 is declared at the process level and therefore does
    *    need to change. We only need to change variables declared within scopes
    *    that are enclosed within a parallel forEach.
    *
    *    The paths to s1_v1, s2_v1, s3_v1 all need the instance info for the parallel
    *    forEach instance scope. As such, the collection returned will contain
    *    the following paths:
    *
    *    /process/variables/variable[@name='p1_v1']
    *    /process/forEach/scope[@name='s1'][instance()=1]/variables/variable[@name='s1_v1']
    *    /process/forEach/scope[@name='s1'][instance()=1]/sequence/scope[@name='s2']/variables/variable[@name='s2_v1']
    *    /process/forEach/scope[@name='s1'][instance()=1]/sequence/scope[@name='s2']/flow/scope[@name='s3']/variables/variable[@name='s3_v1']
    *                                      ^------------^: notice the instance info
    * @param aSetOfPaths
    * @param aActivityPath - should contain instance information
    */
   public static Set addInstanceInfo(Set aSetOfPaths, String aActivityPath)
   {
      Set set = null;
      if (!AeUtil.isNullOrEmpty(aSetOfPaths))
      {
         set = new HashSet();

         // split the acticity path into a list of paths that identify all of the
         // enclosed scopes.
         List scopePaths = splitScopes(aActivityPath);

         // TODO (MF) I could do some of this work during the loading of the def,
         //           but doesn't quite seem worth it. It's simple string pushing
         //           and probably not something that'll bubble up in the profiler.
         //           If it were to, then I could determine which variables in the
         //           collection were declared w/in parallel forEach's and then
         //           modify only them.

         // create a map of the scope paths to their non-instance paths. The
         // paths in the collection will NOT include any instance information
         // Using a map to avoid having to run removeInstanceInfo multiple times
         // for the same scope.
         Map scopePathsToNonInstancePaths = new HashMap();
         for(Iterator it = scopePaths.iterator(); it.hasNext();)
         {
            String path = (String) it.next();
            scopePathsToNonInstancePaths.put(path, removeInstanceInfo(path));
         }

         // walk all of the paths in the collection and see if they are declared
         // in any of the nested parallel forEach scope paths
         for (Iterator iter = aSetOfPaths.iterator(); iter.hasNext();)
         {
            String path = (String) iter.next();

            // if we don't find a match against the scope paths then this variable
            // is declared outside of the parallel forEach and its current path
            // is correct.
            boolean foundMatch = false;
            for (Iterator iter2 = scopePaths.iterator(); !foundMatch && iter2.hasNext();)
            {
               String scopePath = (String) iter2.next();
               String nonInstanceLastScopePath = (String) scopePathsToNonInstancePaths.get(scopePath);

               if (path.startsWith(nonInstanceLastScopePath))
               {
                  // add the instance info to the variable path
                  set.add(scopePath + path.substring(nonInstanceLastScopePath.length()));
                  // record that we've matched this variable to break the loop
                  foundMatch = true;
               }
            }

            if (!foundMatch)
            {
               set.add(path);
            }
         }
      }
      else
      {
         set = aSetOfPaths;
      }
      return set;
   }
   
   /**
    * Splits the activity path into a series of paths to each of its scopes - starting
    * with the first parallel forEach scope.
    *
    * The following path:
    *
    *    <code>/process/sequence/scope/flow/forEach/scope[@name='s1'][instance()=1]/sequence/scope[@name='s2']/sequence/assign</code>
    *
    *    would be split into a list like this:
    *
    *    <code>/process/sequence/scope/flow/forEach/scope[@name='s1'][instance()=1]/sequence/scope[@name='s2']</code>
    *    <code>/process/sequence/scope/flow/forEach/scope[@name='s1'][instance()=1]</code>
    *
    * @param aActivityPath
    */
   protected static List splitScopes(String aActivityPath)
   {
      LinkedList list = new LinkedList();
      int offset = -1;

      // We only care about variables declarations nested within parallel forEach's
      // Therefore, start our search from the first occurrence of a parallel forEach scope
      int startFrom = aActivityPath.lastIndexOf('/', aActivityPath.indexOf("[instance()=")); //$NON-NLS-1$
      while((offset = aActivityPath.indexOf("/scope", startFrom)) != -1) //$NON-NLS-1$
      {
         int endOfScopePath = aActivityPath.indexOf('/', offset+1);
         if (endOfScopePath == -1)
         {
            // must be the end of the string
            endOfScopePath = aActivityPath.length();
         }
         String path = aActivityPath.substring(0, endOfScopePath);
         list.addFirst(path);
         startFrom = endOfScopePath;
      }
      return list;
   }

   /**
    * Returns the immediate instance scope path of the given node path.
    *
    * E.g.
    *
    * aNodePath     "/process/flow/forEach[@name='ForEach']/scope[instance()=2]/sequence/wait"
    *   will return "/process/flow/forEach[@name='ForEach']/scope[instance()=2]"
    *
    * @param aNodePath a model node path.
    * @return String the immediate instance scope path of the given node path or null
    *    if the node path doesn't contain a scope instance.
    */
   public static String getScopeNodePath(String aNodePath)
   {
      String scopeNodePath = null;

      int start = aNodePath.lastIndexOf( "[instance()=" ); //$NON-NLS-1$
      if ( start != -1 )
      {
         int end = aNodePath.substring(start).indexOf("]"); //$NON-NLS-1$
         if ( end != -1 )
            scopeNodePath = aNodePath.substring( 0, start+end+1); 
      }
      return scopeNodePath;
   }

   /**
    * Given some path with instance info and some other path that is an anscestor or descendent
    * path, return a version of the second path that includes the instance
    * information.
    *
    * e.g. given the following paths
    *    ancestor = /process/forEach/scope[@name='s1']/sequence/forEach/scope[@name='s4'][instance()=1]
    *    path     = /process/forEach/scope[@name='s1']/sequence/forEach/scope[@name='s4']/sequence/forEach/scope[@name='s5']/invoke[@name='I']
    * return the following:
    *               /process/forEach/scope[@name='s1']/sequence/forEach/scope[@name='s4'][instance()=1]/sequence/forEach/scope[@name='s5']/invoke[@name='I']
    *                  ------------* notice that the instance info has been added -----* ^------------^
    *
    * @param aPathWithInstanceInfo
    * @param aPathThatNeedsInstanceInfo
    */
   public static String addInstanceInfo(String aPathWithInstanceInfo, String aPathThatNeedsInstanceInfo)
   {
      if (AeUtil.isNullOrEmpty(aPathWithInstanceInfo))
      {
         return aPathThatNeedsInstanceInfo;
      }
      else
      {
         Set set = addInstanceInfo(Collections.singleton(aPathThatNeedsInstanceInfo), aPathWithInstanceInfo);
         return (String) set.iterator().next();
      }
   }
   
   /**
    * Given the current instance value for a parallel forEach and its start and
    * final values, calculate what the instance value was for the starting instance
    * (if applicable).
    *
    * i.e. paths for a parallel forEach include instance info. If a parallel forEach
    *      evaluates its start and final expressions to 11 and 13 respectively, then
    *      the paths created for the scopes will be as follows:
    *
    *      /process/flow/forEach/scope[instance()=1]
    *      /process/flow/forEach/scope[instance()=2]
    *      /process/flow/forEach/scope[instance()=3]
    *
    *      another execution of the same parallel forEach with start and final
    *      expressions of 11 and 13 will generate the following paths
    *
    *      /process/flow/forEach/scope[instance()=4]
    *      /process/flow/forEach/scope[instance()=5]
    *      /process/flow/forEach/scope[instance()=6]
    *
    * @param aInstanceValue
    * @param aStart
    * @param aFinal
    */
   public static int getStartInstanceValue(int aInstanceValue, int aStart, int aFinal)
   {
      if (aStart <= aFinal && aStart != -1)
      {
         return aInstanceValue - (aFinal - aStart) - 1;
      }
      else
      {
         return -1;
      }
   }

   /**
    * Given the current instance value for a parallel forEach and its start and
    * final values, calculate what the instance value was for the final instance
    * (if applicable).
    *
    * @param aInstanceValue
    * @param aStart
    * @param aFinal
    */
   public static int getFinalInstanceValue(int aInstanceValue, int aStart, int aFinal)
   {
      if (aStart <= aFinal && aStart != -1)
      {
         return getStartInstanceValue(aInstanceValue, aStart, aFinal) + (aFinal - aStart);
      }
      else
      {
         return -1;
      }
   }
   
   /**
    * Gets the path for the parent of this correlation set
    * @param aCorrelationPath
    */
   public static String getCorrelationParent(String aCorrelationPath)
   {
      int index = aCorrelationPath.indexOf("/correlationSets/correlationSet"); //$NON-NLS-1$
      if (index == -1)
         return null;
      return aCorrelationPath.substring(0, index);
   }
   
   /**
    * Gets the name of the correlation set referred to by this path
    * @param aCorrelationPath
    */
   public static String getCorrelationSetName(String aCorrelationPath)
   {
      int index = aCorrelationPath.lastIndexOf("/correlationSet"); //$NON-NLS-1$
      if (index == -1)
         return null;
      
      return getName(aCorrelationPath, index);
   }
   
   /**
    * Gets the name of the activity referenced by the given path. If the activity
    * doesn't have a name then null is returned.
    * @param aLocationPath
    */
   public static String getActivityName(String aLocationPath)
   {
      if (AeUtil.notNullOrEmpty(aLocationPath))
      {
         int index = aLocationPath.lastIndexOf('/');
         if (index == -1)
            return null;
      
         return getName(aLocationPath, index);
      }
      else
      {
         return null;
      }
   }
   
   /**
    * Gets the type of the activity referenced by the given path. 
    * 
    * For example:
    *   location path:
    *       /process/forEach/scope[@name='s1']/variables/variable[@name='s1_v1']
    *   would result in: variable
    * 
    * @param aLocationPath
    */
   public static String getActivityType(String aLocationPath)
   {
      if (AeUtil.notNullOrEmpty(aLocationPath))
      {
         int index = aLocationPath.lastIndexOf('/') + 1;
         if (index == -1)
            return null;
         
         String endToken = "[";  //$NON-NLS-1$
         int typeIndex = aLocationPath.indexOf(endToken, index);
         
         if (typeIndex < 0)
         {
            typeIndex = aLocationPath.length();
         } 
         if (typeIndex == -1)
         {
            return null;
         }
         return aLocationPath.substring(index, typeIndex);
      }
      else
      {
         return null;
      }
   }

   /**
    * Extracts the value of the name attribute from the path or returns null
    * if there isn't one.
    * @param aLocationPath - the location path
    * @param aIndex - index to start searching from
    */
   private static String getName(String aLocationPath, int aIndex)
   {
      String nameToken = "@name='"; //$NON-NLS-1$
      int nameIndex = aLocationPath.indexOf(nameToken, aIndex);
      if (nameIndex == -1)
         return null;
      
      int offset = nameIndex + nameToken.length();
      return aLocationPath.substring(offset, aLocationPath.indexOf('\'', offset));
   }

   /**
    * Helper method for stripping any and all "[instance()=XXX]" substrings from the given node path.
    * 
    * @param aNodePath
    * @return the node path minus any instance() info.
    */
   public static String removeInstanceInfo(String aNodePath)
   {
      // TODO (MF) store as a pattern and create matcher
      return aNodePath.replaceAll("\\[instance\\( *\\)=[0-9]+\\]", "");  //$NON-NLS-1$//$NON-NLS-2$
   }
   
   /**
    * Returns true if the given location path has instance information.
    * 
    * @param aNodePath
    */
   public static boolean hasInstanceInfo(String aNodePath)
   {
      return aNodePath.indexOf("instance()") != -1; //$NON-NLS-1$
   }
   
   /**
    * Returns true if the path provided points to the process
    * @param aPath
    */
   public static boolean isProcessPath(String aPath)
   {
      return "/process".equals(aPath); //$NON-NLS-1$
   }

   /**
    * Given a node path this method returns a substring, from the beginning of 
    * the path, of the last dynamic scope container activity in the path that 
    * contains a child instance scope.
    * @param aScopeInstancePath the node path.
    * @return String the last dynamic scope container instance activity path or 
    *         null if not found.
    */
   public static String getDynamicScopeContainerNodePath(String aScopeInstancePath)
   {
      String path = null;
      int index = aScopeInstancePath.lastIndexOf("[instance()="); //$NON-NLS-1$
      if ( index != -1 )
      {
         index = aScopeInstancePath.substring(0, index).lastIndexOf("/scope"); //$NON-NLS-1$
         path = aScopeInstancePath.substring( 0, index); 
      }
      return path;
   }

   /**
    * Returns the node path of the parent scope instance node from the given 
    * node path.  Returns null if the node path does not contain an instance 
    * scope node. 
    * @param aNodePath
    * @return String returns the node path of the hosting scope instance node 
    *         for the given node path or null if the node path doesn't contain 
    *         an instance scope. 
    */
   public static String getScopeInstanceNodePath(String aNodePath)
   {
      int start = aNodePath.lastIndexOf("[instance()="); //$NON-NLS-1$
      
      if ( start != -1 )
      {
         int end = aNodePath.substring(start).indexOf("]"); //$NON-NLS-1$
         if ( end != -1 )
         {
            String scopeNodePath = aNodePath.substring( 0, start+end+1); 
            return scopeNodePath;
         }
      }
      return null;
   }
   
   /**
    * Given an instance node path and a non-instance sub node path this method 
    * returns the corresponding immediate parent scope instance number for the 
    * given non-instance sub node path.
    * 
    * E.g. 
    * aInstanceNodePath = "/process/flow/forEach[@name='ForEach']/scope[instance()=2]/sequence/wait"
    *         aNodePath = "/process/flow/forEach[@name='ForEach']"
    * would yield 2
    * 
    * aInstanceNodePath = "/process/flow/forEach[@name='ForEach']/scope[instance()=2]/sequence/forEach[@name='NestedForEach']/scope[@name='NestedScope'][instance()=1]/sequence/assign"
    *         aNodePath = "/process/flow/forEach[@name='ForEach']/scope/sequence/forEach[@name='NestedForEach']"
    * would yield 1
    *  
    * @param aInstanceNodePath a node path containing scope instance information.
    * @param aNodePath a non-instance node path.
    * @return int the scope instance number or -1 if not located.
    */
   public static int getSubNodePathInstance(String aInstanceNodePath, String aNodePath)
   {
      int instance = -1;
      
      if ( AeLocationPathUtils.getNodePathInstanceNum(aInstanceNodePath) != -1 )
      {
         int searchPos = 0;
         int start = -1;
         while ( (start = aInstanceNodePath.indexOf("[instance()=", searchPos)) != -1 ) //$NON-NLS-1$
         {
            int end = aInstanceNodePath.substring(start).indexOf("]"); //$NON-NLS-1$
            if ( end == -1 )
                break;
            
            String scopePath = aInstanceNodePath.substring( 0, start+end+1 ); 
            int instanceNum = AeLocationPathUtils.getNodePathInstanceNum(scopePath);
            String dynamicScopeContainerPath = AeLocationPathUtils.getDynamicScopeContainerNodePath(scopePath);
            
            if ( aNodePath.equals(AeLocationPathUtils.removeInstanceInfo(dynamicScopeContainerPath)) )
               return instanceNum;
            
            searchPos = start+end+1;
         }
      }
      return instance;
   }

   /**
    * Builds a hash map of activity node paths and their corresponding instance 
    * number. The map key is a complete activity path and the value is its 
    * instance number. This is used in order to determine which scope instance 
    * should be displayed when setting the state of the process model. 
    * 
    * For example, if you select a node in the outline with the following path:
    * 
    * /process/flow/forEach/scope[instance()=5]/flow/forEach/scope[instance()=10]
    * 
    * We will add the following entries to the map
    * 
    * /process/flow/forEach=5
    * /process/flow/forEach/scope[instance()=5]/flow/forEach=10
    * 
    * This serves as a hint that when we visit the process model and restore the
    * object states, we should use the data for instance 5 for the first 
    * forEach's scope and instance 10 for the nested forEach.
    * 
    * @param aNodePath a node path containing potentially nested for each 
    *        activities.
    * @return Map
    */
   public static Map createPathToInstanceNumberMap(String aNodePath)
   {
      HashMap pathMap = new HashMap();
      
      if ( AeLocationPathUtils.getNodePathInstanceNum(aNodePath) != -1 )
      {
         int searchPos = 0;
         int start = -1;
         while ( (start = aNodePath.indexOf("[instance()=", searchPos)) != -1 ) //$NON-NLS-1$
         {
            int end = aNodePath.substring(start).indexOf("]"); //$NON-NLS-1$
            if ( end == -1 )
                break;
            
            String scopePath = aNodePath.substring( 0, start+end+1 ); 
            int instanceNum = AeLocationPathUtils.getNodePathInstanceNum(scopePath);
            String dynamicScopeContainerPath = AeLocationPathUtils.getDynamicScopeContainerNodePath(scopePath);
            
            pathMap.put( dynamicScopeContainerPath, new Integer(instanceNum) );
            searchPos = start+end+1;
         }
      }
      return pathMap;
   }
}
 

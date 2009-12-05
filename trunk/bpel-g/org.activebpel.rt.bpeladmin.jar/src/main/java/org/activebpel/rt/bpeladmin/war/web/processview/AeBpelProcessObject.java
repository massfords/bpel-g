// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/processview/AeBpelProcessObject.java,v 1.10 2008/02/17 21:43:06 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//          PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.processview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.impl.AeSuspendReason;
import org.w3c.dom.Document;

/**
 * Represents a BPEL process. This is the root of the BPEL process.
 */
public class AeBpelProcessObject extends AeBpelScopeObject implements IAeWebProcessStates
{   
   /**
    * List of links found in this process, stored in a Map using the link's name as the key.
    */
   private Map mLinks = new LinkedHashMap();
   
   /** XPath to all variables used by this process. */
   private Set mVariablePaths = new HashSet();
   
   /** Map containing BPEL web model keyed by its location path. */
   private Map mPathToWebModelMap = new HashMap();
      
   /** The xml state document of the active process. */
   private Document mStateDoc = null;
   
   /** Process state.*/
   private int mProcessState = -1;
   
   /** Process state reason.*/
   private int mProcessStateReason = -1;
   
   /** Flag to indicate that the process was the parent in one or more coordinated activities. */
   private boolean mCoordinator;
   /** Flag to indicate that the process was a child/subprocess in an coordinated activity. */
   private boolean mParticipant;
   
   /**
    * Ctor.
    * @param aProcessDef process def.
    * @param aName process name.
    * @param aLocationPath location path of process.
    */
   public AeBpelProcessObject(AeProcessDef aProcessDef, String aName, String aLocationPath)
   {
      super(AeProcessDef.TAG_PROCESS, aProcessDef);
      setName(aName);
      setLocationPath(aLocationPath);
      addWebModel(this);
   }
         
   /**
    * @return Returns the processDef.
    */
   protected AeProcessDef getProcessDef()
   {
      return (AeProcessDef) getDef();
   }
            
   /**
    * @return Returns the processState.
    */
   public int getProcessState()
   {
      return mProcessState;
   }
   
   /**
    * @param aProcessState The processState to set.
    */
   public void setProcessState(int aProcessState)
   {
      mProcessState = aProcessState;
   }
   
   /**
    * @return Returns the processState.
    */
   public int getProcessStateReason()
   {
      return mProcessStateReason;
   }
   
   /**
    * @param aProcessStateReason The processState to set.
    */
   public void setProcessStateReason(int aProcessStateReason)
   {
      mProcessStateReason = aProcessStateReason;
   }
   
   /** 
    * Overrides method to return process level state key. 
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeBpelObjectBase#getDisplayStateKey()
    */
   public String getDisplayStateKey()
   {
      return getProcessStateFromCode( getProcessState(), getProcessStateReason() );
   }
   
   /** 
    * Overrides method to return process level state. 
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeBpelObjectBase#getDisplayState()
    */
   public String getDisplayState()
   {
      return getProcessStateFromCode( getProcessState() );
   }   
   
   /**
    * @return Returns the stateDoc.
    */
   public Document getStateDoc()
   {
      return mStateDoc;
   }
   /**
    * @param aStateDoc The stateDoc to set.
    */
   public void setStateDoc(Document aStateDoc)
   {
      mStateDoc = aStateDoc;
   }
   /**
    * @return Returns the hasState.
    */
   public boolean isHasState()
   {
      return getStateDoc() != null;
   }
      
   /**
    * Adds the given Bpel object to the xpath-object map as well as to the element-object amp.
    * @param aBpelObj
    */
   public void addWebModel(AeBpelObjectBase aBpelObj)
   {
      mPathToWebModelMap.put(aBpelObj.getLocationPath(), aBpelObj);
   }
   
   /**
    * Returns BPEL object given xpath.
    * @param aLocationPath BPEL object or null if not found.
    * @return BPEL def object
    */
   public AeBpelObjectBase getWebModel(String aLocationPath)
   {
      return (AeBpelObjectBase) mPathToWebModelMap.get(aLocationPath); 
   }
       
   /**
    * Adds the variable path.
    * @param aPath XPath to the variable.
    */
   public void addVariablePath(String aPath)
   {
      if (!mVariablePaths.contains(aPath))
      {
         mVariablePaths.add(aPath);
      }
   }
   
   /** 
    * @return Iterator to the list of variable paths (strings).
    */
   public Iterator variablePaths()
   {
      return mVariablePaths.iterator();
   }
   
   /**
    * Adds a link found in the process xml.
    * @param aLink
    */
   public void addLink(AeBpelLinkObject aLink)
   {
     if (!mLinks.containsKey( aLink.getLocationPath()))
     {
        mLinks.put( aLink.getLocationPath(), aLink );
     }
   }
   
   /**
    * Returns the Link given its name.
    * @param aLinkLocationPath link name
    * @return Link associated with this name.
    */
   public AeBpelLinkObject getLink(String aLinkLocationPath)
   {
      return (AeBpelLinkObject) mLinks.get(aLinkLocationPath);  
   }
   
   /**
    * Returns list of Link objects.
    * @return List containing AeBpelLinkObject items.
    */
   public List getLinks()
   {
      List list = new ArrayList();
      Iterator iter = mLinks.keySet().iterator();
      while (iter.hasNext())
      {
         String key = (String) iter.next();
         list.add( mLinks.get(key));
      }
      return list;
   }
   
   /**
    * Returns the root BPEL activity object for this process.  
    * @return root activity model
    */      
   public AeBpelObjectBase getProcessActivity()
   {
      List children = getChildren();
      for (int i = 0; i < children.size(); i++)
      {
         AeBpelObjectBase child = (AeBpelObjectBase) children.get(i);
         if (child instanceof AeBpelActivityObject)
         {
            return child;
         }
      }// for
      return null;
   }

   /**
    * Returns the global fault handlers model for this process.
    * @return faultHandlers model.
    */
   public AeBpelObjectBase getFaultHandlers()
   {
      List children = getChildren();
      for (int i = 0; i < children.size(); i++)
      {
         AeBpelObjectBase child = (AeBpelObjectBase) children.get(i);
         if (child instanceof AeBpelFaultHandlersObject)
         {
            return child;
         }
      }// for
      return null;
   }
   
   /**
    * Returns global compensation handler for this process.
    * @return compensationHandler model.
    */
   public AeBpelObjectBase getCompensationHandler()
   {
      List children = getChildren();
      for (int i = 0; i < children.size(); i++)
      {
         AeBpelObjectBase child = (AeBpelObjectBase) children.get(i);
         if (child instanceof AeBpelCompensationHandlerObject)
         {
            return child;
         }
      }// for
      return null;
   }
   
   /**
    * Returns global termination handler for this process.
    * @return terminationHandler model.
    */
   public AeBpelObjectBase getTerminationHandler()
   {
      List children = getChildren();
      for (int i = 0; i < children.size(); i++)
      {
         AeBpelObjectBase child = (AeBpelObjectBase) children.get(i);
         if (child instanceof AeBpelTerminationHandlerObject)
         {
            return child;
         }
      }
      return null;
   }
   
   /**
    * Returns the global event handler for the process.
    * @return eventHandlers model.
    */
   public AeBpelObjectBase getEventHandlers()
   {
      List children = getChildren();
      for (int i = 0; i < children.size(); i++)
      {
         AeBpelObjectBase child = (AeBpelObjectBase) children.get(i);
         if (child instanceof AeBpelEventHandlersObject)
         {
            return child;
         }
      }// for
      return null;
   }
   
   /**
    * @return Returns the coordinator.
    */
   public boolean isCoordinator()
   {
      return mCoordinator;
   }

   /**
    * @param aCoordinator The coordinator to set.
    */
   public void setCoordinator(boolean aCoordinator)
   {
      mCoordinator = aCoordinator;
   }

   /**
    * @return Returns the participant.
    */
   public boolean isParticipant()
   {
      return mParticipant;
   }

   /**
    * @param aParticipant The participant to set.
    */
   public void setParticipant(boolean aParticipant)
   {
      mParticipant = aParticipant;
   }

   /**
    * Returns the equivalent BPEL state given the process state code.
    * @param aProcessStateCode process state code
    * @return BPEL activity state.
    */
   protected String getProcessStateFromCode(int aProcessStateCode)
   {
      switch (aProcessStateCode)
      {
         case IAeBusinessProcess.PROCESS_LOADED :
            return PROCESS_STATE_LOADED;
         case IAeBusinessProcess.PROCESS_RUNNING:
            return PROCESS_STATE_RUNNING;
         case IAeBusinessProcess.PROCESS_SUSPENDED:
               return PROCESS_STATE_SUSPENDED;
         case IAeBusinessProcess.PROCESS_COMPLETE :
            return PROCESS_STATE_COMPLETED;
         case IAeBusinessProcess.PROCESS_FAULTED :
            return PROCESS_STATE_FAULTED;
         case IAeBusinessProcess.PROCESS_COMPENSATABLE:
            return PROCESS_STATE_COMPENSATABLE;
         case -1:
            return ""; //$NON-NLS-1$         
         default:
            return PROCESS_STATE_UNKNOWN;
      }
   }   

   /**
    * Returns the equivalent BPEL state given the process state code and state reason.
    * @param aProcessStateCode process state code
    * @param aProcessStateReason process state reason code
    * @return BPEL activity state.
    */
   protected String getProcessStateFromCode(int aProcessStateCode, int aProcessStateReason)
   {
      String stateStr = getProcessStateFromCode(aProcessStateCode);
      if (PROCESS_STATE_SUSPENDED.equals(stateStr))
      {
         if (aProcessStateReason == AeSuspendReason.SUSPEND_CODE_AUTOMATIC)
            stateStr = PROCESS_STATE_SUSPENDED_FAULTING;
         else if (aProcessStateReason == AeSuspendReason.SUSPEND_CODE_LOGICAL)
            stateStr = PROCESS_STATE_SUSPENDED_ACTIVITY;
         else if (aProcessStateReason == AeSuspendReason.SUSPEND_CODE_MANUAL)
            stateStr = PROCESS_STATE_SUSPENDED_MANUAL;
         else if (aProcessStateReason == AeSuspendReason.SUSPEND_CODE_INVOKE_RECOVERY)
            stateStr = PROCESS_STATE_SUSPENDED_INVOKE_RECOVERY;
      }
      
      return stateStr;
   }   
}
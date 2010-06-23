//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/processview/AeBpelObjectBase.java,v 1.11 2008/01/10 18:49:39 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//                   PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.processview;


import java.util.List;

import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.graph.IAeXmlDefGraphNodeAdapter;

/**
 * Base model representing an element in a BPEL definition. In addition
 * to standard BPEL element name attribute, this object also stores
 * the current state information based on state document xml. The objects
 * based on this class are used to build HTML tables and or Java Script
 * based tree views to represent the Process's current state.
 * <p/>
 */
public class AeBpelObjectBase
{
   /** BPEL tag name */
   private String mTagName = null;

   /** Name attribute for the given element in the BPEL definition. */
   private String mName = ""; //$NON-NLS-1$

   /** The current state of the activity based on the State xml document. */
   private String mState = ""; //$NON-NLS-1$ //AeBpelState.INACTIVE.toString();

   /** Indicates if this activity is queued by the engine */
   private boolean mQueued = false;

   /** Parent, if one exists, in the composite structer. */
   private AeBpelObjectContainer mParent = null;

   /** Location path of this object with respect to the Process. */
   private String mLocationPath = null;

   /** BPEL object definition */
   private AeBaseXmlDef mDef = null;

   /** Image icon name, normally same as the tag name. */
   private String mIconName = null;

   /** Controller type key used to map the model to view controller.
    * Typically, the controllers are created based on the BPEL object tag name.
    */
   private String controllerType = null;

   /** Adapter for getting visual properties */
   private IAeXmlDefGraphNodeAdapter mAdapter;
   
   /** Display on outline only */
   private boolean mDisplayOutlineOnly;
   
   /**
    * Constructs the BPEL web model given the definition tagname and the definition.
    * @param aTagName tag name
    * @param aDef bpel definition.
    */
   public AeBpelObjectBase(String aTagName, AeBaseXmlDef aDef)
   {
      this(aTagName, aTagName, aDef, "", ""); //$NON-NLS-1$ //$NON-NLS-2$
   }

   /**
    * Constructs the BPEL web model given the definition tagname and the definition.
    * @param aTagName tag name
    * @param aIconName image icon name.
    * @param aDef bpel definition.
    */
   public AeBpelObjectBase(String aTagName, String aIconName, AeBaseXmlDef aDef)
   {
      this(aTagName, aIconName, aDef, "", ""); //$NON-NLS-1$ //$NON-NLS-2$
   }

   /**
    * Ctor.
    * @param aTagName tag name
    * @param aDef bpel definition.
    * @param aName activity name attribute
    * @param aLocationPath location path of the definition.
    */
   public AeBpelObjectBase(String aTagName, AeBaseXmlDef aDef, String aName, String aLocationPath)
   {
      this(aTagName, aTagName, aDef, aName, aLocationPath);
   }

   /**
    * Ctor.
    * @param aTagName tag name
    * @param aIconName tag icon image name.
    * @param aDef bpel definition.
    * @param aName activity name attribute
    * @param aLocationPath location path of the definition.
    */
   public AeBpelObjectBase(String aTagName, String aIconName, AeBaseXmlDef aDef, String aName, String aLocationPath)
   {
      setTagName(aTagName);
      setIconName(aIconName);
      setDef(aDef);
      setName(aName);
      setLocationPath(aLocationPath);
      setControllerType(aTagName);
   }
   
   /**
    * Ctor.
    * @param aTagName tag name
    * @param aIconName tag icon image name.
    * @param aDef bpel definition.
    * @param aName activity name attribute
    * @param aLocationPath location path of the definition.
    * @param aDisplayOutlineOnly
    */
   public AeBpelObjectBase(String aTagName, String aIconName, AeBaseXmlDef aDef, String aName, String aLocationPath, boolean aDisplayOutlineOnly)
   {
      setTagName(aTagName);
      setIconName(aIconName);
      setDef(aDef);
      setName(aName);
      setLocationPath(aLocationPath);
      setControllerType(aTagName);
      setDisplayOutlineOnly(aDisplayOutlineOnly);
   }

   /**
    * @return Returns the def.
    */
   public AeBaseXmlDef getDef()
   {
      return mDef;
   }

   /**
    * @param aDef The def to set.
    */
   protected void setDef(AeBaseXmlDef aDef)
   {
      mDef = aDef;
   }

   /**
    * @return Returns the iconName.
    */
   public String getIconName()
   {
      return mIconName;
   }

   /**
    * @param aIconName The iconName to set.
    */
   public void setIconName(String aIconName)
   {
      mIconName = aIconName;
   }

   /**
    * @return Returns the controllerType.
    */
   public String getControllerType()
   {
      return controllerType;
   }

   /**
    * @param aControllerType The controllerType to set.
    */
   public void setControllerType(String aControllerType)
   {
      controllerType = aControllerType;
   }

   /**
    * Sets the XPath to this object in a Process tree.
    * @param aPath path to this object.
    */
   public void setLocationPath(String aPath)
   {
      mLocationPath = aPath;
   }

   /**
    * @return The XPath location of this object.
    */
   public String getLocationPath()
   {
      return mLocationPath;
   }

   /**
    * @return The parent, if this is a container in the composite pattern.
    */
   public AeBpelObjectContainer getParent()
   {
      return mParent;
   }

   /**
    * Sets the parent of this object.
    * @param aParent
    */
   public void setParent(AeBpelObjectContainer aParent)
   {
      mParent = aParent;
   }

   /**
    * @return The element tag name, which may be used to display in the presentation layer.
    */
   public String getTagName()
   {
      return mTagName;
   }

   /**
    * Mutator to set the tag name.
    * @param aTagName
    */
   public void setTagName(String aTagName)
   {
      mTagName = aTagName;
   }

   /**
    * @return Returns the element's name attribute. Note that this maybe an empty
    * string since not all elements in a deployed process have names.
    */
   public String getName()
   {
      return mName;
   }

   /**
    * Sets the element's name attribute.
    * @param aName
    */
   public void setName(String aName)
   {
      mName = aName;
   }

   /**
    * @return Returns the current execution state of this object.
    * @see org.activebpel.rt.bpel.impl.IAeImplStateNames for enumeration of states.
    */
   public String getState()
   {
      return mState;
   }

   /**
    * Sets the objects execution state based on the state XML document.
    * @param aState current execution state.
    * @see org.activebpel.rt.bpel.impl.IAeImplStateNames for enumeration of states.
    */
   public void setState(String aState)
   {
      mState = aState;
   }

   /**
    * Returns the key used to obtain a the state string for the property listing.
    */
   public String getDisplayStateKey()
   {
      // the display state is the same for all activities, except at the process level (which uses the process state, instead of the activity state)
      return getState();
   }

   /**
    * Returns the state string that is used for the visual.
    */
   public String getDisplayState()
   {
      // the display state is the same for all activities, except at the process level (which uses the process state, instead of the activity state)
      //
      return getState();
   }

   /**
    * @return True if this object is queued for execution by the engine.
    */
   public boolean isQueued()
   {
      return mQueued;
   }

   /**
    * Sets the whether or not the object is queued for execution.
    * @param aQueued
    */
   public void setQueued(boolean aQueued)
   {
      mQueued = aQueued;
   }

   /**
    * Walks up the hierarchy to find the variable given its ncname.
    * @param aVariableName name of variable
    * @return variable model.
    */
   public AeBpelObjectBase findVariable(String aVariableName)
   {
      if (getTagName().equalsIgnoreCase("variable") && getName().equals(aVariableName))//$NON-NLS-1$
      {
         return this;
      }
      AeBpelObjectBase  rVal = null;
      AeBpelObjectBase parentObj = getParent();
      while (rVal == null && parentObj != null && (parentObj instanceof AeBpelObjectContainer))
      {
         AeBpelObjectContainer container = (AeBpelObjectContainer)parentObj;
         List variables = container.getChildren("variables");//$NON-NLS-1$
         if (variables.size() > 0)
         {
            variables = ( (AeBpelObjectContainer)variables.get(0)).getChildren();
         }
         else
         {
            variables = container.getChildren("variable");//$NON-NLS-1$
         }
         if (variables != null && variables.size() > 0)
         {
            for (int i = 0; i < variables.size(); i++)
            {
               AeBpelObjectBase var = (AeBpelObjectBase) variables.get(i);
               if (var.getName().equals(aVariableName))
               {
                  rVal = var;
                  break;
               }
            }
         }// if
         parentObj = parentObj.getParent();
      }
      return rVal;
   }

   /**
    * Walks up the hierarchy to find the CorrealtionSet given its ncname.
    * @param aSetName name of correlationset
    * @return CorrelationSet model.
    */
   public AeBpelObjectBase findCorrelationSet(String aSetName)
   {
      if (getTagName().equalsIgnoreCase("correlationSet") && getName().equals(aSetName))//$NON-NLS-1$
      {
         return this;
      }
      AeBpelObjectBase  rVal = null;
      AeBpelObjectBase parentObj = getParent();
      while (rVal == null && parentObj != null && (parentObj instanceof AeBpelObjectContainer))
      {
         AeBpelObjectContainer container = (AeBpelObjectContainer)parentObj;
         List corrSetsList = container.getChildren("correlationSets");//$NON-NLS-1$
         if (corrSetsList.size() > 0)
         {
            List corrSets = ( (AeBpelObjectContainer) corrSetsList.get(0)).getChildren();
            for (int i = 0; i < corrSets.size(); i++)
            {
               AeBpelObjectBase cs = (AeBpelObjectBase) corrSets.get(i);
               if (cs.getName().equals(aSetName))
               {
                  rVal = cs;
                  break;
               }
            }

         }
         parentObj = parentObj.getParent();
      }
      return rVal;
   }

   /**
    * Finds the parnerLink defined in the process given its ncname.
    * @param aPartnerLinkName name of partner link
    * @return PartnerLinke model or null if not found.
    */
   public AeBpelObjectBase findPartnerLink(String aPartnerLinkName)
   {
      if (getTagName().equalsIgnoreCase("partnerLink") && getName().equals(aPartnerLinkName))//$NON-NLS-1$
      {
         return this;
      }
      AeBpelObjectBase  rVal = null;
      AeBpelObjectContainer parent = getParent();
      while (parent != null && rVal == null)
      {
         if (parent instanceof AeBpelScopeObject)
         {
            rVal = ((AeBpelScopeObject) parent).getPartnerLink(aPartnerLinkName);
         }
         parent = parent.getParent();
      }
      return rVal;
   }

   /**
    * Returns the BPEL object found in the process given its location path expression.
    * @param aLocationPath activity  path
    * @return Activity object if found or null otherwise.
    */
   public AeBpelObjectBase findByLocationPath(String aLocationPath)
   {
      AeBpelObjectBase  rVal = null;
      try
      {
         AeBpelProcessObject process = getRootProcess();
         rVal = process.getWebModel(aLocationPath);
      } catch(Exception e) {
         e.printStackTrace();
      }
      return rVal;
   }

   /**
    * Returns the root process object model.
    * @return root model if this activity is a child of a process or null if not.
    */
   public AeBpelProcessObject getRootProcess()
   {
      if (this instanceof AeBpelProcessObject)
      {
         return (AeBpelProcessObject)this;
      }

      AeBpelProcessObject  rVal = null;
      AeBpelObjectBase parentObj = getParent();
      while (parentObj != null && !(parentObj instanceof AeBpelProcessObject))
      {
         parentObj = parentObj.getParent();
      }
      if ((parentObj instanceof AeBpelProcessObject))
      {
         rVal = (AeBpelProcessObject)parentObj;
      }
      return rVal;
   }

   /**
    * Overrides method to return the BPEL tag name.
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      return getTagName();
   }

   /**
    * @return the adapter
    */
   protected IAeXmlDefGraphNodeAdapter getAdapter()
   {
      return mAdapter;
   }

   /**
    * @param aAdapter the adapter to set
    */
   protected void setAdapter(IAeXmlDefGraphNodeAdapter aAdapter)
   {
      mAdapter = aAdapter;
   }
   
   /**
    * Returns true when the adapter is set
    */
   protected boolean hasAdapter()
   {
      return getAdapter() == null ? false : true;
   }
   
   /**
    * Returns true when this is an outline only node
    */
   public boolean isDisplayOutlineOnly()
   {
      return mDisplayOutlineOnly;
   }

   /**
    * @param aDisplayOutlineOnly the displayOutlineOnly to set
    */
   protected void setDisplayOutlineOnly(boolean aDisplayOutlineOnly)
   {
      mDisplayOutlineOnly = aDisplayOutlineOnly;
   }

}

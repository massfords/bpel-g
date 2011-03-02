//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/AeChildExtensionActivityDef.java,v 1.5 2007/11/27 02:49:47 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.activity; 

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.IAeConditionParentDef;
import org.activebpel.rt.bpel.def.IAeFromParentDef;
import org.activebpel.rt.bpel.def.IAeFromPartsParentDef;
import org.activebpel.rt.bpel.def.IAeToPartsParentDef;
import org.activebpel.rt.bpel.def.IAeVariableParentDef;
import org.activebpel.rt.bpel.def.activity.support.AeConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromPartDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef;
import org.activebpel.rt.bpel.def.activity.support.AeSourcesDef;
import org.activebpel.rt.bpel.def.activity.support.AeTargetsDef;
import org.activebpel.rt.bpel.def.activity.support.AeToPartDef;
import org.activebpel.rt.bpel.def.activity.support.AeToPartsDef;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.AeDocumentationDef;
import org.activebpel.rt.xml.def.AeExtensionAttributeDef;
import org.activebpel.rt.xml.def.AeExtensionElementDef;
import org.activebpel.rt.xml.def.AeXmlDefUtil;
import org.activebpel.rt.xml.def.IAeExtensionDefUnderstoodAdapter;
import org.activebpel.rt.xml.def.IAeExtensionObject;
import org.activebpel.rt.xml.def.IAeExtensionObjectParentDef;

/**
 * Def used to model the contents of an extension activity that is understood
 * by the engine. These extension activities will have been registered with the
 * engine prior to loading the process into a def. 
 * 
 * An extension activity can be a basic activity or a structured activity. As a
 * structured activity the def may contain other activities as children, including
 * other extension activities.
 */
public class AeChildExtensionActivityDef extends AeAbstractExtensionActivityDef 
implements IAeAlarmParentDef, IAeFromParentDef, IAeConditionParentDef, 
      IAeFromPartsParentDef, IAeToPartsParentDef, IAeVariableParentDef,
      IAeExtensionObjectParentDef
{
   /** Extension element object */
   private IAeExtensionObject mExtensionObject;
   
   /** Child Defs for the understood extension activity */
   private List mChildDefs = new ArrayList();
   
    /** Maintains the order of elements added for serialization */
   private List mOrderedDefs;
   
   /**
    * Default c'tor.
    */
   public AeChildExtensionActivityDef()
   {
      super();
   }
   
   /*
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeAlarmParentDef#addAlarmDef(org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef)
    */
   public void addAlarmDef(AeOnAlarmDef aAlarm)
   {
      addChildDef(aAlarm);
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeAlarmParentDef#getAlarmDefs()
    */
   public Iterator getAlarmDefs()
   {
      return getChildren(AeOnAlarmDef.class).iterator();
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeFromParentDef#getFromDef()
    */
   public AeFromDef getFromDef()
   {
      return (AeFromDef) getChild(AeFromDef.class);
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeFromParentDef#setFromDef(org.activebpel.rt.bpel.def.activity.support.AeFromDef)
    */
   public void setFromDef(AeFromDef aFrom)
   {
      addChildDef(aFrom);
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeConditionParentDef#getConditionDef()
    */
   public AeConditionDef getConditionDef()
   {
      return (AeConditionDef) getChild(AeConditionDef.class);
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeConditionParentDef#setConditionDef(org.activebpel.rt.bpel.def.activity.support.AeConditionDef)
    */
   public void setConditionDef(AeConditionDef aCondition)
   {
      addChildDef(aCondition);
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeFromPartsParentDef#getFromPartDefs()
    */
   public Iterator getFromPartDefs()
   {
      return getChildren(AeFromPartDef.class).iterator();
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeFromPartsParentDef#getFromPartsDef()
    */
   public AeFromPartsDef getFromPartsDef()
   {
      return (AeFromPartsDef) getChild(AeFromPartsDef.class);
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeFromPartsParentDef#setFromPartsDef(org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef)
    */
   public void setFromPartsDef(AeFromPartsDef aDef)
   {
      addChildDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeToPartsParentDef#getToPartDefs()
    */
   public Iterator getToPartDefs()
   {
      return getChildren(AeToPartDef.class).iterator();
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeToPartsParentDef#getToPartsDef()
    */
   public AeToPartsDef getToPartsDef()
   {
      return (AeToPartsDef) getChild(AeToPartsDef.class);
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeToPartsParentDef#setToPartsDef(org.activebpel.rt.bpel.def.activity.support.AeToPartsDef)
    */
   public void setToPartsDef(AeToPartsDef aDef)
   {
      addChildDef(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeVariableParentDef#getVariableDef(java.lang.String)
    */
   public AeVariableDef getVariableDef(String aVariableName)
   {
      return (AeVariableDef) getChild(AeVariableDef.class);
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeExtensionActivityDef#isUnderstood()
    */
   public boolean isUnderstood()
   {
      return (getExtensionObject() != null);
   }

   /**
    * @return the childDefs
    */
   public Iterator getChildDefs()
   {
      return mChildDefs.iterator();
   }

   /**
    * @param aChildDefs the childDefs to set
    */
   public void addChildDef(AeBaseDef aChildDefs)
   {
      mChildDefs.add(aChildDefs);
      getOrderedDefs().add(aChildDefs);
   }

   /**
    * Returns all of the children that can be assigned to the class or interface passed in
    * @param aClass
    */
   public List getChildren(Class aClass)
   {
      ArrayList list = new ArrayList();
      for (Iterator iter = getChildDefs(); iter.hasNext(); )
      {
         Object obj = iter.next();
         if (aClass.isAssignableFrom(obj.getClass()))
         {
            list.add(obj);
         }
      }
      return list;
   }

   /**
    * Gets the single child that can be assigned to the class or interface passed in.
    * If there are more than one then return the first child
    * @param aClass
    */
   public AeBaseXmlDef getChild(Class aClass)
   {
      List list = getChildren(aClass);
      if (list.size() >= 1)
         return (AeBaseXmlDef) list.get(0);
      else 
         return null;
   }

   /**
    * @see org.activebpel.rt.xml.def.IAeExtensionObjectParentDef#getExtensionObject()
    */
   public IAeExtensionObject getExtensionObject()
   {
      return mExtensionObject;
   }

   /**
    * @see org.activebpel.rt.xml.def.IAeExtensionObjectParentDef#setExtensionObject(org.activebpel.rt.xml.def.IAeExtensionObject)
    */
   public void setExtensionObject(IAeExtensionObject aExtensionObject)
   {
      mExtensionObject = aExtensionObject;
      AeXmlDefUtil.installDef(mExtensionObject, this);
   }

   /**
    * @see org.activebpel.rt.xml.def.AeBaseXmlDef#isExtensionUnderstood(org.activebpel.rt.xml.def.AeExtensionElementDef)
    */
   public boolean isExtensionUnderstood(AeExtensionElementDef aExtensionElementDef)
   {
      if (getExtensionObject() != null)
      {
         IAeExtensionDefUnderstoodAdapter validator = (IAeExtensionDefUnderstoodAdapter) getExtensionObject().getAdapter(IAeExtensionDefUnderstoodAdapter.class);
         if (validator != null)
         {
            return validator.isUnderstood(aExtensionElementDef);
         }
      }
      return false;
   }

   /**
    * @see org.activebpel.rt.xml.def.AeBaseXmlDef#isExtensionUnderstood(org.activebpel.rt.xml.def.AeExtensionAttributeDef)
    */
   public boolean isExtensionUnderstood(
         AeExtensionAttributeDef aExtensionAttributeDef)
   {
      if (getExtensionObject() != null)
      {
         IAeExtensionDefUnderstoodAdapter validator = (IAeExtensionDefUnderstoodAdapter) getExtensionObject().getAdapter(IAeExtensionDefUnderstoodAdapter.class);
         if (validator != null)
         {
            return validator.isUnderstood(aExtensionAttributeDef);
         }
      }
      return false;
   }

   /**
    * @return the elementOrder
    */
   public List getOrderedDefs()
   {
      if(mOrderedDefs == null)
         mOrderedDefs = new ArrayList();
      return mOrderedDefs;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.AeActivityDef#setTargetsDef(org.activebpel.rt.bpel.def.activity.support.AeTargetsDef)
    */
   public void setTargetsDef(AeTargetsDef aTargetsDef)
   {
     super.setTargetsDef(aTargetsDef);
     getOrderedDefs().add(aTargetsDef);
   }
   

   /**
    * @see org.activebpel.rt.bpel.def.AeActivityDef#setSourcesDef(org.activebpel.rt.bpel.def.activity.support.AeSourcesDef)
    */
   public void setSourcesDef(AeSourcesDef aSourcesDef)
   {
      super.setSourcesDef( aSourcesDef);
      getOrderedDefs().add(aSourcesDef);
   }

   /**
    * @see org.activebpel.rt.xml.def.AeBaseXmlDef#addDocumentationDef(org.activebpel.rt.xml.def.AeDocumentationDef)
    */
   public void addDocumentationDef(AeDocumentationDef aDocumentationDef)
   {
      super.addDocumentationDef(aDocumentationDef);
      getOrderedDefs().add(aDocumentationDef);
   }

   /**
    * @see org.activebpel.rt.xml.def.AeBaseXmlDef#addExtensionAttributeDef(org.activebpel.rt.xml.def.AeExtensionAttributeDef)
    */
   public void addExtensionAttributeDef( AeExtensionAttributeDef aExtension )
   {
      super.addExtensionAttributeDef( aExtension );
      getOrderedDefs().add( aExtension );
   }
   
   /**
    * @see org.activebpel.rt.xml.def.AeBaseXmlDef#addExtensionElementDef(org.activebpel.rt.xml.def.AeExtensionElementDef)
    */
   public void addExtensionElementDef( AeExtensionElementDef aExtension )
   {
      super.addExtensionElementDef( aExtension );
      getOrderedDefs().add( aExtension );
   }
   
}

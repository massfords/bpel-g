//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/AeAbstractExtensionActivityDef.java,v 1.4 2007/12/03 22:15:31 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.activity; 


import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AeActivityDef;

/**
 * Base class for defs that model the contents of an extension activity.
 * Extension activities are either understood or not understood. Those that are
 * understood add new behavior to the engine through an extensibility layer.
 * Those that are not understood, either cause the process to fail to validate
 * (assuming that they were marked as "mustUnderstand") or convert to empty
 * activities and are essentially no-ops.
 */
public abstract class AeAbstractExtensionActivityDef extends AeActivityDef implements IAeExtensionActivityDef
{
   /** The activity's element name. */
   private QName mElementName;

   /**
    * @return Returns the elementName.
    */
   public QName getElementName()
   {
      return mElementName;
   }

   /**
    * @param aElementName The elementName to set.
    */
   public void setElementName(QName aElementName)
   {
      mElementName = aElementName;
   }

   /**
    * @see org.activebpel.rt.bpel.def.activity.IAeExtensionActivityDef#getNamespace()
    */
   public String getNamespace()
   {
      return getElementName().getNamespaceURI();
   }
}
 
// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeIndexedPropertyTEI.java,v 1.1 2007/04/24 17:23:13 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.tags;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * TagExtraInfo impl for setting indexed property values as
 * scripting objects on the JSP page. 
 */
public class AeIndexedPropertyTEI extends TagExtraInfo
{
   /**
    * @see javax.servlet.jsp.tagext.TagExtraInfo#getVariableInfo(javax.servlet.jsp.tagext.TagData)
    */
   public VariableInfo[] getVariableInfo( TagData aData )
   {
      String name = aData.getId();

      return new VariableInfo[] {
         newVariableInfo(name, aData.getAttributeString("indexedClassName")), //$NON-NLS-1$
         newVariableInfo(name + "Index", AeIndexedPropertyTag.class.getName()) //$NON-NLS-1$
      };
   }

   /**
    * Returns new <code>VariableInfo</code> instance for the given name and
    * class name.
    *
    * @param aName
    * @param aClassName
    */
   protected VariableInfo newVariableInfo(String aName, String aClassName)
   {
      return new VariableInfo(aName, aClassName, true, VariableInfo.NESTED);
   }

   /**
    * Always returns true.
    */
   public boolean isValid()
   {
      return true;
   }
}

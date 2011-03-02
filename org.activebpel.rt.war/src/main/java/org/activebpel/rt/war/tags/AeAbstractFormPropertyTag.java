//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.war/src/org/activebpel/rt/war/tags/AeAbstractFormPropertyTag.java,v 1.1 2007/04/24 17:23:13 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.war.tags;

/**
 * Base class used to set form parameters into a bean
 */
public class AeAbstractFormPropertyTag extends AeAbstractBeanPropertyTag
{
   /** Name of the param to read from the request */
   private String mParam;
   
   /**
    * Gets the param value that's converted to a date.
    */
   protected String getParamValue()
   {
      return pageContext.getRequest().getParameter(getParam());
   }

   /**
    * @return Returns the param.
    */
   public String getParam()
   {
      return mParam;
   }
   /**
    * @param aParam The param to set.
    */
   public void setParam(String aParam)
   {
      mParam = aParam;
   }   

}

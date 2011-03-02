//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/graph/AeXmlDefGraphNodeProperty.java,v 1.2 2008/02/13 01:45:55 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2004-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def.graph;

import org.activebpel.rt.util.AeUtil;


/**
 * This is a light weight implementation for represeting properties of HI extension element
 */
public class AeXmlDefGraphNodeProperty implements IAeXmlDefGraphNodeProperty
{

   /** Name of property */
   private String mName;
   /** Value of property */
   private String mValue;
   /** is this a detail proeprty */
   private boolean mDetail;
   /** is this a variable */
   private boolean mVariable;
   /** location path */
   private String mLocationPath;
   /** remove flag */
   private boolean mRemove;
   
   /**
    * C'tor
    * @param aName
    * @param aValue
    * @param aDetail
    */
   public AeXmlDefGraphNodeProperty(String aName, String aValue, boolean aDetail)
   {
      mName = aName;
      mValue = aValue;
      mDetail = aDetail;
   }

   /**
    * C'tor
    * @param aName
    * @param aValue
    * @param aLocationPath
    */
   public AeXmlDefGraphNodeProperty(String aName, String aValue, String aLocationPath)
   {
      mName = aName;
      mValue = aValue;
      mLocationPath = aLocationPath;
   }
   
   /**
    * C'tor
    * @param aName
    * @param aValue
    * @param aVariable
    * @param aDetail
    */
   public AeXmlDefGraphNodeProperty(String aName, String aValue, boolean aVariable, boolean aDetail)
   {
      mName = aName;
      mValue = aValue;
      mDetail = aDetail;
      mVariable = aVariable;
   }
   
   /**
    * @see org.activebpel.rt.xml.def.graph.IAeXmlDefGraphNodeProperty#getName()
    */
   public String getName()
   {
      return mName;
   }

   /**
    * @see org.activebpel.rt.xml.def.graph.IAeXmlDefGraphNodeProperty#getValue()
    */
   public String getValue()
   {
      return mValue;
   }

   /**
    * @see org.activebpel.rt.xml.def.graph.IAeXmlDefGraphNodeProperty#isDetail()
    */
   public boolean isDetail()
   {
      return mDetail;
   }

   /**
    * @see org.activebpel.rt.xml.def.graph.IAeXmlDefGraphNodeProperty#isHasLocationPath()
    */
   public boolean isHasLocationPath()
   {
      return AeUtil.notNullOrEmpty(getLocationPath());
   }

   /**
    * @see org.activebpel.rt.xml.def.graph.IAeXmlDefGraphNodeProperty#isVariable()
    */
   public boolean isVariable()
   {
      return mVariable;
   }

   /**
    * @see org.activebpel.rt.xml.def.graph.IAeXmlDefGraphNodeProperty#getLocationPath()
    */
   public String getLocationPath()
   {
      return mLocationPath;
   }

   /**
    * @param aLocationPath the locationPath to set
    */
   protected void setLocationPath(String aLocationPath)
   {
      mLocationPath = aLocationPath;
   }

   /**
    * @return the remove
    */
   public boolean isRemove()
   {
      return mRemove;
   }

   /**
    * @param aRemove the remove to set
    */
   public void setRemove(boolean aRemove)
   {
      mRemove = aRemove;
   }

}

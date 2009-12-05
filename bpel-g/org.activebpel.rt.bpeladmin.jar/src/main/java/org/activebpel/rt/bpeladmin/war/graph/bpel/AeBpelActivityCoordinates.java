//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/bpel/AeBpelActivityCoordinates.java,v 1.1 2005/04/18 18:31:57 pjayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.bpel;

import java.awt.Rectangle;

import org.activebpel.rt.bpeladmin.war.web.processview.AeBpelObjectBase;

/**
 * A simple object that contains a BPEL activity's bounding rectangle,
 * the activity model and its location path. This is used for hit testing.
 * The coordinates of the bounding rectangle is relative to the root container.
 * 
 */
public class AeBpelActivityCoordinates
{
   /** Location path (xpath) of the activity */
   private String mLocationPath = null;
   /** Hit test bounds */
   private Rectangle mBounds = null;
   /** BPEL model associated with this area */
   private AeBpelObjectBase mBpelModel = null;
   
   /**
    * Default constructor. 
    */
   public AeBpelActivityCoordinates()
   {
   }
   
   /**
    * @return Returns the bounds.
    */
   public Rectangle getBounds()
   {
      return mBounds;
   }
   
   /**
    * @param aBounds The bounds to set.
    */
   public void setBounds(Rectangle aBounds)
   {
      mBounds = aBounds;
   }
   
   /**
    * @return Returns the bpelModel.
    */
   public AeBpelObjectBase getBpelModel()
   {
      return mBpelModel;
   }
   
   /**
    * @param aBpelModel The bpelModel to set.
    */
   public void setBpelModel(AeBpelObjectBase aBpelModel)
   {
      mBpelModel = aBpelModel;
      setLocationPath(mBpelModel.getLocationPath());
   }
   
   /**
    * @return Returns the locationPath.
    */
   public String getLocationPath()
   {
      return mLocationPath;
   }
   
   /**
    * @param aLocationPath The locationPath to set.
    */
   public void setLocationPath(String aLocationPath)
   {
      mLocationPath = aLocationPath;
   }
}

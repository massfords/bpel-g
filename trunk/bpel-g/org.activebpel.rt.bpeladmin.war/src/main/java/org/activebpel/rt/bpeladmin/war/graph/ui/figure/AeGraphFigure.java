//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/ui/figure/AeGraphFigure.java,v 1.1 2005/04/18 18:32:01 pjayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//             PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph.ui.figure;

import org.activebpel.rt.bpeladmin.war.graph.ui.AeContainer;

/**
 * Basic implementation of a figure.
 */
public class AeGraphFigure extends AeContainer implements IAeGraphFigure
{

   /**
    * Default constructor.
    */
   public AeGraphFigure()
   {
      this(null);
   }

   /**
    * Constructor a figure with the given name.
    * @param aName figure or component name.
    */   
   public AeGraphFigure(String aName)  
   {
      super(aName);
   }   
}

// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/graph/AeGraphImageParameters.java,v 1.2 2007/04/11 17:54:59 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.graph;

/**
 * Struct to hold graph image parameters.
 */
public class AeGraphImageParameters
{
   // These have to be declared public for .NET.
   public long mProcessId;
   public int mDeploymentProcessId;
   public int mPlanId;
   public int mPart;
   public String mPivotPath;
   public int mTileWidth;
   public int mTileHeight;
   public int mGridRow;
   public int mGridColumn;
   public String mSessionId;
}
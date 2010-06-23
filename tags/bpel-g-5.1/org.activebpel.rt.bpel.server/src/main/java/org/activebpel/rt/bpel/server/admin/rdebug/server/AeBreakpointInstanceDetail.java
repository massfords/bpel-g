// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/admin/rdebug/server/AeBreakpointInstanceDetail.java,v 1.2 2006/02/24 16:37:31 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.admin.rdebug.server;

import java.io.Serializable;

import javax.xml.namespace.QName;

/**
 * JavaBean for holding some data for a single breakpoint definition.
 */
public class AeBreakpointInstanceDetail implements Serializable
{
   /** name of the process */
   private QName mProcessName;
   /** node path of this breakpoint */
   private String mNodePath;

   /**
    * No-arg constructor
    */
   public AeBreakpointInstanceDetail()
   {
   }
   
   /**
    * Getter for the process name
    */
   public QName getProcessName()
   {
      return mProcessName;
   }

   /**
    * Setter for the name
    * @param aName
    */
   public void setProcessName(QName aName)
   {
      mProcessName = aName;
   }
   
   /**
    * Getter for the node path where this breakpoint is defined.
    * 
    * @return String
    */
   public String getNodePath()
   {
      return mNodePath;
   }

   /**
    * Setter for the node path.
    * 
    * @param aNodePath The node path to set.
    */
   public void setNodePath(String aNodePath)
   {
      mNodePath = aNodePath;
   }
}

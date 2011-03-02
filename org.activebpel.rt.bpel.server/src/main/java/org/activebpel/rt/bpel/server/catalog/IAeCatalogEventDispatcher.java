// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/catalog/IAeCatalogEventDispatcher.java,v 1.1 2006/07/18 20:05:33 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.catalog;

/**
 * Provides convenience methods for dispatching an <code>AeCatalogEvent</code> to the
 * appropriate IAeCatalogListener handler methods.
 */
public interface IAeCatalogEventDispatcher
{
   /** sends the event to the onDeployment handler method */
   public static final IAeCatalogEventDispatcher DEPLOY_SENDER = new IAeCatalogEventDispatcher()
   {
      public void dispatch( IAeCatalogListener aListener, AeCatalogEvent aEvent )
      {
         aListener.onDeployment( aEvent );
      }
   };
   
   /** sends the event to the onRemoval handler method */
   public static final IAeCatalogEventDispatcher REMOVE_SENDER = new IAeCatalogEventDispatcher()
   {
      public void dispatch( IAeCatalogListener aListener, AeCatalogEvent aEvent )
      {
         aListener.onRemoval( aEvent );
      }
   };
   
   /** sends the event to the onDuplicateDeployment handler method */
   public static final IAeCatalogEventDispatcher WARNING_SENDER = new IAeCatalogEventDispatcher()
   {
      public void dispatch( IAeCatalogListener aListener, AeCatalogEvent aEvent )
      {
         aListener.onDuplicateDeployment( aEvent );
      }
   };


   /**
    * Dispatch the event to the appropriate listener method.
    * @param aListener
    * @param aEvent
    */
   public void dispatch( IAeCatalogListener aListener, AeCatalogEvent aEvent );
}

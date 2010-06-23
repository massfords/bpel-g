// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/handlers/AeSetClassLoaderHandler.java,v 1.1 2006/03/09 14:28:19 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002, 2003, 2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.handlers;

import javax.xml.namespace.QName;

import org.activebpel.rt.axis.bpel.deploy.AeResourceProvider;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.MessageContext;

/**
 * This class loads the correct context classloader for the target service so we
 * can locate service resource effectivly.  Note that 
 * <code>org.activebpel.rt.tomcat.AeResetClassLoader</code> will put
 * back the original context class loader.
 */
public class AeSetClassLoaderHandler extends AeRestoreClassLoaderHandler
{
   /** the property to save the classloader in. */
   public static final String SAVED_CLASSLOADER_PROPERTY = "aeOldClassLoader"; // $NON_NLS1 //$NON-NLS-1$
   
   /**
    * Switches context class loaders to that saved for the target service.
    * @see org.apache.axis.Handler#invoke(org.apache.axis.MessageContext)
    */
   public void invoke(MessageContext aMessageContext)
   {
      EngineConfiguration engineConfig = aMessageContext.getAxisEngine().getConfig();

      if (engineConfig instanceof AeResourceProvider)
      {
         AeResourceProvider config = (AeResourceProvider) engineConfig;
         ClassLoader newLoader =
            config.getMyDeployment().getClassLoader(new QName(null, aMessageContext.getTargetService()));
         if (newLoader != null)
         {
            ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
            if (!newLoader.equals(currentLoader))
            {
               aMessageContext.setProperty(SAVED_CLASSLOADER_PROPERTY, currentLoader);
               Thread.currentThread().setContextClassLoader(newLoader);
            }
         }
      }
   }

}

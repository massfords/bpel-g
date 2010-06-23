//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/AeInvokeHandlerFactory.java,v 1.6 2005/06/22 16:53:54 MFord Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine;

import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.wsio.invoke.IAeInvoke;
import org.activebpel.wsio.invoke.IAeInvokeHandler;

/**
 * Factory access to the appropriate <code>IAeInvokeHandler</code> for a given
 * <code>IAeInvoke</code> object.
 */
public class AeInvokeHandlerFactory implements IAeInvokeHandlerFactory
{
   /** Default invoke handler. */
   protected IAeInvokeHandler mDefaultInvokeHandler;
   
   /**
    * Constructor.
    * @throws AeException
    */
   public AeInvokeHandlerFactory( Map aConfig ) throws AeException
   {
      String invokeHandlerClassname = (String)aConfig.get( IAeEngineConfiguration.CLASS_ENTRY );
      IAeInvokeHandler invokeHandler = createDefaultInvokeHandler( invokeHandlerClassname );
      setDefaultInvokeHandler(invokeHandler);
   }
   
   /**
    * Create the default invoke handler.  This is the handler that will be used
    * if no custom invoke handlers are specified on the partner role section
    * of a partner link.
    * @param aClassName
    * @throws AeBusinessProcessException
    */
   protected IAeInvokeHandler createDefaultInvokeHandler( String aClassName )
   throws AeBusinessProcessException
   {
      return createInvokeHandlerObject( aClassName );
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAeInvokeHandlerFactory#createInvokeHandler(org.activebpel.wsio.invoke.IAeInvoke)
    */
   public IAeInvokeHandler createInvokeHandler(IAeInvoke aInvoke) throws AeBusinessProcessException
   {
      String customInvokeHandlerUri = aInvoke.getInvokeHandler();
      if( AeUtil.notNullOrEmpty( customInvokeHandlerUri ) )
      {
         return createCustomInvokeHandler( customInvokeHandlerUri );
      }
      else
      {
         return getDefaultInvokeHandler();
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.IAeInvokeHandlerFactory#getQueryData(org.activebpel.wsio.invoke.IAeInvoke)
    */
   public String getQueryData(IAeInvoke aInvoke)
   {
      return AeInvokeHandlerUri.getQueryString( aInvoke.getInvokeHandler() );
   }

   /**
    * Create the custom <code>IAeInvokeHandler</code> impl.
    * @param aCustomInvokerUri
    * @throws AeBusinessProcessException
    */
   protected IAeInvokeHandler createCustomInvokeHandler( String aCustomInvokerUri ) throws AeBusinessProcessException
   {
      String invoker = AeInvokeHandlerUri.getInvokerString( aCustomInvokerUri );
      return createInvokeHandlerObject( invoker );
   }
   
   /**
    * Instantiate the <code>IAeInvokeHandler</code> impl.
    * @param aClassName
    * @throws AeBusinessProcessException
    */
   protected IAeInvokeHandler createInvokeHandlerObject( String aClassName ) throws AeBusinessProcessException
   {
      try
      {
         return (IAeInvokeHandler) Class.forName( aClassName ).newInstance();
      }
      catch( Throwable t )
      {
         throw new AeBusinessProcessException( AeMessages.getString("AeInvokeHandlerFactory.ERROR_0") + aClassName, t ); //$NON-NLS-1$
      }
   }
   
   /**
    * @return Returns the defaultInvokeHandler.
    */
   protected IAeInvokeHandler getDefaultInvokeHandler()
   {
      return mDefaultInvokeHandler;
   }
   
   /**
    * @param aDefaultInvokeHandler The defaultInvokeHandler to set.
    */
   protected void setDefaultInvokeHandler(IAeInvokeHandler aDefaultInvokeHandler)
   {
      mDefaultInvokeHandler = aDefaultInvokeHandler;
   }
}

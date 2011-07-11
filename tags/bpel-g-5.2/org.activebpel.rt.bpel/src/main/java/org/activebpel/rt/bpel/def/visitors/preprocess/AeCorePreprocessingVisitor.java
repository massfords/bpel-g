//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/preprocess/AeCorePreprocessingVisitor.java,v 1.3.4.1 2008/04/21 16:09:43 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors.preprocess;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef;
import org.activebpel.rt.bpel.def.adapter.IAeCorePreprocessingAdapter;
import org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor;
import org.activebpel.rt.bpel.def.visitors.AeDefTraverser;
import org.activebpel.rt.bpel.def.visitors.AeTraversalVisitor;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.AeExtensionElementDef;
import org.activebpel.rt.xml.def.IAeAdapter;
import org.activebpel.rt.xml.def.IAeExtensionObject;

/**
 * <p>
 * Implementation of a visitor to perform core preprocessing on extensions.
 * </p>
 * <p>Applies an extension adapter to a bpel extension element.</p>
 */
public class AeCorePreprocessingVisitor extends AeAbstractDefVisitor
{
   /** An exception caught during the preprocessing */
   private AeException mException; 

   /**
    * C'tor
    */
   public AeCorePreprocessingVisitor()
   {
      setTraversalVisitor(new AeTraversalVisitor(new AeDefTraverser(), this));
   }

   /**
    * @see org.activebpel.rt.xml.def.visitors.AeBaseXmlDefVisitor#visit(org.activebpel.rt.xml.def.AeExtensionElementDef)
    */
   public void visit(AeExtensionElementDef aDef)
   {
      IAeExtensionObject extObj = aDef.getExtensionObject();
      if ( extObj != null )
      {
         preprocess(aDef, extObj);
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef)
    */
   public void visit(AeChildExtensionActivityDef aDef)
   {
      IAeExtensionObject extObj = aDef.getExtensionObject();
      if ( extObj != null )
      {
         preprocess(aDef, extObj);
      }
   }

   /**
    * Runs the preprocessing visitor on the extension object
    * @param aDef
    * @param aExtObj
    */
   private void preprocess(AeBaseXmlDef aDef, IAeExtensionObject aExtObj)
   {
      IAeAdapter adapter = aExtObj.getAdapter(IAeCorePreprocessingAdapter.class);
      if ( adapter != null )
      {
         try
         {
            ((IAeCorePreprocessingAdapter)adapter).preprocessForCore(aDef);
         }
         catch (AeException e)
         {
            if (getException() == null)
               setException(e);
         }
      }
   }

   /**
    * @return the exception
    */
   public AeException getException()
   {
      return mException;
   }

   /**
    * @param aException the exception to set
    */
   protected void setException(AeException aException)
   {
      mException = aException;
   }
}

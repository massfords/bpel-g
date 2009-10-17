// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io.readers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.activity.support.AeExpressionBaseDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.def.activity.support.AeLiteralDef;
import org.activebpel.rt.bpel.def.io.IAeBpelReader;
import org.activebpel.rt.bpel.def.visitors.AeDefCreateInvokeScopeVisitor;
import org.activebpel.rt.bpel.def.visitors.AeDefPartnerLinkNameVisitor;
import org.activebpel.rt.bpel.def.visitors.AeDefTraverser;
import org.activebpel.rt.bpel.def.visitors.AeDefVisitorFactory;
import org.activebpel.rt.bpel.def.visitors.AeTraversalVisitor;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;
import org.activebpel.rt.bpel.def.visitors.preprocess.AeCorePreprocessingVisitor;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.io.IAeDefRegistry;
import org.activebpel.rt.xml.def.io.readers.AeDomTraverser;
import org.activebpel.rt.xml.def.io.readers.AeDomTraverser.IAeTraversalFilter;
import org.activebpel.rt.xml.def.visitors.AeDefAssignParentVisitor;
import org.activebpel.rt.xml.def.visitors.IAeDefPathVisitor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Base class for readers that use the registry to drive the deserialization process.
 */
public class AeRegistryBasedBpelReader implements IAeBpelReader
{
   /** bpel reader/writer registry */
   private IAeDefRegistry mBpelRegistry;
   
   /**
    * Ctor accepts BpelRegistry
    * @param aBpelRegistry
    */
   public AeRegistryBasedBpelReader(IAeDefRegistry aBpelRegistry)
   {
      mBpelRegistry = aBpelRegistry;
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.IAeBpelReader#readBPEL(org.w3c.dom.Document)
    */
   public AeProcessDef readBPEL(Document aBpelDoc) throws AeException
   {
      Element processElement = aBpelDoc.getDocumentElement();

      AeDomTraverser traverser = createBpelDomTraverser(processElement,
            getBpelRegistry());
      traverser.traverseRootElement( processElement );

      AeProcessDef def = (AeProcessDef) traverser.getRootDef();
      def.setNamespace(processElement.getNamespaceURI());
      runCoreVisitors(def);
      return def;
   }

   /**
    * Factory method used to create a bpel dom traverser.
    * 
    * @param aProcessElement
    * @param aReg
    */
   public static AeDomTraverser createBpelDomTraverser(Element aProcessElement, IAeDefRegistry aReg)
   {
      String ns = aProcessElement.getNamespaceURI();
      IAeTraversalFilter filter = null;
      if (IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI.equals(ns))
      {
         filter = new IAeTraversalFilter()
         {
            /**
             * @see org.activebpel.rt.xml.def.io.readers.AeDomTraverser.IAeTraversalFilter#shouldTraverseChildren(org.activebpel.rt.xml.def.AeBaseXmlDef, org.w3c.dom.Element)
             */
            public boolean shouldTraverseChildren(AeBaseXmlDef aDef, Element aElement)
            {
               return !(aDef instanceof AeLiteralDef) && !(aDef instanceof AeExpressionBaseDef);
            }
         };
      }
      else
      {
         filter = new IAeTraversalFilter()
         {
            /**
             * @see org.activebpel.rt.xml.def.io.readers.AeDomTraverser.IAeTraversalFilter#shouldTraverseChildren(org.activebpel.rt.xml.def.AeBaseXmlDef, org.w3c.dom.Element)
             */
            public boolean shouldTraverseChildren(AeBaseXmlDef aDef, Element aElement)
            {
               return !(aDef instanceof AeFromDef) && !(aDef instanceof AeExpressionBaseDef);
            }
            
         };
      }
      AeDomTraverser traverser = new AeDomTraverser(aReg);
      traverser.setTraversalFilter(filter);
      return traverser;
   }

   /**
    * This is a post load step that assigns parents to all child defs and also gives each child a unique path.
    * It also pulls up Invoke "implicit" scopes.
    *
    * @throws AeBusinessProcessException
    */
   public void runCoreVisitors(AeProcessDef aDef) throws AeException
   {
      // assign parents for definitions -- needed here so we can resolve resources up through the model
      AeDefAssignParentVisitor parentVisitor = new AeDefAssignParentVisitor();
      parentVisitor.setTraversalVisitor( new AeTraversalVisitor(new AeDefTraverser(), parentVisitor));
      aDef.accept(parentVisitor);

      // assign extension objects on extension elements, attributes and activities if they are present
      // give the extension activities a chance to contribute implicit variables
      IAeDefVisitor assignExtObjVisitor= AeDefVisitorFactory.getInstance(aDef.getNamespace()).createAssignExtensionVisitor();
      aDef.accept(assignExtObjVisitor);

      // create any implicit defs prior to assigning parents
      IAeDefVisitor implicitVisitor = AeDefVisitorFactory.getInstance(aDef.getNamespace()).createImplicitVariableVisitor();
      aDef.accept(implicitVisitor);

      // assign parents (again) for definitions
      // need to run through the parents again to ensure that the implicit variables are parented
      parentVisitor.setTraversalVisitor( new AeTraversalVisitor(new AeDefTraverser(), parentVisitor));
      aDef.accept(parentVisitor);

      // pull up any invoke scopes (for invokes that have implicit scopes)
      AeDefCreateInvokeScopeVisitor invokeScopeVizzy = new AeDefCreateInvokeScopeVisitor();
      aDef.accept(invokeScopeVizzy);

      IAeDefVisitor messageExchangeVisitor = AeDefVisitorFactory.getInstance(aDef.getNamespace()).createMessageExchangeVisitor();
      aDef.accept(messageExchangeVisitor);

      assignPaths(aDef);

      AeDefPartnerLinkNameVisitor plNameVisitor = new AeDefPartnerLinkNameVisitor();
      aDef.accept(plNameVisitor);
      
      // Perform extension core preprocessing
      AeCorePreprocessingVisitor  corePreprocessingVisitor = new AeCorePreprocessingVisitor();
      aDef.accept(corePreprocessingVisitor);
      
      if (corePreprocessingVisitor.getException() != null)
         throw corePreprocessingVisitor.getException();
   }

   /**
    * Assigns location paths (and ids) to each of the def objects and records these
    * paths on the process def.
    * @param aDef
    */
   protected void assignPaths(AeProcessDef aDef)
   {
      // assign location paths for definitions
      IAeDefPathVisitor pathVisitor = AeDefVisitorFactory.getInstance(aDef.getNamespace()).createDefPathVisitor();
      pathVisitor.visit(aDef);

      // populate bidirectional maps between location paths and location ids
      Map locationPathsToIds = new HashMap();
      for (Iterator i = pathVisitor.getLocationPaths().iterator(); i.hasNext(); )
      {
         String locationPath = (String) i.next();
         int locationId = pathVisitor.getLocationId(locationPath);
         locationPathsToIds.put(locationPath, new Integer(locationId));
      }
      aDef.setLocationPathsToIds(locationPathsToIds);
   }

   /**
    * Internal accessor for bpel registry
    * @return bpel registry
    */
   protected IAeDefRegistry getBpelRegistry()
   {
      return mBpelRegistry;
   }

}

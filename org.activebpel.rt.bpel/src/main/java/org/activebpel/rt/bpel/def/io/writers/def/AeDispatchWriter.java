// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/writers/def/AeDispatchWriter.java,v 1.9 2007/09/26 02:21:04 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io.writers.def;

import org.activebpel.rt.bpel.def.AeBaseContainer;
import org.activebpel.rt.bpel.def.AeEventHandlersDef;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.io.writers.AeDelegatingDefWriter;
import org.activebpel.rt.xml.def.io.writers.IAeDefWriter;
import org.activebpel.rt.xml.def.io.writers.IAeDefWriterFactory;
import org.w3c.dom.Element;

/**
 * Generic writer class for serializing AeDef objects.
 * Creates an element with the given tag name
 * and uses an instance of the AeWriterVisitor to 
 * extract specific attributes based on object type.
 */
public class AeDispatchWriter extends AeDelegatingDefWriter implements IAeDefWriter
{
   /**
    * Constructor. Creates the writer with the specified namespace uri and tagname.
    * 
    * @param aNamespaceUri
    * @param aTagName
    */
   public AeDispatchWriter(String aNamespaceUri, String aTagName, IAeDefWriterFactory aWriterVisitorFactory)
   {
      super(aNamespaceUri, aTagName, aWriterVisitorFactory);
   }

   /**
    * @see org.activebpel.rt.xml.def.io.writers.IAeDefWriter#createElement(AeBaseXmlDef, org.w3c.dom.Element)
    */
   public Element createElement(AeBaseXmlDef aBaseDef, Element aParentElement)
   {
      if( isEmptyContainer(aBaseDef) || isImplicitVariable(aBaseDef))
      {
         return null;
      }
      
      return super.createElement(aBaseDef, aParentElement);
   }
   
   /**
    * Tests to see if the def passed in is an implicit variable. These variables
    * are not written to the xml.
    * 
    * @param aBaseDef
    */
   protected boolean isImplicitVariable(AeBaseXmlDef aBaseDef)
   {
      if (aBaseDef instanceof AeVariableDef)
      {
         return ((AeVariableDef)aBaseDef).isImplicit();
      }
      return false;
   }

   /**
    * Used to determine if the base def is an empty container.
    * Empty container elements are not added to the bpel xml.
    * @param aBaseDef
    * @return true is the base def is an AeBaseContainer with no contents
    */
   private boolean isEmptyContainer(AeBaseXmlDef aBaseDef)
   {
      if (aBaseDef instanceof AeBaseContainer)
         return ((AeBaseContainer) aBaseDef).isEmpty();
      else if (aBaseDef instanceof AeEventHandlersDef)
         return !((AeEventHandlersDef) aBaseDef).hasEventHandler();

      return false;
   }
}

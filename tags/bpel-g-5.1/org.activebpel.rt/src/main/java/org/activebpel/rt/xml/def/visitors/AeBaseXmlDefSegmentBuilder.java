//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/def/visitors/AeBaseXmlDefSegmentBuilder.java,v 1.1 2007/11/15 22:30:10 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.def.visitors; 

import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.AeDocumentationDef;
import org.activebpel.rt.xml.def.AeExtensionAttributeDef;
import org.activebpel.rt.xml.def.AeExtensionElementDef;
import org.activebpel.rt.xml.def.IAePathSegmentBuilder;

/**
 * base class for visitors that setup location paths for defs 
 */
public class AeBaseXmlDefSegmentBuilder implements IAePathSegmentBuilder, IAeBaseXmlDefVisitor
{
   /** segment for the def */
   private String mPathSegment;

   /**
    * @see org.activebpel.rt.xml.def.IAePathSegmentBuilder#createPathSegment(org.activebpel.rt.xml.def.AeBaseXmlDef)
    */
   public String createPathSegment(AeBaseXmlDef aDef)
   {
      setPathSegment(null);
      aDef.accept(this);
      return getPathSegment();
   }

   /**
    * @see org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor#visit(org.activebpel.rt.xml.def.AeBaseXmlDef)
    */
   public void visit(AeBaseXmlDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor#visit(org.activebpel.rt.xml.def.AeDocumentationDef)
    */
   public void visit(AeDocumentationDef aDef)
   {
      setPathSegment("documentation"); //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor#visit(org.activebpel.rt.xml.def.AeExtensionAttributeDef)
    */
   public void visit(AeExtensionAttributeDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor#visit(org.activebpel.rt.xml.def.AeExtensionElementDef)
    */
   public void visit(AeExtensionElementDef aDef)
   {
      setPathSegment(aDef.getElementQName().getLocalPart());
   }

   /**
    * @return the segment
    */
   public String getPathSegment()
   {
      return mPathSegment;
   }

   /**
    * @param aSegment the segment to set
    */
   public void setPathSegment(String aSegment)
   {
      mPathSegment = aSegment;
   }

}
 
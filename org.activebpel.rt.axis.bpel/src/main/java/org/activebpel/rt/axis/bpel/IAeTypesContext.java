//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/IAeTypesContext.java,v 1.2 2005/06/22 17:10:13 MFord Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel; 

import javax.xml.namespace.QName;

import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.XMLType;

/**
 * Defines methods for looking up schema types. 
 */
public interface IAeTypesContext
{
   /**
    * Finds the element with the given name or returns null if not found within
    * the current context.
    * 
    * @param aElementName
    */
   public ElementDecl findElement(QName aElementName);
   
   /**
    * Finds the type with the given name or returns null if not found within
    * the current context.
    * 
    * @param aTypeName
    */
   public XMLType findType(QName aTypeName);
}
 
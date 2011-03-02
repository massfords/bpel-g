//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis/src/org/activebpel/rt/axis/ser/AeSimpleValueWrapper.java,v 1.1 2005/04/15 13:44:03 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.ser; 


/**
 * A wrapper for a derived simple type. If we add the simple type directly to the
 * rpc param then it will be serialized as a simple type and not its derived type.
 * For example, the type we're wrapping might be a java.lang.Integer. However, the
 * schema type might be ns1:OrderId as opposed to xsd:int. If we added the Integer
 * directly to the rpc param then it'll be serialized as an xsd:int. The use of this
 * wrapper class enables our custom serializer to get called and handle it properly.
 */
public class AeSimpleValueWrapper
{
   /** The type that we're wrapping */
   private Object mDelegate; 

   public AeSimpleValueWrapper(Object aSimpleType)
   {
      mDelegate = aSimpleType;
   }
   
   /**
    * Getter for the delegate.
    */
   public Object getDelegate()
   {
      return mDelegate;
   }
}
 
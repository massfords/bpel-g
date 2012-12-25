//$Header: /Development/AEDevelopment/projects/org.activebpel.wsio/src/org/activebpel/wsio/IAeWsAddressingConstants.java,v 1.1 2006/08/08 16:37:50 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.wsio;

public interface IAeWsAddressingConstants
{
   /** local part of the Anonymous role URI */
   public static final String WSA_ANONYMOUS_ROLE = "/role/anonymous"; //$NON-NLS-1$
   /** default ws-addressing namespace prefix */
   public static final String WSA_NS_PREFIX = "wsa"; //$NON-NLS-1$
   /** local part of the relationship type used in 2004 versions of the spec*/
   public static final String WSA_REPLY_RELATION = "Reply";  //$NON-NLS-1$
   /** local part of the relationship type used in 2003 versions of the spec*/
   public static final String WSA_RESPONSE_RELATION = "Response";  //$NON-NLS-1$
   /** URI of default action */
   public static final String WSA_DEFAULT_ACTION = "http://schemas.active-endpoints.com/ws/2006/08/DefaultAction"; //$NON-NLS-1$
   /** URI of WSA fault action */
   public static final String WSA_FAULT_ACTION_ = "/fault"; //$NON-NLS-1$
   /** Attribute name for RelatesTo RelationshipType */
   public static final String WSA_RELATIONSHIP_TYPE = "RelationshipType"; //$NON-NLS-1$
   /** Web Services Addressing namespace declaration. */
   public static final String WSA_NAMESPACE_URI = "http://schemas.xmlsoap.org/ws/2003/03/addressing";    //$NON-NLS-1$
   /** Web Services Addressing namespace declaration. */
   public static final String WSA_NAMESPACE_URI_2004_03 = "http://schemas.xmlsoap.org/ws/2004/03/addressing";    //$NON-NLS-1$
   /** Web Services Addressing namespace declaration. */
   public static final String WSA_NAMESPACE_URI_2004_08 = "http://schemas.xmlsoap.org/ws/2004/08/addressing";    //$NON-NLS-1$
   
}

// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/lock/IAeLockerSerializationNames.java,v 1.3 2005/02/01 19:53:00 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.lock;

/**
 * Defines constants for <code>AeLockerSerializer</code> and
 * <code>AeLockerDeserializer</code>.
 */
interface IAeLockerSerializationNames
{
   public static final String TAG_LOCK          = "lock"; //$NON-NLS-1$
   public static final String TAG_LOCKS         = "locks"; //$NON-NLS-1$
   public static final String TAG_OWNER         = "owner"; //$NON-NLS-1$
   public static final String TAG_REQUEST       = "request"; //$NON-NLS-1$
   public static final String TAG_REQUESTS      = "requests"; //$NON-NLS-1$
   public static final String TAG_ROOT          = "variableLocker"; //$NON-NLS-1$
   public static final String TAG_VARIABLE      = "variable"; //$NON-NLS-1$
   public static final String ATTR_EXCLUSIVE    = "exclusive"; //$NON-NLS-1$
   public static final String ATTR_OWNERPATH    = "ownerPath"; //$NON-NLS-1$
   public static final String ATTR_VARIABLEPATH = "variablePath"; //$NON-NLS-1$
}

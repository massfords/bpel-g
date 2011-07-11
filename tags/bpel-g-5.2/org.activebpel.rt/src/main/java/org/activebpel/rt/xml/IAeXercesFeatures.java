// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/IAeXercesFeatures.java,v 1.2 2008/01/31 23:10:55 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xml;

/**
 * Supported Xerces features 
 */
public interface IAeXercesFeatures
{
   /** Enable XInclude processing. */
   public static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude"; //$NON-NLS-1$
   /** Perform base URI fixup as specified by the XInclude Recommendation. */
   public static final String XINCLUDE_FIXUP_BASE_URI_FEATURE = "http://apache.org/xml/features/xinclude/fixup-base-uris"; //$NON-NLS-1$
   /** Perform language fixup as specified by the XInclude Recommendation. */
   public static final String XINCLUDE_FIXUP_LANG_FEATURE = "http://apache.org/xml/features/xinclude/fixup-language"; //$NON-NLS-1$
}

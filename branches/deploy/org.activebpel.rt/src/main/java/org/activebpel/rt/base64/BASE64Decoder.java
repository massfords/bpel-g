// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/base64/BASE64Decoder.java,v 1.1 2005/08/09 19:57:37 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.base64;

import java.io.IOException;

/**
 * Replacement for <code>sun.misc.BASE64Decoder</code> that uses public domain
 * Base64 implementation.
 */
public class BASE64Decoder
{
   public byte[] decodeBuffer(String aEncodedString) throws IOException
   {
      return Base64.decode(aEncodedString);
   }
}

// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/attachment/AeAttachmentFileInputStream.java,v 1.1 2007/05/24 00:57:19 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.attachment;

import java.io.File;
import java.io.FileNotFoundException;

import org.activebpel.rt.util.AeBlobInputStream;

/**
 * Extends {@link AeBlobInputStream} to automatically add a reference to an
 * {@link AeAttachmentFile}.
 */
public class AeAttachmentFileInputStream extends AeBlobInputStream
{
   /**
    * Constructs a new input stream on the given file.
    *
    * @param aFile
    * @throws FileNotFoundException
    */
   public AeAttachmentFileInputStream(File aFile) throws FileNotFoundException
   {
      super(aFile);
      
      if (aFile instanceof AeAttachmentFile)
      {
         ((AeAttachmentFile) aFile).addReference();
      }
   }
}

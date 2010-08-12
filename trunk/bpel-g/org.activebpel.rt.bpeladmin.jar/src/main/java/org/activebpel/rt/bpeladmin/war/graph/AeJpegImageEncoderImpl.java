//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/AeJpegImageEncoderImpl.java,v 1.3 2005/04/20 20:19:42 pjayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//                      PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.graph;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.activebpel.rt.AeException;

/**
 * Implements IAeImageEncoder based on Sun's JPEG Image Encoder class.
 */
public class AeJpegImageEncoderImpl implements IAeImageEncoder
{

   /**
    * Default constructer.
    *
    */
   public AeJpegImageEncoderImpl()
   {
   }

   /**
    * @return the MIME content-type for JPEG images.
    * @see org.activebpel.rt.bpeladmin.war.graph.IAeImageEncoder#getContentType()
    */
   public String getContentType()
   {
      return "image/jpeg";//$NON-NLS-1$
   }

   /**
    * Encodes the image as JPEG stream.
    * @param aImage buffered image
    * @param aOutputStream codec output stream 
    * @see org.activebpel.rt.bpeladmin.war.graph.IAeImageEncoder#encode(java.awt.image.BufferedImage, java.io.OutputStream)
    */
   public void encode(BufferedImage aImage, OutputStream aOutputStream) throws IOException, AeException
   {
      try
      {
          ImageIO.write(aImage, "jpg", aOutputStream);
          aOutputStream.flush();
          aOutputStream.close();
      }
      catch(IOException io)
      {
         throw io;
      }
      catch(Throwable t)
      {
         throw new AeException(t);
      }
      
   }

}

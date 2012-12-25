//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/graph/IAeImageEncoder.java,v 1.1 2005/04/18 18:31:51 pjayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
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

import org.activebpel.rt.AeException;

/**
 * Interface the defines contracts for classes that are responsible for
 * encoding a buffered image.
 */
public interface IAeImageEncoder
{
   /**
    * Returns the MIME content-type
    * @return mime content type.
    */
   public String getContentType();
   
   /**
    * Encodes the image into the output stream.
    * @param aImage buffered image
    * @param aOutputStream codec output stream
    * @throws IOException
    * @throws AeException
    */
   public void encode(BufferedImage aImage, OutputStream aOutputStream) throws IOException, AeException;
}

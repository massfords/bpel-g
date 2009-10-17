// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/graph/AeGraphImageDriver.java,v 1.3.4.1 2008/04/21 16:14:10 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.graph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpeladmin.war.AeMessages;
import org.activebpel.rt.bpeladmin.war.graph.AeBpelGraph;
import org.activebpel.rt.bpeladmin.war.graph.AeGraphProperties;
import org.activebpel.rt.bpeladmin.war.graph.AeJpegImageEncoderImpl;
import org.activebpel.rt.bpeladmin.war.graph.IAeImageEncoder;
import org.activebpel.rt.bpeladmin.war.graph.bpel.AeBpelImageResources;
import org.activebpel.rt.bpeladmin.war.web.processview.AeProcessViewBase;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeUtil;

/**
 * Draws the BPEL graph on an off-screen graphics context and sends the rendered
 * image to an instance of {@link IAeGraphImageResponse}.
 */
public class AeGraphImageDriver
{
   /** Buffered image's color depth (8 or 16 bits) */
   private int mImageColorDepth = 8;

   /** Max memory (in MB) allocated for the graph image. A value of &lt;= 0 means it is unbounded. */
   private int mMaxMemory = -1;

   /** The target response object for this driver. */
   private IAeGraphImageResponse mResponse;

   /** The image encoder. */
   private IAeImageEncoder mCodec;

   /**
    * Default constructor.
    */
   public AeGraphImageDriver()
   {
      this(null);
   }

   /**
    * Constructs a graph image driver that sends its output to the given
    * response object.
    *
    * @param aResponse
    */
   public AeGraphImageDriver(IAeGraphImageResponse aResponse)
   {
      // Initialize the image color depth and the maximum graph memory.
      AeGraphProperties graphProperties = AeGraphProperties.getInstance();
      setImageColorDepth(graphProperties.getPropertyInt(AeGraphProperties.COLOR_DEPTH, 8));
      setMaxMemory(graphProperties.getPropertyInt(AeGraphProperties.MAX_MEMORY, 0));

      mResponse = aResponse;
   }

   /**
    * Returns the target response object.
    */
   protected IAeGraphImageResponse getResponse()
   {
      return mResponse;
   }

   /**
    * Sets the target response object.
    */
   protected void setResponse(IAeGraphImageResponse aResponse)
   {
      mResponse = aResponse;
   }

   /**
    * Generates the graph image specified by the given parameters object.
    *
    * @param aParams
    * @param aCache
    */
   public void sendGraphImage(AeGraphImageParameters aParams, AeProcessViewCache aCache)
   {
      try
      {
         // if the graphing module is not enabled, simply stream out the 'Image not available' image.
         if (!AeGraphProperties.getInstance().getPropertyBoolean(AeGraphProperties.ENABLE_GRAPHING, true))
         {
            sendErrorImageStream();
            return;
         }

         // get the bpel graph
         AeProcessViewBase pv = getProcessView(aParams, aCache);
         AeBpelGraph graph = pv.getBpelGraph();

         // default case is 1 tile enclosing the full graph.
         int row = 1;
         int col = 1;
         int width = graph.getWidth();
         int height = graph.getHeight();
         if (aParams.mGridRow > 0 && aParams.mGridColumn > 0 && aParams.mTileWidth > 0 && aParams.mTileHeight > 0)
         {
            // override tiling based on request params.
            row = aParams.mGridRow;
            col = aParams.mGridColumn;
            width = aParams.mTileWidth;
            height = aParams.mTileHeight;
         }

         IAeImageEncoder codec = createImageEncoder(aParams);
         setImageEncoder(codec);

         if (codec != null)
         {
            // send out the image of the process graph.
            sendImageTile(graph, row, col, width, height);
         }
         else
         {
            // codec is null.
            sendCodecErrorImageStream();
         }
      }
      catch (AeResourceAllocationException re)
      {
         sendResourceErrorImageStream();
         String info = AeMessages.format("AeGraphImageDriver.error", re.getInfo()); //$NON-NLS-1$
         AeException.logWarning(info);
      }
      catch (AeException aex)
      {
         sendErrorImageStream();
         String info = AeMessages.format("AeGraphImageDriver.error", aex.getInfo());  //$NON-NLS-1$
         AeException.logWarning(info);
      }
      catch (Throwable t)
      {
         sendErrorImageStream();
         String info = AeMessages.format("AeGraphImageDriver.error", t.getLocalizedMessage()); //$NON-NLS-1$
         AeException.logWarning(info);
      }
   }

   /**
    * Returns the <code>AeProcessViewBase</code> based on parameters.
    * If a cached version is available, then it will be returned.
    * @param aParams
    */
   protected AeProcessViewBase getProcessView(AeGraphImageParameters aParams, AeProcessViewCache aCache)
   {
      // get processview bean model
      AeProcessViewBase pv = null;
      synchronized (aCache)
      {
         // lookup model lru cache for processview bean model.
         if ( AeUtil.notNullOrEmpty(aParams.mSessionId) )
         {
            pv = aCache.get(aParams.mSessionId) ;
         }
         if (pv == null)
         {
            pv = createProcessView();
            // cache it
            if ( AeUtil.notNullOrEmpty(aParams.mSessionId) )
            {
               aCache.put(aParams.mSessionId, pv) ;
            }
         }
      } // end synch (modelCache)

      // initialize bean model (should only do this once for a group of related tiles.
      synchronized (pv)
      {
         boolean rerender = false;
         // set the forEach parallel single instance pivot path if available and is different to current value.
         if (AeUtil.notNullOrEmpty(aParams.mPivotPath) && !aParams.mPivotPath.equalsIgnoreCase(pv.getPivotPath()))
         {
            rerender = true;
            pv.setPivotPath(aParams.mPivotPath);
         }
         // set process id if pid has changed (or this is the first time being set).
         // this step causes the underlying model to load the process (and state) information from the db.
         // (so, use it iff when needed).
         if (aParams.mProcessId >= 0 && aParams.mProcessId != pv.getProcessId())
         {
            rerender = true;
            // prepare graph for active process details given process id.
            pv.setProcessId(aParams.mProcessId);

         }
         // Set process deployment id (i.e. plan for open src product) if present.
         // (the model also accesses the db - so use it iff when needed).
         else if (aParams.mDeploymentProcessId >= 0 && aParams.mDeploymentProcessId != pv.getProcessDeploymentId())
         {
            rerender = true;
            // prepare graph for deployed process details given deployed process (active bpel) id.
            pv.setProcessDeploymentId(aParams.mDeploymentProcessId);
         }
         // plan id (versioning etc.)
         else if (aParams.mPlanId >= 0 && aParams.mPlanId != pv.getProcessDeploymentId())
         {
            rerender = true;
            // prepare graph for deployed process details given deployed process (versioning) plan id.
            pv.setProcessDeploymentId(aParams.mPlanId);
         }
         // create the AeBpelGraph iff model state has changed.
         if (rerender)
         {
            // setting the part-id starts the procedure to create the AeBpelGraph.
            pv.setPartId(aParams.mPart);
         }
      } // end synch(pv)
      return pv;
   }

   /**
    * Renders a subsection of an image and sends it out as jpeg stream.
    * @param aBpelGraph BPEL process graph container
    * @param aRow tile row offset, starting with 1.
    * @param aCol tile coloumn offset, starting with 1.
    * @param aTileWidth image title width.
    * @param aTileHeight image title height.
    * @throws AeException
    * @throws IOException
    */
   protected void sendImageTile(AeBpelGraph aBpelGraph, int aRow, int aCol, int aTileWidth, int aTileHeight)
   throws AeException, IOException
   {
      if (!aBpelGraph.hasImage())
      {
         AeException.logWarning(AeMessages.getString("AeGraphImageDriver.image_not_available"));  //$NON-NLS-1$
         throw new AeException(AeMessages.getString("AeGraphImageDriver.image_not_available"));//$NON-NLS-1$
      }
      // check if memory allocated for the graph is enough to render it.
      checkMemoryAllocation(getImageColorDepth(), aTileWidth, aTileHeight, getMaxMemory());

      // calculate absolute position in the image.
      int deltaX = aTileWidth * (aCol - 1);
      int deltaY = aTileHeight * (aRow - 1);

      // Create a off screen graphics context via a buffered image.
      Graphics graphics = null;
      try
      {
         BufferedImage image = null;
         int imageType = (getImageColorDepth() == 16) ? BufferedImage.TYPE_USHORT_565_RGB : BufferedImage.TYPE_BYTE_INDEXED;
         image = new BufferedImage(aTileWidth, aTileHeight, imageType);
         graphics = image.createGraphics();
         graphics.setColor(Color.WHITE);
         graphics.fillRect(0,0, aTileWidth, aTileHeight);
         // translate graphics context coords.
         graphics.translate(-deltaX, -deltaY);
         // paint the graph on the off screen graphics.
         aBpelGraph.getGraphContainer().paint(graphics);

         // encode and stream out the content.
         encode(image, graphics);
      }
      catch (IOException ioe)
      {
         throw ioe;
      }
      catch (Throwable t)
      {
         throw new AeException(t);
      }
      finally
      {
         if (graphics != null)
         {
            graphics.dispose();
            graphics = null;
         }
      }
   }

   /**
    * Encodes the given image and sends the encoding to the response stream.
    *
    * <p>The .NET implementation overrides this method to encode the .NET
    * <code>Bitmap</code> that backs the <code>Graphics</code> object in the
    * IKVM runtime with a .NET JPEG encoder.</p>
    *
    * @param aImage
    * @param aGraphics
    * @throws IOException
    * @throws AeException
    */
   protected void encode(BufferedImage aImage, Graphics aGraphics) throws IOException, AeException
   {
      IAeImageEncoder codec = getImageEncoder();

      OutputStream out = getResponse().getOutputStream();
      getResponse().setContentType(codec.getContentType());
      getResponse().addHeader("Cache-Control", "no-cache"); //$NON-NLS-1$ //$NON-NLS-2$

      if (!(out instanceof BufferedOutputStream))
      {
         out = new BufferedOutputStream(out);
      }

      // encodes bi as a JPEG data stream
      codec.encode(aImage, out);
   }

   /**
    * Returns the image encoder to use.
    */
   protected IAeImageEncoder getImageEncoder()
   {
      return mCodec;
   }

   /**
    * Sets the image encoder to use.
    *
    * @param aCodec
    */
   protected void setImageEncoder(IAeImageEncoder aCodec)
   {
      mCodec = aCodec;
   }

   /**
    * Returns a new image encoder, possibly customized by the input parameters.
    */
   protected IAeImageEncoder createImageEncoder(AeGraphImageParameters aParams)
   {
      try
      {
         IAeImageEncoder codec = new AeJpegImageEncoderImpl();
         return codec;
      }
      catch (Throwable t)
      {
         String info = AeMessages.format("AeGraphImageDriver.codec_error", t.getLocalizedMessage()); //$NON-NLS-1$
         AeException.logWarning(info);
         return null;
      }
   }

   /**
    * Returns a new process view object.
    */
   protected AeProcessViewBase createProcessView()
   {
      AeProcessViewBase pv = new AeProcessViewBase();
      return pv;
   }

   /**
    * Streams out the data for a stock (place holder) image if any errors are caught during the graphing process.
    */
   public void sendErrorImageStream()
   {
      sendImageStream("error.png");//$NON-NLS-1$
   }

   /**
    * Streams out the data for a stock (place holder) image for codec related errors.
    */
   public void sendCodecErrorImageStream()
   {
      sendImageStream("errorCodec.png");//$NON-NLS-1$
   }

   /**
    * Streams out the data for a stock (place holder) image if any resource related errors are caught during the graphing process.
    */
   public void sendResourceErrorImageStream()
   {
      sendImageStream("errorResources.png");//$NON-NLS-1$
   }

   /**
    * Streams out the data for the given PNG resource name.
    */
   protected void sendImageStream(String aImageName)
   {
      InputStream in = null;
      try
      {
         getResponse().setContentType("image/png"); //$NON-NLS-1$
         in = AeBpelImageResources.getInstance().getImageInputStream(aImageName);
         OutputStream out = getResponse().getOutputStream();
         byte[] data = new byte[32768];
         int n;
         while ( (n = in.read(data)) != -1 )
         {
            out.write(data,0,n);
         }
      }
      catch (Throwable t)
      {
         // ignore error
      }
      finally
      {
         AeCloser.close(in);
      }// finally
   }

   /**
    * Returns the color depth to be used for the buffered image
    * @return Color depth in bits - 8 or 16.
    */
   protected int getImageColorDepth()
   {
      return mImageColorDepth;
   }

   /**
    * Sets the color depth to be used for the buffered image
    * @param aDepth Color depth in bits - 8 or 16.
    */
   protected void setImageColorDepth(int aDepth)
   {
      if (aDepth != 8 && aDepth != 16)
      {
         AeException.logWarning("Invalid color depth " + aDepth  //$NON-NLS-1$
               + ". Using 8-bit color depth instead."); //$NON-NLS-1$
         aDepth = 8;
      }
      mImageColorDepth = aDepth;
   }

   /**
    * Returns true if the memory required to is less than or equal to the maximum allocated.
    * @param aColorDepth color depth of image.
    * @param aWidth image width
    * @param aHeight image height
    * @param aMaxMb max allocated memory, in mb.
    */
   protected void checkMemoryAllocation(int aColorDepth, int aWidth, int aHeight, int aMaxMb)
      throws AeResourceAllocationException
   {
      if (aMaxMb > 0)
      {
         long available = aMaxMb * 1024 * 1024L;
         long needed = ((long)Math.ceil((double)aColorDepth/8.0)) * (long)(aWidth * aHeight);
         if (needed > available)
         {
            throw new AeResourceAllocationException(available, needed);
         }
      }
   }

   /**
    * @return Returns the maxMemory.
    */
   protected int getMaxMemory()
   {
      return mMaxMemory;
   }

   /**
    * @param aMaxMemory The maxMemory to set (in mb).
    */
   protected void setMaxMemory(int aMaxMemory)
   {
      mMaxMemory = aMaxMemory;
   }

   /**
    * Exception to indicate that the graphics context required more memory than
    * the maximum allocated amount.
    */
   private static class AeResourceAllocationException extends AeException
   {
      /**
       * Default ctor.
       * @param aAllocateBytes maximum allocated memory (bytes)
       * @param aRequiredBytes memory required to render the graph image (bytes).
       */
      public AeResourceAllocationException(long aAllocateBytes, long aRequiredBytes)
      {
         super();
         String info = AeMessages.format("AeGraphImageDriver.resource_error", new Object[] { new Long(aRequiredBytes), new Long(aAllocateBytes) } ); //$NON-NLS-1$
         setInfo(info);
      }
   }
}

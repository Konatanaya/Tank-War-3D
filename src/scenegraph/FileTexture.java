package scenegraph;

/**
   A utility class that represents a texture that is created from
   an image file such as a png, jpg, or gif
   Note that bottom left of image has texture coord (0,0)
   and top right has texture coordinate (1,1)
   @author Andrew Ensor (modified for JSR-231)
*/
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.imageio.ImageIO;
import javax.media.opengl.GL;

public class FileTexture extends Texture
{
   public FileTexture(String filename, GL gl, boolean wrapImage,
      boolean smoothImage)
   {  super(gl, wrapImage, smoothImage);
     
      try
      {  // obtain the image from the file as a BufferedImage
         //BufferedInputStream bis = new BufferedInputStream
         //   (ClassLoader.getSystemResourceAsStream(filename));
         //bis.read();
         //BufferedImage bufferedImage = ImageIO.read(bis);
         BufferedImage bufferedImage = ImageIO.read(new File(filename));
        
         if (bufferedImage == null)
            throw new IllegalArgumentException("Unable to read file");
         // calculate the width and height of texture as power of 2
         int texWidth = nextPowerOfTwo(bufferedImage.getWidth());
         int texHeight = nextPowerOfTwo(bufferedImage.getHeight());
         // determine whether image is RGBA or just RGB and create an 
         // image whose width and height are powers of two and which
         // uses the OpenGL GL_RGBA format
         BufferedImage textureImage;
         if (bufferedImage.getColorModel().hasAlpha())
         {  WritableRaster raster = Raster.createInterleavedRaster
               (DataBuffer.TYPE_BYTE, texWidth, texHeight, 4, null);
            ColorModel colorModel = new ComponentColorModel
               (ColorSpace.getInstance(ColorSpace.CS_sRGB),
               new int[]{8, 8, 8, 8}, true, false,
               ComponentColorModel.TRANSLUCENT, DataBuffer.TYPE_BYTE);
            textureImage = new BufferedImage(colorModel, raster,
               false, null);
         }
         else
         {  WritableRaster raster = Raster.createInterleavedRaster
               (DataBuffer.TYPE_BYTE, texWidth, texHeight, 3, null);
            ColorModel colorModel = new ComponentColorModel
               (ColorSpace.getInstance(ColorSpace.CS_sRGB),
               new int[]{8, 8, 8, 0}, false, false,
               ComponentColorModel.OPAQUE, DataBuffer.TYPE_BYTE);
            textureImage = new BufferedImage(colorModel, raster,
               false, null);
         }
         // write bufferedImage to textureImage so image bytes can
         // be obtained and flip the image vertically since OpenGL
         // has (0,0) at bottom left rather than at top left
         Graphics2D g2d = (Graphics2D) textureImage.getGraphics();
         g2d.translate(0.0, texHeight);
         g2d.scale(1.0, -1.0);
         g2d.drawImage(bufferedImage, 0, 0, null);
         // obtain a ByteBuffer with the image bytes
         byte[] imageBytes = ((DataBufferByte)textureImage.getRaster()
            .getDataBuffer()).getData();
         ByteBuffer imageBuffer = ByteBuffer.allocateDirect
            (imageBytes.length);
         imageBuffer.order(ByteOrder.nativeOrder());
         imageBuffer.put(imageBytes, 0, imageBytes.length);
         imageBuffer.rewind();
         // have OpenGL generate the texture for imageBuffer
         if (bufferedImage.getColorModel().hasAlpha())
            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, texWidth,
               texHeight, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE,
               imageBuffer);
         else
            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, texWidth,
               texHeight, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE,
               imageBuffer);
         
      }
      catch (IOException e)
      {  System.err.println("Error loading texture: " + e);
      }
   }
   
   // helper method that calculates the next power of two as least
   // as large as its parameter
   private int nextPowerOfTwo(int n)
   {  int powerOfTwo = 1;
      while (powerOfTwo < n)
         powerOfTwo *= 2;
      return powerOfTwo;
   }
}

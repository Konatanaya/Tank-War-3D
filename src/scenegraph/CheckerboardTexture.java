package scenegraph;

/**
   A utility class that represents a checkboard texture
   @author Andrew Ensor (modified for JSR-231)
*/
import java.awt.Color;
import java.nio.IntBuffer;
import javax.media.opengl.GL;

public class CheckerboardTexture extends Texture
{
   private final int TEX_SIZE = 128; //width and height a power of two
   
   public CheckerboardTexture(int numRows, int numCols,
      Color evenColour, Color oddColour, GL gl, boolean wrapImage)
   {  super(gl, wrapImage, false); // don't smooth texture
      int[] texels = new int[TEX_SIZE*TEX_SIZE];
      int xPixelsPerSquare = TEX_SIZE/numRows;
      int yPixelsPerSquare = TEX_SIZE/numCols;
      for (int i=0; i<TEX_SIZE; i++)
      {  for (int j=0; j<TEX_SIZE; j++)
         {  Color colour;
            if (((i/yPixelsPerSquare)+(j/xPixelsPerSquare))%2 == 0)
               colour = evenColour;
            else
               colour = oddColour;
            // specify texel as ABGR int
            texels[i*TEX_SIZE+j] = (colour.getAlpha()<<24) |
               (colour.getBlue()<<16) | (colour.getGreen()<<8) |
               colour.getRed();
         }
      }
      IntBuffer buffer = IntBuffer.allocate(texels.length);
      buffer.put(texels);
      buffer.rewind();
      gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, TEX_SIZE,
         TEX_SIZE, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buffer);
   }
}

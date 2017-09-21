package scenegraph;

/**
   An abstract class that represents a texture for OpenGL
   @author Andrew Ensor (modified for JSR-231)
*/
import javax.media.opengl.GL;

public abstract class Texture
{
   protected int textureID; // unique id registered with OpenGL
   int[] idArray;
   
   // performs some of the required operations to create a texture
   // note that subclasses must themselves register the texture data
   public Texture(GL gl, boolean wrapImage, boolean smoothImage)
   {  // obtain an unused id for this texture
      idArray = new int[1];
      gl.glGenTextures(1, idArray, 0);
      textureID = idArray[0];
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureID);
      // set suitable parameters for the texture
      int applyMode = (smoothImage ? GL.GL_LINEAR : GL.GL_NEAREST);
      int wrapMode = (wrapImage ? GL.GL_REPEAT : GL.GL_CLAMP_TO_EDGE);
      gl.glTexParameteri(GL.GL_TEXTURE_2D,
         GL.GL_TEXTURE_MAG_FILTER, applyMode);
      gl.glTexParameteri(GL.GL_TEXTURE_2D,
         GL.GL_TEXTURE_MIN_FILTER, applyMode);
      gl.glTexParameteri(GL.GL_TEXTURE_2D,
         GL.GL_TEXTURE_WRAP_S, wrapMode);
      gl.glTexParameteri(GL.GL_TEXTURE_2D,
         GL.GL_TEXTURE_WRAP_T, wrapMode);      
      
   }
   
   public int getID()
   {  return textureID;
   }
   
   public void deleteTexture(GL gl){
       gl.glDeleteTextures(1, idArray, 0);
       
   }  
}

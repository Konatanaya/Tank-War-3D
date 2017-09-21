/**
   An interface that represents a renderable object in OpenGL
   @author Andrew Ensor
*/
package scenegraph;

import javax.media.opengl.GL;

public interface GLRenderable
{
   public void drawObject(GL gl);
}

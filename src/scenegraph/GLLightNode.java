/**
   A class that represents a light node in a scene graph
   @see GLSceneGraph.java
*/
package scenegraph;

import java.awt.Color;
import javax.media.opengl.GL;
import com.sun.opengl.util.GLUT;

public abstract class GLLightNode extends GLNode
{
   public static final int[] LIGHT_IDS = {GL.GL_LIGHT0, GL.GL_LIGHT1,
      GL.GL_LIGHT2, GL.GL_LIGHT3, GL.GL_LIGHT4, GL.GL_LIGHT5,
      GL.GL_LIGHT6, GL.GL_LIGHT7}; // maximum eight lights in OpenGL
   private Color ambientColour, diffuseColour, specularColour;

   public GLLightNode(String nodeName, GLSceneGraph sceneGraph)
   {  this(nodeName,sceneGraph,Color.BLACK,Color.WHITE,Color.WHITE);
   }

   public GLLightNode(String nodeName, GLSceneGraph sceneGraph,
      Color ambientColour, Color diffuseColour, Color specularColour)
   {  super(nodeName, sceneGraph);
      this.ambientColour = ambientColour;
      this.diffuseColour = diffuseColour;
      this.specularColour = specularColour;
   }

   public Color getAmbientColour()
   {  return ambientColour;
   }

   public void setAmbientColour(Color ambientColour)
   {  this.ambientColour = ambientColour;
   }

   public Color getDiffuseColour()
   {  return diffuseColour;
   }

   public void setDiffuseColour(Color diffuseColour)
   {  this.diffuseColour = diffuseColour;
   }

   public Color getSpecularColour()
   {  return specularColour;
   }

   public void setSpecularColour(Color specularColour)
   {  this.specularColour = specularColour;
   }
}

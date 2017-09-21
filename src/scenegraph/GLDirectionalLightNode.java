/**
   A class that represents a directional light node in a scene graph
   with direction pointing along the negative z-axis
   @see GLSceneGraph.java
*/
package scenegraph;

import java.awt.Color;
import javax.media.opengl.GL;
import com.sun.opengl.util.GLUT;

public class GLDirectionalLightNode extends GLLightNode
{

   public GLDirectionalLightNode(String nodeName,
      GLSceneGraph sceneGraph)
   {  super(nodeName, sceneGraph);
   }

   public GLDirectionalLightNode(String nodeName,
      GLSceneGraph sceneGraph, Color ambientColour,
      Color diffuseColour, Color specularColour)
   {  super(nodeName, sceneGraph, ambientColour, diffuseColour,
         specularColour);
   }
}

/**
   A class that represents a point light node in a scene graph located
   at the origin
   @see GLSceneGraph.java
*/
package scenegraph;

import java.awt.Color;
import javax.media.opengl.GL;
import com.sun.opengl.util.GLUT;

public class GLPointLightNode extends GLLightNode
{

   public GLPointLightNode(String nodeName, GLSceneGraph sceneGraph)
   {  super(nodeName, sceneGraph);
   }

   public GLPointLightNode(String nodeName, GLSceneGraph sceneGraph,
      Color ambientColour, Color diffuseColour, Color specularColour)
   {  super(nodeName, sceneGraph, ambientColour, diffuseColour,
         specularColour);
   }
}

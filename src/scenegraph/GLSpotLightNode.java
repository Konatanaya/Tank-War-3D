/**
   A class that represents a spot light node in a scene graph
   with direction along the negative z-axis
   @see GLSceneGraph.java
*/
package scenegraph;

import java.awt.Color;
import javax.media.opengl.GL;
import com.sun.opengl.util.GLUT;

public class GLSpotLightNode extends GLLightNode
{
   private float cutoffAngle, exponent;

   public GLSpotLightNode(String nodeName, GLSceneGraph sceneGraph)
   {  super(nodeName, sceneGraph);
      cutoffAngle = 45.0f;
      exponent = 4.0f;
   }

   public GLSpotLightNode(String nodeName, GLSceneGraph sceneGraph,
      Color ambientColour, Color diffuseColour, Color specularColour)
   {  super(nodeName, sceneGraph, ambientColour, diffuseColour,
         specularColour);
      cutoffAngle = 45.0f;
      exponent = 4.0f;
   }

   public float getCutoffAngle()
   {  return cutoffAngle;
   }

   public void setCutoffAngle(float cutoffAngle)
   {  this.cutoffAngle = cutoffAngle;
   }

   public float getExponent()
   {  return exponent;
   }

   public void setExponent(float exponent)
   {  this.exponent = exponent;
   }
}

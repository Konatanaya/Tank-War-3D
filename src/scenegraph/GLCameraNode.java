/**
   A class that represents a camera node in a scene graph
   @see GLSceneGraph.java
*/
package scenegraph;

import javax.media.opengl.GL;

public class GLCameraNode extends GLNode
{
   public GLCameraNode(String nodeName, GLSceneGraph sceneGraph)
   {  super(nodeName, sceneGraph);
   }

   public void applyInverseGlobalTransformation(GL gl)
   {  // obtain the global transformation
      float[] globalTrans = new float[16];
      gl.glPushMatrix(); // save the current model view matrix
      applyGlobalTransformations(gl);
      gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, globalTrans, 0);
      gl.glPopMatrix(); // restore original model view matrix
      // calculate the inverse of globalTrans
      float[] inverseGlobalTrans = calculateInverse(globalTrans);
      // apply inverse transformation
      gl.glMultMatrixf(inverseGlobalTrans, 0);
   }

   // calculates the inverse of a 4x4 (model-view) matrix whose bottom
   // row is presumed to be 0,0,0,1
   private float[] calculateInverse(float[] m)
   {  float[] inverse = new float[16];
      float det = m[0]*m[5]*m[10] + m[4]*m[9]*m[2] + m[8]*m[1]*m[6]
         - m[0]*m[9]*m[6] - m[4]*m[1]*m[10] - m[8]*m[5]*m[2];
      if (det == 0)
         throw new IllegalArgumentException(
            "Camera transformation matrix is not invertible");
      inverse[0] = (m[5]*m[10] - m[6]*m[9]) / det;
      inverse[1] = (-m[1]*m[10] + m[2]*m[9]) / det;
      inverse[2] = (m[1]*m[6] - m[2]*m[5]) / det;
      inverse[3] = 0;
      inverse[4] = (-m[4]*m[10] + m[6]*m[8]) / det;
      inverse[5] = (m[0]*m[10] - m[2]*m[8]) / det;
      inverse[6] = (-m[0]*m[6] + m[2]*m[4]) / det;
      inverse[7] = 0;
      inverse[8] = (m[4]*m[9] - m[5]*m[8]) / det;
      inverse[9] = (-m[0]*m[9] + m[1]*m[8]) / det;
      inverse[10] = (m[0]*m[5] - m[1]*m[4]) / det;
      inverse[11] = 0;
      inverse[12] = (-m[4]*m[9]*m[14]-m[8]*m[13]*m[6]-m[12]*m[5]*m[10]
         + m[4]*m[13]*m[10] + m[8]*m[5]*m[14] + m[12]*m[9]*m[6]) / det;
      inverse[13] = (m[0]*m[9]*m[14]+m[8]*m[13]*m[2]+m[12]*m[1]*m[10]
         - m[0]*m[13]*m[10] - m[8]*m[1]*m[14] - m[12]*m[9]*m[2]) / det;
      inverse[14] = (-m[0]*m[5]*m[14]-m[4]*m[13]*m[2]-m[12]*m[1]*m[6]
         + m[0]*m[13]*m[6] + m[4]*m[1]*m[14] + m[12]*m[5]*m[2]) / det;
      inverse[15] = 1;
      return inverse;
   }
}

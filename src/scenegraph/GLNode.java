/**
   A class that represents one node in a scene graph
   Note that the additional transformations are always applied to the
   node in the order scale (first), rotation, translation (last)
   @see GLSceneGraph.java
*/
package scenegraph;

import javax.media.opengl.GL;

public abstract class GLNode
{
   private String nodeName;
   private GLSceneGraph sceneGraph;
   private boolean isEnabled; // whether node (and its subtree) enabled
   protected GLNode parent;
   private float[] nodeTransform; // general node transformation
   private boolean additionalScale, additionalRotation,
      additionalTranslation; // true if specific basic transformations
   private float xScale, yScale, zScale; // addition scale applied
   private float angleRot, xAxisRot, yAxisRot, zAxisRot;//add rotation
   private float xTrans, yTrans, zTrans; // additional translation

   public GLNode(String nodeName, GLSceneGraph sceneGraph)
   {  this.nodeName = nodeName;
      this.sceneGraph = sceneGraph;
      isEnabled = true;
      nodeTransform = new float[16];
      clearLocalTransformation(); // identity transform
      clearLocalScale();
      clearLocalRotation();
      clearLocalTranslation();
      parent = null;
   }

   public String getName()
   {  return nodeName;
   }

   public GLSceneGraph getSceneGraph()
   {  return sceneGraph;
   }

   public boolean isEnabled()
   {  return isEnabled;
   }

   public void setEnabled(boolean isEnabled)
   {  this.isEnabled = isEnabled;
   }

   public GLNode getParent()
   {  return parent;
   }

   public float[] getLocalTransformation()
   {  return nodeTransform;
   }

   public void clearLocalTransformation()
   {  nodeTransform = new float[]{1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1};
   }

   public boolean setLocalTransformation(float[] matrix)
   {  if (matrix.length < 16)
         return false; // insufficient values
      for (int i=0; i<16; i++)
         nodeTransform[i] = matrix[i];
      return true;
   }

   public void clearLocalScale()
   {  xScale = 1.0f; yScale = 1.0f; zScale = 1.0f;
      additionalScale = false;
   }

   public void setLocalScale(float scale)
   {  setLocalScale(scale, scale, scale);
   }

   public void setLocalScale(float x, float y, float z)
   {  xScale = x; yScale = y; zScale = z;
      additionalScale = true;
   }

   public void clearLocalRotation()
   {  angleRot=0.0f; xAxisRot=1.0f; yAxisRot=0.0f; zAxisRot=0.0f;
      additionalRotation = false;
   }

   public boolean setLocalRotation(float angle,float x,float y,float z)
   {  // ensure axis is normalized
      float lengthSq = x*x + y*y + z*z;
      if (lengthSq == 0)
         return false; // no axis specified
      else if (lengthSq != 1.0f)
      {  float length = (float)Math.sqrt(lengthSq);
         x /= length;
         y /= length;
         z /= length;
      }
      angleRot = angle; xAxisRot = x; yAxisRot = y; zAxisRot = z;
      additionalRotation = true;
      return true;
   }

   public void clearLocalTranslation()
   {  xTrans = 0.0f; yTrans = 0.0f; zTrans = 0.0f;
      additionalTranslation = false;
   }

   public void setLocalTranslation(float x, float y, float z)
   {  xTrans = x; yTrans = y; zTrans = z;
      additionalTranslation = true;
   }

   // applies all local transformations to current model view matrix
   protected void applyLocalTransformations(GL gl)
   {  gl.glMultMatrixf(nodeTransform, 0); // apply node transform last
      // apply any additional transformations in the order scale,
      // rotation, then translation
      if (additionalTranslation)
         gl.glTranslatef(xTrans, yTrans, zTrans);
      if (additionalRotation)
         gl.glRotatef(angleRot, xAxisRot, yAxisRot, zAxisRot);
      if (additionalScale)
         gl.glScalef(xScale, yScale, zScale);
   }

   // recursive method that applies all transformations to current
   // model view matrix
   protected void applyGlobalTransformations(GL gl)
   {  if (getParent() != null)
         getParent().applyGlobalTransformations(gl);
      applyLocalTransformations(gl);
   }

   public String toString()
   {  return nodeName + (isEnabled()?"":" (disabled) ") + "\n";
   }
}

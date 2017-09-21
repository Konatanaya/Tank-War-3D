/**
   An interface that represents a scene graph node that can have
   children
   @author Andrew Ensor
*/
package scenegraph;

import java.util.List;
import javax.media.opengl.GL;

public interface GLParentable
{
   public List<GLNode> getChildren();

   // adds a child to this node and takes special case if child is a
   // GLLightNode or GLCameraNode
   public boolean addChild(GLNode child);

   // removes a child from this node and takes special case if child
   // is a GLLightNode or GLCameraNode
   public boolean removeChild(GLNode child);

   // returns a string representation of subtree rooted at this node
   public String toString(int ancestry);
}

/**
 * A class that represents one node in a scene graph that can have any
 * GLRenderable object to draw as well as any number of child nodes
 *
 * @see GLSceneGraph.java
 */
package scenegraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.media.opengl.GL;

public class GLRenderableNode extends GLNode
        implements GLParentable, GLRenderable {

    private static HashMap<Integer, GLRenderableNode> idsMap
            = new HashMap<Integer, GLRenderableNode>();
    private static int newId = 0;

    private List<GLNode> childrenList;
    private GLRenderable renderObject;
    private int id = 0;

    public static GLRenderableNode getById(int id) {
        if (idsMap.containsKey(id)) {
            return idsMap.get(id);
        } else {
            return null;
        }
    }

    public GLRenderableNode(String nodeName, GLSceneGraph sceneGraph) {
        super(nodeName, sceneGraph);
        childrenList = new ArrayList<GLNode>(); // no initial children
        renderObject = null;
        
        id = newId++;
        idsMap.put(id, this);
    }

    // constuctor called by subclasses that may have fixed render object
    public GLRenderableNode(String nodeName, GLSceneGraph sceneGraph,
            GLRenderable renderObject) {
        super(nodeName, sceneGraph);
        childrenList = new ArrayList<GLNode>(); // no initial children
        this.renderObject = renderObject;
        
        id = newId++;
        idsMap.put(id, this);
    }

    public int getId() {
        return id;
    }

    @Override
    public void setEnabled(boolean enable) {
        for (GLNode node : childrenList) {
            node.setEnabled(enable);
        }
        
        super.setEnabled(enable);
    }
    
    public List<GLNode> getChildren() {
        return Collections.unmodifiableList(childrenList);
    }

   // adds a child to this node and takes special case if child is a
    // GLLightNode or GLCameraNode
    public boolean addChild(GLNode child) {
        if (child == null) return false;
        
        if (childrenList.contains(child)) {
            return false;
        } else {
            if (child.parent != null) {  // child belonged to another parent
                if (child.parent instanceof GLParentable) {
                    ((GLParentable) child.parent).removeChild(child);
                }
            }
            childrenList.add(child);
            child.parent = this;
            if (child instanceof GLLightNode) {
                getSceneGraph().addLight((GLLightNode) child);
            } else if (child instanceof GLCameraNode) {
                getSceneGraph().addCamera((GLCameraNode) child);
            }
            return true;
        }
    }

   // removes a child from this node and takes special case if child
    // is a GLLightNode or GLCameraNode
    public boolean removeChild(GLNode child) {
        if (!childrenList.contains(child)) {
            return false;
        } else {
            childrenList.remove(child);
            child.parent = null;
            if (child instanceof GLLightNode) {
                getSceneGraph().removeLight((GLLightNode) child);
            } else if (child instanceof GLCameraNode) {
                getSceneGraph().removeCamera((GLCameraNode) child);
            }
            return true;
        }
    }

    public GLRenderable getRenderObject() {
        return renderObject;
    }

    // note this method is commonly overridden by subclasses
    public void setRenderObject(GLRenderable renderObject) {
        this.renderObject = renderObject;
    }

    public void drawObject(GL gl) {
        if (!isEnabled()) {
            return; // don't draw node or subtree if node is disabled
        }
        
        // added by Johnny, for surface picking
        gl.glPushName(id);
 
        gl.glPushMatrix(); // save the current model view matrix
        //gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, new float[]{0.0f, 0.0f, 0.0f, 0.0f}, 0);
        applyLocalTransformations(gl);
        if (renderObject != null) {
            renderObject.drawObject(gl); // draw object for this node
        }
        for (int i=0;i<childrenList.size();i++){
                //GLNode childNode : childrenList) {
            if (childrenList.get(i) instanceof GLRenderable) {  // render each child node using current transformation
                ((GLRenderable) childrenList.get(i)).drawObject(gl);
            }
        }
        gl.glPopMatrix(); // restore original model view matrix
        
        gl.glPopName();
    }

    // returns a string representation of subtree rooted at this node
    public String toString(int ancestry) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getName() + (isEnabled() ? "" : " (disabled) "));
        buffer.append(" (has ");
        buffer.append(childrenList.size());
        buffer.append(" children)");
        buffer.append("\n");
        for (GLNode childNode : childrenList) {
            for (int i = 0; i < ancestry; i++) {
                buffer.append("   ");
            }
            buffer.append("- ");
            if (childNode instanceof GLParentable) {
                buffer.append(((GLParentable) childNode).toString(
                        ancestry + 1));
            } else {
                buffer.append(childNode.toString());
            }
        }
        return buffer.toString();
    }

    public String toString() {
        return toString(0);
    }
}

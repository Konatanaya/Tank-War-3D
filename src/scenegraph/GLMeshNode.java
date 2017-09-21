/**
 * @see GLSceneGraph.java
 */
package scenegraph;

import java.awt.Color;
import javax.media.opengl.GL;
import com.sun.opengl.util.GLUT;

public class GLMeshNode extends GLRenderableNode {

    PolygonalMesh mesh = null;

    public GLMeshNode(String nodeName, GLSceneGraph sceneGraph,
            PolygonalMesh mesh, final Color materialColour) {
        super(nodeName, sceneGraph, new GLRenderable() {
            public void drawObject(GL gl) {
                gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT,
                        materialColour.getComponents(null), 0);
                gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE,
                        materialColour.getComponents(null), 0);
                mesh.drawObject(gl);
            }
        });

        this.mesh = mesh;
    }

    public PolygonalMesh getMesh() {
        return mesh;
    }
    
    public void setRenderObject(GLRenderable renderObject) {
        throw new UnsupportedOperationException(
                "Render object is fixed as a cube");
    }
}

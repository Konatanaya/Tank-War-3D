package scenegraph;

/**
 * A utility class that represents a polygonal mesh of faces
 *
 * @author Andrew Ensor (modified for JSR-231)
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.media.opengl.GL;

public class PolygonalMesh implements GLRenderable {

    private Collection<PolygonalFace> faces;
    private String meshName;
    private int textureId = -1;
    private int textureMode = GL.GL_MODULATE;

    public PolygonalMesh() {
        this(null);
    }

    public PolygonalMesh(String meshName) {
        faces = new ArrayList<PolygonalFace>();
    }

    public void setTextureId(int id) {
        textureId = id;
    }
    
    public void setTextureMode(int mode) {
        textureMode = mode;
    }

    public void addFaces(Collection<PolygonalFace> faces) {
        this.faces.addAll(faces);
    }

    public void addFace(Point3D[] vertices, Vector3D normal) {
        faces.add(new PolygonalFace(vertices, normal));
    }

    public void addFace(Point3D[] vertices, Vector3D[] normals) {
        faces.add(new PolygonalFace(vertices, normals));
    }

    public void addFace(Point3D[] vertices, Vector3D normal,
            Texel2D[] texels) {
        faces.add(new PolygonalFace(vertices, normal, texels));
    }

    public void addFace(Point3D[] vertices, Vector3D[] normals,
            Texel2D[] texels) {
        faces.add(new PolygonalFace(vertices, normals, texels));
    }

    public Collection<PolygonalFace> getFaces() {
        return Collections.unmodifiableCollection(faces);
    }

    public String getMeshName() {
        return meshName;
    }

    public String toString() {
        String output = "Mesh:";
        for (PolygonalFace face : faces) {
            output += " " + face.toString();
        }
        return output;
    }

    // implemented method of GLRenderable that draws the mesh
    public void drawObject(GL gl) {
        if (textureId > 0) {
            gl.glBindTexture(GL.GL_TEXTURE_2D, textureId);
            gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, textureMode);
            gl.glEnable(GL.GL_TEXTURE_2D); // enable texture mapping
        }

        for (PolygonalFace face : faces) {
            Point3D[] vertices = face.getVertices();
            Vector3D[] normals = face.getNormals();
            Texel2D[] texels = face.getTexels();

            gl.glBegin(GL.GL_TRIANGLE_FAN);
            for (int i = 0; i < vertices.length; i++) {
                if (texels != null) {
                    gl.glTexCoord2d(texels[i].getSValue(),
                            texels[i].getTValue());
                }
                gl.glNormal3d(normals[i].getX(), normals[i].getY(),
                        normals[i].getZ());
                gl.glVertex3d(vertices[i].getX(), vertices[i].getY(),
                        vertices[i].getZ());
            }
            gl.glEnd();
        }

        if (textureId > 0) {
            gl.glDisable(GL.GL_TEXTURE_2D); // disable texture mapping
        }
    }
}

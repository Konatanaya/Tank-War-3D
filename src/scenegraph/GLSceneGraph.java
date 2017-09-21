/**
 * A class that represents a simple tree-based scene graph for creating 3D
 * scenes in JOGL without directly manipulating the OpenGL Model View matrix
 *
 * @author Andrew Ensor
 */
package scenegraph;

import com.sun.prism.impl.BufferUtil;
import java.awt.Color;
import java.awt.Point;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

public class GLSceneGraph {

    private GLRenderableNode rootNode;
    private List<GLLightNode> lightNodes; // list of lights available
    private List<GLCameraNode> cameraNodes; // list of cameras available
    private GLCameraNode currentCameraNode; // camera used for rendering
    private Color ambientLightColour, backgroundColour;

    private static final int PICK_BUFFER_SIZE = 64;

    public GLSceneGraph() {
        rootNode = new GLRenderableNode("Scene graph root", this);
        lightNodes = new ArrayList<GLLightNode>();
        cameraNodes = new ArrayList<GLCameraNode>();
        currentCameraNode = null;
        ambientLightColour = new Color(0.2f, 0.2f, 0.2f, 1.0f);//default
        backgroundColour = Color.WHITE;
    }

    public GLRenderableNode getRootNode() {
        return rootNode;
    }

    protected GLRenderable getHitRenderable() {
        GLRenderable node = null;

        return node;
    }

    private GLRenderableNode parseSelectBuffer(int numSelected, IntBuffer selectBuffer) {
        int nearestNodeId = -1;
        float nearestZ = Integer.MAX_VALUE;

        selectBuffer.rewind();

        System.out.println("Number of objects selected: " + numSelected);
        for (int i = 0; i < numSelected; i++) {
            int numNames = selectBuffer.get();
            float zNear = 1 + 1.0f * selectBuffer.get() / 0x7fffffff;
            float zFar = 1 + 1.0f * selectBuffer.get() / 0x7fffffff;
            int id = -1;

            System.out.print("HIT " + i + " has " + numNames + " names, z = "
                    + "[" + zNear + "," + zFar + "] : ");
            for (int j = 0; j < numNames; j++) {
                id = selectBuffer.get();
                System.out.print(GLRenderableNode.getById(id).getName() + " ");
            }
            System.out.println();

            if (zNear < nearestZ) {
                nearestNodeId = id;
                nearestZ = zNear;
            }
        }
        System.out.println();

        return GLRenderableNode.getById(nearestNodeId);
    }

    public void renderScene(GL gl) {
        renderScene(gl, null);
    }

    // renders the complete scene graph
    public GLRenderableNode renderScene(GL gl, Point pickPoint) {  // clear the screen
        GLRenderableNode pickedNode = null;

        gl.glClearColor(backgroundColour.getRed() / 255.0f,
                backgroundColour.getGreen() / 255.0f,
                backgroundColour.getBlue() / 255.0f,
                backgroundColour.getAlpha() / 255.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        // set the ambient light
        gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT,
                ambientLightColour.getComponents(null), 0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix(); // save the current model view matrix

        // first set the camera position
        if ((currentCameraNode != null) && currentCameraNode.isEnabled()) {
            gl.glLoadIdentity(); // clear the previous model view matrix
            currentCameraNode.applyInverseGlobalTransformation(gl);
        }
      // else presume that the model view matrix is already set up
        // correctly for camera position

        // then set up the lighting
        for (int i = 0; i < Math.min(lightNodes.size(), GLLightNode.LIGHT_IDS.length); i++) {
            GLLightNode lightNode = lightNodes.get(i);
            int lightID = GLLightNode.LIGHT_IDS[i];

            if (lightNode.isEnabled()) {
                gl.glLightfv(lightID, GL.GL_AMBIENT,
                        lightNode.getAmbientColour().getComponents(null), 0);
                gl.glLightfv(lightID, GL.GL_DIFFUSE,
                        lightNode.getDiffuseColour().getComponents(null), 0);
                gl.glLightfv(lightID, GL.GL_SPECULAR,
                        lightNode.getSpecularColour().getComponents(null), 0);
            //gl.glLightf(lightID, GL.GL_CONSTANT_ATTENUATION, 2f);
                //gl.glLightf(lightID, GL.GL_LINEAR, 0.1f);
                //gl.glLightf(lightID, GL.GL_QUADRATIC_ATTENUATION, 1f);

                // position the light appropriately in scene
                gl.glPushMatrix(); // save the current model view matrix
                lightNode.applyGlobalTransformations(gl);
                if (lightNode instanceof GLPointLightNode) {
                    gl.glLightfv(lightID, GL.GL_POSITION,
                            new float[]{0, 0, 0, 1}, 0); // position at origin
                } else if (lightNode instanceof GLDirectionalLightNode) {
                    gl.glLightfv(lightID, GL.GL_POSITION,
                            new float[]{0, 0, 1, 0}, 0); // toward -z direction
                }
                if (lightNode instanceof GLSpotLightNode) {
                    gl.glLightfv(lightID, GL.GL_POSITION,
                            new float[]{0, 0, 0, 1}, 0); // position at origin
                    gl.glLightf(lightID, GL.GL_SPOT_CUTOFF,
                            ((GLSpotLightNode) lightNode).getCutoffAngle());
                    gl.glLightf(lightID, GL.GL_SPOT_EXPONENT,
                            ((GLSpotLightNode) lightNode).getExponent());
                    gl.glLightfv(lightID, GL.GL_SPOT_DIRECTION,
                            new float[]{0, 0, -1, 0}, 0); // direction
                }
                gl.glPopMatrix(); // restore original model view matrix
                gl.glEnable(lightID);
            } else {
                gl.glDisable(lightID);
            }
        }
        for (int i = Math.min(lightNodes.size(),
                GLLightNode.LIGHT_IDS.length);
                i < GLLightNode.LIGHT_IDS.length; i++) {
            int lightID = GLLightNode.LIGHT_IDS[i];
            gl.glDisable(lightID); // disable the other lights
        }
        gl.glEnable(GL.GL_LIGHTING); // enable lighting

        // do picking - added by Johnny
        boolean pick = (pickPoint != null);
        IntBuffer pickBuffer = null;
        double[] projMat = null;

        if (pick) {
            GLU glu = new GLU();
            projMat = new double[16];
            int[] vp = new int[4];
            pickBuffer = BufferUtil.newIntBuffer(PICK_BUFFER_SIZE);

            gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projMat, 0);
            gl.glGetIntegerv(GL.GL_VIEWPORT, vp, 0);
            gl.glSelectBuffer(PICK_BUFFER_SIZE, pickBuffer);
            gl.glRenderMode(GL.GL_SELECT);
            gl.glInitNames();

            gl.glMatrixMode(GL.GL_PROJECTION);
            gl.glLoadIdentity();

            glu.gluPickMatrix(
                    pickPoint.getX() - 20, pickPoint.getY() - 20, 40, 40, vp, 0);
            gl.glMultMatrixd(projMat, 0);

            gl.glMatrixMode(GL.GL_MODELVIEW);
        }

        // recursively render all the nodes in the tree
        rootNode.drawObject(gl);
        gl.glPopMatrix(); // restore original model view matrix
        gl.glFlush(); // send all output to display

        if (pick) {
            int hits = gl.glRenderMode(GL.GL_RENDER);
            pickedNode = parseSelectBuffer(hits, pickBuffer);

            // restore projection matrix
            gl.glMatrixMode(GL.GL_PROJECTION);
            gl.glLoadMatrixd(projMat, 0);
        }

        return pickedNode;
    }

    public void setAmbientLight(Color ambientLightColour) {
        this.ambientLightColour = ambientLightColour;
    }

    public void setBackground(Color backgroundColour) {
        this.backgroundColour = backgroundColour;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Scene Graph:\n");
        // determine status of the lights in the scene
        buffer.append("  Lights in scene: ");
        int numLightsEnabled = 0;
        for (GLLightNode lightNode : lightNodes) {
            if (lightNode.isEnabled()) {
                numLightsEnabled++;
            }
        }
        buffer.append(lightNodes.size());
        buffer.append(" light, ");
        buffer.append(numLightsEnabled);
        buffer.append(" enabled");
        // check for any issues with the lighting
        if (lightNodes.size() == 0) {
            buffer.append(" -- Warning: no lights in scene");
        } else if (numLightsEnabled == 0) {
            buffer.append(" -- Warning: no lights enabled");
        } else if (lightNodes.size() > GLLightNode.LIGHT_IDS.length) {
            buffer.append(" -- Warning: too many lights");
        }
        buffer.append("\n");
        // determine status of the current camera in the scene
        buffer.append("  Cameras in scene: ");
        buffer.append(cameraNodes.size());
        buffer.append(" camera");
        if (currentCameraNode == null) {
            buffer.append(" -- Warning: no current camera in scene");
        } else {
            buffer.append(", current camera: "
                    + currentCameraNode.getName());
            if (!currentCameraNode.isEnabled()) {
                buffer.append(" -- Warning: current camera is disabled");
            }
        }
        buffer.append("\n");
        buffer.append(rootNode.toString(0));

        return buffer.toString();
    }

    // method with package visibility that adds a GLLightNode to
    // scene graph list
    boolean addLight(GLLightNode lightNode) {
        if (lightNodes.contains(lightNode)) {
            return false;
        } else {
            lightNodes.add(lightNode);
            return true;
        }
    }

    // method with package visibility that removes a GLLightNode from
    // scene graph list
    boolean removeLight(GLLightNode lightNode) {
        if (!lightNodes.contains(lightNode)) {
            return false;
        } else {
            lightNodes.remove(lightNode);
            return true;
        }
    }

    // method with package visibility that adds a GLCameraNode to scene
    // graph list
    boolean addCamera(GLCameraNode cameraNode) {
        if (cameraNodes.contains(cameraNode)) {
            return false;
        } else {
            cameraNodes.add(cameraNode);
            // automatically select if first camera added to scene graph
            if (cameraNodes.size() == 1) {
                setCurrentCamera(cameraNode);
            }
            return true;
        }
    }

    // method with package visibility that removes a GLCameraNode from
    // scene graph list
    boolean removeCamera(GLCameraNode cameraNode) {
        if (!cameraNodes.contains(cameraNode)) {
            return false;
        } else {
            cameraNodes.remove(cameraNode);
            if (currentCameraNode == cameraNode) {  // try to make another camera current
                if (cameraNodes.size() > 0) {
                    currentCameraNode = cameraNodes.get(0);//another camera
                } else {
                    currentCameraNode = null;
                }
            }
            return true;
        }
    }

    // sets which camera in scene graph to use for next scene rendering
    public boolean setCurrentCamera(GLCameraNode cameraNode) {
        if (!cameraNodes.contains(cameraNode)) {
            return false; // cameraNode not in scene graph
        }
        this.currentCameraNode = cameraNode;
        return true;
    }
}

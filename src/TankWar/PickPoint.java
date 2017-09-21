/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TankWar;

import com.sun.prism.impl.BufferUtil;
import java.awt.Point;
import java.nio.IntBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import scenegraph.*;

/**
 *
 * @author Konatan
 */
public class PickPoint {
    private static final int PICK_BUFFER_SIZE = 64;
    public PickPoint(){
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
    public GLRenderableNode Picknode(GL gl, GLSceneGraph sceneGraph, Point pickPoint){
        GLRenderableNode pickedNode = null;
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
        sceneGraph.getRootNode().drawObject(gl);
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
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TankWar;


import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import scenegraph.Point3D;
import javax.media.opengl.GL;
import scenegraph.*;

/**
 *
 * @author Konatan
 */
public class Tank{
    private Point3D center;
    private GLSceneGraph sceneGraph;
    private double x,y,z;
    private GLRenderableNode meshNode;
    ArrayList<PolygonalMesh> meshes;
    ObjLoader loader;
    public Tank(Point3D center,GLSceneGraph sceneGraph){
        this.sceneGraph = sceneGraph;
        this.center = center;
        this.x = center.getX();
        this.y = center.getY();
        this.z = center.getZ();
    }
    public GLRenderableNode createMeshNodeFromFile(GL gl, String nodeName, Color materialColour, String objPath, String texPath) {
        meshNode = null;
        int i = 0;
        try {
            loader = new ObjLoader();
            meshes = loader.loadFromFile(objPath);
            meshNode = new GLRenderableNode(nodeName, sceneGraph);
            Texture tex = new FileTexture(texPath, gl, true, true);
            
            for (PolygonalMesh mesh : meshes) {
                mesh.setTextureId(tex.getID());
                mesh.setTextureMode(GL.GL_MODULATE);
                GLMeshNode node = new GLMeshNode(mesh.getMeshName(), sceneGraph, mesh, materialColour);
                meshNode.addChild(node);
                
            }
        } catch (IOException ex) {
        }

        return meshNode;
    }
    public void setCenter(Point3D p){
        this.center = p;
        this.x = p.getX();
        this.y = p.getY();
        this.z = p.getZ();
    }
    public Point3D getCenter(){
        return center;
    }
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    public double getZ(){
        return z;
    }
}

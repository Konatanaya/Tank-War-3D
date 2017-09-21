/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TankWar;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import javax.media.opengl.GL;
import scenegraph.*;

/**
 *
 * @author Konatan
 */
public class Missile {
    private Point3D center;
    private GLSceneGraph sceneGraph;
    private double x,y,z;
    Texture tex;
    public Missile(Point3D center,GLSceneGraph sceneGraph, double theta, Tank tk){
        this.sceneGraph = sceneGraph;
        this.center = center;
        this.x = center.getX() + 1.3 * Math.sin(theta);
        this.y = center.getY();
        this.z = center.getZ() + 1.3 * Math.cos(theta);
    }
    public Missile(Point3D center,GLSceneGraph sceneGraph, double theta){
        this.sceneGraph = sceneGraph;
        this.center = center;
        this.x = center.getX() + 1.3 * Math.sin(theta);
        this.y = center.getY();
        this.z = center.getZ() + 1.3 * Math.cos(theta);
    }
    public GLRenderableNode createMeshNodeFromFile(GL gl, String nodeName, Color materialColour, String objPath, String texPath) {
        GLRenderableNode meshNode = null;
        try {
            ObjLoader loader = new ObjLoader();
            ArrayList<PolygonalMesh> meshes = loader.loadFromFile(objPath);
            meshNode = new GLRenderableNode(nodeName, sceneGraph);
            
            for (PolygonalMesh mesh : meshes) {
                GLMeshNode node = new GLMeshNode(mesh.getMeshName(), sceneGraph, mesh, materialColour);   
                meshNode.addChild(node);
            }
        } catch (IOException ex) {
        }

        return meshNode;
    }
    public void deleteTexture(GL gl){
        tex.deleteTexture(gl);
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

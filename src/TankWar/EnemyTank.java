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
import scenegraph.FileTexture;
import scenegraph.GLMeshNode;
import scenegraph.GLRenderableNode;
import scenegraph.GLSceneGraph;
import scenegraph.ObjLoader;
import scenegraph.Point3D;
import scenegraph.PolygonalMesh;
import scenegraph.Texture;
import scenegraph.Vector3D;

/**
 *
 * @author Konatan
 */
public class EnemyTank {
    private Point3D center;
    private GLSceneGraph sceneGraph;
    private double x,y,z;
    private Point3D front;
    private double fx,fy,fz;
    public double angle;
    public double t;
    public Vector3D testV;
    public boolean shoot;
    public boolean boom;
    ObjLoader loader;
    public EnemyTank(Point3D center,GLSceneGraph sceneGraph){
        this.sceneGraph = sceneGraph;
        this.center = center;
        this.x = center.getX();
        this.y = center.getY();
        this.z = center.getZ();
        this.angle = 0.0;
        t = (double)this.angle*Math.PI/180;
        this.front = new Point3D(center.getX()+0.1*Math.sin(t),center.getY(),center.getZ()+0.1*Math.cos(t));
        this.fx=front.getX();
        this.fy=front.getY();
        this.fz=front.getZ();
        this.shoot = false;
        this.boom = false;
    }
    public GLRenderableNode createMeshNodeFromFile(GL gl, String nodeName, Color materialColour, String objPath, String texPath) {
        GLRenderableNode meshNode = null;
        try {
            loader = new ObjLoader();
            ArrayList<PolygonalMesh> meshes = loader.loadFromFile(objPath);
            meshNode = new GLRenderableNode(nodeName, sceneGraph);
            Texture tex = new FileTexture(texPath, gl, true, true);
            
            for (PolygonalMesh mesh : meshes) {
                mesh.setTextureId(tex.getID());
                mesh.setTextureMode(GL.GL_REPLACE);
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
    public void setFront(Point3D p){
        this.front = p;
        this.fx = p.getX();
        this.fy = p.getY();
        this.fz = p.getZ();
    }
    public Point3D getFront(){
        return front;
    }
    public double getFX(){
        return fx;
    }
    public double getFY(){
        return fy;
    }
    public double getFZ(){
        return fz;
    }
    public double turnTank(Tank ct){
        double Angle = this.angle;
        double t1 = 0;
        Vector3D fc=new Vector3D(0,0,0), cc=new Vector3D(0,0,0);
        fc.set(this.getFX()-ct.getX(),this.getFY()-ct.getY(),this.getFZ()-ct.getZ());
        cc.set(this.getX()-ct.getX(),this.getY()-ct.getY(),this.getZ()-ct.getZ());
        while(fc.getParallel(cc) > 0.001 || fc.getParallel(cc) < -0.001|| fc.getLength()>=cc.getLength()){
            if(Angle>=360 || Angle<=-360)
                Angle=0;  
            Angle += 0.01;
            t1 = (double)Angle*Math.PI/180;
            this.setFront(new Point3D((float)(this.getX()+0.1 * Math.sin(t1)), (float)this.getY(), (float)(this.getZ()+0.1 * Math.cos(t1))));
            fc.set(this.getFX()-ct.getX(),this.getFY()-ct.getY(),this.getFZ()-ct.getZ());
            cc.set(this.getX()-ct.getX(),this.getY()-ct.getY(),this.getZ()-ct.getZ());            
        }
        this.angle = Angle;
        return Angle;
    }

}

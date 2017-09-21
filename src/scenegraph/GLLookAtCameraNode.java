/**
   A class that represents a camera node in a scene graph
   @see GLSceneGraph.java
*/
package scenegraph;

import javax.media.opengl.glu.GLU;
import javax.media.opengl.GL;

public class GLLookAtCameraNode extends GLCameraNode {
    protected Point3D eye;
	protected Point3D at;
	protected Vector3D up;
        protected GLU glu;
        private double radius = 0;
        private double theta = 0;
        private double height = 0;

	public GLLookAtCameraNode(String nodeName, GLSceneGraph sceneGraph, Point3D eye, Point3D at, Vector3D up) {
            super(nodeName, sceneGraph);
            this.eye = eye;
            this.at = at;
            this.up = up;
            this.glu = new GLU();
	}
        public void setCameraNode(Point3D eye, Point3D at, Vector3D up){
            this.eye = eye;
            this.at = at;
            this.up = up;
        }
	public void applyInverseGlobalTransformation(GL gl) {  // obtain the global transformation
            glu.gluLookAt(
                            eye.getX(), eye.getY(), eye.getZ(),
                            at.getX(), at.getY(), at.getZ(),
                            up.getX(), up.getY(), up.getZ());
	}
        public void up() {
            if(check(1)){
                eye.set(eye.getX(), eye.getY(), eye.getZ()+0.1);
                at.set(at.getX(), at.getY(), at.getZ()+0.1); 
            }
        }
        public void down() {
            if(check(2)){
                eye.set(eye.getX(), eye.getY(), eye.getZ()-0.1);
                at.set(at.getX(), at.getY(), at.getZ()-0.1); 
            }
        }
        public void right() {
            if(check(3)){
                eye.set(eye.getX()-0.1, eye.getY(), eye.getZ());
                at.set(at.getX()-0.1, at.getY(), at.getZ());    
            }
        }
        public void left() {
            if(check(4)){
                eye.set(eye.getX()+0.1, eye.getY(), eye.getZ());
                at.set(at.getX()+0.1, at.getY(), at.getZ()); 
            }
        }
        
        private boolean check(int i) {
            switch(i){
                case 1:
                    if(at.getZ()+0.1>=45)
                        return false;
                    break;
                case 2:
                    if(at.getZ()-0.1<=-45)
                        return false;
                    break;
                case 3:
                    if(at.getX()-0.1<=-45)
                        return false;
                    break;
                case 4:
                    if(at.getX()+0.1>=45)
                        return false;
                    break;
            }
            return true;
        }
}
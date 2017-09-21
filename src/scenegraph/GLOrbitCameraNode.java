/**
   A class that represents a camera node in a scene graph
   @see GLSceneGraph.java
*/
package scenegraph;

import java.util.Timer;
import java.util.TimerTask;
import javax.media.opengl.GLCanvas;

 public class GLOrbitCameraNode extends GLLookAtCameraNode {

        private double radius = 0;
        private double theta = 0;
        private double height = 0;
        private GLCanvas canvas = null;
        private boolean autoMove = true;

        public GLOrbitCameraNode(String nodeName, GLSceneGraph sceneGraph, GLCanvas canvas, Point3D at, double height, double radius) {
            super(nodeName, sceneGraph, new Point3D(at.getX(), at.getY() + height, at.getZ()-radius), at, new Vector3D(0, 1, 0));
            this.radius = radius;
            this.height = height;
            this.canvas = canvas;
            AngleChanger task = new AngleChanger();
            Timer timer = new Timer();
            timer.schedule(task, 0, 20); // start in 0ms, then every 20ms
        }

        public void up() { height += 0.05; updateEye(); }
        public void down() {height -= 0.05; updateEye(); }
        public void forward() { radius = Math.max(0, radius - 0.05); updateEye(); }
        public void backward() { radius += 0.05; updateEye(); }
        public void right() {theta -= 0.05; updateEye(); }
        public void left() {theta += 0.05; updateEye(); }
        
        public void autoMoveOn() { autoMove = false;}
        public void autoMoveOff() { autoMove = false;}
        
        private void updateEye() {
                eye.set(at.getX() - radius * Math.sin(theta), at.getY() + height, at.getZ() - radius * Math.cos(theta));
        }
        public void updateView(Point3D p , float angle){
            theta = (double)angle*Math.PI/180;
            at.set(p.getX(),p.getY(),p.getZ());
            eye.set(at.getX() - radius * Math.sin(theta), at.getY() + height, at.getZ() - radius * Math.cos(theta));
        }
        public void updateDirection(float angle){
            theta = (double)angle*Math.PI/180;
            eye.set(at.getX() - radius * Math.sin(theta), at.getY() + height, at.getZ() - radius * Math.cos(theta));
        }
        public Point3D getEye(){
            return eye;
        }
        private class AngleChanger extends TimerTask {
            public void run() {
                if (!autoMove) return;
                
                right();
                canvas.repaint();
            }
        }
    }
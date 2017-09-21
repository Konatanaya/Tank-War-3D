/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TankWar;

import scenegraph.GLMeshNode;
import com.sun.opengl.util.GLUT;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import scenegraph.*;


/**
 *
 * @author Konatan
 */
public class TankWars extends JPanel implements GLEventListener{
    private final GLU glu;
    private GLUT glut;
    private final GLCanvas canvas;
    private int width,Height;
    public GLSceneGraph sceneGraph;
    protected static final double WORLD_NEAR = 0.01;
    protected static final double WORLD_FAR = 400;
    protected static final double WORLD_HEIGHT = 1.4;
    private boolean foggy = false;
    
    private boolean perspectiveView = true;
    private boolean smoothShading = true;
    private GLOrbitCameraNode cam1;
    private GLLookAtCameraNode cam2;
    private GLMeshNode floorNode = null;
    private Toolkit kit = Toolkit.getDefaultToolkit();
    private Dimension screenSize = kit.getScreenSize();
    private  int screenWidth = screenSize.width;
    private int screenHeight = screenSize.height;
    private float[] colourArray;
    private GLRenderableNode myTank;
    private PickPoint PointPicked;
    private Tank ct;
    private float angle=0;
    private double theta=0.0;
    private boolean ki = false,kj = false,kk = false,kl = false,kenter = false;
    private boolean send = false,click = false;
    private boolean doPicking = false;
    private boolean doMoving = false;
    private Point pickPoint = null;
    private List<createmissile> lcm = new ArrayList<createmissile>();
    private List<createEnemyTank> cet = new ArrayList<createEnemyTank>();
    private Thread thread;
    private TankWars tws;
    private Menu m;
    private Music mic;
    private GLLightNode cl;
    private GLLightNode cdl;
    private GLLightNode csl;
    Word w;
    
    public TankWars(Menu m){
        tws = this;
        this.m = m;
        GLCapabilities capabilities = new GLCapabilities();
        canvas = new GLCanvas(capabilities);
        this.glu = new GLU();
        this.glut = new GLUT();
        colourArray = new float[4];
        this.setBounds(0, -10, screenWidth, screenHeight);
        this.setVisible(true);
        this.setLayout(null);
        this.requestFocus();
//        canvas.requestFocus();
        canvas.setBounds(0, 0, screenWidth, screenHeight);
        canvas.addGLEventListener(this);
        this.add(canvas);
        ct = new Tank(new Point3D(0,0,0),sceneGraph);
        
        canvas.addKeyListener(new KeyActionListener());
        canvas.addMouseListener(new MousePickListener());
        
        String url = ""; 
        Toolkit tk = Toolkit.getDefaultToolkit(); 
        Image image = new ImageIcon(url).getImage(); 
        Cursor cursor = tk.createCustomCursor(image, new Point(10, 10), "norm");
        this.setCursor(cursor);
        
        thread = new fps();
        thread.start();
        PointPicked = new PickPoint();
    }
    public class fps extends Thread{
        public void run() {  
            long fpsTime = (long) ((Double.valueOf(1000) / Double.valueOf(60)) * 1000000); 
            long now = 0;  
            long total = 0;  
            while(true) {  
                now = System.nanoTime();  
                canvas.repaint();
                try{  
                    total = System.nanoTime() - now;  
                    if(total > fpsTime){  
                       continue; 
                    }  
                    this.sleep((fpsTime - (System.nanoTime()- now)) / 1000000);  
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                }  
                while((System.nanoTime()- now) < fpsTime){  
                    System.nanoTime();  
                }  
            }
        } 
    }    
    public GLCameraNode createCameraNode() {
        cam1 = new GLOrbitCameraNode("Orbital Camera", sceneGraph, canvas,
                 ct.getCenter(), 1.5, 4);
        sceneGraph.getRootNode().addChild(cam1);
        cam1.autoMoveOff();
        
        cam2 = new GLLookAtCameraNode("Overlooking Camera", sceneGraph,
                new Point3D(0, 50, 0), new Point3D(0, 0, 0), new Vector3D(0, 0, 1));
        sceneGraph.getRootNode().addChild(cam2);
        return cam1;
    }
    
    public GLMeshNode createSkyboxNode(GL gl) {
        PolygonalMesh box = new PolygonalMesh();
        Point3D[] v = {
            new Point3D(-5, -5, -5), new Point3D(+5, -5, -5), 
            new Point3D(+5, -5, +5), new Point3D(-5, -5, +5),
            new Point3D(-5, +5, -5), new Point3D(+5, +5, -5), 
            new Point3D(+5, +5, +5), new Point3D(-5, +5, +5)};

        Vector3D[] n = {
            new Vector3D(0, 1, 0), new Vector3D(0, -1, 0), // bottom, up
            new Vector3D(0, 0, 1), new Vector3D(-1, 0, 0), // front, right
            new Vector3D(0, 0, -1), new Vector3D(1, 0, 0)}; // back, left
        
        box.addFace( // bottom
                new Point3D[]{v[0], v[3], v[2], v[1]},
                new Vector3D[]{n[0], n[0], n[0], n[0]},
                new Texel2D[]{
                new Texel2D(0.25f, 0.25f), new Texel2D(0.25f, 0.00f),
                new Texel2D(0.50f, 0.00f), new Texel2D(0.50f, 0.25f)});
        box.addFace( // up
                new Point3D[]{v[4], v[5], v[6], v[7]},
                new Vector3D[]{n[1], n[1], n[1], n[1]},
                new Texel2D[]{
                    new Texel2D(0.25f, 0.75f), new Texel2D(0.50f, 0.75f),
                    new Texel2D(0.50f, 1.00f), new Texel2D(0.25f, 1.00f)});

        box.addFace( // front
                new Point3D[]{v[0], v[1], v[5], v[4]},
                new Vector3D[]{n[2], n[2], n[2], n[2]},
                new Texel2D[]{
                    new Texel2D(0.25f, 0.25f), new Texel2D(0.50f, 0.25f),
                    new Texel2D(0.50f, 0.75f), new Texel2D(0.25f, 0.75f)});

        box.addFace( // right
                new Point3D[]{v[1], v[2], v[6], v[5]},
                new Vector3D[]{n[3], n[3], n[3], n[3]},
                new Texel2D[]{
                    new Texel2D(0.50f, 0.25f), new Texel2D(0.75f, 0.25f),
                    new Texel2D(0.75f, 0.75f), new Texel2D(0.50f, 0.75f)});
        
        box.addFace( // back
                new Point3D[]{v[2], v[3], v[7], v[6]},
                new Vector3D[]{n[4], n[4], n[4], n[4]},
                new Texel2D[]{
                    new Texel2D(0.75f, 0.25f), new Texel2D(1.00f, 0.25f),
                    new Texel2D(1.00f, 0.75f), new Texel2D(0.75f, 0.75f)});
        
        box.addFace( // left
                new Point3D[]{v[3], v[0], v[4], v[7]},
                new Vector3D[]{n[5], n[5], n[5], n[5]},
                new Texel2D[]{
                    new Texel2D(0.00f, 0.25f), new Texel2D(0.25f, 0.25f),
                    new Texel2D(0.25f, 0.75f), new Texel2D(0.00f, 0.75f)});
        
        return new GLMeshNode("Skybox", sceneGraph, box, Color.BLACK);
    }
    
    public GLRenderableNode createFloorNode(float thickness) {
        PolygonalMesh floorMesh = new PolygonalMesh();
        floorMesh.addFace(
                new Point3D[]{
                    new Point3D(-5, 0, -5), 
                    new Point3D(+5, 0, -5), 
                    new Point3D(+5, 0, +5), 
                    new Point3D(-5, 0, +5)},
                new Vector3D[]{
                    new Vector3D(0, 1, 0),
                    new Vector3D(0, 1, 0),
                    new Vector3D(0, 1, 0),
                    new Vector3D(0, 1, 0)},
                new Texel2D[]{
                    new Texel2D(0, 0),
                    new Texel2D(1, 0),
                    new Texel2D(1, 1),
                    new Texel2D(0, 1)});
        floorNode = new GLMeshNode("Floor", sceneGraph, floorMesh, Color.WHITE);
        floorNode.setLocalTranslation(0.5f, 0, 0.5f);
        floorNode.setLocalScale(5.0f, 1.0f, 5.0f);

        return floorNode;
    }
    
    public class GLGridNode extends GLRenderableNode {
        public GLGridNode(String nodeName, GLSceneGraph sceneGraph, final GLUT glut, final Color color, int lines) {
            super(nodeName, sceneGraph, new GLRenderable() {
                public void drawObject(GL gl) {
                    gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, color.getComponents(null), 0);
                    gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, color.getComponents(null), 0);
                    gl.glColor3f(0.5f, 0.5f , 0.5f);
                    gl.glLineWidth(20.0f);
                    gl.glBegin(GL.GL_LINE_LOOP);
                        gl.glVertex3d(4.5, 0.1, 4.5);
                        gl.glVertex3d(4.5, 0.1, -4.5);
                        gl.glVertex3d(-4.5, 0.1, -4.5);
                        gl.glVertex3d(-4.5, 0.1, 4.5);
                    gl.glEnd();
                }
            });
        }
        public void setRenderObject(GLRenderable renderObject) {
            throw new UnsupportedOperationException("");
        }
    }
    
    
    public void initEnemy(GL gl,GLSceneGraph sceneGraph){
        Point3D eCenter;
        eCenter = new Point3D(-30,0,30);
        cet.add(new createEnemyTank(gl,eCenter));
        eCenter = new Point3D(0,0,30);
        cet.add(new createEnemyTank(gl,eCenter));
        eCenter = new Point3D(30,0,30);
        cet.add(new createEnemyTank(gl,eCenter));
        eCenter = new Point3D(30,0,0);
        cet.add(new createEnemyTank(gl,eCenter));  
        eCenter = new Point3D(30,0,-30);
        cet.add(new createEnemyTank(gl,eCenter));
        eCenter = new Point3D(0,0,-30);
        cet.add(new createEnemyTank(gl,eCenter)); 
        eCenter = new Point3D(-30,0,-30);
        cet.add(new createEnemyTank(gl,eCenter));
        eCenter = new Point3D(-30,0,0);
        cet.add(new createEnemyTank(gl,eCenter)); 
        
        
       
    }

    public class createEnemyTank{
        GLRenderableNode ET;
        EnemyTank et;
        Point3D eCenter;
        GL gl;
        double Angle ;
        Timer timer;
        public createEnemyTank(GL gl, Point3D eCenter){
            this.eCenter = eCenter;
            this.gl=gl;
            et = new EnemyTank (eCenter,sceneGraph); 
            ET = et.createMeshNodeFromFile(gl, "Enemy tank", Color.BLACK, "model\\enemy.obj","model\\enemy.jpg");
            ET.setLocalTranslation((float)eCenter.getX(),(float)eCenter.getY(),(float)eCenter.getZ() );
            ET.setLocalRotation((float)et.angle, 0, 1, 0);
            ET.setLocalScale(0.6f);
            timer = new Timer();
            timer.schedule(new Enemymover(), 0, 20);
            timer.schedule(new Enemyshoot(), 0, 2500);
            sceneGraph.getRootNode().addChild(ET);
        }
        private class Enemyshoot extends TimerTask {
            public void run(){
                if(et.shoot){
                    mic = new Music("model\\fire.wav",0);
                    new createEnemymissile(gl,et.angle,et);
                }
            }
        }
        private class Enemymover extends TimerTask {
            Point3D newcenter;
            double t;
            boolean move = true;
            Vector3D cc = new Vector3D(0,0,0);
            Vector3D ee = new Vector3D(0,0,0);
            double speed = 0.1;
            public void run() {  
                cc.set(et.getX()-ct.getX(), et.getY()-ct.getY(), et.getZ()-ct.getZ());
                if(cc.getLength()>25 ){
                    et.shoot = false;
                    if(et.getZ()==30 && et.getX()<30 && et.getX()>=-30){
                        ET.setLocalRotation(90, 0, 1, 0);
                        ET.setLocalTranslation((float)(doublePrecision.add(et.getX(), speed)),(float) et.getY(), (float)et.getZ());
                        et.setCenter(new Point3D(doublePrecision.add(et.getX(), speed),et.getY(), et.getZ()));
                    }
                    if(et.getX()==30 && et.getZ()<=30 && et.getZ()>-30){
                        ET.setLocalRotation(180, 0, 1, 0);
                        ET.setLocalTranslation((float)et.getX(),(float) et.getY(), (float)(doublePrecision.sub(et.getZ(), speed)));
                        et.setCenter(new Point3D(et.getX(),et.getY(), doublePrecision.sub(et.getZ(), speed)));
                    } 
                    if(et.getZ()==-30 && et.getX()<=30 && et.getX()>-30){
                        ET.setLocalRotation(270, 0, 1, 0);
                        ET.setLocalTranslation((float)(doublePrecision.sub(et.getX(), speed)),(float) et.getY(), (float)et.getZ());
                        et.setCenter(new Point3D(doublePrecision.sub(et.getX(), speed),et.getY(), et.getZ()));
                    } 
                    if(et.getX()==-30 && et.getZ()<30 && et.getZ()>=-30){
                        ET.setLocalRotation(0, 0, 1, 0);
                        ET.setLocalTranslation((float)et.getX(),(float) et.getY(), (float)(doublePrecision.add(et.getZ(), speed)));
                        et.setCenter(new Point3D(et.getX(),et.getY(), doublePrecision.add(et.getZ(), speed)));
                    } 
                }
                else{
                    Angle = et.turnTank(ct);
                    ET.setLocalRotation((float)Angle, 0, 1, 0);
                    et.shoot = true;
                }
                if(et.boom){
                    cancel();
                }
//                canvas.repaint();          
            }   
        }
    }
    public class createmissile{
        private Missile ms;
        private double dis = 0.0;
        private double gravity = 9.8;
        private double angle;
        public boolean bomb = false;
        createmissile c;
        GLRenderableNode missile;
        //final Animator animator;
        GL gl;
        
        public createmissile(GL gl, double angle){
            c = this;
            this.gl = gl;
            this.angle=angle;
            theta = (double)angle*Math.PI/180;
            ms = new Missile(ct.getCenter(),sceneGraph, theta, ct);
            missile = ms.createMeshNodeFromFile(gl, "missile", Color.GRAY, "model\\missile.obj", "model\\missile.jpg");
            missile.setLocalTranslation((float)ms.getX(), (float)ms.getY(), (float)ms.getZ());
            Timer timer = new Timer();
            timer.schedule(new Missilemover(), 0, 20); 
            sceneGraph.getRootNode().addChild(missile);     
        }
        private class Missilemover extends TimerTask {
            public void run() {
                dis += 0.01;
                double t = (double)angle*Math.PI/180;
                missile.setLocalRotation((float)angle, 0,1,0);
                missile.setLocalTranslation((float)(ms.getX() + dis * Math.sin(t)), (float)0.35, (float)(ms.getZ() + dis * Math.cos(t)));
                missile.setLocalScale(0.15f);
                Point3D newcenter = new Point3D((float)(ms.getX()+dis * Math.sin(t)), (float)ms.getY(), (float)(ms.getZ()+dis * Math.cos(t)));
                ms.setCenter(newcenter);
                if(!bomb && (ms.getX()>=49.5 || ms.getX()<=-49.5 || ms.getZ()>=49.5 || ms.getZ()<=-49.5)){
                    mic = new Music("model\\boom.wav",0);
                    sceneGraph.getRootNode().removeChild(c.missile);
                    bomb = true;
                    c=null;
                    cancel();   
                }
                if(ms!=null){
                    for(int i = 0;i<cet.size();i++){
                        if(ms.getX()-cet.get(i).et.getX()<0.5 && ms.getX()-cet.get(i).et.getX()>-0.5 && ms.getZ()-cet.get(i).et.getZ()<0.5 && ms.getZ()-cet.get(i).et.getZ()>-0.5){
                            mic = new Music("model\\boom.wav",0);
                            mic = new Music("model\\goal.wav",0);
                            sceneGraph.getRootNode().removeChild(cet.get(i).ET);
                            cet.get(i).timer.cancel();
                            cet.get(i).ET=null;
                            sceneGraph.getRootNode().removeChild(c.missile);
                            cet.remove(cet.get(i));
                            cancel();
                            if(cet.size()==0){
                                if(tws!=null){
                                    ki = false; kj = false; kk = false; kl = false; kenter = false;
                                    thread.stop();
//                                    be.drawObject(gl);
                                    mic = new Music("model\\win.wav",0);
                                    m.bt.setVisible(true);
                                    m.remove(tws);
                                    tws=null;
                                    if(!send){
                                        send = true;
                                        Word w = new Word("You Win!");
                                        m.repaint();
                                    }
                                    
                                }
                            }
                        }
                    }
                }
            }
        }
    } 
    public class createEnemymissile{
        private Missile ms;
        private double dis = 0.0;
        private double gravity = 9.8;
        private double angle;
        public boolean bomb = false;
        createEnemymissile c;
        GLRenderableNode missile;
        GL gl;
        
        public createEnemymissile(GL gl, double angle,EnemyTank et){
            c = this;
            this.gl = gl;
            this.angle=angle;
            theta = (double)angle*Math.PI/180;
            ms = new Missile(et.getCenter(),sceneGraph, theta);
            missile = ms.createMeshNodeFromFile(gl, "missile", Color.GRAY, "model\\missile.obj", "model\\missile.jpg");
            missile.setLocalTranslation((float)ms.getX(), (float)ms.getY(), (float)ms.getZ());
            Timer timer = new Timer();
            timer.schedule(new Missilemover(), 0, 20); 
            sceneGraph.getRootNode().addChild(missile);     
        }
        private class Missilemover extends TimerTask {
            public void run() {
                dis += 0.01;
                double t = (double)angle*Math.PI/180;
                missile.setLocalRotation((float)angle, 0,1,0);
                missile.setLocalTranslation((float)(ms.getX() + dis * Math.sin(t)), (float)0.35, (float)(ms.getZ() + dis * Math.cos(t)));
                missile.setLocalScale(0.15f);
                Point3D newcenter = new Point3D((float)(ms.getX()+dis * Math.sin(t)), (float)ms.getY(), (float)(ms.getZ()+dis * Math.cos(t)));
                ms.setCenter(newcenter);
                if(!bomb && (ms.getX()>=49.5 || ms.getX()<=-49.5 || ms.getZ()>=49.5 || ms.getZ()<=-49.5)){
                    mic = new Music("model\\boom.wav",0);
                    sceneGraph.getRootNode().removeChild(c.missile);
                    bomb = true;
                    c=null;
                    cancel();   
                }
                if(ms!=null){
                    for(int i = 0;i<cet.size();i++){
                        if(ms.getX()-cet.get(i).et.getX()<0.5 && ms.getX()-cet.get(i).et.getX()>-0.5 && ms.getZ()-cet.get(i).et.getZ()<0.5 && ms.getZ()-cet.get(i).et.getZ()>-0.5){
                            mic = new Music("model\\boom.wav",0);
                            sceneGraph.getRootNode().removeChild(cet.get(i).ET);
                            cet.get(i).timer.cancel();
                            cet.get(i).ET=null;
                            sceneGraph.getRootNode().removeChild(c.missile);
                            cet.remove(cet.get(i));
                            cancel(); 
                            
                        }
                    }
                    
                    if(ms.getX()-ct.getCenter().getX()<0.5 && ms.getX()-ct.getCenter().getX()>-0.5 && ms.getZ()-ct.getCenter().getZ()<0.5 && ms.getZ()-ct.getCenter().getZ()>-0.5){
                        if(tws!=null){
                            mic = new Music("model\\boom.wav",0);
                            ki = false; kj = false; kk = false; kl = false; kenter = false;
                            sceneGraph.getRootNode().removeChild(myTank);
                            sceneGraph.getRootNode().removeChild(c.missile);
                            cancel();
                            for(int i = 0;i<cet.size();i++)
                                cet.get(i).timer.cancel();
                            mic = new Music("model\\fail.wav",0);
                            thread.stop();
                            //tws=null;
                            
                            if(!send){
                                send = true;
                                Word w = new Word("You Lose!");
                                m.remove(tws);
                                m.bt.setVisible(true);
                                m.repaint();
                            }
                        }
                    }
                }
            }
        }
    }
    
    public GLLightNode createLightNode() {
        GLLightNode lightNode
                = new GLDirectionalLightNode("Directional light", sceneGraph);
        lightNode.setDiffuseColour(new Color(0.7f, 0.7f, 0.7f, 1.0f));
        lightNode.setLocalRotation(65, -6, 2, 0);

        return lightNode;
    }
    
    public GLSpotLightNode createSpotLight(){
        GLSpotLightNode LightNode = new GLSpotLightNode("sun", sceneGraph, Color.WHITE, Color.YELLOW, Color.WHITE);
        LightNode.setCutoffAngle(75);
        LightNode.setLocalTranslation(50, 5, 50);
        LightNode.setLocalRotation(45, 0, 1, 0);
        return LightNode;
    }
    
    public GLDirectionalLightNode createDirectionalLightNode() {
        GLDirectionalLightNode lightNode
                = new GLDirectionalLightNode("Directional light", sceneGraph);
        lightNode.setAmbientColour(new Color(0.2f, 0.2f, 0.2f, 1.0f));
        lightNode.setDiffuseColour(new Color(0.6f, 0.6f, 0.6f, 1.0f));
        lightNode.setSpecularColour(new Color(0.2f, 0.2f, 0.2f, 1.0f));
        lightNode.setLocalTranslation(0f, 10f, 0f);
        lightNode.setLocalRotation(270, 0, 1, 0);

        return lightNode;
    }
    
    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        sceneGraph = new GLSceneGraph();
        sceneGraph.setBackground(Color.BLACK);
        //sceneGraph.setAmbientLight(Color.white);
        
        
        gl.glShadeModel(GL.GL_SMOOTH);
        gl.glEnable(GL.GL_NORMALIZE);
        gl.glEnable(GL.GL_DEPTH_TEST); 
        
        
        GLCameraNode cameraNode = createCameraNode();
        sceneGraph.getRootNode().addChild(cameraNode);
        sceneGraph.setCurrentCamera(cameraNode);
        
        GLMeshNode skyboxNode = createSkyboxNode(gl);
        sceneGraph.getRootNode().addChild(skyboxNode);
        
        sceneGraph.getRootNode().addChild(createFloorNode(0.04f));
        Texture tex1 = new FileTexture("pic\\floor.jpg", gl, true, true);
        floorNode.getMesh().setTextureId(tex1.getID());
        floorNode.getMesh().setTextureMode(GL.GL_MODULATE);
        floorNode.setLocalScale(10.0f);
        floorNode.setLocalTranslation(0.5f, 0f, 0.5f);
        
        Texture tex3 = new FileTexture("pic\\skybox.jpg", gl, true, false);
        skyboxNode.getMesh().setTextureId(tex3.getID());
        skyboxNode.getMesh().setTextureMode(GL.GL_REPLACE);
        skyboxNode.setLocalScale(10.0f);
        skyboxNode.setLocalTranslation(0.5f, 0f, 0.5f);

        myTank = ct.createMeshNodeFromFile(gl, "MyTank", Color.GRAY, "model\\1.obj", "model\\1.jpg");
        myTank.setLocalScale(0.2f);
        myTank.setLocalTranslation((float)ct.getX(),(float)ct.getY(),(float)ct.getZ());
        sceneGraph.getRootNode().addChild(myTank); 
        
        GLRenderableNode gridNode = new GLGridNode("XZ Grid", sceneGraph, glut, Color.ORANGE, 100);
        gridNode.setLocalScale(10, 1, 10);
        gridNode.setLocalTranslation(0, 0, 0);
        sceneGraph.getRootNode().addChild(gridNode);       
        
        initEnemy(gl,sceneGraph);

        cl = createLightNode();
        cl.setEnabled(false);
        sceneGraph.getRootNode().addChild(cl);
        
        cdl = createDirectionalLightNode();
        cdl.setEnabled(false);
        sceneGraph.getRootNode().addChild(cdl);
        
        csl = createSpotLight();
        csl.setEnabled(false);
        sceneGraph.getRootNode().addChild(createSpotLight());
        
    }
    protected void setProjection(GL gl, double width, double height) {
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45, width / height, WORLD_NEAR, WORLD_FAR);          
    }

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();; 
        setProjection(gl, canvas.getWidth(), canvas.getHeight());
        gl.glShadeModel(smoothShading ? GL.GL_SMOOTH : GL.GL_FLAT);
        sceneGraph.renderScene(gl);
        
        if (foggy) {
        gl.glEnable(GL.GL_FOG);
            float[] fogColour = {0.5f, 0.5f, 0.5f, 1.0f};
            gl.glFogfv(GL.GL_COLOR, fogColour, 0);
            gl.glFogi(GL.GL_FOG_MODE, GL.GL_LINEAR);
            gl.glFogf(GL.GL_FOG_START, 15.0f);
            gl.glFogf(GL.GL_FOG_END, 60.0f);
        }
        else
            gl.glDisable(GL.GL_FOG);
            
        if (doPicking) {
            mic = new Music("model\\fire.wav",0);
            lcm.add(new createmissile(gl,angle));
            for(int i=0;i<lcm.size();i++){
                if(lcm.get(i).bomb){
                    lcm.remove(i);
                }
            }
            doPicking = false;
        }
    }
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();
        setProjection(gl, width, height);
    }


    public void displayChanged(GLAutoDrawable drawable,
            boolean modeChanged, boolean deviceChanged) {
    }
    
    
    private class KeyActionListener extends KeyAdapter{
        int count = 0;
        public void keyPressed(KeyEvent e){
            Point3D newcenter;
            int keyCode = e.getKeyCode();
            if(e.getKeyCode()==KeyEvent.VK_W)
                ki = true;
            if(e.getKeyCode()==KeyEvent.VK_A)
                kj = true;
            if(e.getKeyCode()==KeyEvent.VK_S)
                kk = true;
            if(e.getKeyCode()==KeyEvent.VK_D)
                kl = true;
            if(e.getKeyCode()==KeyEvent.VK_ENTER)
                kenter = true;
            
            switch (keyCode) {
                case KeyEvent.VK_U:
                    cam1.up();
                    break;
                case KeyEvent.VK_O:
                    if(cam1.getEye().getY()>0.3)
                        cam1.down();
                    break;
                case KeyEvent.VK_I:
                    cam1.forward();
                    break;
                case KeyEvent.VK_K:
                    cam1.backward();
                    break;
                case KeyEvent.VK_J:
                    cam1.left();
                    break;
                case KeyEvent.VK_L:
                    cam1.right();
                    break;
                case KeyEvent.VK_F:
                    foggy = !foggy;
                    break;
                case KeyEvent.VK_P:
                    count++;
                    if(count>2)
                        count = 0;
                    switch(count){
                        case 0:
                            cl.setEnabled(true);
                            cdl.setEnabled(false);
                            csl.setEnabled(false);
                            break;
                            
                        case 1:
                            cl.setEnabled(false);
                            cdl.setEnabled(true);
                            csl.setEnabled(false);
                            break;
                            
                        case 2:
                            cl.setEnabled(false);
                            cdl.setEnabled(false);
                            csl.setEnabled(true);
                            break;
                            
                    }
                    break;
                case KeyEvent.VK_1:
                    sceneGraph.setCurrentCamera(cam1);
                    break;
                case KeyEvent.VK_2:
                    sceneGraph.setCurrentCamera(cam2);
                    break;
                case KeyEvent.VK_UP:
                    cam2.up();
                    break;
                case KeyEvent.VK_DOWN:
                    cam2.down();
                    break;
                case KeyEvent.VK_LEFT:
                    cam2.left();
                    break;
                case KeyEvent.VK_RIGHT:
                    cam2.right();
                    break;
                case KeyEvent.VK_ESCAPE:
                    m.bt.setVisible(true);
                    m.remove(tws);
                    tws=null;
                    m.repaint();
                    break;      
            } 

            if(ki){
                theta = (double)angle*Math.PI/180;
                if((ct.getX()+0.5 * Math.sin(theta)<45 && (ct.getX()+0.5 * Math.sin(theta))>-45 && (ct.getZ()+0.5 * Math.cos(theta))<45 && (ct.getZ()+0.5 * Math.cos(theta))>-45 )){
                    myTank.setLocalTranslation((float)(ct.getX()+0.5 * Math.sin(theta)), (float)ct.getY(), (float)(ct.getZ()+0.5 * Math.cos(theta)));
                    newcenter = new Point3D((float)(ct.getX()+0.5 * Math.sin(theta)), (float)ct.getY(), (float)(ct.getZ()+0.5 * Math.cos(theta)));
                    ct.setCenter(newcenter);
                    cam1.updateView(newcenter,angle);
                    cam2.setCameraNode(new Point3D(newcenter.getX(),newcenter.getY()+50,newcenter.getZ()), newcenter, new Vector3D(0,0,1));
                    doMoving = true;
                }
                else
                    myTank.setLocalTranslation((float)(ct.getX()), (float)ct.getY(), (float)(ct.getZ()));
            }
            
            if(kk){
                theta = (double)angle*Math.PI/180;
                if((ct.getX()-0.5 * Math.sin(theta)<45 && (ct.getX()-0.5 * Math.sin(theta))>-45 && (ct.getZ()-0.5 * Math.cos(theta))<45 && (ct.getZ()-0.5 * Math.cos(theta))>-45 )){
                    myTank.setLocalTranslation((float)(ct.getX()-0.5 * Math.sin(theta)), (float)ct.getY(), (float)(ct.getZ()-0.5 * Math.cos(theta)));
                    newcenter = new Point3D((float)(ct.getX()-0.5 * Math.sin(theta)), (float)ct.getY(), (float)(ct.getZ()-0.5 * Math.cos(theta)));
                    ct.setCenter(newcenter);
                    cam1.updateView(newcenter,angle);
                    cam2.setCameraNode(new Point3D(newcenter.getX(),newcenter.getY()+50,newcenter.getZ()), newcenter, new Vector3D(0,0,1));
                    doMoving = true;
                }
                else
                    myTank.setLocalTranslation((float)(ct.getX()), (float)ct.getY(), (float)(ct.getZ()));    
            }
            if(kj){
                angle += 1.5;
                myTank.setLocalRotation(angle, 0,1,0);
                cam1.updateDirection(angle);
                
                if(angle>=360 || angle<=-360)
                    angle=0;
            }
            if(kl){
                angle -= 1.5;
                myTank.setLocalRotation(angle, 0,1,0);
                cam1.updateDirection(angle);
               
                if(angle>=360 || angle<=-360)
                    angle=0;
            }
            if(kenter){
                doPicking = true;
            }
//            canvas.repaint();
        }
        public void keyReleased(KeyEvent e) {
            if(e.getKeyCode()==KeyEvent.VK_W)
                ki = false;
            if(e.getKeyCode()==KeyEvent.VK_A)
                kj = false;
            if(e.getKeyCode()==KeyEvent.VK_S)
                kk = false;
            if(e.getKeyCode()==KeyEvent.VK_D)
                kl = false;
            if(e.getKeyCode()==KeyEvent.VK_ENTER)
                kenter = false;
        }
    }
     private class MousePickListener extends MouseAdapter{
        
         public void mouseClicked(MouseEvent e){
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TankWar;

import com.sun.opengl.util.FPSAnimator;
import com.sun.opengl.util.GLUT;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.text.NumberFormat;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

/**
 *
 * @author Konatan
 */
public class Word extends JFrame implements GLEventListener{
   private float rotation;				
    private GLU glu = new GLU();
    private GLUT glut = new GLUT();
    private NumberFormat numberFormat;
    private String str;
    private final GLCanvas canvas;
    private FPSAnimator animator;
    Toolkit kit=Toolkit.getDefaultToolkit();
    Dimension screenSize=kit.getScreenSize();
    int screenWidth=screenSize.width;
    int screenHeight=screenSize.height;
    
    public Word(String str) {
        this.str = str;
        GLCapabilities capabilities = new GLCapabilities();
        canvas = new GLCanvas(capabilities);
        canvas.setBounds(0,0, 600,300);
        canvas.addGLEventListener(this);
        numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        
        
        this.setBounds((screenWidth-600)/2,(screenHeight-300)/2 , 600,300);
        this.getContentPane().add(canvas);
        this.setTitle(str);
        this.setVisible(true);
        animator = new FPSAnimator( canvas, 60 );
        animator.setRunAsFastAsPossible(false);
        animator.start();
    }

    void renderStrokeString(GL gl, int font, String string) {
        float width = glut.glutStrokeLength(font, string);
        gl.glTranslatef(-width / 2f, 0, 0);
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            glut.glutStrokeCharacter(font, c);
        }
    }

    public void init(GLAutoDrawable glDrawable) {
        GL gl = glDrawable.getGL();
        gl.glShadeModel(GL.GL_SMOOTH);							
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);				
        gl.glClearDepth(1.0f);									
        gl.glEnable(GL.GL_DEPTH_TEST);							
        gl.glDepthFunc(GL.GL_LEQUAL);								
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
        gl.glEnable(GL.GL_LIGHT0);								
        gl.glEnable(GL.GL_LIGHTING);								
        gl.glEnable(GL.GL_COLOR_MATERIAL);						
    }

    public void display(GLAutoDrawable glDrawable) {
        GL gl = glDrawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);	
        gl.glLoadIdentity();									
        gl.glTranslatef(0.0f, 0.0f, -15.0f);						
        gl.glRotatef(rotation, 1.0f, 0.0f, 0.0f);					
        gl.glRotatef(rotation * 1.5f, 0.0f, 1.0f, 0.0f);				
        gl.glRotatef(rotation * 1.4f, 0.0f, 0.0f, 1.0f);				
        gl.glScalef(0.01f, 0.01f, 0.01f);
        
        gl.glColor3f((float) (Math.cos(rotation / 20.0f)), (float) (Math.sin(rotation / 25.0f)), 1.0f - 0.5f * (float) (Math.cos(rotation / 17.0f)));
        renderStrokeString(gl, GLUT.STROKE_MONO_ROMAN, str); 
        rotation += 0.5f;										
    }

    public void reshape(GLAutoDrawable glDrawable, int x, int y, int w, int h) {
        if (h == 0) h = 1;
        GL gl = glDrawable.getGL();
        gl.glViewport(0, 0, w, h);                       
        gl.glMatrixMode(GL.GL_PROJECTION);                           
        gl.glLoadIdentity();                                      
        glu.gluPerspective(45.0f, (float) w / (float) h, 0.1f, 100.0f);  
        gl.glMatrixMode(GL.GL_MODELVIEW);                            
        gl.glLoadIdentity();                                      
    }

    public void displayChanged(GLAutoDrawable glDrawable, boolean b, boolean b1) {
    }
}



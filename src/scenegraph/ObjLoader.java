package scenegraph;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * @Author Seth Hall
 */

import com.sun.opengl.util.GLUT;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.media.opengl.GL;

/**
 *
 * @author sehall
 */
public class ObjLoader
{
    private int nPlanes = 0;
    private int n = 0;
    private int index = 0;
    private ArrayList<Plane> planes;
    private ArrayList<Point> points;
    public ArrayList<PolygonalMesh> loadFromFile(String fileName)
            throws IOException
    {   //loaded in shape consisting of 1 or more polygonal mesh
        ArrayList<Point3D> verticesList = new ArrayList<Point3D>();
        ArrayList<Vector3D> normalList = new ArrayList<Vector3D>();
        ArrayList<Texel2D> texelList = new ArrayList<Texel2D>();
        planes = new ArrayList<Plane>();
        points = new ArrayList<Point>();
        
        ArrayList<PolygonalMesh> loadedShape
                = new ArrayList<PolygonalMesh>();
        BufferedReader br = new BufferedReader(
                new FileReader(new File(fileName)));
        String inputLine = "";
        StringTokenizer tokenizer = null;
        //current Polygonal Mesh that we can construct
        //by adding polygonal faces
        PolygonalMesh shape = new PolygonalMesh();
        //Loop until we read in a null value
        do
        {   //read first line and obtain the Obj command (either v, vn,
            //vt, f or g), values should be seperated by spaces
            inputLine = br.readLine();
            String objCommand = "";
            if(inputLine != null)
            {   tokenizer = new StringTokenizer(inputLine);
                objCommand = tokenizer.nextToken(" ");
            }
            //if command is a vertex read in the (x,y,z) co-ordinates
            //and store in the array list of vertices
            if(objCommand.equals("v"))
            {   double x = Double.parseDouble(
                        tokenizer.nextToken(" \t"));
                double y = Double.parseDouble(
                        tokenizer.nextToken(" \t"));
                double z = Double.parseDouble(
                        tokenizer.nextToken(" \t"));
                verticesList.add(new Point3D(x,y,z));
                Point point = new Point();
                point.x = (float)x;
                point.y = (float)y;
                point.z = (float)z;
                points.add(point);
            }
            //if command is a vector normal read in the (x,y,z) values
            //and add to the array list of vectors
            else if(objCommand.equals("vn"))
            {   double x = Double.parseDouble(
                        tokenizer.nextToken(" \t"));
                double y = Double.parseDouble(
                        tokenizer.nextToken(" \t"));
                double z = Double.parseDouble(
                        tokenizer.nextToken(" \t"));
                normalList.add(new Vector3D(x,y,z));
            }
            //if command is a texel, read in the (s,t) values and add
            //to array list of texel values (might not be given)
            else if(objCommand.equals("vt"))
            {  double s = Double.parseDouble(
                       tokenizer.nextToken(" \t"));
               double t = Double.parseDouble(
                       tokenizer.nextToken(" \t"));
               texelList.add(new Texel2D(s, t));
            }
            //called if command is to add a face to the polygonal mesh
            else if(objCommand.equals("f"))
            {   boolean texturesAdded = true;
                //textures are optional, need to check this condition
                //as StringTokenizer will not determine if there is
                //nothing between the 2 slashes.
               if(inputLine.indexOf("//") >= 0)
                    texturesAdded = false;
               //create ArrayLists for points, normals and texels for
               //this face, each index of point/texel(optional)/normal
               //corresponds to the loaded in array lists,
               //these values start from index 1.
               ArrayList<Point3D> facePoints
                       = new ArrayList<Point3D>();
               ArrayList<Vector3D> faceNormals
                       = new ArrayList<Vector3D>();
               ArrayList<Texel2D> faceTexels
                       = new ArrayList<Texel2D>();
               Plane plane = new Plane();
               //loop for each set of point/texel/normal values
               while(tokenizer.hasMoreTokens())
               {  //get index for the vertex
                    int i=0;
                    int vertexIndex = Integer.parseInt(
                            tokenizer.nextToken(" /\t"));
                    facePoints.add(verticesList.get(vertexIndex-1));
                    //get index value for the texel (if included in face)
                    if(texturesAdded)
                    {  int textureIndex = Integer.parseInt(
                               tokenizer.nextToken(" /\t"));
                       faceTexels.add(texelList.get(textureIndex-1));
                    }
                    int normalIndex = Integer.parseInt(
                            tokenizer.nextToken(" /\t"));
                    faceNormals.add(normalList.get(normalIndex-1));
                    plane.p[i]=vertexIndex;
                    plane.normals[i].x = (float)normalList.get(normalIndex-1).getX();
                    plane.normals[i].y = (float)normalList.get(normalIndex-1).getY();
                    plane.normals[i].z = (float)normalList.get(normalIndex-1).getZ();
                    i++;
               }
               planes.add(plane);
               //add Vector3D[], Point3D[], Texel2D[] (if any)
               //to create polygonal face
               if(faceTexels.size() > 0)
               {    shape.addFace(facePoints.toArray(
                       new Point3D[facePoints.size()]),
                       faceNormals.toArray(
                       new Vector3D[faceNormals.size()]),
                       faceTexels.toArray(
                       new Texel2D[faceTexels.size()]));
                       
               }else
               {    shape.addFace(facePoints.toArray(
                       new Point3D[facePoints.size()]),
                       faceNormals.toArray(
                       new Vector3D[faceNormals.size()]));
               }
               
            }
            //create a new Polygonal mesh.
            else if(objCommand.equals("g"))
            {   //get the optional name for this mesh
                String meshName = tokenizer.nextToken(" \t");
                //need to check this condition to handle if the object
                //is the first shape in the list to avoid adding a
                //emtpy polygonal mesh.
                if(verticesList.size() > 0 && normalList.size() > 0)
                {  //add previous constructed mesh to the loaded object
                   //and create a new polygonal mesh to construct.
                   loadedShape.add(shape);
                }
                shape = new PolygonalMesh(meshName);
            }
         }while(inputLine != null);
         //add previous constructed mesh to loaded object
         loadedShape.add(shape);
         //clear the Lists of points, texels and vectors
         verticesList.clear();
         normalList.clear();
         texelList.clear();
         calcPlanes();
         setConnectivity();
         return loadedShape;
    }
    
    public void setConnectivity() {
        for (int i = 0; i < nPlanes - 1; i++)
            for (int j = i + 1; j < nPlanes; j++)
                for (int ki = 0; ki < 3; ki++)
                    if (planes.get(i).neigh[ki] == 0) {
                        for (int kj = 0; kj < 3; kj++) {
                            int p1i = ki;
                            int p1j = kj;
                            int p2i = (ki + 1) % 3;
                            int p2j = (kj + 1) % 3;

                            p1i = planes.get(i).p[p1i];
                            p2i = planes.get(i).p[p2i];
                            p1j = planes.get(j).p[p1j];
                            p2j = planes.get(j).p[p2j];

                            int P1i = ((p1i + p2i) - Math.abs(p1i - p2i)) / 2;
                            int P2i = ((p1i + p2i) + Math.abs(p1i - p2i)) / 2;
                            int P1j = ((p1j + p2j) - Math.abs(p1j - p2j)) / 2;
                            int P2j = ((p1j + p2j) + Math.abs(p1j - p2j)) / 2;

                            if ((P1i == P1j) && (P2i == P2j)) {  //they are neighbours
                                planes.get(i).neigh[ki] = j + 1;
                                planes.get(j).neigh[kj] = i + 1;
                            }
                        }
                    }
    }
    private void calcPlanes() {
        for (int i = 0; i < nPlanes; i++)                   
            calcPlane(planes.get(i));   
    }
    
    private void calcPlane(Plane plane) {
        Point v[] = new Point[4];

        for (int i = 0; i < 3; i++) {
            v[i + 1] = new Point();
            v[i + 1].x = points.get(plane.p[i]).x;
            v[i + 1].y = points.get(plane.p[i]).y;
            v[i + 1].z = points.get(plane.p[i]).z;
        }
        plane.PlaneEq.a = v[1].y * (v[2].z - v[3].z) + v[2].y * (v[3].z - v[1].z) + v[3].y * (v[1].z - v[2].z);
        plane.PlaneEq.b = v[1].z * (v[2].x - v[3].x) + v[2].z * (v[3].x - v[1].x) + v[3].z * (v[1].x - v[2].x);
        plane.PlaneEq.c = v[1].x * (v[2].y - v[3].y) + v[2].x * (v[3].y - v[1].y) + v[3].x * (v[1].y - v[2].y);
        plane.PlaneEq.d = -(v[1].x * (v[2].y * v[3].z - v[3].y * v[2].z) +
                v[2].x * (v[3].y * v[1].z - v[1].y * v[3].z) +
                v[3].x * (v[1].y * v[2].z - v[2].y * v[1].z));
    }
 public void castShadow(GL gl, float[] lp) {
                    Point v1 = new Point();
                    Point v2 = new Point();

                    for (int i = 0; i < planes.size(); i++) {
                        float side = planes.get(i).PlaneEq.a * lp[0] +
                                planes.get(i).PlaneEq.b * lp[1] +
                                planes.get(i).PlaneEq.c * lp[2] +
                                planes.get(i).PlaneEq.d * lp[3];
                        if (side > 0)
                            planes.get(i).visible = true;
                        else
                            planes.get(i).visible = false;
                    }

                    gl.glDisable(GL.GL_LIGHTING);
                    gl.glDepthMask(false);
                    gl.glDepthFunc(GL.GL_LEQUAL);

                    gl.glEnable(GL.GL_STENCIL_TEST);
                    gl.glColorMask(false, false, false, false);
                    gl.glStencilFunc(GL.GL_ALWAYS, 1, 0xffffffff);

                    gl.glFrontFace(GL.GL_CCW);
                    gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_INCR);

                    for (int i = 0; i < nPlanes; i++) {
                        if (planes.get(i).visible)
                            for (int j = 0; j < 3; j++) {
                                int k = planes.get(i).neigh[j];
                                if ((k == 0) || (!planes.get(k-1).visible)) {

                                    int p1 = planes.get(i).p[j];
                                    int jj = (j + 1) % 3;
                                    int p2 = planes.get(i).p[jj];

                                    v1.x = (points.get(p1).x - lp[0]) * 100;
                                    v1.y = (points.get(p1).y - lp[1]) * 100;
                                    v1.z = (points.get(p1).z - lp[2]) * 100;

                                    v2.x = (points.get(p2).x - lp[0]) * 100;
                                    v2.y = (points.get(p2).y - lp[1]) * 100;
                                    v2.z = (points.get(p2).z - lp[2]) * 100;

                                    gl.glBegin(GL.GL_TRIANGLE_STRIP);
                                    gl.glVertex3f(points.get(p1).x,
                                            points.get(p1).y,
                                            points.get(p1).z);
                                    gl.glVertex3f(points.get(p1).x + v1.x,
                                            points.get(p1).y + v1.y,
                                            points.get(p1).z + v1.z);

                                    gl.glVertex3f(points.get(p2).x,
                                            points.get(p2).y,
                                            points.get(p2).z);
                                    gl.glVertex3f(points.get(p2).x + v2.x,
                                            points.get(p2).y + v2.y,
                                            points.get(p2).z + v2.z);
                                    gl.glEnd();
                                }
                            }
                    }

                    gl.glFrontFace(GL.GL_CW);
                    gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_DECR);

                    for (int i = 0; i < nPlanes; i++) {
                        if (planes.get(i).visible)
                            for (int j = 0; j < 3; j++) {
                                int k = planes.get(i).neigh[j];
                                if ((k == 0) || (!planes.get(k-1).visible)) {

                                    int p1 = planes.get(i).p[j];
                                    int jj = (j + 1) % 3;
                                    int p2 = planes.get(i).p[jj];

                                    v1.x = (points.get(p1).x - lp[0]) * 100;
                                    v1.y = (points.get(p1).y - lp[1]) * 100;
                                    v1.z = (points.get(p1).z - lp[2]) * 100;

                                    v2.x = (points.get(p2).x - lp[0]) * 100;
                                    v2.y = (points.get(p2).y - lp[1]) * 100;
                                    v2.z = (points.get(p2).z - lp[2]) * 100;

                                    gl.glBegin(GL.GL_TRIANGLE_STRIP);
                                    gl.glVertex3f(points.get(p1).x,
                                            points.get(p1).y,
                                            points.get(p1).z);
                                    gl.glVertex3f(points.get(p1).x + v1.x,
                                            points.get(p1).y + v1.y,
                                            points.get(p1).z + v1.z);

                                    gl.glVertex3f(points.get(p2).x,
                                            points.get(p2).y,
                                            points.get(p2).z);
                                    gl.glVertex3f(points.get(p2).x + v2.x,
                                            points.get(p2).y + v2.y,
                                            points.get(p2).z + v2.z);
                                    gl.glEnd();
                                }
                            }
                    }
                    gl.glFrontFace(GL.GL_CCW);
                    gl.glColorMask(true, true, true, true);

                    gl.glColor4f(1.0f, 1.0f, 1.0f, 0.4f);
                    gl.glEnable(GL.GL_BLEND);
                    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
                    gl.glStencilFunc(GL.GL_NOTEQUAL, 0, 0xffffffff);
                    gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
                    gl.glPushMatrix();
                    gl.glLoadIdentity();
                    gl.glBegin(GL.GL_TRIANGLE_STRIP);
                    gl.glVertex3f(-1f, 1f, -1f);
                    gl.glVertex3f(-1f, -1f, -1f);
                    gl.glVertex3f(1f, 1f, -1f);
                    gl.glVertex3f(1f, -1f, -1f);
                    gl.glEnd();
                    gl.glPopMatrix();
                    gl.glDisable(GL.GL_BLEND);

                    gl.glDepthFunc(GL.GL_LEQUAL);
                    gl.glDepthMask(true);
                    gl.glEnable(GL.GL_LIGHTING);
                    gl.glDisable(GL.GL_STENCIL_TEST);
                    gl.glShadeModel(GL.GL_SMOOTH);
                }

    private static class Point {
        float x, y, z;
    }

    // plane equation
    private static class PlaneEq {
        float a, b, c, d;
    }

    // structure describing an object's face
    private static class Plane {
        int p[] = new int[3],
        neigh[] = new int[3];
        Point normals[] = new Point[3];
        boolean visible;
        PlaneEq PlaneEq;

        Plane() {
            PlaneEq = new PlaneEq();
            for (int i = 0; i < 3; i++)
                normals[i] = new Point();
        }
    }
}

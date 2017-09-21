package scenegraph;

import javax.media.opengl.GL;

/**
   A utility class that represents a polygonal face in a mesh
   @author Andrew Ensor
*/
public class PolygonalFace
{  // vertices and normals for face, note arrays have same length
   private Point3D[] vertices;
   private Vector3D[] normals;
   private Texel2D[] texels;
   public PolygonalFace(Point3D[] vertices, Vector3D normal)
   {  this(vertices,normal,null);
   }

   public PolygonalFace(Point3D[] vertices, Vector3D[] normals)
   {  this(vertices,normals,null);
   }

   public PolygonalFace(Point3D[] vertices, Vector3D[] normals,
      Texel2D[] texels)
   {  if (vertices == null || vertices.length == 0)
         throw new IllegalArgumentException("no vertices in face");
      this.vertices = vertices;
      if (normals == null || normals.length == 0)
         throw new IllegalArgumentException("no normals for face");
      if (vertices.length != normals.length)
         throw new IllegalArgumentException
            ("vertices don't match normals");
      if (texels != null && vertices.length != texels.length)
         throw new IllegalArgumentException
            ("vertices don't match texels");
      this.normals = normals;
      this.texels = texels;
   }

   public PolygonalFace(Point3D[] vertices, Vector3D normal,
      Texel2D[] texels)
   {  if (vertices == null || vertices.length == 0)
         throw new IllegalArgumentException("no vertices in face");
      this.vertices = vertices;
      if (normal == null)
         throw new IllegalArgumentException("no normal for face");
      if (texels != null && vertices.length != texels.length)
         throw new IllegalArgumentException
            ("vertices don't match texels");
      // create the normals array
      normals = new Vector3D[vertices.length];
      for (int i=0; i<normals.length; i++)
         normals[i] = normal;
      this.texels = texels;
   }

   public Point3D[] getVertices()
   {  return vertices;
   }

   public Vector3D[] getNormals()
   {  return normals;
   }

   public String toString()
   {  String output = "";
      for (int i=0; i<vertices.length; i++)
         output += " " + vertices[i] + ":" + normals[i];
      return output;
   }

   public Texel2D[] getTexels()
   {  return texels;
   }
}

package scenegraph;

public class Point3D
{
   private double px, py, pz; // coordinates of point
   
   public Point3D(double px, double py, double pz)
   {  set(px, py, pz);
   }
   
   // constructs a new point that has same coordinates as parameter
   public Point3D(Point3D point)
   {  set(point.px, point.py, point.pz);
   }
   
   public double getX()
   {  return px;
   }
   
   public double getY()
   {  return py;
   }
   
   public double getZ()
   {  return pz;
   }
   
   // mutator method for changing coordinates of point
   public void set(double px, double py, double pz)
   {  this.px = px;
      this.py = py;
      this.pz = pz;
   }
   
   // returns the point resulting from adding vector parameter
   // to this point
   public Point3D add(Vector3D other)
   {  return new Point3D(this.px+other.getX(), this.py+other.getY(),
         this.pz+other.getZ());
   }
   
   // returns the vector resulting from subtracting point parameter
   // from this point
   public Vector3D subtract(Point3D other)
   {  return new Vector3D(this.px-other.px, this.py-other.py,
         this.pz-other.pz);
   }
   
   public String toString()
   {  return "(" + px + ", " + py + ", " + pz + ")";
   }
}
   

package scenegraph;

/**
   A utility class that represents a three dimensional vector
   @author Andrew Ensor
*/
public class Vector3D
{
   private double vx, vy, vz; // components of vector
   
   public Vector3D(double vx, double vy, double vz)
   {  this.vx = vx;
      this.vy = vy;
      this.vz = vz;
   }
   
   // constructs a new vector that has same components as parameter
   public Vector3D(Vector3D vector)
   {  this.vx = vector.vx;
      this.vy = vector.vy;
      this.vz = vector.vz;
   }
   
   public double getX()
   {  return vx;
   }
   
   public double getY()
   {  return vy;
   }
   
   public double getZ()
   {  return vz;
   }
   
   // mutator method for changing components of vector
   public void set(double vx, double vy, double vz)
   {  this.vx = vx;
      this.vy = vy;
      this.vz = vz;
   }
   
   // returns the vector resulting from adding vector parameter
   // to this vector
   public Vector3D add(Vector3D other)
   {  return new Vector3D(this.vx+other.vx, this.vy+other.vy,
         this.vz+other.vz);
   }
   
   // returns the vector resulting from subtracting vector parameter
   // from this vector
   public Vector3D subtract(Vector3D other)
   {  return new Vector3D(this.vx-other.vx, this.vy-other.vy,
         this.vz-other.vz);
   }
   
   // returns the vector resulting from scalar multiplying
   // this vector by the scalar parameter
   public Vector3D scalarMultiple(double scalar)
   {  return new Vector3D(this.vx*scalar, this.vy*scalar,
         this.vz*scalar);
   }
   
   // returns the dot product of this vector with vector parameter
   public double dotProduct(Vector3D other)
   {  return this.vx*other.vx + this.vy*other.vy + this.vz*other.vz;
   }
   
   public double getCos(Vector3D other)
   {  
       double cos = this.dotProduct(other)/(this.getLength()*other.getLength());
       return cos;
   }
   public double getParallel(Vector3D other){
       return this.vx*other.vz-this.vz*other.vx;
   }
   // returns the cross product of this vector with vector parameter
   public Vector3D crossProduct(Vector3D other)
   {  double cx = this.vy*other.vz - this.vz*other.vy;
      double cy = this.vz*other.vx - this.vx*other.vz;
      double cz = this.vx*other.vy - this.vy*other.vx;
      return new Vector3D(cx, cy, cz);
   }
   
   // returns the length of this vector
   public double getLength()
   {  return Math.sqrt(vx*vx+vy*vy+vz*vz);
   }
   
   // returns a unit vector parallel to this vector
   public Vector3D normalized()
   {  double length = getLength();
      return new Vector3D(vx/length, vy/length, vz/length);
   }
   
   public String toString()
   {  return "<" + vx + ", " + vy + ", " + vz + ">";
   }
}

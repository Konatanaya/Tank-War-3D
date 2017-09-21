package scenegraph;

/**
   A utility class that represents a s and t coordinate for
   a texel on a texture
   @Author Seth Hall
*/

public class Texel2D
{   public double s;
    public double t;

    public Texel2D()
    {   this(0,0);
    }
    public Texel2D(double s, double t)
    {   this.s = s;
        this.t = t;
    }
    public double getSValue()
    { return s;
    }
    public double getTValue()
    {  return t;
    }
    public String toString()
    {
        return "("+s+","+t+")";
    }
}

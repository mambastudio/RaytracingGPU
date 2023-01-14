/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.geom;

import coordinate.generic.SCoord;
import coordinate.struct.structfloat.FloatStruct;


/**
 *
 * @author user
 * 
 * This is only used for mesh data only, OpenCL handles data differently.
 * 
 */
public class RPoint3 extends FloatStruct implements SCoord<RPoint3, RVector3>{
public float x, y, z, w;
    public RPoint3(){
        super();
    }

    public RPoint3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public RPoint3(RPoint3 p) {
        x = p.x;
        y = p.y;
        z = p.z;
    }
                
    public static final RVector3 sub(RPoint3 p1, RPoint3 p2) 
    {
        RVector3 dest = new RVector3();
        dest.x = p1.x - p2.x;
        dest.y = p1.y - p2.y;
        dest.z = p1.z - p2.z;
        return dest;
    }

    public static final RPoint3 mid(RPoint3 p1, RPoint3 p2) 
    {
        RPoint3 dest = new RPoint3();
        dest.x = 0.5f * (p1.x + p2.x);
        dest.y = 0.5f * (p1.y + p2.y);
        dest.z = 0.5f * (p1.z + p2.z);
        return dest;
    }
    
   
    public RPoint3 setValue(float x, float y, float z) {
        RPoint3 p = SCoord.super.setValue(x, y, z);
        this.refreshGlobalArray();
        return p;
    }
    

    @Override
    public int getSize() {
        return 4;
    }

    @Override
    public float[] getArray() {
        return new float[]{x, y, z, 0};
    }

    @Override
    public void set(float... values) {        
        x = values[0];
        y = values[1];
        z = values[2];
    }

    @Override
    public float get(char axis) {
        switch (axis) {
            case 'x':
                return x;
            case 'y':
                return y;
            case 'z':
                return z;
            default:
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.  
        }
    }

    @Override
    public void set(char axis, float value) {
        switch (axis) {
            case 'x':
                x = value;
                break;
            case 'y':
                y = value;
                break;
            case 'z':
                z = value;
                break;
            default:
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public RPoint3 copy() {
        return new RPoint3(x, y, z);
    }

    @Override
    public RPoint3 getSCoordInstance() {
        return new RPoint3();
    }

    @Override
    public RVector3 getVCoordInstance() {
        return new RVector3();
    }
    
    @Override
    public void setIndex(int index, float value) {
        switch (index)
        {
            case 0:
                x = value;
                break;
            case 1:
                y = value;
                break;    
            case 2:
                z = value;
                break;
        }
    }
        
    @Override
    public String toString()
    {
        float[] array = getArray();
        return String.format("(%3.2f, %3.2f, %3.2f)", array[0], array[1], array[2]);
    }

    @Override
    public int getByteSize() {
        return 4;
    }
}

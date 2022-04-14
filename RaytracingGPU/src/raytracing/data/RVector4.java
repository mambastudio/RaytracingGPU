/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.data;

import coordinate.generic.VCoord;
import coordinate.struct.structfloat.FloatStruct;

/**
 *
 * @author user
 */
public class RVector4 extends FloatStruct implements VCoord<RVector4>{
    
    public float x, y, z, w;
    public RVector4(){}
    public RVector4(float x, float y, float z, float w){this.x = x; this.y = y; this.z = z; this.w = w;};
    public RVector4(RVector4 v) {this.x = v.x; this.y = v.y; this.z = v.z; this.w = v.w;}

    @Override
    public RVector4 getCoordInstance() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RVector4 copy() {
        return new RVector4(x, y, z, w);
    }

    @Override
    public void set(float... values) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getByteSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

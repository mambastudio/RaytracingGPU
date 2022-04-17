/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.structs;

import coordinate.struct.structbyte.Structure;
import raytracing.geom.RPoint3;

/**
 *
 * @author user
 */
public class RFrame extends Structure{
    public RPoint3 mX;
    public RPoint3 mY;
    public RPoint3 mZ;
    
    public RFrame()
    {
        mX = new RPoint3();
        mY = new RPoint3();
        mZ = new RPoint3();
    }
}

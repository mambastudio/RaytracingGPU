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
public class RBsdf extends Structure{
    public  RSurfaceParameter2  param;     //chosen surface

    public  RFrame frame;                 //local frame of reference

    public  RPoint3 localDirFix;          //incoming (fixed) incoming direction, in local
    public  RPoint3 localGeomN;           //geometry normal (without normal shading)
   
    public  int materialID;              //material id (Check if necessary, if not remove)
   
    public  RComponentProbabilities probabilities; //!< Sampling probabilities
    
    public RBsdf()
    {
        param           = new RSurfaceParameter2();
        frame           = new RFrame();
        localDirFix     = new RPoint3();
        localGeomN      = new RPoint3();
        materialID      = 0;
        probabilities   = new RComponentProbabilities();
    }
}

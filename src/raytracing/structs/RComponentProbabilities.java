/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.structs;

import coordinate.struct.structbyte.StructBufferMemory;


/**
 *
 * @author user
 */
public class RComponentProbabilities extends StructBufferMemory{
    public float diffProb;
    public float glossyProb;
    public float reflProb;
    public float refrProb;
    
    
}

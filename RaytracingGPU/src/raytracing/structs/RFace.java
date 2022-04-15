/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.structs;

import coordinate.struct.structint.IntStruct;

/**
 *
 * @author user
 */
public class RFace extends IntStruct{
    public int     v1,  v2,  v3;
    public int     uv1, uv2, uv3;
    public int     n1,  n2,  n3;
    public int     mat;
    
    public RFace()
    {
        v1  = v2  = v3  = 0;
        uv1 = uv2 = uv3 = 0;
        n1  = n2  = n3  = 0;
        mat = -1;
    }
    
    public int getMaterialIndex()
    {
        return mat & 0xFFFF;
    }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(); 
        builder.append("v1  ").append(v1).append(" v2  " ).append(v2).append(" v3  ").append(v3).append("\n");
        builder.append("uv1 ").append(uv1).append(" uv2 ").append(uv2).append(" uv3 ").append(uv3).append("\n");
        builder.append("n1 ").append(n1).append(" n2 ").append(n2).append(" n3 ").append(n3).append("\n");
        builder.append("mat ").append(mat);
         
        return builder.toString();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.abstracts;


import coordinate.generic.raytrace.AbstractAccelerator;
import raytracing.mesh.RMesh;
import raytracing.structs.RBound;
import raytracing.structs.RIntersection;
import raytracing.structs.RNode;
import raytracing.structs.RRay;
import wrapper.core.CMemory;

/**
 *
 * @author user
 */
public interface RayAcceleratorInterface extends AbstractAccelerator<RRay, RIntersection, RMesh, RBound>
{   
    public CMemory<RBound> getBounds();
    public CMemory<RNode> getNodes();
    default int getStartNodeIndex()
    {
        return 0;
    }
}

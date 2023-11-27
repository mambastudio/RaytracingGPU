/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grid.mesh;

import coordinate.generic.g2.AbstractMesh;
import static coordinate.generic.g2.AbstractMesh.MeshMemoryType.NATIVE;
import coordinate.generic.g2.AbstractTriangleIndex;
import grid.geometry.Vector4f;
import grid.geometry.Point2f;
import grid.geometry.Point4f;
import grid.geometry.Ray;
import grid.geometry.BoundingBox;
import grid.geometry.Point4i;
import grid.mesh.TriangleMesh.TriangleIndex;

/**
 *
 * @author user
 */
public class TriangleMesh extends AbstractMesh<Point4f, Vector4f, Point2f, Ray, BoundingBox, TriangleIndex, Triangle>{
    private final BoundingBox bounds;
        
    public TriangleMesh()
    {
        super(NATIVE);
        bounds = new BoundingBox();
    }

    @Override
    public void addPoint(Point4f p) {        
        long index = pointsCount.getAndIncrement();
        points.set(index, p);
        bounds.include(p);
    }

    @Override
    public void addPoint(float... values) { 
        addPoint(new Point4f(values[0], values[1], values[2]));
    }

    @Override
    public void addNormal(Vector4f n) {
        long index = normalsCount.getAndIncrement();
        normals.set(index, n);
    }

    @Override
    public void addNormal(float... values) {
        addNormal(new Vector4f(values[0], values[1], values[2]));
    }

    @Override
    public void addTexCoord(Point2f t) {
        long index = texcoordsCount.getAndIncrement();
        texcoords.set(index, t);
    }

    @Override
    public void addTexCoord(float... values) {
        addTexCoord(new Point2f(values[0], values[1]));
    }

    @Override
    public Triangle getTriangle(int index) {
        if(this.hasNormal(index))
            return new Triangle(
                    this.getVertex1(index), 
                    this.getVertex2(index), 
                    this.getVertex3(index),
                    this.getNormal1(index),
                    this.getNormal2(index),
                    this.getNormal3(index));
        else
            return new Triangle(
                    this.getVertex1(index), 
                    this.getVertex2(index), 
                    this.getVertex3(index));
    }

    @Override
    public BoundingBox getBounds() {
        return bounds;
    }

    @Override
    public void addTriangle(int vert1, int vert2, int vert3, int uv1, int uv2, int uv3, int norm1, int norm2, int norm3, int data) {
        long index = facesCount.getAndIncrement();
        triangleFaces.set(index, new TriangleIndex(vert1, vert2, vert3, uv1, uv2, uv3, norm1, norm2, norm3, data));
    }
    
    public static class TriangleIndex extends AbstractTriangleIndex<Point4i> {
        public TriangleIndex()
        {
            super(new Point4i(), new Point4i(), new Point4i());
        }

        public TriangleIndex(
                int vert1, int vert2, int vert3, 
                int uv1, int uv2, int uv3, 
                int norm1, int norm2, int norm3, 
                int data)
        {
            super(
                    new Point4i(vert1, vert2, vert3), 
                    new Point4i(uv1, uv2, uv3, data), 
                    new Point4i(norm1, norm2, norm3));
        }

        @Override
        public String toString()
        {
            return String.format("v: %7.0f, %7.0f, %7.0f, u: %7.0f, %7.0f, %7.0f, n: %7.0f, %7.0f, %7.0f, m: %7.0f", 
                        (float)v_123.x,     (float)v_123.y,     (float)v_123.z,
                        (float)u_123m.x,    (float)u_123m.y,    (float)u_123m.z,
                        (float)n_123.x,     (float)n_123.y,     (float)n_123.z,
                        (float)(u_123m.w & 0xFFFF));
        }


        @Override
        public TriangleIndex newStruct() {
            return new TriangleIndex();
        }

        @Override
        public TriangleIndex copyStruct() {
            return new TriangleIndex();
        }    
    }
}

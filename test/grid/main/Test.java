/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grid.main;

import coordinate.parser.obj.OBJInfo;
import grid.mesh.TriangleMesh;
import grid.geometry.Point2f;
import grid.geometry.Point4f;
import grid.geometry.Vector4f;
import grid.mesh.TriangleMesh.TriangleIndex;
import grid.parser.OBJMappedParser;
import grid.parser.OBJMappedParser2;
import java.io.File;
import java.nio.file.Paths;

/**
 *
 * @author user
 */
public class Test {
    public static void main(String... args)
    {
        testFile2();
    }
    
    public static void testFile()
    {
        File file = Paths.get("C:\\Users\\user\\Documents\\3D Scenes\\003_96_ferrari_550_maranello_wwc\\OBJ", 
                "96_Ferrari_550_Maranello.obj").toFile();
        
        OBJMappedParser parser = new OBJMappedParser();            
        TriangleMesh mesh = new TriangleMesh();
        parser.readAttributes(file.toURI());

        //init size (estimate) of coordinate list/array
        OBJInfo info = parser.getInfo(); System.out.println(info.f());
        mesh.initCoordList(new Point4f(), new Point2f(), new Vector4f(), new TriangleIndex(),
                info.v(), info.vt(), info.vn(), info.f());
        
        parser.read(file.toURI(), mesh); 
        
        System.out.println(mesh.getBounds());
    }
    
    public static void testFile2()
    {
        File file = Paths.get("C:\\Users\\user\\Documents\\3D Scenes\\003_96_ferrari_550_maranello_wwc\\OBJ", 
                "96_Ferrari_550_Maranello.obj").toFile();
        
        OBJMappedParser2 parser = new OBJMappedParser2();            
        TriangleMesh mesh = new TriangleMesh();
        parser.readAttributes(file.toURI());

        //init size (estimate) of coordinate list/array
        OBJInfo info = parser.getInfo();        
        System.out.println(info.f());
        
        mesh.initCoordList(new Point4f(), new Point2f(), new Vector4f(), new TriangleIndex(),
                info.v(), info.vt(), info.vn(), info.f());
        
        parser.read(file.toURI(), mesh); 
        
        
    }
}

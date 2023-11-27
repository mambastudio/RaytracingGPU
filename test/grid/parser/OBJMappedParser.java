/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grid.parser;

import coordinate.generic.g2.AbstractMesh;
import static coordinate.generic.g2.AbstractMesh.MeshType.FACE;
import static coordinate.generic.g2.AbstractMesh.MeshType.FACE_NORMAL;
import static coordinate.generic.g2.AbstractMesh.MeshType.FACE_UV;
import static coordinate.generic.g2.AbstractMesh.MeshType.FACE_UV_NORMAL;
import coordinate.generic.g2.AbstractParser;
import coordinate.generic.io.LineMappedReader;
import coordinate.parser.attribute.MaterialT;
import coordinate.parser.obj.OBJInfo;
import static coordinate.parser.obj.OBJInfo.SplitOBJPolicy.GROUP;
import static coordinate.parser.obj.OBJInfo.SplitOBJPolicy.NONE;
import static coordinate.parser.obj.OBJInfo.SplitOBJPolicy.OBJECT;
import static coordinate.parser.obj.OBJInfo.SplitOBJPolicy.USEMTL;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author user
 */
public class OBJMappedParser implements AbstractParser{
    private AbstractMesh mesh;           
    private ArrayList<MaterialT> sceneMaterials;
    
    private int idCount;
    private ArrayList<MaterialT> idMaterials;
        
    private OBJInfo info;
    
    int c = 0;
    
    public ArrayList<MaterialT> getSceneMaterialList()
    {
        if(sceneMaterials.isEmpty())
            return new ArrayList<>(Arrays.asList(new MaterialT()));
        else
            return sceneMaterials;
    }
    
    public OBJInfo getInfo()
    {
        return info;
    }
    
    @Override
    public void read(String uri, AbstractMesh data)
    {
        read(new File(uri).toURI(), data);
    }
    
    @Override
    public void read(URI uri, AbstractMesh data)
    {        
        read(data);
    }
    
    @Override
    public void readString(String string, AbstractMesh mesh) {        
        throw new UnsupportedOperationException("strings as source file is not allowed");
    }
    
    public void readAttributes(String uri)
    {
        readAttributes(new File(uri).toURI());
    }
    
    public void readAttributes(URI uri)
    {
         info = new OBJInfo(uri);
         info.read();  
    }
    
    private void read(AbstractMesh mesh)
    {        
        this.mesh = mesh;   
        this.sceneMaterials = new ArrayList<>();
        this.idMaterials = new ArrayList<>();
        this.idCount = -1;
        
        LineMappedReader parser = info.reader;
        parser.rewind();
                
        parser.goToStartChar();
        while(parser.hasRemaining())
        {            
            if(parser.isCurrentIsolated("v"))
            {               
                float[] array = parser.readLineFloatArray();                
                mesh.addPoint(array);
                
            }            
            else if(parser.isCurrentIsolated("vn"))
            {
                mesh.addNormal(parser.readLineFloatArray());
            }
            else if(parser.isCurrentIsolated("vt"))
            {
                mesh.addTexCoord(parser.readLineFloatArray());
            }
            else if(parser.isCurrentIsolated("usemtl"))
            {
                String mtl = parser.readLineString().replace("usemtl", "").trim();
                MaterialT mat = new MaterialT(mtl);

                sceneMaterials.add(new MaterialT(mat));

                if(info.splitPolicy() == USEMTL)
                {
                    //id material
                    idMaterials.add(mat);
                    idCount++;               
                }                
            }
            else if(parser.isCurrentIsolated("f"))
            {                                      
                boolean doubleBackSlash = parser.currentLineContains("//");
                boolean singleBackSlash = parser.currentLineContains("/");
                                 
                //handle various type of face types
                if(singleBackSlash)
                    readSingleSlashFace(parser);
                else if(doubleBackSlash)
                    readDoubleSlashFace(parser);
                else
                    readRegularFace(parser);
   
            }
            else if(parser.isCurrentIsolated("o"))
            {
                if(info.splitPolicy() == OBJECT)
                {                   
                    String nameGroup = parser.readLineString().replace("o", "").trim();
                    MaterialT material = new MaterialT(nameGroup);

                    //id material
                    idMaterials.add(material);
                    idCount++;               
                }
            }
            else if(parser.isCurrentIsolated("g"))
            {                
                if(info.splitPolicy() == GROUP)
                {
                    String nameGroup = parser.readLineString().replace("g", "").trim();
                    MaterialT material = new MaterialT(nameGroup);
                    //id material
                    idMaterials.add(material);
                    idCount++;            
                }
            }
            
            parser.goToNextDefinedLine();
        }
        
        if(info.splitPolicy() == NONE)
            idMaterials.add(new MaterialT("default"));
        mesh.setMaterialList(idMaterials);
        parser.close();
    }
        
    private int getMaterialCount()
    {
        if(idCount<0) return 0;
        else return idCount;
    }
    
    public void readRegularFace(LineMappedReader reader)
    {
        
        reader.goToStartDigit();                   //get to first digit 
        int[] array             = reader.readLineIntArray(); 
        int n                   = array.length;
        
        for(int i = 1; i<= n - 2; i++) 
            modifyAdd(FACE, array[0], array[i], array[i+1]);   
        
    }
    
    public void readSingleSlashFace(LineMappedReader reader)
    { 
        reader.goToStartDigit();                   //get to first digit 
        int ncoordtypes         = reader.readIntArrayUntilSpace().length; 
        int[] array             = reader.readLineIntArray(); 
        int n                   = array.length;
        
        switch (ncoordtypes) {
            case 2:
                for(int i = 1; i<= n - 5; i+=2) 
                    modifyAdd(FACE_UV,  array[0], array[i+1], array[i+3],
                                        array[1], array[i+2], array[i+4]);         
                break;            
            case 3:
                for(int i = 1; i<= n - 8; i+=3) 
                    modifyAdd(FACE_UV_NORMAL,   array[0], array[i+2], array[i+5],
                                                array[1], array[i+3], array[i+6],
                                                array[2], array[i+4], array[i+7]);            
                break;
            default:
                break;
        }
    }
    
    public void readDoubleSlashFace(LineMappedReader reader)
    {
        reader.goToStartDigit();                   //get to first digit 
        int ncoordtypes         = reader.readIntArrayUntilSpace().length;
        int[] array             = reader.readLineIntArray(); 
        int n                   = array.length;
        switch (ncoordtypes) {
            case 2:
                for(int i = 1; i<= n - 5; i+=2) 
                    modifyAdd(FACE_NORMAL,  array[0], array[i+1], array[i+3],
                                            array[1], array[i+2], array[i+4]);         
                break;           
            default:
                break;
        }        
    }
    
    //this modifies the read obj parser to friendly array read (also handles negative indices)
    private void modifyAdd(AbstractMesh.MeshType type, int... indices)
    {        
        
        if(indices[0] >= 0) //positive face indices
            for(int i = 0; i<indices.length; i++)
                indices[i] = indices[i]-1;
        else //negative face indices
        {            
            for(int i = 0; i<indices.length; i++)
            {    
                if(null != type)
                switch (type) {
                    case FACE:
                        switch (i) {
                            case 0:
                                indices[i] = (int) (indices[i] + mesh.pointSize());
                                break;
                            case 1:
                                indices[i] = (int) (indices[i] + mesh.pointSize());
                                break;
                            case 2:
                                indices[i] = (int) (indices[i] + mesh.pointSize());
                                break;
                            default:
                                break;
                        }   break;
                    case FACE_UV:
                        switch (i) {
                            case 0:
                                indices[i] = (int) (indices[i] + mesh.pointSize());
                                break;
                            case 1:
                                indices[i] = (int) (indices[i] + mesh.pointSize());
                                break;
                            case 2:
                                indices[i] = (int) (indices[i] + mesh.pointSize());
                                break;
                            case 3:
                                indices[i] = (int) (indices[i] + mesh.texCoordsSize());
                                break;
                            case 4:
                                indices[i] = (int) (indices[i] + mesh.texCoordsSize());
                                break;
                            case 5:
                                indices[i] = (int) (indices[i] + mesh.texCoordsSize());
                                break;
                            default:
                                break;
                        }   break;
                    case FACE_UV_NORMAL:
                        switch (i) {
                            case 0:
                                indices[i] = (int) (indices[i] + mesh.pointSize());
                                break;
                            case 1:
                                indices[i] = (int) (indices[i] + mesh.pointSize());
                                break;
                            case 2:
                                indices[i] = (int) (indices[i] + mesh.pointSize());
                                break;
                            case 3:
                                indices[i] = (int) (indices[i] + mesh.texCoordsSize());
                                break;
                            case 4:
                                indices[i] = (int) (indices[i] + mesh.texCoordsSize());
                                break;
                            case 5:
                                indices[i] = (int) (indices[i] + mesh.texCoordsSize());
                                break;
                            case 6:
                                indices[i] = (int) (indices[i] + mesh.normalSize());
                                break;
                            case 7:
                                indices[i] = (int) (indices[i] + mesh.normalSize());
                                break;
                            case 8:
                                indices[i] = (int) (indices[i] + mesh.normalSize());
                                break;
                            default:
                                break;
                        }   break;
                    case FACE_NORMAL:
                        switch (i) {
                            case 0:
                                indices[i] = (int) (indices[i] + mesh.pointSize());
                                break;
                            case 1:
                                indices[i] = (int) (indices[i] + mesh.pointSize());
                                break;
                            case 2:
                                indices[i] = (int) (indices[i] + mesh.pointSize());
                                break;
                            case 3:
                                indices[i] = (int) (indices[i] + mesh.normalSize());
                                break;
                            case 4:
                                indices[i] = (int) (indices[i] + mesh.normalSize());
                                break;
                            case 5:
                                indices[i] = (int) (indices[i] + mesh.normalSize());
                                break;
                            default:
                                break;
                        }   break;
                    default:
                        break;
                }               
            }
            
        }
        //System.out.println(Arrays.toString(indices));
        mesh.add(type, idCount, getMaterialCount(), indices);
    }   
    
    @Override
    public String toString()
    {
        return info.toString();
    }
}

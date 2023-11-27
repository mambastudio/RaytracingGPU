/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grid.parser;

import coordinate.generic.g2.AbstractMesh;
import coordinate.generic.g2.AbstractParser;
import coordinate.generic.io.StringMappedReader;
import coordinate.generic.io.StringReader;
import coordinate.parser.attribute.MaterialT;
import coordinate.parser.obj.OBJInfo;
import static coordinate.parser.obj.OBJInfo.SplitOBJPolicy.NONE;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author user
 */
public class OBJMappedParser2 implements AbstractParser {
    private AbstractMesh mesh;       
    private ArrayList<MaterialT> groupMaterials;
    
    private ArrayList<MaterialT> sceneMaterials;
    
    private int groupCount = -1;
    private int groupMaterialCount = -1;
    
    private boolean defaultMatPresent = true;
    
    private OBJInfo info;    
    private OBJInfo.SplitOBJPolicy splitPolicy = NONE;
    
    
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
        read(new StringReader(uri),data);
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
    
    private void read(StringReader parser, AbstractMesh data)
    {        
        this.mesh = data;   
        this.groupMaterials = new ArrayList<>();
        this.sceneMaterials = new ArrayList<>();
        
        while(parser.hasNext())
        {
            String peekToken = parser.peekNextToken();            
            switch (peekToken) {
                default:
                    parser.getNextToken();
                    break;
            }
        }
    }
}

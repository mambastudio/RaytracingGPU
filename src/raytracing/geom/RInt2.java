/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raytracing.geom;


import coordinate.generic.AbstractCoordinateInteger;
import java.util.Arrays;

/**
 *
 * @author user
 */
public class RInt2 implements AbstractCoordinateInteger{
    
    public int x, y;
    
    public RInt2()
    {
        x = y = 0;
    }
    
    public RInt2(int xy)
    {
        x = y = xy;
    }
    
    public RInt2(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public int get(char axis) {
        switch (axis) {
            case 'x':
                return x;
            case 'y':
                return y;
            default:
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.  
        }
    }

    @Override
    public void set(char axis, int value) {
        switch (axis) {
            case 'x':
                x = value;
                break;
            case 'y':
                y = value;
                break;
            default:
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public void set(int... values) {
        x = values[0];
        y = values[1];
    }

    @Override
    public void setIndex(int index, int value) {
        switch (index)
        {
            case 0:
                x = value;
                break;
            case 1:
                y = value;
                break;   
            default:
                throw new UnsupportedOperationException("Not supported yet.");
        }                
    }

    @Override
    public int getSize() {
        return 2;
    }

    @Override
    public int[] getArray() {
        return new int[]{x, y};
    }

    @Override
    public int getByteSize() {
        return 4;
    }
    
    
    @Override
    public String toString()
    {
        int[] array = getArray();
        return Arrays.toString(array);
    }
}

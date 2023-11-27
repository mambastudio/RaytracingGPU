/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.Arrays;
import raytracing.cl.RaytraceSource;
import wrapper.core.OpenCLConfiguration;

/**
 *
 * @author user
 */
public class TestCL {
    public static void main(String... args)
    {
        OpenCLConfiguration configuration = OpenCLConfiguration.getDefault(RaytraceSource.readFiles());
        String str = "1/1/1";
        System.out.println(Arrays.toString(str.split("[/\\s]+")));
        System.out.println(str.matches("-?\\d+(\\.\\d+)?"));
    }
}

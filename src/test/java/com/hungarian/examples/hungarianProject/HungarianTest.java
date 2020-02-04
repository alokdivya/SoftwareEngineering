package com.hungarian.examples.hungarianProject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hinguapps.graph.Hungarian;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class HungarianTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public HungarianTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( HungarianTest.class );
    }
    
	public void test() {
		double [][] matrix= {{6,10,8},{4,14,5},{5,12,2}};
		Hungarian alpha = new Hungarian();
		double result= alpha.alok(matrix);
		assertEquals(16.0,result);
	}
	
	public void test1() {
		double [][] matrix= {{7,12,81,20},{4,14,51,31},{52,12,29,18},{99,121,219,118}};
		Hungarian alpha = new Hungarian();
		double result= alpha.alok(matrix);
		assertEquals(162.0,result);
	}
	public void test2() {
		double [][] matrix= {{75,36,81},{43,14,51},{51,12,23}};
		Hungarian alpha = new Hungarian();
		double result= alpha.alok(matrix);
		assertEquals(102.0,result);
	}
	public void test3() {
		double [][] matrix= {{111,321,106,112,118},{212,542,114,134,151},{212,331,135,142,212},{255,311,135,152,212},{212,131,135,172,112}};
		Hungarian alpha = new Hungarian();
		double result= alpha.alok(matrix);
		assertEquals(670.0,result);
	}
    /**
     * Rigourous Test :-)
     */
//    public void testApp()
//    {
//        assertTrue( true );
//    }
}

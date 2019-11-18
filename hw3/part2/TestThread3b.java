/*
 * Author: Sharanya Sudhakar
 *
 * Calculating factorials from given input argument.
 *
 * This thread is a test written for large computations
 * that take CPU time to complete.
 *
 */
 
 import java.util.*;
 import java.lang.*;
 
 class TestThread3b extends Thread
 {
    public TestThread3b () 
	{
	//SysLib.cout("TestThread3b starts...\n");
    }
	
    public void run( ) 
	{
		byte[] block = new byte[512];
        for (int i = 0; i < 250; i++)
        {
	        //SysLib.cout(i+" ");
            SysLib.rawread(i, block);
        }
        SysLib.cout("TestThread3b done.\n");
        SysLib.exit();
    }
}

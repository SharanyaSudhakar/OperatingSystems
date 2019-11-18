/*
 * Author: Sharanya Sudhakar
 *
 * Calculating factorials from given input argument.
 *
 * This thread is a test written for large computations
 * that take CPU time to complete.
 *
 */
 
 class TestThread3a extends Thread
 {

	private long argNum;

    public TestThread3a ( String args[] ) {
	argNum = Integer.parseInt( args[0] );
	if(argNum<=0)
		SysLib.cout("invalid number");
    }
	
	private long findFactorial(long num)
	{
		if(num == 1)
			return 1;
		return num * findFactorial(num-1);
	}

    public void run( ) {

	long ans = findFactorial(argNum);
    double d = Math.tan(Math.atan(Math.tan(Math.atan(Math.tan(Math.atan(Math.tan(Math.atan(Double.longBitsToDouble(ans)))))))));
	SysLib.cout("TestThread3a done.\n");
	SysLib.exit( );
    }
}
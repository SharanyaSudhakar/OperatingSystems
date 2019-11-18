/*
 * Author: Sharanya Sudhakar
 *
 * Test3 custom written to run TestThread3a 
 * and TestThread3b as pairs
 * The number of pairs is determined at run time
 *
 */
 
import java.util.Date;
import java.util.Random;

public class MyTest3 extends Thread
{
	private int pairCount;
	private String name;
	
	//to calculate the time taken for execution
	private long submissionTime;
    private long responseTime;
    private long completionTime;
	
	//constructor
	public MyTest3(String args[])
	{
		pairCount = Integer.parseInt(args[0]);
		submissionTime = new Date( ).getTime( );
	}
	
	public void run()
	{
		responseTime = new Date( ).getTime( );
		
		String[] threeA = SysLib.stringToArgs("TestThread3a 58");
        String[] threeB = SysLib.stringToArgs("TestThread3b");
		
		//execute the threads as pairs
		for (int i = 0; i < pairCount; i++)
        {
            // Execute 3a and 3b
            SysLib.exec(threeA);
            SysLib.exec(threeB);
        }
		
		//wait for threads to finsh.
		int totalthreads = pairCount * 2;
		for (int i = 0; i < totalthreads; i++)
        {
            SysLib.join();
        }
		
		completionTime = new Date( ).getTime( );
		SysLib.cout( "ThreadTest3:" +
		     " response time = " + (responseTime - submissionTime) +
		     " turnaround time = " + (completionTime - submissionTime)+
		     " execution time = " + (completionTime - responseTime)+
		     "\n");
		SysLib.exit( );
	}
}

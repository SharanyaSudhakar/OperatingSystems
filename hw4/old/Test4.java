import java.util.Arrays;
import java.util.Random;
import java.lang.System;

public class Test4 extends Thread
{
	private static final int blockSize = 512;
	private byte[] readBlock, writeBlock;
	private int testNum;
	private boolean cacheEnabled;
	private Random rand;
	private long endTime, startTime, writeTime, readTime;
	
	public Test4(String args[])
	{
		writeBlock = new byte[blockSize];
        readBlock = new byte[blockSize];
		rand = new Random();
		//cache is enabled by default
		if(args[0].equals("disabled"))
			cacheEnabled = false;
		else
			cacheEnabled = true;
		testNum = Integer.parseInt(args[1]);
	}
	
	public void run()
	{
		SysLib.flush();
		switch(testNum)
		{
			case 1:
				randomAccess();
				break;
			case 2:
				localizedAccess();
				break;
			case 3:
				mixedAccess();
				break;
			case 4:
				adversaryAccess();
				break;
			case 0:
				randomAccess(); 
				SysLib.flush();
				localizedAccess(); 
				SysLib.flush();
				mixedAccess(); 
				SysLib.flush();
				adversaryAccess();
				break;
			default: 
				SysLib.cout("Invalid Test Number!\n ");
                break;
		}
		syncCache();
		SysLib.exit();
	}
	
	private boolean validateCache()
	{
		return Arrays.equals(readBlock,writeBlock);
	}
	
	private void writeToCache(int blockid, byte[] buff)
	{
		if(!cacheEnabled)
			SysLib.rawwrite(blockid, buff);
		SysLib.cwrite(blockid, buff);
	}
	
	private void syncCache()
	{
		if(!cacheEnabled)
			SysLib.sync();
		SysLib.csync();
	}
	
	private void readFromCache(int blockid, byte[] buff)
	{
		if(!cacheEnabled)
			SysLib.rawread(blockid, buff);
		SysLib.cread(blockid, buff);
	}
		
	private void printResults(long write, long read, 
							String testname, int count)
	{
		String iscache = cacheEnabled ? "Enabled" : "Disabled";
        SysLib.cout("\n"+ testname + "..."+count+" With Caching " + iscache 
					+ "\n");
        SysLib.cout("WriteTime: " + write + " Readtime: " 
					+ read +"\n" );
		SysLib.cout("Avg. WriteTime: " + write/count + " Avg. Readtime: " 
					+ read/count +"\n" );
    
	}
	
	private void RWAccess(int accessCount, int blockID_range, String testname)
	{
		//random input to write in cache.
		//will help validate the read.
		writeBlock[rand.nextInt(blockSize-1)] = 
							(byte)rand.nextInt(125);
		int testIndex = rand.nextInt(blockID_range);
		startTime = System.currentTimeMillis();
		for(int i=0; i< accessCount; i++)
			writeToCache(rand.nextInt(blockID_range),writeBlock);
		writeToCache(testIndex,writeBlock);
		endTime = System.currentTimeMillis();
		writeTime = endTime - startTime;
		
		startTime = System.currentTimeMillis();
		for(int i=0; i< accessCount; i++)
			readFromCache(rand.nextInt(blockID_range),readBlock);
		readFromCache(testIndex,readBlock);
		endTime = System.currentTimeMillis();
		readTime = endTime - startTime;
		
		if(!validateCache())
			SysLib.cout("error: caches dont match in " +
						testname +"\n");
		
		printResults(writeTime, readTime, testname, accessCount);
	}
	/* read and write many blocks randomly across the disk. 
	 * Verify the correctness of your disk cache.
	 */
	public void randomAccess()
	{
		int randomAccess_size = rand.nextInt(200)+100;
		RWAccess(randomAccess_size, 500, "Random Access");
	}
	/*
	 * read and write a small selection of blocks many 
	 * times to get a high ratio of cache hits.
	 */
	public void localizedAccess()
	{
		int randomAccess_size = rand.nextInt(200)+100;
		RWAccess(randomAccess_size, 10, "Localized Access");
	}
	
	/*
	 * 90% of the total disk operations should be localized 
	 * accesses and 10% should be random accesses.
	 */
	public void mixedAccess()
	{
		int randomAccess_size = rand.nextInt(300)+100;
		int ninety = (int)(randomAccess_size * 0.9);
		int ten = randomAccess_size-ninety;
		SysLib.cout ("\nMixed Access");
		RWAccess(ninety, 50, "Localized Access: 90%");
		RWAccess(ten, 1000, "Random Access:10%");
		SysLib.cout ("\n");
	}
	
	/*
	 * generate disk accesses that do not make good use 
	 * of the disk cache at all.
	 */
	public void adversaryAccess()
	{
		int randomAccess_size = rand.nextInt(200)+100;
		RWAccess(randomAccess_size, 1000, "Adversary Access");
	}
	
}
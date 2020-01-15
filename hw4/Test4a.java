import java.util.Arrays;
import java.util.Random;

public class Test4a extends Thread
{
	private static final int blockSize = 512,arraySize = 5;
	private byte[] readBlock, writeBlock;
	Random rand;
	
	public Test4a()
	{
		writeBlock = new byte[blockSize];
        readBlock = new byte[blockSize];
		rand = new Random();
	}
	public void run()
	{
		SysLib.cread(0, readBlock);
		SysLib.cwrite(1, writeBlock);
		SysLib.cread(3, readBlock);
		SysLib.cread(6, readBlock);
		SysLib.cwrite(2, writeBlock);
		SysLib.cread(4, readBlock);
		SysLib.cread(5, readBlock);
		SysLib.cwrite(2, writeBlock);
		SysLib.cread(5, readBlock);
		SysLib.cread(0, readBlock);
		SysLib.cwrite(3, writeBlock);
		SysLib.cread(1, readBlock);
		SysLib.cread(2, readBlock);
		SysLib.cwrite(5, writeBlock);
		SysLib.cread(4, readBlock);
		SysLib.cread(1, readBlock);
		SysLib.cwrite(0, writeBlock);
		//SysLib.cwrite(3, writeBlock);
		SysLib.exit();
	}
}
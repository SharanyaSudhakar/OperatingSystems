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
		writeBlock[0] = (byte)rand.nextInt(125);
		SysLib.cwrite(1, writeBlock);
		writeBlock[0] = (byte)rand.nextInt(125);
		SysLib.cwrite(4, writeBlock);
		SysLib.cwrite(7, writeBlock);
		writeBlock[0] = (byte)rand.nextInt(125);
		SysLib.cwrite(2, writeBlock);
		SysLib.cwrite(9, writeBlock);
		writeBlock[0] = (byte)rand.nextInt(125);
		SysLib.cwrite(0, writeBlock);
		SysLib.cread(4, readBlock);
		SysLib.cread(6, readBlock);
		SysLib.cread(2, readBlock);
		SysLib.cread(8, readBlock);
		SysLib.cread(0, readBlock);
		System.out.println(writeBlock[0]);
		System.out.println(readBlock[0]);
		System.out.println(writeBlock[0]);
		String o = Arrays.equals(readBlock,writeBlock)?"true":"false";
		SysLib.cout (o+"\n\n");
		writeBlock[3] = (byte)rand.nextInt(125);
		SysLib.rawwrite(0, writeBlock);
		SysLib.rawread(0, readBlock);
		System.out.println(readBlock[3]);
		System.out.println(writeBlock[3]);
		o = Arrays.equals(readBlock,writeBlock)?"true":"false";
		SysLib.cout (o);
		SysLib.exit();
	}
}
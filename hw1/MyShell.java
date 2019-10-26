/*
 * Author: Sharanya Sudhakar
 * Simulation shell using Java.
 * The shell processes threads 
 * parallelly and sequentially
 *
 * Project Part 2: CSS 430
 */
 
import java.io.*;
import java.util.*;

class MyShell extends Thread
{
	//static string to color code shell execution
	public static final String RESET = "\u001B[0m";
	public static final String GREEN = "\u001B[32m";
	public static final String CYAN = "\u001B[36m";
	public static final String YELLOW = "\u001B[33m";

   // constructor for shell
   public MyShell( ) 
   {
		SysLib.cout(GREEN + "****** Custom Shell Starting ******\n" + RESET);
   }//end MyShell
   
   /*
	* Method to check for 'exit' condition
	* @arguments: input string from SysLib.cin
	* @return: return true for exit and false otherwise
    */
   public boolean isExit(String input)
   {
		String cmds[] = SysLib.stringToArgs(input);
		if(cmds.length == 1)
		   if(cmds[0].equals("exit"))
			   return true;
		   
		return false;
   }//end isExit

   /*
    * required run method for this Shell Thread
	* executes the basic shell that executes an 
	* arbitrary number of commands with ; and & 
	* as delimiters
	*/
   public void run( ) 
   {
		int shellNum=1;
		while(true)
		{
			SysLib.cout(CYAN + "\nShell[" + (shellNum++) + "]% " + RESET);
			
			StringBuffer input = new StringBuffer();
			SysLib.cin(input);
			String inputStr = input.toString();
			
			if(isExit(inputStr))
			  break;
			
			String[] seqCmds = inputStr.split(";");
			processSeq(seqCmds);
		}
		SysLib.cout(GREEN+"****** MyShell Execution Stopped ******\n"+RESET);
		SysLib.exit();
   }//end run
   
   /*
    * This method processes the commands sequentially
	* this method also identifies parallel processing 
	* commands and branches off to processPll. 
	* 
	* @ arguments String array of the commands to 
	* be processed.
    */
   public void processSeq(String[] seqCmds)
   {
	   for(int i=0; i< seqCmds.length; i++)
	   {
			if(seqCmds[i].contains("&"))
			{
				String[] pllCmds = seqCmds[i].split("&");
				processPll(pllCmds);
				continue;
			}
			String args[] = SysLib.stringToArgs(seqCmds[i]);
			SysLib.cout(YELLOW + "\nProcessing command: " + RESET + seqCmds[i] + "\n");
			int tid = SysLib.exec( args );
			if(tid>0)
			{
				SysLib.cout("Started Thread tid = " + tid + "\n\n");
				SysLib.join();
			}
	   }
   }//end processSeq
   
   /*
    * Method to parallel process the commands. 
	* @ arguments String array of commands
    */
   public void processPll(String[] pllCmds)
   {
	   int totalprocesses = 0;
	   for(int i=0; i< pllCmds.length; i++)
	   {
			String args[] = SysLib.stringToArgs(pllCmds[i]);
			SysLib.cout(YELLOW + "\nProcessing command: " + RESET + pllCmds[i] + "\n");
			int tid = SysLib.exec( args );
			if(tid>0) 
			{
				totalprocesses++;
				SysLib.cout("Started Thread tid = " + tid + "\n\n");
			}
	   }
	   
	   while(totalprocesses!=0)
	   {
		   SysLib.join();
		   totalprocesses--;
	   }
   }// end processPll
}//end class
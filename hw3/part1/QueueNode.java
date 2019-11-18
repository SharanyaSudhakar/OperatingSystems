/*
 * Author: Sharanya Sudhakar
 *
 * QueueNode for Sync Queue
 */
 
 import java.util.Vector;
 public class QueueNode
 {
	 private Vector<Integer> queue;
	 
	 //default constructor to hold queue with same condition
	 public QueueNode()
	 {
		 queue = new Vector<>();
	 }
	 
	 /* sleep thread with 
	  *thread id as return value
	  */
	  public synchronized int sleep()
	  {
		  if(queue.size() == 0)
		  {
			  try
			  {
				  wait();
			  }
			  catch (InterruptException e)
			  {
				  SysLib.cout("Error in sleep method");
			  }
			  return queue.remove(0);
		  }
		  return -1;
	  }
	  
	  /*
	   *Wake method with thread id.
	   */
	   public synchronized void wake(int tid)
	   {
		   queue.add(tid);
		   notify();
	   }
 }
/*
 * Author: Sharanya Sudhakar
 *
 * Sync Queue for ThreadOS
 */
 
 public class SyncQueue
 {
	 private QueueNode[] queue;
	 public static int DEFAULT_COND_LEN = 10;
	 
	 /*
	  *create a queue and allow threads to wait for a 
	  *default condition number (=10) or a condMax number 
	  *of condition/event types.
	  */
	 public SyncQueue()
	 {
		 queue = new QueueNode[DEFAULT_COND_LEN];
		 initQueueNode();
	 }
	 
	 public SyncQueue(int condMax)
	 {
		 queue = new QueueNode[condMax];
		 initQueueNode();
	 }
	 
	 public int enqueueAndSleep(int condition)
	 {
		 return queue[condition].sleep();
	 }
	 
	 public void dequeueAndWakeup(int condition)
	 {
		 queue[condition].wake(0);
	 }
	 
	 public void dequeueAndWakeup(int condition, int tid)
	 {
		 queue[condition].wake(tid);
	 }
	 
	 //private methods
	 private void initQueueNode()
	 {
		 int len = queue.length;
		 for(int i=0; i<len; i++)
		 {
			 queue[i] = new QueueNode();
		 }
	 }
 }
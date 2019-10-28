/*
 * author: Sharanya Sudhakar
 * Multi Level Feedback Queue
 *
 */
 
import java.util.*;
/*
 *Enum to manage multilevel queue.
 */
enum Queue {ZERO, ONE, TWO;}

public class Scheduler extends Thread
{
	//TCB - Thread Control BlockView

    private Vector queue_0, queue_1, queue_2;//list of active threads one for each level.
    private int timeSlice; // time slice allocated to each thread
    private static final int DEFAULT_TIME_SLICE = 1000; // 1 sec default timeslice

    // New data added to p161 
    private boolean[] tids; // Indicate which ids have been used
    private static final int DEFAULT_MAX_THREADS = 10000;

    // A new feature added to p161 
    // Allocate an ID array, each element indicating if that id has been used
    private int nextId = 0;

	 public Scheduler( ) 
	{
		timeSlice = DEFAULT_TIME_SLICE;
		queue_0 = new Vector( );
		queue_1 = new Vector( );
		queue_2 = new Vector( );
		initTid( DEFAULT_MAX_THREADS );
    }

    public Scheduler( int quantum ) 
	{
		timeSlice = quantum;
		queue_0 = new Vector( );
		queue_1 = new Vector( );
		queue_2 = new Vector( );
		initTid( DEFAULT_MAX_THREADS );
    }
	
    private void initTid( int maxThreads ) 
	{
		tids = new boolean[maxThreads];
		for ( int i = 0; i < maxThreads; i++ )
			tids[i] = false;
    }

    // A new feature added to p161 
    // Search an available thread ID and provide a new thread with this ID
    private int getNewTid( ) 
	{
		for ( int i = 0; i < tids.length; i++ ) 
		{
			int tentative = ( nextId + i ) % tids.length;
			if ( tids[tentative] == false ) 
			{
			tids[tentative] = true;
			nextId = ( tentative + 1 ) % tids.length;
			return tentative;
			}
		}
		return -1;
    }

    // A new feature added to p161 
    // Return the thread ID and set the corresponding tids element to be unused
    private boolean returnTid( int tid ) 
	{
		if ( tid >= 0 && tid < tids.length && tids[tid] == true ) 
		{
			tids[tid] = false;
			return true;
		}
		return false;
    }

    // A new feature added to p161 
    // Retrieve the current thread's TCB from the queue
    public TCB getMyTcb() 
	{
		TCB tcb = findTcb(queue_0);
		if(tcb==null)
		{
			tcb = findTcb(queue_1);
			if(tcb == null)
				tcb = findTcb(queue_2);
		}
		return tcb;
    }

	// helper function for getMyTcb
	private TCB findTcb(Vector queue)
	{
		Thread myThread = Thread.currentThread( ); // Get my thread object
		synchronized( queue ) 
		{
			for ( int i = 0; i < queue.size( ); i++ ) 
			{
				TCB tcb = ( TCB )queue.elementAt( i );
				Thread thread = tcb.getThread( );
				if ( thread == myThread ) // if this is my TCB, return it
					return tcb;
			}
		}
		return null;
	}

    // A new feature added to p161 
    // Return the maximal number of threads to be spawned in the system
    public int getMaxThreads( ) 
	{
		return tids.length;
    }

    // A new feature added to p161 
    // A constructor to receive the max number of threads to be spawned
    public Scheduler( int quantum, int maxThreads ) 
	{
		timeSlice = quantum;
		queue_0 = new Vector( );
		queue_1 = new Vector( );
		queue_2 = new Vector( );
		initTid( maxThreads );
    }

    private void schedulerSleep( ) 
	{
		try 
		{
			Thread.sleep( timeSlice );
		} 
		catch ( InterruptedException e ) {}
    }

    // A modified addThread of p161 example
    public TCB addThread( Thread t ) 
	{
		TCB parentTcb = getMyTcb(); // get my TCB and find my TID
		int pid = ( parentTcb != null ) ? parentTcb.getTid( ) : -1;
		int tid = getNewTid( ); // get a new TID
		if ( tid == -1)
			return null;
		TCB tcb = new TCB( t, tid, pid ); // create a new TCB
		queue_0.add( tcb );// always add to first queue.
		return tcb;
    }

    // A new feature added to p161
    // Removing the TCB of a terminating thread
    public boolean deleteThread( ) 
	{
		TCB tcb = getMyTcb( ); 
		if ( tcb!= null )
			return tcb.setTerminated( );
		else
			return false;
    }

    public void sleepThread( int milliseconds ) 
	{
		try 
		{
			sleep( milliseconds );
		} 
		catch ( InterruptedException e ) { }
    }

	//thread process of the queues
	private void processQueue(Vector queue,Queue num)
	{
		Thread current = null;
		
		// get the next TCB and its thread
		//check for any active threads
		//if active threads are empty continue
		if ( queue.size( ) == 0 )
			return;
		while (queue.size() > 0)
        {
			TCB currentTCB = (TCB)queue.firstElement( ); // at index 0
			if ( currentTCB.getTerminated( ) == true ) //if thread is terminated remove from queue and continue
			{
				queue.remove( currentTCB );
				returnTid( currentTCB.getTid( ) );
				continue;
			}
			current = currentTCB.getThread( );//from TCB block get thread
			if ( current != null ) 
			{
				current.resume();
				if (! current.isAlive( ) )
					current.start( ); 
			}
		
			manageML(current,num);

			synchronized ( queue ) 
			{
				if ( current != null && current.isAlive( ) )
				current.suspend();
				queue.remove( currentTCB ); // rotate this TCB to the end
				switch(num)
				{
					case ZERO:
						queue_1.add( currentTCB );
					break;
					case ONE:
					case TWO:
						queue_2.add( currentTCB );
				}
			}
		}
	}

	//helper function for processQueue, 
	//it manages new threads in queue 0or1 when another queue is being processed.
	private void manageML(Thread current, Queue num)
	{
		switch(num)
		{
			case ZERO:
				sleepThread(timeSlice/2);
			break;

			case ONE:
			case TWO:
				sleepThread(timeSlice / 2);
				if (queue_0.size() != 0)
				{
					// new threads in first queue
					current.suspend();
					processQueue(queue_0,Queue.ZERO);
					current.resume();
				}
				sleepThread(timeSlice / 2);

				if(num == Queue.TWO ) //only case two
				{
					if (queue_1.size() != 0)
					{
						// new threads in second queue
						current.suspend();
						processQueue(queue_1,Queue.ONE);
						current.resume();
					}
					sleepThread(timeSlice);
				}
			break;
		}
	}
    
    // A modified run of p161
    public void run( ) 
	{
		System.out.println("\n********** Sharanya's Scheduler 2 - MFQS *************\n");
		while ( true ) 
		{
			try 
			{
				processQueue(queue_0,Queue.ZERO);
				processQueue(queue_1,Queue.ONE);
				processQueue(queue_2,Queue.TWO);
			} 
			catch ( NullPointerException e3 ) { };
		}
    }
}

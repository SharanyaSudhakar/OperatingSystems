/*
 * Author: Sharanya Sudhakar
 *
 * Disk Caching based on enhanced second chance algorithm
 * 4 possible cases: 
 *
 * (0, 0) neither recently used nor modified—best page to 
 * replace
 * (0, 1) not recently used but modified—not quite as good,
 * because the page will need to be written out before 
 * replacement
 * (1, 0) recently used but clean—probably will be used 
 * again soon
 * (1, 1) recently used and modified—probably will 
 * be used again soon, and the page will be need to be 
 * written out to disk before it can be replaced
 */

 public class Cache
 {
	 final int INVALID = -1;
	 private int blockSize;
	 private int cacheSize;
	 private int victimIndex;
	 private CacheBlock[] myCache;
	 
	 /*
	  * Each cache block is defined here.
	  *
	  * The block frame is set to -1 in case of invalid block
	  * information
	  * The refBit is true when the block is accessed ans set to false
	  * when the algortihm is serching for the next victim
	  * The dirtyBit is set to true when block is written
	  * and to false when it is written to disk.
	  *
	  */ 
	 private class CacheBlock
	 {
		byte[] data;
		int blockFrame;
		boolean refBit;
		boolean dirtyBit;

		private CacheBlock(int size){
			data = new byte[size];		
			blockFrame = INVALID;						
			refBit = false;					
			dirtyBit = false;						
		}
	 }
	 /*
	 The constructor: allocates a cacheBlocks number of
	 cache blocks, each containing blockSize-byte data, on 
	 memory
	  */
	 Cache(int blocksize, int cachesize)
	 {
		 blockSize = blocksize;
		 cacheSize = cachesize;
		 myCache = new CacheBlock[cacheSize];
		 for(int i=0; i< cacheSize; i++)
		 {
			 myCache[i] = new CacheBlock(blockSize);
		 }
		 victimIndex = INVALID;
		 //outputCache();
	 }
	 
	 /*
	  * prints the contents of the cache.
	  * primarily for error checking
	  */
	 private void outputCache()
	 {
		 for(CacheBlock c: myCache)
			SysLib.cout (c.data.length + " " +
						c.blockFrame + " " +
						c.refBit + " " +
						c.dirtyBit + "\n");
		 SysLib.cout(victimIndex + "\n");	
	 }
	 
	 /*
	  * @parem blockID
	  * given the bockid return the index value in 
	  * matching cache, else return -1
	  * @return index or -1
	  */
	 private int getBlockIndex(int blockID)
	 {
		 for (int i=0; i< cacheSize;i ++)
			 if(myCache[i].blockFrame == blockID)
				 return i;
		 return INVALID;
	 }
	 
	 /*
	  * @parem index and buffer
	  * read cache at index i to buffer
	  */
	 private void readCache(int i, byte[] buff)
	 {
		 System.arraycopy(myCache[i].data,0, buff,0,blockSize);
	 }
	 
	 /*
	  * @parem i(index) and buffer
	  * from buff write to cache at index i
	  */
	 private void writeCache(int i, byte[] buff)
	 {
		 System.arraycopy(buff,0,myCache[i].data ,0,blockSize);
	 }
	 
	 /*
	  * @parem i(index), ref(boolean), dirty(boolean)
	  * set the ref and dirty bits of the cache at index i
	  */
	 private void updateCacheBits(int i, boolean ref, boolean dirty)
	 {
		 myCache[i].refBit = ref;
		 myCache[i].dirtyBit = dirty;
	 }
	 
	 /*
	  * @parem i(index), ref(boolean), dirty(boolean), 
	  * id(blockid)
	  * set the ref and dirty bits of the cache at index i
	  * and set the block frame at the same index to blockid.
	  */
	 private void updateCacheBits(int index,int id, boolean ref, 
								  boolean dirty)
	 {
		 updateCacheBits(index, ref, dirty);
		 myCache[index].blockFrame = id;
	 }
	 
	 /*
	  * find victim function that implements the 
	  * enhanced second chance algorithm
	  * if current victims ref bit is false, no changes made.
	  * otherwise, the next index is probed for a false ref bit.
	  * if it is not set to false, the index is given a second chance
	  * and its ref bit is set to false and the index moves 
	  * forward in its search, until a ref bit with false 
	  * is encountered.
	  */
	 private void findVictim()
	 {
		 while(true)
		 {						
			victimIndex = ((++victimIndex) % cacheSize);
			if(!myCache[victimIndex].refBit)
				return; 
			myCache[victimIndex].refBit = false; 	
		 } 
	 }
	 
	 private void diskWrite( int i)
	 {
		 if(myCache[i].dirtyBit && 
		 myCache[i].blockFrame != INVALID)
		 {
			SysLib.rawwrite(myCache[i].blockFrame, 
							myCache[victimIndex].data);
			myCache[i].dirtyBit = false;
		 }
	 }
	 
	 /*
	  * reads into the buffer[ ] array the cache block 
	  * specified by blockId from the disk cache if it is 
	  * in cache, otherwise reads the corresponding disk block 
	  * from the disk device. Upon an error, it should return 
	  * false, otherwise return true.
	  */
	 public synchronized boolean read(int blockID, byte buff[])
	 {
		 int cacheIndex = getBlockIndex(blockID);
		 if(cacheIndex != INVALID)
		 {
			 readCache(cacheIndex, buff);
			 updateCacheBits(cacheIndex,true,false);
			 //outputCache();
			 return true;
		 }
		 cacheIndex = getBlockIndex(INVALID);
		 if(cacheIndex == INVALID)
		 {
		 findVictim();
		 diskWrite(victimIndex);
		 cacheIndex = victimIndex;
		 }
		 SysLib.rawread(blockID, myCache[cacheIndex].data);
         readCache(cacheIndex, buff);
		 updateCacheBits(cacheIndex,blockID,true,false);
		 //outputCache();
		 return true;
	 }
	 
	 /*
	  * writes the buffer[ ]array contents to the cache block 
	  * specified by blockId from the disk cache if it is in 
	  * cache, otherwise finds a free cache block and writes 
	  * the buffer [ ] contents on it. No write through. 
	  * Upon an error, it should return false, otherwise 
	  * return true.
	  */
	 public synchronized boolean write(int blockID, byte[] buff)
	 {
		 int cacheIndex = getBlockIndex(blockID);
		 if(cacheIndex != INVALID)
		 {
			 writeCache(cacheIndex, buff);
			 updateCacheBits(cacheIndex,true,true);
			 //outputCache();
			 return true;
		 }
		 cacheIndex = getBlockIndex(INVALID);
		 if(cacheIndex == INVALID)
		 {
			 findVictim();
			 diskWrite(victimIndex);
			 cacheIndex = victimIndex;
		 }
		 SysLib.rawread(blockID, myCache[cacheIndex].data);
         writeCache(cacheIndex, buff);
		 updateCacheBits(cacheIndex,blockID,true,true);
		 //outputCache();
		 return true;
	 }
	 
	 /*
	  * writes back all dirty blocks to the DISK file. 
	  * The sync( ) method maintains valid clean copies 
	  * of the block in Cache.java
	  *
	  *  Call sync() when shutting down ThreadOS.
	  */
	 public synchronized void sync()
	 {
		for(int i = 0; i < cacheSize; i++) 
			diskWrite(i); 
		SysLib.sync();							
		//outputCache();
	 }
	 
	 /*
	  * flush( ) method invalidates all cached blocks
	  * Call flush between the running of performance 
	  * tests so each test starts with an empty cache.
	  */
	 public synchronized void flush()
	 {
		for(int i = 0; i < cacheSize; i++)
		{			
			diskWrite(i);							
			updateCacheBits(i,INVALID,false,false);			
		}	
		SysLib.sync();				
		//outputCache();
	 }
 }
package threads;

public class thread {
    private int _activeThreads = 0;
  
    private boolean _started = false;
    
    synchronized public void waitDone()
    {
     try {
      while ( _activeThreads>0 ) {
       wait();
      }
     } catch ( InterruptedException e ) {
     }
    }

    synchronized public void waitBegin()
    {
     try {
      while ( !_started ) {
       wait();
      }
     } catch ( InterruptedException e ) {
     }
    }
     
    
    synchronized public void workerBegin()
    {
     _activeThreads++;
     _started = true;
     notify();
    }
     
    synchronized public void workerEnd()
    {
     _activeThreads--;
     notify();
    }
     
    synchronized public void reset()
    {
     _activeThreads = 0;
    }
     
}


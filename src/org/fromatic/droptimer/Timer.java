package org.fromatic.droptimer;

public class Timer {
	
	private long startTime = 0;
    private long stopTime = 0;
    private long elapsed = 0;
    private boolean running = false;

   
    public void start() {
    	if (running)
    		return;
    	
        this.startTime = System.nanoTime();
        this.running = true;
    }

   
    public void stop() {
    	if (!running)
    		return;
    	
    	this.stopTime = System.nanoTime();
        this.running = false;
    }

    public void reset() {
        this.startTime = 0;
        this.stopTime = 0;
        this.running = false;
    }    
     
    public long getElapsedTime() {
        if (running) {
            elapsed = ((System.nanoTime() - startTime) / 1000000);
        }
        else {
            elapsed = ((stopTime - startTime) / 1000000);
        }
        return elapsed;
    }
    
    public boolean isRunning() {
    	return running;
    }
}

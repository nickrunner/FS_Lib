package dev.firstseed.sports_reference;


import java.util.ArrayList;

public class ThreadHelper
{
    private ArrayList<Thread> activeThreads = new ArrayList<>();
    private int maxConcurrentThreads;

    public ThreadHelper(int maxConcurrentThreads){
        this.maxConcurrentThreads = maxConcurrentThreads;
    }

    public void run(ArrayList<Thread> threadList)
    {
        if(threadList.isEmpty()){
            return;
        }
        int maxThreads = maxConcurrentThreads;
        if(threadList.size() <= maxConcurrentThreads){
            maxThreads = threadList.size();
        }
        for(Thread t : threadList){
            run(t);
        }

        for(Thread t : activeThreads){
            try{
                t.join();
            }catch (Exception e){
                System.out.println("THREAD Interrupted "+e);
            }
        }
    }

    private boolean threadIsRunning(Thread thread){
        if(thread == null){
            return false;
        }
        else{
            return thread.isAlive();
        }
    }


    public void run(Thread t){
        if(activeThreads.size() < maxConcurrentThreads){
            activeThreads.add(t);
            t.start();
        }
        else{
            //Wait for a thread to finish
            int i=0;
            if(!activeThreads.isEmpty()) {
                try {
                    while (activeThreads.size() >= maxConcurrentThreads) {
                        if (i >= activeThreads.size()) {
                            i = 0;
                        }
                        if (activeThreads.get(i) != null) {
                            if (!activeThreads.get(i).isAlive()) {
                                activeThreads.remove(i);
                            }
                        } else {
                            activeThreads.remove(i);
                        }
                        i++;

                    }
                }
                catch(Exception e){
                    System.out.println("TH Error waiting for threads. Size="+activeThreads.size()+" i="+i);
                }
                run(t);
            }
        }
    }

    public void setMaxConcurrentThreads(int maxConcurrentThreads){
        this.maxConcurrentThreads = maxConcurrentThreads;
    }
}

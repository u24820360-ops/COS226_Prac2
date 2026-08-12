
public class BakeryLock implements Lock 
{

    private final int n;                            // The number of competing threads
    private final VolatileBoolean[] flag;           // Indicates if a thread is interested in acquiring the lock
    private final VolatileInt[] label;              // The "ticket number" of the thread. Threads with lower tickets get served first

    public BakeryLock(int n) 
    {
        this.n=n;
        this.flag=new VolatileBoolean[n];
        this.label=new VolatileInt[n];

        //set flags to false and labels to 0
        for(int i=0; i < this.n; i++) {
            this.flag[i]=new VolatileBoolean(false);
            this.label[i]=new VolatileInt(0);
        }
    }

    @Override
    public void lock(int threadId) 
    {
        this.flag[threadId].value = true; // i am interested in acquiring lock

        //find max value in the array and add 1 for new threads
        int MAXIMUM=0;
        for(int i = 0; i < this.n; i++) {
            if(MAXIMUM < this.label[i].value) {
                MAXIMUM=this.label[i].value;
            }
        }

        //set max + 1 as the thread ids number
        ++MAXIMUM;
        this.label[threadId].value=MAXIMUM;

        // wait if there exist other threads interested and there label is lower
        while(this.otherThreadsInterested(threadId) && this.otherThreadsPrecedeMe(threadId)) {} //wait yeah
    }

    @Override
    public void unlock(int threadId) 
    {
        this.label[threadId].value=0;
        this.flag[threadId].value=false;
    }

    //HELPER FUNCTIONS
    private boolean otherThreadsInterested(int threadId) {
        for(int i=0; i < this.n; i++ ) {
            if(i != threadId && this.flag[i].value==true) {
                return true;
            }
        }
        return false;
    }

    private boolean otherThreadsPrecedeMe(int threadId) {
        int myLabel=this.label[threadId].value;

        for(int i = 0; i < this.n; i++) {
            // We must ONLY consider threads that are currently interested
            if(i != threadId && this.flag[i].value == true) {
                // Another thread precedes me if it has a lower label, OR the same label but a lower thread ID
                if (this.label[i].value < myLabel || (this.label[i].value == myLabel && i < threadId)) {
                    return true;
                }
            }
        }

        return false;
    }
}
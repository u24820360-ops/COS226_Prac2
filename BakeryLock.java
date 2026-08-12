
public class BakeryLock implements Lock 
{

    private final int n;
    private final VolatileBoolean[] flag;
    private final VolatileInt[] label;

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
        this.flag[threadId]=new VolatileBoolean(true); // i am interested in acquiring lock

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
            if(i != threadId && this.label[i].value < myLabel) {
                return true;
            }
        }

        return false;
    }
}
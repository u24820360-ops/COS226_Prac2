public class FilterLock implements Lock 
{

    private final int n;
    private final VolatileInt[] level;
    private final VolatileInt[] victim;

    public FilterLock(int n) 
    {
        this.n = n;                                     //the number of competing threads
        this.level = new VolatileInt[n];                //the levels where the other threads wait in before progressing closer to the critical section
        this.victim = new VolatileInt[n];               //one thread is the victim at each level and other threads can pass through if there is a victim
        for (int i = 0; i < n; i++) {                   //set levels and victim to 0.
            this.level[i] = new VolatileInt(0);
            this.victim[i] = new VolatileInt(0);
        }
    }

    @Override
    public void lock(int threadId) 
    {
        for (int L = 1; L < this.n - 1; L++) {          //n threads require n - 1 levels because one thread must enter the critical section.
            this.level[threadId].value = L;             //thread with ID, threadId enters level L
            this.victim[L].value = threadId;            //thread with ID, threadId announces itself as the victim
            while (ahead(threadId) && this.victim[L].value == threadId) {
                //then wait while another thread is at the same or higher level AND I'm the victim.
            }
        }
    }

    //HELPER FUNCTION
    public boolean ahead(int threadId) {
        int temp = this.level[threadId].value;
        for (int i = 0; i < this.n; i++) {
            if (this.level[i].value >= temp && i != threadId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void unlock(int threadId) 
    {
        this.level[threadId].value = 0;                 //the thread no longer wants to lock so it goes back to level 0 which is a non-competing level
    }
}
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

//TUTORIAL - CHAPTER 28 - http://ocpj8.javastudyguide.com/ch28.html
// also see: https://www.adictosaltrabajo.com/2015/08/21/el-paralelismo-en-java-y-el-framework-forkjoin/

//seudocode parrallel (in compute method)

//Result solve (Problem problem) {
//    if problem.size < SEQUENTIAL_THRESHOLD
//        return solveSequentially(problem);
//    else {
//        Result left, right;
//        INVOKE-IN-PARALLEL {
//            left = solve(extractLeftHalf(problem));
//            right = solve(extractRightHalf(problem));
//        }
//        return combine(left, right);
//    }
//}


/*-----------------------
//-----------------------
ForkJoinTask<V> fork()
V join()
// 	if you extend RecursiveAction
		protected abstract void compute()
// if you extend RecursiveTask
		protected abstract V compute()
*/

/* El orden SI IMPORTA */
/* fork -> 	compute -> join */

public class FindMinimumAction extends RecursiveTask<Integer>{

	   /**
	 * 
	 */
		private static final long serialVersionUID = 1L;
	// A thread can easily handle, let's say, five elements
	   private static final int SEQUENTIAL_THRESHOLD = 5;
	   // The array with the numbers (we'll pass the same array in
	   // every recursive call to avoid creating a lot of arrays)
	   private int[] data;
	   // The index that tells use where a (sub)task starts
	   private int start;
	   // The index that tells use where a (sub)task ends
	   private int end;

	   // Since compute() doesn't take parameters, you have to
	   // pass in the task's constructor the data to work
	   public FindMinimumAction(int[] data, int start, int end) {
	      this.data = data;
	      this.start = start;
	      this.end = end;
	   }
	   @Override
	   protected Integer compute() {
	      int length = end - start;
	      if (length <= SEQUENTIAL_THRESHOLD) { // base case
	    	  return computeMinimumDirectly();
	         //int min = computeMinimumDirectly();
	         //System.out.println("Minimum of this subarray: "+ min);
	      } else { // recursive case
	         // Calculate new subtasks range
	         int mid = start + length / 2;
	         FindMinimumAction firstSubtask =  new FindMinimumAction(data, start, mid);
	         FindMinimumAction secondSubtask = new FindMinimumAction(data, mid, end);
	         firstSubtask.fork(); // queue the first task
	         //secondSubtask.compute(); // compute the second task
	         //firstSubtask.join(); // wait for the first task result	         
	         //modify for
	         // Return the min off all taks
	         return Math.min(secondSubtask.compute(),(int) firstSubtask.join());
	      }
	      //return 0;
	   }
	   /** Method that find the minimum value */
	   private int computeMinimumDirectly() {
	      int min = Integer.MAX_VALUE;
	      for (int i = start; i < end; i++) {
	         if (data[i] < min) {
	            min = data[i];
	         }
	      }
	      return min;
	   }
public static void main(String[] args) {
	   int[] data = new int[20];
	   Random random = new Random();
	   for (int i = 0; i < data.length; i++) {
	      data[i] = random.nextInt(1000);
	      System.out.print(data[i] + " ");
	      // Let's print each subarray in a line
	      if( (i+1) % SEQUENTIAL_THRESHOLD == 0 ) {
	         System.out.println();
	      }
	   }
	   ForkJoinPool pool = new ForkJoinPool();
	   FindMinimumAction task =
	      new FindMinimumAction(data, 0, data.length);
	   System.out.println("Minimum value of list:"+pool.invoke(task));
}
}

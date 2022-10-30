package algo;


import java.util.Arrays;
import java.util.Comparator;
import java.util.*;
import java.util.Queue;

public class FIFO {

    private int[][] table;
    private int totalCycle;
    private float turnaroundTime;
    private float waitingTime = 0;
    private float cpuUtilization;
    private int[][] workSorted;
    private Queue<Integer> fifoQueue;
    private LinkedList<Integer> waitingList;

    public FIFO(int[][] table) {
        this.waitingList = new LinkedList<Integer>();
        this.fifoQueue = new LinkedList<>();
        this.workSorted = sortWRTArrivalTime(table,1);
        this.table = deepCopy(workSorted,table.length, 5);
    }

    public int[][] sortWRTArrivalTime(int[][] table, int col){

        int[][] arr = deepCopy(table,table.length, 5);
        // Using built-in sort function Arrays.sort,We try to sort 2D array with respect to given column.
        Arrays.sort(arr, new Comparator<int[]>() {
            @Override
            public int compare(final int[] entry1, final int[] entry2) {

                if (entry1[col] > entry2[col])
                    return 1;
                else
                    return -1;
            }
        });

        return arr;
    }
    private void BURST(int currentProcess,int cycle){
        //CPU BURST TIME CHECKER AND DECREMENT
        int j;
        for(j = 0 ; j < workSorted.length; j++){
            // Find that Process Index
            if(workSorted[j][0] == currentProcess){
                //First Execution Time
                if(workSorted[j][7] == -1){
                    workSorted[j][7] = cycle;
                }
                //Second Execution Time
                if(workSorted[j][6] == 3){
                    workSorted[j][9] = cycle;
                }
                workSorted[j][6] = 2; // Currently Executing
                workSorted[j][2]--;//decrement total burst time to ensure it runs enough

                if(workSorted[j][2] == -1){
                    fifoQueue.remove();
                    workSorted[j][6] = 1; //execution complete
                    workSorted[j][8] = cycle;
                    int waiting = workSorted[j][8] - workSorted[j][7];
                    if(waiting == table[j][2]){
                        workSorted[j][5] = 0;
                    }
                    else{
                        workSorted[j][5] =  waiting - table[j][2];
                    }

                }
                break;

            }
        }
    }

    public void INTERRUPT(int currentProcess,int cycle){
        int j;
        for(j = 0 ; j < workSorted.length; j++){
            if(fifoQueue.contains(workSorted[j][0]) && workSorted[j][0] == currentProcess){
                if(workSorted[j][6] == 2){
                    if( workSorted[j][4] != 0 && workSorted[j][4] + workSorted[j][7] == cycle){ // I/O Interrupt Checker
                        waitingList.add(currentProcess);
                        fifoQueue.remove();
                        workSorted[j][6] = 3; // waiting
                        workSorted[j][10] = cycle;

                    }
                }
                break;

            }
        }
    }

    public void WAITING(){
        int j;
        if(waitingList.size() != 0){
            for(j = 0 ; j < workSorted.length; j++) {
                // Find that Process Index
                if (waitingList.contains(workSorted[j][0])) {
                    int index= waitingList.indexOf(workSorted[j][0]);
                    if(workSorted[j][3] != 0){
                        workSorted[j][3] --;
                    }
                    if(workSorted[j][3] == 0){
                        waitingList.remove(index);
                        fifoQueue.add(workSorted[j][0]);
                    }
                }
            }
        }
    }

    public boolean sizeChangeChecker(int size1, int size2){
        if(size1 != size2){
            return true;
        }
        return false;
    }



    public void FifoSimulation() {

        int cycle_count = -1;

        int i = 0, j, currentProcess = -1, currentProcessAfter = -1;
        boolean breakFlag = false;

        do {
            i++;
            cycle_count++;
            for (j = 0; j < workSorted.length; j++) { //ARRIVAL TIME CHECKER AND PUSH QUEUE
                if (cycle_count == workSorted[j][1]) {
                    fifoQueue.add(workSorted[j][0]);
                }
            }

            if (fifoQueue.size() != 0) {
                int tempSize0, tempSize1;
                currentProcess = fifoQueue.peek();

                tempSize0 = fifoQueue.size();
                INTERRUPT(currentProcess, cycle_count);
                tempSize1 = fifoQueue.size();

                if (sizeChangeChecker(tempSize0, tempSize1)) {
                    if (fifoQueue.size() != 0) {
                        currentProcess = fifoQueue.peek(); // switch to new process if an interrupt happened
                        BURST(currentProcess, cycle_count);
                    }
                } else {
                    BURST(currentProcess, cycle_count);
                }
            } else {
                cpuUtilization++;
            }
            WAITING();

        } while (fifoQueue.size() != 0 || waitingList.size() != 0 || i < workSorted.length);
        totalCycle = cycle_count;
    }
    public void statistic(){

        int tTemp = 0,i;
        for(i = 0 ; i < workSorted.length; i++){

            waitingTime = waitingTime + workSorted[i][5];
            tTemp += (workSorted[i][8] - workSorted[i][7]);
            workSorted[i][2] = table[i][2];
            workSorted[i][3] = table[i][3];

        }

        waitingTime /= workSorted.length;
        System.out.println("Average Waiting Time = " + waitingTime);
        float temp = 100 - (cpuUtilization / totalCycle) * 100;
        System.out.println("CPU Utilization: " + temp);
        turnaroundTime = tTemp / workSorted.length;
        System.out.println("Average Turn around Time: "+ turnaroundTime);




    }

    public int[][] deepCopy (int[][] arr,int row,int column){
        int[][] newArr = new int[row][column+6];//for waiting and executed flag
        int i, j;
        for(i = 0; i < row; i++){
            for(j = 0; j<column; j++){
                newArr[i][j] = arr[i][j];
                newArr[i][5] = 0; //waiting
                newArr[i][6] = 0; //execution flag
                newArr[i][7] = -1; //first running time
                newArr[i][8] = 0; //finish time
                newArr[i][9] = 0; //second start time
                newArr[i][10] = 0; //interrupt time
            }
        }
        return newArr;

    }


    @Override
    public String toString() {
        String format = "%3d  | %14d | %14d | %16d | %18d | %16d | %10d | %12d | %13d | %14d | %14d\n";
        System.out.println("Process ID  |  Arrival time  |  CPU Burst time  |  I/O Block Time  " +
                "|  I/O Interrupt time | Waiting Time | Executed | First Execution | Finish Time | Second Execution | Interrupt Time\n" +
                "------------------------------------------------------------------------------------------------------" +
                "-----------------------------------------------------------------------------------");
        for (int[] ints : workSorted) {
            System.out.printf(format, ints[0], ints[1], ints[2], ints[3],
                    ints[4], ints[5], ints[6], ints[7], ints[8], ints[9], ints[10]);
        }
        return "\nFIFO sorts the random Load with respect to arrival time with ascending order.\n\n";
    }



}

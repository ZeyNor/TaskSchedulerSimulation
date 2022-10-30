package algo;

import java.util.*;

public class SJF {

    private final int[][] table;
    private int totalCycle;
    private float turnaroundTime;
    private float waitingTime = 0;
    private float cpuUtilization;
    private final int[][] workSorted;
    private final PriorityQueue<int[]> SJFQueue;
    private final LinkedList<Integer> waitingList;

    public SJF(int[][] table) {

        Comparator <int[]> sjfComparator = new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                if(o2[6] != 2){
                    if(o1[2] > o2[2])
                        return 1;
                    else{
                        return -1;
                    }
                }
                else{
                    return 0;
                }
            }
        };


        this.waitingList = new LinkedList<Integer>();
        this.SJFQueue = new PriorityQueue<int[]>(sjfComparator);
        this.workSorted = sortWRTArrivalTime(table,2);
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
                    SJFQueue.remove();
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

    private void INTERRUPT(int currentProcess,int cycle){
        int j;
        for(j = 0 ; j < workSorted.length; j++){
            if(SJFQueue.contains(workSorted[j]) && workSorted[j][0] == currentProcess){
                if(workSorted[j][6] == 2){
                    if( workSorted[j][4] != 0 && workSorted[j][4] + workSorted[j][7] == cycle){ // I/O Interrupt Checker
                        waitingList.add(currentProcess);
                        SJFQueue.remove();
                        workSorted[j][6] = 3; // waiting
                        workSorted[j][10] = cycle;

                    }
                }
                break;
            }
        }
    }

    private void WAITING(){
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
                        SJFQueue.add(workSorted[j]);
                    }
                }
            }
        }
    }

    private boolean sizeChangeChecker(int size1, int size2){
        return size1 != size2;
    }

    private int findIndex(int size, int key){
        for(int i = 0 ; i <size ; i++){
            if(workSorted[i][0] == key){
                return i;
            }
        }
        return -1;
    }

    public void SJFSimulation() {

        int cycle_count = -1;
        int i = 0, j, currentProcess = -1, index;

        do {
            i++;
            cycle_count++;

            for (j = 0; j < workSorted.length; j++) { //ARRIVAL TIME CHECKER AND PUSH QUEUE
                if (cycle_count == workSorted[j][1]) {
                    SJFQueue.add(workSorted[j]);
                }
            }

            if (SJFQueue.size() != 0) {
                int tempSize0, tempSize1;
                currentProcess = SJFQueue.peek()[0];

                tempSize0 = SJFQueue.size();
                INTERRUPT(currentProcess, cycle_count);
                tempSize1 = SJFQueue.size();

                if (sizeChangeChecker(tempSize0, tempSize1)) {
                    if (SJFQueue.size() != 0) {
                        currentProcess = SJFQueue.peek()[0]; // switch to new process if an interrupt happened
                        BURST(currentProcess, cycle_count);
                    }
                    else{
                        cpuUtilization++;
                    }
                } else {
                    BURST(currentProcess, cycle_count);
                }
            }
            else {
                cpuUtilization++;
            }
            WAITING();

        } while (SJFQueue.size() != 0 || waitingList.size() != 0 || i < workSorted.length);
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

    private int[][] deepCopy (int[][] arr,int row,int column){
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
        return "\nSJF sorts the random Load with respect to arrival time with ascending order.\n\n";
    }

}



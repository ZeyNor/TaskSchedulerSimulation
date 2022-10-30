

import java.util.Arrays;
import java.util.Random;


public class WorkGenerator {

    private int[][] work;

    public WorkGenerator(int tableSize) {
        this.work = randomWorkLoadGenerator(tableSize);
    }

    public int[][] getWork() {
        return work;
    }

    public void setWork(int[][] work) {
        this.work = work;
    }

    public int[][] randomWorkLoadGenerator(int size){
        int [][] table = new int[size][5];
        /*
        table[][] = Process ID  |  Arrival time  |  CPU burst time  |  I/O Block Time  |  I/O Interrupt time
                   -----------------------------------------------------------------------------------------
                         0             0                 1                  2                   2
                         *             *                 *                  *                   *
                         *             *                 *                  *                   *
                         *             *                 *                  *                   *
        * */
        int i ,j;

        Random rand = new Random();
        for(i = 0 ; i < size; i++){
            // Process ID
            table[i][0] = i;

            // Arrival time
            table[i][1] = getRandomInt(rand, 0, table[i][0] + size - size % 10);

            // CPU burst time
            table[i][2] = getRandomInt(rand, 1, size-1);

            // I/O Interrupt time
            table[i][4] = getRandomInt(rand, 0, table[i][2]-1);

            if (table[i][4] > 0)
                // I/O Block Time
                table[i][3] = getRandomInt(rand, 0, 20);
            else
                table[i][3] = 0;
        }

        return table;
    }

    public int getRandomInt(Random random, int min, int max)
    {
        return random.nextInt(max - min + 1) + min;
    }


    @Override
    public String toString() {
        String format = "%3d  | %14d | %14d | %16d | %18d\n";
        System.out.println("Process ID  |  Arrival time  |  CPU Burst time  |  I/O Block Time  |  I/O Interrupt time\n" +
                "-----------------------------------------------------------------------------------------");
        for (int[] ints : work) {
            System.out.printf(format, ints[0], ints[1], ints[2], ints[3], ints[4]);
        }
        return "\nRandom Load is Generated\n\n";
    }
}


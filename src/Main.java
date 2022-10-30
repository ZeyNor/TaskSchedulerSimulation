

import algo.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Please Enter the Workload Size: ");
        int size = in.nextInt();
        WorkGenerator test = new WorkGenerator(size);
        System.out.println(test);

        FIFO FIFOTest = new FIFO(test.getWork());


        FIFOTest.FifoSimulation();
        System.out.println("Fifo statistics");
        FIFOTest.statistic();
        System.out.println(FIFOTest);


        SJF SJFTest = new SJF(test.getWork());
        SJFTest.SJFSimulation();
        System.out.println("\nSJF statistics");
        SJFTest.statistic();
        System.out.println(SJFTest);



    }
}
package com.systemvi.examples.minesweaper;

import java.util.Scanner;

public class App {
    public static void start() {
        Table table=new Table(15,50);
        Scanner scanner=new Scanner(System.in);
        while (true){
            table.print();
            int x=scanner.nextInt();
            int y=scanner.nextInt();
            table.open(x,y);
        }
    }
}

package cn.ningle.shellsimulator;

import java.util.Scanner;

/**
 * @author ningle
 * @version : Main.java, v 0.1 2024/06/24 15:26 ningle
 **/
public class Main {
    public static void main(String[] args) {
        System.out.println("=========控制台============");
        Scanner sc = new Scanner(System.in);
        while (true) {
            String command = sc.nextLine();
            if ("exit".equals(command)) break;
            CommandHandler.handle(command);
        }
    }
}

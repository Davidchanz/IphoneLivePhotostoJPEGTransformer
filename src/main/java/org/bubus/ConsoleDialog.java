package org.bubus;

import org.bubus.zambara.annotation.Component;

import java.util.Scanner;

@Component
public class ConsoleDialog {
    public boolean askYesNo(String question){
        System.out.print(question);
        while (true) {
            System.out.print(" Enter [Yes/No]: ");
            Scanner scanner = new Scanner(System.in);
            String answer = scanner.nextLine();
            if(answer.equalsIgnoreCase("Yes") ||
                    answer.equalsIgnoreCase("Y")){
                return true;
            }else if(answer.equalsIgnoreCase("No") ||
                    answer.equalsIgnoreCase("N")){
                return false;
            }
        }
    }
}

package loanpaymentcalculator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class LoanPaymentCalculator {
    private static Scanner scanner = new Scanner(System.in);
    private static double monthlyRate;
    
    private static double calculate(double principal, int numberOfPayments){
        double amortization = 0.00;
        if(monthlyRate > 0){
            amortization = (principal * monthlyRate * Math.pow(1 + monthlyRate, (double)numberOfPayments)) /
                     (Math.pow(1 + monthlyRate, (double)numberOfPayments) - 1);
        } else {
            amortization = principal / numberOfPayments;
        }
        return amortization;
    }
    
    public static void printTable(double principal, int numberOfPayments){
        Object[] headers = {"Payment #", "Amount Due", "Interest Due", "Principal Due", "Amount Left"};
        System.out.println("\n*****************************************************************************");
        System.out.println("******************************* Loan Payoff Table ***************************");
        System.out.println("*****************************************************************************");
        System.out.format("%5s %12s %18s %18s %16s \n", headers);
        System.out.println("*****************************************************************************");

        double monthlyPayment = calculate(principal, numberOfPayments);
        double interestPayment = 0.0;
        double principalDue = 0.0;
        double principalLeft = principal;
        Object[][] rows = new Object[numberOfPayments][];

        for(int i = 0; i < numberOfPayments; i++) {
            interestPayment = monthlyRate * principalLeft;
            principalDue = monthlyPayment - interestPayment;
            principalLeft = Math.abs(principalLeft - principalDue);
            Object[] row = {i+1, monthlyPayment, interestPayment, principalDue, principalLeft};
            rows[i] = row;
            System.out.format("%5d%17.2f%19.2f%19.2f%17.2f\n", row);
        }

        promptToSaveCsv(rows);
    }

    private static void promptToSaveCsv(Object[][] rows){
        System.out.print("\nSave the table as a CSV file? [Yes/No]: ");
        String response = scanner.next();
        
        if("yes".equalsIgnoreCase(response)){
            try {
                FileWriter fileWriter = new FileWriter("LoanPaymentTable.csv");
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                StringBuilder stringBuilder = new StringBuilder();

                for (Object[] row : rows) {
                    for (Object column : row) {
                        if(stringBuilder.length()!= 0){
                            stringBuilder.append(',');
                        }
                        stringBuilder.append(column);
                    }

                    stringBuilder.append("\n");
                    bufferedWriter.write(stringBuilder.toString());
                    stringBuilder.setLength(0);
                }

                bufferedWriter.close();
                System.out.println("Table saved as LoanPaymentTable.csv");
            } catch(IOException e){
                e.printStackTrace();
            }
        } else {
            System.out.println("\tTable not saved as a CSV file\n");
        }
    }  
    
    public static void main(String[] args) {
        System.out.println("*******************************************");
        System.out.println("\tLoan Payment Calculator");
        System.out.println("*******************************************");
        System.out.print("Enter the loan amount: ");
        double principal = scanner.nextDouble();

        System.out.print("Enter the interest rate (APR format is 00.00): ");
        double interest = scanner.nextDouble();

        System.out.print("Enter the total number of payments: ");
        int numberOfPayments = scanner.nextInt();

        monthlyRate = (double) interest / 1200;
        
        double loanPayment = calculate(principal, numberOfPayments);
        
        System.out.println("*******************************************");
        System.out.printf("  The loan payment amount is %.2f\n", loanPayment);
        System.out.println("*******************************************");
        printTable(principal, numberOfPayments);
    }
}
package mta.patmal.enigma.console;

import mta.patmal.enigma.dto.MachineData;
import mta.patmal.enigma.engine.Engine;

import java.util.Scanner;

public class ConsoleUI {
    private final Engine engine;

    public ConsoleUI(Engine engine) {
        this.engine = engine;
    }

    public void run(){
        while (true) {
            showMenu();
            int input = getUserInput();
            processUserInput(input);
        }
    }

    public void showMenu() {
        System.out.println("Enigma Machine");
        System.out.println("1. Load XML");
        System.out.println("2. Show Machine Data");
        System.out.println("3. Code Manual");
        System.out.println("4. Code Automatic");
        System.out.println("5. Process");
        System.out.println("6. Reset Current Code");
        System.out.println("7. Statistics");
        System.out.println("8. Exit");
    }

    private int getUserInput(){
        System.out.print("Enter your choice: ");
        Scanner scanner = new Scanner(System.in);
        try{
            int input = scanner.nextInt();
            return input;
        } catch (Exception e){
            System.out.println("Invalid input. Make sure you enter a number between 1 and 8.");
            return getUserInput();
        }
    }

    public void processUserInput(int input) {
        switch (input) {
            case 1: // Load XML
                // will be a class handling this?
                System.out.print("Enter XML file path: ");
                Scanner scanner = new Scanner(System.in);
                String path = scanner.nextLine();
                System.out.println("Loading XML file...");
                engine.loadXml(path);
                break;

            case 2: // Show Machine Data
                MachineData machineData = engine.showMachineData();
                if (machineData != null) {
                    showMachineData(machineData);
                }
                break;

            case 3: // Code Manual
                engine.codeManual();
                break;

            case 4: // Code Automatic
                engine.codeAutomatic();
                break;

            case 5: // Process
                System.out.print("Enter text to process: ");
                String text = System.console().readLine();
                String result = engine.process(text);
                System.out.println("Processed text: " + result);
                break;

            case 6: // Reset Current Code
                System.out.println("Resetting current code...");
                // engine.resetCurrentCode(); // Uncomment if this method is added to Engine interface
                break;

            case 7: // Statistics
                engine.statistics();
                break;

            case 8: // Exit
                System.out.println("Exiting...");
                System.exit(0);
                break;

            default:
                System.out.println("Invalid option. Make sure you enter a number between 1 and 8.\n Please try again.");
        }
    }

    private void showMachineData(MachineData machineData){
        System.out.println("Machine specification:");
        System.out.println("Total rotors: " + machineData.getTotalRotors());
        System.out.println("Total reflectors: " + machineData.getTotalReflectors());
        System.out.println("Messages processed since last load: " + machineData.getMessagesProcessed());

        if (machineData.getCurrentCode() != null) {
            if (machineData.getOriginalCode() != null) {
                System.out.println("Original code configuration: " + machineData.getOriginalCode());
            }
            System.out.println("Current code configuration: " + machineData.getCurrentCode());
        } else {
            System.out.println("No code configured.");
        }
    }
}

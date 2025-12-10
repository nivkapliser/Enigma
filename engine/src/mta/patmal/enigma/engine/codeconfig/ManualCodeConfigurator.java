package mta.patmal.enigma.engine.codeconfig;

import mta.patmal.enigma.engine.loader.RomanNumeralUtils;
import mta.patmal.enigma.engine.loader.XmlLoader;
import mta.patmal.enigma.machine.component.code.Code;
import mta.patmal.enigma.machine.component.code.CodeImpl;
import mta.patmal.enigma.machine.component.machine.Machine;
import mta.patmal.enigma.machine.component.machine.MachineImpl;
import mta.patmal.enigma.machine.component.reflector.Reflector;
import mta.patmal.enigma.machine.component.rotor.Rotor;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class ManualCodeConfigurator {
    private static final int REQUIRED_ROTOR_COUNT = 3;

    private final Machine machine;
    private final XmlLoader xmlLoader;
    private final String abc;
    private final int totalRotors;
    private final int totalReflectors;

    public ManualCodeConfigurator(Machine machine, XmlLoader xmlLoader, String abc, int totalRotors, int totalReflectors) {
        this.machine = machine;
        this.xmlLoader = xmlLoader;
        this.abc = abc;
        this.totalRotors = totalRotors;
        this.totalReflectors = totalReflectors;
    }

    public boolean configure() {
        List<Integer> rotorIds = promptAndParseRotorIdsWithRetry();
        if (rotorIds == null) {
            return false; // User chose to return to main menu
        }

        List<Integer> rotorPositions = promptAndParseRotorPositionsWithRetry(rotorIds);
        if (rotorPositions == null) {
            return false; // User chose to return to main menu
        }

        Reflector reflector = promptAndParseReflectorWithRetry();
        if (reflector == null) {
            return false; // User chose to return to main menu
        }

        try {
            createAndSetCode(rotorIds, rotorPositions, reflector);
            System.out.println("Code configuration set successfully!");
            return true;
        } catch (Exception e) {
            System.out.println("Error configuring code: " + e.getMessage());
            System.out.println("Returning to main menu.");
            return false;
        }
    }

    private List<Integer> promptAndParseRotorIdsWithRetry() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            displayAvailableRotors();
            System.out.print("Please enter " + REQUIRED_ROTOR_COUNT + " rotor IDs (separated by commas): ");
            String input = scanner.nextLine().trim();
            
            if (shouldReturnToMenu(input)) {
                return null;
            }
            
            try {
                return parseRotorIds(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                if (!shouldRetry(scanner)) {
                    return null;
                }
            }
        }
    }

    private void displayAvailableRotors() {
        System.out.println("Available rotors:");
        for (int i = 1; i <= totalRotors; i++) {
            System.out.println(i + ". Rotor " + i);
        }
    }

    private List<Integer> parseRotorIds(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Rotor IDs cannot be empty. Please enter " + REQUIRED_ROTOR_COUNT + 
                    " rotor IDs separated by commas.");
        }

        String[] parts = input.split(",");
        if (parts.length != REQUIRED_ROTOR_COUNT) {
            throw new IllegalArgumentException("Expected exactly " + REQUIRED_ROTOR_COUNT + 
                    " rotor IDs, but got " + parts.length + ". Please enter " + REQUIRED_ROTOR_COUNT + 
                    " rotor IDs separated by commas.");
        }

        List<Integer> rotorIds = new ArrayList<>();
        for (String part : parts) {
            try {
                int rotorId = Integer.parseInt(part.trim());
                rotorIds.add(rotorId);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid rotor ID format: '" + part.trim() + 
                        "'. Rotor IDs must be decimal numbers.");
            }
        }

        // Validate that all rotor IDs exist in the loaded XML
        for (Integer rotorId : rotorIds) {
            if (rotorId < 1 || rotorId > totalRotors) {
                throw new IllegalArgumentException("Rotor ID " + rotorId + " not found in the loaded machine. " +
                        "Available rotor IDs are 1-" + totalRotors + ".");
            }
            try {
                xmlLoader.createRotorById(rotorId);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Rotor ID " + rotorId + " not found in the loaded machine. " +
                        "Available rotor IDs are 1-" + totalRotors + ".");
            }
        }

        return rotorIds;
    }

    private List<Integer> promptAndParseRotorPositionsWithRetry(List<Integer> rotorIds) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            displayAvailablePositions();
            System.out.print("Please enter " + REQUIRED_ROTOR_COUNT + 
                    " initial position characters: ");
            String input = scanner.nextLine().trim();
            
            if (shouldReturnToMenu(input)) {
                return null;
            }
            
            try {
                return parseRotorPositions(input,rotorIds);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                if (!shouldRetry(scanner)) {
                    return null;
                }
            }
        }
    }

    private void displayAvailablePositions() {
        System.out.println("Available position characters (ABC): " + abc);
    }


    private List<Integer> parseRotorPositions(String input, List<Integer> rotorIds) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Initial positions cannot be empty. Please enter " + 
                    REQUIRED_ROTOR_COUNT + " characters from the ABC.");
        }

        if (input.length() != REQUIRED_ROTOR_COUNT) {
            throw new IllegalArgumentException("Expected exactly " + REQUIRED_ROTOR_COUNT + 
                    " characters for initial positions, but got " + input.length() + 
                    ". Please enter " + REQUIRED_ROTOR_COUNT + " characters from the ABC.");
        }

        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < input.length(); i++) {
            char positionChar = input.charAt(i);
            // קודם מוודאים שהאות בכלל נמצאת ב-ABC
            if (abc.indexOf(positionChar) == -1) {
                throw new IllegalArgumentException("Character '" + positionChar +
                        "' is not in the ABC. The ABC is: " + abc +
                        ". Please enter " + REQUIRED_ROTOR_COUNT + " characters from the ABC.");
            }

            int rotorId = rotorIds.get(i); // הרוטור המתאים לאות הזאת (משמאל לימין)

            // וזה החלק החשוב: לוקחים את האינדקס בעמודת RIGHT של הרוטור
            int zeroBasedPos = xmlLoader.getPositionIndexByRightLetter(rotorId, positionChar);
            positions.add(zeroBasedPos);
        }

        return positions;
    }

    private Reflector promptAndParseReflectorWithRetry() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            displayReflectorMenu();
            System.out.print("Please select a reflector (1-" + totalReflectors + "): ");
            String input = scanner.nextLine().trim();
            
            if (shouldReturnToMenu(input)) {
                return null;
            }
            
            try {
                return parseReflector(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                if (!shouldRetry(scanner)) {
                    return null;
                }
            }
        }
    }

    private void displayReflectorMenu() {
        System.out.println("Available reflectors:");
        for (int i = 1; i <= totalReflectors; i++) {
            String romanNumeral = RomanNumeralUtils.intToRoman(i);
            System.out.println(i + ". " + romanNumeral);
        }
    }

    private Reflector parseReflector(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Reflector selection cannot be empty. Please enter a number between 1 and " + 
                    totalReflectors + ".");
        }

        int reflectorId;
        try {
            reflectorId = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid reflector selection: '" + input.trim() + 
                    "'. Please enter a number between 1 and " + totalReflectors + ".");
        }

        if (reflectorId < 1 || reflectorId > totalReflectors) {
            throw new IllegalArgumentException("Reflector ID must be between 1 and " + 
                    totalReflectors + ", but got " + reflectorId + 
                    ". Please enter a number between 1 and " + totalReflectors + ".");
        }

        try {
            return xmlLoader.createReflectorByNumericId(reflectorId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Reflector " + RomanNumeralUtils.intToRoman(reflectorId) + 
                    " not found in the loaded machine. " + e.getMessage());
        }
    }

    private boolean shouldReturnToMenu(String input) {
        return input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("menu") || 
               input.equalsIgnoreCase("back") || input.equalsIgnoreCase("cancel");
    }

    private boolean shouldRetry(Scanner scanner) {
        System.out.print("Would you like to try again? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();
        return response.equals("y") || response.equals("yes");
    }

    private void createAndSetCode(List<Integer> rotorIds, List<Integer> rotorPositions, Reflector reflector) {
        // Reverse the lists because user input is left-to-right, but we store right-to-left
        // (rightmost rotor is at index 0)
        List<Integer> reversedRotorIds = new ArrayList<>();
        List<Integer> reversedPositions = new ArrayList<>();
        
        for (int i = rotorIds.size() - 1; i >= 0; i--) {
            reversedRotorIds.add(rotorIds.get(i));
            reversedPositions.add(rotorPositions.get(i));
        }

        // Create rotors in the correct order (rightmost first)
        List<Rotor> rotors = new ArrayList<>();
        for (Integer rotorId : reversedRotorIds) {
            Rotor rotor = xmlLoader.createRotorById(rotorId);
            rotors.add(rotor);
        }

        // Set initial positions on rotors
        for (int i = 0; i < rotors.size(); i++) {
            rotors.get(i).setPosition(reversedPositions.get(i));
        }

        // Create Code object
        Code code = new CodeImpl(rotors, reversedPositions, reflector);
        
        // Set code on machine
        if (machine instanceof MachineImpl) {
            ((MachineImpl) machine).setCode(code);
        } else {
            throw new IllegalStateException("Machine is not a MachineImpl instance");
        }
    }
}


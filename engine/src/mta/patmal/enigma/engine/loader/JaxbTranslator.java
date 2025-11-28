package mta.patmal.enigma.engine.loader;

import mta.patmal.enigma.engine.jaxb.generated.*;
import mta.patmal.enigma.machine.component.machine.Machine;
import mta.patmal.enigma.machine.component.reflector.Reflector;
import mta.patmal.enigma.machine.component.rotor.Rotor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JaxbTranslator {

    public void validateMachineFormat(BTEEnigma xmlEnigma) {
        if (xmlEnigma == null) {
            throw new IllegalArgumentException("Invalid XML file"); // change
        }

        validateABC(xmlEnigma);
        validateRotors(xmlEnigma);
        validateReflectors(xmlEnigma);

    }

    private void validateABC(BTEEnigma bteEnigma) {
        String abc = bteEnigma.getABC();
        if (abc == null){
            System.out.println("No ABC specified"); // will change to an exception
        }
        abc = abc.trim();
        if (abc.length() % 2 != 0){
            System.out.println("ABC must be even length"); // will change to an exception
        }
    }

    private void validateRotors(BTEEnigma bteEnigma) {
        List<BTERotor> rotors = bteEnigma.getBTERotors().getBTERotor();
        String abc = bteEnigma.getABC();
        int abcSize = abc.length();

        if (rotors == null || rotors.size() < 3){
            System.out.println("At least 3 rotors are required"); // will change to an exception
        }

        Set<Integer> ids = new HashSet<>();
        int minId = Integer.MAX_VALUE;
        int maxId = Integer.MIN_VALUE;

        for (BTERotor rotor : rotors) {
            int id = rotor.getId();
            if (!ids.add(id)) {
                System.out.println("Duplicate rotor id: " + id); // will change to an exception
            }
            minId = Math.min(minId, id);
            maxId = Math.max(maxId, id);

            validateSingleRotorMappings(rotor, abcSize);
            validateNotch(rotor, abcSize);
        }
        if (minId != 1) {
            System.out.println("Rotor ids must start from 1, minimal id is " + minId); // will change to an exception
        }

        if (ids.size() != maxId) {
            System.out.println("Rotor ids must form a continuous sequence 1.." + maxId + ", but got: " + ids);
        }
    }

    private void validateSingleRotorMappings(BTERotor rotor, int abcSize) {
        List<BTEPositioning> mappings = rotor.getBTEPositioning();
        if (mappings == null || mappings.isEmpty()) {
            System.out.println("Rotor " + rotor.getId() + " has no mappings"); // will change to an exception
        }

        Set<Character> leftSet = new HashSet<>(); // should change to map?
        Set<Character> rightSet = new HashSet<>(); // should change to map?

        for (BTEPositioning pos : mappings) {
            String leftStr = pos.getLeft();
            String rightStr = pos.getRight();

            if (leftStr == null || rightStr == null || leftStr.length() != 1 || rightStr.length() != 1) {
                System.out.println("Rotor " + rotor.getId() + " has invalid mapping (left/right must be single letters)"); // will change to an exception
            }

            char left = leftStr.charAt(0);
            char right = rightStr.charAt(0);

            if (!leftSet.add(left)) {
                System.out.println("Rotor " + rotor.getId() + " has duplicate mapping for LEFT letter: " + left); // will change to an exception
            }
            if (!rightSet.add(right)) {
                System.out.println("Rotor " + rotor.getId() + " has duplicate mapping for RIGHT letter: " + right);
            }
        }

        if (mappings.size() != abcSize) {
            System.out.println("Rotor " + rotor.getId() + " mapping count (" + mappings.size() +
                            ") does not match ABC size (" + abcSize + ")"); // will change to an exception
        }
    }

    private void validateNotch(BTERotor rotor, int abcSize) {
        int notch = rotor.getNotch();
        if (notch < 1 || notch > abcSize) {
            System.out.println("Rotor " + rotor.getId() + " notch (" + notch + ") is out of range 1.." + abcSize);// will change to an exception
        }
    }

    private void validateReflectors(BTEEnigma machine) {
        List<BTEReflector> reflectors = machine.getBTEReflectors().getBTEReflector();
        if (reflectors == null || reflectors.isEmpty()) {
            System.out.println("No reflectors defined in XML"); // will change to an exception
        }

        Set<Integer> ids = new HashSet<>();
        int maxId = Integer.MIN_VALUE;

        for (BTEReflector reflector : reflectors) {
            String romanId = reflector.getId();
            int numericId = romanToInt(romanId);

            if (numericId < 1 || numericId > 5) {
                System.out.println("Reflector id must be in range I..V, got: " + romanId); // will change to an exception
            }

            if (!ids.add(numericId)) {
                System.out.println("Duplicate reflector id: " + romanId + " (=" + numericId + ")"); // will change to an exception
            }

            maxId = Math.max(maxId, numericId);

            validateSingleReflectorMappings(reflector);
        }

        for (int i = 1; i <= maxId; i++) {
            if (!ids.contains(i)) {
                System.out.println(
                        "Reflector ids must form a continuous roman sequence from I to " +
                                intToRoman(maxId) + ", missing: " + intToRoman(i)); // will change to exception
            }
        }
    }

    private void validateSingleReflectorMappings(BTEReflector reflector) {
        List<BTEReflect> mappings = reflector.getBTEReflect();
        if (mappings == null || mappings.isEmpty()) {
            System.out.println(
                    "Reflector " + reflector.getId() + " has no mappings"); // will change to an exception
        }

        for (BTEReflect mapping : mappings) {
            int input = mapping.getInput();
            int output = mapping.getOutput();

            if (input == output) {
                System.out.println(
                        "Reflector " + reflector.getId() +
                                " has a mapping from position to itself: " + input); // will change to an exception
            }
        }
    }

    private int romanToInt(String roman) {
        if (roman == null) {
            System.out.println("Reflector id is null"); // will change to exception
        }

        switch (roman.trim()) {
            case "I":
                return 1;
            case "II":
                return 2;
            case "III":
                return 3;
            case "IV":
                return 4;
            case "V":
                return 5;
            default:
                System.out.println("Invalid roman reflector id: " + roman); // will change to exception
                return -1;
        }
    }

    private String intToRoman(int value) {
        switch (value) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            default:
                return Integer.toString(value);
        }
    }

    public Machine translateToMachine(BTEEnigma enigma) {
        return null;
    }
}
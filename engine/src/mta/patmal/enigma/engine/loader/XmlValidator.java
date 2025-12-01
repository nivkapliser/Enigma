package mta.patmal.enigma.engine.loader;

import mta.patmal.enigma.engine.jaxb.generated.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class XmlValidator {

    public void validateMachineFormat(BTEEnigma xmlEnigma) {
        if (xmlEnigma == null) {
            throw new IllegalArgumentException("Invalid XML file: BTEEnigma is null");
        }

        validateABC(xmlEnigma);
        validateRotors(xmlEnigma);
        validateReflectors(xmlEnigma);
    }

    private void validateABC(BTEEnigma bteEnigma) {
        String abc = bteEnigma.getABC();
        if (abc == null) {
            throw new IllegalStateException("No ABC specified in XML");
        }
        abc = abc.trim();
        if (abc.isEmpty()) {
            throw new IllegalArgumentException("ABC cannot be empty");
        }
        if (abc.length() % 2 != 0) {
            throw new IllegalArgumentException("ABC must be even length, got: " + abc.length());
        }
    }

    private void validateRotors(BTEEnigma bteEnigma) {
        List<BTERotor> rotors = bteEnigma.getBTERotors().getBTERotor();
        String abc = bteEnigma.getABC();
        if (abc == null || abc.trim().isEmpty()) {
            throw new IllegalStateException("Cannot validate rotors: ABC is null or empty");
        }
        int abcSize = abc.length();

        if (rotors == null || rotors.size() < 3) {
            throw new IllegalStateException("At least 3 rotors are required, got: " + 
                    (rotors == null ? 0 : rotors.size()));
        }

        Set<Integer> ids = new HashSet<>();
        int minId = Integer.MAX_VALUE;
        int maxId = Integer.MIN_VALUE;

        for (BTERotor rotor : rotors) {
            int id = rotor.getId();
            if (!ids.add(id)) {
                throw new IllegalArgumentException("Duplicate rotor id: " + id);
            }
            minId = Math.min(minId, id);
            maxId = Math.max(maxId, id);

            validateSingleRotorMappings(rotor, abcSize);
            validateNotch(rotor, abcSize);
        }
        
        if (minId != 1) {
            throw new IllegalArgumentException("Rotor ids must start from 1, minimal id is " + minId);
        }

        if (ids.size() != maxId) {
            throw new IllegalArgumentException("Rotor ids must form a continuous sequence 1.." + maxId + 
                    ", but got: " + ids);
        }
    }

    private void validateSingleRotorMappings(BTERotor rotor, int abcSize) {
        List<BTEPositioning> mappings = rotor.getBTEPositioning();
        if (mappings == null || mappings.isEmpty()) {
            throw new IllegalStateException("Rotor " + rotor.getId() + " has no mappings");
        }

        Set<Character> leftSet = new HashSet<>();
        Set<Character> rightSet = new HashSet<>();

        for (BTEPositioning pos : mappings) {
            String leftStr = pos.getLeft();
            String rightStr = pos.getRight();

            if (leftStr == null || rightStr == null || leftStr.length() != 1 || rightStr.length() != 1) {
                throw new IllegalArgumentException("Rotor " + rotor.getId() + 
                        " has invalid mapping (left/right must be single letters): left=" + leftStr + ", right=" + rightStr);
            }

            char left = leftStr.charAt(0);
            char right = rightStr.charAt(0);

            if (!leftSet.add(left)) {
                throw new IllegalArgumentException("Rotor " + rotor.getId() + 
                        " has duplicate mapping for LEFT letter: " + left);
            }
            if (!rightSet.add(right)) {
                throw new IllegalArgumentException("Rotor " + rotor.getId() + 
                        " has duplicate mapping for RIGHT letter: " + right);
            }
        }

        if (mappings.size() != abcSize) {
            throw new IllegalArgumentException("Rotor " + rotor.getId() + 
                    " mapping count (" + mappings.size() + ") does not match ABC size (" + abcSize + ")");
        }
    }

    private void validateNotch(BTERotor rotor, int abcSize) {
        int notch = rotor.getNotch();
        if (notch < 1 || notch > abcSize) {
            throw new IllegalArgumentException("Rotor " + rotor.getId() + 
                    " notch (" + notch + ") is out of range 1.." + abcSize);
        }
    }

    private void validateReflectors(BTEEnigma machine) {
        List<BTEReflector> reflectors = machine.getBTEReflectors().getBTEReflector();
        if (reflectors == null || reflectors.isEmpty()) {
            throw new IllegalStateException("No reflectors defined in XML");
        }

        Set<Integer> ids = new HashSet<>();
        int maxId = Integer.MIN_VALUE;

        for (BTEReflector reflector : reflectors) {
            String romanId = reflector.getId();
            int numericId;
            try {
                numericId = RomanNumeralUtils.romanToInt(romanId);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Reflector id must be in range I..V, got: " + romanId, e);
            }

            if (numericId < 1 || numericId > 5) {
                throw new IllegalArgumentException("Reflector id must be in range I..V, got: " + romanId + " (=" + numericId + ")");
            }

            if (!ids.add(numericId)) {
                throw new IllegalArgumentException("Duplicate reflector id: " + romanId + " (=" + numericId + ")");
            }

            maxId = Math.max(maxId, numericId);

            validateSingleReflectorMappings(reflector);
        }

        for (int i = 1; i <= maxId; i++) {
            if (!ids.contains(i)) {
                throw new IllegalArgumentException("Reflector ids must form a continuous roman sequence from I to " +
                        RomanNumeralUtils.intToRoman(maxId) + ", missing: " + RomanNumeralUtils.intToRoman(i));
            }
        }
    }

    private void validateSingleReflectorMappings(BTEReflector reflector) {
        List<BTEReflect> mappings = reflector.getBTEReflect();
        if (mappings == null || mappings.isEmpty()) {
            throw new IllegalStateException("Reflector " + reflector.getId() + " has no mappings");
        }

        for (BTEReflect mapping : mappings) {
            int input = mapping.getInput();
            int output = mapping.getOutput();

            if (input == output) {
                throw new IllegalArgumentException("Reflector " + reflector.getId() +
                        " has a mapping from position to itself: " + input);
            }
        }
    }
}


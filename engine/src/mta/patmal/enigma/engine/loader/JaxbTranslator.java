package mta.patmal.enigma.engine.loader;

import mta.patmal.enigma.engine.jaxb.generated.*;
import mta.patmal.enigma.machine.component.code.Code;
import mta.patmal.enigma.machine.component.code.CodeImpl;
import mta.patmal.enigma.machine.component.keyboard.Keyboard;
import mta.patmal.enigma.machine.component.keyboard.KeyboardImpl;
import mta.patmal.enigma.machine.component.machine.Machine;
import mta.patmal.enigma.machine.component.machine.MachineImpl;
import mta.patmal.enigma.machine.component.reflector.Reflector;
import mta.patmal.enigma.machine.component.reflector.ReflectorImpl;
import mta.patmal.enigma.machine.component.rotor.Rotor;
import mta.patmal.enigma.machine.component.rotor.RotorImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JaxbTranslator {

    public Machine translateToMachine(BTEEnigma enigma) {
        // Get ABC string
        String abc = enigma.getABC().trim();
        
        // Create Keyboard
        Keyboard keyboard = new KeyboardImpl(abc);
        
        // Sort rotors by ID and select first 3
        List<BTERotor> bteRotors = enigma.getBTERotors().getBTERotor();
        List<BTERotor> selectedBteRotors = bteRotors.stream()
                .sorted((r1, r2) -> Integer.compare(r1.getId(), r2.getId()))
                .limit(3)
                .collect(Collectors.toList());
        
        // Create selected Rotors
        List<Rotor> selectedRotors = createRotors(selectedBteRotors, abc);
        
        // Sort reflectors by ID and select first one
        List<BTEReflector> bteReflectors = enigma.getBTEReflectors().getBTEReflector();
        BTEReflector selectedBteReflector = bteReflectors.stream()
                .sorted((r1, r2) -> Integer.compare(RomanNumeralUtils.romanToInt(r1.getId()), RomanNumeralUtils.romanToInt(r2.getId())))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No reflectors available"));
        
        // Create selected Reflector
        Reflector selectedReflector = createReflector(selectedBteReflector);
        
        // Create initial positions (all 0)
        List<Integer> initialPositions = new ArrayList<>();
        for (int i = 0; i < selectedRotors.size(); i++) {
            initialPositions.add(0);
        }
        
        // Create Code
        Code code = new CodeImpl(selectedRotors, initialPositions, selectedReflector);
        
        // Create Machine
        Machine machine = new MachineImpl(keyboard);
        machine.setCode(code);
        
        return machine;
    }
    
    private List<Rotor> createRotors(List<BTERotor> bteRotors, String abc) {
        List<Rotor> rotors = new ArrayList<>();
        
        for (BTERotor bteRotor : bteRotors) {
            // Create forward and backward wiring maps
            Map<Integer, Integer> forwardWiring = new HashMap<>();
            Map<Integer, Integer> backwardWiring = new HashMap<>();
            
            // Convert character mappings to index mappings
            for (BTEPositioning positioning : bteRotor.getBTEPositioning()) {
                char leftChar = positioning.getLeft().charAt(0);
                char rightChar = positioning.getRight().charAt(0);
                
                int leftIndex = abc.indexOf(leftChar);
                int rightIndex = abc.indexOf(rightChar);
                
                if (leftIndex == -1 || rightIndex == -1) {
                    throw new IllegalArgumentException(
                            "Character not found in ABC: left=" + leftChar + ", right=" + rightChar);
                }
                
                forwardWiring.put(leftIndex, rightIndex);
                backwardWiring.put(rightIndex, leftIndex);
            }
            
            // Convert notch from 1-based to 0-based
            int notch = bteRotor.getNotch() - 1;
            
            // Create rotor with initial position 0 and ring setting 0
            Rotor rotor = new RotorImpl(
                    bteRotor.getId(),
                    forwardWiring,
                    backwardWiring,
                    0,  // initial position
                    notch,
                    0   // ring setting
            );
            
            rotors.add(rotor);
        }
        
        return rotors;
    }
    
    private Reflector createReflector(BTEReflector bteReflector) {
        Map<Integer, Integer> wiring = new HashMap<>();
        
        // Convert reflector mappings from 1-based to 0-based
        for (BTEReflect reflect : bteReflector.getBTEReflect()) {
            int input = reflect.getInput() - 1;  // Convert to 0-based
            int output = reflect.getOutput() - 1; // Convert to 0-based
            wiring.put(input, output);
        }
        
        return new ReflectorImpl(wiring);
    }
}
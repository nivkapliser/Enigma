package mta.patmal.enigma.machine.test;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MachineTestMain {
    public static void main(String[] args) {

        String alphabet = "ABCDEF";
        Keyboard keyboard = new KeyboardImpl(alphabet);

        // === ROTOR 1 ===  (A<->F, B<->E, C<->D)
        Map<Integer, Integer> fwd1 = Map.of(
                0,5, 1,4, 2,3,
                3,2, 4,1, 5,0
        );
        Map<Integer, Integer> back1 = fwd1;

        Rotor r1 = new RotorImpl(1, fwd1, back1,
                0,     // initial position
                3     // notch at D
        );

        // === ROTOR 2 === (just example wiring)
        Map<Integer, Integer> fwd2 = Map.of(
                0,1, 1,2, 2,3,
                3,4, 4,5, 5,0
        );
        Map<Integer, Integer> back2 = Map.of(
                1,0, 2,1, 3,2,
                4,3, 5,4, 0,5
        );

        Rotor r2 = new RotorImpl(2, fwd2, back2,
                1, // initial pos B
                4 // notch E
        );

        // === ROTOR 3 === (example)
        Map<Integer, Integer> fwd3 = Map.of(
                0,3, 1,0, 2,5, 3,2, 4,1, 5,4
        );
        Map<Integer, Integer> back3 = new HashMap<>();
        fwd3.forEach((k,v)-> back3.put(v,k));

        Rotor r3 = new RotorImpl(3, fwd3, back3,
                2, // initial pos C
                0 // notch A
        );

        // Reflector
        Map<Integer, Integer> ref = Map.of(
                0,3, 3,0,
                1,4, 4,1,
                2,5, 5,2
        );
        Reflector reflector = new ReflectorImpl(ref);

        List<Rotor> rotors = List.of(r1, r2, r3);
        List<Integer> positions = List.of(0,1,2);

        Code code = new CodeImpl(rotors, positions, reflector);

        Machine machine = new MachineImpl(keyboard);
        machine.setCode(code);

        String input = "ABCDEF";
        StringBuilder output = new StringBuilder();

        for (char c : input.toCharArray()) {
            output.append(machine.process(c));
        }

        System.out.println("Input : " + input);
        System.out.println("Output: " + output);

    }
}


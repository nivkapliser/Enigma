package mta.patmal.enigma.machine.component.reflector;
import java.util.HashMap;
import java.util.Map;

public class ReflectorImpl implements Reflector {
    private final Map<Integer, Integer> wiring;

    public ReflectorImpl(Map<Integer, Integer> wiring) {
        if (wiring == null || wiring.isEmpty()) {
            throw new IllegalArgumentException("Wiring cannot be null or empty");
        }
        this.wiring = wiring;
    }

    @Override
    public int process(int input){
        Integer output = wiring.get(input);
        if (output == null) {
            throw new IllegalArgumentException("Invalid input for reflector" + input);
        }
        return output;
    }
}

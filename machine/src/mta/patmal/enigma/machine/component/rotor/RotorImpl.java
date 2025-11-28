package mta.patmal.enigma.machine.component.rotor;

import java.util.Map;

public class RotorImpl implements Rotor {
    private int id;
    private Map<Integer, Integer> forwardWiring;
    private Map<Integer, Integer> backwardWiring;
    private int position;
    private int notch;
    private int ringSetting;

    public RotorImpl(
            int id,
            Map<Integer, Integer> forwardWiring,
            Map<Integer, Integer> backwardWiring,
            int position,
            int notch,
            int ringSetting
    ) {
        this.id = id;
        this.forwardWiring = forwardWiring;
        this.backwardWiring = backwardWiring;
        this.position = position;
        this.notch = notch;
        this.ringSetting = ringSetting;
    }

    @Override
    public int process(int input, Direction direction) {
        Map<Integer, Integer> wiring = (direction == Direction.FORWARD) ? forwardWiring : backwardWiring;
        return processWithWiring(input, wiring);
    }

    private int processWithWiring(int input, Map<Integer, Integer> wiring) {
        int size = wiring.size();
        int shift = (position - ringSetting + size) % size;
        int contact = (input + shift) % size;
        int result = (wiring.get(contact) - shift + size) % size;
        return result >= 0 ? result : result + size;
    }

    @Override
    public boolean advance() {
        boolean reachedNotch = position == notch;
        position = (position + 1) % forwardWiring.size();
        return reachedNotch;
    }

}

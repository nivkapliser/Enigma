package mta.patmal.enigma.machine.component.keyboard;

public class KeyboardImpl {
    private final String alphabet;
    public KeyboardImpl(String alphabet) {
        if (alphabet == null || alphabet.isEmpty()) {
            throw new IllegalArgumentException("Alphabet cannot be empty");
        }
        this.alphabet = alphabet;
    }
    public int processChar(char input) {
        int index = alphabet.indexOf(input);
        if (index == -1) {
            throw new IllegalArgumentException("Input character not in alphabet");
        }
        return index;
    }
    public char lightALamp(int input) {
        if (input < 0 || input >= alphabet.length()) {
            throw new IllegalArgumentException("Input index out of bounds");
        }
        return alphabet.charAt(input);
    }
}

package mta.patmal.enigma.engine;

import mta.patmal.enigma.dto.MachineData;
import mta.patmal.enigma.engine.loader.XmlLoader;
import mta.patmal.enigma.engine.loader.RomanNumeralUtils;
import mta.patmal.enigma.machine.component.code.Code;
import mta.patmal.enigma.machine.component.machine.Machine;
import mta.patmal.enigma.machine.component.machine.MachineImpl;
import mta.patmal.enigma.machine.component.reflector.Reflector;
import mta.patmal.enigma.machine.component.rotor.Rotor;

import java.util.List;

public class EngineImpl implements Engine{

    private Machine machine;
    private final XmlLoader xmlLoader = new XmlLoader();
    private int totalRotors;
    private int totalReflectors;
    private int messagesProcessed;
    private Code originalCode;
    // private StatisticsManager statisticsManager;
    // private Repository repository; why not machine?

    @Override
    public void loadXml(String path) {
        try {
            this.machine = xmlLoader.loadMachineFromXml(path);
            this.totalRotors = xmlLoader.getTotalRotorCount();
            this.totalReflectors = xmlLoader.getTotalReflectorCount();
            this.messagesProcessed = 0;
            if (this.machine instanceof MachineImpl) {
                this.originalCode = ((MachineImpl) this.machine).getCode();
            } else {
                this.originalCode = null;
            }
        } catch (Exception e) { // need to narrow down
            e.printStackTrace();
        }
        System.out.println("XML file loaded successfully!"); // should be in console
    }

    @Override
    public MachineData showMachineData() {
        if (machine == null) { // should be exception that catches in console.
            System.out.println("No machine loaded. Please load an XML file first.");
            return null;
        }

        MachineData machineData = new MachineData(totalRotors, totalReflectors, messagesProcessed);

        MachineImpl machineImpl = (machine instanceof MachineImpl)
                ? (MachineImpl) machine
                : null;

        Code currentCode = (machineImpl != null) ? machineImpl.getCode() : null;
//
//        // should be in the console
//        System.out.println("Original code configuration:");
//        //
//
        if (originalCode != null && machineImpl != null) {
            String stringOriginalCode = formatCodeConfiguration(originalCode, machineImpl);
            machineData.setOriginalCode(stringOriginalCode);
        } else {
            machineData.setOriginalCode(null);
        }
//            System.out.println(formatCodeConfiguration(originalCode, machineImpl));
//        } else {
//            System.out.println("No original code configured."); // should be exception
//        }
//
//        // should be in the console
//        System.out.println("Current code configuration:");
//        //
//
        if (currentCode != null && machineImpl != null) {
            String stringCurrentCode = formatCodeConfiguration(currentCode, machineImpl);
            machineData.setCurrentCode(stringCurrentCode);
        } else {
            machineData.setCurrentCode(null);
        }

        return machineData;
//            System.out.println(formatCodeConfiguration(currentCode, machineImpl));
//        } else {
//            System.out.println("No current code configured."); // should be exception
//        }
    }

    @Override
    public void codeManual() {

    }

    @Override
    public void codeAutomatic() {

    }

    @Override
    public String process(String input) {
        char[] result = new char[input.length()];
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            result[i] = machine.process(c);
        }

        messagesProcessed++;

        return new String(result);

    }

    @Override
    public void statistics() {

    }

    private String formatCodeConfiguration(Code code, MachineImpl machineImpl) {
        if (code == null) {
            return "No code configured.";
        }

        List<Rotor> rotors = code.getRotors();
        Reflector reflector = code.getReflector();
        int alphabetSize = machineImpl.getAlphabetSize();

        StringBuilder sb = new StringBuilder();

        // Rotor IDs section, from left (last in list) to right (first in list)
        sb.append("<");
        for (int i = rotors.size() - 1; i >= 0; i--) {
            sb.append(rotors.get(i).getId());
            if (i != 0) {
                sb.append(",");
            }
        }
        sb.append(">");

        // Positions and notch distances
        sb.append("<");
        for (int i = rotors.size() - 1; i >= 0; i--) {
            Rotor rotor = rotors.get(i);
            int position = rotor.getPosition();
            char windowChar = machineImpl.indexToChar(position);
            int distance = (rotor.getNotch() - position + alphabetSize) % alphabetSize;

            sb.append(windowChar)
              .append("(")
              .append(distance)
              .append(")");

            if (i != 0) {
                sb.append(",");
            }
        }
        sb.append(">");

        // Reflector section
        String romanId = RomanNumeralUtils.intToRoman(reflector.getId());
        sb.append("<").append(romanId).append(">");

        return sb.toString();
    }
}

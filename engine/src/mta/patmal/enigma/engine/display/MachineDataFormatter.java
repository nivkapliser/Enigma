package mta.patmal.enigma.engine.display;

import mta.patmal.enigma.dto.MachineData;
import mta.patmal.enigma.engine.loader.RomanNumeralUtils;
import mta.patmal.enigma.machine.component.code.Code;
import mta.patmal.enigma.machine.component.machine.MachineImpl;
import mta.patmal.enigma.machine.component.reflector.Reflector;
import mta.patmal.enigma.machine.component.rotor.Rotor;

import java.util.List;

public class MachineDataFormatter {

    public MachineData createMachineData(MachineImpl machineImpl, Code originalCode, 
                                         int totalRotors, int totalReflectors, int messagesProcessed) {
        MachineData machineData = new MachineData(totalRotors, totalReflectors, messagesProcessed);

        Code currentCode = (machineImpl != null) ? machineImpl.getCode() : null;

        if (originalCode != null && machineImpl != null) {
            String stringOriginalCode = formatCodeConfiguration(originalCode, machineImpl);
            machineData.setOriginalCode(stringOriginalCode);
        } else {
            machineData.setOriginalCode(null);
        }

        if (currentCode != null && machineImpl != null) {
            String stringCurrentCode = formatCodeConfiguration(currentCode, machineImpl);
            machineData.setCurrentCode(stringCurrentCode);
        } else {
            machineData.setCurrentCode(null);
        }

        return machineData;
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


package mta.patmal.enigma.engine;

import mta.patmal.enigma.dto.MachineData;
import mta.patmal.enigma.machine.component.machine.Machine;

public interface Engine {

    void loadXml(String path);
    MachineData showMachineData();
    void codeManual(/*args*/);
    void codeAutomatic();
    String process(String input);
    void statistics();
}

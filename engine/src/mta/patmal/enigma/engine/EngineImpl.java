package mta.patmal.enigma.engine;

import mta.patmal.enigma.machine.component.machine.Machine;

public class EngineImpl implements Engine{

    private Machine machine;
    // private LoadManager loadManager;
    // private StatisticsManager statisticsManager;
    // private Repository repository; why not machine?

    @Override
    public void loadXml(String path) {

    }

    @Override
    public void showMachineData() {

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

        return new String(result);

    }

    @Override
    public void statistics() {

    }
}

package mta.patmal.enigma.engine;

import mta.patmal.enigma.dto.MachineData;
import mta.patmal.enigma.engine.codeconfig.AutomaticCodeConfigurator;
import mta.patmal.enigma.engine.codeconfig.ManualCodeConfigurator;
import mta.patmal.enigma.engine.display.MachineDataFormatter;
import mta.patmal.enigma.engine.loader.XmlLoader;
import mta.patmal.enigma.machine.component.code.Code;
import mta.patmal.enigma.machine.component.machine.Machine;
import mta.patmal.enigma.machine.component.machine.MachineImpl;

public class EngineImpl implements Engine{

    private Machine machine;
    private final XmlLoader xmlLoader = new XmlLoader();
    private final MachineDataFormatter dataFormatter = new MachineDataFormatter();
    private int totalRotors;
    private int totalReflectors;
    private int messagesProcessed;
    private Code originalCode;
    private String abc;
    // private StatisticsManager statisticsManager;
    // private Repository repository; why not machine?

    @Override
    public void loadXml(String path) {
        try {
            this.machine = xmlLoader.loadMachineFromXml(path);
            this.totalRotors = xmlLoader.getTotalRotorCount();
            this.totalReflectors = xmlLoader.getTotalReflectorCount();
            this.messagesProcessed = 0;
            this.abc = xmlLoader.getABC();
            // Machine is created without code - code will be set later by user
            this.originalCode = null;
        } catch (Exception e) { // need to narrow down
            e.printStackTrace();
        }
        System.out.println("XML file loaded successfully!"); // should be in console
    }

    @Override
    public MachineData showMachineData() {
        if (machine == null) {
            System.out.println("Error: No machine loaded. Please load an XML file first (command 1).");
            return null;
        }

        MachineImpl machineImpl = (machine instanceof MachineImpl)
                ? (MachineImpl) machine
                : null;

        if (machineImpl == null) {
            System.out.println("Error: Machine is not a valid MachineImpl instance.");
            return null;
        }

        return dataFormatter.createMachineData(machineImpl, originalCode, 
                totalRotors, totalReflectors, messagesProcessed);
    }

    @Override
    public void codeManual() {
        if (machine == null || abc == null) {
            System.out.println("Error: No machine loaded. Please load an XML file first (command 1).");
            return;
        }

        ManualCodeConfigurator configurator = new ManualCodeConfigurator(
                machine, xmlLoader, abc, totalRotors, totalReflectors);
        boolean success = configurator.configure();
        
        // Set originalCode the first time a code is successfully configured
        if (success && originalCode == null && machine instanceof MachineImpl) {
            this.originalCode = ((MachineImpl) machine).getCode();
        }
    }

    @Override
    public void codeAutomatic() {
        if (machine == null || abc == null) {
            System.out.println("Error: No machine loaded. Please load an XML file first (command 1).");
            return;
        }

        AutomaticCodeConfigurator configurator = new AutomaticCodeConfigurator(
                machine, xmlLoader, abc, totalRotors, totalReflectors);
        boolean success = configurator.configure();
        
        // Set originalCode the first time a code is successfully configured
        if (success && originalCode == null && machine instanceof MachineImpl) {
            this.originalCode = ((MachineImpl) machine).getCode();
        }
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
}

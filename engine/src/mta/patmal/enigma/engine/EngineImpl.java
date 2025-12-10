package mta.patmal.enigma.engine;

import mta.patmal.enigma.dto.MachineData;
import mta.patmal.enigma.engine.codeconfig.AutomaticCodeConfigurator;
import mta.patmal.enigma.engine.codeconfig.ManualCodeConfigurator;
import mta.patmal.enigma.engine.display.MachineDataFormatter;
import mta.patmal.enigma.engine.loader.XmlLoader;
import mta.patmal.enigma.machine.component.code.Code;
import mta.patmal.enigma.machine.component.code.CodeImpl;
import mta.patmal.enigma.machine.component.machine.Machine;
import mta.patmal.enigma.machine.component.machine.MachineImpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EngineImpl implements Engine{

    private Machine machine;
    private final XmlLoader xmlLoader = new XmlLoader();
    private final MachineDataFormatter dataFormatter = new MachineDataFormatter(xmlLoader);
    private int totalRotors;
    private int totalReflectors;
    private int messagesProcessed;
    private String originalCodeString;
    private String abc;
    private Code originalCode;
    private final Map<String, List<HistoryEntry>> history = new LinkedHashMap<>();
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
            this.originalCodeString = null;
            this.originalCode = null;
            this.history.clear();
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

        return dataFormatter.createMachineData(machineImpl, originalCodeString, 
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
        if (success && machine instanceof MachineImpl machineImpl) {
            Code currentCode= machineImpl.getCode();
            var rotors = currentCode.getRotors();
            var reflector = currentCode.getReflector();
            var positions = new java.util.ArrayList<Integer>();
            for (var rotor : rotors) {
                positions.add(rotor.getPosition());
            }
            this.originalCode = new CodeImpl(rotors, positions, reflector);
            this.originalCodeString = dataFormatter.formatCode(currentCode, machineImpl);

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
        if (success && machine instanceof MachineImpl machineImpl) {
            Code currentCode= machineImpl.getCode();
            var rotors = currentCode.getRotors();
            var reflector = currentCode.getReflector();
            var positions = new java.util.ArrayList<Integer>();
            for (var rotor : rotors) {
                positions.add(rotor.getPosition());
            }
            this.originalCode = new CodeImpl(rotors, positions, reflector);
            this.originalCodeString = dataFormatter.formatCode(currentCode, machineImpl);

        }
    }

    @Override
    public String process(String input) {
        // Validate machine is loaded
        if (machine == null) {
            throw new IllegalStateException("No machine loaded. Please load an XML file first (command 1).");
        }

        // Validate code is configured
        if (originalCodeString == null) {
            throw new IllegalStateException("No code configured. Please configure a code first (command 3 or 4).");
        }

        // Validate input is not null or empty
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be empty.");
        }

        // Validate all characters are in ABC
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (abc.indexOf(c) == -1) {
                throw new IllegalArgumentException("Invalid character '" + c + 
                        "' at position " + (i + 1) + ". Character is not in the ABC: " + abc);
            }
        }

        long start = System.nanoTime();

        // Process the input
        char[] result = new char[input.length()];
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            result[i] = machine.process(c);
        }

        long duration = System.nanoTime() - start;
        String output = new String(result);

        messagesProcessed++;

        String codeKey = originalCodeString;
        history.computeIfAbsent(codeKey, k -> new ArrayList<>())
                .add(new HistoryEntry(input, output, duration));



        return new String(result);

    }

    @Override
    public void statistics() {
        if (machine == null) {
            System.out.println("Error: No machine loaded. Please load an XML file first (command 1).");
            return;
        }

        if (originalCodeString == null) {
            System.out.println("No code configured. Please configure a code first (command 3 or 4).");
            return;
        }

        if (history.isEmpty()) {
            System.out.println("No history available yet.");
            return;
        }

        int index = 1;
        for (Map.Entry<String, List<HistoryEntry>> entry : history.entrySet()) {
            String code = entry.getKey();
            List<HistoryEntry> entries = entry.getValue();

            System.out.println("Code configuration: " + code);
            for (HistoryEntry h : entries) {
                System.out.printf(
                        "%d. <%s> --> <%s> (%d nano-seconds)%n",
                        index++,
                        h.getInput(),
                        h.getOutput(),
                        h.getDurationNanos()
                );
            }
            System.out.println();
        }


    }

    @Override
    public void resetCurrentCode() {
        if (machine == null) {
            throw new IllegalStateException("No machine loaded. Please load an XML file first (command 1).");
        }

        if (originalCode == null) {
            throw new IllegalStateException("No original code configured. Please configure a code first (command 3 or 4).");
        }

        if (!(machine instanceof MachineImpl machineImpl)) {
            throw new IllegalStateException("Machine is not a valid MachineImpl instance.");
        }

        machineImpl.setCode(originalCode);
    }
}

package mta.patmal.enigma.engine;

import mta.patmal.enigma.machine.component.machine.Machine;
import javax.xml.bind.JAXBContext;

import java.io.File;
import java.io.FileNotFoundException;

public class EngineImpl implements Engine{

    private Machine machine;
    // private LoadManager loadManager;
    // private StatisticsManager statisticsManager;
    // private Repository repository; why not machine?

    @Override
    public void loadXml(String path) {
//        File xmlFile = new File(path);
//        if (!xmlFile.exists()) {
//            throw new FileNotFoundException("XML file not found: " + path);
//        }
//
//        JAXBContext jaxbContext = JAXBContext.newInstance(/*class*/);
//        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
//

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

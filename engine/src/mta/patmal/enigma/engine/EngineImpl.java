package mta.patmal.enigma.engine;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import mta.patmal.enigma.engine.jaxb.generated.BTEEnigma;
import mta.patmal.enigma.engine.loader.JaxbLoader;
import mta.patmal.enigma.engine.loader.XmlLoader;
import mta.patmal.enigma.machine.component.machine.Machine;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.FileNotFoundException;

public class EngineImpl implements Engine{

    private Machine machine;
    private final XmlLoader xmlLoader = new XmlLoader();
    // private StatisticsManager statisticsManager;
    // private Repository repository; why not machine?

    @Override
    public void loadXml(String path) {
        System.out.println("Loading XML file...");
        try {
            this.machine = xmlLoader.loadMachineFromXml(path);
        } catch (Exception e) { // need to narrow down
            e.printStackTrace();
        }
        System.out.println("XML file loaded successfully!");
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

package mta.patmal.enigma.engine.loader;

import jakarta.xml.bind.JAXBException;
import mta.patmal.enigma.engine.jaxb.generated.BTEEnigma;
import mta.patmal.enigma.machine.component.machine.Machine;

import java.io.File;
import java.io.FileNotFoundException;

public class XmlLoader {
    private static final JaxbLoader jaxbLoader = new JaxbLoader(); // final?
    JaxbTranslator jaxbTranslator = new JaxbTranslator();

    public Machine loadMachineFromXml(String xmlPath) throws FileNotFoundException, JAXBException{ // add more exceptions
        try {
            validateXmlPath(xmlPath);
            File xmlFile = new File(xmlPath);
            validateXmlFile(xmlFile);
            BTEEnigma bteEnigma = jaxbLoader.loadXmlUsingJaxb(xmlFile);
            jaxbTranslator.validateMachineFormat(bteEnigma);
            Machine enigmaMachine = jaxbTranslator.translateToMachine(bteEnigma);
            return enigmaMachine;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } // add more exceptions
    }

    private void validateXmlPath(String xmlPath) throws FileNotFoundException{
        if (xmlPath == null || xmlPath.trim().isEmpty()) {
            throw new FileNotFoundException("XML path is empty");
        }
        if (!xmlPath.toLowerCase().endsWith(".xml")) {
            throw new IllegalArgumentException("File is not an XML (.xml): " + xmlPath);
        }
    }

    private void validateXmlFile(File xmlFile) throws FileNotFoundException{
        if (!xmlFile.exists() || !xmlFile.isFile()) {
            throw new FileNotFoundException("XML file not found: " + xmlFile.getAbsolutePath());
        }
    }

}

package mta.patmal.enigma.engine.loader;

import jakarta.xml.bind.JAXBException;
import mta.patmal.enigma.engine.jaxb.generated.BTEEnigma;
import mta.patmal.enigma.machine.component.machine.Machine;

import java.io.File;
import java.io.FileNotFoundException;

public class XmlLoader {
    private static final String XML_FILE_EXTENSION = ".xml";
    
    private final JaxbLoader jaxbLoader;
    private final JaxbTranslator jaxbTranslator;
    private final XmlValidator xmlValidator;

    public XmlLoader() {
        this(new JaxbLoader(), new JaxbTranslator(), new XmlValidator());
    }

    public XmlLoader(JaxbLoader jaxbLoader, JaxbTranslator jaxbTranslator, XmlValidator xmlValidator) {
        this.jaxbLoader = jaxbLoader;
        this.jaxbTranslator = jaxbTranslator;
        this.xmlValidator = xmlValidator;
    }

    public Machine loadMachineFromXml(String xmlPath) throws FileNotFoundException, JAXBException, IllegalArgumentException, IllegalStateException {
        validateXmlPath(xmlPath);
        File xmlFile = new File(xmlPath);
        validateXmlFile(xmlFile);
        BTEEnigma bteEnigma = jaxbLoader.load(xmlFile);
        xmlValidator.validateMachineFormat(bteEnigma);
        return jaxbTranslator.translateToMachine(bteEnigma);
    }

    private void validateXmlPath(String xmlPath) throws FileNotFoundException {
        if (xmlPath == null || xmlPath.trim().isEmpty()) {
            throw new FileNotFoundException("XML path is empty");
        }
        if (!xmlPath.toLowerCase().endsWith(XML_FILE_EXTENSION)) {
            throw new IllegalArgumentException("File is not an XML (" + XML_FILE_EXTENSION + "): " + xmlPath);
        }
    }

    private void validateXmlFile(File xmlFile) throws FileNotFoundException {
        if (!xmlFile.exists() || !xmlFile.isFile()) {
            throw new FileNotFoundException("XML file not found: " + xmlFile.getAbsolutePath());
        }
    }

}

package mta.patmal.enigma.engine.loader;

import jakarta.xml.bind.JAXB;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import mta.patmal.enigma.engine.jaxb.generated.BTEEnigma;

import java.io.File;
import java.io.FileNotFoundException;

public class JaxbLoader {
    private static final String JAXB_XML_PACKAGE_NAME = "engine.jaxb.generated";

    public BTEEnigma loadXmlUsingJaxb(File xmlFile) throws JAXBException{

        JAXBContext jaxbContext = JAXBContext.newInstance(JAXB_XML_PACKAGE_NAME);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (BTEEnigma) jaxbUnmarshaller.unmarshal(xmlFile);
    }
}

package mta.patmal.enigma.engine;

public interface Engine {

    void loadXml(String path);
    void showMachineData();
    void codeManual(/*args*/);
    void codeAutomatic();
    String process(String input);
    void statistics();
}

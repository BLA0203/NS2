package ann.app;

import ann.network.NeuralNetwork;
import ann.parser.XMLParser;
import ann.server.DriverInterface;
import ann.server.RaceConnector;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Neural network homework 2 - BLA0203");
        XMLParser xmlParser = new XMLParser();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Please select one of three options to continue (1 - lekar.xml, 2 - own example, 3 - racer).\n");
            String selection = reader.readLine();
            int option = Integer.parseInt(selection);
            switch (option) {
                case 1:
                    xmlParser.parse("./resource/lekar.xml");
                    NeuralNetwork neuralNetwork = new NeuralNetwork(xmlParser);
                    neuralNetwork.trainNetwork(100000);
                    break;
                case 2:
                    xmlParser.parse("./resource/zamestnani.xml");
                    NeuralNetwork neuralNetworkMine = new NeuralNetwork(xmlParser);
                    neuralNetworkMine.trainNetwork(200000);
                    break;
                case 3:
                    System.out.println("Please select either 1 (train network for racer) or 2 (connect to race and run) - hard configurated\n");
                    selection = reader.readLine();
                    //TRAIN
                    if (Integer.parseInt(selection) == 1) {
                        xmlParser.parse("./resource/racer.xml");
                        NeuralNetwork neuralNetworkCar = new NeuralNetwork(xmlParser);
                        neuralNetworkCar.trainNetwork(200000);
                        //train and race, before import je otestovany
                        xmlParser.exportNeuronNetworkConfig("./resource/learned/raceANN.xml", neuralNetworkCar);
                        //neuralNetworkCar.runRace("java.cs.vsb.cz", 9460, "Zavod3", "bla0203", null);
                    }
                    //RACE
                    else if (Integer.parseInt(selection) == 2) {
                        XMLParser loadANN = new XMLParser();
                        //TODO
                        /*
                        loadANN.importNeuronNetworkConfig("./resource/learned/raceANN.xml");
                        NeuralNetwork race = new NeuralNetwork(loadANN, true);
                        race.runRace("java.cs.vsb.cz", 9460, "Zavod", "bla0203", "fabia");
                        */
                    }
                    //NOTHING, WRONG INPUT
                    else {
                        System.out.println("Undefined option!\n");
                    }
                    break;
                default:
                    break;
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

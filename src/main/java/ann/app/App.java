package ann.app;

import ann.network.NeuralNetwork;
import ann.parser.XMLParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Neural network homework 2 - BLA0203");
        XMLParser xmlParser = new XMLParser();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Please select one of three options to continue (1 - lekar.xml, 2 - own example, 3 - racer).");
            String selection = reader.readLine();
            int option = Integer.parseInt(selection);
            switch (option) {
                case 1:
                    xmlParser.parse("./resource/lekar.xml");
                    NeuralNetwork neuralNetwork = new NeuralNetwork(xmlParser, false);
                    neuralNetwork.trainNetwork(100000);
                    break;
                case 2:
                    xmlParser.parse("./resource/zamestnani.xml");
                    NeuralNetwork neuralNetworkMine = new NeuralNetwork(xmlParser, false);
                    neuralNetworkMine.trainNetwork(200000);
                    break;
                case 3:
                    System.out.println("Please select either 1 (train network for racer) or 2 (connect to race and run) - hard configurated");
                    selection = reader.readLine();
                    //TRAIN
                    if (Integer.parseInt(selection) == 1) {
                        xmlParser.parse("./resource/car.xml");
                        NeuralNetwork neuralNetworkCar = new NeuralNetwork(xmlParser, true);
                        neuralNetworkCar.trainNetwork(500000);
                        xmlParser.exportNeuronNetworkConfig("./resource/learned/raceANN.xml", neuralNetworkCar);
                        System.out.println("Neural network is fully trained, would you like to exit program or enter race? (y/n)");
                        selection = reader.readLine();
                        if (selection.equalsIgnoreCase("Y")) {
                            System.out.println("Please enter name of race you want to connect to " +
                                    "(there is no check for correct race name, so make sure to spell name correctly!): ");
                            selection = reader.readLine();
                            neuralNetworkCar.runRace("java.cs.vsb.cz", 9460, selection, "bla0203", null);
                        } else {
                            System.out.println("Exiting...\n");
                            return;
                        }
                    }
                    //RACE
                    else if (Integer.parseInt(selection) == 2) {
                        XMLParser loadANN = new XMLParser();
                        xmlParser.parse("./resource/car.xml");
                        NeuralNetwork neuralNetworkCar = new NeuralNetwork(xmlParser, true);
                        xmlParser.importNeuronNetworkConfig("./resource/learned/raceANN.xml", neuralNetworkCar);
                        System.out.println("Please enter name of race you want to connect to " +
                                "(there is no check for correct race name, so make sure to spell name correctly!): ");
                        selection = reader.readLine();
                        neuralNetworkCar.runRace("java.cs.vsb.cz", 9460, selection, "bla0203", null);
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

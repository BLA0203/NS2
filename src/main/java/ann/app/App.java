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
            System.out.println("please select one of three options to continue (1 - lekar.xml, 2 - own example, 3 - racer.\n");
            String selection = reader.readLine();
            int v = Integer.parseInt(selection);
            switch (v) {
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

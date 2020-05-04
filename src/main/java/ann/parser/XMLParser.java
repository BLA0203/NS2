package ann.parser;

import ann.network.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XMLParser implements Parser {
    public Double learningRate;
    public int layerCount;
    public int inputCount;
    public int outputCount;
    public List<Integer> neuronsInLayer;
    public List<String> inputNames;
    public List<String> outputNames;
    //
    public List<TrainingItem> trainingSet;
    public List<TestingItem> testingSet;
    //
    public List<NeuralNetworkLayer> neuralNetworkLayers;
    // parser helper objects
    private List<List<String>> inputControlCheck;

    public XMLParser() {
        neuronsInLayer = new ArrayList<Integer>();
        inputNames = new ArrayList<String>();
        outputNames = new ArrayList<String>();
        trainingSet = new ArrayList<TrainingItem>();
        testingSet = new ArrayList<TestingItem>();
        neuralNetworkLayers = new ArrayList<NeuralNetworkLayer>();
        inputControlCheck = new ArrayList<List<String>>();
    }


    public void parse(String pathToFile) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(pathToFile));
        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement();
        NodeList rootList = root.getChildNodes();

        for (int index = 0; index < rootList.getLength(); index++) {
            Node rootNode = rootList.item(index);
            //INPUT DESCRIPTION
            if (rootNode.getNodeName() == "inputDescriptions") {
                Element inputDesElement = (Element) rootNode;
                NodeList inputDesList = inputDesElement.getChildNodes();
                for (int i = 0; i < inputDesList.getLength(); i++) {
                    //ONE INPUT NODE
                    Node desInputNode = inputDesList.item(i);
                    if (desInputNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element desInputElement = (Element) desInputNode;
                        NodeList desInputElementList = desInputElement.getChildNodes();
                        List<String> inputData = new ArrayList<String>();
                        for (int j = 0; j < desInputElementList.getLength(); j++) {
                            Node attributeNode = desInputElementList.item(j);
                            if (attributeNode.getNodeName() == "maximum") {
                                inputData.add(attributeNode.getFirstChild().getNodeValue());
                            } else if (attributeNode.getNodeName() == "minimum") {
                                inputData.add(attributeNode.getFirstChild().getNodeValue());
                            } else if (attributeNode.getNodeName() == "name") {
                                inputData.add(attributeNode.getFirstChild().getNodeValue());
                                inputNames.add(attributeNode.getFirstChild().getNodeValue());
                            }
                        }
                        this.inputControlCheck.add(inputData);
                    }
                }
            }
            //INPUTS COUNT
            if (rootNode.getNodeName() == "inputsCount") {
                this.inputCount = Integer.parseInt(rootNode.getFirstChild().getNodeValue());
            }
            //LAYERS COUNT
            if (rootNode.getNodeName() == "layersCount") {
                this.layerCount = Integer.parseInt(rootNode.getFirstChild().getNodeValue());
            }
            //LEARNING RATE
            if (rootNode.getNodeName() == "learningRate") {
                this.learningRate = Double.parseDouble(rootNode.getFirstChild().getNodeValue());
            }
            //NEURONS IN LAYER
            if (rootNode.getNodeName() == "neuronInLayersCount") {
                Element neuronInLayerElement = (Element) rootNode;
                NodeList neuronInLayerElementList = neuronInLayerElement.getChildNodes();
                for (int i = 0; i < neuronInLayerElementList.getLength(); i++) {
                    Node currLayerNode = neuronInLayerElementList.item(i);
                    if (currLayerNode.getNodeType() == Node.ELEMENT_NODE) {
                        this.neuronsInLayer.add(Integer.parseInt(currLayerNode.getFirstChild().getNodeValue()));
                    }
                }
            }
            //OUTPUT DESCRIPTION
            if (rootNode.getNodeName() == "outputDescriptions") {
                Element outputDes = (Element) rootNode;
                NodeList outputDesList = outputDes.getChildNodes();
                for (int i = 0; i < outputDesList.getLength(); i++) {
                    Node currDes = outputDesList.item(i);
                    if (currDes.getNodeType() == Node.ELEMENT_NODE) {
                        this.outputCount++;
                        this.outputNames.add(currDes.getFirstChild().getNodeValue());
                    }
                }
            }
            //TRAIN SET ----
            if (rootNode.getNodeName() == "trainSet") {
                Element trainSetElement = (Element) rootNode;
                NodeList trainSetList = trainSetElement.getChildNodes();
                for (int i = 0; i < trainSetList.getLength(); i++) {
                    //ELEMENT NODES
                    Node elementNode = trainSetList.item(i);
                    if (elementNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element elementElement = (Element) elementNode;
                        NodeList inputsList = elementElement.getChildNodes();
                        TrainingItem trainingItem = new TrainingItem();
                        for (int j = 0; j < inputsList.getLength(); j++) {
                            Node inputNode = inputsList.item(j);
                            //INPUT NODES
                            if (inputNode.getNodeName() == "inputs") {
                                Element inputElement = (Element) inputNode;
                                NodeList valueList = inputElement.getChildNodes();
                                double[] toAddVector = new double[inputCount];
                                int inputInd = 0;
                                for (int k = 0; k < valueList.getLength(); k++) {
                                    Node valueNode = valueList.item(k);
                                    //VALUE NODES
                                    if (valueNode.getNodeName() == "value") {
                                        toAddVector[inputInd] = Double.parseDouble(valueNode.getFirstChild().getNodeValue());
                                        inputInd++;
                                    }
                                }
                                trainingItem.setInputVector(toAddVector);
                            }
                            //OUTPUT NODES
                            else if (inputNode.getNodeName() == "outputs") {
                                Element outputElement = (Element) inputNode;
                                NodeList outputElementlist = outputElement.getChildNodes();
                                double[] toAddVector = new double[this.outputCount];
                                int outputInd = 0;
                                for (int k = 0; k < outputElementlist.getLength(); k++) {
                                    Node valueNode = outputElementlist.item(k);
                                    //VALUE NODES
                                    if (valueNode.getNodeName() == "value") {
                                        toAddVector[outputInd] = Double.parseDouble(valueNode.getFirstChild().getNodeValue());
                                        outputInd++;
                                    }
                                }
                                trainingItem.setOutputVector(toAddVector);
                            }
                        }
                        this.trainingSet.add(trainingItem);
                    }
                }
            }
            //TEST SET ----
            else if (rootNode.getNodeName() == "testSet") {
                Element trainSetElement = (Element) rootNode;
                NodeList trainSetList = trainSetElement.getChildNodes();
                for (int i = 0; i < trainSetList.getLength(); i++) {
                    //ELEMENT NODES
                    Node elementNode = trainSetList.item(i);
                    if (elementNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element elementElement = (Element) elementNode;
                        NodeList inputsList = elementElement.getChildNodes();
                        TestingItem testingItem = new TestingItem();
                        for (int j = 0; j < inputsList.getLength(); j++) {
                            Node inputNode = inputsList.item(j);
                            //INPUT NODES
                            if (inputNode.getNodeName() == "inputs") {
                                Element inputElement = (Element) inputNode;
                                NodeList valueList = inputElement.getChildNodes();
                                double[] toAddVector = new double[inputCount];
                                int inputInd = 0;
                                for (int k = 0; k < valueList.getLength(); k++) {
                                    Node valueNode = valueList.item(k);
                                    //VALUE NODES
                                    if (valueNode.getNodeName() == "value") {
                                        toAddVector[inputInd] = Double.parseDouble(valueNode.getFirstChild().getNodeValue());
                                        inputInd++;
                                    }
                                }
                                testingItem.setInputVector(toAddVector);
                            }
                        }
                        this.testingSet.add(testingItem);
                    }
                }
            }
        }
    }

    public void exportNeuronNetworkConfig(String path, NeuralNetwork neuralNetwork) throws Exception {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element root = document.createElement("backpropagationNeuronNet");
        document.appendChild(root);

        for (NeuralNetworkLayer currLayer : neuralNetwork.getNeuralNetworkLayers()) {
            Element currLayerXML = document.createElement("layerInNetwork");
            root.appendChild(currLayerXML);
            for (Neuron currNeuron : currLayer.getNeurons()) {
                Element currNeuronXML = document.createElement("neuron");
                currLayerXML.appendChild(currNeuronXML);
                for (int i = 0; i < currNeuron.getInputSize(); i++) {
                    Element currWeightXML = document.createElement("weight");
                    currWeightXML.appendChild(document.createTextNode(Double.toString(currNeuron.getWeightAt(i))));
                    currNeuronXML.appendChild(currWeightXML);
                }
            }
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File(path));

        transformer.transform(domSource, streamResult);
    }

    public void importNeuronNetworkConfig(String path) {

    }

}

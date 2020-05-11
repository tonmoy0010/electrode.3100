import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * This file consists of all the methods and functions essential to run the simulation
 * simulation between the Client-side and the Server-side
 * @author Tonmoy Ahmed Jitu, 44266278
 * @author Lakshmi Priya Bhuphatiraju, 45431957
 * Communication address 127.0.0.1, port 50000
 **/

public class Electrode3100 {

    private Socket socket = null;

    // Two variables input and output are used to get and write information form socket
    private BufferedReader input = null;
    private DataOutputStream output = null;

    // Server list has been initiated with minimum server numbers being one
    private Server[] serverList = new Server[1];
    private ArrayList<Server> serverArrayList = new ArrayList<Server>();
    private int allToLargest = 0;

    private String serverComms; //serverComms is short for serverCommunication messages
    private Boolean jobComplete = false;
    public String algo;

    // Establishing connection with address 127.0.0.1, Port 50000
    public Electrode3100(String address, int port) {
        try {
            socket = new Socket(address, port);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new DataOutputStream(socket.getOutputStream());
        } catch (UnknownHostException u) {
            System.out.println("Error: " + u);
        } catch (IOException i) {
            System.out.println("Error: " + i);
        }
    }


    // The method sends commands to server using bytecode since the server side is coded in C Language
    public void send(String command) {
        try {
            output.write(command.getBytes());
            output.flush();
        } catch (IOException i) {
            System.out.println("Error: " + i);
        }
    }

    /*
     * The method is used to receive commands from the server, since the Server-side sends bytecode
     * a loop has to be used to build up the string.
     */
    public String receive(){
        String command = "";
        try {
            while (!input.ready()) { //Check if its sending anymore byte code or not
            }
            while (input.ready()) {
                command += (char) input.read();
            }
            serverComms = command;
        } catch (IOException i) {
            System.out.println("Error: " + i);
        }

        return command;
    }

    /*
     * This method closes the communication between the Client and the Server
     * once the QUIT command has been send && received.
     */
    public void quit() {
        try {
            send("QUIT");
            serverComms = receive();
            if (serverComms == "QUIT") {
                input.close();
                output.close();
                socket.close();
            }
        } catch (IOException i) {
            System.out.println("Error: " + i);
        }
    }

    /*
     * The method was built using the help of the library javax.xml.parser.*
     * The method basically readys the XML file sent by the server and goes through the attribute.
     * It uses a document builder to categorize all the records according to the tags and sorts them accordingly
     * in an NodeList.
     */
    public void parseXML() {
        try {
            File systemXML = new File("system.xml");

            DocumentBuilderFactory docuFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docuBuilder = docuFactory.newDocumentBuilder();
            Document doc = docuBuilder.parse(systemXML);
            doc.getDocumentElement().normalize();

            NodeList servers = doc.getElementsByTagName("server");
            serverList = new Server[servers.getLength()];
            for (int i = 0; i < servers.getLength(); i++) {
                Element server = (Element) servers.item(i);
                String type = server.getAttribute("type");
                int limit = Integer.parseInt(server.getAttribute("limit"));
                int bootupTime = Integer.parseInt(server.getAttribute("bootupTime"));
                float rate = Float.parseFloat(server.getAttribute("rate"));
                int coreCount = Integer.parseInt(server.getAttribute("coreCount"));
                int memory = Integer.parseInt(server.getAttribute("memory"));
                int disk = Integer.parseInt(server.getAttribute("disk"));
                Server temp = new Server(i, type, limit, bootupTime, rate, coreCount, memory, disk);
                serverList[i] = temp;
            }
            findLargestServer();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // This method finds the largest server from the NodeList, which stores the data from the XML file.
    public void findLargestServer() {
        int coreCount = serverList[0].id;
        for (int i = 0; i < serverList.length; i++) {
            if (serverList[i].coreCount > serverList[coreCount].coreCount) {
                coreCount = serverList[i].id;
            }
        }
        setLargest(coreCount);
    }

    // Setter method to set the largest server.
    public void setLargest(int largest) {
        allToLargest = largest;
    }

    /*
     * Contained in this method is everything that we would want to happen when
     * running the protocol. Expectation is that run() will be called when we want
     * the sequence of interaction to occur with the server. run() requires
     * interaction with a server model.
     */
    public void run() {
        send("HELO");
        serverComms = receive();
        send("AUTH " + System.getProperty("user.name"));
        serverComms = receive();
        parseXML();
        send("REDY");
        serverComms = receive();

        if (serverComms.equals("NONE")) {
            quit();
        } else {
            while (!jobComplete) {
                if (serverComms.equals("OK")) {
                    send("REDY");
                    serverComms = receive();
                }
                if (serverComms.equals("NONE")) {
                    jobComplete = true;
                    break;
                }

                // jobs XML file is parsed to store jobs information using attributes from the XML file
                String[] jobsXML = serverComms.split("\\s+");
                Jobs jobs = new Jobs(Integer.parseInt(jobsXML[1]), Integer.parseInt(jobsXML[2]),
                        Integer.parseInt(jobsXML[3]), Integer.parseInt(jobsXML[4]), Integer.parseInt(jobsXML[5]),
                        Integer.parseInt(jobsXML[6]));

                send("RESC All");
                serverComms = receive();
                send("OK");

                serverComms = receive();


                serverArrayList = new ArrayList<Server>(); //may not need this
                while (!serverComms.equals(".")) {

                    String[] serverSCHD = serverComms.split("\\s+");
                    serverArrayList.add(
                            new Server(serverSCHD[0], Integer.parseInt(serverSCHD[1]), Integer.parseInt(serverSCHD[2]),
                                    Integer.parseInt(serverSCHD[3]), Integer.parseInt(serverSCHD[4]),
                                    Integer.parseInt(serverSCHD[5]), Integer.parseInt(serverSCHD[6])));
                    send("OK");
                    serverComms = receive();
                }

                pickAlgo(jobs);

                serverComms = receive();
            }
        }
        quit();
    }

    public void pickAlgo(Jobs jobs) {

        AlgorythmType chooseAlgo = new AlgorythmType(serverArrayList, serverList);

        Server newServer = null;
        if (algo.equals("ff")) {
            newServer = chooseAlgo.firstFit(jobs);
            send("SCHD " + jobs.id + " " + newServer.type + " " + newServer.id);
        } else if (algo.equals("wf")) {
            newServer = chooseAlgo.worstFit(jobs);
            send("SCHD " + jobs.id + " " + newServer.type + " " + newServer.id);
        } else {
            String[] jobData = serverComms.split("\\s+");
            int count = Integer.parseInt(jobData[2]);
            send("SCHD " + count + " " + serverList[allToLargest].type + " " + "0");
        }
    }

}
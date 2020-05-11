import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.*;
/**
 * This file consists of all the methods and functions essential to run the simulation
 * simulation between the Client-side and the Server-side
 * @author Tonmoy Ahmed Jitu, 44266278
 * @author Lakshmi Priya Bhuphatiraju, 45431957
 * Communication address 127.0.0.1, port 50000
 **/

public class Electrode3100 {

    Server server;
    private Socket socket = null;

    //Two variables input and output are used to get and write information form socket
    private BufferedReader input = null;
    private DataOutputStream output = null;

    //Server list has been initiated with minimum server numbers being one
    private Server[] serverList = new Server[1];
    private int allToLargest = 0;

    private String serverComm; //serverComm is short for serverCommunication messages
    private Boolean jobComplete = false;

    public Electrode3100(String address, int portNum) {
        //Establishing connection with address 127.0.0.1, Port 50000
        try {
            socket = new Socket(address, portNum);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new DataOutputStream(socket.getOutputStream());
        } catch (UnknownHostException u) {
            System.out.println("Error: " + u);
        } catch (IOException i) {
            System.out.println("Error: " + i);
        }
    }

    /**
     * The method sends commands to server using bytecode since the server side is coded in C Language
     */
    public void send(String command) {
        try {
            output.write(command.getBytes());
            output.flush();
        } catch (IOException i) {
            System.out.println("Error: " + i);
        }
    }

    /**
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
            serverComm = command;
        } catch (IOException i) {
            System.out.println("Error: " + i);
        }

        return command;
    }

    /**
     * This method closes the communication between the Client and the Server
     * once the QUIT command has been send && received.
     */
    public void quit() {
        try {
            send("QUIT");
            serverComm = receive();
            if (serverComm == "QUIT") {
                input.close();
                output.close();
                socket.close();
            }
        } catch (IOException i) {
            System.out.println("Errpr: " + i);
        }
    }

    /**
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
                String t = server.getAttribute("type");
                int l = Integer.parseInt(server.getAttribute("limit"));
                int b = Integer.parseInt(server.getAttribute("bootupTime"));
                float r = Float.parseFloat(server.getAttribute("rate"));
                int c = Integer.parseInt(server.getAttribute("coreCount"));
                int m = Integer.parseInt(server.getAttribute("memory"));
                int d = Integer.parseInt(server.getAttribute("disk"));
                Server temp = new Server(i, t, l, b, r, c, m, d);
                serverList[i] = temp;
            }
            findLargestServer();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     *This method finds the largest srever from the NodeList.
     */
    public void findLargestServer() {
        int coreCount = serverList[0].id;
        for (int i = 0; i < serverList.length; i++) {
            if (serverList[i].coreCount > serverList[coreCount].coreCount) {
                coreCount = serverList[i].id;
            }
        }
        setLargest(coreCount);
    }

    /**
     *Setter method to set the largest server.
     */
    public void setLargest(int largest) {
        allToLargest = largest;
    }

    /**
     * This method initialtes the simulation
     */
    public void run() {
        send("HELO");
        serverComm = receive();
        send("AUTH " + System.getProperty("user.name"));
        serverComm = receive();
        parseXML();
        send("REDY");
        serverComm = receive();

        if (serverComm.equals("NONE")) {
            quit();
        } else {
            while (!jobComplete) {
                if (serverComm.equals("OK")) {
                    send("REDY");
                    serverComm = receive();
                }
                if (serverComm.equals("NONE")) {
                    jobComplete = true;
                    break;
                }
                /**
                 * This part of the code is crucial as this is where the job schedulng occurs
                 * the job is send to the largets server
                 */
                String[] jobsXML = serverComm.split("\\s+"); //Splitting string using single whitespace regex "\\s"
                int currJobID = Integer.parseInt(jobsXML[2]);
                send("SCHD " + currJobID + " " + serverList[allToLargest].type + " " + "0");
                serverComm = receive();
            }
        }
        quit();
    }
}

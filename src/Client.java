/**
 * This file consists of the client class which is used to initiate the
 * simulation between the Client-side and the Server-side
 * @author Tonmoy Ahmed Jitu, 44266278
 * @author Lakshmi Priya Bhuphatiraju, 45431957
 * Communication address 127.0.0.1, port 50000
 **/
public class Client {

    public static void main(String args[]) {

        Electrode3100 electrode3100 = new Electrode3100("127.0.0.1", 50000);

        if (args.length == 2) {
            if (args[0].equals("-a")) {
                if (args[1].equals("ff")) {
                    electrode3100.algo = "ff";
                } else if (args[1].equals("wf")) {
                    electrode3100.algo = "wf";
                }
            }
            electrode3100.run();
        }
    }
}
import java.io.IOException;
/**
 * This file consists of the client class which is used to initiate the
 * simulation between the Client-side and the Server-side
 * @author Tonmoy Ahmed Jitu, 44266278
 * @author Lakshmi Priya Bhuphatiraju, 45431957
 * Communication address 127.0.0.1, port 50000
 **/
public class Client {

    public static final String SERVER =  "127.0.0.1";
    public static final int PORT = 50000;

    public static void main(String[] args) throws IOException {
        Electrode3100 electrode3100 = new Electrode3100(SERVER, PORT);
        electrode3100.run();
    }
}

/**
 * Server Class to store all the attributes from server XML.
 */
public class Server {

    public int id;
    public String type;
    public int limit;
    public int bootupTime;
    public float rate;
    public int coreCount;
    public int memory;
    public int disk;
    public int state;
    public int availableTime;

    Server(int id, String type, int limit, int bootupTime, float rate, int coreCount, int memory, int disk) {
        this.id = id;
        this.type = type;
        this.limit = limit;
        this.bootupTime = bootupTime;
        this.rate = rate;
        this.coreCount = coreCount;
        this.memory = memory;
        this.disk = disk;
    }

    Server(String type, int id, int state, int availableTime, int coreCount, int memory, int disk) {
        this.type = type;
        this.id = id;
        this.state = state;
        this.availableTime = availableTime;
        this.coreCount = coreCount;
        this.memory = memory;
        this.disk = disk;
    }
}
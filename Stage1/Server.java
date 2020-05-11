/**
 * Server Class to store all the attributes.
 */
public class Server {

    int id;
    String type;
    int limit;
    int bootupTime;
    float rate;
    int coreCount;
    int memory;
    int disk;

    public Server(int id, String type, int limit, int bootupTime, float rate, int coreCount, int memory, int disk) {
        this.id = id;
        this.type = type;
        this.limit = limit;
        this.bootupTime = bootupTime;
        this.rate = rate;
        this.coreCount = coreCount;
        this.memory = memory;
        this.disk = disk;
    }
}
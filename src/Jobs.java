/**
 * Jobs Class to store all the attributes from XML file.
 */
public class Jobs {

    public int submitTime;
    public int id;
    public int estRuntime;
    public int cpuCores;
    public int memory;
    public int disk;

    Jobs(int submitTime, int id, int estRuntime, int cpuCores, int memory, int disk) {
        this.submitTime = submitTime;
        this.id = id;
        this.estRuntime = estRuntime;
        this.cpuCores = cpuCores;
        this.memory = memory;
        this.disk = disk;
    }
}
import java.util.ArrayList;

public class AlgorithmType {

    // Array List of servers which is used to dispatch jobs by comparing attributes
    private ArrayList<Server> newServers = new ArrayList<Server>();
    private Server[] parsedServer;

    AlgorithmType(ArrayList<Server> newServers, Server[] parsedServer) {
        this.newServers = newServers;
        this.parsedServer = parsedServer;
    }

    // STATES: 0 = inactive, 1 = booting, 2 = idle, 3 = active, 4 = unavailable

    /* The idea of the first-fit algorithm is assigning jobs to server, from smallest to largest. For first fit
     * algorithm to assign jobs, it first needs to check whether the server is available or not. The availability
     * of the server is depended on whether the server has enough memory, disk space and the CPU core count. In order
     * to meet basic criteria of assigning jobs from the smallest to the largest, a function was created which
     * was used to sort servers by serverType, this allowed the algorithm to filter out small and large jobs and
     * send it to specific servers. This makes the algorithm efficient as the algorithm aims to reduce running higher
     * server cost.
     */
    public Server firstFit(Jobs jobs) {
        // Array to store sorted servers
        Server[] sortedServers = sortServerID(parsedServer);

        for (Server s : sortedServers) {
            for (Server s1 : newServers) {
                if ((s.type).equals(s1.type)) {
                    // Comparing if Server have enough resource
                    if (s1.coreCount >= jobs.cpuCores && s1.disk >= jobs.disk && s1.memory >= jobs.memory
                            && s1.state != 4) {
                        return s1;
                    }
                }
            }
        }

        // This loop is initiated when the algo could not find any good fit servers, hence it goes through the whole
        // list and checks the next available server and checking state status.
        for (Server s : parsedServer) {
            Server temp = null;
            if (s.coreCount >= jobs.cpuCores && s.disk >= jobs.disk && s.memory >= jobs.disk && s.state != 4) {
                temp = s;
                temp.id = 0;
                return temp;
            }
        }
        return null;
    }

    /* The worse-fit algorithm allocates jobs to the largest server available whenever the server becomes available.
     * The server attribute coreCount is used to determines the type of server, which is then used to identify the
     * size of the server as well. The algorithm also takes some idea from first-fit to determine whether a server
     * has enough resource like memory and disk space for job allocation.
     */
    public Server worstFit(Jobs jobs) {
        int worstFit = Integer.MIN_VALUE;
        int alternateFit = Integer.MIN_VALUE;
        Server worst = null;
        Server alternate = null;
        Boolean worstFound = false;
        Boolean alternateFound = false;

        for (Server s : newServers) {
            if (s.coreCount >= jobs.cpuCores && s.disk >= jobs.disk && s.memory >= jobs.memory
                    && (s.state == 0 || s.state == 2 || s.state == 3)) {
                int fitnessValue = s.coreCount - jobs.cpuCores;
                if (fitnessValue > worstFit && (s.availableTime == -1 || s.availableTime == jobs.submitTime)) {
                    worstFit = fitnessValue;
                    worstFound = true;
                    worst = s;
                } else if (fitnessValue > alternateFit && s.availableTime >= 0) {
                    alternateFit = fitnessValue;
                    alternateFound = true;
                    alternate = s;
                }
            }
        }

        if (worstFound) {
            return worst;
        } else if (alternateFound) {
            return alternate;
        }
        int lowest = Integer.MIN_VALUE;
        Server tempNow = null;
        for (Server serv : parsedServer) {
            int fit = serv.coreCount - jobs.cpuCores;
            if (fit > lowest && serv.disk >= jobs.disk && serv.memory >= jobs.memory) {
                lowest = fit;
                tempNow = serv;
            }
        }
        tempNow.id = 0;
        return tempNow;
    }

    // Function to sort servers according to their core count
    public Server[] sortServerID(Server[] servArr) {
        int length = servArr.length;
        for (int i = 0; i < length - 1; i++) {
            for (int j = 0; j < length - i - 1; j++) {
                if (servArr[j].coreCount > servArr[j + 1].coreCount) {
                    Server temp = servArr[j];
                    servArr[j] = servArr[j + 1];
                    servArr[j + 1] = temp;
                }
            }
        }
        return servArr;
    }
}
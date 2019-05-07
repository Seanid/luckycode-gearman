package com.luckycode.gearman;

/**
 * jobserver config
 */
public class JobServer {

    private String host;

    private  int port;



    public JobServer(){}


    public JobServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "JobServer{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}

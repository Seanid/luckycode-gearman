package com.luckycode.gearman;

import org.gearman.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * gearman client and and server Factory
 */
public class GearmanFactory {


    private static Logger logger= LoggerFactory.getLogger(GearmanFactory.class);

    private static Gearman gearman = Gearman.createGearman();

    private static List<GearmanClient> clients;//客户端列表

    private static GearmanWorker worker;//worker


    private final static int DEFAULT_CLIENT_COUNT=1;//默认的客户端数目

    private final static String DEFAULT_JOBSERVER="localhost:4730";//默认的job server地址

    private final static String DEFAULT_PROPERTIES="gearman.properties";//默认配置文件

    private  final static  PropertiesUtil propertiesUtil=new PropertiesUtil(DEFAULT_PROPERTIES);

    private final static String DEFAULT_CLIENTS_KEY="gearman.job.clients";//客户端数目key

    private final static String DEFAULT_JOBSERVER_KEY="gearman.job.server";//jobserver地址


    private final static String DEFAULT_MAXWORKER_KEY ="gearman.job.maxworker";//work最大线程数目

    private final static int DEFAULT_MAXWORKER_COUNT=10;//默认的最大线程数目


    /**
     * 访问的计数器
     */
    private static volatile AtomicInteger counter;

    /**
     * 初始化客户端队列
     */
    public static void initClient(){

        int clientNum=Integer.parseInt(propertiesUtil.getValueByKey(DEFAULT_CLIENTS_KEY,DEFAULT_CLIENT_COUNT+""));


        if (clientNum < 1) {
            clientNum = DEFAULT_CLIENT_COUNT;
        }
        clients = new ArrayList<GearmanClient>();

        List<JobServer> jobServers=getJobServers();


        for (int i = 0; i < clientNum; i++) {
            clients.add(gearman.createGearmanClient());
                 for(JobServer jobServer:jobServers){
                GearmanServer server = gearman.createGearmanServer(jobServer.getHost(), jobServer.getPort());
                clients.get(i).addServer(server);
            }
        }

    }

    /**
     * 初始化worker
     */
    public static void initWorker(){

        List<JobServer> jobServers=getJobServers();
        worker=gearman.createGearmanWorker();
        for(JobServer jobServer:jobServers){
            GearmanServer server = gearman.createGearmanServer(jobServer.getHost(), jobServer.getPort());
            worker.addServer(server);
        }
        int maxWorkers=Integer.parseInt(propertiesUtil.getValueByKey(DEFAULT_MAXWORKER_KEY,DEFAULT_MAXWORKER_COUNT+""));
        worker.setMaximumConcurrency(maxWorkers);

    }



    /**
     * 返回 GearmanClient实例
     *
     * @return
     */
    public static GearmanClient createClient() {
        if (clients == null || clients.size() == 0) {
           initClient();
        }

        if (clients.size() == 1) {
            return clients.get(0);
        }
        //加上绝对值，防止溢出负数
        int n = Math.abs(counter.getAndIncrement()) % clients.size();
        return clients.get(n);

    }



    /**
     * 返回 GearmanClient实例
     *
     * @return
     */
    public static GearmanWorker createWorker() {
        if (worker == null ) {
            initWorker();
        }

       return worker;

    }


    /**
     * 从配置文件中获取集群中的JobServer
     * @return
     */
    public static List<JobServer> getJobServers(){
        List<JobServer> jobServers=new ArrayList<>();
        String jobServersStr=propertiesUtil.getValueByKey(DEFAULT_JOBSERVER_KEY,DEFAULT_JOBSERVER);

        String[] jobServerArray=jobServersStr.split(";");

        for(String jobServer :jobServerArray){
            String[] tmp=jobServer.split(":");
            if(tmp.length==2){
                JobServer server=new JobServer(tmp[0],Integer.parseInt(tmp[1]));
                jobServers.add(server);
            }
        }
        return jobServers;
    }



    /**
     * shutdown gearman
     */
    public static void destroy() {
        gearman.shutdown();
    }



}

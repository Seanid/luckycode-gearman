package com.luckycode.gearman;

import org.gearman.GearmanFunction;
import org.gearman.GearmanWorker;

public class GearmanWorkerUtil {


    /**
     * 返回 GearmanClient实例
     *
     * @return
     */
    public static void registWorker(String jobName, GearmanFunction function)  {
        GearmanWorker worker=GearmanFactory.createWorker();
        worker.addFunction(jobName,function);




    }

}

package com.luckycode.gearman;

public class JobStatus {

    private String functionName;//任务名称

    private int total;//任务在队列中的数目

    private int running;//任务在队列中执行的数目

    private int workers;//可执行的worker数目

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getRunning() {
        return running;
    }

    public void setRunning(int running) {
        this.running = running;
    }

    public int getWorkers() {
        return workers;
    }

    public void setWorkers(int workers) {
        this.workers = workers;
    }

    @Override
    public String toString() {
        return "JobStatus{" +
                "functionName='" + functionName + '\'' +
                ", total=" + total +
                ", running=" + running +
                ", workers=" + workers +
                '}';
    }
}

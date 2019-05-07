package com.luckycode.gearman;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * 操作gearman工具类
 */
public class GearmanCommandUtil {

    // 支持的命令: status
    private static final String STATUS_COMMAND = "status\n";
    // Gearmand 服务 队列 状态 命令 结果结尾
    private static final String STATUS_COMMAND_RESULT_END = ".";
    private static final Logger logger = LoggerFactory.getLogger(GearmanCommandUtil.class);

    //保存gearman数据
    private static Map<String, Integer> gearmanDataMap;


    /**
     * 查询gearman队列 status
     *
     * @param hostAndPort
     * @param taskName    模糊查找的taskname
     * @return 返回执行status命令后返回的结果
     */
    public static List<JobStatus> exceStatus(String hostAndPort, String taskName) {
        String[] params = hostAndPort.split(":");
        String host = params[0];
        int port = Integer.parseInt(params[1]);

        List<JobStatus> outResult = new ArrayList<>();

        TelnetClient client = null;
        PrintWriter writer = null;
        try {
            client = new TelnetClient();
            // 默认1s超时
            client.setDefaultTimeout(1000);
            client.connect(host, port);

            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            writer = new PrintWriter(out);
            // 执行队列状态命令
            writer.write(STATUS_COMMAND);
            writer.flush();
            String readStr;
            while ((readStr = br.readLine()) != null && !readStr.equals(STATUS_COMMAND_RESULT_END)) {
                String[] temps = readStr.split("\\s");
                //两种情况，一种是taskname不为空，只获取taskname相同的task，一种是taskName是空，列出所有
                if ((StringUtils.isNotBlank(taskName) && temps[0].contains(taskName)) || StringUtils.isBlank(taskName)) {
                    JobStatus jobStatus = new JobStatus();
                    jobStatus.setFunctionName(temps[0]);
                    jobStatus.setTotal(Integer.parseInt(temps[1]));
                    jobStatus.setRunning(Integer.parseInt(temps[2]));
                    jobStatus.setWorkers(Integer.parseInt(temps[3]));
                    outResult.add(jobStatus);
                }
            }
        } catch (Exception ex) {
            logger.error("gearman exceStatus 执行失败：{}", ex.getMessage());
            //出现错误则抛出异常
            throw new RuntimeException("gearman exceStatus 执行失败：" + ex.getMessage());
        } finally {
            if (null != writer) {
                writer.flush();
                writer.close();
            }
            if (null != client) {
                try {
                    client.disconnect();
                } catch (IOException e) {
                    logger.error("流关闭失败：" + e.getMessage());
                }
            }
        }
        return outResult;
    }


    /**
     * TODO 待测试
     * 删除队列中的任务
     *
     * @param hostAndPort
     * @param taskName
     * @return
     */
    public static boolean executeDropFunctionName(String hostAndPort, String taskName) {

        boolean flag = true;
        String[] params = hostAndPort.split(":");
        String host = params[0];
        int port = Integer.parseInt(params[1]);

        // 执行命令
        Process child = null;
        InputStream in = null;
        InputStream error = null;
        try {
            String command = "gearadmin -h " + host + " -p " + port + " --drop-function " + taskName;
            logger.info("执行任务队列删除命令：" + command);
            child = Runtime.getRuntime().exec(command);
            String out = "";
            in = child.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                out += strLine + "\n";
            }

            if (StringUtils.isBlank(out)) {
                error = child.getErrorStream();
                br = new BufferedReader(new InputStreamReader(error));
                while ((strLine = br.readLine()) != null) {
                    out += strLine + "\n";
                }
            }

        } catch (Exception e) {
            //执行失败
            flag = false;
            logger.error("执行gearman删除队列发生异常：", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("流关闭失败：{}", e.getMessage());
                }
            }
            if (error != null) {
                try {
                    error.close();
                } catch (IOException e) {
                    logger.error("流关闭失败：{}", e.getMessage());

                }
            }
            if (child != null) {
                try {
                    child.waitFor();
                } catch (InterruptedException e) {
                    logger.error("流关闭失败：{}", e.getMessage());

                }
            }
        }
        return flag;
    }

    /**
     * 从gearman server读取数据 并以Map<functionName,队列等待数>形式返回
     */
    public static synchronized Map<String, Integer> readGearmanData(String hostAndPort, String taskName) {

        List<JobStatus> result = GearmanCommandUtil.exceStatus(hostAndPort, taskName);
        gearmanDataMap = new HashMap<>();
        for (JobStatus jobStatus : result) {
            String functionName = jobStatus.getFunctionName();
            Integer currentNodeCount = jobStatus.getTotal();
            if (gearmanDataMap.get(functionName) == null) {
                gearmanDataMap.put(functionName, currentNodeCount);
            } else {
                Integer mapCount = gearmanDataMap.get(functionName);
                gearmanDataMap.put(functionName, mapCount + currentNodeCount);
            }
        }
        return new HashMap<>(gearmanDataMap);
    }
}

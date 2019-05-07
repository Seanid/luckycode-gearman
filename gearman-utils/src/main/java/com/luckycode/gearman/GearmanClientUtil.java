package com.luckycode.gearman;

import org.gearman.GearmanClient;
import org.gearman.GearmanJobEvent;
import org.gearman.GearmanJobReturn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

/**
 * Gearman 客户端工具
 */
public class GearmanClientUtil {

    private static Logger logger = LoggerFactory.getLogger(GearmanClientUtil.class);

    /**
     * 提交同步任务
     * 同步任务会等待结果，获取结果是在GEARMAN_JOB_SUCCESS之后
     * @param jobName
     * @param data
     * @return
     */
    public static String syncSubmit(String jobName, String data) {
        GearmanClient client = GearmanFactory.createClient();
        try {
            int flag = 0;
            GearmanJobReturn jobReturn = client.submitJob(jobName, data.getBytes("UTF-8"));
            while (!jobReturn.isEOF()) {
                GearmanJobEvent event = jobReturn.poll(3, TimeUnit.SECONDS);
                if (event == null) {
                    flag++;
                    if(flag > 3){
                        break;
                    }else {
                        continue;
                    }
                }

                //同步类型job，只处理GEARMAN_JOB_SUCCESS和GEARMAN_JOB_FAIL
                switch (event.getEventType()) {
                    case GEARMAN_SUBMIT_SUCCESS:
                        continue;
                    case GEARMAN_JOB_SUCCESS:
                        return new String(event.getData());
                    case GEARMAN_SUBMIT_FAIL:
                    case GEARMAN_JOB_FAIL:
                        logger.error("提交或者处理失败.[eventType={}][eventData={}]" ,event.getEventType() , new String(event.getData()));
                        break;
                }
            }
        }
        catch (UnsupportedEncodingException e) {
            logger.error("转换UTF-8编码错误.[data={}]" , data, e);
        }
        catch (InterruptedException e) {
            logger.error("处理过程中断.[jobName={}]" , jobName, e);
        }
        return null;
    }


    /**
     * 非同步提交
     *
     * 异步提交只需要监听提交成功事件即可
     * @param jobName
     * @param data
     * @return
     */
    public static String rsyncSubmit(String jobName, String data) {
        GearmanClient client = GearmanFactory.createClient();

        String result = null;
        try {

            GearmanJobReturn jobReturn = client.submitBackgroundJob(jobName, data.getBytes("UTF-8"));

            if (!jobReturn.isEOF()) {
                // 等待server反馈，3秒超时
                GearmanJobEvent event = jobReturn.poll(3, TimeUnit.SECONDS);

                if (event != null) {
                    // 对于submitBackgroundJob，事件返回是否提交成功
                    switch ((event.getEventType())) {
                        case GEARMAN_SUBMIT_SUCCESS: {
                            result = "提交任务成功." + "[functionName=" + jobName + "]" + "[data=" + data + "]";
                            break;
                        }
                        case GEARMAN_SUBMIT_FAIL: {
                            result = event.getEventType() + "-" + new String(event.getData());
                            break;
                        }
                        default:
                            // 忽略其它消息，正常情况下submitBackgroundJob只会返回提交成功或失败
                    }
                }
                else {
                    // 超时没收到回复消息，就认为有job server挂了
                    result = "超时没有收到job server反馈消息(请检查server状态)";
                }
            }
        }
        catch (UnsupportedEncodingException e) {
            result = "不支持的字符集";
        }
        catch (NullPointerException ex) {
            result = "job处理函数名为空";
        }
        catch (InterruptedException e) {
            // ignore
        }
        return result;
    }


}

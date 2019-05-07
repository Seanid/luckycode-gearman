package com.luckycode.gearman;

import com.alibaba.fastjson.JSON;
import org.gearman.GearmanFunction;
import org.gearman.GearmanFunctionCallback;

import java.util.Map;

/**
 * 任务的简易封装类
 */
public abstract  class BaseJob  implements GearmanFunction {


    @Override
    public byte[] work(String function, byte[] data, GearmanFunctionCallback callback) throws Exception {
        String str = "{}";
        if (data != null) {
            str = new String(data, "UTF-8");
        }
        Map<String, Object> context=JSON.parseObject(str);
        return execute(context);
    }

    /**
     * 执行函数
     * @param context
     * @return
     */
    public abstract byte[] execute(Map<String,Object> context);


}

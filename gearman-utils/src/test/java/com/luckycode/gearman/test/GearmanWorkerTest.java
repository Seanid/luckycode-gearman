package com.luckycode.gearman.test;

import com.luckycode.gearman.BaseJob;
import com.luckycode.gearman.GearmanWorkerUtil;
import org.gearman.GearmanFunction;
import org.gearman.GearmanFunctionCallback;
import org.junit.Test;

import java.util.Map;

public class GearmanWorkerTest {

    @Test
    public void regTest() throws InterruptedException {

        GearmanWorkerUtil.registWorker("TEST_MAP", new BaseJob() {
            @Override
            public byte[] execute(Map<String, Object> context) {

                context.forEach((k,v)->{
                    System.out.println(k+"=="+v);
                });

                return "测试成功".getBytes();
            }
        });

        Thread.sleep(6000);
    }


}

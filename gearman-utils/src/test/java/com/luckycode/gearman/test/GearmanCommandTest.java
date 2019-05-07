package com.luckycode.gearman.test;

import com.luckycode.gearman.GearmanCommandUtil;
import com.luckycode.gearman.JobStatus;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class GearmanCommandTest {



    @Test
    public void testStatusList1(){

        List<JobStatus> lists= GearmanCommandUtil.exceStatus("192.168.56.129:4730","HELLO_WORLD");

        lists.forEach(item->{
            System.out.println(item.toString());
        });

    }

    @Test
    public void testStatusList2(){

        List<JobStatus> lists= GearmanCommandUtil.exceStatus("192.168.56.129:4730","HELLO_WORLD_2");

        lists.forEach(item->{
            System.out.println(item.toString());
        });

    }

    @Test
    public void testStatusList3(){

        List<JobStatus> lists= GearmanCommandUtil.exceStatus("192.168.56.129:4730","");

        lists.forEach(item->{
            System.out.println(item.toString());
        });

    }

    @Test
    public void testStatusMap(){
        Map<String,Integer> dataMap=GearmanCommandUtil.readGearmanData("192.168.56.129:4730","");
        dataMap.forEach((k,v)->{
            System.out.println(k+"=="+v);
        });
    }



}

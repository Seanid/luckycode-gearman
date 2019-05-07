package com.luckycode.gearman.test;

import com.luckycode.gearman.GearmanClientUtil;
import com.luckycode.gearman.GearmanFactory;
import org.gearman.Gearman;
import org.junit.Test;

public class GearmanClientTest {

    @Test
    public void testRsyncSubmit(){


        System.out.println(GearmanClientUtil.rsyncSubmit("HELLO_WORLD","hello world"));


    }



    @Test
    public void testRsyncSubmit2(){


        System.out.println(GearmanClientUtil.rsyncSubmit("TEST_MAP","{'data1':'12345','data2':'45678'}"));


    }


    @Test
    public void testSyncSubmit(){


        System.out.println(GearmanClientUtil.syncSubmit("HELLO_WORLD","hello world"));


    }


}

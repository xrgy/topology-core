package com.gy.topologyCore;

import com.gy.topologyCore.base.BaseRepositoryFactoryBean;
import com.gy.topologyCore.schedule.WeaveScheduleTask;
import com.gy.topologyCore.service.TopoService;
import com.gy.topologyCore.utils.ProperUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by gy on 2018/3/31.
 */
@SpringBootApplication
//@EnableAutoConfiguration
@EnableJpaRepositories(repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
public class BingoApplication {
    public static void main(String[] args){
        ProperUtil.SetConfInfo();
        SpringApplication.run(BingoApplication.class,args);
        WeaveScheduleTask task = new WeaveScheduleTask();
//        task.scheduleGetWeaveInfo();
//        task.scheduleGetLLDPInfo();
    }
}

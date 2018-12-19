package com.gy.topologyCore.schedule;

import com.gy.topologyCore.service.MonitorService;
import com.gy.topologyCore.service.TopoService;
import com.gy.topologyCore.service.impl.TopoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by gy on 2018/12/18.
 */
@Component
public class WeaveScheduleTask {
    //@Component泛指各种组件，就是说当我们的类不属于各种归类的时候（不属于@Controller、@Services等的时候），我们就可以使用@Component来标注这个类。
    //被@PostConstruct修饰的方法会在服务器加载Servle的时候运行，并且只会被服务器执行一次。PostConstruct在构造函数之后执行,init()方法之前执行。
    ScheduledExecutorService serviceSchedule = Executors.newScheduledThreadPool(5);
    @Autowired
    TopoService topoService;


    @PostConstruct
    public void scheduleGetWeaveInfo() {
        serviceSchedule.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                topoService.getWeaveInfo();
            }
        }, 20, 10, TimeUnit.SECONDS);

    }
}

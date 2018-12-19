package com.gy.topologyCore.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gy.topologyCore.entity.TestEntity;
import com.gy.topologyCore.schedule.WeaveScheduleTask;
import com.gy.topologyCore.service.TopoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by gy on 2018/3/31.
 */
@RestController
@RequestMapping("topo")
public class TopoController {

    @Autowired
    private TopoService service;

    @Autowired
    private ObjectMapper mapper;

    @RequestMapping("jpa")
    @ResponseBody
    public TestEntity testJPA(){
//        TestEntity entity = new TestEntity();
//        entity.setId("sasada");
//        entity.setName("gygy");
//        return entity;
        return service.getJPAInfo();
    }

    @RequestMapping("getLldpInfo")
    @ResponseBody
    public void getLldpInfo(String uuid){

        service.getLldpInfo();
    }

    @RequestMapping("getWeaveInfo")
    @ResponseBody
    public void getWeaveInfo(String uuid){
//        service.getWeaveInfo();
//        WeaveScheduleTask task = new WeaveScheduleTask();
//        task.scheduleGetWeaveInfo();
    }

    @RequestMapping("getAllWeaveTopoNode")
    @ResponseBody
    public String getAllWeaveTopoNode() throws JsonProcessingException {
        return mapper.writeValueAsString(service.getAllWeaveTopoNode());
    }

    @RequestMapping("getAllWeaveTopoLink")
    @ResponseBody
    public String getAllWeaveTopoLink() throws JsonProcessingException {
        return mapper.writeValueAsString(service.getAllWeaveTopoLink());
    }
}

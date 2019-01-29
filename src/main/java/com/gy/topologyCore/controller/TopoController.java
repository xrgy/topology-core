package com.gy.topologyCore.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gy.topologyCore.entity.*;
import com.gy.topologyCore.schedule.WeaveScheduleTask;
import com.gy.topologyCore.service.TopoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

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
    public String getAllWeaveTopoNode(String canvasId) throws JsonProcessingException {
        return mapper.writeValueAsString(service.getAllWeaveTopoNode(canvasId));
    }

    @RequestMapping("getBusinessNodeByUuid")
    @ResponseBody
    public String getBusinessNodeByUuid(String uuid) throws JsonProcessingException {
        return mapper.writeValueAsString(service.getBusinessNodeByUuid(uuid));
    }

    @RequestMapping("getAllWeaveTopoLink")
    @ResponseBody
    public String getAllWeaveTopoLink(String canvasId) throws JsonProcessingException {
        return mapper.writeValueAsString(service.getAllWeaveTopoLink(canvasId));
    }

    @RequestMapping("getAllNetTopoNode")
    @ResponseBody
    public String getTopoNodeByCanvasId(String canvasId) throws JsonProcessingException {
        return mapper.writeValueAsString(service.getTopoNodeByCanvasId(canvasId));
    }

    @RequestMapping("getAllNetTopoLink")
    @ResponseBody
    public String getTopoLinkByCanvasId(String canvasId) throws JsonProcessingException {
        return mapper.writeValueAsString(service.getTopoLinkByCanvasId(canvasId));
    }

    @RequestMapping("getCanvasByType")
    @ResponseBody
    public String getCanvasByType(String name) throws JsonProcessingException {
        return mapper.writeValueAsString(service.getCanvasByType(name));
    }

    @RequestMapping("getCanvasByUUid")
    @ResponseBody
    public String getCanvasByUUid(String uuid) throws JsonProcessingException {
        return mapper.writeValueAsString(service.getCanvasByUuid(uuid));
    }

    @RequestMapping("getAllNetTopoPort")
    @ResponseBody
    public String getAllPorts() throws JsonProcessingException {
        return mapper.writeValueAsString(service.getAllPorts());
    }


    //调用拓扑的deleteBymonitoruuid
    @RequestMapping("deleteTopoResourceBymonitoruuid")
    @ResponseBody
    public boolean deleteTopoResourceBymonitoruuid(String monitorUuid) throws JsonProcessingException {
        return service.deleteTopoResourceBymonitoruuid(monitorUuid);
    }

    @RequestMapping("getCanvasLinkRate")
    @ResponseBody
    public String getCanvasLinkRate(String canvasId, String linkRate) throws JsonProcessingException {
        return mapper.writeValueAsString(service.getCanvasLinkRate(canvasId,linkRate));
    }

    @RequestMapping("insertTopoNodeList")
    @ResponseBody
    public boolean insertTopoNodeList(@RequestBody String data) throws IOException {
        List<TopoNodeEntity> nodes = mapper.readValue(data,new TypeReference<List<TopoNodeEntity>>(){});
        return service.insertTopoNodeList(nodes);
    }
}

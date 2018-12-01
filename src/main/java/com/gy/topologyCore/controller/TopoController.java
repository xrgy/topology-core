package com.gy.topologyCore.controller;

import com.gy.topologyCore.entity.TestEntity;
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

        service.getLldpInfo(uuid);
    }
}

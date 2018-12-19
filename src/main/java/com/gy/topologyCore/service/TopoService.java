package com.gy.topologyCore.service;

import com.gy.topologyCore.entity.TestEntity;
import com.gy.topologyCore.entity.TopoBusinessLinkEntity;
import com.gy.topologyCore.entity.TopoBusinessNodeEntity;

import java.util.List;

/**
 * Created by gy on 2018/3/31.
 */
public interface TopoService {
    public TestEntity getJPAInfo();

    public void getLldpInfo();

    void getWeaveInfo();

//    void scheduleGetWeaveInfo();

    List<TopoBusinessNodeEntity> getAllWeaveTopoNode();

    List<TopoBusinessLinkEntity> getAllWeaveTopoLink();
}

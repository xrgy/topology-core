package com.gy.topologyCore.service;

import com.gy.topologyCore.entity.*;

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

    List<TopoNodeEntity> getTopoNodeByCanvasId(String canvasId);

    List<TopoLinkEntity> getTopoLinkByCanvasId(String canvasId);

    List<TopoCanvasEntity> getCanvasByType(String name);

    public List<TopoPortEntity> getAllPorts();
}

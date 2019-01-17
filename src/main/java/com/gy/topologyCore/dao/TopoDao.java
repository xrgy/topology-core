package com.gy.topologyCore.dao;

import com.gy.topologyCore.entity.*;
import com.gy.topologyCore.entity.monitor.OperationMonitorEntity;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Created by gy on 2018/3/31.
 */
public interface TopoDao {
    public TestEntity getJPAInfo();

    public TopoCanvasEntity getWholeTopoCanvas();

    public CompletionStage<Optional<TopoNodeEntity>> getNodeByMonitorAndCanvas(String monitorUUid,String canvasId);

    public TopoNodeEntity insertTopoNode(TopoNodeEntity node);

    public TopoPortEntity insertTopoPort(TopoPortEntity port);

    public TopoLinkEntity insertTopoLink(TopoLinkEntity link);

    public List<TopoNodeEntity>  getAllNodeByCanvasId(String uuid);

    public List<TopoLinkEntity>  getAllLinkByCanvasId(String uuid);

    public List<TopoPortEntity>  getAllPortByNodeId(String uuid);

    public List<TopoPortEntity> getAllPorts();

    public void deleteLinkByCanvasId(String canvasId);

    public void removeLinkByNodeAndPort(String nodeId,String port);

    public TopoCanvasEntity insertTopoCanvas(TopoCanvasEntity canvas);

    public TopoBusinessNodeEntity insertTopoBusinessNode(TopoBusinessNodeEntity node);



    public TopoBusinessLinkEntity insertTopoBusinessLink(TopoBusinessLinkEntity link);


    public TopoCanvasEntity canvasIsExist(String uuid);

    public List<TopoBusinessNodeEntity>  getAllBusinessNodeByCanvasId(String uuid);

    public List<TopoBusinessLinkEntity>  getAllBusinessLinkByCanvasId(String uuid);

    public List<TopoCanvasEntity> getCanvasByType(String name);


    List<TopoBusinessNodeEntity> getAllWeaveTopoNode();

    List<TopoBusinessLinkEntity> getAllWeaveTopoLink();

}

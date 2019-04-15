package com.gy.topologyCore.service;

import com.gy.topologyCore.entity.*;
import com.gy.topologyCore.entity.snmp.LldpInfos;

import java.util.List;

/**
 * Created by gy on 2018/3/31.
 */
public interface TopoService {
    public TestEntity getJPAInfo();

    public void getLldpInfo(LldpInfos infos);

    void getWeaveInfo();

//    void scheduleGetWeaveInfo();

    List<TopoBusinessNodeEntity> getAllWeaveTopoNode(String relUuid);

    /**
     *
     * @param relUuid linkuuid
     * @return
     */
    List<TopoBusinessLinkEntity> getAllWeaveTopoLink(String relUuid);

    List<TopoNodeEntity> getTopoNodeByCanvasId(String canvasId);

    List<TopoLinkEntity> getTopoLinkByCanvasId(String canvasId);

    List<TopoCanvasEntity> getCanvasByType(String name);

    public List<TopoPortEntity> getAllPorts();

    /**
     * 删除监控记录时候需要级联删除拓扑中的设备
     * @param monitorUuid
     * @return
     */
    boolean deleteTopoResourceBymonitoruuid(String monitorUuid);



    boolean deleteTopoLinkByUuid(String uuid);

    /**
     * 获取画布链路流量信息
     * @param canvasId
     * @param linkRate
     * @return
     */
    List<TopoLinkRateView> getCanvasLinkRate(String canvasId,String linkRate);


    /**
     * 保存节点列表
     * @param nodes
     * @return
     */
    boolean insertTopoNodeList(List<TopoNodeEntity> nodes);

    /**
     * 获取business topo node
     * @param uuid
     * @return
     */
    TopoBusinessNodeEntity getBusinessNodeByUuid(String uuid);


    /**
     * 获取topo canvas
     * @param uuid
     * @return
     */
    TopoCanvasEntity getCanvasByUuid(String uuid);

    boolean insertBusinessTopoNodeList(List<TopoBusinessNodeEntity> nodes);

    /**
     * 删除业务相关拓扑资源
     * @param uuid 业务uuid
     * @return
     */
    boolean delTopoResourceByBusinessId(String uuid);

    boolean deleteBusTopoLinkByUuid(String uuid);

    boolean deleteBusTopoNodeByUuid(String uuid);
}

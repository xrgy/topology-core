package com.gy.topologyCore.service.impl;

import com.gy.topologyCore.common.MonitorEnum;
import com.gy.topologyCore.dao.TopoDao;
import com.gy.topologyCore.entity.*;
import com.gy.topologyCore.entity.lldp.LldpInfo;
import com.gy.topologyCore.entity.lldp.LocalHashNode;
import com.gy.topologyCore.entity.lldp.LocalInfo;
import com.gy.topologyCore.entity.monitor.LightTypeEntity;
import com.gy.topologyCore.entity.monitor.MiddleTypeEntity;
import com.gy.topologyCore.entity.monitor.OperationMonitorEntity;
import com.gy.topologyCore.service.MonitorService;
import com.gy.topologyCore.service.TopoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * Created by gy on 2018/3/31.
 */
@Service
public class TopoServiceImpl implements TopoService {

    @Autowired
    private TopoDao dao;

    @Autowired
    private MonitorService monitorService;

    @Override
    public TestEntity getJPAInfo() {
        return dao.getJPAInfo();
    }

    private String getLocalNodeHashCode(String mac,String name){
        return mac.concat(name);
    }

    @Override
    public void getLldpInfo() {
        List<MiddleTypeEntity> middleTypeList = monitorService.getMiddleTypeEntity();
        List<LightTypeEntity> lightTypeList = monitorService.getLightTypeEntity();
        Optional<MiddleTypeEntity> networkMiddle = middleTypeList.stream()
                .filter(midlle -> midlle.getName().equals(MonitorEnum.MonitorType.NETWORK_DEVICE.name())).findFirst();
        String middleUuid = networkMiddle.map(MiddleTypeEntity::getUuid).orElse("");
        TopoCanvasEntity canvas = dao.getWholeTopoCanvas();
        //删除画布所有的链路
        dao.deleteLinkByCanvasId(canvas.getUuid());
        List<TopoNodeEntity> nodeEntityList = dao.getAllNodeByCanvasId(canvas.getUuid());
        monitorService.getExporterLldpInfo(middleUuid).thenAccept(lldpInfos -> {
            Map<String, String> nodeMonitorMap = new HashMap<>();
            List<LldpInfo> lldp = lldpInfos.getLldpInfos();
            lldp.forEach(node -> {
                Optional<TopoNodeEntity> nodeEntity = nodeEntityList.stream()
                        .filter(n -> n.getMonitorUuid().equals(node.getMonitorUuid())).findFirst();
                if (nodeEntity.isPresent()) {
                    nodeMonitorMap.put(nodeEntity.get().getMonitorUuid(), nodeEntity.get().getUuid());
                } else {
                    String randomUuid = UUID.randomUUID().toString();
                    TopoNodeEntity insertNode = new TopoNodeEntity();
                    insertNode.setUuid(randomUuid);
                    insertNode.setCanvasId(canvas.getUuid());
                    insertNode.setMonitorUuid(node.getMonitorUuid());
                    insertNode.setXPoint(100);
                    insertNode.setYPoint(100);
                    monitorService.getOperationMonitorEntity(node.getMonitorUuid()).thenApply(operationMonitorEntity -> {
                        insertNode.setNodeName(operationMonitorEntity.getName());
                        insertNode.setNodeType(lightTypeList.stream()
                                .filter(light -> light.getUuid().equals(operationMonitorEntity.getLightTypeId())).findFirst()
                                .map(LightTypeEntity::getName).orElse(""));
                        return insertNode;
                    }).thenAccept(inserNode -> {
                        TopoNodeEntity nodeExist = dao.insertTopoNode(inserNode);
                        nodeMonitorMap.put(nodeExist.getMonitorUuid(), nodeExist.getUuid());
                    });
                }
            });
            //localinfo 做成hashcode
            HashMap<String,LocalHashNode> hashMapLocalNode = new HashMap<>();
            lldp.forEach(l->{
                String ip = l.getIp();
                String monitId = l.getMonitorUuid();
                l.getLocalInfos().forEach(local->{
                    LocalHashNode hashNode = new LocalHashNode();
                    hashNode.setIp(ip);
                    hashNode.setMonitorUuid(monitId);
                    hashNode.setLocalPortName(local.getLocalPortName());
                    hashNode.setLocalPortIndex(local.getLocalPortIndex());
                    hashNode.setLocalPortMac(local.getLocalPortMac());
                    //hash
                    hashMapLocalNode.put(getLocalNodeHashCode(local.getLocalPortMac(),local.getLocalPortName()),hashNode);
                });
            });

            List<TopoLinkEntity> linkEntityList = dao.getAllLinkByCanvasId(canvas.getUuid());

            for (int i = 1; i < lldp.size(); i++) {
                LldpInfo localNode = lldp.get(i);
                localNode.getRemInfos().forEach(localRem->{
                    String fromNodeMonitorUuid = localNode.getMonitorUuid();
                    String fromNodePort = localNode.getLocalInfos().stream()
                            .filter(local->local.getLocalPortIndex().equals(localRem.getRemLocalIndex()))
                            .findFirst().map(LocalInfo::getLocalPortName).orElse("");
                    String fromNodeId = nodeMonitorMap.get(fromNodeMonitorUuid);
                    List<TopoPortEntity> fromPortList = dao.getAllPortByNodeId(fromNodeId);
                    Optional<TopoPortEntity> fromPortOpt = fromPortList.stream().filter(x->x.getPort().equals(fromNodePort)).findFirst();
                    String hashCode = getLocalNodeHashCode(localRem.getRemPortMac(),localRem.getRemPortName());
                    LocalHashNode elseNodeLocal = hashMapLocalNode.get(hashCode);
                    if (null!=elseNodeLocal){
                        String toNodeMonitorUuid = elseNodeLocal.getMonitorUuid();
                        String toNodePort = elseNodeLocal.getLocalPortName();
                        String toNodeId = nodeMonitorMap.get(toNodeMonitorUuid);
                        List<TopoPortEntity> toPortList = dao.getAllPortByNodeId(toNodeId);
                        Optional<TopoPortEntity> toPortOpt = toPortList.stream().filter(x -> x.getPort().equals(toNodePort)).findFirst();
                        if (fromPortOpt.isPresent() && toPortOpt.isPresent()) {
                            Optional<TopoLinkEntity> linkOpt = linkEntityList.stream().filter(link -> (link.getFromNodeId().equals(fromNodeId)
                                    && link.getFromPortId().equals(fromPortOpt.get().getUuid()) && link.getToNodeId().equals(toNodeId)
                                    && link.getToPortId().equals(toPortOpt.get().getUuid())) || (link.getFromNodeId().equals(toNodeId)
                                    && link.getFromPortId().equals(toPortOpt.get().getUuid()) && link.getToNodeId().equals(fromNodeId)
                                    && link.getToPortId().equals(fromPortOpt.get().getUuid()))).findFirst();
                            if (!linkOpt.isPresent()) {
                                //删除from这条链路
                                //删除to这条链路
                                dao.removeLinkByNodeAndPort(fromNodeId,fromPortOpt.get().getUuid());
                                dao.removeLinkByNodeAndPort(toNodeId,toPortOpt.get().getUuid());
                                //持久化这条链路
                                TopoLinkEntity link = new TopoLinkEntity();
                                link.setUuid(UUID.randomUUID().toString());
                                link.setFromNodeId(fromNodeId);
                                link.setToNodeId(toNodeId);
                                link.setFromPortId(fromPortOpt.get().getUuid());
                                link.setToPortId(toPortOpt.get().getUuid());
                                link.setCanvasId(canvas.getUuid());
                                dao.insertTopoLink(link);
                            }
                        } else {
                            TopoPortEntity fromPortInsert = null;
                            TopoPortEntity toPortInsert = null;
                            if (fromPortOpt.isPresent()) {
                                //to持久化
                                TopoPortEntity toPort = new TopoPortEntity();
                                toPort.setUuid(UUID.randomUUID().toString());
                                toPort.setNodeUuid(toNodeId);
                                toPort.setPort(toNodePort);
                                toPortInsert = dao.insertTopoPort(toPort);
                            } else if (toPortOpt.isPresent()) {
                                //from持久化
                                TopoPortEntity fromPort = new TopoPortEntity();
                                fromPort.setUuid(UUID.randomUUID().toString());
                                fromPort.setNodeUuid(fromNodeId);
                                fromPort.setPort(fromNodePort);
                                fromPortInsert = dao.insertTopoPort(fromPort);

                            } else {
                                //from and to 持久化
                                TopoPortEntity fromPort = new TopoPortEntity();
                                fromPort.setUuid(UUID.randomUUID().toString());
                                fromPort.setNodeUuid(fromNodeId);
                                fromPort.setPort(fromNodePort);
                                TopoPortEntity toPort = new TopoPortEntity();
                                toPort.setUuid(UUID.randomUUID().toString());
                                toPort.setNodeUuid(toNodeId);
                                toPort.setPort(toNodePort);
                                fromPortInsert = dao.insertTopoPort(fromPort);
                                toPortInsert = dao.insertTopoPort(toPort);
                            }
                            //持久化链路
                            TopoLinkEntity link = new TopoLinkEntity();
                            link.setUuid(UUID.randomUUID().toString());
                            link.setFromNodeId(fromNodeId);
                            link.setToNodeId(toNodeId);
                            link.setFromPortId(fromPortInsert.getUuid());
                            link.setToPortId(toPortInsert.getUuid());
                            link.setCanvasId(canvas.getUuid());
                            dao.insertTopoLink(link);
                        }
                    }
                });
            }
        });
    }
}

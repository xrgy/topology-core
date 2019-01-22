package com.gy.topologyCore.service.impl;

import com.gy.topologyCore.common.TopoEnum;
import com.gy.topologyCore.dao.TopoDao;
import com.gy.topologyCore.entity.*;
import com.gy.topologyCore.entity.snmp.*;
import com.gy.topologyCore.entity.weave.WeaveContainerImageCluster;
import com.gy.topologyCore.service.MonitorService;
import com.gy.topologyCore.service.TopoService;
import com.gy.topologyCore.service.WeaveScopeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;


/**
 * Created by gy on 2018/3/31.
 */
@Service
public class TopoServiceImpl implements TopoService {

    @Autowired
    private TopoDao dao;

    @Autowired
    private WeaveScopeService weaveScopeService;

    @Autowired
    private MonitorService monitorService;

    @Override
    public TestEntity getJPAInfo() {
        return dao.getJPAInfo();
    }

    private String getLocalNodeHashCode(String mac, String name) {
        return mac.concat(name);
    }

    @Override
    public void getLldpInfo() {
        TopoCanvasEntity canvas = dao.getWholeTopoCanvas();
        //删除画布所有的链路
        dao.deleteLinkByCanvasId(canvas.getUuid());
        List<TopoNodeEntity> nodeEntityList = dao.getAllNodeByCanvasId(canvas.getUuid());
        monitorService.getExporterLldpInfo().thenAccept(lldpInfos -> {
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
                    monitorService.getNetworkMonitorEntity(node.getMonitorUuid()).thenApply(operationMonitorEntity -> {
                        insertNode.setNodeName(operationMonitorEntity.getName());
                        insertNode.setNodeType(operationMonitorEntity.getLightType());
                        return insertNode;
                    }).thenAccept(inserNode -> {
                        TopoNodeEntity nodeExist = dao.insertTopoNode(inserNode);
                        nodeMonitorMap.put(nodeExist.getMonitorUuid(), nodeExist.getUuid());
                    });
                }
            });
            //localinfo 做成hashcode
            HashMap<String, LocalHashNode> hashMapLocalNode = new HashMap<>();
            lldp.forEach(l -> {
                String ip = l.getIp();
                String monitId = l.getMonitorUuid();
                l.getLocalInfos().forEach(local -> {
                    LocalHashNode hashNode = new LocalHashNode();
                    hashNode.setIp(ip);
                    hashNode.setMonitorUuid(monitId);
                    hashNode.setLocalPortName(local.getLocalPortName());
                    hashNode.setLocalPortIndex(local.getLocalPortIndex());
                    hashNode.setLocalPortMac(local.getLocalPortMac());
                    //hash
                    hashMapLocalNode.put(getLocalNodeHashCode(local.getLocalPortMac(), local.getLocalPortName()), hashNode);
                });
            });

            List<TopoLinkEntity> linkEntityList = dao.getAllLinkByCanvasId(canvas.getUuid());

            for (int i = 1; i < lldp.size(); i++) {
                LldpInfo localNode = lldp.get(i);
                localNode.getRemInfos().forEach(localRem -> {
                    String fromNodeMonitorUuid = localNode.getMonitorUuid();
                    String fromNodePort = localNode.getLocalInfos().stream()
                            .filter(local -> local.getLocalPortIndex().equals(localRem.getRemLocalIndex()))
                            .findFirst().map(LocalInfo::getLocalPortName).orElse("");
                    String fromNodeId = nodeMonitorMap.get(fromNodeMonitorUuid);
                    List<TopoPortEntity> fromPortList = dao.getAllPortByNodeId(fromNodeId);
                    Optional<TopoPortEntity> fromPortOpt = fromPortList.stream().filter(x -> x.getPort().equals(fromNodePort)).findFirst();
                    String hashCode = getLocalNodeHashCode(localRem.getRemPortMac(), localRem.getRemPortName());
                    LocalHashNode elseNodeLocal = hashMapLocalNode.get(hashCode);
                    if (null != elseNodeLocal) {
                        String toNodeMonitorUuid = elseNodeLocal.getMonitorUuid();
                        String toNodePort = elseNodeLocal.getLocalPortName();
                        String toNodeId = nodeMonitorMap.get(toNodeMonitorUuid);
                        List<TopoPortEntity> toPortList = dao.getAllPortByNodeId(toNodeId);
                        Optional<TopoPortEntity> toPortOpt = toPortList.stream().filter(x -> x.getPort().equals(toNodePort)).findFirst();
                        if (fromPortOpt.isPresent() && toPortOpt.isPresent()) {
                            Optional<TopoLinkEntity> linkOpt = linkEntityList.stream().filter(link -> ( link.getFromPortId().equals(fromPortOpt.get().getUuid())
                                    && link.getToPortId().equals(toPortOpt.get().getUuid())) || (link.getFromPortId().equals(toPortOpt.get().getUuid())
                                    && link.getToPortId().equals(fromPortOpt.get().getUuid()))).findFirst();
                            if (!linkOpt.isPresent()) {
                                //删除from这条链路
                                //删除to这条链路
                                dao.deleteTopoLinkByPort(fromPortOpt.get().getUuid());
                                dao.deleteTopoLinkByPort(toPortOpt.get().getUuid());
                                //持久化这条链路
                                TopoLinkEntity link = new TopoLinkEntity();
                                link.setUuid(UUID.randomUUID().toString());
//                                link.setFromNodeId(fromNodeId);
//                                link.setToNodeId(toNodeId);
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
//                            link.setFromNodeId(fromNodeId);
//                            link.setToNodeId(toNodeId);
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

    @Override
    public void getWeaveInfo() {
        WeaveContainerImageCluster imageClusterMap = weaveScopeService.getWeaveInfoFromBusiness();

        imageClusterMap.getNodes().forEach((clusterId, list) -> {
            //clusterId是canvas的name
            TopoCanvasEntity canvasIsAlready = dao.canvasIsExist(clusterId);
            if (null != canvasIsAlready) {
                String canvasId = canvasIsAlready.getUuid();
                List<TopoBusinessNodeEntity> existNodeList = dao.getAllBusinessNodeByCanvasId(canvasId);
                List<String> existNodeUUid = new ArrayList<>();
                existNodeList.forEach(x -> {
                    existNodeUUid.add(x.getUuid());
                });
//                Map<String,String> allNameToUuid = new HashMap<>();
                list.forEach(imageNode -> {
//                    allNameToUuid.put(imageNode.getName(),imageNode.getId());
                    if (!existNodeUUid.contains(imageNode.getId())) {
                        //该节点不在数据库中
                        TopoBusinessNodeEntity businessNode = new TopoBusinessNodeEntity();
                        businessNode.setCanvasId(canvasId);
                        businessNode.setUuid(imageNode.getId());
                        businessNode.setNodeName(imageNode.getName());
                        businessNode.setXPoint(10);
                        businessNode.setYPoint(10);
                        dao.insertTopoBusinessNode(businessNode);
                    }
                });
                List<TopoBusinessLinkEntity> existLinkList = dao.getAllBusinessLinkByCanvasId(canvasId);
                List<String> existLink = new ArrayList<>();
                existLinkList.forEach(x -> {
                    existLink.add(x.getFromNodeId() + ";" + x.getToNodeId());
                });
                List<String> allLink = new ArrayList<>();
                list.forEach(imageNode -> {
                    if (null != imageNode.getAdjacency() && imageNode.getAdjacency().size() > 0) {
                        imageNode.getAdjacency().forEach(adj -> {
                            allLink.add(imageNode.getId() + ";" + adj);
                        });
                    }
                });
                allLink.forEach(x -> {
                    if (!existLink.contains(x)) {
                        //插入新链路
                        TopoBusinessLinkEntity linkEntity = new TopoBusinessLinkEntity();
                        linkEntity.setUuid(UUID.randomUUID().toString());
                        linkEntity.setCanvasId(canvasId);
                        linkEntity.setFromNodeId(x.split(";")[0]);
                        linkEntity.setToNodeId(x.split(";")[1]);
                        dao.insertTopoBusinessLink(linkEntity);
                    }
                });

            } else {
                //不存在 则存储新的canvas 所有的node 所有的link
//                Map<String,String> nameToUuid = new HashMap<>();
                TopoCanvasEntity canvas = new TopoCanvasEntity();
                String canvasuuid = UUID.randomUUID().toString();
                canvas.setUuid(canvasuuid);
                canvas.setCanvasName(clusterId);
                canvas.setCanvasType(TopoEnum.CanvasType.CANVAS_BUSINESS.value());
                dao.insertTopoCanvas(canvas);
                list.forEach(imageNode -> {
//                    nameToUuid.put(imageNode.getName(),imageNode.getId());
                    TopoBusinessNodeEntity businessNode = new TopoBusinessNodeEntity();
                    businessNode.setCanvasId(canvasuuid);
                    businessNode.setUuid(imageNode.getId());
                    businessNode.setNodeName(imageNode.getName());
                    businessNode.setXPoint(10);
                    businessNode.setYPoint(10);
                    dao.insertTopoBusinessNode(businessNode);
                });
                list.forEach(imageNode -> {
                    if (null != imageNode.getAdjacency() && imageNode.getAdjacency().size() > 0) {
                        imageNode.getAdjacency().forEach(adj -> {
                            TopoBusinessLinkEntity linkEntity = new TopoBusinessLinkEntity();
                            linkEntity.setUuid(UUID.randomUUID().toString());
                            linkEntity.setCanvasId(canvasuuid);
                            linkEntity.setFromNodeId(imageNode.getId());
                            linkEntity.setToNodeId(adj);
                            dao.insertTopoBusinessLink(linkEntity);
                        });
                    }
                });
            }
        });
    }

    @Override
    public List<TopoBusinessNodeEntity> getAllWeaveTopoNode() {
        return dao.getAllWeaveTopoNode();
    }

    @Override
    public List<TopoBusinessLinkEntity> getAllWeaveTopoLink() {
        return dao.getAllWeaveTopoLink();
    }

    @Override
    public List<TopoNodeEntity> getTopoNodeByCanvasId(String canvasId) {
        return dao.getAllNodeByCanvasId(canvasId);
    }

    @Override
    public List<TopoLinkEntity> getTopoLinkByCanvasId(String canvasId) {
        return dao.getAllLinkByCanvasId(canvasId);
    }

    @Override
    public List<TopoCanvasEntity> getCanvasByType(String name) {
        return dao.getCanvasByType(name);
    }

    @Override
    public List<TopoPortEntity> getAllPorts() {
        return dao.getAllPorts();
    }

    @Override
    public boolean deleteTopoResourceBymonitoruuid(String monitorUuid) {

        //通过node找到端口
        //删除以该端口为链路源和目的的链路
        //删除端口
        //删除node
        List<TopoNodeEntity> nodes = dao.getTopoNodeBymonitoruuid(monitorUuid);
        nodes.forEach(node -> {
            List<TopoPortEntity> ports = dao.getAllPortByNodeId(node.getUuid());
            ports.forEach(port -> {
                dao.deleteTopoLinkByPort(port.getUuid());
            });
            dao.deleteTopoPortByNodeUuid(node.getUuid());
        });

        return dao.deleteTopoNodeBymonitoruuid(monitorUuid);
    }

    @Override
    public List<TopoLinkRateView> getCanvasLinkRate(String canvasId, String linkRate) {
        List<TopoLinkEntity> linkList = dao.getAllLinkByCanvasId(canvasId);
        List<TopoNodeEntity> nodeList = dao.getAllNodeByCanvasId(canvasId);
        Map<String, TopoNodeEntity> portToNode = new HashMap<>();
        Map<String, TopoPortEntity> portMap = new HashMap<>();
        nodeList.forEach(x -> {
            List<TopoPortEntity> portList = dao.getAllPortByNodeId(x.getUuid());
            portList.forEach(y -> {
                portMap.put(y.getUuid(), y);
                portToNode.put(y.getUuid(), x);
            });
        });


        List<TopoLinkRateView> view = new ArrayList<>();
        //先弄一个portid和nodeid的map
        linkList.forEach(link -> {
            String fromportId = link.getFromPortId();
            String toPortId = link.getToPortId();
            TopoPortEntity fromPort = null;
            TopoPortEntity toPort = null;
            if (portToNode.containsKey(fromportId) && portToNode.containsKey(toPortId)) {
                TopoNodeEntity fromNode = portToNode.get(fromportId);
                TopoNodeEntity toNode = portToNode.get(toPortId);
                String fromNodeId = fromNode.getUuid();
                String toNodeId = toNode.getUuid();
                Optional<DeviceInterface> fromInterface = Optional.empty();
                Optional<DeviceInterface> toInterface = Optional.empty();
                if (portMap.containsKey(fromportId)) {
                    InterfaceInfo fromInterfaceInfo = monitorService.getExporterInterfaceInfo(fromNode.getMonitorUuid());
                    fromPort = portMap.get(fromportId);
                    TopoPortEntity finalFromPort1 = fromPort;
                    fromInterface = fromInterfaceInfo.getInterfaces().stream().filter(x -> x.getDescr().equals(finalFromPort1.getPort())).findFirst();
                }
                if (portMap.containsKey(toPortId)) {
                    InterfaceInfo toInterfaceInfo = monitorService.getExporterInterfaceInfo(toNode.getMonitorUuid());
                    toPort = portMap.get(toPortId);
                    TopoPortEntity finalToPort1 = toPort;
                    toInterface = toInterfaceInfo.getInterfaces().stream().filter(x -> x.getDescr().equals(finalToPort1.getPort())).findFirst();
                }
                if (fromInterface.isPresent() && toInterface.isPresent()){
                    TopoLinkRateView linkRateView = new TopoLinkRateView();
                    linkRateView.setUuid(link.getUuid());
                    if (fromInterface.get().getOperStatus().equals("1") && toInterface.get().getOperStatus().equals("1")){
                        //同时正常 才可获取流量
                        linkRateView.setLinkStatus("1");
                        QuotaInfo fromquotaInfo = null;
                        QuotaInfo toquotaInfo = null;
                        if (linkRate.equals("in")){
                            //进速率
                            fromquotaInfo = monitorService.getInterfaceRate(fromNode.getMonitorUuid(),TopoEnum.QuoatName.IN_OCTETS_RATE.value());
                            toquotaInfo = monitorService.getInterfaceRate(toNode.getMonitorUuid(),TopoEnum.QuoatName.IN_OCTETS_RATE.value());
                        }else if (linkRate.equals("out")){
                            //出速率
                            fromquotaInfo = monitorService.getInterfaceRate(fromNode.getMonitorUuid(),TopoEnum.QuoatName.OUT_OCTETS_RATE.value());
                            toquotaInfo = monitorService.getInterfaceRate(toNode.getMonitorUuid(),TopoEnum.QuoatName.OUT_OCTETS_RATE.value());
                        }
                        QuotaItemInfo fromiteminfo = fromquotaInfo.getItemInfo();
                        QuotaItemInfo toiteminfo = toquotaInfo.getItemInfo();
                        Optional<QuotaItemData> fromdata = Optional.empty();
                        Optional<QuotaItemData> todata = Optional.empty();
                        if (fromiteminfo!=null){
                            TopoPortEntity finalFromPort = fromPort;
                            fromdata =fromiteminfo.getItemData().stream().filter(x->x.getName().equals(finalFromPort.getPort())).findFirst();
                        }
                        if (toiteminfo!=null){
                            TopoPortEntity finalToPort = toPort;
                            todata =toiteminfo.getItemData().stream().filter(x->x.getName().equals(finalToPort.getPort())).findFirst();
                        }
                        if (fromdata.isPresent()){
                            linkRateView.setFormNodeRate(str2float2(fromdata.get().getValue()));
                        }
                        if (todata.isPresent()){
                            linkRateView.setToNodeRate(str2float2(todata.get().getValue()));
                        }

                    }else {
                        linkRateView.setLinkStatus("0");
                    }
                    view.add(linkRateView);
                }
            }

        });

        return view;
    }

    @Override
    public boolean insertTopoNodeList(List<TopoNodeEntity> nodes) {
        nodes.forEach(x->{
            dao.insertTopoNode(x);
        });
        return true;
    }


    /**
     * 将str转为float保留两位小数
     * @param str
     * @return
     */
    private String str2float2(String str) {
        Double d = Double.parseDouble(str);
        BigDecimal b = new BigDecimal(d);
//        float df = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        double df = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        return String.valueOf(df);
    }


}

package com.gy.topologyCore.service;

import com.gy.topologyCore.entity.QuotaInfo;
import com.gy.topologyCore.entity.snmp.InterfaceInfo;
import com.gy.topologyCore.entity.snmp.LldpInfos;
import com.gy.topologyCore.entity.monitor.NetworkMonitorEntity;

import java.util.concurrent.CompletionStage;

/**
 * Created by gy on 2018/5/5.
 */
public interface MonitorService {
    public CompletionStage<NetworkMonitorEntity> getNetworkMonitorEntity(String uuid);

//    public List<MiddleTypeEntity> getMiddleTypeEntity();

    public CompletionStage<LldpInfos> getExporterLldpInfo();

//    public List<LightTypeEntity> getLightTypeEntity();

    /**
     * 获取设备的网口信息
     * @param monitoruuid
     * @return
     */
    InterfaceInfo getExporterInterfaceInfo(String monitoruuid);

    /**
     * 获取设备端口流量
     * @param monitorUuid
     * @param quotaName
     * @return
     */
    QuotaInfo getInterfaceRate(String monitorUuid, String quotaName);
}

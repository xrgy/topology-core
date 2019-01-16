package com.gy.topologyCore.service;

import com.gy.topologyCore.entity.lldp.LldpInfos;
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
}

package com.gy.topologyCore.service;

import com.gy.topologyCore.entity.lldp.LldpInfos;
import com.gy.topologyCore.entity.monitor.LightTypeEntity;
import com.gy.topologyCore.entity.monitor.MiddleTypeEntity;
import com.gy.topologyCore.entity.monitor.OperationMonitorEntity;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Created by gy on 2018/5/5.
 */
public interface MonitorService {
    public CompletionStage<OperationMonitorEntity> getOperationMonitorEntity(String uuid);

    public List<MiddleTypeEntity> getMiddleTypeEntity();

    public CompletionStage<LldpInfos> getExporterLldpInfo(String uuid);

    public List<LightTypeEntity> getLightTypeEntity();
}

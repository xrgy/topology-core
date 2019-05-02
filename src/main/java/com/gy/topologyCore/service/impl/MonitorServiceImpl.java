package com.gy.topologyCore.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gy.topologyCore.entity.QuotaInfo;
import com.gy.topologyCore.entity.snmp.InterfaceInfo;
import com.gy.topologyCore.entity.snmp.LldpInfos;
import com.gy.topologyCore.entity.monitor.NetworkMonitorEntity;
import com.gy.topologyCore.service.MonitorService;
import com.gy.topologyCore.utils.EtcdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Created by gy on 2018/5/5.
 */
@Service
public class MonitorServiceImpl implements MonitorService {

//    private String IP = "http://127.0.0.1";
    private String PORT = "8084";
    private String PREFIX = "monitor";
    private String PATH_NETWORK_MONITOR_RECORD = "getNetworkMonitorRecord";
    private String PATH_LLDP_INFO = "getExporterLldpInfo";
    private String PATH_DEVICE_INTERFACE_INFO = "getExporterInterfaceInfo";
    private String PATH_DEVICE_INTERFACE_RATE = "getInterfaceRate";
    private static final String HTTP="http://";


    @Autowired
    ObjectMapper mapper;


    @Bean
    public RestTemplate rest(){
        return new RestTemplate();
    }

    private String monitorPrefix(){
        String ip = "";
//        try {
            ip="127.0.0.1";
//            ip = EtcdUtil.getClusterIpByServiceName("monitor-core-service");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return HTTP+"47.105.64.176:30004/"+PREFIX+"/";

        return HTTP+ip+":"+PORT+"/"+PREFIX+"/";
    }


    @Override
    public CompletionStage<NetworkMonitorEntity> getNetworkMonitorEntity(String uuid) {
        return CompletableFuture.supplyAsync(()-> {
            ResponseEntity<String> response = rest().getForEntity(monitorPrefix() + PATH_NETWORK_MONITOR_RECORD + "?uuid=" + uuid, String.class);
            try {
                return mapper.readValue(response.getBody(), NetworkMonitorEntity.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }
//
//    @Override
//    public List<MiddleTypeEntity> getMiddleTypeEntity() {
//        ResponseEntity<String> response = rest().getForEntity(monitorPrefix()+MIDDLE_PATH,String.class);
//        try {
//            return mapper.readValue(response.getBody(),new TypeReference<List<MiddleTypeEntity>>(){});
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


    @Override
    public CompletionStage<LldpInfos> getExporterLldpInfo() {
        return CompletableFuture.supplyAsync(()->{
            ResponseEntity<String> response = rest().getForEntity(monitorPrefix()+PATH_LLDP_INFO,String.class);
            try {
                return mapper.readValue(response.getBody(),LldpInfos.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });

    }

    @Override
    public InterfaceInfo getExporterInterfaceInfo(String monitoruuid) {
        ResponseEntity<String> response = rest().getForEntity(monitorPrefix()+PATH_DEVICE_INTERFACE_INFO+"?monitorUuid={1}",String.class,monitoruuid);
        try {
            return mapper.readValue(response.getBody(),InterfaceInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public QuotaInfo getInterfaceRate(String monitorUuid, String quotaName) {
        ResponseEntity<String> response = rest().getForEntity(monitorPrefix()+PATH_DEVICE_INTERFACE_RATE+"?monitorUuid={1}&quotaName={2}",String.class,monitorUuid,quotaName);
        try {
            return mapper.readValue(response.getBody(),QuotaInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//    @Override
//    public List<LightTypeEntity> getLightTypeEntity() {
//         ResponseEntity<String> response = rest().getForEntity(monitorPrefix()+Light_PATH,String.class);
//            try {
//                return mapper.readValue(response.getBody(),new TypeReference<List<LightTypeEntity>>(){});
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        };

}

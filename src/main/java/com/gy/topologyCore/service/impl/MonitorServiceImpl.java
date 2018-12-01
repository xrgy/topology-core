package com.gy.topologyCore.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gy.topologyCore.entity.lldp.LldpInfo;
import com.gy.topologyCore.entity.lldp.LldpInfos;
import com.gy.topologyCore.entity.monitor.LightTypeEntity;
import com.gy.topologyCore.entity.monitor.MiddleTypeEntity;
import com.gy.topologyCore.entity.monitor.OperationMonitorEntity;
import com.gy.topologyCore.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.Id;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Created by gy on 2018/5/5.
 */
@Service
public class MonitorServiceImpl implements MonitorService {

    private String IP = "http://127.0.0.1";
    private String PORT = "8084";
    private String PREFIX = "monitor";
    private String RECORD_PATH = "getMonitorRecord";
    private String SNMPEXPORTER_PORT = "9106";
    private String LLDP_PREFIX = "";
    private String MIDDLE_PATH = "getMiddleType";
    private String Light_PATH = "getLightType";

    @Autowired
    ObjectMapper mapper;


    @Bean
    public RestTemplate rest(){
        return new RestTemplate();
    }

    private String monitorPrefix(){
        return IP+":"+PORT+"/"+PREFIX+"/";
    }

    private String snmpExporterPrefix(){
        return IP+":"+SNMPEXPORTER_PORT+"/";
    }

    @Override
    public CompletionStage<OperationMonitorEntity> getOperationMonitorEntity(String uuid) {
        return CompletableFuture.supplyAsync(()-> {
            ResponseEntity<String> response = rest().getForEntity(monitorPrefix() + RECORD_PATH + "?uuid=" + uuid, String.class);

            try {
                return mapper.readValue(response.getBody(), OperationMonitorEntity.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public List<MiddleTypeEntity> getMiddleTypeEntity() {
        ResponseEntity<String> response = rest().getForEntity(monitorPrefix()+MIDDLE_PATH,String.class);
        try {
            return mapper.readValue(response.getBody(),new TypeReference<List<MiddleTypeEntity>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public CompletionStage<LldpInfos> getExporterLldpInfo(String uuid) {
        return CompletableFuture.supplyAsync(()->
                rest().getForEntity(snmpExporterPrefix()+LLDP_PREFIX+"/"+uuid,LldpInfos.class).getBody());
    }

    @Override
    public List<LightTypeEntity> getLightTypeEntity() {
         ResponseEntity<String> response = rest().getForEntity(monitorPrefix()+Light_PATH,String.class);
            try {
                return mapper.readValue(response.getBody(),new TypeReference<List<LightTypeEntity>>(){});
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        };

}

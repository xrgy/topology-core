package com.gy.topologyCore.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gy.topologyCore.entity.weave.WeaveContainerImageCluster;
import com.gy.topologyCore.service.WeaveScopeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

/**
 * Created by gy on 2018/12/17.
 */
@Service
public class WeaveScopeServiceImpl implements WeaveScopeService{


    private static final String HTTP="http://";
    private static final String PORT = "8088";
    private static final String PREFIX = "business";
    private static final String PATH_WEAVE_INFO = "getWeaveInfo";

    private static final String ip="127.0.0.1";
    @Autowired
    ObjectMapper mapper;



    private String businessPrefix(){
//        String ip = "";
//        try {
//            ip = EtcdUtil.getClusterIpByServiceName("business-core-service");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return HTTP+ip+":"+PORT+"/"+PREFIX+"/";
    }


    @Bean
    public RestTemplate rest() {
        return new RestTemplate();
    }


    @Override
    public WeaveContainerImageCluster getWeaveInfoFromBusiness() {
        ResponseEntity<String> response = rest().getForEntity(businessPrefix()+PATH_WEAVE_INFO,String.class);
        try {
            return mapper.readValue(response.getBody(),WeaveContainerImageCluster.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

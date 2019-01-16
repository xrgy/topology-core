package com.gy.topologyCore.entity.monitor;

import lombok.Getter;
import lombok.Setter;


/**
 * Created by gy on 2018/5/5.
 */
@Getter
@Setter
public class NetworkMonitorEntity {

    private String uuid;

    private String name;

    private String ip;

    private String snmpVersion;

    private String readCommunity;

    private String writeCommunity;

    private String port;

    private String lightType;

    private String monitorType;

    private String scrapeInterval;

    private String scrapeTimeout;

    private String templateId;

}

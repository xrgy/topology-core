package com.gy.topologyCore.entity.snmp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2019/1/18.
 */
@Getter
@Setter
public class DeviceInterface {

    //网口索引
    @JsonProperty("ifindex")
    private String index;


    //网口名称
    @JsonProperty("ifdescr")
    private String descr;

    //网口类型
    @JsonProperty("iftype")
    private String type;


    //网口管理状态
    @JsonProperty("ifadminstatus")
    private String adminStatus;

    //网口操作状态
    @JsonProperty("ifoperstatus")
    private String operStatus;
}

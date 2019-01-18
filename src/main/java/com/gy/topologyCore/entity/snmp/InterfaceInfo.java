package com.gy.topologyCore.entity.snmp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by gy on 2019/1/18.
 */
@Getter
@Setter
public class InterfaceInfo {

    @JsonProperty("interfaces")
    List<DeviceInterface> interfaces;
}

package com.gy.topologyCore.entity.lldp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2018/5/5.
 */
@Getter
@Setter
public class LocalInfo {

    @JsonProperty("localportindex")
    String localPortIndex;

    @JsonProperty("localchassismac")
    String localPortMac;

    @JsonProperty("localportname")
    String localPortName;
}

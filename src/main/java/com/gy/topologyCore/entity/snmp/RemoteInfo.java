package com.gy.topologyCore.entity.snmp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2018/5/5.
 */
@Getter
@Setter
public class RemoteInfo {
    @JsonProperty("remlocalindex")
    String remLocalIndex;

    @JsonProperty("remchassismac")
    String remPortMac;

    @JsonProperty("remportname")
    String remPortName;
}

package com.gy.topologyCore.entity.lldp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by gy on 2018/5/5.
 */
@Getter
@Setter
public class LldpInfo {

    @JsonProperty("uuid")
    String monitorUuid;

    @JsonProperty("ip")
    String ip;

    @JsonProperty("localinfos")
    List<LocalInfo> localInfos;

    @JsonProperty("reminfos")
    List<RemoteInfo> remInfos;
}

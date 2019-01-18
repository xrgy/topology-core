package com.gy.topologyCore.entity.snmp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by gy on 2018/5/5.
 */

@Getter
@Setter
public class LldpInfos {

    @JsonProperty("lldpinfos")
    List<LldpInfo> lldpInfos;
}

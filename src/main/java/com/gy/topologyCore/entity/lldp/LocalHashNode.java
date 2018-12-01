package com.gy.topologyCore.entity.lldp;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by gy on 2018/6/10.
 */
@Getter
@Setter
public class LocalHashNode {

    private String ip;

    private String monitorUuid;

    private String localPortName;

    private String localPortIndex;

    private String localPortMac;
}

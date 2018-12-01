package com.gy.topologyCore.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by gy on 2018/5/5.
 */
@Data
@Entity
@Table(name = "tbl_topo_port")
public class TopoPortEntity {

    @Id
    @Column(name = "uuid")
    String uuid;

    @Column(name = "port")
    String port;

    @Column(name = "node_uuid")
    String nodeUuid;

}

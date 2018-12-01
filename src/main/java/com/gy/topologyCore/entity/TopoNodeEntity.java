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
@Table(name = "tbl_topo_node")
public class TopoNodeEntity {

    @Id
    @Column(name = "uuid")
    String uuid;

    @Column(name = "canvas_id")
    String canvasId;

    @Column(name = "node_name")
    String nodeName;

    @Column(name = "monitor_uuid")
    String monitorUuid;

    @Column(name = "x_point")
    int xPoint;

    @Column(name = "y_point")
    int yPoint;

    @Column(name = "node_type")
    String nodeType;

    @Column(name = "extra")
    String extra;


}

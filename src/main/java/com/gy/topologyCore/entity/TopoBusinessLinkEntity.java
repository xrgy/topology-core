package com.gy.topologyCore.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by gy on 2018/5/5.
 */
@Data
@Entity
@Table(name = "tbl_businesslink")
public class TopoBusinessLinkEntity {

    @Id
    @Column(name = "uuid")
    String uuid;

    @Column(name = "from_node_id")
    String fromNodeId;

    @Column(name = "to_node_id")
    String toNodeId;

    @Column(name = "canvas_id")
    String canvasId;
}

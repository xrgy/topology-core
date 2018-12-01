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
@Table(name = "tbl_topo_link")
public class TopoLinkEntity {

    @Id
    @Column(name = "uuid")
    String uuid;

    @Column(name = "canvas_id")
    String canvasId;

    @Column(name = "fromNode_id")
    String fromNodeId;

    @Column(name = "toNode_id")
    String toNodeId;

    @Column(name = "fromPort_id")
    String fromPortId;

    @Column(name = "toPort_id")
    String toPortId;
}

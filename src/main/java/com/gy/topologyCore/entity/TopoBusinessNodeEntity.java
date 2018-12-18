package com.gy.topologyCore.entity;

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
@Table(name = "tbl_businessnode")
public class TopoBusinessNodeEntity {

    @Id
    @Column(name = "uuid")
    String uuid;

    @Column(name = "canvas_id")
    String canvasId;

    @Column(name = "node_name")
    String nodeName;

    @Column(name = "x_point")
    int xPoint;

    @Column(name = "y_point")
    int yPoint;

    @Column(name = "extra")
    String extra;


}

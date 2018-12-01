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
@Table(name = "tbl_topo_canvas")
public class TopoCanvasEntity {

    @Id
    @Column(name = "uuid")
    String uuid;

    @Column(name = "canvas_type")
    String canvasType;

    @Column(name = "canvas_name")
    String canvasName;

    @Column(name = "description")
    String description;

    @Column(name = "configuration")
    String configuration;
}

package com.gy.topologyCore.entity.weave;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by gy on 2018/12/17.
 */
@Getter
@Setter
public class WeaveContainerImage {


    private String id;

    private String name;

    private List<String> adjacency;

}

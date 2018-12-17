package com.gy.topologyCore.entity.weave;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Created by gy on 2018/12/17.
 */
@Getter
@Setter
public class WeaveContainerImageCluster {

    private Map<String,List<WeaveContainerImage>> nodes;
}

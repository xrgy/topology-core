package com.gy.topologyCore.service;

import com.gy.topologyCore.entity.TestEntity;

/**
 * Created by gy on 2018/3/31.
 */
public interface TopoService {
    public TestEntity getJPAInfo();

    public void getLldpInfo();
}

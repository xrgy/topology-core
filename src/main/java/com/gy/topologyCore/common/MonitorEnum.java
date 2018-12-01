package com.gy.topologyCore.common;

/**
 * Created by gy on 2018/5/6.
 */
public interface MonitorEnum {

   public enum MonitorType{

       NETWORK_DEVICE("network_device");

       String name;

       MonitorType(String name) {
           this.name = name;
       }
   }
}

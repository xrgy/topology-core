package com.gy.topologyCore.dao.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gy.topologyCore.dao.TopoDao;
import com.gy.topologyCore.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Created by gy on 2018/3/31.
 */
@Repository
public class TopoDaoImpl implements TopoDao {

//    @Autowired
//    @Qualifier("database")
//    Executor executor;

    @Autowired
    @PersistenceContext
    EntityManager em;

    @Override
    public TestEntity getJPAInfo() {
        List<TestEntity> result = em.createQuery("FROM TestEntity", TestEntity.class)
                .getResultList();
        if (result.size() == 0) {
            return null;
        }
        return result.get(0);
    }

    @Override
    public TopoCanvasEntity getWholeTopoCanvas() {
        String sql = "From TopoCanvasEntity canvas WHERE canvas.canvasType = :wholeTopo";
        return em.createQuery(sql, TopoCanvasEntity.class)
                .setParameter("wholeTopo", "wholetopo")
                .getSingleResult();
    }

    @Override
    public CompletionStage<Optional<TopoNodeEntity>> getNodeByMonitorAndCanvas(String monitorUUid, String canvasId) {

        String sql = "From TopoNodeEntity node WHERE node.uuid = :monitoruuid AND node.canvasId = :canvasid";
        return CompletableFuture.supplyAsync(() -> {
            Query query = em.createQuery(sql, TopoNodeEntity.class)
                    .setParameter("monitoruuid", monitorUUid)
                    .setParameter("canvasid", canvasId);
            if (query.getResultList().size() == 0) {
                return null;
            } else {
                return Optional.of((TopoNodeEntity) query.getSingleResult());
            }
        });
    }

    @Override
    @Transactional
    public TopoNodeEntity insertTopoNode(TopoNodeEntity node) {
        return em.merge(node);
    }

    @Override
    @Transactional
    public TopoPortEntity insertTopoPort(TopoPortEntity port) {
        return em.merge(port);
    }

    @Override
    @Transactional
    public TopoLinkEntity insertTopoLink(TopoLinkEntity link) {
        return em.merge(link);
    }

    @Override
    public List<TopoNodeEntity> getAllNodeByCanvasId(String uuid) {
        String sql = "FROM TopoNodeEntity node WHERE node.canvasId =:canvasId";
        return em.createQuery(sql, TopoNodeEntity.class)
                .setParameter("canvasId", uuid)
                .getResultList();
    }

    @Override
    public List<TopoLinkEntity> getAllLinkByCanvasId(String uuid) {
        String sql = "FROM TopoLinkEntity link WHERE link.canvasId =:canvasId";
        return em.createQuery(sql, TopoLinkEntity.class)
                .setParameter("canvasId", uuid)
                .getResultList();
    }

    @Override
    public List<TopoPortEntity> getAllPortByNodeId(String uuid) {
        String sql = "FROM TopoPortEntity port WHERE port.nodeUuid =:nodeId";
        return em.createQuery(sql, TopoPortEntity.class)
                .setParameter("nodeId", uuid)
                .getResultList();
    }
    @Override
    public List<TopoPortEntity> getAllPorts() {
        String sql = "FROM TopoPortEntity";
        return em.createQuery(sql, TopoPortEntity.class)
                .getResultList();
    }

    @Override
    @Transactional
    @Modifying
    public void deleteLinkByCanvasId(String canvasId) {
        String sql = "delete from TopoLinkEntity link WHERE link.canvasId =:canvasId";
        em.createQuery(sql)
                .setParameter("canvasId", canvasId)
                .executeUpdate();
    }

    @Override
    @Transactional
    @Modifying
    public boolean deleteTopoLinkByPort(String port) {
        String sql = "delete from TopoLinkEntity link WHERE link.fromPortId =:port or " +
                " link.toPortId =:port";
        int res =em.createQuery(sql)
                .setParameter("port", port)
                .executeUpdate();
        return res > 0;
    }

    @Override
    @Transactional
    @Modifying
    public boolean deleteTopoLinkByUuid(String uuid) {
        String sql = "delete from TopoLinkEntity link WHERE link.uuid =:uuid";
        int res =em.createQuery(sql)
                .setParameter("uuid", uuid)
                .executeUpdate();
        return res > 0;
    }

    @Override
    @Transactional
    @Modifying
    public boolean deleteTopoPortByNodeUuid(String uuid) {
        String sql = "delete from TopoPortEntity port WHERE port.nodeUuid =:uuid";
        int res =em.createQuery(sql)
                .setParameter("uuid", uuid)
                .executeUpdate();
        return res > 0;
    }

    @Override
    @Transactional
    @Modifying
    public boolean deleteTopoNodeNotInUuids(List<String> uuids) {
        String sql = "DELETE from tbl_topo_node where uuid NOT in('"+ StringUtils.join(uuids,"','")+"')";
        int res = em.createNativeQuery(sql)
                .executeUpdate();
        return res > 0;
    }

    @Override
    @Transactional
    @Modifying
    public boolean deleteTopoPortNotInUuids(List<String> uuids) {
        String sql = "DELETE from tbl_topo_port where uuid NOT in('"+ StringUtils.join(uuids,"','")+"')";
        int res = em.createNativeQuery(sql)
                .executeUpdate();
        return res > 0;
    }

    @Override
    @Transactional
    @Modifying
    public boolean deleteTopoLinkNotInUuids(List<String> uuids) {
        String sql = "DELETE from tbl_topo_link where uuid NOT in('"+ StringUtils.join(uuids,"','")+"')";
        int res = em.createNativeQuery(sql)
                .executeUpdate();
        return res > 0;
    }

    @Override
    public TopoLinkEntity getlinkNetTopoLinkByUUid(String uuid) {
        String sql = "FROM TopoLinkEntity link WHERE link.uuid =:uuid";
        List<TopoLinkEntity> links =  em.createQuery(sql, TopoLinkEntity.class)
                .setParameter("uuid", uuid)
                .getResultList();
        if (links.size()>0){
            return links.get(0);
        }else {
            return null;
        }
    }

    @Override
    @Transactional
    @Modifying
    public boolean deleteTopoPortByUuid(String portId) {
        String sql = "delete from TopoPortEntity port WHERE port.uuid =:uuid";
        int res =em.createQuery(sql)
                .setParameter("uuid", portId)
                .executeUpdate();
        return res > 0;
    }

    @Override
    @Transactional
    @Modifying
    public boolean delBusinessTopoLink(String uuid) {
        String sql = "delete from TopoBusinessLinkEntity link WHERE link.fromNodeId =:uuid or link.toNodeId =:uuid";
        int res =em.createQuery(sql)
                .setParameter("uuid", uuid)
                .executeUpdate();
        return res > 0;
    }
    @Override
    @Transactional
    @Modifying
    public boolean delBusinessTopoLinkByUuid(String uuid) {
        String sql = "delete from TopoBusinessLinkEntity link WHERE link.uuid =:uuid";
        int res =em.createQuery(sql)
                .setParameter("uuid", uuid)
                .executeUpdate();
        return res > 0;
    }

    @Override
    @Transactional
    @Modifying
    public boolean delBusinessTopoNode(String uuid) {
        String sql = "delete from TopoBusinessNodeEntity node WHERE node.uuid =:uuid";
        int res =em.createQuery(sql)
                .setParameter("uuid", uuid)
                .executeUpdate();
        return res > 0;
    }

    @Override
    @Transactional
    public TopoCanvasEntity insertTopoCanvas(TopoCanvasEntity canvas) {
        return em.merge(canvas);
    }

    @Override
    @Transactional
    public TopoBusinessNodeEntity insertTopoBusinessNode(TopoBusinessNodeEntity node) {
        return em.merge(node);
    }

    @Override
    @Transactional
    public TopoBusinessLinkEntity insertTopoBusinessLink(TopoBusinessLinkEntity link) {
        return em.merge(link);
    }

    @Override
    public TopoCanvasEntity canvasIsExist(String name) {
        String sql = "From TopoCanvasEntity canvas WHERE canvas.canvasName =:name";
        List<TopoCanvasEntity> canvas = em.createQuery(sql, TopoCanvasEntity.class)
                .setParameter("name", name)
                .getResultList();
        if (canvas!=null && canvas.size()>0){
            return canvas.get(0);
        }else {
            return null;
        }
    }

    @Override
    public List<TopoBusinessNodeEntity> getAllBusinessNodeByCanvasId(String uuid) {
        String sql = "FROM TopoBusinessNodeEntity node WHERE node.canvasId =:canvasId";
        return em.createQuery(sql, TopoBusinessNodeEntity.class)
                .setParameter("canvasId", uuid)
                .getResultList();
    }

    @Override
    public List<TopoBusinessLinkEntity> getAllBusinessLinkByCanvasId(String uuid) {
        String sql = "FROM TopoBusinessLinkEntity link WHERE link.canvasId =:canvasId";
        return em.createQuery(sql, TopoBusinessLinkEntity.class)
                .setParameter("canvasId", uuid)
                .getResultList();
    }

    @Override
    public TopoBusinessNodeEntity getBusinessNodeByUuid(String uuid) {
        String sql = "FROM TopoBusinessNodeEntity node WHERE node.uuid =:uuid";
        List<TopoBusinessNodeEntity> entitys= em.createQuery(sql, TopoBusinessNodeEntity.class)
                .setParameter("uuid", uuid)
                .getResultList();
        if (entitys.size()>0){
                return entitys.get(0);
        }else {
            return null;
        }
    }

    @Override
    public TopoBusinessLinkEntity getBusinessLinkByUuid(String uuid) {
        String sql = "FROM TopoBusinessLinkEntity link WHERE link.uuid =:uuid";
        List<TopoBusinessLinkEntity> entitys= em.createQuery(sql, TopoBusinessLinkEntity.class)
                .setParameter("uuid", uuid)
                .getResultList();
        if (entitys.size()>0){
            return entitys.get(0);
        }else {
            return null;
        }
    }

    @Override
    public List<TopoCanvasEntity> getCanvasByType(String name) {
        String sql = "From TopoCanvasEntity canvas WHERE canvas.canvasType =:name";
        return em.createQuery(sql, TopoCanvasEntity.class)
                .setParameter("name", name)
                .getResultList();
    }

    @Override
    public TopoCanvasEntity getCanvasByUUid(String uuid) {
        String sql = "From TopoCanvasEntity canvas WHERE canvas.uuid =:uuid";
        List<TopoCanvasEntity> canvass =  em.createQuery(sql, TopoCanvasEntity.class)
                .setParameter("uuid", uuid)
                .getResultList();
        if (canvass.size()>0){
            return canvass.get(0);
        }else {
            return null;
        }
    }

    @Override
    public List<TopoBusinessNodeEntity> getAllWeaveTopoNode() {
        String sql = "FROM TopoBusinessNodeEntity";
        return em.createQuery(sql, TopoBusinessNodeEntity.class)
                .getResultList();
    }

    @Override
    public List<TopoBusinessLinkEntity> getAllWeaveTopoLink() {
        String sql = "FROM TopoBusinessLinkEntity";
        return em.createQuery(sql, TopoBusinessLinkEntity.class)
                .getResultList();
    }

    @Override
    @Transactional
    @Modifying
    public boolean deleteTopoNodeBymonitoruuid(String monitorUuid) {
        String sql = "DELETE FROM TopoNodeEntity WHERE uuid =:monitorUuid";
        int res = em.createQuery(sql)
                .setParameter("monitorUuid", monitorUuid)
                .executeUpdate();
        return res > 0;
    }

    @Override
    public List<TopoNodeEntity> getTopoNodeBymonitoruuid(String monitorUuid) {
        String sql = "FROM TopoNodeEntity node WHERE node.uuid =:monitorUuid";
        return em.createQuery(sql, TopoNodeEntity.class)
                .setParameter("monitorUuid", monitorUuid)
                .getResultList();
    }
}

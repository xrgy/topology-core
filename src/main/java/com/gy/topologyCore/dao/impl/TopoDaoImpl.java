package com.gy.topologyCore.dao.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gy.topologyCore.dao.TopoDao;
import com.gy.topologyCore.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

        String sql = "From TopoNodeEntity node WHERE node.monitorUuid = :monitoruuid AND node.canvasId = :canvasid";
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
    public TopoNodeEntity insertTopoNode(TopoNodeEntity node) {
        return em.merge(node);
    }

    @Override
    public TopoPortEntity insertTopoPort(TopoPortEntity port) {
        return em.merge(port);
    }

    @Override
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
    public void deleteLinkByCanvasId(String canvasId) {
        String sql = "delete from TopoLinkEntity link WHERE link.canvasId =:canvasId";
        em.createQuery(sql)
                .setParameter("canvasId",canvasId)
                .executeUpdate();
    }

    @Override
    public void removeLinkByNodeAndPort(String nodeId, String port) {
        String sql = "delete from TopoLinkEntity link WHERE (link.fromNodeId =:node and link.fromPortId =:port) or " +
                "(link.toNodeId =:node and link.toPortId =:port)";
        em.createQuery(sql)
                .setParameter("node",nodeId)
                .setParameter("port",port)
                .executeUpdate();
    }
}

package org.example.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.example.dto.TrainerWorkloadRequest;
import org.example.entities.WorkloadOutbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class OutboxDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(OutboxDao.class);

    private final ObjectMapper objectMapper;

    @PersistenceContext
    private EntityManager em;

    public OutboxDao(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<WorkloadOutbox> findByStatus(String status) {
        TypedQuery<WorkloadOutbox> query =
                em.createQuery(
                        "SELECT w FROM WorkloadOutbox w WHERE w.status = :status",
                        WorkloadOutbox.class
                );

        query.setParameter("status", status);

        return query.getResultList();
    }

    public void update(WorkloadOutbox task) {
        em.merge(task);
        em.flush();
    }

    public void createPendingRequest(TrainerWorkloadRequest request) {
        String txId = MDC.get("transactionId");
            WorkloadOutbox outbox = new WorkloadOutbox();
            outbox.setTransactionId(txId);
            try {
                String json = objectMapper.writeValueAsString(request);
                outbox.setPayload(json);
            } catch (JsonProcessingException e) {
                LOGGER.error("JSON Error: {}. Path: {}", e.getMessage(), e.getStackTrace());
                throw new RuntimeException("Data format error in Outbox payload", e);
            }
            outbox.setStatus("PENDING");
            outbox.setCreatedAt(LocalDateTime.now());
            em.persist(outbox);
    }
}

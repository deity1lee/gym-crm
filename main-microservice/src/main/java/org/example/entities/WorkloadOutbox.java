package org.example.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "workload_outbox")
public class WorkloadOutbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionId; // From Point 8

    @Column(columnDefinition = "TEXT")
    private String payload; // JSON representation of TrainerWorkloadRequest

    private String status; // PENDING, SENT, FAILED

    private LocalDateTime createdAt;

    public WorkloadOutbox() {
    }

    public WorkloadOutbox(Long id, String transactionId, String payload, String status, LocalDateTime createdAt) {
        this.id = id;
        this.transactionId = transactionId;
        this.payload = payload;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
package com.bidding.platform.buyer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "requirement_documents")
@Data
public class RequirementDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String objectName;     // MinIO object name
    private String originalName;   // User uploaded name
    private String contentType;    // PDF / PNG
    private Long fileSize;

    @ManyToOne
    @JoinColumn(name = "requirement_id")
    private Requirements requirement;
}

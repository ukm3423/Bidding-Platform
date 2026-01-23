package com.bidding.platform.buyer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bidding.platform.buyer.model.RequirementDocument;

public interface RequirementDocumentRepository extends JpaRepository<RequirementDocument, Long> {
}

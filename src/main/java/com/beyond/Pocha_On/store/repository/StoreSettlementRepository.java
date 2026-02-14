package com.beyond.Pocha_On.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreSettlementRepository extends JpaRepository<com.beyond.Pocha_On.store.domain.StoreSettlement, Long> {

}

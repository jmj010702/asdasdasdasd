package com.beyond.Pocha_On.store.repository;

import com.beyond.Pocha_On.owner.domain.Owner;
import com.beyond.Pocha_On.store.domain.Store;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByOwner(Owner owner);
    Optional<Store> findByOwnerId(Long ownerId);
}

package com.beyond.Pocha_On.customerorder.repository;


import com.beyond.Pocha_On.ordering.domain.Ordering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Ordering,Long> {
}

package com.beyond.Pocha_On.ordering.repository;

import com.beyond.Pocha_On.ordering.domain.OrderingDetail;
import com.beyond.Pocha_On.ordering.domain.OrderingDetailOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderingDetailOptionRepository extends JpaRepository<OrderingDetailOption, Long> {
    List<OrderingDetailOption> findByOrderingDetail(OrderingDetail orderingDetail);
}

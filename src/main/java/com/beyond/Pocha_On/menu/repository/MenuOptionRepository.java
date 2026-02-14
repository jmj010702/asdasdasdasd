package com.beyond.Pocha_On.menu.repository;

import com.beyond.Pocha_On.menu.domain.MenuOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MenuOptionRepository extends JpaRepository<MenuOption, Long> {
    @Query(
            value = "select sum(price) from option where id in (:optionIds)",
            nativeQuery = true
    )

    int sumPriceByOptionIds(@Param("optionIds") List<Long> optionIds);
}

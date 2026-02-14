package com.beyond.Pocha_On.menu.repository;


import com.beyond.Pocha_On.menu.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuCategoryRepository extends JpaRepository<Category, Long> {

}
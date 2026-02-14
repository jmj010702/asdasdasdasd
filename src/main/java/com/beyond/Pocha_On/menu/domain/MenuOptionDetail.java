package com.beyond.Pocha_On.menu.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuOptionDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String optionDetailName;
    private int optionDetailPrice;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_option_id", foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT), nullable = false)
    private MenuOption menuOption;

    public void update(String detailName, int detailPrice) {
        this.optionDetailName = detailName;
        this.optionDetailPrice = detailPrice;
    }
}

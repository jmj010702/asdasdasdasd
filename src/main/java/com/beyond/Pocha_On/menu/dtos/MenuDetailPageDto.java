package com.beyond.Pocha_On.menu.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MenuDetailPageDto {
    private Long menuId;
    private String menuName;
    private int menuPrice;
    private int quantity;
    private List<mappingOption> mappingOptionList;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class mappingOption {
        private Long optionId;
        private String optionName;
        private List<mappingOptionDetail> mappingOptionDetailList;

        @AllArgsConstructor
        @NoArgsConstructor
        @Data
        @Builder
        public static class mappingOptionDetail {
            private Long optionDetailId;
            private String optionDetailName;
            private int optionDetailPrice;
        }
    }
}

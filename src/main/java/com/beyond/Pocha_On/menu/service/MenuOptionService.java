package com.beyond.Pocha_On.menu.service;

import com.beyond.Pocha_On.menu.domain.Menu;
import com.beyond.Pocha_On.menu.domain.MenuOption;
import com.beyond.Pocha_On.menu.dtos.MenuOptionReqDto;
import com.beyond.Pocha_On.menu.repository.MenuCategoryRepository;
import com.beyond.Pocha_On.menu.repository.MenuOptionRepository;
import com.beyond.Pocha_On.menu.repository.MenuRepository;
import com.beyond.Pocha_On.owner.domain.Owner;
import com.beyond.Pocha_On.owner.repository.OwnerRepository;
import com.beyond.Pocha_On.store.repository.StoreRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
public class MenuOptionService {
    private final OwnerRepository ownerRepository;
    private final HttpServletRequest request;
    private final MenuOptionRepository menuOptionRepository;
    private final StoreRepository storeRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuRepository menuRepository;
    @Autowired
    public MenuOptionService(OwnerRepository ownerRepository, HttpServletRequest request, MenuOptionRepository menuOptionRepository, StoreRepository storeRepository, MenuCategoryRepository menuCategoryRepository, MenuRepository menuRepository) {
        this.ownerRepository = ownerRepository;
        this.request = request;
        this.menuOptionRepository = menuOptionRepository;
        this.storeRepository = storeRepository;
        this.menuCategoryRepository = menuCategoryRepository;
        this.menuRepository = menuRepository;
    }

//    owner 메뉴옵션 추가
    public Long createOption(Long menuId, MenuOptionReqDto reqDto) throws AccessDeniedException {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Owner owner = ownerRepository.findByOwnerEmail(email).orElseThrow(() -> new EntityNotFoundException("Owner not found"));
        Long storeId = (Long) request.getAttribute("storeId");
        if (storeId == null) {
            throw new AccessDeniedException("매장 선택 후 이용가능합니다");
        }
        Menu menu = menuRepository.findById(menuId).orElseThrow(()-> new EntityNotFoundException("menu not found"));
        if (!menu.getStore().getId().equals(storeId)){
            throw new AccessDeniedException("해당 매장의 메뉴가 아닙니다");
        }
        if(!menu.getStore().getOwner().getId().equals(owner.getId())){
            throw new AccessDeniedException("해당 권한이 없습니다");
        }
        MenuOption menuOption = MenuOption.builder()
                .optionName(reqDto.getOptionName())   /// ///
                .menu(menu)
                .build();
        menuOptionRepository.save(menuOption);
        return menuOption.getId();
    }

//    owner 메뉴옵션 수정
    public void updateOption(Long optionId, MenuOptionReqDto reqDto) throws AccessDeniedException {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Owner owner = ownerRepository.findByOwnerEmail(email).orElseThrow(() -> new EntityNotFoundException("Owner not found"));
        Long storeId = (Long) request.getAttribute("storeId");
        MenuOption menuOption = menuOptionRepository.findById(optionId).orElseThrow(()-> new EntityNotFoundException("Option not found"));
        Menu menu = menuOption.getMenu();
        if (storeId == null) {
            throw new AccessDeniedException("매장 선택 후 이용가능합니다");
        }
        if (!menu.getStore().getId().equals(storeId)) {
            throw new AccessDeniedException("해당 매장의 메뉴가 아닙니다");
        }
        if (!menu.getStore().getOwner().getId().equals(owner.getId())) {
            throw new AccessDeniedException("해당 권한이 없습니다");
        }
        menuOption.update(reqDto.getOptionName());
    }

//    owner 메뉴옵션 삭제
    public void deleteOption(Long optionId) throws AccessDeniedException {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Owner owner = ownerRepository.findByOwnerEmail(email).orElseThrow(() -> new EntityNotFoundException("Owner not found"));
        Long storeId = (Long) request.getAttribute("storeId");
        MenuOption menuOption = menuOptionRepository.findById(optionId).orElseThrow(()-> new EntityNotFoundException("Option not found"));
        Menu menu = menuOption.getMenu();
        if (storeId == null) {
            throw new AccessDeniedException("매장 선택 후 이용가능합니다");
        }
        if (!menu.getStore().getId().equals(storeId)) {
            throw new AccessDeniedException("해당 매장의 메뉴가 아닙니다");
        }
        if (!menu.getStore().getOwner().getId().equals(owner.getId())) {
            throw new AccessDeniedException("해당 권한이 없습니다");
        }
        menuOptionRepository.delete(menuOption);


    }


}

package com.beyond.Pocha_On.common.auth;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

//구분 :  	    예전 필터	   지금 필터
//토큰 없음:    	그냥 통과	   인증 실패
//인증 책임:    	있으면 한다	  없으면 실패
//permitAll :    의존O	        의존 X
//보안 강도	:     느슨	        엄격
//실무 트렌드:	  과거	        현재

public class JwtTokenFilter extends OncePerRequestFilter {

    //    인가 예외 처리 url List
    private static final List<String> WHITE_LIST = List.of(
            "/owner/baseLogin",
            "/owner/refresh",
            "/owner/create",
            "/auth/email/send",
            "/auth/email/verify",
            "/auth/password/reset"
    );
    //    url별 접근 권한 설정을 위한 Map => 해당 stage 토큰이어야만 url 패턴에 맞게 api 접근 가능하도록 하기 위함.
    private static final Map<String, List<String>> STAGE_RULES = Map.of(
            "/owner/base", List.of("BASE"),
            "/store/select", List.of("BASE"),
            "/store", List.of("STORE"),
            "/customertable/select", List.of("STORE"),
            "/customertable", List.of("TABLE"),
            "/ordering", List.of("STORE") // 점주 주문 큐
    );
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return WHITE_LIST.stream().anyMatch(uri::startsWith);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        System.out.println("Authorization = " + request.getHeader("Authorization"));
        System.out.println("URI = " + request.getRequestURI());

        String uri = request.getRequestURI();

        if (WHITE_LIST.stream().anyMatch(uri::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        String bearerToken = request.getHeader("Authorization");

        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String accessToken = bearerToken.substring(7);

        try {
            Claims claims = jwtTokenProvider.validateAccessToken(accessToken);

            String email = claims.getSubject();
            String role = claims.get("role", String.class);
            String stage = claims.get("stage", String.class);
//            token에서 email, stage꺼내쓰기 위한 속성 부여
            request.setAttribute("email", email);
            request.setAttribute("stage", stage);

            if ("STORE".equals(stage) || "TABLE".equals(stage)) {
                Long storeId = claims.get("storeId", Long.class);
                request.setAttribute("storeId", storeId);
            }



//              stage 검사
            boolean matched = false;
            boolean allowed = false;

            for (Map.Entry<String, List<String>> entry : STAGE_RULES.entrySet()) {
                if (uri.startsWith(entry.getKey())) {
                    matched = true;
                    allowed = entry.getValue().contains(stage);
                    break;
                }
            }

            if (matched && !allowed) {
                response.sendError(403,"권한 없음");
                return;
            }

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }
}
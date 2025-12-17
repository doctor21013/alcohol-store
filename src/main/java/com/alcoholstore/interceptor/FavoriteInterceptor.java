package com.alcoholstore.interceptor;

import com.alcoholstore.service.FavoriteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class FavoriteInterceptor implements HandlerInterceptor {

    @Autowired
    private FavoriteService favoriteService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId");

        if (userId != null) {
            int favoriteCount = favoriteService.getFavoriteCount(userId);
            session.setAttribute("favoriteCount", favoriteCount);
        } else {
            session.setAttribute("favoriteCount", 0);
        }

        return true;
    }
}
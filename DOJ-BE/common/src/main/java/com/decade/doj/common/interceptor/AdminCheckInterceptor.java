package com.decade.doj.common.interceptor;

import com.decade.doj.common.annotation.AdminRequired;
import com.decade.doj.common.client.UserClient;
import com.decade.doj.common.domain.R;
import com.decade.doj.common.domain.vo.InfoVO;
import com.decade.doj.common.exception.ForbiddenException;
import com.decade.doj.common.exception.UnauthorizedException;
import com.decade.doj.common.utils.UserContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AdminCheckInterceptor implements HandlerInterceptor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private UserClient userClient;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        AdminRequired adminRequired = handlerMethod.getMethodAnnotation(AdminRequired.class);

        if (adminRequired == null) {
            return true;
        }

        Long userId = UserContext.getCurrentUser();
        if (userId == null) {
            throw new UnauthorizedException("用户未登录");
        }

        // Lazily get the UserClient bean from the context
        if (userClient == null) {
            this.userClient = applicationContext.getBean(UserClient.class);
        }

        R<InfoVO> userResult = userClient.getUser(userId);
        if (!userResult.success() || userResult.getData() == null) {
            throw new ForbiddenException("无法获取用户信息");
        }

        InfoVO userInfo = userResult.getData();
        if (userInfo.getRole()) {
            throw new ForbiddenException("无权访问，需要管理员权限");
        }

        return true;
    }
}

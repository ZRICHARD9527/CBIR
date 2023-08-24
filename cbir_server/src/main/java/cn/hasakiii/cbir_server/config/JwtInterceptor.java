package cn.hasakiii.cbir_server.config;


import cn.hasakiii.cbir_server.util.JwtUtils;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//拦截器
@Configuration
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //自动排除生成token的路径,并且如果是options请求是cors跨域预请求，设置allow对应头信息
        String myuri = request.getRequestURI();

        if (RequestMethod.OPTIONS.toString().equals(request.getMethod())) {
            System.out.println("自动排除生成token的路径");
            return true;
        }

        String token = request.getHeader("token");
        if (token == null || "".equals(token.trim())) {
            throw new ServletException("无法获取token");
        }
        try {
            DecodedJWT decodedJWT = JwtUtils.getTokenInfo(token);
            //管理员
            request.setAttribute("a_id", decodedJWT.getClaim("a_id").asString());
            request.setAttribute("account", decodedJWT.getClaim("account").asString());
            return true;
        } catch (Exception e) {
            throw new ServletException("令牌错误，请重新登录");
        }
    }

}

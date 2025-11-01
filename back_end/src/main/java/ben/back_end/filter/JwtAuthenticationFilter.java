package ben.back_end.filter;

import ben.back_end.util.JWTUtils;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        // 从 header 获取 token
        String header = request.getHeader("Authorization");
        DecodedJWT decodedJWT = jwtUtils.verifyJWT(header);

        // 校验成功才注入认证信息
        if (decodedJWT != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Integer userId = jwtUtils.toId(decodedJWT);
            String userName = decodedJWT.getClaim("userName").asString();

            // 提取 authorities 数组
            String[] authoritiesArray = decodedJWT.getClaim("authorities").asArray(String.class);
            List<SimpleGrantedAuthority> authorities = authoritiesArray == null
                    ? List.of()
                    : Arrays.stream(authoritiesArray)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userName, null, authorities);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 设置当前用户认证上下文
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // 放行请求
        filterChain.doFilter(request, response);
    }
}

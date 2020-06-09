package org.springframework.cloud.hystrixservice.filter;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * @program: demo
 * @description: Hystrix 请求上下文拦截器
 *
 * 在缓存使用过程中，我们需要在每次使用缓存的请求前后对HystrixRequestContext进行初始化和关闭，否则会出现如下异常:
 * java.lang.IllegalStateException: Request caching is not available. Maybe you need to initialize the HystrixRequestContext
 *
 *  我们通过使用过滤器，在每个请求前后初始化和关闭HystrixRequestContext来解决该问题
 *
 * @author: HyJan
 * @create: 2020-05-22 17:22
 **/
@Component
@WebFilter(urlPatterns = "/*", asyncSupported = true)
public class HystrixRequestContextFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try{
            filterChain.doFilter(servletRequest,servletResponse);
        }finally{
            context.close();
        }
    }
}

package org.springframework.cloud.hystrixservice.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheRemove;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.hystrixservice.entity.Result;
import org.springframework.cloud.hystrixservice.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;

/**
 * @program: demo
 * @description: 用户服务类
 * @author: HyJan
 * @create: 2020-05-22 17:46
 */
@Service
@Slf4j
public class UserService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${service-url.user-service}")
    private String userService;

    /**
     * 服务降级
     *
     * @param id
     * @return
     */
    @HystrixCommand(fallbackMethod = "getDefaultUser")
    public Result getUser(Long id) {
        return restTemplate.getForObject(
                new StringBuilder(userService).append("/user/{1}").toString(), Result.class, id);
    }

    public Result getDefaultUser(@PathVariable Long id) {
        User user = new User(-1L, "default", "default");
        return Result.success(user);
    }

    @HystrixCommand(
            fallbackMethod = "getDefaultUser2",
            ignoreExceptions = {NullPointerException.class})
    public Result getUserException(Long id) {
        if (Objects.equals(id, 1)) {
            throw new IllegalArgumentException();
        } else if (Objects.equals(id, 2)) {
            throw new IndexOutOfBoundsException();
        }
        return restTemplate.getForObject(
                new StringBuilder(userService).append("/user/{1}").toString(), Result.class, id);
    }

    public Result getDefaultUser2(@PathVariable Long id, Throwable e) {
        log.error("the id is {},throwable class is {}", id, e.getClass());
        User defaultUser = new User(-1L, "default", "default");
        return Result.success(defaultUser);
    }

    /**
     * @param id
     * @return
     * @HystrixCommand中的常用参数 fallbackMethod：指定服务降级处理方法；
     * ignoreExceptions：忽略某些异常，不发生服务降级；
     * commandKey：命令名称，用于区分不同的命令；
     * groupKey：分组名称，Hystrix会根据不同的分组来统计命令的告警及仪表盘信息；
     * threadPoolKey：线程池名称，用于划分线程池。
     */
    @HystrixCommand(
            fallbackMethod = "getDefaultUser",
            commandKey = "getUserCommand",
            groupKey = "getUserGroup",
            threadPoolKey = "getUserThreadPool")
    public Result getUserCommand(@PathVariable Long id) {
        log.info("user id is {}", id);
        return restTemplate.getForObject(
                new StringBuilder(userService).append("/user/{1}").toString(), Result.class, id);
    }

    /**
     * Hystrix的请求缓存
     *
     * @CacheResult：开启缓存，默认所有参数作为缓存的key，cacheKeyMethod可以通过返回String类型的方法指定key;
     * @CacheKey：指定缓存的key，可以指定参数或指定参数中的属性值为缓存key，cacheKeyMethod还可以通过返回String类型的方法指定;
     * @CacheRemove：移除缓存，需要指定commandKey。
     */
    @CacheResult(cacheKeyMethod = "getCacheKey")
    @HystrixCommand(fallbackMethod = "getDefaultUser", commandKey = "getUserCache")
    public Result getUserCache(Long id) {
        return restTemplate.getForObject(
                new StringBuilder(userService).append("/user/{1}").toString(), Result.class, id);
    }

    @CacheRemove(commandKey = "getUserCache", cacheKeyMethod = "getCacheKey")
    @HystrixCommand
    public Result removeCache(Long id){
        log.info("remove cache id is {}",id);
        return restTemplate.postForObject(new StringBuilder(userService).append("user/delete/{1}").toString(),
                null,Result.class,id);
    }

    /**
     * 为缓存生成key的方法
     * */
    public String getCacheKey(Long id){
        return String.valueOf(id);
    }

    /**
     * 方法的合并，每100毫秒进行一个合并，对每一个id请求，合并为ids的一次集中请求
     * @param id
     * @return
     */
    @HystrixCollapser(batchMethod = "getUserByIds",collapserProperties = {
            @HystrixProperty(name = "timerDelayInMilliseconds",value = "100")
    })
    public Future<User> getUserFuture(Long id){
        // 采用异步方式
        return new AsyncResult<User>() {
            @Override
            public User invoke() {
                Result result = restTemplate.getForObject(userService+"/user/{1}",Result.class,id);
                Map data = (Map) result.getData();
                // 使用hutool 工具类进行转化
                User user = BeanUtil.mapToBean(data, User.class, true);
                return user;
            }
        };
    }

    @HystrixCommand
    public List<User> getUserByIds(List<Long> ids) {
        log.info("getUserByIds:{}", ids);
        Result commonResult = restTemplate.getForObject(userService + "/user/getUserByIds?ids={1}", Result.class, CollUtil.join(ids,","));
        return (List<User>) commonResult.getData();
    }
}

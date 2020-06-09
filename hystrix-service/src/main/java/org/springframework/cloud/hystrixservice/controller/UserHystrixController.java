package org.springframework.cloud.hystrixservice.controller;

import cn.hutool.core.thread.ThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.hystrixservice.entity.Result;
import org.springframework.cloud.hystrixservice.entity.User;
import org.springframework.cloud.hystrixservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Future;

/**
 * @program: demo
 * @description:
 * @author: HyJan
 * @create: 2020-06-08 18:32
 **/
@RestController
@RequestMapping("/user")
public class UserHystrixController {

    @Autowired
    private UserService userService;

    @GetMapping("/testFallback/{id}")
    public Result testFallback(@PathVariable Long id){
        return userService.getUser(id);
    }

    @GetMapping("/testCommand/{id}")
    public Result testCommand(@PathVariable Long id){
        return userService.getUserCommand(id);
    }

    @GetMapping("/testException/{id}")
    public Result testException(@PathVariable Long id){
        return userService.getUserException(id);
    }

    @GetMapping("/testCache/{id}")
    public Result testCache(@PathVariable Long id){
        userService.getUserCache(id);
        userService.getUserCache(id);
        userService.getUserCache(id);
        return Result.success();
    }

    @GetMapping("/testRemoveCache/{id}")
    public Result testRemoveCache(@PathVariable Long id){
        userService.getUserCache(id);
        userService.removeCache(id);
        userService.getUserCache(id);
        return Result.success();
    }

    @GetMapping("/testCollapser")
    public Result testCollapser()throws Exception{
        Future<User> userFuture = userService.getUserFuture(1L);
        Future<User> future = userService.getUserFuture(2L);
        userFuture.get();
        future.get();
        // 线程安全休眠
        ThreadUtil.safeSleep(200);
        Future<User> future1 = userService.getUserFuture(3L);
        future1.get();
        return Result.success();
    }
}

package org.springframework.cloud.hystrixservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @program: demo
 * @description: 用户实体测试类
 * @author: HyJan
 * @create: 2020-05-22 17:29
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    private Long id;
    private String userName;
    private String password;
}

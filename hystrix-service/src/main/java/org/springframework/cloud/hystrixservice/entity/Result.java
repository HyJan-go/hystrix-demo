package org.springframework.cloud.hystrixservice.entity;

import lombok.Data;

/**
 * @program: demo
 * @description: 返回结果类
 * @author: HyJan
 * @create: 2020-05-22 17:31
 */
@Data
public class Result {

  private Integer code;
  private String msg;
  private Object data;

  private static final Integer SUCCESS_CODE = 200;
  private static final Integer ERROR_CODE = 500;
  private static final String SUCCESS_MSG = "操作成功";
  private static final String ERROR_MSG = "操作失败";

  public Result(Integer code, String msg, Object data) {
    this.code = code;
    this.data = data;
    this.msg = msg;
  }

  public static Result success() {
    return new Result(SUCCESS_CODE, SUCCESS_MSG, null);
  }

  public static Result error() {
    return new Result(ERROR_CODE, ERROR_MSG, null);
  }

  public static Result error(Integer code, String msg) {
    return new Result(code, msg, null);
  }

  public static Result success(Object data) {
    return new Result(SUCCESS_CODE, SUCCESS_MSG, data);
  }
}

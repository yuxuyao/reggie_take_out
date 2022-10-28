package com.yuxuyao.utils;

import java.util.Currency;

/**自定义业务异常
 * @author yuxuyao
 * @date 2022/10/14 - 20:05
 */
public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);
    }
}

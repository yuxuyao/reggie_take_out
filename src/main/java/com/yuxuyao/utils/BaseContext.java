package com.yuxuyao.utils;



/**基于ThreadLocal封装工具类，用户保存和获取当前登录用户id
 * @author yuxuyao
 * @date 2022/10/14 - 16:06
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 将id设置到当前线程空间
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 将id从当前线程空间取出
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}

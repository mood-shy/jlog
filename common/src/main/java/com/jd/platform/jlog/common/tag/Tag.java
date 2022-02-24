package com.jd.platform.jlog.common.tag;

import java.lang.annotation.*;

/**
 * @author tangbohu
 * @version 1.0.0
 * @ClassName Tag.java
 * @Description TODO 后续完善使用
 * @createTime 2022年02月24日 20:21:00
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Tag {

    boolean extractReq();

    boolean compressReq();

    boolean extractResp();

    boolean compressResp();

}
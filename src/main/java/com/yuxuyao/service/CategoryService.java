package com.yuxuyao.service;

import com.yuxuyao.domain.Category;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface CategoryService extends IService<Category> {
    void remove(Long id);
}

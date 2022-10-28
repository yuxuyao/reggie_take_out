package com.yuxuyao.service.impl;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuxuyao.domain.Category;
import com.yuxuyao.domain.Dish;
import com.yuxuyao.domain.DishFlavor;
import com.yuxuyao.dto.DishDto;
import com.yuxuyao.service.CategoryService;
import com.yuxuyao.service.DishFlavorService;
import com.yuxuyao.service.DishService;
import com.yuxuyao.mapper.DishMapper;
import com.yuxuyao.utils.CustomException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.events.Event;

import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
    implements DishService{
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;
    /**
     * 新增菜品，同时保存对应的口味数据
     * 因为涉及多张表的配置，需要加上@Transactional
     * @param dishDto
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);
        //获取菜品id
        Long dishId = dishDto.getId();
        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
//        for (DishFlavor flavor : flavors) {
//            flavor.setDishId(dishId);
//        }
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应口味信息
     * @param id
     * @return
     */
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息，从dish表查询
        Dish dish = getById(id);
        //对象拷贝
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 更新菜品信息，同时更新对应口味信息
     * 因为涉及多张表的配置，需要加上@Transactional
     * @param dishDto
     */
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        updateById(dishDto);

        //清理当前菜品对应口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);
        //添加当前提交过来的口味数据---dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((dishFlavor) -> {
            dishFlavor.setDishId(dishDto.getId());
            return dishFlavor;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 批量修改起售状态
     * @param status
     * @param ids
     */
    @Override
    public void saleStatus(Integer status, List<Long> ids) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //批量查询
        queryWrapper.in(ids!=null,Dish::getId,ids);
        List<Dish> dishes = dishService.list(queryWrapper);
        //批量修改
        dishes.stream().map((dish)->{
            if (dish!=null){
                dish.setStatus(status);
                dishService.updateById(dish);
            }
            return dish;
        }).collect(Collectors.toList());
    }

    /**
     * 批量删除
     * @param ids
     */
    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //批量查询
        queryWrapper.in(ids!=null,Dish::getId,ids);
        queryWrapper.eq(ids!=null,Dish::getStatus,1);
        long count = this.count(queryWrapper);
        if (count>0){
            throw new CustomException("删除菜品中有正在售卖的菜品，无法全部删除");
        }
        this.removeByIds(ids);

        //删除菜品对应的口味  也是逻辑删除
        LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapper1);
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    public Page<DishDto> pageFind(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //过滤条件
        queryWrapper.like(name!=null,Dish::getName,name);
        //排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //分页查询
        dishService.page(pageInfo,queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        //将Dish对象数组转为dishDto对象数组
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((dish) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);
            //根据id查询分类对象
            Category category = categoryService.getById(dish.getCategoryId());
            if (category!= null){
                String categoryName = category.getName();
                //设置dishDto对象的CategoryName属性
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        //设置dishDtoPage的records属性
        dishDtoPage.setRecords(list);
        return dishDtoPage;
    }
}





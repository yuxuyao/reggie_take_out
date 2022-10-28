package com.yuxuyao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuxuyao.domain.Category;
import com.yuxuyao.domain.Setmeal;
import com.yuxuyao.domain.SetmealDish;
import com.yuxuyao.dto.SetmealDto;
import com.yuxuyao.service.CategoryService;
import com.yuxuyao.service.SetmealDishService;
import com.yuxuyao.service.SetmealService;
import com.yuxuyao.mapper.SetmealMapper;
import com.yuxuyao.utils.CustomException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
        implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     *
     * @param setmealDto
     */
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal,执行insert操作
        this.save(setmealDto);
        //为setmealDish里的setmealId属性赋值
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((setmealDish) -> {
            setmealDish.setSetmealId(setmealDto.getId());
            return setmealDish;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     */
    public Page<SetmealDto> pageFind(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //过滤条件
        queryWrapper.eq(name != null, Setmeal::getName, name);
        //排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //分页查询
        setmealService.page(pageInfo, queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");
        //将Setmeal对象转为SetmealDto对线
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((setmeal) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);
            //根据id查询分类对象
            Category category = categoryService.getById(setmeal.getCategoryId());
            if (category != null) {
                //设置SetmealDto对象的CategoryName属性
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());
        return setmealDtoPage.setRecords(list);
    }

    /**
     * 批量删除
     *
     * @param ids
     */
    @Transactional
    public void removeWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null, Setmeal::getId, ids);
        //查询是否套餐是否正在售卖，在售则无法删除
        queryWrapper.eq(ids != null, Setmeal::getStatus, 1);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        //可以删除则先删除套餐表中的数据--setmeal
        this.removeByIds(ids);
        //删除关系表中的数据--setmeal_dish
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(lambdaQueryWrapper);


    }

    /**
     * 更新菜品信息，同时更新对应口味信息
     * @param setmealDto
     */
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        //更新setmeal表的基本信息
        this.updateById(setmealDto);
        //清理当前套餐对应菜品数据---setmeal_dish表的delete操作
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        //添加当前提交过来的菜品数据---setmeal_dish表的insert操作
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 根据id查询菜品信息和对应口味信息
     * @param id
     * @return
     */
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal setmeal = setmealService.getById(id);
        //对象拷贝
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(setmealDishes);

        return setmealDto;
    }

    /**
     * 批量修改起售状态
     * @param status
     * @param ids
     */
    public void saleStatus(Integer status, List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        List<Setmeal> setmeals = setmealService.list(queryWrapper);
        //批量修改
        setmeals.stream().map((item)->{
            if (item != null){
                item.setStatus(status);
                setmealService.updateById(item);
            }
            return item;
        }).collect(Collectors.toList());
    }



}





package com.yuxuyao.dto;

import com.yuxuyao.domain.Setmeal;
import com.yuxuyao.domain.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}

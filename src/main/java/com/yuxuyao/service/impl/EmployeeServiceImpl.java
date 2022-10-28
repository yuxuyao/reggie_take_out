package com.yuxuyao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuxuyao.domain.Employee;
import com.yuxuyao.service.EmployeeService;
import com.yuxuyao.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee>
    implements EmployeeService{

}





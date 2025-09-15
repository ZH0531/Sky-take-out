package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordEditFailedException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO 登录数据
     * @return 员工对象
     */
    @Override
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);
        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        //密码比对
        //进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
        if (Objects.equals(employee.getStatus(), StatusConstant.DISABLE)) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }
        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     *
     * @param employeeDTO 新增员工数据
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        // 创建对象
        Employee employee = new Employee();

        // 属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);

        // 设置默认密码123456，进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        // 设置账号状态，创建时间等
        employee.setStatus(StatusConstant.ENABLE);
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        // 添加当前登录用户ID
//        employee.setCreateUser(BaseContext.getCurrentId());
//        employee.setUpdateUser(BaseContext.getCurrentId());
//        BaseContext.removeCurrentId();

        // 插入数据
        employeeMapper.insert(employee);

    }

    /**
     * 分页查询
     *
     * @param employeePageQueryDTO 分页查询参数
     * @return 分页结果
     */
    @Override
    public PageResult page(EmployeePageQueryDTO employeePageQueryDTO) {
        // 开始分页
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        // 查询数据
        Page<Employee> page = employeeMapper.page(employeePageQueryDTO);
        // 封装结果并返回
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 设置员工账号状态
     *
     * @param status 状态
     * @param id     员工ID
     */
    @Override
    public void setStatus(Long id, Integer status) {
        // 构建需要更新的字段
        Employee employee = Employee.builder()
                .id(id)
                .status(status)
//                .updateTime(LocalDateTime.now())
//                .updateUser(BaseContext.getCurrentId())
                .build();
        // 更新员工数据
        employeeMapper.update(employee);
    }


    /**
     * 根据ID查询员工信息
     *
     * @param id 员工ID
     * @return 员工信息
     */
    @Override
    public Employee getEmployeeById(Long id) {
        Employee employee = employeeMapper.getEmployeeById(id);
        employee.setPassword("****");
        return employee;
    }

    /**
     * 修改员工密码
     *
     * @param passwordEditDTO 修改密码信息
     */
    @Override
    public void editPassword(PasswordEditDTO passwordEditDTO) {
        // 对传入密码进行MD5加密
        String newPassword = DigestUtils.md5DigestAsHex(passwordEditDTO.getNewPassword().getBytes());
        String oldPassword = DigestUtils.md5DigestAsHex(passwordEditDTO.getOldPassword().getBytes());
        // 从线程局部变量获取当前登录用户ID
        Long empId = BaseContext.getCurrentId();
        // 获取数据库中的密码进行比对
        String password = employeeMapper.getEmployeeById(empId).getPassword();
        // 密码一致/重复/错误
        if (password.equals(oldPassword) && !password.equals(newPassword)) {
            Employee employee = Employee.builder()
                    .id(passwordEditDTO.getEmpId())
                    .password(newPassword)
//                    .updateTime(LocalDateTime.now())
//                    .updateUser(BaseContext.getCurrentId())
                    .build();
            // 修改密码
            employeeMapper.update(employee);
        } else if (!password.equals(oldPassword)) {
            // 密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        } else {
            // 新旧密码一致
            throw new PasswordEditFailedException(MessageConstant.PASSWORD_NOT_CHANGE);
        }
    }


    /**
     * 编辑员工信息
     *
     * @param employeeDTO 员工信息
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = Employee.builder()
                .id(employeeDTO.getId())
                .username(employeeDTO.getUsername())
                .name(employeeDTO.getName())
                .phone(employeeDTO.getPhone())
                .sex(employeeDTO.getSex())
                .idNumber(employeeDTO.getIdNumber())
//                .updateTime(LocalDateTime.now())
//                .updateUser(BaseContext.getCurrentId())
                .build();
        employeeMapper.update(employee);
    }
}

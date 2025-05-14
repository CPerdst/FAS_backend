package com.l1Akr.pojo.dao.mapper;

import com.github.pagehelper.Page;
import com.l1Akr.pojo.po.RolePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface RoleMapper {
    RolePO findById(@Param("id") Integer id);
    List<RolePO> findAll();
    int insert(RolePO rolePO);
    int update(RolePO rolePO);
    int delete(@Param("id") Integer id);
    List<RolePO> findByUserId(@Param("userId") Integer userId);
    
    Page<RolePO> findAllRoles();
    
    RolePO findByRolePO(RolePO rolePO);

    int deleteById(Integer id);
}
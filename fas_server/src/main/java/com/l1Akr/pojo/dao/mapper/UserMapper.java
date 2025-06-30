package com.l1Akr.pojo.dao.mapper;

import com.github.pagehelper.Page;
import com.l1Akr.pojo.dto.UserLineHistoryDTO;
import com.l1Akr.pojo.po.UserBasePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface UserMapper {

    void insertByUserBasePo(UserBasePO userBasePO);

    UserBasePO findByUserBasePo(UserBasePO userBasePO);

    int updateByUserBasePo(UserBasePO userBasePO);

    int updateByUserBasePoForAdmin(UserBasePO userBasePO);
    
    /**
     * 获取用户总数
     * @return 用户总数
     */
    int getUserCount();
    
    /**
     * 分页查询所有用户
     * @return 用户分页列表
     */
    Page<UserBasePO> findAllUsers();

    /**
     * 根据用户ID删除用户
     * @param id 用户ID
     * @return 受影响的行数
     */
    int deleteByUserId(Integer id);
    
    /**
     * 查询用户近期创建和更新历史数据
     * @param days 天数
     * @return 用户历史数据列表
     */
    List<UserLineHistoryDTO> selectLineUserHistory(@Param("days") int days);
}

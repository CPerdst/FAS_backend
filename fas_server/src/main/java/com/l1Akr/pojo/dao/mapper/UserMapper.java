package com.l1Akr.pojo.dao.mapper;

import com.l1Akr.pojo.po.UserBasePO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface UserMapper {

    void insertByUserBasePo(UserBasePO userBasePO);

    UserBasePO findByUserBasePo(UserBasePO userBasePO);

    int updateByUserBasePo(UserBasePO userBasePO);
    
    /**
     * 获取用户总数
     * @return 用户总数
     */
    int getUserCount();

}

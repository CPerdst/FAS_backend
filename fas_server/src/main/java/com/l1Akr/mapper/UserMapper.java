package com.l1Akr.mapper;

import com.l1Akr.po.UserBasePO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface UserMapper {

    void insertByUserBasePo(UserBasePO userBasePO);

    UserBasePO findByUserBasePo(UserBasePO userBasePO);

    int updateByUserBasePo(UserBasePO userBasePO);

}

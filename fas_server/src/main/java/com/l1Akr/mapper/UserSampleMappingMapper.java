package com.l1Akr.mapper;

import com.l1Akr.pojo.po.UserSampleMappingPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface UserSampleMappingMapper {

    void insertByUserSampleMappingPo(UserSampleMappingPO userSampleMappingPO);

    UserSampleMappingPO getUserSampleMappingByUserIdAndSampleId(Integer userId, Integer sampleId, Integer limit);

    Integer getUserIdBySampleId(Integer sampleId);

}

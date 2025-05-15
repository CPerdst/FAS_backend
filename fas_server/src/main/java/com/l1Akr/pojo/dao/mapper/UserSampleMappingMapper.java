package com.l1Akr.pojo.dao.mapper;

import com.l1Akr.pojo.po.UserSampleMappingPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface UserSampleMappingMapper {

    void insertByUserSampleMappingPo(UserSampleMappingPO userSampleMappingPO);

    UserSampleMappingPO getUserSampleMappingByUserIdAndSampleId(Integer userId, Integer sampleId, Integer limit);

    Integer getUserIdBySampleId(Integer sampleId);
    
    /**
     * 通过样本ID删除用户样本映射关系
     * @param sampleId 样本ID
     * @return 影响的行数
     */
    int deleteBySampleId(@Param("sampleId") Integer sampleId);

}

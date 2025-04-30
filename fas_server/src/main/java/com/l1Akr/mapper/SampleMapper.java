package com.l1Akr.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.l1Akr.pojo.po.SampleBasePO;

@Mapper
public interface SampleMapper {

    /**
     * 根据用户id获取样本列表
     * @param userId
     * @return
     */
    @Select("SELECT s.* FROM sample_base s " +
            "JOIN user_sample_mapping m ON s.id = m.sample_id " +
            "WHERE m.user_id = #{userId} " +
            "ORDER BY s.create_time DESC")
    List<SampleBasePO> selectSamplesByUserId(int userId);

}

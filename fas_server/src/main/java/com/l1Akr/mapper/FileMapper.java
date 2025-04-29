package com.l1Akr.mapper;

import com.l1Akr.po.SampleBasePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface FileMapper {

    void insertBySampleBasePo(SampleBasePO sampleBasePO);

    SampleBasePO getSampleByMd5(String md5);

    SampleBasePO getSampleById(int id);

    int updateSampleBySampleBasePo(SampleBasePO sampleBasePO);

    @Select("SELECT s.* FROM sample_base s " +
            "JOIN user_sample_mapping m ON s.id = m.sample_id " +
            "WHERE m.user_id = #{userId} " +
            "ORDER BY s.create_time DESC")
    List<SampleBasePO> selectSamplesByUserId(int userId);

}

package com.l1Akr.mapper;

import com.l1Akr.po.SampleBasePO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface FileMapper {

    void insertBySampleBasePo(SampleBasePO sampleBasePO);

    SampleBasePO getSampleByMd5(String md5);

    SampleBasePO getSampleById(int id);

    int updateSampleBySampleBasePo(SampleBasePO sampleBasePO);

}

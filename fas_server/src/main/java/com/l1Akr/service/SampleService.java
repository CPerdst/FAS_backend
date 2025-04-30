package com.l1Akr.service;

import org.springframework.stereotype.Component;

import com.github.pagehelper.PageInfo;
import com.l1Akr.pojo.dto.SampleBaseLightDTO;

@Component
public interface SampleService {

    /**
     * 根据用户id获取样本列表
     * @param userId
     * @return
     */
    PageInfo<SampleBaseLightDTO> getSampleListByUserId(int userId, int pageNum, int pageSize);

}

package com.l1Akr.service;

import com.github.pagehelper.Page;
import com.l1Akr.pojo.dto.SampleHistoryDTO;
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

    /**
     * 根据用户id查询用户历史样本记录
     */
    SampleHistoryDTO getSampleHistoryByUserId(int userId);

}

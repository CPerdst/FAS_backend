package com.l1Akr.service;

import com.l1Akr.pojo.dto.SampleHistoryDTO;
import com.l1Akr.pojo.dto.SampleLineHistoryDTO;
import org.springframework.stereotype.Component;

import com.github.pagehelper.PageInfo;
import com.l1Akr.pojo.dto.SampleBaseLightDTO;

import java.util.List;

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
    SampleHistoryDTO getAllSampleHistoryByUserId(int userId);

    /**
     * 查询用户近期样本提交历史数据
     */
    List<SampleLineHistoryDTO> getLineSampleHistoryByUserId(int userId, int days);
    
    /**
     * 获取样本总数
     * @return 样本总数
     */
    int getSampleTotalCount();
    
    /**
     * 获取报告总数
     * @return 报告总数
     */
    int getReportTotalCount();

}

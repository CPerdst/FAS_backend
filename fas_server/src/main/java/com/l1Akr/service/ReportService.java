package com.l1Akr.service;

import org.springframework.stereotype.Component;

import com.github.pagehelper.PageInfo;
import com.l1Akr.dto.SampleReportDTO;

@Component
public interface ReportService {

    /**
     * 根据用户id获取样本报告列表
     * @param userId
     * @return
     */
    PageInfo<SampleReportDTO> getReportListByUserId(int userId, int pageNum, int pageSize);

}

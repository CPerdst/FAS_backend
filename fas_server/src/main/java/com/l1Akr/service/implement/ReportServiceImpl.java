package com.l1Akr.service.implement;

import java.util.List;

import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.l1Akr.dto.SampleBaseLightDTO;
import com.l1Akr.dto.SampleReportDTO;
import com.l1Akr.mapper.SampleMapper;
import com.l1Akr.service.ReportService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final SampleMapper sampleMapper;

    public ReportServiceImpl(SampleMapper sampleMapper) {
        this.sampleMapper = sampleMapper;
    }

    /**
     * 根据用户id获取样本报告列表
     * @param userId
     * @return
     */
    @Override
    public PageInfo<SampleReportDTO> getReportListByUserId(int userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<SampleReportDTO> sampleBaseLightList = sampleMapper.selectSamplesByUserId(userId).stream().map(sampleBasePO -> {
                    SampleReportDTO sampleReportDTO = new SampleReportDTO();
                    sampleReportDTO.setId(sampleBasePO.getId());
                    sampleReportDTO.setFileMd5(sampleBasePO.getFileMd5());
                    sampleReportDTO.setPdfPath(sampleBasePO.getPdfPath());
                    sampleReportDTO.setPdfSize(sampleBasePO.getPdfSize());
                    sampleReportDTO.setPdfCreateTime(sampleBasePO.getPdfCreateTime());
                    return sampleReportDTO;
                }).toList();
        return new PageInfo<>(sampleBaseLightList);
    }

}

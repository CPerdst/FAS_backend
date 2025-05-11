package com.l1Akr.service.implement;

import java.util.List;

import com.github.pagehelper.Page;
import com.l1Akr.pojo.dao.mapper.ReportMapper;
import com.l1Akr.pojo.po.SampleBasePO;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.l1Akr.pojo.dto.SampleReportDTO;
import com.l1Akr.service.ReportService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final ReportMapper reportMapper;

    public ReportServiceImpl(ReportMapper reportMapper) {
        this.reportMapper = reportMapper;
    }

    /**
     * 根据用户id获取样本报告列表
     * @param userId
     * @return
     */
    @Override
    public PageInfo<SampleReportDTO> getReportListByUserId(int userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        Page<SampleBasePO> sampleBasePOPage = reportMapper.getSampleReport(userId);

        List<SampleReportDTO> sampleReportDTOList = sampleBasePOPage.stream().map(this::convertToSampleReportDTO)
                .toList();

        PageInfo<SampleReportDTO> pageInfo = new PageInfo<>(sampleReportDTOList);
        pageInfo.setTotal(sampleBasePOPage.getTotal());
        pageInfo.setPages(sampleBasePOPage.getPages());
        pageInfo.setPageNum(sampleBasePOPage.getPageNum());
        pageInfo.setPageSize(sampleBasePOPage.getPageSize());

        return pageInfo;
    }

    private SampleReportDTO convertToSampleReportDTO(SampleBasePO sampleBasePO) {
        SampleReportDTO sampleReportDTO = new SampleReportDTO();
        sampleReportDTO.setId(sampleBasePO.getId());
        sampleReportDTO.setFileMd5(sampleBasePO.getFileMd5());
        sampleReportDTO.setPdfPath(sampleBasePO.getPdfPath());
        sampleReportDTO.setPdfSize(sampleBasePO.getPdfSize());
        sampleReportDTO.setPdfCreateTime(sampleBasePO.getPdfCreateTime());
        return sampleReportDTO;
    }

}

package com.l1Akr.service.implement;

import java.util.List;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.l1Akr.common.excption.BusinessException;
import com.l1Akr.common.result.Result;
import com.l1Akr.common.util.OssUtils;
import com.l1Akr.pojo.dao.mapper.SampleMapper;
import com.l1Akr.pojo.dao.mapper.UserSampleMappingMapper;
import com.l1Akr.pojo.dto.SampleBaseLightDTO;
import com.l1Akr.pojo.dto.SampleHistoryDTO;
import com.l1Akr.pojo.dto.SampleLineHistoryDTO;
import com.l1Akr.pojo.dto.SampleUserDTO;
import com.l1Akr.pojo.po.SampleBasePO;
import com.l1Akr.pojo.po.UserSampleMappingPO;
import com.l1Akr.service.SampleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
public class SampleServiceImpl implements SampleService {

    private final SampleMapper sampleMapper;
    private final UserSampleMappingMapper userSampleMappingMapper;
    private final OssUtils ossUtils;

    SampleServiceImpl(SampleMapper sampleMapper, UserSampleMappingMapper userSampleMappingMapper, OssUtils ossUtils) {
        this.sampleMapper = sampleMapper;
        this.userSampleMappingMapper = userSampleMappingMapper;
        this.ossUtils = ossUtils;
    }

    /**
     * 根据用户id获取样本列表
     * @param userId
     * @return
     */
    @Override
    public PageInfo<SampleBaseLightDTO> getSampleListByUserId(int userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        try {
            Page<SampleBasePO> sampleBasePOPage = sampleMapper.selectSamplesByUserId(userId);
            List<SampleBaseLightDTO> sampleBaseLightDTOList = sampleBasePOPage.stream().map(this::convertToDTO)
                    .toList();
            PageInfo<SampleBaseLightDTO> sampleBaseLightDTOPageInfo = new PageInfo<>(sampleBaseLightDTOList);
            sampleBaseLightDTOPageInfo.setTotal(sampleBasePOPage.getTotal());
            sampleBaseLightDTOPageInfo.setPageNum(sampleBasePOPage.getPageNum());
            sampleBaseLightDTOPageInfo.setPageSize(sampleBasePOPage.getPageSize());
            sampleBaseLightDTOPageInfo.setPages(sampleBasePOPage.getPages());
            return sampleBaseLightDTOPageInfo;
        } finally {
            PageHelper.clearPage();
        }
    }

    /**
     * 根据用户id查询用户历史样本记录
     */
    @Override
    public SampleHistoryDTO getAllSampleHistoryByUserId(int userId) {
        return sampleMapper.selectAllSampleHistoryByUserId(userId);
    }

    /**
     * 查询用户近期样本提交历史数据
     */
    @Override
    public List<SampleLineHistoryDTO> getLineSampleHistoryByUserId(int userId, int days) {
        return sampleMapper.selectLineSampleHistoryByUserId(userId, days - 1);
    }

    @Override
    public int getSampleTotalCount() {
        return sampleMapper.selectSampleTotalCount();
    }

    @Override
    public int getReportTotalCount() {
        return sampleMapper.selectReportTotalCount();
    }
    
    @Override
    public PageInfo<SampleUserDTO> getAllSamples(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        try {
            Page<SampleUserDTO> samplePage = sampleMapper.selectAllSamplesWithUserInfo();
            return new PageInfo<>(samplePage);
        } finally {
            PageHelper.clearPage();
        }
    }
    
    @Override
    public void deleteSample(Integer id) {
        // 获取样本信息
        SampleBasePO sample = sampleMapper.selectSampleById(id);
        if (sample == null) {
            throw new BusinessException(Result.ResultEnum.PARAM_ERROR);
        }
        
        // 1. 删除OSS中的样本文件
        if (StringUtils.isNotBlank(sample.getFilePath())) {
            String ossPath = sample.getFilePath().split("/", 4)[3]; // 提取相对路径
            try {
                ossUtils.deleteSample(ossPath);
                log.info("Sample file deleted from OSS: {}", sample.getFilePath());
            } catch (Exception e) {
                log.error("Failed to delete sample file from OSS: {}", e.getMessage());
            }
        }
        
        // 2. 删除OSS中的PDF报告文件(如果有)
        if (StringUtils.isNotBlank(sample.getPdfPath())) {
            String pdfOssPath = sample.getPdfPath().split("/", 4)[3]; // 提取相对路径
            try {
                ossUtils.deleteSample(pdfOssPath);
                log.info("PDF report deleted from OSS: {}", sample.getPdfPath());
            } catch (Exception e) {
                log.error("Failed to delete PDF report from OSS: {}", e.getMessage());
            }
        }
        
        // 3. 删除用户样本映射关系
        userSampleMappingMapper.deleteBySampleId(id);
        
        // 4. 删除样本记录
        if (sampleMapper.deleteSampleById(id) <= 0) {
            throw new BusinessException(Result.ResultEnum.PARAM_ERROR);
        }
        
        log.info("Sample deleted successfully: {}", id);
    }

    @Override
    public SampleUserDTO getSampleDetail(Integer id) {
        if (id == null || id <= 0) {
            throw new BusinessException(Result.ResultEnum.PARAM_ERROR);
        }
        
        // 查询样本详情，包括上传用户信息
        List<SampleUserDTO> samples = sampleMapper.selectSampleDetailById(id);
        if (samples == null || samples.isEmpty()) {
            throw new BusinessException(Result.ResultEnum.PARAM_ERROR);
        }
        
        return samples.get(0);
    }

    private SampleBaseLightDTO convertToDTO(SampleBasePO sampleBasePO) {
        SampleBaseLightDTO sampleBaseLightDTO = new SampleBaseLightDTO();
        sampleBaseLightDTO.setId(sampleBasePO.getId());
        sampleBaseLightDTO.setFilename(sampleBasePO.getFilename());
        sampleBaseLightDTO.setFileSize(sampleBasePO.getFileSize());
        sampleBaseLightDTO.setFileMd5(sampleBasePO.getFileMd5());
        sampleBaseLightDTO.setCreateTime(sampleBasePO.getCreateTime());
        sampleBaseLightDTO.setDisposeStatus(sampleBasePO.getDisposeStatus());
        return sampleBaseLightDTO;
    }
}

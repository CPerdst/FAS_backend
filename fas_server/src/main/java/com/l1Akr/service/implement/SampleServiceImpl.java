package com.l1Akr.service.implement;

import java.util.List;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.l1Akr.pojo.dao.mapper.SampleMapper;
import com.l1Akr.pojo.dto.SampleBaseLightDTO;
import com.l1Akr.pojo.dto.SampleHistoryDTO;
import com.l1Akr.pojo.dto.SampleLineHistoryDTO;
import com.l1Akr.pojo.po.SampleBasePO;
import com.l1Akr.service.SampleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SampleServiceImpl implements SampleService {

    private final SampleMapper sampleMapper;

    SampleServiceImpl(SampleMapper sampleMapper) {
        this.sampleMapper = sampleMapper;
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

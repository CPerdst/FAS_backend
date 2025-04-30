package com.l1Akr.service.implement;

import java.util.List;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.l1Akr.pojo.dto.SampleBaseLightDTO;
import com.l1Akr.mapper.FileMapper;
import com.l1Akr.service.SampleService;

public class SampleServiceImpl implements SampleService {

    private final FileMapper fileMapper;

    SampleServiceImpl(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    /**
     * 根据用户id获取样本列表
     * @param userId
     * @return
     */
    @Override
    public PageInfo<SampleBaseLightDTO> getSampleListByUserId(int userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<SampleBaseLightDTO> sampleBaseLightList = fileMapper.selectSamplesByUserId(userId).stream().map(sampleBasePO -> {
                    SampleBaseLightDTO sampleBaseLightDTO = new SampleBaseLightDTO();
                    sampleBaseLightDTO.setId(sampleBasePO.getId());
                    sampleBaseLightDTO.setFilename(sampleBasePO.getFilename());
                    sampleBaseLightDTO.setFileSize(sampleBasePO.getFileSize());
                    sampleBaseLightDTO.setFileMd5(sampleBasePO.getFileMd5());
                    sampleBaseLightDTO.setCreateTime(sampleBasePO.getCreateTime());
                    sampleBaseLightDTO.setDisposeStatus(sampleBasePO.getDisposeStatus());
                    return sampleBaseLightDTO;
                }).toList();
        return new PageInfo<>(sampleBaseLightList);
    }

}

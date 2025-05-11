package com.l1Akr.pojo.dao.mapper;

import com.github.pagehelper.Page;
import com.l1Akr.pojo.dto.SampleReportDTO;
import com.l1Akr.pojo.po.SampleBasePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface ReportMapper {

    /**
     * 根据用户id获取样本列表
     * @param userId
     * @return
     */
    @Select("SELECT s.* FROM sample_base s " +
            "JOIN user_sample_mapping m ON s.id = m.sample_id " +
            "WHERE m.user_id = #{userId} " +
            "ORDER BY s.create_time DESC")
    Page<SampleBasePO> getSampleReport(int userId);

}

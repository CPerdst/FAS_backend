package com.l1Akr.pojo.dao.mapper;

import java.util.List;

import com.github.pagehelper.Page;
import com.l1Akr.pojo.dto.SampleHistoryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.l1Akr.pojo.po.SampleBasePO;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface SampleMapper {

    /**
     * 根据用户id获取样本列表
     * @param userId
     * @return
     */
    @Select("SELECT s.* FROM sample_base s " +
            "JOIN user_sample_mapping m ON s.id = m.sample_id " +
            "WHERE m.user_id = #{userId} " +
            "ORDER BY s.create_time DESC")
    Page<SampleBasePO> selectSamplesByUserId(int userId);

    @Select("SELECT \n" +
            "    COUNT(*) AS total_count,\n" +
            "    SUM(CASE WHEN s.dispose_status = 1 THEN 1 ELSE 0 END) AS undisposed_count,\n" +
            "    SUM(CASE WHEN s.dispose_status = 2 THEN 1 ELSE 0 END) AS disposing_count,\n" +
            "    SUM(CASE WHEN s.dispose_status = 3 THEN 1 ELSE 0 END) AS safe_count,\n" +
            "    SUM(CASE WHEN s.dispose_status = 4 THEN 1 ELSE 0 END) AS virus_count,\n" +
            "    SUM(CASE WHEN s.dispose_status = 5 THEN 1 ELSE 0 END) AS failed_count\n" +
            "FROM sample_base s\n" +
            "JOIN user_sample_mapping m ON s.id = m.sample_id\n" +
            "WHERE m.user_id = #{userId};")
    SampleHistoryDTO selectSampleHistoryByUserId(int userId);

}

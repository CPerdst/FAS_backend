package com.l1Akr.pojo.dao.mapper;

import java.util.List;

import com.github.pagehelper.Page;
import com.l1Akr.pojo.dto.SampleHistoryDTO;
import com.l1Akr.pojo.dto.SampleLineHistoryDTO;
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
    SampleHistoryDTO selectAllSampleHistoryByUserId(int userId);

    @Select("WITH date_series AS (\n" +
            "    SELECT CURDATE() - INTERVAL (a.a + (10 * b.a)) DAY AS date\n" +
            "    FROM (SELECT 0 AS a UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) AS a\n" +
            "    CROSS JOIN (SELECT 0 AS a UNION SELECT 1 UNION SELECT 2) AS b\n" +
            "    WHERE (a.a + (10 * b.a)) <= #{days}  -- 最近30天\n" +
            ")\n" +
            "SELECT \n" +
            "    ds.date AS date,\n" +
            "    IFNULL(COUNT(s.id), 0) AS total\n" +
            "FROM date_series ds\n" +
            "LEFT JOIN sample_base s ON DATE(s.create_time) = ds.date\n" +
            "    AND s.id IN (SELECT sample_id FROM user_sample_mapping WHERE user_id = #{userId})\n" +
            "GROUP BY ds.date\n" +
            "ORDER BY ds.date;")
    List<SampleLineHistoryDTO> selectLineSampleHistoryByUserId(int userId, int days);
    
    /**
     * 获取样本总数
     * @return 样本总数
     */
    int selectSampleTotalCount();
    
    /**
     * 获取报告总数
     * @return 报告总数
     */
    int selectReportTotalCount();

}

package com.l1Akr.pojo.dto;

import com.l1Akr.pojo.po.SampleBasePO;
import com.l1Akr.pojo.po.UserBasePO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskWithUserSampleDTO {

    SampleBasePO sampleBasePO;

    Integer userId;

}

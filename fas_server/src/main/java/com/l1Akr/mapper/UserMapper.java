package com.l1Akr.mapper;

import com.l1Akr.dao.UserDAO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface UserMapper {

    public UserDAO InsertByUser(UserDAO userDAO);

}

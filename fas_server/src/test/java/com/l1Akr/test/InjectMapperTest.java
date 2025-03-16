package com.l1Akr.test;

import com.l1Akr.dao.UserDAO;
import com.l1Akr.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class InjectMapperTest {
    @Autowired
    UserMapper userMapper;

    @Test
    public void testInsert(){
        UserDAO userDAO = new UserDAO();
        userMapper.InsertByUser(userDAO);

    }

}

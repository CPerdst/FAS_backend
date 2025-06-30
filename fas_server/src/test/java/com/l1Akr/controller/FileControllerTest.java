package com.l1Akr.controller;

import com.l1Akr.service.FileService;
import com.l1Akr.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(FileController.class)
public class FileControllerTest {

    @Mock
    private FileService fileService;

    @Mock
    private UserService userService;

    @InjectMocks
    private FileController fileController;

    @BeforeEach
    public void setUp() {
        // Mock any necessary setup
        MockitoAnnotations.openMocks(this);
//        UserBasePO user = new UserBasePO();
//        user.setId(1);
//        UserThreadLocal.setCurrentUser(user);
    }

//    @Test
//    @Disabled
//    public void testAvatarUpload_InvalidFile() throws Exception {
//        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello, World!".getBytes());
//        mockMvc.perform(multipart("/avatar/upload").file(file))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().string("Invalid file type. Only image files are allowed."));
//
//
//    }





}

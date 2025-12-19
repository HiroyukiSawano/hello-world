package org.example.myjweb.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.example.myjweb.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserMapperTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    void shouldInsertAndSelect() {
        User user = new User();
        user.setName("Bob");
        user.setEmail("bob@example.com");

        int rows = userMapper.insert(user);
        assertEquals(1, rows);
        assertNotNull(user.getId());

        User found = userMapper.selectById(user.getId());
        assertNotNull(found);
        assertEquals("Bob", found.getName());
        assertEquals("bob@example.com", found.getEmail());
    }
}

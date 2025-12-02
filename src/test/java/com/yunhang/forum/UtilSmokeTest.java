package com.yunhang.forum;

import com.yunhang.forum.util.IdGenerator;
import com.yunhang.forum.util.SecurityUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UtilSmokeTest {

    @Test
    public void testIdGenerationAndHashing() {
        String userId = IdGenerator.generateUserId();
        String postId = IdGenerator.generatePostId();
        assertNotNull(userId);
        assertNotNull(postId);
        assertTrue(userId.startsWith("usr_"));
        assertTrue(postId.startsWith("pst_"));

        String hash = SecurityUtil.sha256("password123");
        assertNotNull(hash);
        assertEquals(64, hash.length()); // SHA-256 hex length
    }
}

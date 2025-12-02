package com.yunhang.forum;

import com.yunhang.forum.util.IdGenerator;
import com.yunhang.forum.util.SecurityUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class YunhangForumApplicationTests {

    @Test
    void idGeneratorProducesDifferentIdsWithPrefixes() {
        String u1 = IdGenerator.generateUserId();
        String u2 = IdGenerator.generateUserId();
        String p1 = IdGenerator.generatePostId();

        assertNotNull(u1);
        assertNotNull(u2);
        assertNotEquals(u1, u2);
        assertTrue(u1.startsWith("usr_"));
        assertTrue(p1.startsWith("pst_"));
    }

    @Test
    void sha256ComputesKnownDigest() {
        String digest = SecurityUtil.sha256("password");
        assertEquals("5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8", digest);
    }

    @Test
    void sha256RejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> SecurityUtil.sha256(null));
    }
}

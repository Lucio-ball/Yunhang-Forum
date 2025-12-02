package com.yunhang.forum.util;

import java.util.UUID;

/**
 * Utility for generating unique IDs for users and posts.
 * <p>
 * Uses UUIDv4 to create collision-resistant identifiers. Methods return
 * simple string IDs prefixed to indicate their type.
 */
public final class IdGenerator {

    private IdGenerator() {
        // Utility class; prevent instantiation.
    }

    /**
     * Generate a new user ID.
     *
     * @return a string like "usr_<uuid>"
     */
    public static String generateUserId() {
        return "usr_" + UUID.randomUUID().toString();
    }

    /**
     * Generate a new post ID.
     *
     * @return a string like "pst_<uuid>"
     */
    public static String generatePostId() {
        return "pst_" + UUID.randomUUID().toString();
    }
}

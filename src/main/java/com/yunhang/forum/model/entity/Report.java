package com.yunhang.forum.model.entity;

public class Report {
    String reason;
    public String targetId;
    public Report(String targetId, String reason) { this.targetId = targetId; this.reason = reason; }
    @Override
    public String toString() { return "Report -> Target: " + targetId + ", Reason: " + reason; }
    public String getTargetId() { return targetId; }
}

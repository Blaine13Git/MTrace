package com.ggj.mtracefront.result;

import lombok.Data;

@Data
public class TraceResult {
    String timestamp;
    String threadId;
    String traceLinks;
    String timeSpend;
}

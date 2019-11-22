package com.ggj.mtracefront.requestVO;

import lombok.Data;

@Data
public class LinkTrackingVO {
    private String methodName;
    private String threadID;
    private String startTime;
    private String endTime;
    private String parameterName;
}

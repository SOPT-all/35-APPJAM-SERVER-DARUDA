package com.daruda.darudaserver.domain.tool.dto.res;

import com.daruda.darudaserver.domain.tool.entity.License;
import com.daruda.darudaserver.domain.tool.entity.Tool;
import lombok.AccessLevel;
import lombok.Builder;

import java.util.List;


public record ToolDtoGetRes(
        Long toolId,
        String toolName,
        String toolLogo,
        String description,
        License license,
        List<String> keywords
) {
    // 정적 팩토리 메서드
    public static ToolDtoGetRes from(Tool tool, List<String> keywords) {
        return new ToolDtoGetRes(
                tool.getToolId(),
                tool.getToolMainName(),
                tool.getToolLogo(),
                tool.getDescription(),
                tool.getLicense(),
                keywords
        );
    }
}

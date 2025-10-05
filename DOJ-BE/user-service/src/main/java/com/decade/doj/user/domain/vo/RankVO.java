package com.decade.doj.user.domain.vo;

import lombok.Data;

@Data
public class RankVO {
    private Long rank;
    private Long userId;
    private String username;
    private String avatar;
    private Integer score;
    private Integer easySolve;
    private Integer middleSolve;
    private Integer hardSolve;
    private String mostUsedLanguage;
}

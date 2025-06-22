package com.decade.doj.common.domain.vo;

import com.decade.doj.common.domain.po.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class InfoVO extends User {

}

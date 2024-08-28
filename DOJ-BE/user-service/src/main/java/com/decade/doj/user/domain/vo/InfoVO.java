package com.decade.doj.user.domain.vo;

import com.decade.doj.user.domain.po.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class InfoVO extends User {

}

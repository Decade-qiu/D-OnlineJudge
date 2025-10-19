package com.decade.doj.common.client;

import com.decade.doj.common.domain.R;
import com.decade.doj.common.domain.vo.InfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.constraints.NotNull;

@FeignClient("user-service")
public interface UserClient {

    @GetMapping("/user/{id}")
    R<InfoVO> getUser(@PathVariable("id") @NotNull Long id);

}

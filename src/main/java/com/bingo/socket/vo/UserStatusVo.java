package com.bingo.socket.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 * Author: xbb
 * Date: 2023/12/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatusVo {

    private String userId;

    /**
     * 用户状态
     * @see (com.bingo.socket.enums.UserStatusEnum)
     */
    private Integer status;

}

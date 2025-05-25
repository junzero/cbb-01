package cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.restore;

import lombok.Data;

/**
 * @author hsx
 */
@Data
public class RestoreRes {

    public static final Integer FAIL = 0;
    public static final Integer SUCCESS = 1;

    /**
     * 0:失败
     * 1:成功
     */
    private Integer result;

    /**
     * 失败场景原因
     */
    private String errorMes;

    /**
     * 交互详情
     */
    private String executeDetail;

}

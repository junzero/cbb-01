package cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.backup;

import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.commom.FileServer;
import lombok.Data;

/**
 * @author hsx
 */
@Data
public class BackupRes {

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
     * 配置回显结果
     */
    private String configContent;

    /**
     * 交互详情(查看配置场景不返回)
     */
    private String executeDetail;

}

package cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.restore;

import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.commom.FileServer;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.commom.Operation;
import lombok.Data;

/**
 * 备份还原相关厂商适配信息 配置定义
 * @author huangquanhuan hsx
 */
@Data
public class ConfigRestoreDriver {

    /**
     * 文件传输协议
     */
    private FileServer.FileTransType fileTransType;

    /**
     * 配置恢复操作指令
     */
    private Operation configRestore;

    /**
     * 是否需要重启
     */
    private boolean reboot;

    /**
     * 重启操作指令
     */
    private Operation rebootOperation;

    /**
     * 脚本，和上述的规则匹配互斥
     */
    private String script;

}

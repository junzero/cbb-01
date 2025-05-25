package cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.backup;

import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.commom.FileServer;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.commom.Operation;
import lombok.Data;

/**
 * 备份还原相关厂商适配信息 配置定义
 * @author huangquanhuan hsx
 */
@Data
public class ConfigBackupDriver {

    /**
     * 运行配置备份相关信息
     */
    private ConfigBackupDetail runningConfig;

    /**
     * 启动配置备份相关信息
     */
    private ConfigBackupDetail startUpConfig;

    /**
     * 配置备份相关信息
     */
    @Data
    public static class ConfigBackupDetail {

        /**
         * 配置文件上传
         */
        private ConfigBackupOfUpload configBackupOfUpload;

        /**
         * 配置文件查看
         */
        private ConfigBackupOfShow configBackupOfShow;

    }


    /**
     * 备份配置上传
     */
    @Data
    public static class ConfigBackupOfUpload {

        /**
         * 文件传输协议
         */
        private FileServer.FileTransType fileTransType;

        /**
         * 文件名后缀,为了解决备份上送各厂商文件类型不一致问题，比如锐捷的XXX.zip和H3C的XXX.cfg
         */
        private String fileExtension;

        /**
         * 操作
         */
        private Operation operation;

        /**
         * 是否同步上传回显
         */
        private boolean withShow;

        /**
         * 脚本，和上述的规则匹配互斥
         */
        private String script;

    }

    /**
     * 备份配置查看
     */
    @Data
    public static class ConfigBackupOfShow {

        /**
         * 操作
         */
        private Operation operation;

        /**
         * 脚本，和上述的规则匹配互斥
         */
        private String script;

    }

}

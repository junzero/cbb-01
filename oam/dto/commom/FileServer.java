package cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.commom;

import cn.com.ruijie.ion.netbase.ne.mgr.constant.enums.DeviceType;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.common.errors.AppErrorEnum;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author hsx
 */
@Data
public class FileServer {

    /**
     * 文件服务器地址
     */
    private String serverIp;

    /**
     * 文件服务器类型 FTP TFTP SFTP
     */
    private FileServerType fileServerType;

    /**
     * 文件服务用户名
     */
    private String userName;

    /**
     * 文件服务密码
     */
    private String password;


    public void valid() {
        if (StringUtils.isBlank(serverIp) || fileServerType == null || StringUtils.isBlank(userName)) {
            throw AppErrorEnum.VALID_WITH_ILLEGAL_FILE_SERVER.toMsfException();
        }
        if (FileServerType.TFTP.name().equals(fileServerType.name())) {
            return;
        }
        if (StringUtils.isBlank(password)) {
            throw AppErrorEnum.VALID_WITH_ILLEGAL_FILE_SERVER.toMsfException();
        }
    }


    @Getter
    public enum FileServerType {
        /**
         * 10X/异构 等;不安全 平台默认不拉起
         */
        FTP,

        /**
         * 异构/非EDN设备升级 等;不安全 平台默认不拉起
         */
        TFTP,

        /**
         * 安全 平台默认拉起
         */
        SFTP
    }

    @Getter
    public enum FileTransType {
        /**
         * FTP
         */
        FTP,

        /**
         * SFTP
         */
        TFTP,

        /**
         * HTTPS 特殊 取代了SFTP
         */
        HTTPS
    }

}

package cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.backup;

import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.commom.FileServer;
import lombok.Builder;
import lombok.Data;

/**
 * @author hsx
 */
@Data
@Builder
public class BackupRequest {

    /**
     * 设备key
     */
    private String deviceKey;

    /**
     * sceneKey
     */
    private String sceneKey;

    /**
     * 1:运行配置
     * 2:启动配置
     */
    private Integer configType;

    /**
     * 是否为仅查看配置 不执行备份操作
     */
    private boolean onlyShow;

    /**
     * 备份文件路径
     */
    private String uploadPath;

    /**
     * 备份文件上传url
     */
    private String httpUrl;

    /**
     * 可用的文件服务
     */
    private FileServer availableFileServer;

}

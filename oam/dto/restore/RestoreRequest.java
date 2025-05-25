package cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.restore;

import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.commom.FileServer;
import lombok.Builder;
import lombok.Data;

/**
 * @author hsx
 */
@Data
@Builder
public class RestoreRequest {

    /**
     * 设备key
     */
    private String deviceKey;

    /**
     * sceneKey
     */
    private String sceneKey;

    /**
     * 还原文件路径
     */
    private String restoreFilePath;

    /**
     * 下载还原文件url
     */
    private String httpUrl;

    /**
     * 还原文件所在文件服务器
     */
    private FileServer fileServer;

}

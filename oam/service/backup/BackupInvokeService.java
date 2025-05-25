package cn.com.ruijie.ion.netbase.ztp.pnp.oam.service.backup;


import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.backup.BackupRequest;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.backup.BackupRes;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.restore.RestoreRequest;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.restore.RestoreRes;


public interface BackupInvokeService {

    /**
     * 备份配置
     *
     * @param device         设备信息
     * @param driver         备份驱动
     * @param configType     配置类型
     * @param backUpId       备份ID
     * @param configFilePath 配置文件路径
     */
    BackupRes backup(BackupRequest backupRequest);

    /**
     * 还原配置
     *
     * @param device       设备信息
     * @param driver       备份驱动
     * @param logId        日志ID
     * @param backFilePath 备份文件路径
     * @return boolean
     */
    RestoreRes restore(RestoreRequest restoreRequest);

    /**
     * @param deviceKey 设备Key
     * @param sceneKey  sceneKey
     * @return  文件服务器类型
     */
    String getFileServerTypeByDevice(String deviceKey, String sceneKey);

    /**
     * 获取文件后缀
     * @param deviceKey 设备Key
     * @param configType    配置类型
     * @param sceneKey      sceneKey
     * @return  文件后缀
     */
    String getFileExtension(String deviceKey, Integer configType, String sceneKey);

}

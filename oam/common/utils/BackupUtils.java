package cn.com.ruijie.ion.netbase.ztp.pnp.oam.common.utils;

import cn.com.ruijie.ion.netbase.ztp.pnp.constant.PnpConstants;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.common.errors.AppErrorEnum;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.backup.BackupRequest;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.restore.RestoreRequest;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.commom.FileServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * @author hsx
 */
@Slf4j
public class BackupUtils {

    public static final int RUNNING_CONFIG = 1;
    public static final int STARTUP_CONFIG = 2;

    private static final String DATE_FORMAT = "yyyyMMddHHmmss";


    public static void validBackupRequest(BackupRequest backupRequest) {
        if (StringUtils.isBlank(backupRequest.getDeviceKey())) {
            throw AppErrorEnum.VALID_WITH_NULL_DEVICE_KEY.toMsfException();
        }
        Integer configType = backupRequest.getConfigType();
        if (!Objects.equals(configType, RUNNING_CONFIG) && !Objects.equals(configType, STARTUP_CONFIG)) {
            throw AppErrorEnum.VALID_WITH_ILLEGAL_CONFIG_TYPE.toMsfException();
        }
        if (!backupRequest.isOnlyShow()) {
            if (StringUtils.isBlank(backupRequest.getUploadPath())) {
                throw AppErrorEnum.VALID_WITH_NULL_UPLOAD_PATH.toMsfException();
            }
            if (backupRequest.getAvailableFileServer() == null) {
                throw AppErrorEnum.VALID_WITH_EMPTY_FILE_SERVER.toMsfException();
            }
        }
    }

    public static void validRestoreRequest(RestoreRequest restoreRequest) {
        Optional.ofNullable(restoreRequest)
            .map(RestoreRequest::getDeviceKey)
            .orElseThrow(AppErrorEnum.VALID_WITH_NULL_DEVICE_KEY::toMsfException);
        Optional.of(restoreRequest)
            .map(RestoreRequest::getSceneKey)
            .orElseThrow(AppErrorEnum.VALID_WITH_NULL_SCENE_KEY::toMsfException);
        Optional.of(restoreRequest)
            .map(RestoreRequest::getRestoreFilePath)
            .orElseThrow(AppErrorEnum.VALID_WITH_NULL_DOWNLOAD_PATH::toMsfException);
        FileServer fileServer = Optional.of(restoreRequest)
            .map(RestoreRequest::getFileServer)
            .orElseThrow(AppErrorEnum.VALID_WITH_EMPTY_FILE_SERVER::toMsfException);;
        fileServer.valid();
    }



    public static String getFileName(Integer configType, String fileType) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        String timeStr = sdf.format(new Date());
        // 暂时统一使用锐捷设备配置文件名， 集成关注文件名对设备影响
        String fileName = StringUtils.EMPTY;
        if (configType == PnpConstants.ConfigType.BOOT) {
            fileName = String.format("config_%s", timeStr);
        } else if (configType == PnpConstants.ConfigType.RUNNING) {
            fileName = String.format("showrun_%s", timeStr);
        }
        return fileName + fileType;
    }

}

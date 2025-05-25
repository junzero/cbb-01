package cn.com.ruijie.ion.netbase.ztp.pnp.oam.service.backup;

import cn.com.ruijie.ibns.necfg.common.dto.cli.CliConfigExecOutput;
import cn.com.ruijie.ion.msf.common.errors.MsfRuntimeException;
import cn.com.ruijie.ion.msf.ne.mgr.common.vendors.service.DataStore;
import cn.com.ruijie.ion.msf.ne.mgr.plugin.service.dto.DeviceDTO;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.common.errors.AppErrorEnum;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.common.utils.BackupUtils;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.common.utils.CommonUtils;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.backup.*;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.commom.ExecuteDetailHelper;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.commom.FileServer;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.commom.Operation;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.restore.ConfigRestoreDriver;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.restore.RestoreRequest;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.restore.RestoreRes;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.service.CommonService;
import cn.com.ruijie.ion.netbase.ztp.pnp.web.rest.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 备份执行服务实现类
 *
 * @author huangquanhuan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BackupInvokeServiceImpl implements BackupInvokeService {

    private final DataStore dataStore;

    private final CommonService commonService;

    @Override
    public RestoreRes restore(RestoreRequest restoreRequest) {
        RestoreRes restoreRes = new RestoreRes();
        try {
            log.info("device {} begin to restore", restoreRequest.getDeviceKey());
            // 校验
            BackupUtils.validRestoreRequest(restoreRequest);
            // 获取配置
            ConfigRestoreDriver configRestoreDriver = dataStore.getByDeviceKey(restoreRequest.getDeviceKey(), restoreRequest.getSceneKey(), ConfigRestoreDriver.class);
            // 获取最终还原命令
            Operation restoreOperation = Optional.ofNullable(configRestoreDriver)
                .map(ConfigRestoreDriver::getConfigRestore)
                .orElseThrow(AppErrorEnum.VALID_WITH_NULL_VENDOR_ADAPTER::toMsfException);
            List<String> finalRestoreCommandList = CommonUtils.getFinalCommand(restoreOperation.getOperation(), configRestoreDriver.getFileTransType(),
                restoreRequest.getFileServer(), restoreRequest.getRestoreFilePath(), restoreRequest.getHttpUrl());
            // 下发还原操作
            DeviceDTO deviceDTO = commonService.getDevice(restoreRequest.getDeviceKey());
            ExecuteDetailHelper executeDetailHelper = new ExecuteDetailHelper();
            String executeInitMes = CommonUtils.getMesWithFormatArgs(CommonUtils.CONFIG_SEND_INIT_CODE, deviceDTO.getDeviceIp()) + System.lineSeparator();
            executeDetailHelper.setExecuteDetail(executeInitMes);
            executeDetailHelper.setStartTime(System.currentTimeMillis());
            commonService.sendCommand(deviceDTO, restoreOperation, finalRestoreCommandList, restoreRequest.getSceneKey(), executeDetailHelper);
            if (configRestoreDriver.isReboot()) {
                // 需要重启则继续下发重启
                Operation rebootOperation = configRestoreDriver.getRebootOperation();
                commonService.sendCommand(deviceDTO, rebootOperation, Arrays.asList(CommonUtils.LINE.split(rebootOperation.getOperation())), restoreRequest.getSceneKey(), executeDetailHelper);
            }
            // 正常流程结束
            CommonUtils.writeExecuteDetailByOverAllCost(executeDetailHelper, deviceDTO.getDeviceIp());
            restoreRes.setExecuteDetail(executeDetailHelper.getExecuteDetail());
            restoreRes.setResult(BackupRes.SUCCESS);
            log.info("device {} restore success", restoreRequest.getDeviceKey());
        } catch (MsfRuntimeException e) {
            String errorMessage = e.getErrorMessage();
            log.error("restore request {} with runtime exception {}", restoreRequest, errorMessage);
            restoreRes.setResult(BackupRes.FAIL);
            restoreRes.setErrorMes(errorMessage);
        } catch (Exception e) {
            // 预期外异常打印堆栈信息
            log.error("restore request {} with unexpected exception", restoreRequest, e);
            restoreRes.setResult(BackupRes.FAIL);
            restoreRes.setErrorMes(e.getMessage());
        }
        return restoreRes;
    }

    @Override
    public String getFileServerTypeByDevice(String deviceKey, String sceneKey) {
        ConfigBackupDriver configBackupDriver = dataStore.getByDeviceKey(deviceKey, sceneKey, ConfigBackupDriver.class);
        // 获取文件传输类型
        FileServer.FileTransType fileTransType = Optional.ofNullable(configBackupDriver)
            .map(ConfigBackupDriver::getStartUpConfig)
            .map(ConfigBackupDriver.ConfigBackupDetail::getConfigBackupOfUpload)
            .map(ConfigBackupDriver.ConfigBackupOfUpload::getFileTransType)
            .orElseThrow(AppErrorEnum.CHECK_WITH_EMPTY_FILE_TRANS_TYPE::toMsfException);
        return fileTransType.name();
    }

    @Override
    public String getFileExtension(String deviceKey, Integer configType, String sceneKey) {
        ConfigBackupDriver configBackupDriver = dataStore.getByDeviceKey(deviceKey, sceneKey, ConfigBackupDriver.class);
        ConfigBackupDriver.ConfigBackupDetail configBackupDetail = getConfigUpload(configType, configBackupDriver);
        return Optional.ofNullable(configBackupDetail)
            .map(ConfigBackupDriver.ConfigBackupDetail::getConfigBackupOfUpload)
            .map(ConfigBackupDriver.ConfigBackupOfUpload::getFileExtension)
            .orElseThrow(AppErrorEnum.VALID_WITH_ILLEGAL_FILE_EXTENSION::toMsfException);
    }

    /**
     * 获取获取用户的自定义备份配置
     *
     * @param configType
     * @return
     */
    private ConfigBackupDriver.ConfigBackupDetail getConfigUpload(Integer configType, ConfigBackupDriver configBackupDriver) {
        switch (configType) {
            case BackupUtils.RUNNING_CONFIG:
                return Optional.ofNullable(configBackupDriver)
                    .map(ConfigBackupDriver::getRunningConfig)
                    .orElseThrow(AppErrorEnum.VALID_WITH_NULL_VENDOR_ADAPTER::toMsfException);
            case BackupUtils.STARTUP_CONFIG:
                return Optional.ofNullable(configBackupDriver)
                    .map(ConfigBackupDriver::getStartUpConfig)
                    .orElseThrow(AppErrorEnum.VALID_WITH_NULL_VENDOR_ADAPTER::toMsfException);
            default:
                throw AppErrorEnum.VALID_WITH_ILLEGAL_CONFIG_TYPE.toMsfException();
        }
    }

    @Override
    public BackupRes backup(BackupRequest backupRequest) {
        BackupRes backupRes = new BackupRes();
        try {
            log.info("device {} begin to backup", backupRequest.getDeviceKey());
            // 获取配置
            ConfigBackupDriver configBackupDriver = dataStore.getByDeviceKey(backupRequest.getDeviceKey(), backupRequest.getSceneKey(), ConfigBackupDriver.class);
            BackupUtils.validBackupRequest(backupRequest);
            // 获取对应配置类型
            ConfigBackupDriver.ConfigBackupDetail configBackupDetail = getConfigUpload(backupRequest.getConfigType(), configBackupDriver);
            // 获取设备 初始化配置下发信息
            DeviceDTO deviceDTO = commonService.getDevice(backupRequest.getDeviceKey());
            ExecuteDetailHelper executeDetailHelper = new ExecuteDetailHelper();
            // 查看配置 or 备份
            if (backupRequest.isOnlyShow()) {
                // 查看配置场景暂不记录交互详情
                String content = getConfigRealTime(deviceDTO, backupRequest, configBackupDetail, null);
                backupRes.setConfigContent(content);
            } else {
                String executeInitMes = CommonUtils.getMesWithFormatArgs(CommonUtils.CONFIG_SEND_INIT_CODE, deviceDTO.getDeviceIp()) + System.lineSeparator();
                executeDetailHelper.setExecuteDetail(executeInitMes);
                executeDetailHelper.setStartTime(System.currentTimeMillis());
                backupConfig(deviceDTO, backupRequest, configBackupDetail, executeDetailHelper);
                if (configBackupDetail.getConfigBackupOfUpload().isWithShow()) {
                    // 需要回显的场景 -- > 添加校验
                    String content = getConfigRealTime(deviceDTO, backupRequest, configBackupDetail, executeDetailHelper);
                    backupRes.setConfigContent(content);
                }
            }
            // 正常流程结束
            CommonUtils.writeExecuteDetailByOverAllCost(executeDetailHelper, deviceDTO.getDeviceIp());
            backupRes.setExecuteDetail(executeDetailHelper.getExecuteDetail());
            backupRes.setResult(BackupRes.SUCCESS);
            log.info("device {} backup success", backupRequest.getDeviceKey());
        } catch (MsfRuntimeException e) {
            String errorMessage = e.getErrorMessage();
            log.error("backup request {} with runtime exception {}", backupRequest, errorMessage);
            backupRes.setResult(BackupRes.FAIL);
            backupRes.setErrorMes(errorMessage);
        } catch (Exception e) {
            // 预期外异常打印堆栈信息
            log.error("backup request {} with unexpected exception", backupRequest, e);
            backupRes.setResult(BackupRes.FAIL);
            backupRes.setErrorMes(e.getMessage());
        }
        return backupRes;
    }


    private String getConfigRealTime(DeviceDTO deviceDTO, BackupRequest backupRequest, ConfigBackupDriver.ConfigBackupDetail configBackupDetail, ExecuteDetailHelper executeDetailHelper) {
        ConfigBackupDriver.ConfigBackupOfShow configBackupOfShow = configBackupDetail.getConfigBackupOfShow();
        // 直接存在show命令
        if (configBackupOfShow != null) {
            return showConfig(configBackupOfShow, deviceDTO,  backupRequest.getSceneKey(), executeDetailHelper);
        }
        // 不存在show命令 只能尝试执行备份后查看内容
        ConfigBackupDriver.ConfigBackupOfUpload configBackupOfUpload = Optional.ofNullable(configBackupDetail.getConfigBackupOfUpload()).orElseThrow(AppErrorEnum.VALID_WITH_NULL_VENDOR_ADAPTER::toMsfException);
//        FileServer fileServerFromAvailable = CommonUtils.getFileServerFromAvailable(backupRequest.getAvailableFileServer(), configBackupOfUpload.getFileTransType());
        FileServer availableFileServer = backupRequest.getAvailableFileServer();
        String path = CommonUtils.generalFinalUploadPath(backupRequest.getConfigType(), backupRequest.getUploadPath(), configBackupOfUpload.getFileExtension());
        commonService.makeDir(FileUtils.getFileDirPath(path), availableFileServer.getFileServerType().name());
        // 备份
        copyConfig(deviceDTO, availableFileServer, path, configBackupOfUpload, backupRequest.getHttpUrl(), backupRequest.getSceneKey(), executeDetailHelper);
        return commonService.readFile(availableFileServer.getFileServerType().name(), path);
    }

    private void backupConfig(DeviceDTO deviceDTO, BackupRequest backupRequest, ConfigBackupDriver.ConfigBackupDetail configBackupDetail, ExecuteDetailHelper executeDetailHelper) {
        ConfigBackupDriver.ConfigBackupOfUpload configBackupOfUpload = configBackupDetail.getConfigBackupOfUpload();
        FileServer fileServerFromAvailable = backupRequest.getAvailableFileServer();
        // 直接存在备份命令
        if (configBackupOfUpload.getOperation() != null) {
            // 备份
            copyConfig(deviceDTO, fileServerFromAvailable, backupRequest.getUploadPath(), configBackupOfUpload, backupRequest.getHttpUrl(),  backupRequest.getSceneKey(), executeDetailHelper);
            return;
        }
        // 不存在备份命令 只能尝试show命令后上传文件
        ConfigBackupDriver.ConfigBackupOfShow configBackupOfShow = Optional.ofNullable(configBackupDetail.getConfigBackupOfShow()).orElseThrow(AppErrorEnum.VALID_WITH_NULL_VENDOR_ADAPTER::toMsfException);
        String content = showConfig(configBackupOfShow, deviceDTO, backupRequest.getSceneKey(), executeDetailHelper);
        commonService.uploadFile(fileServerFromAvailable.getFileServerType().name(), content, backupRequest.getUploadPath());
    }


    private String showConfig(ConfigBackupDriver.ConfigBackupOfShow configBackupOfShow, DeviceDTO deviceDTO, String sceneKey, ExecuteDetailHelper executeDetailHelper) {
        Operation showOperation = configBackupOfShow.getOperation();
        List<CliConfigExecOutput> cliConfigExecOutputs = commonService.sendCommand(deviceDTO, showOperation, Collections.singletonList(showOperation.getOperation()), sceneKey, executeDetailHelper);
        CliConfigExecOutput cliConfigExecOutput = cliConfigExecOutputs.get(cliConfigExecOutputs.size() - 1);
        return cliConfigExecOutput.getResult();
    }

    private void copyConfig(DeviceDTO deviceDTO, FileServer fileServerFromAvailable, String path, ConfigBackupDriver.ConfigBackupOfUpload configBackupOfUpload, String url, String sceneKey, ExecuteDetailHelper executeDetailHelper) {
        Operation operationOfUpload = configBackupOfUpload.getOperation();
        List<String> finalCommand = CommonUtils.getFinalCommand(operationOfUpload.getOperation(), configBackupOfUpload.getFileTransType(), fileServerFromAvailable, path, url);
        commonService.sendCommand(deviceDTO, operationOfUpload, finalCommand, sceneKey, executeDetailHelper);
    }


}

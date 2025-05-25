package cn.com.ruijie.ion.netbase.ztp.pnp.oam.service.backup;

import cn.com.ruijie.ion.msf.ne.mgr.common.vendors.dto.*;
import cn.com.ruijie.ion.msf.ne.mgr.plugin.rest.VendorCallbackAdapter;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.common.errors.AppErrorEnum;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.common.utils.Constants;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.backup.ConfigBackupDriver;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @param <ConfigBackupDriver>
 * @author hsx wujiajun
 */
@Service
public class BackUpVendorCallbackAdapter extends VendorCallbackAdapter<ConfigBackupDriver> {
    @Override
    public ValidateResult validate(ValidateDriverDTO driver, String deviceKey) {
        //TODO
        //return  唯一id。 提供框架异步查询
        //回调地址： /v1/vendor/driver/validate/ne-mgr-backup/query
        //
        //执行备份逻辑。
        //下发设备校验  不校验 可以跳过



        ValidateResult validateResult = new ValidateResult();
        validateResult.setCode(0);
        return validateResult;
    }

    @Override
    public CheckResult check(Driver<ConfigBackupDriver> driver) {
//        // TODO 需调整
//        ConfigBackupDriver data = driver.getData();
//        ConfigBackupDriver.ConfigBackupInfo configBackupInfo = data.getConfigBackupInfo();
//        ConfigBackupDriver.ConfigRestoreInfo configRestoreInfo = data.getConfigRestoreInfo();
//        // 1 未配置备份/还原命令
//        if (configBackupInfo == null || configRestoreInfo == null) {
//            throw AppErrorEnum.CHECK_WITH_NULL_BACK_UP_CONFIG.toMsfException();
//        }
//        // 2 备份命令check 两种备份命令至少需要配置其一
//        boolean startUpCheck = checkConfigBackup(configBackupInfo.getStartUpConfig());
//        boolean runningCheck = checkConfigBackup(configBackupInfo.getRunningConfig());
//        if (!startUpCheck && !runningCheck) {
//            throw AppErrorEnum.CHECK_WITH_EMPTY_BACK_UP_CONFIG.toMsfException();
//        }
//        // 3 还原命令校验
//        checkConfigRestore(configRestoreInfo);
        // 返回结果
        CheckResult checkResult = new CheckResult();
        checkResult.setResult(Boolean.TRUE);
        checkResult.setDegree(CheckDegree.ALL);
        return checkResult;
    }

//    private boolean checkConfigBackup(ConfigBackupDriver.ConfigBackupDetail config) {
//        if (config == null) {
//            return false;
//        }
//        if (config.getConfigBackupOfUpload() == null && config.getConfigBackupOfShow() == null) {
//            return false;
//        }
//        checkConfigBackupOfUpload(config.getConfigBackupOfUpload(), config);
//        checkConfigBackupOfShow(config.getConfigBackupOfShow(), config);
//        return true;
//    }
//
//    private void checkConfigBackupOfUpload(ConfigBackupDriver.ConfigBackupOfUpload configBackupOfUpload, ConfigBackupDriver.ConfigBackupDetail config) {
//        if (configBackupOfUpload == null || StringUtils.isNotBlank(configBackupOfUpload.getScript())) {
//            return;
//        }
//        // 文件传输方式校验
//        Optional.of(configBackupOfUpload).map(ConfigBackupDriver.ConfigBackupOfUpload::getFileTransType).orElseThrow(AppErrorEnum.VALID_WITH_ILLEGAL_FILE_TRANS_TYPE::toMsfException);
//        // 备份命令校验
//        Optional.of(configBackupOfUpload).map(ConfigBackupDriver.ConfigBackupOfUpload::getOperation).map(ConfigBackupDriver.Operation::getOperation).orElseThrow(AppErrorEnum.CHECK_WITH_EMPTY_BACKUP_OPERATION::toMsfException);
//        if (StringUtils.isBlank(configBackupOfUpload.getOperation().getOperation())) {
//            throw AppErrorEnum.CHECK_WITH_EMPTY_BACKUP_OPERATION.toMsfException();
//        }
//        // 需要同步上传回显则必须配置查看命令
//        if (configBackupOfUpload.isWithShow()) {
//            Optional.ofNullable(config.getConfigBackupOfShow()).map(ConfigBackupDriver.ConfigBackupOfShow::getOperation).orElseThrow(AppErrorEnum.CHECK_WITH_IS_SHOW_ERROR::toMsfException);
//            if (StringUtils.isBlank(config.getConfigBackupOfShow().getOperation().getOperation())) {
//                throw AppErrorEnum.CHECK_WITH_IS_SHOW_ERROR.toMsfException();
//            }
//        }
//    }
//
//    private void checkConfigBackupOfShow(ConfigBackupDriver.ConfigBackupOfShow configBackupOfShow, ConfigBackupDriver.ConfigBackupDetail config) {
//        if (configBackupOfShow == null || StringUtils.isNotBlank(configBackupOfShow.getScript())) {
//            return;
//        }
//        Optional.ofNullable(config.getConfigBackupOfShow()).map(ConfigBackupDriver.ConfigBackupOfShow::getOperation).orElseThrow(AppErrorEnum.CHECK_WITH_EMPTY_SHOW_OPERATION::toMsfException);
//        if (StringUtils.isEmpty(config.getConfigBackupOfShow().getOperation().getOperation())) {
//            throw AppErrorEnum.CHECK_WITH_EMPTY_SHOW_OPERATION.toMsfException();
//        }
//    }
//
//    private void checkConfigRestore(ConfigBackupDriver.ConfigRestoreInfo configRestoreInfo) {
//        if (StringUtils.isNotBlank(configRestoreInfo.getScript())) {
//            return;
//        }
//        // 文件传输方式校验
//        Optional.of(configRestoreInfo).map(ConfigBackupDriver.ConfigRestoreInfo::getFileTransType).orElseThrow(AppErrorEnum.VALID_WITH_ILLEGAL_FILE_TRANS_TYPE::toMsfException);
//        // 还原命令校验
//        Optional.of(configRestoreInfo).map(ConfigBackupDriver.ConfigRestoreInfo::getConfigRestore).map(ConfigBackupDriver.Operation::getOperation).orElseThrow(AppErrorEnum.CHECK_WITH_EMPTY_RESTORE_OPERATION::toMsfException);
//        if (StringUtils.isEmpty(configRestoreInfo.getConfigRestore().getOperation())) {
//            throw AppErrorEnum.CHECK_WITH_EMPTY_RESTORE_OPERATION.toMsfException();
//        }
//        // 需要重启场景校验重启命令
//        if (configRestoreInfo.isReboot()) {
//            Optional.of(configRestoreInfo).map(ConfigBackupDriver.ConfigRestoreInfo::getRebootOperation).map(ConfigBackupDriver.Operation::getOperation).orElseThrow(AppErrorEnum.CHECK_WITH_EMPTY_REBOOT_OPERATION::toMsfException);
//            if (StringUtils.isEmpty(configRestoreInfo.getConfigRestore().getOperation())) {
//                throw AppErrorEnum.CHECK_WITH_EMPTY_REBOOT_OPERATION.toMsfException();
//            }
//        }
//    }

    @Override
    public String getBusinessName() {
        return Constants.BACKUP_BUSINESS_NAME;
    }
}

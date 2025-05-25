package cn.com.ruijie.ion.netbase.ztp.pnp.oam.service.strategy;

import cn.com.ruijie.ion.msf.ne.mgr.common.vendors.script.service.ScriptService;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.common.utils.Constants;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.backup.BackupRequest;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.backup.BackupRes;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.service.backup.BackupInvokeService;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wujiajun
 * 判断走脚本还是走 自定义cli
 */
@Slf4j
@Component
public class BackUpStrategyContext {
    @Autowired
    private ScriptService scriptService;
    @Autowired
    private BackupInvokeService backupInvokeServiceCli;

    public BackupRes executeStrategy(BackupRequest backupRequest) {
        log.info("===BackUpStrategyContext 入参==={}=====", JSONUtil.toJsonStr(backupRequest));
        //取脚本有没有配置； 怎么取？
        BackupInvokeService backupInvokeService = scriptService.getInstance(Constants.BACKUP_BUSINESS_NAME, () -> {
            return "";
        });
        if (ObjectUtil.isNotNull(backupInvokeService)) {
            BackupRes backup = backupInvokeService.backup(backupRequest);
            return backup;
        }
        BackupRes backup = backupInvokeServiceCli.backup(backupRequest);
        log.info("===BackUpStrategyContext end====");
        return backup;
    }
}

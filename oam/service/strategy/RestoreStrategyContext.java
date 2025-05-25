package cn.com.ruijie.ion.netbase.ztp.pnp.oam.service.strategy;

import cn.com.ruijie.ion.msf.ne.mgr.common.vendors.script.service.ScriptService;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.common.utils.Constants;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.backup.BackupRequest;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.restore.RestoreRequest;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.restore.RestoreRes;
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
public class RestoreStrategyContext {
    @Autowired
    private ScriptService scriptService;
    @Autowired
    private BackupInvokeService backupInvokeServiceCli;

    public RestoreRes executeStrategy(RestoreRequest restoreRequest) {
        log.info("===RestoreStrategyContext 入参==={}=====", JSONUtil.toJsonStr(restoreRequest));
        //取脚本有没有配置； 怎么取？
        BackupInvokeService backupInvokeService = scriptService.getInstance(Constants.RESTORE_BUSINESS_NAME, () -> {
            return "";
        });
        if (ObjectUtil.isNotNull(backupInvokeService)) {
            RestoreRes restore = backupInvokeService.restore(restoreRequest);
            return restore;
        }
        RestoreRes restore = backupInvokeServiceCli.restore(restoreRequest);
        log.info("===RestoreStrategyContext end====");
        return restore;
    }
}

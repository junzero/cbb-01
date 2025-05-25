package cn.com.ruijie.ion.netbase.ztp.pnp.oam.service.restore;

import cn.com.ruijie.ion.msf.ne.mgr.common.vendors.dto.*;
import cn.com.ruijie.ion.msf.ne.mgr.plugin.rest.VendorCallbackAdapter;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.common.utils.Constants;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.restore.ConfigRestoreDriver;
import org.springframework.stereotype.Service;

/**
 * @param <ConfigBackupDriver>
 * @author hsx wujiajun
 */
@Service
public class RestoreVendorCallbackAdapter extends VendorCallbackAdapter<ConfigRestoreDriver> {
    @Override
    public ValidateResult validate(ValidateDriverDTO driver, String deviceKey) {
        //TODO
        //return  唯一id。 提供框架异步查询
        //回调地址： /v1/vendor/driver/validate/ne-mgr-restore/query
        //
        //执行备份逻辑。
        //下发设备校验  不校验 可以跳过



        ValidateResult validateResult = new ValidateResult();
        validateResult.setCode(0);
        return validateResult;
    }



    @Override
    public CheckResult check(Driver<ConfigRestoreDriver> driver) {
        // 返回结果
        CheckResult checkResult = new CheckResult();
        checkResult.setResult(Boolean.TRUE);
        checkResult.setDegree(CheckDegree.ALL);
        return checkResult;
    }

    @Override
    public String getBusinessName() {
        return Constants.RESTORE_BUSINESS_NAME;
    }
}

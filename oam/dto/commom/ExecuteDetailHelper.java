package cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.commom;

import lombok.Data;

/**
 * @author hsx
 */
@Data
public class ExecuteDetailHelper {

    private Long startTime;

    private Long endTime;

    private String executeDetail;

    public static boolean valid(ExecuteDetailHelper executeDetailHelper) {
        if (executeDetailHelper == null) {
            return false;
        }
        return executeDetailHelper.getStartTime() != null && executeDetailHelper.getEndTime() != null && executeDetailHelper.getExecuteDetail() != null;
    }

}

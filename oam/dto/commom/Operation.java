package cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.commom;

import lombok.Data;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 * @author hsx
 */
@Data
public class Operation {

    /**
     * 指令类型，目前仅支持命令行
     */
    private Method method;

    /**
     * 指令，如果方式是命令行则为命令行表达式
     */
    private String operation;

    /**
     * 超时时间
     */
    private Long overTime = TimeUnit.MINUTES.toMillis(1);

    /**
     * 需要控制器额外回调处理
     */
    private Boolean needCall = Boolean.FALSE;

    /**
     * 是否需要进入配置模式
     */
    private Boolean needConfig = Boolean.FALSE;


    @Getter
    public enum Method {
        /**
         * 命令行
         */
        CLI,
    }

}

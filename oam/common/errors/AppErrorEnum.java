package cn.com.ruijie.ion.netbase.ztp.pnp.oam.common.errors;

import cn.com.ruijie.ion.msf.common.errors.MsfRuntimeException;
import cn.com.ruijie.ion.msf.i18n.utils.MessageUtil;
import cn.com.ruijie.ion.netbase.ztp.pnp.web.rest.util.ApplicationContextUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.zalando.problem.Status;

import java.util.Arrays;
import java.util.List;

/**
 * The enum App error enum.
 */
@Getter
@AllArgsConstructor
public enum AppErrorEnum {
    /**
     * The App unknown error.
     */
    VALID_WITH_NULL_DEVICE_KEY(Status.BAD_REQUEST, "OAM-VALID-REQUEST-001", "设备标识不允许为空"),
    VALID_WITH_ILLEGAL_CONFIG_TYPE(Status.BAD_REQUEST, "OAM-VALID-REQUEST-002", "请求的备份配置类型不合法"),
    VALID_WITH_NULL_UPLOAD_PATH(Status.BAD_REQUEST, "OAM-VALID-REQUEST-003", "上传文件路径不能为空"),
    VALID_WITH_EMPTY_FILE_SERVER(Status.BAD_REQUEST, "OAM-VALID-REQUEST-004", "可用的文件服务信息为空"),
    VALID_WITH_NULL_VENDOR_ADAPTER(Status.BAD_REQUEST, "OAM-VALID-REQUEST-005", "当前设备缺少相应的适配信息，请在厂商适配中维护"),
    VALID_WITH_NULL_DEVICE(Status.BAD_REQUEST, "OAM-VALID-REQUEST-006", "根据设备标识【{0}】获取设备失败，请检测设备是否已被删除"),
    VALID_WITH_NOT_MATCH_FILE_SERVER(Status.BAD_REQUEST, "OAM-VALID-REQUEST-007", "当前设备需要使用【{0}】，请检查相关服务是否就绪"),
    VALID_WITH_ILLEGAL_FILE_TRANS_TYPE(Status.BAD_REQUEST, "OAM-VALID-REQUEST-008", "文件传输方式不合法"),
    VALID_WITH_NULL_SCENE_KEY(Status.BAD_REQUEST, "OAM-VALID-REQUEST-009", "sceneKey不允许为空"),
    VALID_WITH_NULL_DOWNLOAD_PATH(Status.BAD_REQUEST, "OAM-VALID-REQUEST-010", "还原文件路径不能为空"),
    VALID_WITH_ILLEGAL_FILE_SERVER(Status.BAD_REQUEST, "OAM-VALID-REQUEST-011", "传入的文件服务器信息不完整"),
    VALID_WITH_ILLEGAL_FILE_EXTENSION(Status.BAD_REQUEST, "OAM-VALID-REQUEST-011", "文件后缀名不能为空"),

    CHECK_WITH_NULL_BACK_UP_CONFIG(Status.BAD_REQUEST, "OAM-CHECK-ADAPTER-001", "备份/还原命令配置为空"),
    CHECK_WITH_EMPTY_BACK_UP_CONFIG(Status.BAD_REQUEST, "OAM-CHECK-ADAPTER-002", "至少需要配置一份备份命令"),
    CHECK_WITH_IS_SHOW_ERROR(Status.BAD_REQUEST, "OAM-CHECK-ADAPTER-002", "需要支持同步上传回显必须配置查看命令"),
    CHECK_WITH_EMPTY_RESTORE_OPERATION(Status.BAD_REQUEST, "OAM-CHECK-ADAPTER-003", "还原命令不允许为空"),
    CHECK_WITH_EMPTY_BACKUP_OPERATION(Status.BAD_REQUEST, "OAM-CHECK-ADAPTER-003", "备份命令不允许为空"),
    CHECK_WITH_EMPTY_SHOW_OPERATION(Status.BAD_REQUEST, "OAM-CHECK-ADAPTER-003", "查看配置命令不允许为空"),
    CHECK_WITH_EMPTY_REBOOT_OPERATION(Status.BAD_REQUEST, "OAM-CHECK-ADAPTER-004", "设备需要重启时重启命令不允许为空"),
    CHECK_WITH_EMPTY_FILE_TRANS_TYPE(Status.BAD_REQUEST, "OAM-CHECK-ADAPTER-004", "文件传输类型不能为空"),
    CHECK_WITH_ILLEGAL_FILE_TRANS_TYPE(Status.BAD_REQUEST, "OAM-CHECK-ADAPTER-004", "不同类型备份文件不允许配置不一致的文件传输类型"),


    EXECUTE_CLI_ERROR(Status.BAD_REQUEST, "OAM-EXECUTE-001", "下发命令失败，详情请查看交互日志")

    ;


    /**
     * The Status.
     */
    protected Status status;
    /**
     * The Error app code.
     */
    protected String errorAppCode;
    /**
     * The Error message.
     */
    protected String errorMessage;

    /**
     * To msf exception msf runtime exception.
     *
     * @return the msf runtime exception
     */
    public MsfRuntimeException toMsfException() {
        return toMsfException(StringUtils.EMPTY);
    }

    public MsfRuntimeException toMsfException(Exception e) {
        return toMsfException(e.getMessage());
    }

    /**
     * To msf exception msf runtime exception.
     *
     * @return the msf runtime exception
     */
    public MsfRuntimeException toMsfException(String otherMessage) {
        ApplicationContext applicationContext = ApplicationContextUtil.getApplicationContext();
        MessageUtil messageUtil = applicationContext.getBean(MessageUtil.class);
        return MsfRuntimeException.builder()
            .status(this.status)
            .errorAppCode(this.errorAppCode)
            .errorMessage(messageUtil.get(this.errorAppCode))
            .otherMessage(otherMessage)
            .build();
    }

    public static MsfRuntimeException buildException(MsfRuntimeException exception, String ... args) {
        List<String> argList = Arrays.asList(args);
        return MsfRuntimeException.builder().status(exception.getStatus()).errorArgs(argList)
            .errorAppCode(exception.getErrorAppCode())
            .errorMessage(getMessage(exception.getErrorMessage(), argList))
            .otherMessage(getMessage(exception.getOtherMessage(), argList))
            .build();
    }

    private static String getMessage(String message, List<String> args) {
        if (!CollectionUtils.isEmpty(args)) {
            int index = 0;
            for (String arg : args) {
                String replaceStr = "{" + index + "}";
                message = message.replace(replaceStr, arg);
                index++;
            }
        }
        return message;
    }

}

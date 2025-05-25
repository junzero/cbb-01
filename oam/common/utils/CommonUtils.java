package cn.com.ruijie.ion.netbase.ztp.pnp.oam.common.utils;

import cn.com.ruijie.ibns.necfg.common.dto.cli.CliConfigExecOutput;
import cn.com.ruijie.ibns.nsp.common.utils.SpringContextUtil;
import cn.com.ruijie.ion.msf.i18n.utils.MessageUtil;
import cn.com.ruijie.ion.msf.security.util.RSAUtil;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.common.errors.AppErrorEnum;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.commom.ExecuteDetailHelper;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.commom.FileServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author hsx
 */
@Slf4j
public class CommonUtils {

    public static String CONFIG_SEND_INIT_CODE = "OAM_CONFIG_SEND_INIT";
    public static String CONFIG_SEND_BEGIN_CODE = "OAM_CONFIG_SEND_BEGIN";
    public static String CONFIG_SEND_END_CODE = "OAM_CONFIG_SEND_END";
    public static String CONFIG_END_COST_CODE = "OAM_CONFIG_END_COST";

    private static final String NULL_RESPONSE = "null response";

    public static final Pattern LINE = Pattern.compile("\n");



    public static List<String> getFinalCommand(String command, FileServer.FileTransType fileTransType, FileServer fileServerFromAvailable, String path, String url) {
        switch (fileTransType) {
            case FTP:
            case TFTP:
                String fileDir = FileUtils.getFileDirPath(path);
                String fileName = FileUtils.getFileNameFromPath(path);
                // TODO 还有部分占位符未补齐
                command = command.replace("${server_ip}", fileServerFromAvailable.getServerIp());
                command = command.replace("${server_type}", fileServerFromAvailable.getServerIp());
                command = command.replace("${user_name}", fileServerFromAvailable.getUserName());
                command = command.replace("${user_pwd}", fileServerFromAvailable.getPassword());
                command = command.replace("${file_dir}", fileDir);
                command = command.replace("${file_name}", fileName);
                break;
            case HTTPS:
                // https传输暂时只需要url(path) 不需要文件服务相关信息
                command = command.replace("${http_url}", url);
                break;
            default:
                throw AppErrorEnum.VALID_WITH_ILLEGAL_FILE_TRANS_TYPE.toMsfException();

        }
        return Arrays.asList(LINE.split(command));
    }

    /**
     * @param availableFileServer   可用文件服务器
     * @param fileTransType         使用文件传输方式
     * @return  文件服务信息
     */
    public static FileServer getFileServerFromAvailable(List<FileServer> availableFileServer, FileServer.FileTransType fileTransType) {
        switch (fileTransType) {
            case FTP:
                //AI_CREATE:OPTIMIZATION:1747300109406:4:4
                return availableFileServer.stream()
                    .filter(fileServer -> FileServer.FileServerType.FTP.equals(fileServer.getFileServerType()))
                    .findFirst()
                    .orElseThrow(() -> AppErrorEnum.buildException(AppErrorEnum.VALID_WITH_NOT_MATCH_FILE_SERVER.toMsfException(), FileServer.FileServerType.FTP.name()));
            case TFTP:
                return availableFileServer.stream()
                    .filter(fileServer -> FileServer.FileServerType.TFTP.equals(fileServer.getFileServerType()))
                    .findFirst()
                    .orElseThrow(() -> AppErrorEnum.buildException(AppErrorEnum.VALID_WITH_NOT_MATCH_FILE_SERVER.toMsfException(), FileServer.FileServerType.TFTP.name()));
            case HTTPS:
                // https传输暂时只需要url(path) 不需要文件服务相关信息
                return availableFileServer.stream()
                    .filter(fileServer -> FileServer.FileServerType.SFTP.equals(fileServer.getFileServerType()))
                    .findFirst()
                    .orElseThrow(() -> AppErrorEnum.buildException(AppErrorEnum.VALID_WITH_NOT_MATCH_FILE_SERVER.toMsfException(), FileServer.FileServerType.SFTP.name()));
            default:
                throw AppErrorEnum.VALID_WITH_ILLEGAL_FILE_TRANS_TYPE.toMsfException();

        }
    }


    public static String generalFinalUploadPath(Integer configType, String uploadPath, String fileType) {
        String finalUploadPath = uploadPath;
        if (StringUtils.isBlank(finalUploadPath)) {
            finalUploadPath = FileUtils.generalTempFilePath(configType, fileType);
        }
        return FileUtils.replaceFileSuffix(finalUploadPath, fileType);
    }



    /**
     * 用于记录ssh交互记录
     * @param executeDetailHelper   写入目标交互记录
     * @param resList               ssh返回结果
     * @param startTime             开始时间
     */
    public static void writeCliExecuteDetail(ExecuteDetailHelper executeDetailHelper, List<CliConfigExecOutput> resList, Long startTime) {
        if (executeDetailHelper == null) {
            return;
        }
        if (resList == null || resList.size() == 0) {
            log.error("writeCliExecuteDetail fail, res is null");
            return;
        }
        int size = resList.size();
        log.info("writeCliExecuteDetail with cli length {}", size);
        long endTime = System.currentTimeMillis();
        StringBuilder executeDetailBuilder = new StringBuilder(executeDetailHelper.getExecuteDetail());
        for (CliConfigExecOutput res : resList) {
            // 添加下发部分
            String command = res.getCommand();
            executeDetailBuilder.append(getMesWithFormatArgs(CONFIG_SEND_BEGIN_CODE, formatTime(startTime)))
                .append(command)
                .append(System.lineSeparator());
            // 添加应答部分
            String result = res.getResult();
            String processResult = StringUtils.isBlank(result) ? NULL_RESPONSE : processCliResStringSimple(result, command);
            executeDetailBuilder.append(getMesWithFormatArgs(CONFIG_SEND_END_CODE, formatTime(endTime)))
                .append(processResult)
                .append(System.lineSeparator());
        }
        // 更新结果
        executeDetailHelper.setExecuteDetail(executeDetailBuilder.toString());
        executeDetailHelper.setEndTime(endTime);
    }


    /**
     * 用于记录交互总耗时
     * @param executeDetailHelper   写入目标交互记录
     * @param deviceIp  设备ip
     */
    public static void writeExecuteDetailByOverAllCost(ExecuteDetailHelper executeDetailHelper, String deviceIp) {
        if (!ExecuteDetailHelper.valid(executeDetailHelper)) {
            return;
        }
        StringBuilder executeDetailBuilder = new StringBuilder(executeDetailHelper.getExecuteDetail());
        // 添加应答部分
        executeDetailBuilder.append(getMesWithFormatArgs(CONFIG_END_COST_CODE, deviceIp, String.valueOf(executeDetailHelper.getEndTime() - executeDetailHelper.getStartTime())))
            .append(System.lineSeparator());
        executeDetailHelper.setExecuteDetail(executeDetailBuilder.toString());
    }

    /**
     * @param code  国际化code
     * @param args  参数
     * @return  填充参数的mes
     */
    public static String getMesWithFormatArgs(String code, String ... args) {
        MessageUtil messageUtil = SpringContextUtil.getBean(MessageUtil.class);
        String mes = messageUtil.get(code);
        return String.format(mes, args);
    }


    /**
     * 将毫秒时间格式化为 yyyy-MM-dd HH:mm:ss
     * @param timeMillis    example:1747290794961
     * @return  yyyy-MM-dd HH:mm:ss
     */
    public static String formatTime(Long timeMillis) {
        // 转换为 Instant
        Instant instant = Instant.ofEpochMilli(timeMillis);
        // 转换为本地时间（LocalDateTime）
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId);
        // 定义时间格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 格式化为字符串
        return localDateTime.format(formatter);
    }

    /**
     * 当前暂时发现ssh返回结果会存在$标识的重复行，提取$标识的后续展示
     * @param output    返回结果
     * @param input     输入命令
     * @return  提取$标识的后续结果
     */
    private static String processCliResStringSimple(String output, String input) {
        // 按行分割字符串
        String[] lines = output.split("\n");
        StringBuilder result = new StringBuilder();

        // 标志位，只有找到第一个 $ 标识后才开始保留行
        boolean keepLine = !output.contains("$");

        for (String line : lines) {
            // 如果遇到 $ 标识，开始保留行
            if (line.contains("$")) {
                keepLine = true;
                continue;
            }
            // 保留后续的行
            if (keepLine) {
                if (StringUtils.equals(line, input)) {
                    continue;
                }
                result.append(line).append("\n");
            }
        }
        // 去掉末尾多余的换行符
        return result.toString().trim();
    }


}

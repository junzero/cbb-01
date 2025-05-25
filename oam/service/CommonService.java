package cn.com.ruijie.ion.netbase.ztp.pnp.oam.service;

import cn.com.ruijie.ibns.necfg.common.dto.cli.CliConfigBatchExecOutput;
import cn.com.ruijie.ibns.necfg.common.dto.cli.CliConfigBatchInput;
import cn.com.ruijie.ibns.necfg.common.dto.cli.CliConfigExecOutput;
import cn.com.ruijie.ibns.necfg.plugin.service.deploy.ConfigDeployService;
import cn.com.ruijie.ion.msf.common.errors.MsfRuntimeException;
import cn.com.ruijie.ion.msf.ne.mgr.plugin.service.DeviceInfoService;
import cn.com.ruijie.ion.msf.ne.mgr.plugin.service.dto.BasicDeviceDTO;
import cn.com.ruijie.ion.msf.ne.mgr.plugin.service.dto.DeviceDTO;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.common.errors.AppErrorEnum;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.common.utils.CommonUtils;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.backup.ConfigBackupDriver;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.commom.ExecuteDetailHelper;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.commom.Operation;
import cn.com.ruijie.ion.netbase.ztp.pnp.oam.service.file.FileServiceTemplate;
import cn.com.ruijie.ion.netbase.ztp.pnp.service.FileServerOfUncService;
import cn.com.ruijie.ion.netbase.ztp.pnp.web.rest.util.BaseInfoUtil;
import cn.com.ruijie.ion.netbase.ztp.pnp.web.rest.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author hsx
 */
@Service
@Slf4j
public class CommonService {

    @Autowired
    private ConfigDeployService configDeployService;

    @Autowired
    private DeviceInfoService deviceInfoService;

    @Autowired
    private FileServerOfUncService fileServerOfUncService;

    private Map<String, FileServiceTemplate> fileServer = new HashMap<>();


    public DeviceDTO getDevice(String deviceKey) {
        DeviceDTO device = deviceInfoService.getDeviceByKey(deviceKey);
        if (device == null) {
            throw AppErrorEnum.buildException(AppErrorEnum.VALID_WITH_NULL_DEVICE.toMsfException(), String.valueOf(deviceKey));
        }
        return device;
    }

    public void makeDir(String fileServerType, String dirPath) {
        fileServerOfUncService.makeDir(dirPath, fileServerType);
//        FileServiceTemplate fileServiceTemplate = fileServer.get(fileServerType);
//       fileServiceTemplate.makeDir(dirPath);
    }

    public String readFile(String fileServerType, String filePath) {
        String host = fileServerOfUncService.checkFileExistAndGetAvailableAddress(filePath, fileServerType);
        File file = fileServerOfUncService.downloadFile(filePath, host, fileServerType);
        long fileLength = file.length();
        byte[] fileContent = new byte[(int) fileLength];
        /* 读取文件内容 */
        //AI_CREATE:OPTIMIZATION:1732505235413:12:12
        // 使用 try-with-resources 确保资源被关闭
        try (FileInputStream in = new FileInputStream(file)) {
            int bytesRead = 0;
            int totalBytesRead = 0;

            // 循环读取文件内容
            while (totalBytesRead < fileContent.length && (bytesRead = in.read(fileContent, totalBytesRead, fileContent.length - totalBytesRead)) != -1) {
                totalBytesRead += bytesRead;
            }
        } catch (IOException e) {
            log.warn("Read fileserver failed.", e);
            return null;
        }
        /* 返回文件内容 */
        try {
            return new String(fileContent, BaseInfoUtil.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            log.warn("Convert fileserver content failed.", e);
            return null;
        }
//        FileServiceTemplate fileServiceTemplate = fileServer.get(fileServerType);
//        return fileServiceTemplate.read(filePath);
    }

    public void uploadFile(String fileServerType, String content, String filePath) {
        MultipartFile configFile = new MockMultipartFile(filePath, filePath, "text/plain", content.getBytes());
        String fileDirPath = FileUtils.getFileDirPath(filePath);
        String fileName = FileUtils.getFileNameFromPath(filePath);
        fileServerOfUncService.uploadFile(configFile, fileDirPath, fileName, fileServerType);
//        FileServiceTemplate fileServiceTemplate = fileServer.get(fileServerType);
//        fileServiceTemplate.uploadFile(content, filePath);
    }





    public List<CliConfigExecOutput> sendCommand(DeviceDTO device, Operation operation, List<String> commandList, String sceneKey, ExecuteDetailHelper executeDetailHelper) {
        if (null == operation || CollectionUtils.isEmpty(commandList)) {
            log.warn("Device {}_{} commandList is empty", device.getId(), device.getDeviceIp());
            return Collections.emptyList();
        }
        String commands = String.join(",", commandList);
        try {
            CliConfigBatchInput input = new CliConfigBatchInput();
            // 先写死
            Operation.Method type = Operation.Method.CLI;
            input.setType(type.name().toUpperCase());
            input.setCommands(commandList);
            input.setDeviceKey(device.getDeviceKey());
            input.setIp(device.getDeviceIp());
            input.setOrigin(sceneKey);
            input.setWaitDeviceFeedback(Boolean.TRUE);
            input.setConfigMode(operation.getNeedConfig());
            input.setVendor(device.getDeviceManufactor());
            input.setMaxReadTimeOut(operation.getOverTime());
            Long startTime = System.currentTimeMillis();
            CliConfigBatchExecOutput cliConfigBatchExecOutput = configDeployService.sendConfigBatchNew(input);
            CommonUtils.writeCliExecuteDetail(executeDetailHelper, cliConfigBatchExecOutput.getRes(), startTime);
            if (!cliConfigBatchExecOutput.getSuccess()) {
                log.error("Device {}_{} invoke CLI error, cli={},errorMsg={}", device.getId(), device.getDeviceIp(),
                    commands, cliConfigBatchExecOutput.getExceptionMessage());
                throw AppErrorEnum.EXECUTE_CLI_ERROR.toMsfException();
            }
            return cliConfigBatchExecOutput.getRes();
        } catch (MsfRuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Device {}_{} invoke CLI error, cli={}", device.getId(), device.getDeviceIp(), commands, e);
            throw cn.com.ruijie.ion.msf.ne.mgr.common.vendors.errors.AppErrorEnum.NE_VENDOR_CLIENT_ERROR_O06.toMsfException(commands);
        }
    }

}

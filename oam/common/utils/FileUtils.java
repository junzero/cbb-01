package cn.com.ruijie.ion.netbase.ztp.pnp.oam.common.utils;

import cn.com.ruijie.ion.netbase.ztp.pnp.constant.FilePathConstants;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;

/**
 * @author hsx
 */
@Slf4j
public class FileUtils {

    /**
     * 根据文件路径获取文件名
     * @param path 文件路径
     * @return 文件名
     */
    public static String getFileNameFromPath(String path) {
       return path.substring(path.lastIndexOf('/') + 1);
    }

    /**
     * 获取去拓展属性的文件名
     * @param fileName 文件名
     * @return 文件名去拓展属性
     */
    public static String getFileNameWithoutSuffix(String fileName) {
        String fileNameWithoutSuffix;
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex != -1) {
            fileNameWithoutSuffix = fileName.substring(0, dotIndex);
        } else {
            // 如果没有后缀名，则直接使用文件名
            fileNameWithoutSuffix = fileName;
        }
        return fileNameWithoutSuffix;
    }

    /**
     * 获取文件的后缀名
     * @param fileName 文件名
     * @return 文件的后缀名
     */
    public static String getFileNameSuffix(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex != -1) {
            return fileName.substring(dotIndex + 1);
        } else {
            return null;
        }
    }

    /**
     * 获取文件的上级文件夹路径
     * @param path 文件路径
     * @return 对应文件的上级文件夹路径
     */
    public static String getFileDirPath(String path) {
        int index = path.lastIndexOf(FilePathConstants.FILE_CHAR_SPLIT);
        // 未提供路径时，默认返回根目录
        if (index == 0 || index == -1) {
            return FilePathConstants.FILE_SPLIT;
        }
        return path.substring(0, index);
    }

    public static String generalTempFilePath(Integer configType, String fileType) {
        String currentDate =  ZonedDateTime.now().toLocalDate().toString();
        // 未提供路径时，默认返回根目录
        return String.format("/%s-temp/%s", currentDate, BackupUtils.getFileName(configType, fileType));
    }

    /**
     * 替换文件名的后缀名
     *
     * @param fileName 原始的文件名（例如 "AAA.text"）
     * @param newSuffix 新的后缀名（例如 ".deb"）
     * @return 替换后新的文件名（例如 "AAA.deb"）
     */
    public static String replaceFileSuffix(String fileName, String newSuffix) {
        // 参数校验
        if (fileName == null || newSuffix == null) {
            log.warn("replaceFileSuffix with null, fileName {} newSuffix {}", fileName, newSuffix);
            return fileName;
        }

        // 确定最后一个 '.' 的位置
        int lastDotIndex = fileName.lastIndexOf('.');

        // 如果文件名中包含 '.'，替换后缀名
        if (lastDotIndex != -1) {
            // 截取文件名的主干部分，并拼接新的后缀名
            return fileName.substring(0, lastDotIndex) + newSuffix;
        }

        // 如果文件名没有后缀，直接添加新的后缀名
        return fileName + newSuffix;
    }


}

package cn.com.ruijie.ion.netbase.ztp.pnp.oam.service.file;

/**
 * @author hsx
 */
public interface FileServiceTemplate {

    /**
     * 读取文件内容
     * @param filePath  文件路径
     * @return  文件读取内容
     */
    String read(String filePath);

    /**
     * 创建文件加
     * @param dir   文件夹路径
     */
    void makeDir(String dir);

    /**
     * 将String写入文件服务器
     * @param content   文件内容
     * @param filePath  上传路径
     */
    void uploadFile(String content, String filePath);

}

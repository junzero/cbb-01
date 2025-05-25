package cn.com.ruijie.ion.netbase.ztp.pnp.oam.dto.commom;

import lombok.Builder;
import lombok.Data;

/**
 * @author hsx
 */
@Data
@Builder
public class PlaceHolderHelper {

    /**
     * 上传 下载 路径 可以是FTP TFTP HTTPS
     */
    private String path;



}

package terrell.common.ceph.model;
/**
 * @author: TerrellChen
 * @version: Created in 下午3:53 2/4/19
 */

/**
 * Description:
 */
public class FileUploadResult {
    boolean success;
    String downloadUrl;
    String filename;

    public FileUploadResult() {
        this.success = false;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "FileUploadResult{" +
                "success=" + success +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", filename='" + filename + '\'' +
                '}';
    }
}

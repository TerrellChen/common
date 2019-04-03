package terrell.common.ceph;
/**
 * @author: TerrellChen
 * @version: Created in 下午3:52 2/4/19
 */

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import terrell.common.ceph.model.FileUploadResult;
import terrell.common.log.ExceptionUtil;

import java.io.ByteArrayInputStream;
import java.io.File;

/**
 * Description:
 */
public class CephUtil {
    private static Logger logger = LoggerFactory.getLogger(CephUtil.class);

    private static volatile AmazonS3Client instance;

    private static final String bucketName = "";
    public static final String AccessKeyId = "";
    public static final String SecretAccessKey = "";
    public static final String endPoint = "";

    public static AmazonS3Client getInstance(){
        if (instance == null){
            synchronized (CephUtil.class){
                if (instance == null){
                    ClientConfiguration clientConfig;
                    BasicAWSCredentials basicCred;
                    try {
                        basicCred = new BasicAWSCredentials(AccessKeyId, SecretAccessKey);
                        clientConfig = new ClientConfiguration();

                        clientConfig.setMaxConnections(100);
                        clientConfig.setRequestTimeout(1000000);
                        clientConfig.setProtocol(Protocol.HTTP);
                        clientConfig.setSignerOverride("S3SignerType");
                    } catch (Exception e) {
                        throw new AmazonClientException("failed",e);
                    }
                    instance = new AmazonS3Client(basicCred, clientConfig);
                    instance.setEndpoint(endPoint);

                    S3ClientOptions s3ClientOptions = new S3ClientOptions();
                    s3ClientOptions.setPathStyleAccess(true);
                    instance.setS3ClientOptions(s3ClientOptions);
                }
            }
        }
        return instance;
    }

    public static String getBucketName(String env){
        if(Strings.isNullOrEmpty(env)){
            return "";
        }
        return env+bucketName;
    }

    public static PutObjectResult putObject(String bucketName, String cephKey, byte[] bytes){
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bytes.length);
        PutObjectResult pr = getInstance().putObject(
                bucketName,
                cephKey,
                new ByteArrayInputStream(bytes),
                metadata);
        getInstance().setObjectAcl(bucketName, cephKey, CannedAccessControlList.PublicRead);
        return pr;
    }

    public static PutObjectResult putObject(String bucketName, String cephKey, File file){
        PutObjectResult pr = getInstance().putObject(
                bucketName,
                cephKey,
                file);
        getInstance().setObjectAcl(bucketName, cephKey, CannedAccessControlList.PublicRead);
        return pr;
    }

    public static FileUploadResult uploadFile(String id, String env, String filename, byte[] bytes){
        FileUploadResult result = new FileUploadResult();
        try {
            AmazonS3Client amazonS3Client = getInstance();
            StringBuilder cephKeyBuilder = new StringBuilder();
            cephKeyBuilder.append(env).append("/").append(id).append("/").append(filename);
            String cephKey = cephKeyBuilder.toString();
            //阻塞方法用于上传ceph
            PutObjectResult putObjectResult = putObject(getBucketName(env), cephKey, bytes);
            amazonS3Client.setObjectAcl(getBucketName(env), cephKey, CannedAccessControlList.PublicRead);
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(getBucketName(env), cephKey);
            String cephUrl = amazonS3Client.generatePresignedUrl(request).toString();
            String url = cephUrl.substring(0, cephUrl.indexOf("?AWSAccessKeyId"));
            result.setDownloadUrl(url);
            result.setSuccess(true);
            return result;
        } catch (Exception e){
            logger.error(ExceptionUtil.getFullStackTrace(e));
            return result;
        }
    }
}

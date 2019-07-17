package terrell.common.http.model;
/**
 * @author: TerrellChen
 * @version: Created in 上午10:53 2/4/19
 */

/**
 * Description:
 */
public class FetchData {
    int statusCode;
    String content;
    String url;


    public FetchData(int statusCode, String content, String url){
        this.statusCode = statusCode;
        this.content = content;
        this.url = url;
    }


    public int getStatusCode() {
        return statusCode;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "FetchData{" +
                "statusCode=" + statusCode +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}

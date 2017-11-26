package assignment2_2;

import assignment2.common.ParamHolder;

@SuppressWarnings("Duplicates")
public class RequestBuilder {

    private HttpRequest request;
    private ParamHolder holder;
    private String rawData;

    public RequestBuilder(String rawData, ParamHolder holder) {
        this.request = new HttpRequest();
        this.holder  = holder;
        this.rawData = rawData;
        parseRawData();
    }

    private void parseRawData() {
        String   all         = this.rawData;
        String   requestLine = all.substring(0, all.indexOf('\r'));
        String[] arr         = requestLine.split("\\s+");
        request.method       = arr[0];
        request.path         = arr[1];
        request.version      = arr[2];
        String[] res         = all.split("[\r\n]+");
        int      i           = 1;
        for (; i < res.length; i++) {
            String resHeader = res[i];
            if (resHeader.contains(":")) {
                int idx = resHeader.indexOf(':');
                String key = resHeader.substring(0, idx).trim();
                String value = resHeader.substring(idx + 1).trim();
                request.header.put(key, value);
            } else {
                break;
            }
        }
        // it means all the remaining data is assignment2.response fileBody
        StringBuilder bodyData = new StringBuilder();
        for (; i < res.length; i++) {
            bodyData.append(res[i]);
        }

        request.body = bodyData.toString();
        request.targetPath  = holder.directory + request.path;
    }

    public HttpRequest getRequest() {
        return request;
    }
}

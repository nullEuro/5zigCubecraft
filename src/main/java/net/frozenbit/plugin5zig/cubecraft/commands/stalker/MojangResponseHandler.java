package net.frozenbit.plugin5zig.cubecraft.commands.stalker;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.UUID;

public class MojangResponseHandler implements ResponseHandler<HashMap<String, UUID>> {
    @Override
    public HashMap<String, UUID> handleResponse(HttpResponse response) throws IOException {
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= 300) {
            throw new HttpResponseException(
                    statusLine.getStatusCode(),
                    statusLine.getReasonPhrase());
        }
        if (entity == null) {
            throw new ClientProtocolException("Response contains no content");
        }
        HashMap<String, UUID> nameIdPairs = new HashMap<>();
        JSONArray jsonResponse = new JSONArray(EntityUtils.toString(entity));
        for (int i = 0; i < jsonResponse.length(); i++) {
            JSONObject nameIdPair = jsonResponse.getJSONObject(i);
            String stringId = nameIdPair.getString("id");
            UUID id = new UUID(
                    new BigInteger(stringId.substring(0, 16), 16).longValue(),
                    new BigInteger(stringId.substring(16), 16).longValue());
            nameIdPairs.put(nameIdPair.getString("name"), id);
        }
        return nameIdPairs;
    }
}

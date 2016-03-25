package com.zju.lab.ct.handlers;

import com.zju.lab.ct.algorithm.feature.ImageFeature;
import com.zju.lab.ct.annotations.RouteHandler;
import com.zju.lab.ct.annotations.RouteMapping;
import com.zju.lab.ct.annotations.RouteMethod;
import com.zju.lab.ct.dao.FeatureDao;
import com.zju.lab.ct.model.HttpCode;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by wuhaitao on 2016/2/25.
 */
@RouteHandler("/api/ct")
public class RecognitionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecognitionHandler.class);
    private CloseableHttpClient httpClient = HttpClients.createDefault();
    private FeatureDao featureDao;

    public RecognitionHandler(FeatureDao featureDao) {
        this.featureDao = featureDao;
    }


    /**
     * /api/ct/predict
     * POST
     * 病变识别handler
     * @return
     */
    @RouteMapping(value = "/predict", method = RouteMethod.POST)
    public Handler<RoutingContext> predictLesionType() {
        return ctx -> {
            JsonObject data = ctx.getBodyAsJson();
            String image = data.getString("image");
            int x1 = data.getInteger("x1");
            int y1 = data.getInteger("y1");
            int x2 = data.getInteger("x2");
            int y2 = data.getInteger("y2");
            URI uri = null;
            HttpServerResponse re = ctx.response();
            re.putHeader("Access-Control-Allow-Origin", "*").putHeader("Access-Control-Allow-Methods", "PUT,POST,GET,DELETE,OPTIONS").putHeader("Access-Control-Max-Age", "60");
            try {
                uri = new URIBuilder().setHost("http://127.0.0.1:8081/predict")
                        .setPort(8081)
                        .setParameter("image", image)
                        .setParameter("x1", String.valueOf(x1))
                        .setParameter("y1", String.valueOf(y1))
                        .setParameter("x2", String.valueOf(x2))
                        .setParameter("y2", String.valueOf(y2))
                        .build();
                StringBuilder sb = new StringBuilder("http://127.0.0.1:8081/predict?image=").append(image.replace("\\","/"))
                        .append("&x1=").append(x1)
                        .append("&y1=").append(y1)
                        .append("&x2=").append(x2)
                        .append("&y2=").append(y2);
                HttpGet get = new HttpGet(sb.toString());
                CloseableHttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (200 == statusCode) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        String res = EntityUtils.toString(entity,"utf-8");
                        JsonObject result = new JsonObject();
                        result.put("lesion", res);
                        re.end(result.encode());
                    }
                } else {
                    re.setStatusCode(500).end();
                }

            } catch (URISyntaxException e) {
                e.printStackTrace();
                re.setStatusCode(500).end();
            } catch (IOException e) {
                e.printStackTrace();
                re.setStatusCode(500).end();
            }
        };
    }

    /**
     * /api/ct/addLiverfeature
     * 添加肝脏病变特征到数据表handler
     * @return
     */
    @RouteMapping(value = "/addLiverfeature", method = RouteMethod.POST)
    public Handler<RoutingContext> addLiverfeature() {
        return ctx -> {
            JsonObject data = ctx.getBodyAsJson();
            String image = data.getString("image");
            int x1 = data.getInteger("x1");
            int y1 = data.getInteger("y1");
            int x2 = data.getInteger("x2");
            int y2 = data.getInteger("y2");
            String label = data.getString("label");
            ImageFeature imageFeature = new ImageFeature();
            JsonObject result = new JsonObject();
            HttpServerResponse re = ctx.response();
            re.putHeader("Access-Control-Allow-Origin", "*").putHeader("Access-Control-Allow-Methods", "PUT,POST,GET,DELETE,OPTIONS").putHeader("Access-Control-Max-Age", "60");
            try {
                double[] feature = imageFeature.getFeature(image, x1, y1, x2, y2);
                featureDao.addLiverFeature(feature, label, res -> {
                    if ("success".equals(res)){
                        result.put("result", "特征入库成功");
                        re.setChunked(true).setStatusCode(HttpCode.OK.getCode()).end(result.encode());
                    }
                    else{
                        result.put("result", res);
                        re.setChunked(true).setStatusCode(HttpCode.INTERNAL_SERVER_ERROR.getCode()).end(result.encode());
                    }
                });
            } catch (IOException e) {
                result.put("result", e.getMessage());
                re.setChunked(true).setStatusCode(HttpCode.NOT_FOUND.getCode()).end(result.encode());
            }
        };
    }

    /**
     * /api/ct/addLungfeature
     * 添加肺部病变特征到数据表handler
     * @return
     */
    @RouteMapping(value = "/addLungfeature", method = RouteMethod.POST)
    public Handler<RoutingContext> addLungfeature() {
        return ctx -> {
            JsonObject data = ctx.getBodyAsJson();
            String image = data.getString("image");
            int x1 = data.getInteger("x1");
            int y1 = data.getInteger("y1");
            int x2 = data.getInteger("x2");
            int y2 = data.getInteger("y2");
            String label = data.getString("label");
            ImageFeature imageFeature = new ImageFeature();
            JsonObject result = new JsonObject();
            HttpServerResponse re = ctx.response();
            re.putHeader("Access-Control-Allow-Origin", "*").putHeader("Access-Control-Allow-Methods", "PUT,POST,GET,DELETE,OPTIONS").putHeader("Access-Control-Max-Age", "60");
            try {
                double[] feature = imageFeature.getFeature(image, x1, y1, x2, y2);
                featureDao.addLungFeature(feature, label, res -> {
                    if ("success".equals(res)){
                        result.put("result", "特征入库成功");
                        re.setChunked(true).setStatusCode(HttpCode.OK.getCode()).end(result.encode());
                    }
                    else{
                        result.put("result", res);
                        re.setChunked(true).setStatusCode(HttpCode.INTERNAL_SERVER_ERROR.getCode()).end(result.encode());
                    }
                });
            } catch (IOException e) {
                result.put("result", e.getMessage());
                re.setChunked(true).setStatusCode(HttpCode.NOT_FOUND.getCode()).end(result.encode());
            }
        };
    }
}
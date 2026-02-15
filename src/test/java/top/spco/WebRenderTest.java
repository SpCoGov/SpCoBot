package top.spco;

import com.google.gson.*;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class WebRenderTest {
    @Test
    public void downloadTest() {
        try {
            List<String> additionalElements = Arrays.asList(".custom-element");
            List<BufferedImage> images = generateScreenshotV2(
                    "https://zh.wikipedia.org/wiki/A",
                    null,
                    true,
                    true,
                    additionalElements
            );

            if (images != null) {
                int index = 1;
                for (BufferedImage image : images) {
                    File output = new File("screenshot_" + index + ".png");
                    ImageIO.write(image, "png", output);
                    System.out.println("Saved image: " + output.getAbsolutePath());
                    index++;
                }
            } else {
                System.out.println("No images were generated.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void download2Test() {
        try {
            Map<String, String> headers = new HashMap<>();

            List<BufferedImage> images = generateScreenshotV1(
                    "https://zh.wikipedia.org/",
                    "https://zh.wikipedia.org/wiki/User:YuCheinSYQ",
                    headers,
                    null,
                    true
            );

            if (images != null) {
                int index = 1;
                for (BufferedImage image : images) {
                    File output = new File("screenshot_" + index + ".png");
                    ImageIO.write(image, "png", output);
                    System.out.println("Saved image: " + output.getAbsolutePath());
                    index++;
                }
            } else {
                System.out.println("No images were generated.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void webRenderTest() {
        // 示例调用
        String method1 = "source";
        String targetUrl1 = "http://example.com";
        boolean ignoreStatus1 = false;

        String method2 = "screenshot";
        String targetUrl2 = null; // 非 source 方法，不需要 URL
        boolean ignoreStatus2 = true;

        // 调用 webrender 方法
        String result1 = webrender(method1, targetUrl1, ignoreStatus1);
        String result2 = webrender(method2, targetUrl2, ignoreStatus2);

        // 输出结果
        System.out.println("Generated URL 1: " + result1);
        System.out.println("Generated URL 2: " + result2);
    }

    public static String download(
            String urlString,
            String filename,
            String directory,
            int expectedStatusCode,
            String method,
            String postData,
            int timeout,
            int attempts
    ) throws Exception {
        if (method == null || method.isEmpty()) {
            method = "GET";
        }

        if (directory == null || directory.isEmpty()) {
            directory = System.getProperty("java.io.tmpdir"); // 默认缓存目录
        }

        // 重试机制
        for (int i = 0; i < attempts; i++) {
            try {
                // 发起 HTTP 请求
                byte[] data = fetchUrl(urlString, method, postData, expectedStatusCode, timeout);

                // 生成随机文件名（如果未指定）
                if (filename == null || filename.isEmpty()) {
                    filename = UUID.randomUUID() + ".txt"; // 默认扩展名为 .txt
                }

                // 创建目标目录
                Path directoryPath = Paths.get(directory);
                if (!Files.exists(directoryPath)) {
                    Files.createDirectories(directoryPath);
                }

                // 保存文件
                Path filePath = Paths.get(directory, filename);
                Files.write(filePath, data);

                // 成功返回文件路径
                return filePath.toAbsolutePath().toString();
            } catch (Exception e) {
                // 捕获异常进行重试
                if (i == attempts - 1) {
                    throw e; // 最后一次失败时抛出异常
                }
                Thread.sleep(3000); // 等待 3 秒后重试
            }
        }

        return null; // 永远不会到达这里
    }

    private static byte[] fetchUrl(
            String urlString,
            String method,
            String postData,
            int expectedStatusCode,
            int timeout
    ) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setConnectTimeout(timeout * 1000);
            connection.setReadTimeout(timeout * 1000);

            // 如果是 POST 请求，写入数据
            if ("POST".equalsIgnoreCase(method) && postData != null) {
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(postData.getBytes());
                    os.flush();
                }
            }

            // 检查响应状态码
            int statusCode = connection.getResponseCode();
            if (statusCode != expectedStatusCode) {
                throw new IOException("Unexpected HTTP status code: " + statusCode);
            }

            // 读取响应内容
            try (InputStream inputStream = connection.getInputStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                return outputStream.toByteArray();
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static final String WEB_RENDER = "http://192.168.50.155:15551/";
    private static boolean webRenderStatus = true; // 示例：全局变量，代表远程 WebRender 服务是否可用

    /**
     * 根据请求方法生成 WebRender URL。
     *
     * @param method       API 方法。
     * @param url          当方法为 "source" 时指定的目标 URL。
     * @param ignoreStatus 是否忽略服务状态。
     * @return 生成的 WebRender URL。
     */
    public static String webrender(String method, String url, boolean ignoreStatus) {

        if ("source".equalsIgnoreCase(method)) {
            url = (url == null || url.isEmpty()) ? "" : url;
            if (webRenderStatus || ignoreStatus) {
                return WEB_RENDER + "source?url=" + url;
            }
        } else {
            url = ""; // 默认清空 URL，因为其他方法不需要它
            if (webRenderStatus || ignoreStatus) {
                return WEB_RENDER + method;
            }
        }

        return url; // 如果状态不可用，返回空字符串
    }

    private static final List<String> INFOBOX_ELEMENTS = Arrays.asList("div#infoboxborder",
            ".arcaeabox",
            ".infobox",
            ".infoboxtable",
            ".infotemplatebox",
            ".moe-infobox",
            ".notaninfobox",
            ".portable-infobox",
            ".rotable",
            ".skin-infobox",
            ".tpl-infobox");
    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .readTimeout(300, TimeUnit.SECONDS)  // 设置读取超时时间
            .connectTimeout(300, TimeUnit.SECONDS)  // 设置连接超时时间
            .build();
    ;
    private static final Gson gson = new Gson();

    public static List<BufferedImage> generateScreenshotV2(
            String pageLink,
            String section,
            boolean allowSpecialPage,
            boolean contentMode,
            List<String> additionalElements
    ) throws Exception {
        List<String> elements = new ArrayList<>(INFOBOX_ELEMENTS);
        if (additionalElements != null && !additionalElements.isEmpty()) {
            elements.addAll(additionalElements);
        }

        String responseJson;
        if (section == null || section.isEmpty()) {
            // 生成元素截图
            if (allowSpecialPage) {
                if (contentMode) {
                    elements.add(0, ".mw-body-content");
                } else {
                    elements.add(0, ".diff");
                }
            }

            System.out.println("[WebRender] Generating element screenshot...");
            try {
                JsonObject postData = new JsonObject();
                postData.addProperty("url", pageLink);
                postData.add("element", gson.toJsonTree(elements));

                responseJson = postRequest(
                        webrender("element_screenshot"),
                        postData.toString()
                );
            } catch (Exception e) {
                System.err.println("[WebRender] Generation Failed: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        } else {
            // 生成指定 section 的截图
            System.out.println("[WebRender] Generating section screenshot...");
            try {
                section = section.replace(" ", "_");

                JsonObject postData = new JsonObject();
                postData.addProperty("url", pageLink);
                postData.addProperty("section", section);

                responseJson = postRequest(
                        webrender("section_screenshot"),
                        postData.toString()
                );
            } catch (Exception e) {
                System.err.println("[WebRender] Generation Failed. " + e.getMessage());
                return null;
            }
        }

        // 解析 JSON 响应并处理图片
        JsonArray imagesArray = JsonParser.parseString(responseJson).getAsJsonArray();
        List<BufferedImage> imageList = new ArrayList<>();

        for (JsonElement element : imagesArray) {
            String base64Image = element.getAsString();
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
                BufferedImage image = ImageIO.read(bais);
                imageList.add(image);
            }
        }

        return imageList;
    }

    private static String webrender(String method) {
        if ("element_screenshot".equals(method) || "section_screenshot".equals(method)) {
            return WEB_RENDER + method;
        }
        return "";
    }


    private static String postRequest(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();
        System.out.println(url);
        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
        //jsonObject.addProperty("content", "");
        System.out.println(jsonObject.toString());
        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            System.out.println(responseBody);
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return responseBody;
        }
    }

    private static Document processDocument(Document document, String link, String section, boolean allowSpecialPage) {
        if (section == null || section.isEmpty()) {
            // Process infobox or special page
            if (allowSpecialPage) {
                Element diffElement = document.selectFirst("table.diff");
                if (diffElement != null) {
                    System.out.println("Found diff element...");
                    return Jsoup.parse(diffElement.outerHtml());
                }
            }

            for (String infoboxClass : INFOBOX_ELEMENTS) {
                Element infobox = document.selectFirst(infoboxClass);
                if (infobox != null) {
                    System.out.println("Found infobox...");
                    return Jsoup.parse(infobox.outerHtml());
                }
            }
        } else {
            // Process specific section
            Elements sectionHeaders = document.select("h1, h2, h3, h4, h5, h6");
            for (Element header : sectionHeaders) {
                if (header.select("span[id=" + section + "]").size() > 0) {
                    System.out.println("Found section...");
                    Element content = header.nextElementSibling();
                    StringBuilder sectionHtml = new StringBuilder(header.outerHtml());
                    while (content != null && !content.tagName().matches("h[1-6]")) {
                        sectionHtml.append(content.outerHtml());
                        content = content.nextElementSibling();
                    }
                    return Jsoup.parse(sectionHtml.toString());
                }
            }
        }

        System.out.println("Nothing found...");
        return document;
    }

    private static final String CACHE_PATH = "./cache/";

    public static List<BufferedImage> generateScreenshotV1(
            String link,
            String pageLink,
            Map<String, String> headers,
            String section,
            boolean allowSpecialPage
    ) throws IOException {

        System.out.println("Starting to find infobox/section...");
        if (!link.endsWith("/")) {
            link += "/";
        }

        // Download HTML content
        Document document;
        try {
            document = Jsoup.connect(pageLink)
                    .headers(headers)
                    .timeout(20000)
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // Save to temporary file
        String uniqueFileName = UUID.randomUUID().toString() + ".html";
        File tempHtmlFile = new File(CACHE_PATH + uniqueFileName);
        if (!tempHtmlFile.getParentFile().exists()) {
            tempHtmlFile.getParentFile().mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempHtmlFile))) {
            writer.write("<!DOCTYPE html>\n");
            writer.write(document.outerHtml());
        }

        // Fix relative URLs and modify HTML content
        fixRelativeUrls(document, link);
        Document processedDocument = processDocument(document, link, section, allowSpecialPage);

        // Save processed content
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempHtmlFile))) {
            writer.write(processedDocument.outerHtml());
        }

        // Render the page to images
        Map<String, Object> renderRequest = new HashMap<>();
        renderRequest.put("content", processedDocument.outerHtml());
        renderRequest.put("width", 1000);
        renderRequest.put("mw", true);

        String renderResponse = postRequest(
                WEB_RENDER,
                gson.toJson(renderRequest)
        );

        if (renderResponse == null) {
            System.err.println("Failed to render.");
            return null;
        }

        JsonArray imagesArray = JsonParser.parseString(renderResponse).getAsJsonArray();
        List<BufferedImage> imageList = new ArrayList<>();

        for (JsonElement element : imagesArray) {
            String base64Image = element.getAsString();
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
                BufferedImage image = ImageIO.read(bais);
                imageList.add(image);
            }
        }

        return imageList;
    }

    private static void fixRelativeUrls(Document document, String baseUrl) {
        Elements elements = document.select("[href], [src], [srcset], [style]");
        for (Element element : elements) {
            if (element.hasAttr("href")) {
                element.attr("href", document.baseUri() + element.attr("href"));
            }
            if (element.hasAttr("src")) {
                element.attr("src", document.baseUri() + element.attr("src"));
            }
            if (element.hasAttr("srcset")) {
                element.attr("srcset", document.baseUri() + element.attr("srcset"));
            }
            if (element.hasAttr("style")) {
                String style = element.attr("style");
                element.attr("style", style.replaceAll("url\\(/", "url(" + baseUrl + "/"));
            }
        }
    }
}

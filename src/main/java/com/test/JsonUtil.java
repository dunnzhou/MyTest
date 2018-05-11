package com.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by dunn on 13/03/2017.
 */
public class JsonUtil {
    private static Logger log = LoggerFactory.getLogger(JsonUtil.class);

    private static ObjectMapper objectMapper = new ObjectMapper();//单实例多线程安全的
    private static ConcurrentMap<String, CollectionLikeType> typeHashMap = Maps.newConcurrentMap();
    private static ConcurrentMap<String, JavaType> javaTypeMap = Maps.newConcurrentMap();

    static {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    public static String toJson(Object obj) {
        if (obj == null) {
            return "";
        }
        try {
            String json = objectMapper.writeValueAsString(obj);
            return json;
        } catch (JsonProcessingException e) {
            log.error("to json error", e);
        }
        return "";
    }


    public static JsonNode toJsonNode(Object obj) {
        try {
            String json = objectMapper.writeValueAsString(obj);
            JsonNode node = objectMapper.readTree(json);
            return node;
        } catch (Exception e) {
            log.error("to json error", e);
        }
        return null;
    }

    public static JsonNode toJsonNode(String json) {
        if (json == null) {
            return null;
        }
        try {
            JsonNode node = objectMapper.readTree(json);
            return node;
        } catch (Exception e) {
            log.error("to json error", e);
        }
        return null;
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        try {
            T t = objectMapper.readValue(json, clazz);
            return t;
        } catch (Exception e) {
            log.error("json parse error,json=" + json, e);
        }
        return null;
    }

    public static <T> T toObject(JsonNode json, Class<T> clazz) {

        try {
            T t = objectMapper.readValue(json.toString(), clazz);
            return t;
        } catch (Exception e) {
            log.error("json parse error,json=" + json, e);
        }
        return null;
    }

    public static <T> T toObject(JsonNode json, Class<T> clazz, Map<String, String> nameMap) {
        try {
            ObjectMapper newMapper = objectMapper.copy();
            PropertyNamingStrategy namingStrategy = getNamingStrategy(nameMap);
            newMapper.setPropertyNamingStrategy(namingStrategy);
            T t = newMapper.readValue(json.toString(), clazz);

            for (String key : nameMap.keySet()) {
                JsonNode valueNode = json.path(nameMap.get(key));
                JsonNodeType nodeType = valueNode.getNodeType();
                if (!JsonNodeType.MISSING.equals(nodeType) && !JsonNodeType.ARRAY.equals(nodeType) && !JsonNodeType.OBJECT.equals(nodeType)) {
                    log.error("千万不要尝试使用这个方法，你会后悔的。");
                }
            }
            return t;
        } catch (Exception e) {
            log.error("json parse error,json=" + json, e);
        }
        return null;
    }

    private static PropertyNamingStrategy getNamingStrategy(final Map<String, String> nameMap) {
        return new MappedPropertyNamingStrategy(nameMap);
    }

    public static <T> T getNodeToObject(JsonNode json, String nodeName, Class<T> clazz) {
        try {
            JsonNode node = json.findPath(nodeName);
            T t = objectMapper.readValue(node.toString(), clazz);
            return t;
        } catch (Exception e) {
            log.error("json parse error,json=" + json, e);
        }
        return null;
    }

    public static <T> T getNodeToObject(String json, String nodeName, Class<T> clazz) {
        try {
            String nodeJson = getNodeJson(json, nodeName);
            return objectMapper.readValue(nodeJson, clazz);
        } catch (Exception e) {
            log.error("json parse error,json=" + json, e);
        }
        return null;
    }


    public static <T> T toObjectAsType(String json, Class<T> clazz) {
        JavaType type = javaTypeMap.get(clazz.getName());
        if (Objects.isNull(type)) {
            type = objectMapper.getTypeFactory().constructFromCanonical(clazz.getCanonicalName());
            javaTypeMap.put(clazz.getName(), type);
        }
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            log.error("json parse error,json=" + json, e);
        }
        return null;
    }

    public static <T> List<T> toListAsType(String json, Class<T> clazz) {
        CollectionLikeType type = typeHashMap.get(clazz.getName());
        if (type == null) {
            type = objectMapper.getTypeFactory().constructCollectionLikeType(ArrayList.class, clazz);
            typeHashMap.put(clazz.getName(), type);
        }

        try {
            return (List<T>) objectMapper.readValue(json, type);
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public static <T> List<T> getNodeToList(JsonNode json, String nodeName, Class<T> clazz) {
        try {
            JsonNode jsonNode = json.findPath(nodeName);

            return toList(jsonNode, clazz);

        } catch (Exception e) {
            log.error("json parse error,json=" + json, e);
        }
        return Collections.emptyList();
    }

    private static boolean isEmpty(JsonNode node) {
        return node.isArray() && node.size() == 0;
    }

    public static <T> List<T> getNodeToList(JsonNode json, String nodeName, Class<T> clazz, Map<String, String> nameMap) {
        try {
            JsonNode jsonNode = json.findPath(nodeName);
            return toList(jsonNode, clazz, nameMap);
        } catch (Exception e) {
            log.error("json parse error,json=" + json, e);
        }
        return Collections.emptyList();
    }

    public static <T> List<T> getNodeToList(String json, String nodeName, Class<T> clazz) {
        try {
            return getNodeToList(toJsonNode(json), nodeName, clazz);
        } catch (Exception e) {
            log.error("json parse error,json=" + json, e);
        }
        return Collections.emptyList();
    }

    //    取JSON字段普通字符串内容，不要用这个方法，请用getNodeToObject方法
//    如果字段内容是Json，请用这个方法
    public static String getNodeJson(JsonNode json, String nodeName) {
        try {

            JsonNode node = json.findPath(nodeName);

            return node.toString();
        } catch (Exception e) {
            log.error("json parse error,json=" + json, e);
        }
        return "";
    }

    //    取JSON字段内容，不要用这个方法，请用getNodeToObject方法
    public static String getNodeJson(String json, String nodeName) {
        try {
            return getNodeJson(toJsonNode(json), nodeName);
        } catch (Exception e) {
            log.error("json parse error,json=" + json, e);
        }
        return "";
    }

    public static <T> List<T> toList(String json, Class<T> clazz) {
        try {
            JsonNode node = toJsonNode(json);
            return toList(node, clazz);
        } catch (Exception e) {
            log.error("json parse error,json=" + json, e);
        }
        return Collections.emptyList();
    }

    public static <T> List<T> toList(JsonNode json, Class<T> clazz, Map<String, String> nameMap) {
        try {
            Iterator<JsonNode> ite = json.elements();
            if (ite == null) {
                return Collections.emptyList();
            }

            List<T> list = new ArrayList<T>();
            while (ite.hasNext()) {
                JsonNode nextNode = ite.next();

//               php返回的空对象格式是[]，是数组格式的，没法转成JAVA的目标class格式,这里会报错，所以，我们对空对象处理就是忽略。
//               如果class本来就是数组，那么isEmpty中的size==0的判断不会使得正常的数组被过滤掉
                if (isEmpty(nextNode)) {
                    continue;
                }
                T t = null;
                if (nameMap == null) {
                    t = toObject(nextNode, clazz);
                } else {
                    log.error("这里有个坑爹的方法，千万不要调用");
                    t = toObject(nextNode, clazz, nameMap);
                }
                if (t != null) {
                    list.add(t);
                }
            }

            return list;
        } catch (Exception e) {
            log.error("json parse error,json=" + json, e);
        }
        return Collections.emptyList();
    }

    public static <T> List<T> toList(JsonNode json, Class<T> clazz) {
        return toList(json, clazz, null);
    }

    public static Map<String, JsonNode> toJsonMap(JsonNode json) {
        try {
            if (json == null) {
                return Collections.emptyMap();
            }

            Iterator<Map.Entry<String, JsonNode>> ite = json.fields();
            if (ite == null) {
                return Collections.emptyMap();
            } else {
                HashMap<String, JsonNode> datas = new HashMap<String, JsonNode>();
                while (ite.hasNext()) {
                    Map.Entry<String, JsonNode> jsonEntry = ite.next();
                    String nodeKey = jsonEntry.getKey();
                    JsonNode nextNode = jsonEntry.getValue();
                    datas.put(nodeKey, nextNode);
                }
                return datas;
            }
        } catch (Exception e) {
            log.error("json parse error,json=" + json, e);
            return Collections.emptyMap();
        }
    }

    public static <T> Map<String, T> getNodeToMap(JsonNode json, Class<T> clazz) {
        try {
            Iterator<Map.Entry<String, JsonNode>> ite = json.fields();
            if (ite == null) {
                return Collections.emptyMap();
            } else {
                HashMap<String, T> datas = new HashMap<String, T>();
                while (ite.hasNext()) {
                    Map.Entry<String, JsonNode> jsonEntry = ite.next();
                    String nodeKey = jsonEntry.getKey();
                    JsonNode nextNode = jsonEntry.getValue();
                    if (nextNode != null && nextNode.asText() != null && !nextNode.toString().equals("[]")) {
                        T t = JsonUtil.toObject(nextNode, clazz);
                        datas.put(nodeKey, t);
                    }
                }
                return datas;
            }
        } catch (Exception ex) {
            log.error("json parse error,json=" + json, ex);
            return Collections.emptyMap();
        }
    }

    public static class MappedPropertyNamingStrategy extends PropertyNamingStrategy {
        private Map<String, String> nameMap;

        public MappedPropertyNamingStrategy(Map<String, String> nameMap) {
            this.nameMap = nameMap;
        }

        @Override
        public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
            return mapName(defaultName);
        }

        @Override
        public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
            return mapName(defaultName);
        }

        @Override
        public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
            return mapName(defaultName);
        }

        private String mapName(String defaultName) {
            String name = nameMap.get(defaultName);
            if (name != null) {
                return name;
            } else {
                return defaultName;
            }
        }
    }
}

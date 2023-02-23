package com.xzkj.platform.khd.utils;

/**
 * @author WHY
 * @date 2022年03月06日 22:32
 */
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class ListUtils {

    /**
     *  lambda表达式对两个List进行循环,根据符合条件,进行相关的赋值操作并返回这个对象集合
     * @param sourceList   待设置源列表
     * @param srcEqualProp   源对象条件判断属性名
     * @param srcSetProp     源对象待设置属性名
     * @param targetList     资源提供者列表
     * @param tarEqualProp   对象条件判断参数名
     * @param tarGetProp     待获取对象属性名
     * @param <T>
     * @param <U>
     * @return
     */
    public static <T,U>List<T> setListByEqualObjProperty(List<T> sourceList, String srcEqualProp, String srcSetProp,
                                                         List<U> targetList, String tarEqualProp, String tarGetProp){
        List<T> resultList = Lists.newArrayList();
        resultList = sourceList.stream()
                .map(sur-> targetList.stream()
                        .filter(tar -> Objects.equals(getValueByPropName(sur, srcEqualProp), getValueByPropName(tar, tarEqualProp)))
                        .findFirst()
                        .map(tar -> {
                            setValueByPropName(sur, srcSetProp, getValueByPropName(tar, tarGetProp));
                            return sur;
                        } ).orElse(null))
                .collect(Collectors.toList());

        return  resultList;
    }

    /**
     *  通过遍历两个List中按id属性相等的归结到resultList中
     * @param oneList  源list 1
     * @param twoList  源list 2
     * @param equalKeyName 相等的map键值
     */
    public static List<Map<Object, Object>> compareListHitData(List<Map<Object, Object>> oneList, List<Map<Object, Object>> twoList, Object equalKeyName) {
        List<Map<Object, Object>> resultList = oneList.stream().map(map -> twoList.stream()
                .filter(m -> Objects.equals(m.get(equalKeyName),map.get(equalKeyName)))
                .findFirst().map(m -> {
                    map.putAll(m);
                    return map;
                }).orElse(null))
                .filter(Objects::nonNull).collect(Collectors.toList());
        return resultList;
    }



    // 通过属性获取传入对象的指定属性的值
    public static <T> T getValueByPropName(Object object, String propName) {
        T value = null;
        try {
            // 通过属性获取对象的属性
            Field field = object.getClass().getDeclaredField(propName);
            // 对象的属性的访问权限设置为可访问
            field.setAccessible(true);
            // 获取属性的对应的值
            value = (T)field.get(object);
        } catch (Exception e) {
            return null;
        }

        return value;
    }

    // 通过属性设置传入对象的指定属性的值
    public static <U> void setValueByPropName(Object object, String propName, U updateValue) {

        try {
            // 通过属性获取对象的属性
            Field field = object.getClass().getDeclaredField(propName);
            // 对象的属性的访问权限设置为可访问
            field.setAccessible(true);
            // 设置属性的对应的值
            field.set(object, updateValue);
        } catch (Exception e) {
            log.error("setValueByPropName.error {}", propName, e);
        }


    }
    @Data
    public static class Girl {
        private String id;

        private String name;
    }

    @Data
    public static class SchoolBoy {

        private String girlId;

        private String id;

        private String name;

        private Integer age;

        private String girlName;
    }
    public static void main(String[] args) {


        List<SchoolBoy> schoolBoys = new ArrayList<>(3);
        SchoolBoy boy1 = new SchoolBoy();
        boy1.setGirlId("1");
        boy1.setId("10");
        boy1.setName("小明");

        SchoolBoy boy2 = new SchoolBoy();
        boy2.setGirlId("2");
        boy2.setId("11");
        boy2.setName("小豪");

        SchoolBoy boy3 = new SchoolBoy();
        boy3.setGirlId("3");
        boy3.setId("12");
        boy3.setName("小白");
        schoolBoys.add(boy1);
        schoolBoys.add(boy2);
        schoolBoys.add(boy3);

        List<Girl> girls = new ArrayList<>(3);
        Girl girl1 = new Girl();
        girl1.setId("1");
        girl1.setName("小英");

        Girl girl2 = new Girl();
        girl2.setId("2");
        girl2.setName("小美");

        Girl girl3 = new Girl();
        girl3.setId("3");
        girl3.setName("小花");
        girls.add(girl1);
        girls.add(girl2);
        girls.add(girl3);

        List<SchoolBoy> list = ListUtils.setListByEqualObjProperty(schoolBoys,"girlId", "girlName",
                girls, "id", "name");

        System.out.println(list.toString());

        List<Map<Object, Object>> oneList = new ArrayList<>();
        Map<Object, Object> oneMap = new HashMap<>();
        oneMap.put("id", 111);
        oneMap.put("userName", "林飞");
        Map<Object, Object> twoMap = new HashMap<>();
        twoMap.put("id", 222);
        twoMap.put("userName", "Hejinrong");
        oneList.add(oneMap);
        oneList.add(twoMap);

        List<Map<Object, Object>> twoList = new ArrayList<>();
        Map<Object, Object> threeMap = new HashMap<>();
        threeMap.put("id", 111);
        threeMap.put("userName", "林飞");
        Map<Object, Object> fourMap = new HashMap<>();
        fourMap.put("id", 333);
        fourMap.put("userName", "Hejinrong");
        twoList.add(threeMap);
        twoList.add(fourMap);

        List<Map<Object, Object>> resultList = compareListHitData(oneList, twoList, "id");
        System.out.println(resultList);


    }

}


package com;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.haihun.comm.security.MD5;
import org.junit.Test;

import javax.swing.text.DateFormatter;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author kaiser·von·d
 * @version 2018/6/20
 */
public class LambdaTest {

    @Test
    public void demo1() {
//        BinaryOperator<Long> add

        // 此种表达式的阅读方式为： add 不是 x+y的和，而是指向一个 函数 add = function(x,y) {return x+y;}
        BinaryOperator<Long> add = (Long x, Long y) -> x + y;

        Long result = add.apply(1L, 2L);
        System.out.println(result);

    }

    @Test
    public void runnable() {
        String name = getName();
//        name = "b";
        Runnable runnable = () -> {
            System.out.println(name);
        };


        Consumer consumer = x -> System.out.println(x);
        consumer.accept("x");
    }

    @Test
    public void predicate() {
        Predicate<Integer> result = x -> x == 1;
        boolean flag = result.test(1);
        System.out.println(flag);
    }

    @Test
    public void supplier() {
        Supplier<Integer> tSupplier = () -> 1;
        Integer result = tSupplier.get();
        System.out.println(result);

        // 类型推断
        useHashMap(new HashMap<>());
    }

    @Test
    public void BinaryOperator() {
        BinaryOperator<String> result = (x, y) -> x + y;
        System.out.println(result.apply("a", "b"));
    }

    public String getName() {
        return "name";
    }

    public void useHashMap(Map<String, String> values) {

    }

    public void threadLocal() {
        ThreadLocal<DateFormatter> variable = ThreadLocal.withInitial(() -> new DateFormatter(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));

        Predicate<Integer> b = x -> x > 1;
        boolean test = b.test(2);
        IntPredicate i = x -> x == 1;
        boolean test1 = i.test(2);

        UnaryOperator<Integer> k = x -> x * x;
        k.apply(2);

    }

    @Test
    public void fun() {
//        Function<Integer, String> fun = x -> String.valueOf(x) + "" + 2;
//        String result = fun.apply(1);
//        System.out.println(result);
        ArrayList<Map<String, String>> list = getMaps();
        // 惰性求值
        Stream<Map<String, String>> stream = list.stream().filter(x -> x.get("name").equals("伦敦"));


        List<String> name = stream.map(x -> x.get("name")).collect(Collectors.toList());
        System.out.println("values : " + name);

        // 及早求值
        long count = stream.count();
        System.out.println("惰性求值：" + count);

        long count2 = list.stream().filter(x -> {
            System.out.println(x.get("name"));
            return x.get("name").equals("华盛顿");
        }).count();

        System.out.println(count2);


    }

    @Test
    public void filter() {
        Stream<String> stream = Stream.of("a", "b", "c");
        List<String> list = stream.map(x -> x.toUpperCase()).collect(Collectors.toList());
        System.out.println(list);
        ArrayList<Map<String, String>> maps = getMaps();
        List<String> names = maps.stream().map(m -> m.get("name")).filter(x -> x.equalsIgnoreCase("伦敦")).collect(Collectors.toList());
        System.out.println(names);

//        Object o = maps.stream().map(m -> m.get("name")).findFirst().get();
//        System.out.println(o);
//        Set<String> name = maps.stream().map(m -> m.get("name")).collect(Colltors.toSet());

//        System.out.println(name);
    }

    @Test
    public void minOrMax() {
        ArrayList<Map<String, Integer>> maps = ageMap();
        System.out.println();
        Map<String, Integer> map = maps.stream().max(Comparator.comparingInt(m -> m.get("age"))).get();
        System.out.println(map);
    }

    @Test
    public void flatMap() {
        ArrayList<Map<String, String>> maps = getMaps();
        List<String> list = maps.stream().flatMap(m -> m.entrySet().stream().map(e -> e.setValue("3333"))).collect(Collectors.toList());
        System.out.println(maps);
        System.out.println(list);
//        List<Integer> together = Stream.of(Arrays.asList(1, 2), Arrays.asList(3, 4)).flatMap(numbers -> numbers.stream()).collect(toList());

    }

    @Test
    public void reduce() {
        File file = new File("E:\\software");
    }

    private ArrayList<Map<String, String>> getMaps() {
        HashMap<String, String> map;
        ArrayList<Map<String, String>> list = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            map = new HashMap<>();
            if (i <= 5) {
                map.put("name", "伦敦");
            } else if (i > 5 && i <= 8) {
                map.put("name", "华盛顿");
            } else {
                map.put("name", "北京");
            }
            list.add(map);
        }
        return list;
    }

    private ArrayList<Map<String, Integer>> ageMap() {
        HashMap<String, Integer> map;
        ArrayList<Map<String, Integer>> list = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            map = new HashMap<>();
            map.put("age", i + 1);
            list.add(map);
        }
        return list;
    }


    public static void main(String[] args) {
        String param="extra=9201806271510128121797&orderId=210820180627151023&paymentTime=2018-06-27 15:10:34&paymentType=Wechatpay&status=已付款&title=60元宝&totalFee=6.0&key=bddfd155d74e430995cf019c38a58b0c";
        String sign = MD5.getMD5(param);
        System.out.println(sign);

        Double cny = 600.0;
        String result = String.format("%.2f",cny);
        System.out.println(result);

    }
}

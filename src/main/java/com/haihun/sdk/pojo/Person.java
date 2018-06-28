package com.haihun.sdk.pojo;

import lombok.Data;
import lombok.ToString;

/**
 * @author kaiser·von·d
 * @version 2018/6/13
 */
@Data
@ToString(of = {"age","name","stature","weight"})
public class Person {

    private final int age;
    private final String name;
    private final int stature;

    private final double weight;

    public static class Builder {
        private final String name;
        private int stature;
        private double weight;
        private int age;

        public Builder(String name) {
            this.name = name;
        }

        public Builder() {
            this.name = "";
            this.age = 1;
            this.weight = 3.0;
            this.stature = 35;
        }

        public Builder stature(int stature) {
            this.stature = stature;
            return this;
        }

        public Builder weight(double weight) {
            this.weight = weight;
            return this;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Person build() {
            return new Person(this);
        }
    }

    private Person(Builder builder) {
        this.age = builder.age;
        this.stature = builder.stature;
        this.name = builder.name;
        this.weight = builder.weight;
    }

    public Person() {
        this(new Builder());
    }
}




package net.wouterb.blockblock.config;

import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.Arrays;

public class ConfigDefaultValues {
    public static JsonArray getDefaultLockedBlocks(){
        JsonArray arr = new JsonArray();
        arr.add("minecraft:dirt");
        arr.add("minecraft:spruce_log");

        return arr;
    }
}

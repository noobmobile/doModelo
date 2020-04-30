package com.dont.modelo.database.adapters;


import com.dont.modelo.utils.Utils;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class ItemStackAdapter extends TypeAdapter<ItemStack> {
    @Override//
    public void write(JsonWriter out, ItemStack item) throws IOException {
        out.beginObject();
        out.name("item").value(Utils.toBase64(item));
        out.endObject();
    }

    @Override
    public ItemStack read(JsonReader in) throws IOException {
        in.beginObject();

        ItemStack item = null;
        while (in.hasNext()) {
            if (in.nextName().equalsIgnoreCase("item")) {
                item = Utils.fromBase64(in.nextString());
            }
        }

        in.endObject();
        return item;
    }

}

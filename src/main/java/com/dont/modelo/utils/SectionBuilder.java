package com.dont.modelo.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class SectionBuilder<T> {

    private static final Map<Class<?>, Adapter<?>> CLASS_ADAPTERS = new HashMap<>();

    static {
        CLASS_ADAPTERS.put(ItemStack.class, new ItemAdapter());
        CLASS_ADAPTERS.put(String.class, new StringAdapter());
        CLASS_ADAPTERS.put(Material.class, new MaterialAdapter());
        CLASS_ADAPTERS.put(StringList.class, new ListAdapter<>(new StringAdapter()));
        CLASS_ADAPTERS.put(ItemList.class, new ListAdapter<>(new ItemAdapter()));
    }

    private final ConfigurationSection mainSection;
    private final Class<T> clazz;
    private final Constructor<T> constructor;
    private final Map<String, Class<?>> parameters;
    private final Map<String, Adapter<?>> parametersAdapters;

    public static <E> SectionBuilder<E> of(Class<E> clazz, ConfigurationSection section) {
        return new SectionBuilder<E>(clazz, section);
    }

    private SectionBuilder(Class<T> clazz, ConfigurationSection mainSection) {
        this.mainSection = mainSection;
        this.clazz = clazz;
        this.constructor = (Constructor<T>) clazz.getConstructors()[0];
        this.parameters = new LinkedHashMap<>();
        this.parametersAdapters = new HashMap<>();
    }


    public SectionBuilder<T> parameter(String key, Class<?> valueClass) {
        this.parameters.put(key, valueClass);
        return this;
    }

    public SectionBuilder<T> parameter(String key, Class<?> valueClass, Adapter<?> adapter) {
        this.parameters.put(key, valueClass);
        this.parametersAdapters.put(key, adapter);
        return this;
    }

    public SectionBuilder<T> adapter(Class<?> clazz, Adapter<?> adapter) {
        CLASS_ADAPTERS.put(clazz, adapter);
        return this;
    }


    public List<T> build() {
        if (constructor.getParameterCount() - 1 != parameters.size()) { // primeiro argumento é sempre a key, e não precisa ser passado pelo método parameter(String, Class)
            throw new IllegalArgumentException("Constructor has " + (constructor.getParameterCount() - 1) + " parameters, but it was passed " + parameters.size() + " parameters.");
        }
        List<T> toReturn = new ArrayList<>();
        for (String key : mainSection.getKeys(false)) {
            try {
                ConfigurationSection section = mainSection.getConfigurationSection(key);
                List<Object> constructorParameters = new ArrayList<>();
                constructorParameters.add(key);
                for (Map.Entry<String, Class<?>> entry : parameters.entrySet()) {
                    String parameter = entry.getKey();
                    Class<?> parameterClass = entry.getValue();
                    Object object = section.get(parameter);
                    Adapter<?> adapter = parametersAdapters.getOrDefault(parameter, CLASS_ADAPTERS.get(parameterClass));
                    constructorParameters.add(adapter == null ? object : adapter.supply(object));
                }
                T instance = constructor.newInstance(constructorParameters.toArray());
                toReturn.add(instance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return toReturn;
    }

    public static interface Adapter<A> {
        A supply(Object object);
    }

    public static class ItemAdapter implements Adapter<ItemStack> {

        private final static MaterialAdapter MATERIAL_ADAPTER = new MaterialAdapter();
        private final static StringAdapter STRING_ADAPTER = new StringAdapter();

        /* O único atributo necessário é o material
            Material: STONE
            #Texture: http://textures.minecraft.net/texture/fa8887814578ce7c540d7ab8cc6b2a2e22a7492cc86c65a7e839c887b2ed62 # caso tenha essa propriedade, o item automaticamente ira ser uma skull com a textura setada (pode ser um nick também)
            Data: 1
            Quantidade: 16
            Nome: "&5Nome Legal"
            Lore:
             - "&6Lore legal"
             Enchants:
              - "DAMAGE_ALL: 10"
             Glow: false
         */
        @Override
        public ItemStack supply(Object object) {
            ConfigurationSection section = (ConfigurationSection) object;

            String url = !section.isSet("Texture") ? null : section.getString("Texture");
            Material material = MATERIAL_ADAPTER.supply(section.get("Material"));
            int data = !section.isSet("Data") ? 0 : section.getInt("Data");
            int amount = !section.isSet("Quantidade") ? 1 : section.getInt("Quantidade");
            String name = !section.isSet("Nome") ? null : STRING_ADAPTER.supply(section.getString("Nome"));
            List<String> lore = !section.isSet("Lore") ? null : section.getStringList("Lore").stream().map(STRING_ADAPTER::supply).collect(Collectors.toList());
            Map<Enchantment, Integer> enchants = !section.isSet("Enchants") ? null : section.getStringList("Enchants").stream().map(string -> string.split(":")).collect(Collectors.toMap(array -> Enchantment.getByName(array[0]), array -> Integer.parseInt(array[1].trim())));
            boolean glow = section.isSet("Glow") && section.getBoolean("Glow");

            ItemStack itemStack = new ItemStack(material, amount, (byte) data);
            ItemMeta itemMeta = itemStack.getItemMeta();

            if (url != null) itemStack = getCustomHead(url);
            if (name != null) itemMeta.setDisplayName(name);
            if (lore != null) itemMeta.setLore(lore);
            if (glow) {
                itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            if (enchants != null)
                enchants.forEach((enchantment, integer) -> itemMeta.addEnchant(enchantment, integer, true));

            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }

        private ItemStack getCustomHead(String url) {
            ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            if (url.length() <= 16) {
                skullMeta.setOwner(url);
                itemStack.setItemMeta(skullMeta);
                return itemStack;
            }
            if (!url.startsWith("http://textures.minecraft.net/texture/")) {
                url = "http://textures.minecraft.net/texture/" + url;
            }
            try {
                GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(url.getBytes()), null);
                profile.getProperties().put("textures", new Property("textures", new String(Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes()))));
                Field profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skullMeta, profile);
                itemStack.setItemMeta(skullMeta);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return itemStack;
        }

    }

    private static class StringAdapter implements Adapter<String> {

        @Override
        public String supply(Object object) {
            return ChatColor.translateAlternateColorCodes('&', (String) object);
        }
    }

    private static class MaterialAdapter implements Adapter<Material> {

        @Override
        public Material supply(Object object) {
            String value = object.toString();
            return isNumber(value) ? Material.getMaterial(Integer.parseInt(value)) : Material.valueOf(value.toUpperCase());
        }

        private boolean isNumber(String string) {
            try {
                int i = Integer.parseInt(string);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

    }

    public static class ListAdapter<A> implements Adapter<List<A>> {

        private final Adapter<A> adapter;

        public ListAdapter(Adapter<A> adapter) {
            this.adapter = adapter;
        }

        @Override
        public List<A> supply(Object object) {
            if (object instanceof ConfigurationSection) {
                ConfigurationSection section = (ConfigurationSection) object;
                return section.getKeys(false).stream().map(section::getConfigurationSection).map(adapter::supply).collect(Collectors.toList());
            } else {
                List<Object> list = (List<Object>) object;
                return list.stream().map(adapter::supply).collect(Collectors.toList());
            }
        }
    }

    public static class ItemList {
    }

    public static class StringList {
    }
}

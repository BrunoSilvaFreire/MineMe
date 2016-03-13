/* 
 * Copyright (C) 2016 Selma
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.ddevil.core.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.exception.ItemConversionException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Selma
 */
public class ItemUtils {

    private static boolean glowRegistrado = false;

    //geral
    public static final ItemStack NA = ItemUtils.createItem(Material.BARRIER, "§4§l§o§nN/A");
    public static final ItemStack Gray = ItemUtils.createItem(Material.IRON_FENCE, "§r");

    public static String toString(ItemStack i) {
        return i.getType() + ":" + i.getData().getData();
    }

    public static ItemStack addToLore(ItemStack i, String... strings) {
        List<String> lore = getLore(i);
        for (String toAdd : strings) {
            lore.add(toAdd);
        }
        ItemMeta itemMeta = i.getItemMeta();
        itemMeta.setLore(lore);
        i.setItemMeta(itemMeta);
        return i;
    }

    public static ItemStack createItem(Material material, String nome) {
        ItemStack is = new ItemStack(material);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(nome);
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack createItem(ItemStack material, String nome) {
        ItemStack is = new ItemStack(material);
        ItemMeta im = is.getItemMeta();
        if (nome != null) {
            im.setDisplayName(nome);
        }
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack createItem(ItemStack material, String nome, String[] desc) {
        ItemStack is = ItemUtils.createItem(material, nome);
        ItemMeta im = is.getItemMeta();
        im.setLore(Arrays.asList(desc));
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack createItem(Material material, String nome, String[] desc) {
        ItemStack is = ItemUtils.createItem(material, nome);
        ItemMeta im = is.getItemMeta();
        im.setLore(Arrays.asList(desc));
        is.setItemMeta(im);
        return is;
    }

    public static void addGlow(ItemStack i) {
        if (!glowRegistrado) {
            registerGlow();
        }
        Glow glow = new Glow(70);
        ItemMeta im = i.getItemMeta();
        im.addEnchant(glow, 1, true);
        i.setItemMeta(im);
    }

    public static boolean checkDisplayName(ItemStack i) {
        if (checkItemMeta(i)) {
            ItemMeta im = i.getItemMeta();
            if (im.getDisplayName() != null) {
                return true;
            }
        }

        return false;
    }

    public static boolean checkItemMeta(ItemStack i) {
        ItemMeta im = i.getItemMeta();
        return im != null;
    }

    public static boolean checkLore(ItemStack i) {
        if (checkItemMeta(i)) {
            ItemMeta im = i.getItemMeta();
            if (im.getLore() != null) {
                return true;
            }
        }
        return false;
    }

    private static void registerGlow() {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {

        }
        try {
            Glow glow = new Glow(70);
            Enchantment.registerEnchantment(glow);
            glowRegistrado = true;
        } catch (IllegalArgumentException e) {
        }
    }

    public static ItemStack criarBotaoSim(String itemVendido, String finalCusto) {
        ItemStack itemCompra = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta IMCompra = itemCompra.getItemMeta();
        IMCompra.setDisplayName("§a§lComprar este " + itemVendido);
        IMCompra.setLore(Arrays.asList(new String[]{"§7Compre este " + itemVendido + " por " + finalCusto}));
        itemCompra.setItemMeta(IMCompra);
        return itemCompra;
    }

    public static boolean isValido(ItemStack item) {
        return isValido(item, true);
    }

    public static boolean isValido(ItemStack item, boolean comNome) {
        if (item != null) {
            if (item.getItemMeta() != null) {
                if (comNome) {
                    if (item.getItemMeta().getDisplayName() != null) {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public static List<String> getLore(ItemStack i) {
        if (checkLore(i)) {
            return i.getItemMeta().getLore();
        } else {
            return new ArrayList();
        }
    }

    public static ItemStack convertFromInput(String input, String name) throws ItemConversionException {
        return createItem(convertFromInput(input), name);
    }

    public static ItemStack convertFromInput(String input) throws ItemConversionException {
        try {
            String[] materialanddata = input.split(":");
            Material mat = Material.valueOf(materialanddata[0]);
            Byte b;
            if (materialanddata.length > 1) {
                try {
                    b = Byte.valueOf(materialanddata[1]);
                } catch (NumberFormatException exception) {
                    MineMe.getInstance().debug(materialanddata[1] + " in " + input + "isn't a number! Setting byte to 0");
                    b = 0;
                }
            } else {
                b = 0;
            }
            return new ItemStack(mat, 1, (short) 0, b);
        } catch (Exception e) {
            throw new ItemConversionException(input);
        }
    }

    public static boolean equals(ItemStack a, ItemStack b) {
        if (!checkDisplayName(a) || !checkDisplayName(b)) {
            return false;
        } else {
            return a.getItemMeta().getDisplayName().equalsIgnoreCase(b.getItemMeta().getDisplayName());
        }
    }

    public static boolean equalMaterial(ItemStack a, ItemStack b) {
        if (a.getType() == b.getType()) {
            if (a.getData().getData() == b.getData().getData()) {
                return true;
            }
        }
        return false;
    }
}

class Glow extends Enchantment {

    public Glow(int id) {
        super(id);
    }

    @Override
    public String getName() {
        return "Glow";
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ALL;
    }

    @Override
    public boolean conflictsWith(Enchantment e) {
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack is) {
        return true;
    }
}

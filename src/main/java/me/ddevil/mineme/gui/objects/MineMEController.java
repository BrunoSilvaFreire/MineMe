 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ddevil.mineme.gui.objects;

import java.util.ArrayList;
import java.util.List;
import me.ddevil.core.events.inventory.InventoryObjectClickEvent;
import me.ddevil.core.utils.inventory.objects.BasicClickableInventoryObject;
import me.ddevil.core.utils.inventory.objects.BasicInventoryContainer;
import me.ddevil.core.utils.inventory.objects.interfaces.InventoryObjectClickListener;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.gui.GUIResourcesUtils;
import me.ddevil.mineme.gui.menus.MineMenu;
import me.ddevil.mineme.mines.Mine;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 *
 * @author BRUNO II
 */
public class MineMEController extends BasicInventoryContainer {

    protected final Mine mine;
    protected final BasicClickableInventoryObject toogle;
    protected boolean showingMaterial = true;

    protected List<MineMaterialDisplay> materialsDisplays = new ArrayList();
    protected List<MineEffectDisplay> effectsDisplays = new ArrayList();

    public MineMEController(MineMenu menu, Mine mine) {
        super(menu, 36, 44);
        this.toogle = new BasicClickableInventoryObject(GUIResourcesUtils.TOOGLE_MATERIALS_EFFECTS_SELECTION, new InventoryObjectClickListener() {

            @Override
            public void onInteract(InventoryObjectClickEvent e) {
                toogleDisplay();
            }
        }, menu);
        this.mine = mine;
        for (int i = 0; i < 8; i++) {
            MineMaterialDisplay mmd = new MineMaterialDisplay(mine, menu);
            materialsDisplays.add(mmd);
            MineEffectDisplay med = new MineEffectDisplay(mine, menu);
            effectsDisplays.add(med);
        }
    }

    public Mine getMine() {
        return mine;
    }

    public void toogleDisplay() {
        showingMaterial = !showingMaterial;
        update();
    }

    public boolean isShowingMaterial() {
        return showingMaterial;
    }

    @Override
    public void update() {
        MineMe.instance.broadcastDebug("Updating controller to showingMaterial = " + showingMaterial);
        clear();
        if (showingMaterial) {
            List<ItemStack> materials = mine.getMaterials();
            int i = 36;
            for (MineMaterialDisplay m : materialsDisplays) {
                int index = materialsDisplays.indexOf(m);
                if (materials.size() > index) {
                    m.setMaterial(materials.get(index));
                } else {
                    m.setMaterial(null);
                }
                setObject(i, m);
                i++;
            }
        } else {
            List<PotionEffect> effects = mine.getEffects();
            int i = 36;
            for (MineEffectDisplay m : effectsDisplays) {
                int index = effectsDisplays.indexOf(m);
                if (effects.size() > index) {
                    m.setPotionEffect(effects.get(index));
                } else {
                    m.setPotionEffect(null);
                }
                setObject(i, m);
                i++;
            }
        }
        setObject(44, toogle);
        super.update();
    }

}

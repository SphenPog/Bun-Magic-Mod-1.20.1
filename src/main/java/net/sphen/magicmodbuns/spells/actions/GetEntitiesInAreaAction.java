package net.sphen.magicmodbuns.spells.actions;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class GetEntitiesInAreaAction {

    /**
     * @param level The level to search in.
     * @param bounds The AABB to search within.
     * @param entityClass The class of the entity to look for (e.g., ItemEntity).
     * @return A list of all matching entities found in the area.
     */
    public <T extends Entity> List<T> execute(Level level, AABB bounds, Class<T> entityClass) {
        return level.getEntitiesOfClass(entityClass, bounds);
    }
}

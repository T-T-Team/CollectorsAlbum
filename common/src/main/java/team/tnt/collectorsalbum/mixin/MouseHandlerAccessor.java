package team.tnt.collectorsalbum.mixin;

import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MouseHandler.class)
public interface MouseHandlerAccessor {

    @Accessor
    double getXpos();

    @Accessor
    void setXpos(double xpos);

    @Accessor
    double getYpos();

    @Accessor
    void setYpos(double ypos);
}

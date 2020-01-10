package com.rumaruka.powercraft.api.gres;

import com.rumaruka.powercraft.api.PCVec2I;
import com.rumaruka.powercraft.api.gres.events.IGresEventListener;
import com.rumaruka.powercraft.api.gres.events.PCGresEvent;
import com.rumaruka.powercraft.api.gres.events.PCGresMouseButtonEvent;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Array;

@SideOnly(Side.CLIENT)
public class PCGresDisplayObject implements IGresEventListener {
    private final Object display;

    public PCGresDisplayObject(Object display){
        Object d = display;
        if(d.getClass().isArray()){
            d = new ObjectChange(d);
        }
        if(d instanceof Item){
            d = new ItemStack((Item)d);
        }else if(d instanceof Block){
            d = new ItemStack((Block)d);
        }
        if(!( d instanceof ItemStack || d instanceof PCGresTexture || d instanceof ObjectChange))
            throw new IllegalArgumentException("Unknow display object:"+d);
        this.display = d;
    }

    public PCGresDisplayObject(Object...display){
        this(new ObjectChange(display));
    }

    public Object getDisplayObject(){
        if(this.display instanceof ObjectChange){
            return ((ObjectChange)this.display).display;
        }
        return this.display;
    }

    public Object getActiveDisplayObject(){
        if(this.display instanceof ObjectChange){
            return ((ObjectChange)this.display).getObject();
        }
        return this.display;
    }

    public int getActiveDisplayObjectIndex(){
        if(this.display instanceof ObjectChange){
            return ((ObjectChange)this.display).pos;
        }
        return 0;
    }

    public void setActiveDisplayObjectIndex(int index){
        if(this.display instanceof ObjectChange){
            ((ObjectChange)this.display).pos = index;
        }
    }

    public PCVec2I getMinSize() {
        Object d = this.display;
        if(this.display instanceof ObjectChange){
            d = ((ObjectChange)this.display).getObject();
        }
       if(d instanceof ItemStack){
            return new PCVec2I(16, 16);
        }else if(d instanceof PCGresTexture){
            return ((PCGresTexture)d).getMinSize();
        }
        return new PCVec2I(-1, -1);
    }

    public PCVec2I getPrefSize() {
        Object d = this.display;
        if(this.display instanceof ObjectChange){
            d = ((ObjectChange)this.display).getObject();
        }
        if(d instanceof ItemStack){
            return new PCVec2I(16, 16);
        }else if(d instanceof PCGresTexture){
            return ((PCGresTexture)d).getDefaultSize();
        }
        return new PCVec2I(-1, -1);
    }

    public void draw(int x, int y, int width, int height) {
        Object d = this.display;
        if(this.display instanceof ObjectChange){
            d = ((ObjectChange)this.display).getObject();
        }
       if(d instanceof ItemStack){
            int nx = x + width/2-8;
            int ny = y + height/2-8;
            PCGresRenderer.drawItemStackAllreadyLighting(nx, ny, (ItemStack)d, null);
        }else if(d instanceof PCGresTexture){
            ((PCGresTexture)d).draw(x, y, width, height, 0);
        }
    }

    @Override
    public void onEvent(PCGresEvent event) {
        if(this.display instanceof ObjectChange){
            ((ObjectChange) this.display).onEvent(event);
        }
    }

    private static class ObjectChange implements IGresEventListener{

        Object[] display;
        int pos;

        ObjectChange(Object[] display){
            this.display = new Object[display.length];
            for(int i=0; i<display.length; i++){
                Object o = display[i];
                if(o instanceof Item){
                    o = new ItemStack((Item)o);
                }else if(o instanceof Block){
                    o = new ItemStack((Block)o);
                }
                if(!(o instanceof ItemStack || o instanceof PCGresTexture))
                    throw new IllegalArgumentException("Unknow display object:"+o);
                this.display[i] = o;
            }
        }

        ObjectChange(Object display){
            this.display = new Object[Array.getLength(display)];
            for(int i=0; i<this.display.length; i++){
                Object o = Array.get(display, i);
                if(o instanceof Item){
                    o = new ItemStack((Item)o);
                }else if(o instanceof Block){
                    o = new ItemStack((Block)o);
                }
                if(!( o instanceof ItemStack || o instanceof PCGresTexture))
                    throw new IllegalArgumentException("Unknow display object:"+o);
                this.display[i] = o;
            }
        }

        @Override
        public void onEvent(PCGresEvent event) {
            if(event instanceof PCGresMouseButtonEvent){
                if(((PCGresMouseButtonEvent)event).getEvent()==PCGresMouseButtonEvent.Event.UP && event.getComponent().mouseDown){
                    this.pos++;
                    this.pos %= this.display.length;
                }
            }
        }

        Object getObject(){
            return this.display[this.pos];
        }

    }

}

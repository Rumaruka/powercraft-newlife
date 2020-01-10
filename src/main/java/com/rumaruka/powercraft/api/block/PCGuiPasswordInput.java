package com.rumaruka.powercraft.api.block;

import com.rumaruka.powercraft.api.gres.*;
import com.rumaruka.powercraft.api.gres.events.IGresEventListener;
import com.rumaruka.powercraft.api.gres.events.PCGresEvent;
import com.rumaruka.powercraft.api.gres.events.PCGresKeyEvent;


import com.rumaruka.powercraft.api.gres.events.PCGresMouseButtonEvent;
import com.rumaruka.powercraft.api.gres.layot.PCGresLayoutVertical;
import com.rumaruka.powercraft.api.network.PCPacketHandler;
import com.rumaruka.powercraft.api.network.packet.PCPacketPasswordReply;
import com.rumaruka.powercraft.api.tile.PCTileEntityAPI;
import org.lwjgl.input.Keyboard;

public class PCGuiPasswordInput implements IGresGui, IGresEventListener {

    private PCTileEntityAPI tileEntity;
    private PCGresTextEdit password;
    private PCGresLabel status;
    private PCGresButton ok;
    private PCGresButton cancel;
    private PCGresGuiHandler gui;

    public PCGuiPasswordInput(PCTileEntityAPI tileEntity) {
        this.tileEntity = tileEntity;
    }
    @Override
    public void initGui(PCGresGuiHandler gui) {
        this.gui = gui;
        PCGresWindow window = new PCGresWindow("Password Input");
        window.setLayout(new PCGresLayoutVertical());
        this.password = new PCGresTextEdit("", 20, PCGresTextEdit.PCGresInputType.PASSWORD);
        window.add(this.password);
        this.password.addEventListener(this);
        this.status = new PCGresLabel("Type Password");
        window.add(this.status);
        this.ok = new PCGresButton("OK");
        window.add(this.ok);
        this.ok.addEventListener(this);
        this.cancel = new PCGresButton("Cancel");
        window.add(this.cancel);
        this.cancel.addEventListener(this);
        gui.add(window);
        gui.addEventListener(this);
    }

    @Override
    public void onEvent(PCGresEvent event) {

        PCGresComponent component = event.getComponent();
        if(event instanceof PCGresMouseButtonEvent){
            PCGresMouseButtonEvent bEvent = (PCGresMouseButtonEvent)event;
            if(bEvent.getEvent()== PCGresMouseButtonEvent.Event.CLICK){
                if(component==ok){
                    send();
                }else if(component==cancel){
                    component.getGuiHandler().close();
                }
            }
        }else if(event instanceof PCGresKeyEvent){
            PCGresKeyEvent kEvent = (PCGresKeyEvent)event;
            if(kEvent.getKeyCode()== Keyboard.KEY_RETURN){
                send();
            }else if(kEvent.getKeyCode()==Keyboard.KEY_ESCAPE){
                component.getGuiHandler().close();
            }
        }
    }

    private void send(){
        this.gui.addWorking();
        this.status.setText("Sending ...");
        this.ok.setEnabled(false);
        PCPacketHandler.sendToServer(new PCPacketPasswordReply(this.tileEntity, this.password.getText()));
    }

    protected void passwordFailed() {
        status.setText("Failed, wrong password");
        ok.setEnabled(true);
    }
    public void wrongPassword(PCTileEntityAPI te) {
        if(this.tileEntity==te){
            this.gui.removeWorking();
            this.status.setText("Failed, wrong password");
            this.ok.setEnabled(true);
        }
    }
}

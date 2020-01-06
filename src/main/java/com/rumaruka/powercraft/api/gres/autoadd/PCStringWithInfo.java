package com.rumaruka.powercraft.api.gres.autoadd;

public class PCStringWithInfo implements Comparable<PCStringWithInfo>{
    private String string;
    private String tooltip;
    private String[] info;

    public PCStringWithInfo(String string, String tooltip) {
        this.string = string;
        this.tooltip = tooltip;
    }

    public PCStringWithInfo(String string, String tooltip, String[] info) {
        this.string = string;
        this.tooltip = tooltip;
        this.info = info;
    }

    @Override
    public int compareTo(PCStringWithInfo o) {
        return this.string.compareTo(o.string);
    }

    public boolean startsWith(String prefix) {
        return this.string.startsWith(prefix);
    }

    public String getString() {
        return this.string;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public String[] getInfo() {
        return this.info;
    }
}

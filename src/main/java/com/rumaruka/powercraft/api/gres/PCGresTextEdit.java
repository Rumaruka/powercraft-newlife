package com.rumaruka.powercraft.api.gres;

import com.rumaruka.powercraft.api.PCRect;
import com.rumaruka.powercraft.api.PCVec2;
import com.rumaruka.powercraft.api.PCVec2I;
import com.rumaruka.powercraft.api.gres.history.PCGresHistory;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;

@SideOnly(Side.CLIENT)
public class PCGresTextEdit extends PCGresComponent{
    protected static final String textureName = "TextEdit";
    protected static final String textureName2 = "TextEditSelect";

    public enum PCGresInputType {
        /** accept all characters */
        TEXT,
        /** accept signed number */
        INT,
        /** accept unsigned number */
        UNSIGNED_INT,
        /** accept signed number with dot */
        SIGNED_FLOAT,
        /** accept unsigned number with dot */
        UNSIGNED_FLOAT,
        /** Disable user input */
        NONE,
        /** [a-zA-Z_][a-zA-Z0-9_] */
        IDENTIFIER,
        /** accept all characters but writes only **/
        PASSWORD;
    }

    private int maxChars;
    private int scroll;
    private int mouseSelectStart = 0;
    private int mouseSelectEnd = 0;
    private PCGresInputType type;
    private int cursorCounter;

    public PCGresTextEdit(String text, int chars) {
        this(text, chars, PCGresInputType.TEXT);
    }
    public PCGresTextEdit(String text, int chars, PCGresInputType type) {
        setText(text);
        this.type = type;
        this.maxChars = chars;
        this.fontColors[0] = 0xffffffff;
        this.fontColors[1] = 0xffffffff;
        this.fontColors[2] = 0xffffffff;
        this.fontColors[3] = 0xffffffff;
    }

    @Override
    protected PCVec2I calculateMinSize() {
        return new PCVec2I(this.maxChars * 8 + 4, fontRenderer.getStringSize(" ").y + 12);
    }

    @Override
    protected PCVec2I calculateMaxSize() {
        return new PCVec2I(-1, fontRenderer.getStringSize(" ").y + 12);
    }

    @Override
    protected PCVec2I calculatePrefSize() {
        return new PCVec2I(this.maxChars * 8 + 4, fontRenderer.getStringSize(" ").y + 12);
    }

    @Override
    protected void paint(PCRect scissor, double scale, int displayHeight, float timeStamp, float zoom) {

        drawTexture(textureName, 0, 0, this.rect.width, this.rect.height);
        PCVec2 offset = getRealLocation();
        setDrawRect(scissor,
                new PCRect(2+offset.x, 6+offset.y, this.rect.width - 4, this.rect.height - 12), scale,
                displayHeight, zoom);

        String t = this.text;
        if(this.type==PCGresInputType.PASSWORD){
            char c[] = new char[t.length()];
            Arrays.fill(c, '*');
            t = new String(c);
        }

        if (this.focus && this.mouseSelectStart != this.mouseSelectEnd) {
            int s = this.mouseSelectStart;
            int e = this.mouseSelectEnd;
            if (s > e) {
                e = this.mouseSelectStart;
                s = this.mouseSelectEnd;
            }
            drawTexture(textureName2,
                    fontRenderer.getStringSize(t.substring(0, s)).x - this.scroll
                            + 2, 1,
                    fontRenderer.getStringSize(t.substring(s, e)).x,
                    this.rect.height+1);
        }

        drawString(t, 2 - this.scroll, 6, false);

        if (this.focus && this.cursorCounter / 6 % 2 == 0) {
            PCVec2I max = fontRenderer.getStringSize(t.isEmpty()?" ":t);
            PCVec2I size = fontRenderer.getStringSize(t.substring(0, this.mouseSelectEnd));
            PCGresRenderer.drawVerticalLine(size.x + 2, 6,
                    6 + max.y, this.fontColors[0]|0xff000000);
        }

        if(scissor==null){
            setDrawRect(scissor, new PCRect(-1, -1, -1, -1), scale, displayHeight, zoom);
        }else{
            setDrawRect(scissor, scissor, scale, displayHeight, zoom);
        }
    }

    private int getMousePositionInString(int x) {
        int charSize;
        int nx = x-2-this.scroll;
        for (int i = 0; i < this.text.length(); i++) {
            charSize = fontRenderer.getCharSize(this.type==PCGresInputType.PASSWORD?'*':this.text.charAt(i)).x;
            if (nx - charSize / 2 < 0) {
                return i;
            }
            nx -= charSize;
        }
        return this.text.length();
    }

    @Override
    protected boolean handleKeyTyped(char key, int keyCode, boolean repeat, PCGresHistory history) {
        super.handleKeyTyped(key, keyCode, repeat, history);
        this.cursorCounter = 0;
        if (this.type == PCGresInputType.NONE)
            return true;
        switch (key) {
            case 3:
                GuiScreen.setClipboardString(getSelect());
                break;
            case 22:
                setSelected(GuiScreen.getClipboardString());
                break;
            case 24:
                GuiScreen.setClipboardString(getSelect());
                deleteSelected();
                break;
            default:
                switch (keyCode) {
                    case Keyboard.KEY_RETURN:
                        return true;
                    case Keyboard.KEY_BACK:
                        key_backspace();
                        return true;
                    case Keyboard.KEY_HOME:
                        this.mouseSelectEnd = this.mouseSelectStart = 0;
                        return true;
                    case Keyboard.KEY_END:
                        this.mouseSelectEnd = this.mouseSelectStart = this.text.length();
                        return true;
                    case Keyboard.KEY_DELETE:
                        key_delete();
                        return true;
                    case Keyboard.KEY_LEFT:
                        if (this.mouseSelectEnd > 0) {
                            this.mouseSelectEnd -= 1;
                            if (!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard
                                    .isKeyDown(Keyboard.KEY_LSHIFT))) {
                                this.mouseSelectStart = this.mouseSelectEnd;
                            }

                        }
                        return true;
                    case Keyboard.KEY_RIGHT:
                        if (this.mouseSelectEnd < this.text.length()) {
                            this.mouseSelectEnd += 1;
                            if (!(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard
                                    .isKeyDown(Keyboard.KEY_LSHIFT))) {
                                this.mouseSelectStart = this.mouseSelectEnd;
                            }
                        }
                        return true;
                    default:
                        switch (this.type) {
                            case UNSIGNED_INT:
                                if (Character.isDigit(key)) {
                                    addKey(key);
                                    return true;
                                }
                                return false;

                            case INT:
                                // writing before minus
                                if (this.text.length() > 0 && this.text.charAt(0) == '-'
                                        && this.mouseSelectStart == 0 && this.mouseSelectEnd == 0) {
                                    return true;
                                }

                                if (Character.isDigit(key)) {
                                    addKey(key);
                                    return true;
                                } else if ((this.mouseSelectStart == 0 || this.mouseSelectEnd == 0)
                                        && key == '-') {
                                    addKey(key);
                                    return true;
                                }
                                return false;

                            case SIGNED_FLOAT:

                                if (key == '.') {
                                    if (this.mouseSelectStart == 0 || this.mouseSelectEnd == 0) {
                                        return true;
                                    }
                                    if (this.text.length() > 0
                                            && (this.mouseSelectStart == 1 || this.mouseSelectEnd == 1)
                                            && this.text.charAt(0) == '-') {
                                        return true;
                                    }
                                    if (this.text.length() > 0 && this.text.contains(".")) {
                                        return true;
                                    }
                                    addKey(key);
                                    return true;
                                }

                                if (this.text.length() > 0 && this.text.charAt(0) == '-'
                                        && this.mouseSelectStart == 0 && this.mouseSelectEnd == 0) {
                                    return true;
                                }

                                if (Character.isDigit(key)) {
                                    addKey(key);
                                    return true;
                                } else if ((this.mouseSelectStart == 0 || this.mouseSelectEnd == 0)
                                        && key == '-') {
                                    addKey(key);
                                    return true;
                                }

                                return false;

                            case UNSIGNED_FLOAT:

                                if (key == '.') {
                                    if (this.mouseSelectStart == 0 || this.mouseSelectEnd == 0) {
                                        return true;
                                    }
                                    if (this.text.length() > 0 && this.text.contains(".")) {
                                        return true;
                                    }
                                    addKey(key);
                                    return true;
                                }

                                if (Character.isDigit(key)) {
                                    addKey(key);
                                    return true;
                                }

                                return false;

                            case IDENTIFIER:

                                if (Character.isDigit(key)) {
                                    if (this.mouseSelectStart == 0 || this.mouseSelectEnd == 0) {
                                        return true;
                                    }
                                    addKey(key);
                                    return true;
                                }

                                if (Character.isLetter(key) || key == '_') {
                                    addKey(key);
                                    return true;
                                }

                                return false;

                            case TEXT:
                            default:
                                if (ChatAllowedCharacters.isAllowedCharacter(key)) {
                                    addKey(key);
                                    return true;
                                }
                                return false;
                        }
                }
        }
        return true;
    }

    /**
     * Add a character instead of current selection (or in place of, if start ==
     * end)
     *
     * @param c
     *            character
     */
    protected void addKey(char c) {
        int s = this.mouseSelectStart, e = this.mouseSelectEnd;
        if (s > e) {
            e = this.mouseSelectStart;
            s = this.mouseSelectEnd;
        }
        try {
            String s1 = this.text.substring(0, s);
            String s2 = this.text.substring(e);
            if ((s1 + c + s2).length() > this.maxChars) {
                return;
            }
            this.text = s1 + c + s2;
            this.mouseSelectEnd =  s + 1;
            this.mouseSelectStart = this.mouseSelectEnd;
        } catch (StringIndexOutOfBoundsException ss) {
            ss.printStackTrace();
        }
    }

    private void deleteSelected() {
        int s = this.mouseSelectStart, e = this.mouseSelectEnd;
        if (s > e) {
            e = this.mouseSelectStart;
            s = this.mouseSelectEnd;
        }
        String s1 = this.text.substring(0, s);
        String s2 = this.text.substring(e);
        this.text = s1 + s2;
        this.mouseSelectEnd = s;
        this.mouseSelectStart = s;
    }

    private void key_backspace() {
        if (this.mouseSelectStart != this.mouseSelectEnd) {
            deleteSelected();
            return;
        }
        if (this.mouseSelectEnd <= 0) {
            return;
        }
        String s1 = this.text.substring(0, this.mouseSelectEnd - 1);
        String s2 = this.text.substring(this.mouseSelectEnd);
        this.text = s1 + s2;
        this.mouseSelectEnd -= 1;
        this.mouseSelectStart = this.mouseSelectEnd;
    }

    private void key_delete() {
        if (this.mouseSelectStart != this.mouseSelectEnd) {
            deleteSelected();
            return;
        }
        if (this.mouseSelectEnd >= this.text.length()) {
            return;
        }
        String s1 = this.text.substring(0, this.mouseSelectEnd);
        String s2 = this.text.substring(this.mouseSelectEnd + 1);
        this.text = s1 + s2;
    }

    private String getSelect() {
        int s = this.mouseSelectStart, e = this.mouseSelectEnd;
        if (s > e) {
            e = this.mouseSelectStart;
            s = this.mouseSelectEnd;
        }
        return this.text.substring(s, e);
    }

    /**
     * Replace selected part of the text
     *
     * @param stri
     *            replacement
     */
    private void setSelected(String stri) {
        int s = this.mouseSelectStart, e = this.mouseSelectEnd;
        if (s > e) {
            e = this.mouseSelectStart;
            s = this.mouseSelectEnd;
        }
        String s1 = this.text.substring(0, s);
        String s2 = this.text.substring(e);
        String ss = "";
        switch (this.type) {
            case UNSIGNED_INT:
                for (int i = 0; i < stri.length(); i++) {
                    if (Character.isDigit(stri.charAt(i))) {
                        ss += stri.charAt(i);
                    }
                }
                break;

            case INT:
                if (this.text.length() > 0) {
                    if (this.text.charAt(0) == '-') {
                        if (this.mouseSelectStart == 0 && this.mouseSelectEnd == 0) {
                            break;
                        }
                    }
                }
                for (int i = 0; i < stri.length(); i++) {
                    if (i == 0) {
                        if (stri.charAt(0) == '-') {
                            if (s == 0) {
                                ss += stri.charAt(i);
                            }
                        }
                    }
                    if (Character.isDigit(stri.charAt(i))) {
                        ss += stri.charAt(i);
                    }
                }
                break;

            case SIGNED_FLOAT:
                if (this.text.length() > 0) {
                    if (this.text.charAt(0) == '-') {
                        if (this.mouseSelectStart == 0 && this.mouseSelectEnd == 0) {
                            break;
                        }
                    }
                }
                for (int i = 0; i < stri.length(); i++) {
                    if (i == 0) {
                        if (stri.charAt(0) == '-') {
                            if (s == 0) {
                                ss += stri.charAt(i);
                            }
                        }
                    }
                    if (stri.charAt(i) == '.') {
                        if (!(s1.contains(".") || s2.contains(".") || ss
                                .contains("."))) {
                            ss += ".";
                        }
                    }
                    if (Character.isDigit(stri.charAt(i))) {
                        ss += stri.charAt(i);
                    }
                }
                break;

            case UNSIGNED_FLOAT:
                for (int i = 0; i < stri.length(); i++) {
                    if (stri.charAt(i) == '.') {
                        if (!(s1.contains(".") || s2.contains(".") || ss
                                .contains("."))) {
                            ss += ".";
                        }
                    }
                    if (Character.isDigit(stri.charAt(i))) {
                        ss += stri.charAt(i);
                    }
                }
                break;

            case NONE:
                break;

            default:
                for (int i = 0; i < stri.length(); i++) {
                    if (ChatAllowedCharacters.isAllowedCharacter(stri.charAt(i))) {
                        ss += stri.charAt(i);
                    }
                }
                break;
        }
        if ((s1 + ss + s2).length() > this.maxChars) {
            return;
        }
        this.text = s1 + ss + s2;
        this.mouseSelectEnd = s + ss.length();
        this.mouseSelectStart = s;
    }

    @Override
    protected boolean handleMouseMove(PCVec2I mouse, int buttons, PCGresHistory history) {
        super.handleMouseMove(mouse, buttons, history);
        if (this.mouseDown) {
            this.mouseSelectEnd = getMousePositionInString(mouse.x);
            this.cursorCounter = 0;
        }
        return true;
    }

    @Override
    protected boolean handleMouseButtonDown(PCVec2I mouse, int buttons,
                                            int eventButton, boolean doubleClick, PCGresHistory history) {
        super.handleMouseButtonDown(mouse, buttons, eventButton, doubleClick, history);
        this.mouseSelectStart = getMousePositionInString(mouse.x);
        this.mouseSelectEnd = this.mouseSelectStart;
        this.cursorCounter = 0;
        return true;
    }

    @Override
    protected void onTick() {
        if (this.focus) {
            this.cursorCounter++;
        } else {
            this.cursorCounter = 0;
        }
    }
}

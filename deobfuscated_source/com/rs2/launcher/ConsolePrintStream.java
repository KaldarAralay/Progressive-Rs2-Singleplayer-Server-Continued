/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.launcher;

import java.io.PrintStream;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public final class ConsolePrintStream
extends PrintStream {
    private JTextArea outputTextArea;

    public ConsolePrintStream(JTextArea jTextArea, JScrollPane jScrollPane) {
        super(System.out);
        this.outputTextArea = jTextArea;
    }

    @Override
    public final void println(Object object) {
        this.appendLine(object);
    }

    private void appendLine(Object object) {
        this.outputTextArea.append(object.toString() + "\n");
    }

    @Override
    public final void println(String string) {
        this.appendLine(string);
    }

    @Override
    public final void println() {
        this.println("println\n");
    }

    @Override
    public final void print(Object object) {
        this.appendText(object);
    }

    private void appendText(Object object) {
        this.outputTextArea.append(object.toString());
    }

    @Override
    public final void print(String string) {
        this.appendText(string);
    }
}


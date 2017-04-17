/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import java.util.LinkedList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author David
 */
public class Keyboard extends Stage {

    private static Stage keyboard;
    private static String text;

    private TextField field;

    private boolean caps;
    private boolean shift;
    
    private final int width = 1500;
    private final int height = 800;

    LinkedList<Button> list;

    public Keyboard(Window parent, String title) {
        super();
        list = new LinkedList<>();
        init();
        setTitle(title);
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
    }

    public static String show(Window parent, String title) {
        keyboard = new Keyboard(parent, title);
        text = "";
        keyboard.showAndWait();
        return text;
    }

    private void init() {
        GridPane pane = new GridPane();

        field = new TextField();
        field.setMaxHeight(50);
        field.setMinHeight(50);
        field.setMaxHeight(400);
        field.setFont(Font.font("Tahoma", FontWeight.NORMAL, 24));
        field.setOnAction((ActionEvent event) -> {
            onEnter();
        });

        Button one = new Button("1");
        one.setId("keyboard");
        one.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        one.setOnAction(new MyActionEvent());
        
        Button two = new Button("2");
        two.setId("keyboard");
        two.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        two.setOnAction(new MyActionEvent());
        
        Button three = new Button("3");
        three.setId("keyboard");
        three.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        three.setOnAction(new MyActionEvent());
        
        Button four = new Button("4");
        four.setId("keyboard");
        four.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        four.setOnAction(new MyActionEvent());
        
        Button five = new Button("5");
        five.setId("keyboard");
        five.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        five.setOnAction(new MyActionEvent());
        
        Button six = new Button("6");
        six.setId("keyboard");
        six.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        six.setOnAction(new MyActionEvent());
        
        Button seven = new Button("7");
        seven.setId("keyboard");
        seven.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        seven.setOnAction(new MyActionEvent());
        
        Button eight = new Button("8");
        eight.setId("keyboard");
        eight.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        eight.setOnAction(new MyActionEvent());
        
        Button nine = new Button("9");
        nine.setId("keyboard");
        nine.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        nine.setOnAction(new MyActionEvent());
        
        Button zero = new Button("0");
        zero.setId("keyboard");
        zero.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        zero.setOnAction(new MyActionEvent());

        Button q = new Button("q");
        q.setId("keyboard");
        q.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        q.setOnAction(new MyActionEvent());
        list.add(q);
        
        Button w = new Button("w");
        w.setId("keyboard");
        w.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        w.setOnAction(new MyActionEvent());
        list.add(w);
        
        Button e = new Button("e");
        e.setId("keyboard");
        e.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        e.setOnAction(new MyActionEvent());
        list.add(e);
        
        Button r = new Button("r");
        r.setId("keyboard");
        r.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        r.setOnAction(new MyActionEvent());
        list.add(r);
        
        Button t = new Button("t");
        t.setId("keyboard");
        t.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        t.setOnAction(new MyActionEvent());
        list.add(t);
        
        Button y = new Button("y");
        y.setId("keyboard");
        y.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        y.setOnAction(new MyActionEvent());
        list.add(y);
        
        Button u = new Button("u");
        u.setId("keyboard");
        u.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        u.setOnAction(new MyActionEvent());
        list.add(u);
        
        Button i = new Button("i");
        i.setId("keyboard");
        i.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        i.setOnAction(new MyActionEvent());
        list.add(i);
        
        Button o = new Button("o");
        o.setId("keyboard");
        o.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        o.setOnAction(new MyActionEvent());
        list.add(o);
        
        Button p = new Button("p");
        p.setId("keyboard");
        p.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        p.setOnAction(new MyActionEvent());
        list.add(p);
        Button a = new Button("a");
        a.setId("keyboard");
        a.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        a.setOnAction(new MyActionEvent());
        list.add(a);
        
        Button s = new Button("s");
        s.setId("keyboard");
        s.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        s.setOnAction(new MyActionEvent());
        list.add(s);
        
        Button d = new Button("d");
        d.setId("keyboard");
        d.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        d.setOnAction(new MyActionEvent());
        list.add(d);
        
        Button f = new Button("f");
        f.setId("keyboard");
        f.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        f.setOnAction(new MyActionEvent());
        list.add(f);
        
        Button g = new Button("g");
        g.setId("keyboard");
        g.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        g.setOnAction(new MyActionEvent());
        list.add(g);
        
        Button h = new Button("h");
        h.setId("keyboard");
        h.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        h.setOnAction(new MyActionEvent());
        list.add(h);
        
        Button j = new Button("j");
        j.setId("keyboard");
        j.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        j.setOnAction(new MyActionEvent());
        list.add(j);
        
        Button k = new Button("k");
        k.setId("keyboard");
        k.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        k.setOnAction(new MyActionEvent());
        list.add(k);
        
        Button l = new Button("l");
        l.setId("keyboard");
        l.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        l.setOnAction(new MyActionEvent());
        list.add(l);
        
        Button z = new Button("z");
        z.setId("keyboard");
        z.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        z.setOnAction(new MyActionEvent());
        list.add(z);
        
        Button x = new Button("x");
        x.setId("keyboard");
        x.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        x.setOnAction(new MyActionEvent());
        list.add(x);
        
        Button c = new Button("c");
        c.setId("keyboard");
        c.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        c.setOnAction(new MyActionEvent());
        list.add(c);
        
        Button v = new Button("v");
        v.setId("keyboard");
        v.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        v.setOnAction(new MyActionEvent());
        list.add(v);
        
        Button b = new Button("b");
        b.setId("keyboard");
        b.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        b.setOnAction(new MyActionEvent());
        list.add(b);
        
        Button n = new Button("n");
        n.setId("keyboard");
        n.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        n.setOnAction(new MyActionEvent());
        list.add(n);
        
        Button m = new Button("m");
        m.setId("keyboard");
        m.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        m.setOnAction(new MyActionEvent());
        list.add(m);

        Button comma = new Button(",");
        comma.setId("keyboard");
        comma.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        comma.setOnAction(new MyActionEvent());
        
        Button dot = new Button(".");
        dot.setId("keyboard");
        dot.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        dot.setOnAction(new MyActionEvent());

        Button space = new Button("Space");
        space.setId("keyboard");
        space.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        space.setOnAction((ActionEvent event) -> {
            field.setText(field.getText() + " ");
        });

        Button capsButton = new Button("Caps");
        capsButton.setId("keyboard");
        capsButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        capsButton.setOnAction((ActionEvent event) -> {
            toggleCaps();
        });

        Button shiftButton = new Button("Shift");
        shiftButton.setId("keyboard");
        shiftButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        shiftButton.setOnAction((ActionEvent event) -> {
            onShift();
        });

        Button enter = new Button("Enter");
        enter.setId("keyboard");
        enter.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        enter.setOnAction((ActionEvent event) -> {
            onEnter();
        });

        Button back = new Button("Back");
        back.setId("keyboard");
        back.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        back.setOnAction((ActionEvent event) -> {
            if(field.getText().length() > 0);
            field.setText(field.getText().substring(0, field.getText().length() - 1));
        });

        Button clear = new Button("Clear");
        clear.setId("keyboard");
        clear.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        clear.setOnAction((ActionEvent event) -> {
            field.setText("");
        });

        pane.add(field, 0, 0, 12, 1);
        pane.add(one, 1, 1);
        pane.add(two, 2, 1);
        pane.add(three, 3, 1);
        pane.add(four, 4, 1);
        pane.add(five, 5, 1);
        pane.add(six, 6, 1);
        pane.add(seven, 7, 1);
        pane.add(eight, 8, 1);
        pane.add(nine, 9, 1);
        pane.add(zero, 10, 1);

        pane.add(q, 1, 2);
        pane.add(w, 2, 2);
        pane.add(e, 3, 2);
        pane.add(r, 4, 2);
        pane.add(t, 5, 2);
        pane.add(y, 6, 2);
        pane.add(u, 7, 2);
        pane.add(i, 8, 2);
        pane.add(o, 9, 2);
        pane.add(p, 10, 2);
        pane.add(a, 1, 3);
        pane.add(s, 2, 3);
        pane.add(d, 3, 3);
        pane.add(f, 4, 3);
        pane.add(g, 5, 3);
        pane.add(h, 6, 3);
        pane.add(j, 7, 3);
        pane.add(k, 8, 3);
        pane.add(l, 9, 3);
        pane.add(z, 1, 4);
        pane.add(x, 2, 4);
        pane.add(c, 3, 4);
        pane.add(v, 4, 4);
        pane.add(b, 5, 4);
        pane.add(n, 6, 4);
        pane.add(m, 7, 4);
        pane.add(comma, 8, 4);
        pane.add(dot, 9, 4);
        pane.add(space, 3, 5, 5, 1);

        pane.add(capsButton, 0, 3);
        pane.add(shiftButton, 0, 4);
        pane.add(back, 11, 1, 1, 2);
        pane.add(clear, 11, 3, 1, 2);
        pane.add(enter, 10, 3, 1, 2);

        for (int ci = 1; ci <= 12; ci++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth((100 / 12));
            col.setFillWidth(true);
            col.setHgrow(Priority.ALWAYS);
            pane.getColumnConstraints().add(col);
        }

        for (int ri = 1; ri <= 6; ri++) {
            RowConstraints row = new RowConstraints();
            row.setPrefHeight(height / 6);
            row.setFillHeight(true);
            row.setVgrow(Priority.ALWAYS);
            pane.getRowConstraints().add(row);
        }

        Scene scene = new Scene(pane, width, height);
        String stylesheet = MainStage.class.getResource("style.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);
        setScene(scene);
    }

    private class MyActionEvent implements EventHandler {

        @Override
        public void handle(Event event) {
            if (shift) {
                field.setText(field.getText() + ((Button) event.getSource()).getText().toUpperCase());
            } else {
                field.setText(field.getText() + ((Button) event.getSource()).getText());
            }
            shift = false;
        }
    }

    private void onShift() {
        shift = !shift;
    }

    private void toggleCaps() {
        caps = !caps;
        list.forEach((b) -> {
            if (caps) {
                b.setText(b.getText().toUpperCase());
            } else {
                b.setText(b.getText().toLowerCase());
            }
        });
    }

    private void onEnter() {
        text = field.getText();
        hide();
    }
}

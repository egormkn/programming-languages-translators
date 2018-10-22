package ru.ifmo.translators;

import java.util.Arrays;
import java.util.List;

public class Tree {

    private final String name;
    private final List<Tree> children;

    public Tree(String name, Tree... children) {
        this.name = name;
        this.children = Arrays.asList(children);
    }

    public String getName() {
        return name;
    }

    public List<Tree> getChildren() {
        return children;
    }

    public void print() {
        print(0);
    }

    private void print(int level) {
        StringBuilder builder = new StringBuilder(level * 4);
        for (int i = 0; i < (level - 1) * 4; i++) {
            builder.append(i % 4 == 0 ? '│' : ' ');
        }

        String padding = builder.toString();
        System.out.print(padding);
        if (level > 0) System.out.print("└───");
        System.out.println(name);
        for (Tree t : children) {
            t.print(level + 1);
        }
    }
}
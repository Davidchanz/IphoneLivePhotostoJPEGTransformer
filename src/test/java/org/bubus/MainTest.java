package org.bubus;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    @Test
    void Test(){
        Set<Object> beans = new HashSet<>();
        Transformer t1 = new Transformer();
        Transformer t2 = new Transformer();
        beans.add(t1);
        beans.add(t2);
        beans.add(t1);
        System.out.println("sdf");
    }
}
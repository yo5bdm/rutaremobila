/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author yo5bd
 */
public class HashDB {
    private Nod cap;
    private int sz;
    //metodele vizibile
    public void adauga(int h,Double fit) {
        if(cap==null){
            cap = new Nod(h,fit);
        }
        add(cap,h, fit);
    }
    public boolean cauta(int h) {
        if(search(cap,h)!=null) return true;
        return false;
    }
    public int size() {
        sz=0;
        calcSize(cap);
        return sz;
    }
    public Nod iaNod(int h) {
        return search(cap,h);
    }
    //metodele de ajutor
    private void add(Nod c, int h, Double fit) {
        if(c==null) return;
        if(c.hash == h) return;
        if(c.hash<h) {
            if(c.st == null) {
                c.st = new Nod(h, fit);
            } else {
                add(c.st,h, fit);
            }
        } else {
            if(c.dr == null) {
                c.dr = new Nod(h, fit);
            } else {
                add(c.dr,h, fit);
            }
        }
    }
    private Nod search(Nod c, int h) {
        if(c==null) return null;
        if(c.hash==h) return c;
        Nod ret = search(c.st,h);
        if(ret==null) ret = search(c.dr,h);
        return ret;
    }
    private void calcSize(Nod c) {
        if(c==null) return;
        sz++;
        calcSize(c.dr);
        calcSize(c.st);
    }
}

class Nod {
    int hash;
    Double fitness;
    Nod st, dr;

    public Nod(int hash, Double fitness) {
        this.fitness = fitness;
        this.hash = hash;
        st=null;
        dr=null;
    }
}
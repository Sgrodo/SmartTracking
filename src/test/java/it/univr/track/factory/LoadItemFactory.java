package it.univr.track.factory;

import it.univr.track.entity.data.LoadItem;

import java.util.List;

public class LoadItemFactory {

    public static LoadItem item1(){
        return new LoadItem("Item1", "QR001", "First Load Item");
    }

    public static LoadItem item2(){
        return new LoadItem("Item2", "QR002", "Second Load Item");
    }

    public static List<LoadItem> loadItems(){
        return List.of(item1(),item2());
    }
}

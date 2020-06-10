package com.example.mediaplayer.Data;

import java.util.List;

public class PlayList {
    String Name;
    List<Container> Containers;

    PlayList(String Name)
    {
        this.Name=Name;
    }
    public void AddContainer(Container Container )
    {
        Containers.add(Container);
    }
    void EditName(String Name)
    {
        this.Name=Name;
    }
    void RemoveContainer(Container Container)
    {
        Containers.remove(Container);
    }

    void getContainer(int Containerindex)
    {

    }
}

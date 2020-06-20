package com.example.mediaplayer.Data;

import java.util.List;

public class PlayList {
    String Name;
    List<Container> Containers;

    public PlayList(String Name,List<Container> containers)
    {
        this.Name=Name;
        Containers = containers;
    }
    public void AddContainer(Container Container )
    {
        Containers.add(Container);
    }
    public void EditName(String Name)
    {
        this.Name=Name;
    }
    public void RemoveContainer(Container Container)
    {
        Containers.remove(Container);
    }

    public Container getContainer(int Containerindex)
    {
       return Containers.get(Containerindex);
    }
}

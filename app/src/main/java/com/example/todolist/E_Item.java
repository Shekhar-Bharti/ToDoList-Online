package com.example.todolist;

public class E_Item {
    private String title;
    private String description;
    private String date;

    public E_Item(String title,String description,String date){
        this.date=date;
        this.description=description;
        this.title=title;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public String getDate(){
        return date;
    }
}

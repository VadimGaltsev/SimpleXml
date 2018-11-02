package home.parser;

import java.util.ArrayList;

@XmlPath("commands/command")
public class test {
//    @XmlPath("commandName")
    private String data1;

    @XmlPath("members/member")
    @XmlPath.List(testInner.class)
    private ArrayList<testInner> data2;
    @XmlPath("commanda/commanda1")
    private String data3;
}

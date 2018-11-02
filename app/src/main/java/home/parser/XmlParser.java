package home.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public interface XmlParser<T> {
    ArrayList<T> parse(String s, Class t) throws XmlPullParserException, IOException;
    ArrayList<T> parse(XmlPullParser parsing, Class t, String closed) throws XmlPullParserException, IOException;
}
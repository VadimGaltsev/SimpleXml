package home.parser;

import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class ParserAdapter<T extends Object> implements XmlParser<T> {

    private XmlPullParser xmlPullParser;
    private HashMap<String, Field> keyProperty = new HashMap<>();
    private ArrayList<T> result = new ArrayList<>();
    private String path;
    private Class target;
    private String closed;
    {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            xmlPullParser = factory.newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    public static <R> XmlParser<R> get() {
        return new ParserAdapter<R>() {};
    }

    @Override
    public ArrayList<T> parse(String s, Class c) throws XmlPullParserException, IOException {
        getAttr(c);
        xmlPullParser.setInput(new StringReader(s));
        path = getPath(c);
        target = c;
        return mapping();
    }

    @Override
    public ArrayList<T> parse(XmlPullParser parsing, Class t, String closed) throws XmlPullParserException, IOException {
        getAttr(t);
        xmlPullParser = parsing;
        path = getPath(t);
        target = t;
        this.closed = closed;
        return mapping();
    }

    private ArrayList<T> mapping() throws XmlPullParserException, IOException {
        StringBuilder builder_ = new StringBuilder();
        StringBuilder property = new StringBuilder();
        String name = "";
        String nameProperty = "";
        Object o = null;
        if (path == null) return null;
        while (xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT) {
            if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                nameProperty = xmlPullParser.getName();
                System.out.println(xmlPullParser.getName());
                for (String region : path.split("/")) {
                    if (name.equals(region)) {
                        builder_.append(name).append("/");
                    }
                }
                for (String key : keyProperty.keySet()) {
                    String[] arrs = key.split("/");
                    if (arrs.length > 1) {
                        for (String path : arrs) {
                            if (path.equals(nameProperty)) {
                                property.append(nameProperty).append("/");
                            }
                        }
                    }
                }
                name = xmlPullParser.getName();

                if (builder_.toString().equals(path) ||
                        builder_.toString().split("/")[ builder_.toString().split("/").length - 1].equals(name)) {
                    System.out.println("object created " + target.getName());
                    o = get(target);
                    result.add((T) o);
                    builder_.delete(0, builder_.length());
                    property.delete(0, property.length());
                }
                Class clazz;
                if (keyProperty.containsKey(property.toString()))
                if ((clazz = isItChild(keyProperty.get(property.toString()))) != null && o != null) {
                    setFieldClass(o, property.toString(), get().parse(xmlPullParser, clazz, property.toString()));
                    builder_.delete(0, builder_.length());
                    property.delete(0, property.length());
                }
            }
            if (xmlPullParser.getEventType() == XmlPullParser.TEXT) {
                if (!xmlPullParser.getText().matches("\\W+")) {
                    if (o != null) {
                        setField(o, name + "/", xmlPullParser.getText());
                    }
                    if (o != null && keyProperty.containsKey(property.toString())) {
                            setField(o, property.toString(), xmlPullParser.getText());
                            property.delete(0, property.length());
                    }
                }
            }
            if (xmlPullParser.getEventType() == XmlPullParser.END_TAG) {
                if (xmlPullParser.getName().equals(path.split("/")[0])) {
                    builder_.delete(0, builder_.length());
                    property.delete(0, property.length());
                }
                if (closed != null && !TextUtils.isEmpty(closed)) {
                   if (closed.split("/")[0].equals(xmlPullParser.getName())) break;
                }

            }
            xmlPullParser.next();
        }
        return result;
    }

    protected Object get(Class c) {
        Object o = null;
        try {
            o = c.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }



    protected void setField(Object o, String tag, String val) {
        if (keyProperty.containsKey(tag)) {
            keyProperty.get(tag).setAccessible(true);
            try {
                keyProperty.get(tag).set(o, val);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    protected void setFieldClass(Object o, String tag, ArrayList result) {
        if (keyProperty.containsKey(tag)) {
            Field field = keyProperty.get(tag);
            field.setAccessible(true);
            if (field.getType() == ArrayList.class) {
                try {
                    keyProperty.get(tag).set(o, result);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected String getPath(Class c) {
        XmlPath annotation = null;
        if ((annotation = (XmlPath) c.getAnnotation(XmlPath.class)) == null) {
            return null;
        }
        if (annotation.value().endsWith("/")) return annotation.value();
        return annotation.value() + "/";
    }
    @Deprecated
    protected String[] getFieldPath(Field field) {
        XmlPath annotation = null;
        field.setAccessible(true);
        if ((annotation = (XmlPath) field.getAnnotation(XmlPath.class)) == null) {
            return null;
        }
        return annotation.value().split("/");
    }

    protected void getAttr(Class c) {
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            XmlPath xmlPath;
            field.setAccessible(true);
            if ((xmlPath = field.getAnnotation(XmlPath.class)) == null) {
                continue;
            }
            if (!xmlPath.value().endsWith("/")) {
                keyProperty.put(xmlPath.value() + "/", field);
            } else keyProperty.put(xmlPath.value(), field);
        }
    }

    private Class isItChild(Field c) {
        if (c.isAnnotationPresent(XmlPath.List.class)) {
            XmlPath.List list = c.getAnnotation(XmlPath.List.class);
            return list.value();
        }
        return null;
    }

}





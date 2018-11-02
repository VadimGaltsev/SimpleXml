package home.parser;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(home.parser.R.layout.activity_main);
        try {

             ArrayList<test> parser_ = ParserAdapter.<test>get().parse("<commands>\n" +
                     "    <command>\n" +
                     "        <commandName>Fifa</commandName>\n" +
                     "        <commanda><commanda1>Fifa1</commanda1>\\n\"</commanda>\n" +

                     "        <members>\n" +
                     "           <member>\n" +
                     "              <lastName>Alice</lastName>\n" +
                     "           </member>\n" +
                     "           <member>\n" +
                     "              <lastName>Bob</lastName>\n" +
                     "           </member>\n" +
                     "           <member>\n" +
                     "               <lastName>Clar</lastName>\n" +
                     "           </member>\n" +
                     "        </members>\n" +
                     "    </command>\n" +
                     "    <command>\n" +
                     "        <commandName>Lala</commandName>\n" +
                     "        <members>\n" +
                     "           <member>\n" +
                     "              <lastName>David</lastName>\n" +
                     "           </member>\n" +
                     "           <member>\n" +
                     "              <lastName>Eva</lastName>\n" +
                     "           </member>\n" +
                     "        </members>\n" +
                     "    </command>\n" +
                     "</commands>", test.class);
            Field[] fields = test.class.getDeclaredFields();
            for (Field field : fields) {
                XmlPath path = field.getAnnotation(XmlPath.class);
                System.out.println(field.getType());
                if (field.getType() == ArrayList.class) {
                    System.out.println(((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0]);
                }
            }
            System.out.println(parser_);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

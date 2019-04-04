package com.example.lightstudy;

import android.content.ContentValues;
import android.database.Cursor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import javax.xml.transform.Source;

/**
 * Author:mihon
 * Time: 2019\4\4 0004.9:24
 * Description:This is ReflectObject
 * <p>
 * 支持boolean  内部自动化为int
 */
public class ReflectObject {
    public int id;

    private Field[] getAllField() {
        Field[] fields = getClass().getDeclaredFields();
        Field[] fields2 = new Field[fields.length + 1];
        try {
            fields2[0] = ReflectObject.class.getDeclaredField("id");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < fields.length; i++) {
            fields2[i + 1] = fields[i];
        }

        return fields2;
    }

    public static ReflectObject newInstance(Class<? extends ReflectObject> clazz) {
        try {
            Constructor<?> constructor = clazz.getConstructor();
            return (ReflectObject) constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void testFildsName() {
        Field[] fields = getAllField();
        for (int i = 0; i < fields.length; i++) {
            System.out.println(fields[i].getName());
//            System.out.println(fields[i].getType().getName());
            try {
                Object o = fields[i].get(this);
                if (fields[i].getType().getName().equals("int")) {
                    System.out.println("Integer");
                } else if (fields[i].getType().getName().endsWith(".String")) {
                    System.out.println("String");
                } else if (fields[i].getType().getName().equals("float")) {
                    System.out.println("Float");
                } else if (fields[i].getType().getName().equals("long")) {
                    System.out.println("Long");
                } else if (fields[i].getType().getName().equals("boolean")) {
                    System.out.println("Boolean");
                } else if (fields[i].getType().getName().equals("double")) {
                    System.out.println("Double");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void fullEntry(Cursor c) {
        Field[] fields = getAllField();
        for (int i = 0; i < fields.length; i++) {

            if (!Modifier.isStatic(fields[i].getModifiers())) {
                fields[i].setAccessible(true);
                try {
                    if (fields[i].getType().getName().equals("int")) {
                        int id = c.getInt(c.getColumnIndex(fields[i].getName()));
                        fields[i].set(this, id);
                    } else if (fields[i].getType().getName().endsWith(".String")) {
                        String str = c.getString(c.getColumnIndex(fields[i].getName()));
                        fields[i].set(this, str);
                    } else if (fields[i].getType().getName().equals("boolean")) {
                        int b = c.getInt(c.getColumnIndex(fields[i].getName()));
                        fields[i].set(this, b == 1);
                    } else if (fields[i].getType().getName().equals("float")) {
                        float f = c.getFloat(c.getColumnIndex(fields[i].getName()));
                        fields[i].set(this, f);
                    } else if (fields[i].getType().getName().equals("long")) {
                        long l = c.getLong(c.getColumnIndex(fields[i].getName()));
                        fields[i].set(this, l);
                    } else if (fields[i].getType().getName().equals("double")) {
                        double d = c.getDouble(c.getColumnIndex(fields[i].getName()));
                        fields[i].set(this, d);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        System.out.println(cv);
        Field[] fields = getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            //TODO 这样的话只能限定一个entry固定哪些字段是不要update的
            if (fields[i].getDeclaredAnnotations() != null && fields[i].getDeclaredAnnotations().length > 0) {
                continue;
            }
            if (!Modifier.isStatic(fields[i].getModifiers())) {
                fields[i].setAccessible(true);
                try {
                    Object o = fields[i].get(this);
                    if (fields[i].getType().getName().equals("int")) {
                        cv.put(fields[i].getName(), (Integer) o);
                    } else if (fields[i].getType().getName().endsWith(".String")) {
                        cv.put(fields[i].getName(), (String) o);
                    } else if (fields[i].getType().getName().equals("boolean")) {
                        cv.put(fields[i].getName(), ((Boolean) o) ? 1 : 0);
                    } else if (fields[i].getType().getName().equals("float")) {
                        cv.put(fields[i].getName(), (Float) o);
                    } else if (fields[i].getType().getName().equals("long")) {
                        cv.put(fields[i].getName(), (Long) o);
                    } else if (fields[i].getType().getName().equals("boolean")) {
                        cv.put(fields[i].getName(), (Boolean) o);
                    } else if (fields[i].getType().getName().equals("double")) {
                        cv.put(fields[i].getName(), (Double) o);
                    } else {
                        cv.put(fields[i].getName(), (String) o);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return cv;
    }

    public String makeSQLTable() {
        String table = getClass().getSimpleName().toLowerCase();
        String s = "create table " + table + "("
                + "id integer primary key autoincrement ";
//        String s = "create table " + table + "("
//                + "id integer primary key autoincrement "
//                + ",startTime varchar(20) "
//                + ",endTime varchar(20) "
//                + ",planDuration integer "
//                + ",result integer "
//                + ",mark varchar(20))";
        Field[] fields = getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (Modifier.isStatic(fields[i].getModifiers())) {
                continue;
            }
            fields[i].setAccessible(true);
            if (fields[i].getType().getName().equals("int")) {
                s += "," + fields[i].getName() + " integer";
            } else if (fields[i].getType().getName().endsWith(".String")) {
//                ReflectTable reflectTable = fields[i].getAnnotation(ReflectTable.class);
//                int size = 20;
//                if(reflectTable!=null){
//                    size = reflectTable.strLength();
//                }
//                s+=","+fields[i].getName()+" varchar("+size+")";
                s += "," + fields[i].getName() + " text";
            } else if (fields[i].getType().getName().equals("float")) {
                s += "," + fields[i].getName() + " real";
            } else if (fields[i].getType().getName().equals("long")) {
            } else if (fields[i].getType().getName().equals("boolean")) {
                s += "," + fields[i].getName() + " integer";
            } else if (fields[i].getType().getName().equals("double")) {
                s += "," + fields[i].getName() + " real";
            } else {
                s += "," + fields[i].getName() + " text";
            }

        }
        s += ");";
        return s;
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ReflectIgnore {
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ReflectTable {
        int strLength() default 30;
    }

    @Override
    public String toString() {
        String table = getClass().getSimpleName();
        String s = table + "{";
        try {
            Field[] fields = getAllField();
            for (int i = 0; i < fields.length; i++) {
                if (Modifier.isStatic(fields[i].getModifiers())) {
                    continue;
                }
                System.out.println(fields[i].getName());
                System.out.println(fields[i].get(this));
                fields[i].setAccessible(true);
                s += (fields[i].getName() + "=" + fields[i].get(this)+",");

            }
            s = s.substring(0,s.length() -1) +"}";

        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
}

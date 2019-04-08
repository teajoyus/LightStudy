package com.example.lightstudy;

/**
 * Author:mihon
 * Time: 2019\4\4 0004.9:07
 * Description:This is TimeStudy
 */
public class TimeStudy extends ReflectObject{
    public String startTime;
    public String endTime;
    public int planDuration;
    public int result;
    public int status;//状态：1、开始学习；2、学习完成；3、学习提前退出；
    public String mark;
    public String lightStyleName;//收集到的灯泡样式
    public int lightLayout;//收集到的灯泡摆放位置 1：top 2：bottom
    public TimeStudy(int id, String startTime, String endTime, int planDuration, int result, String mark,String lightStyleName,int lightLayout) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.planDuration = planDuration;
        this.result = result;
        this.mark = mark;
        this.lightStyleName = lightStyleName;
        this.lightLayout = lightLayout;
    }
    public TimeStudy() {
    }

//    @Override
//    public String toString() {
//        return "TimeStudy{" +
//                "startTime='" + startTime + '\'' +
//                ", endTime='" + endTime + '\'' +
//                ", mark='" + mark + '\'' +
//                ", planDuration=" + planDuration +
//                ", result=" + result +
//                ", id=" + id +
//                '}';
//    }
    public static void main(String[] args){
        TimeStudy object = new TimeStudy();
        object.endTime = "123";
//        object.testFildsName();
        System.out.println(object.makeSQLTable());
        System.out.println(object);

    }


}

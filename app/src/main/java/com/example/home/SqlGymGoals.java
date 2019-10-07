package com.example.home;

public class SqlGymGoals {
    //Таблица бд для класса     GymGoals

    //Поля: textGoal, statusGoal
    public static final String insertGoals(String textGoal, int statusGoal)
    {
        return "INSERT INTO GymGoals(textGoal,statusGoal)\n" +
                "VALUES\n" +
                "(\""+textGoal+"\",\""+statusGoal+"\");";
    }

    public static final String selectGoals()
    {
        return "SELECT * FROM GymGoals;";
    }

    public static final String selectNotCheckedGoals()
    {
        return "SELECT * FROM GymGoals \n" +
                "WHERE "+TableGymGoals.COLUMN_STATUS_GOALS+" = 0;";
    }
}

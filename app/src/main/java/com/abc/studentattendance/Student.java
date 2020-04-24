package com.abc.studentattendance;

public class Student {
    public String fullName,Email,mobileNumber,rollNumber,gender,branch,year,section,imguri,isAddInSecList;
    public Student(){

    }
    public Student(String fn,String email,String mn,String rn,String gen,String brnch,String yr,String sec,String imgur,String iaisc)
    {
        fullName = fn;
        Email = email;
        mobileNumber = mn;
        rollNumber = rn;
        gender = gen;
        branch = brnch;
        year = yr;
        section = sec;
        imguri = imgur;
        isAddInSecList = iaisc;
    }
}

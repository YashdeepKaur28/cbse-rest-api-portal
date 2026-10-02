package cbse;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Student {
    private int studentId;
    private String name;
    private String className;
    private double mathMarks, scienceMarks, englishMarks, hindiMarks, sstMarks;
    private double totalMarks, percentage;
    private String grade;

    public Student() {}

    public int getStudentId() { return studentId; }
    public void setStudentId(int v) { this.studentId = v; }
    public String getName() { return name; }
    public void setName(String v) { this.name = v; }
    public String getClassName() { return className; }
    public void setClassName(String v) { this.className = v; }
    public double getMathMarks() { return mathMarks; }
    public void setMathMarks(double v) { this.mathMarks = v; }
    public double getScienceMarks() { return scienceMarks; }
    public void setScienceMarks(double v) { this.scienceMarks = v; }
    public double getEnglishMarks() { return englishMarks; }
    public void setEnglishMarks(double v) { this.englishMarks = v; }
    public double getHindiMarks() { return hindiMarks; }
    public void setHindiMarks(double v) { this.hindiMarks = v; }
    public double getSstMarks() { return sstMarks; }
    public void setSstMarks(double v) { this.sstMarks = v; }
    public double getTotalMarks() { return totalMarks; }
    public void setTotalMarks(double v) { this.totalMarks = v; }
    public double getPercentage() { return percentage; }
    public void setPercentage(double v) { this.percentage = v; }
    public String getGrade() { return grade; }
    public void setGrade(String v) { this.grade = v; }
}